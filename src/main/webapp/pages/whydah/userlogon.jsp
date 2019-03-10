<html>
<head>
    <title>Whydah User Logons</title>

</head>
<body>
<h2>Whydah User Logons for  ${model.username}</h2>
<h4>${message}</h4>
<div id="chart2" style="height:500px; width:800px;"></div>

<script src="//code.jquery.com/jquery.js"></script>
<!--<script src="${pageContext.request.contextPath}/js/jquery-1.8.2.min.js"></script>  -->
<script src="//code.highcharts.com/highcharts.js"></script>
<script src="//code.highcharts.com/modules/exporting.js"></script>
<script type="text/javascript">
    //Expected output
    //{"prefix":"All","activityName":"userlogon","startTime":1457332861892,"endTime":1457419261892,"activities":{"userlogons":[1457419114782,1457419114782]}}
    //Current output
    //[{"prefix":"initial","methodName":"com.valuereporter.test","duration":900000,"startTime":1457348563296,"count":4,"max":50,"min":2,"mean":5.0,"median":0.0,"stdDev":0.0,"p95":0.0,"p98":0.0,"p99":0.0}]
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });
    var chart;
    $.getJSON('/reporter/observe/statistics/${model.prefix}/userlogon?startTime=${model.from}&endTime=${model.to}', function(data) {
        //var methodNames = '${model.methodName}'.split(".");
       // var niceNum=methodNames.length-2;
        var graphTitle='User Logons';//methodNames[niceNum] + '.' + methodNames[niceNum+1];

        var series=[
            {
                name: 'User Logon',
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
        var userlogons = data.activities.userlogons;
        userlogons.forEach(function(userlogon){
            //console.log("startTime: " + interval.startTime +", mean : " + interval.mean + ", max: " + interval.max + ", p95: " + interval.p95);
            series[0].data.push([userlogon, 1]);
//            series[0].data.push([interval.startTime,interval.count]);
//            series[1].data.push([interval.startTime,interval.mean]);
//            series[2].data.push([interval.startTime,interval.p95]);
//            series[3].data.push([interval.startTime,interval.max]);
        });

        $('#chart2').highcharts({
            chart: {
                type: 'scatter',
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