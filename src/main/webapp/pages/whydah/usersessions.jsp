<%@ taglib serviceName="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>Whydah usersessions</title>

</head>
<body>
<h2>Whydah Usersessions for  ${model.username}</h2>
<h4>${message}</h4>
<div id="chart2" style="height:500px; width:800px;"></div>

<script src="//code.jquery.com/jquery.js"></script>
<!--<script src="${pageContext.request.contextPath}/js/jquery-1.8.2.min.js"></script>  -->
<script src="//code.highcharts.com/highcharts.js"></script>
<script src="//code.highcharts.com/modules/exporting.js"></script>
<script type="text/javascript">
    // Expected output
    //{"serviceName":"All","activityName":"userSession","startTime":1457332781744,"endTime":1457419181744,"activities":{"userSessions":[{"serviceName":"","name":"userSession","startTime":1457419114782,"data":{"usersessionfunction":null,"applicationid":"app1","userid":"me","applicationtokenid":"token1"}},{"serviceName":"","name":"userSession","startTime":1457419114782,"data":{"usersessionfunction":null,"applicationid":"app2","userid":"me","applicationtokenid":"token2"}}]}}
    //-->
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });
    var chart;
    $.getJSON('/reporter/observe/statistics/${model.serviceName}/usersession?startTime=${model.from}&endTime=${model.to}', function(data) {
        <%--var methodNames = '${model.methodName}'.split(".");--%>
//        var niceNum=methodNames.length-2;
        var graphTitle='User Session Activities';//methodNames[niceNum] + '.' + methodNames[niceNum+1];

        var series=[
            {
                name: 'User Session Count',
                data: []
            },
            {
                name: 'Created',
                data: []
            },
            {
                name: 'Access',
                data: []
            },
            {
                name: 'Verification',
                data: []
            },
            {
                name: 'Removed',
                data: []
            }
        ];
        var usersessions = data.activities.userSessions;
        var userSessionFunction = "";
        var noSession = 0;
        usersessions.forEach(function(usersession) {
//        data.forEach(function(interval){
            //console.log("startTime: " + interval.startTime +", mean : " + interval.mean + ", max: " + interval.max + ", p95: " + interval.p95);
            noSession=noSession+1;
            userSessionFunction = usersession.data.usersessionfunction;
            series[0].data.push([usersession.startTime, noSession % 55 ]);
            if (userSessionFunction == "userSessionCreated") {
                series[1].data.push([usersession.startTime, parseInt(usersession.data.applicationid) % 55]);
            }
            if (userSessionFunction == "userSessionAccess") {
                series[2].data.push([usersession.startTime, parseInt(usersession.data.applicationid) % 55]);
            }
            if (userSessionFunction == "userSessionVerification") {
                series[3].data.push([usersession.startTime, parseInt(usersession.data.applicationid) % 55]);
            }
            if (userSessionFunction == "userSessionRemoved") {
                series[4].data.push([usersession.startTime, parseInt(usersession.data.applicationid) % 55]);
            }


        });

//        message ="hi";

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
                    text: 'Application Session Distribution',
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
            /*
            tooltip: {
                shared: true,
                crosshairs: true,
                xDateFormat: '<b>%m-%d %H:%M</b>'
            },
            */
            tooltip: {
                shared: true,
                crosshairs: true,
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
            series: series
        });

    });



</script>
</body>
</html>