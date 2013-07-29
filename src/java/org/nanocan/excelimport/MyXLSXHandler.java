package org.nanocan.excelimport;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.PrintStream;

/**
 * The type of the data value is indicated by an attribute on the cell.
 * The value is usually in a "v" element within the cell.
 */
enum xssfDataType {
    BOOL,
    ERROR,
    FORMULA,
    INLINESTR,
    SSTINDEX,
    NUMBER,
}

/**
 * Derived from http://poi.apache.org/spreadsheet/how-to.html#xssf_sax_api
 * <p/>
 * Also see Standard ECMA-376, 1st edition, part 4, pages 1928ff, at
 * http://www.ecma-international.org/publications/standards/Ecma-376.htm
 * <p/>
 * A web-friendly version is http://openiso.org/Ecma/376/Part4
 */
public class MyXLSXHandler extends DefaultHandler{

    /**
     * Table with styles
     */
    private StylesTable stylesTable;

    /**
     * Table with unique strings
     */
    private ReadOnlySharedStringsTable sharedStringsTable;

    /**
     * Destination for data
     */
    private final PrintStream output;

    /**
     * Number of columns to read starting with leftmost
     */
    private final int minColumnCount;

    // Set when V start element is seen
    private boolean vIsOpen;

    // Set when cell start element is seen;
    // used when cell close element is seen.
    private xssfDataType nextDataType;

    // Used to format numeric cell values.
    private short formatIndex;
    private String formatString;
    private final DataFormatter formatter;

    private int thisColumn = -1;
    // The last column printed to the output stream
    private int lastColumnNumber = -1;

    // Gathers characters as they are seen.
    private StringBuffer value;

    /**
     * Accepts objects needed while parsing.
     *
     * @param styles  Table of styles
     * @param strings Table of shared strings
     * @param cols    Minimum number of columns to show
     * @param target  Sink for output
     */
    public MyXLSXHandler(
            StylesTable styles,
            ReadOnlySharedStringsTable strings,
            int cols,
            PrintStream target) {
        this.stylesTable = styles;
        this.sharedStringsTable = strings;
        this.minColumnCount = cols;
        this.output = target;
        this.value = new StringBuffer();
        this.nextDataType = xssfDataType.NUMBER;
        this.formatter = new DataFormatter();
    }

    /*
    * (non-Javadoc)
    * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
    */
    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {

        if ("inlineStr".equals(name) || "v".equals(name)) {
            vIsOpen = true;
            // Clear contents cache
            value.setLength(0);
        }
        // c => cell
        else if ("c".equals(name)) {
            // Get the cell reference
            String r = attributes.getValue("r");
            int firstDigit = -1;
            for (int c = 0; c < r.length(); ++c) {
                if (Character.isDigit(r.charAt(c))) {
                    firstDigit = c;
                    break;
                }
            }
            thisColumn = nameToColumn(r.substring(0, firstDigit));

            // Set up defaults.
            this.nextDataType = xssfDataType.NUMBER;
            this.formatIndex = -1;
            this.formatString = null;
            String cellType = attributes.getValue("t");
            String cellStyleStr = attributes.getValue("s");
            if ("b".equals(cellType))
                nextDataType = xssfDataType.BOOL;
            else if ("e".equals(cellType))
                nextDataType = xssfDataType.ERROR;
            else if ("inlineStr".equals(cellType))
                nextDataType = xssfDataType.INLINESTR;
            else if ("s".equals(cellType))
                nextDataType = xssfDataType.SSTINDEX;
            else if ("str".equals(cellType))
                nextDataType = xssfDataType.FORMULA;
            else if (cellStyleStr != null) {
                // It's a number, but almost certainly one
                //  with a special style or format
                int styleIndex = Integer.parseInt(cellStyleStr);
                XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
                this.formatIndex = style.getDataFormat();
                this.formatString = style.getDataFormatString();
                if (this.formatString == null)
                    this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
            }
        }

    }

    /*
    * (non-Javadoc)
    * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
    */
    public void endElement(String uri, String localName, String name)
            throws SAXException {

        String thisStr = null;

        // v => contents of a cell
        if ("v".equals(name)) {
            // Process the value contents as required.
            // Do now, as characters() may be called more than once
            switch (nextDataType) {

                case BOOL:
                    char first = value.charAt(0);
                    thisStr = first == '0' ? "FALSE" : "TRUE";
                    break;

                case ERROR:
                    thisStr = "\"ERROR:" + value.toString() + '"';
                    break;

                case FORMULA:
                    // A formula could result in a string value,
                    // so always add double-quote characters.
                    thisStr = '"' + value.toString() + '"';
                    break;

                case INLINESTR:
                    // TODO: have seen an example of this, so it's untested.
                    XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
                    thisStr = '"' + rtsi.toString() + '"';
                    break;

                case SSTINDEX:
                    String sstIndex = value.toString();
                    try {
                        int idx = Integer.parseInt(sstIndex);
                        XSSFRichTextString rtss = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx));
                        thisStr = '"' + rtss.toString() + '"';
                    }
                    catch (NumberFormatException ex) {
                        output.println("Failed to parse SST index '" + sstIndex + "': " + ex.toString());
                    }
                    break;

                case NUMBER:
                    String n = value.toString();
                    if (this.formatString != null)
                        thisStr = formatter.formatRawCellContents(Double.parseDouble(n), this.formatIndex, this.formatString);
                    else
                        thisStr = n;
                    break;

                default:
                    thisStr = "(TODO: Unexpected type: " + nextDataType + ")";
                    break;
            }

            // Output after we've seen the string contents
            // Emit commas for any fields that were missing on this row
            if (lastColumnNumber == -1) {
                lastColumnNumber = 0;
            }
            for (int i = lastColumnNumber; i < thisColumn; ++i)
                output.print(',');

            // Might be the empty string.
            output.print(thisStr);

            // Update column
            if (thisColumn > -1)
                lastColumnNumber = thisColumn;

        } else if ("row".equals(name)) {

            // Print out any missing commas if needed
            if (minColumnCount > 0) {
                // Columns are 0 based
                if (lastColumnNumber == -1) {
                    lastColumnNumber = 0;
                }
                for (int i = lastColumnNumber; i < (this.minColumnCount); i++) {
                    output.print(',');
                }
            }

            // We're onto a new row
            output.println();
            lastColumnNumber = -1;
        }

    }

    /**
     * Captures characters only if a suitable element is open.
     * Originally was just "v"; extended for inlineStr also.
     */
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (vIsOpen)
            value.append(ch, start, length);
    }

    /**
     * Converts an Excel column name like "C" to a zero-based index.
     *
     * @param name
     * @return Index corresponding to the specified name
     */
    private int nameToColumn(String name) {
        int column = -1;
        for (int i = 0; i < name.length(); ++i) {
            int c = name.charAt(i);
            column = (column + 1) * 26 + c - 'A';
        }
        return column;
    }

}