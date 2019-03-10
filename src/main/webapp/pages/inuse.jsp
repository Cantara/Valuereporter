<html>
<head>
    <title>Usage %</title>

</head>
<body>
<h2>Usage graf for ${model.prefix}</h2>

<div id="usage" style="height:300px; width:500px;"></div>

<script src="http://code.jquery.com/jquery.js"></script>
<!--<script src="${pageContext.request.contextPath}/js/jquery-1.8.2.min.js"></script>  -->
<script src="http://code.highcharts.com/highcharts.js"></script>
<script src="http://code.highcharts.com/modules/exporting.js"></script>
<script type="text/javascript">
    var chart;
    var filterOnName = '${model.filterOnName}';
    var chartDataUrl = "/reporter/observe/valuemethods/${model.prefix}/chart/${model.filterOnName}";
    if (!filterOnName) {
        chartDataUrl = "/reporter/observe/valuemethods/${model.prefix}/chart";
    }
    alert("Chart " + chartDataUrl);
    $.getJSON(chartDataUrl, function(data) {
        var mySeries = [];

        console.log(data);
        var line1=[];
        data.forEach(function(entryvalue){
            // $.each(data, function (entrykey, entryvalue) {
            console.info("name:" + entryvalue.name);
            console.log("usageCount:" + entryvalue.usageCount);
            line1.push([entryvalue.name, entryvalue.usageCount]);
        });
        console.log(line1);
        mySeries.push(
                {name: 'Usage distribution for ${model.methodName}',
                   type: 'pie',
                    data: line1});

        $('#usage').highcharts({
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            tooltip: {
                pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                        }
                    }
                }
            },
            data: {
                json: line1
            },

            credits: {
                enabled: false
            },
            title: {
                text: 'Service in-use'
            },
            series: mySeries
        });

    });



</script>
</body>
</html>