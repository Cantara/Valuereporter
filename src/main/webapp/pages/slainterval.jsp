<html>
<head>
    <title>SLA using HighChart</title>

</head>
<body>
<h2>SLA graph for ${model.prefix}</h2>
<h4>${message}</h4>
<div id="chart2" style="height:500px; width:800px;"></div>

<script src="http://code.jquery.com/jquery.js"></script>
<!--<script src="${pageContext.request.contextPath}/js/jquery-1.8.2.min.js"></script>  -->
<script src="http://code.highcharts.com/highcharts.js"></script>
<script src="http://code.highcharts.com/modules/exporting.js"></script>
<script type="text/javascript">
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });
    var chart;
    $.getJSON('/reporter/observe/sla/interval/${model.prefix}?filter=${model.methodName}&from=${model.from}&to=${model.to}', function(data) {
        var methodNames = '${model.methodName}'.split(".");
        var niceNum=methodNames.length-2;
        var graphTitle=methodNames[niceNum] + '.' + methodNames[niceNum+1];

        var series=[
            {
                name: 'Count',
                data: []
            },
            {
                name: 'Mean',
                data: []
            },
            {
                name: '95 percentile',
                data: [],
                visible: false
            }, {
                name: 'Max',
                data: [],
                visible: false
        }];
        data.forEach(function(interval){
            //console.log("startTime: " + interval.startTime +", mean : " + interval.mean + ", max: " + interval.max + ", p95: " + interval.p95);
            series[0].data.push([interval.startTime,interval.count]);
            series[1].data.push([interval.startTime,interval.mean]);
            series[2].data.push([interval.startTime,interval.p95]);
            series[3].data.push([interval.startTime,interval.max]);
        });

        $('#chart2').highcharts({
            chart: {
                type: 'spline',
                zoomType: 'x'
            },
            credits: {
                enabled: false
            },
            title: {
                text: graphTitle
            },
            xAxis: {
                type: 'datetime',
                dateTimeLabelFormats: {
                    month: '%e. %b',
                    year: '%b'
                }
            },
            yAxis: [{ // Primary yAxis
                labels: {
                    format: '{value} ',
                    style: {
                        color: Highcharts.getOptions().colors[0]
                    }
                },
                title: {
                    text: 'Count pr 15 minutes',
                    style: {
                        color: Highcharts.getOptions().colors[0]
                    }
                },
                min: 0
            }, { // Secondary yAxis
                title: {
                    text: 'Responsetime (ms)',
                    style: {
                        color: Highcharts.getOptions().colors[1]
                    }
                },
                labels: {
                    format: '{value} ',
                    style: {
                        color: Highcharts.getOptions().colors[1]
                    }
                },
                opposite: true
            }],
            tooltip: {
                shared: true,
                crosshairs: true,
                xDateFormat: '<b>%m-%d %H:%M</b>'
            },
            /*
            tooltip: {
                dateTimeLabelFormats: { // don't display the dummy year
                    second:" %d/%m %H:%M:%S",
                    minute:" %d/%m %H:%M",
                    hour:" %d/%m %H:%M",
                    day:"%A, %b %e, %Y",
                    month: '%e. %b',
                    year: '%b'
                },
                followPointer: true
            },
            */
            series: series
        });

    });



</script>
</body>
</html>