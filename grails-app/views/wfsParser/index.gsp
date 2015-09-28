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
//        var wfsServer = "http://localhost:8080/geoserver/wfs";
        var wfsServer = "${createLink( absolute: true, controller: 'wfs' )}";
//        var wfsServer = 'http://demo.boundlessgeo.com/geoserver/wfs';
//	    var wfsServer = 'http://clc.developpement-durable.gouv.fr/geoserver/wfs';

        var wfsClient = new OGC.WFSClient(wfsServer);

        var featureTypeName = 'topp:states';
        wfsClient.getFeatureTypeNames();
        wfsClient.getFeatureTypeSchema(featureTypeName);
        wfsClient.getFeature(featureTypeName, "STATE_ABBR='IN'");

        var getFeatureURL = "http://localhost:8080/geoserver/wfs";
//        var getFeatureURL = "http://clc.developpement-durable.gouv.fr/geoserver/wfs";

    } );
</asset:script>
<asset:deferredScripts/>
</body>
</html>