<%--
  Created by IntelliJ IDEA.
  User: bardl
  Date: 15.05.2014
  Time: 07:41
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Statistics and Value Reporting</title>
</head>
<body>
    <h3>Implemented methods</h3>
    <li><a href="./gui/implemented?serviceNameOnly">List of serviceName's</a> </li>
    <li><a href="./gui/implemented?serviceName=dummy-load">List of methods within a serviceName.</a> </li>


    <h3>Observations</h3>
    <li><a href="./gui/observations?serviceName=dummy-load">List usage-count pr method.</a> </li>
    <li><a href="./gui/slainterval?serviceName=initial&methodName=com.valuereporter.test">Graph of Count/time for a method and serviceName.</a> </li>

    <h3>InUse - which methods are actually being used</h3>
    <li><a href="./gui/inuse?serviceName=dummy-load">Show methods implemented vs. in use.</a> </li>


    <h3>Rest interface</h3>
    <li><a href="./observe/implementedserviceName">List implemented serviceNamees</a> </li>
    <li><a href="./observe/implementedmethods/dummy-load">List implemented methods for serviceName</a> </li>"

    ________
    <h3>Old stuff...</h3>
    <li><a href="./observe/observedmethods/{serviceName}/{name}">template listing of operations (./observe/observedmethods/{serviceName}/{name})</a></li>
    <b>Ping and Hello test</b>
    <li><a href="./observe/observedmethods/initial/com.valuereporter.test">Initial Data</a> </li>
    <li><a href="./observe/observedmethods/template-serviceName/org.valuereporter.Welcome.ping">Ping</a>
    <li><a href="./observe/observedmethods/template-serviceName/org.valuereporter.Welcome.hello">Helo</a>

    <h3>SLA</h3>
   <li>SLA reporting: <a href="./gui/sla?serviceName=template-serviceName&methodName=org.valuereporter.Welcome.ping">SLA reporting on Welcome.ping</a></li>
   <li>SLA Summary: <a href="./gui/slainterval?serviceName=template-serviceName&methodName=org.valuereporter.Welcome.ping">SLA reporting on Welcome.ping</a></li>
   <li>SLA rest: <a href="./observe/sla/observations/template-serviceName?filter=org.valuereporter.Welcome.hello">http://localhost:4901/reporter/observe/sla/observations/{serviceName}?filter={methodName}</a></li>

   <h3>Load Generator</h3>
   <li>SLA Interval/Summary: <a href="./gui/slainterval?serviceName=dummy-load&methodName=org.dummy.load.LoadThread.performVisibleLoad">Graph of Count/time</a> </li>

</body>
</html>
