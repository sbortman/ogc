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
<asset:javascript src="jquery.js"/>
<asset:javascript src="OpenLayersLite-formats.js"/>
<asset:script>
    $(document).ready(function(){
        var formatter = new OpenLayers.Format.WFSCapabilities();
//        var endpoint = "http://localhost:8080/geoserver/wfs";
        var endpoint = "http://localhost:9999/ogc/wfs";
//        var endpoint = 'http://demo.boundlessgeo.com/geoserver/wfs';
//	    var endpoint = 'http://clc.developpement-durable.gouv.fr/geoserver/wfs';

        var layers = [];

/*
        $.ajax({
            url: endpoint + '?service=WFS&version=1.1.0&request=GetCapabilities',
            success: function(data) {
                // use the tool to parse the data
                var response = (formatter.read(data));

                //console.log('response', response);

                // this object contains all the GetCapabilities data
                //var capability = response.capability;

                // I want a list of names to use in my queries
                for( var i = 0; i < response.featureTypeList.featureTypes.length; i++ ) {
                    var featureType = response.featureTypeList.featureTypes[i];

                    //console.log('featureType',  featureType);

                    layers.push(featureType.name);
                }

                console.log(layers);

            }
        });
*/
/*
        var formatter2 =  new OpenLayers.Format.WFSDescribeFeatureType();
        $.ajax( {
            url: endpoint + '?service=WFS&version=1.1.0&request=DescribeFeatureType&typeName=topp:states',
            dataType: "html",
            success: function(data) {
                var response = (formatter2.read(data));

                console.log('response', response);
            },
            error: function(error) {
                console.log('error', error);
           }
        } );
*/

        var getFeatureURL = "http://localhost:8080/geoserver/wfs";
//        var getFeatureURL = "http://clc.developpement-durable.gouv.fr/geoserver/wfs";

        getFeatureURL += "?service=WFS&version=1.1.0&request=GetFeature&typeName=topp:states&outputFormat=JSON&cql_filter=STATE_ABBR='IN'";
        getFeatureURL = "/ogc/wfsParser/proxyWFS?url=" + btoa(getFeatureURL);

        console.log('getFeatureURL', getFeatureURL);

        $.ajax({
            url:  getFeatureURL,
            success: function(data) {
                var results = $.parseJSON(data);

                console.log(results);
            },
            error: function(error) {
                console.log(error);
            }
        });


    } );
</asset:script>
<asset:deferredScripts/>
</body>
</html>