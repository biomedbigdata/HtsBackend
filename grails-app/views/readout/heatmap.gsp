<!doctype HTML>
<meta charset = 'utf-8'>
<html>
<head>
    <r:require module="polychart2"/>
    <r:require module="jquery"/>
    <r:layoutResources />
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">

    <style>
    .rChart {
        display: block;
        margin-left: auto;
        margin-right: auto;
        width: 300px;
        height: 200px;
    }
    </style>

</head>
<body>
<div id='dynamicHeatmap' class='rChart polycharts'></div>
<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>


<r:script>
    var dataUrl = "${g.createLink(controller:"readoutHeatmap", action: "exportWellReadoutForHeatmapAsJSON", id:readoutId)}";
    var polyjsData = polyjs.data.url(dataUrl);

    var chartParams = {
        "dom": "dynamicHeatmap",
        "width": "300",
        "height": "200",
        "layers": [
                {
                    "x": {"var":"Column"},
                    "y": {"var":"Row"},
                    "data": polyjsData,
                    "color": "Value",
                    "type": "tile",
                    "height":    200
                }
                ],
                "guides": {
                    "x": {
                        "title": "Column",
                        "bw": 1
                    },
                    "y": {
                        "title": "Row",
                        "bw": 1
                    },
                    "color": {
                        "scale": {
                            "type": "gradient2",
                            "lower": "blue",
                            "middle": "yellow",
                            "upper": "red"
                        }
                    }
                },
                "coord": [],
                "id": "dynamicHeatmap"
            }

    polyjs.chart(chartParams);
</r:script>

<r:layoutResources />
</body>
</html>