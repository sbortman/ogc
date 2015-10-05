<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 9/25/15
  Time: 10:56 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
</head>

<body>
<asset:javascript src="WFSClient.js"/>
<asset:script>
    $(document).ready(function(){
        var params = {
          wfsServer: "${createLink( absolute: true, controller: 'wfs' )}",
//            wfsServer: "http://localhost/geoserver/wfs",
            wfsProxy: "${createLink( action: 'proxyWFS', params: [url: ''] )}"
        };

        WFSClient.init(params);
/*
        TestWFS.init(params);
*/
    } );
</asset:script>
<asset:deferredScripts/>
</body>
</html>