package org.nanocan.layout

import org.nanocan.plates.Plate

class LayoutSpot implements Comparable{

    CellLine cellLine
    LysisBuffer lysisBuffer
    Dilution dilutionFactor
    Inducer inducer
    SpotType spotType
    Sample sample
    Treatment treatment
    NumberOfCellsSeeded numberOfCellsSeeded
    WellLayout wellLayout
    int replicate
    Plate plate

    int block
    int col
    int row

    static belongsTo = SlideLayout
    SlideLayout layout

    static constraints = {
        plate nullable: true
        wellLayout nullable: true
        numberOfCellsSeeded nullable: true
        cellLine nullable:  true
        lysisBuffer nullable: true
        dilutionFactor nullable: true
        inducer nullable: true
        sample nullable:  true
        spotType nullable:  true
        treatment nullable:  true
        replicate nullable: true
    }

    static mapping = {
        layout index: 'lspot_idx'
        block index: 'lspot_idx'
        col index: 'lspot_idx'
        row index: 'lspot_idx'
    }

    //makes samples sortable in order block -> column -> row
    public int compareTo(def other) {
        //first compare in which 12er this spot is
        def blockTab = (int) ((block-1) / 12)
        def otherBlockTab = (int) ((other.block-1) /12)

        if(blockTab < otherBlockTab) return -1
        else if(blockTab > otherBlockTab) return 1
        else
        {
            if(row < other.row) return -1
            else if(row > other.row) return 1
            else{
                if(block < other.block) return -1
                else if(block > other.block) return 1
                else{
                    if(col < other.col) return -1
                    else if(col > other.col) return 1
                    else return 0
                }
            }
        }
    }

    String toString()
    {
        "B/C/R: ${block}/${col}/${row}"
    }
}
