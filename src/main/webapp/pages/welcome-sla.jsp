<html>
<head>
    <title>Some graphing examples</title>
    <script language="javascript" type="text/javascript" src="../js/jqplot/jquery.min.js"></script>
    <script language="javascript" type="text/javascript" src="../js/jqplot/jquery.jqplot.js"></script>
    <script type="text/javascript" src="../js/jqplot/jqplot.highlighter.js"></script>
    <script type="text/javascript" src="../js/jqplot/jqplot.cursor.js"></script>
    <script type="text/javascript" src="../js/jqplot/jqplot.dateAxisRenderer.js"></script>
    <link rel="stylesheet" type="text/css" href="../css/jqplot/jquery.jqplot.css" />
    <script type="text/javascript">
        $(document).ready(function(){
            var uptime_vs_in_use_url =  "/reporter/observe/observedmethods/template-serviceName/org.valuereporter.Welcome.ping";
            var line1=[];
            $.getJSON(uptime_vs_in_use_url,  function(data) {
                console.info("data:" + data[0].name);
                var uptimeData = [];
                var inUseData = [];
                data.forEach(function(entryvalue){
                    // $.each(data, function (entrykey, entryvalue) {
                    console.info("startTime:" + entryvalue.startTime);
                    console.log("duration:" + entryvalue.duration);
                    line1.push([entryvalue.startTime, entryvalue.duration]);
                });


                var plot2 = $.jqplot('chart2', [line1], {
                    title:'org.valuereporter.Welcome.ping',
                    gridPadding:{right:35},
                    axes:{
                        xaxis:{
                            renderer:$.jqplot.DateAxisRenderer,
                            tickOptions:{formatString:'%H:%M:%S'}
                            //min:'May 30, 2008',
                            //tickInterval:'1 month'
                        }
                    },
                    highlighter: {
                        show: true,
                        sizeAdjust: 7.5
                    },
                    cursor: {
                        show: false
                    },
                    series:[{lineWidth:4, markerOptions:{style:'square'}}]
                });
            });
        });
    </script>
</head>
<body>
<h2>Live graph</h2>
<h4>${message}</h4>
<div id="chart2" style="height:300px; width:500px;"></div>
</body>
</html>