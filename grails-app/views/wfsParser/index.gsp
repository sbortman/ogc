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
//        var wfsServer = "${createLink( absolute: true, controller: 'wfs' )}";
        var wfsServer = "http://localhost/geoserver/wfs";
        var wfsProxy = "${createLink( action: 'proxyWFS', params: [url: ''] )}";

/*
        WFSClient.init({
            wfsServer: wfsServer,
            wfsProxy: wfsProxy
        });
*/


        OpenLayers.ProxyHost = wfsProxy;

        var gml = new OpenLayers.Format.GML.v3();
        var cql = new OpenLayers.Format.CQL();
        var xml = new OpenLayers.Format.XML();
        var filter = xml.write( new OpenLayers.Format.Filter({version: "1.1.0"}).write(cql.read("STATE_ABBR='IN'")) );

        //console.log(filter);

        OpenLayers.Request.GET({
            url: wfsServer,
            params: {
                SERVICE: "WFS",
                VERSION: "1.1.0",
                REQUEST: "GetFeature",
                typeName: 'topp:states',
                filter: filter,
                outputFormat: 'gml3'
            },
            success: function(request) {
                var doc = request.responseText;
                if (!doc || !doc.documentElement) {
                    doc = request.responseXML;
                }

                //console.log(doc);
                var response = gml.read(doc);

                console.log('response', response);
            },
            failure: function() {
                alert("Trouble getting capabilities doc");
                OpenLayers.Console.error.apply(OpenLayers.Console, arguments);
            }
        });

    } );
</asset:script>
<asset:deferredScripts/>
</body>
</html>