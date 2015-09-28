/**
 * Created by sbortman on 9/28/15.
 */
//= require jquery.js
//= require OpenLayersLite-formats.js
//= require_self

var OGC = OGC || {};

OGC.WFSClient = OpenLayers.Class( {
    initialize: function ( wfsServer )
    {
        this.wfsServer = wfsServer;

        //console.log(this.wfsServer);
    },
    getFeatureTypeNames: function ()
    {
        var formatter = new OpenLayers.Format.WFSCapabilities();
        var featureTypeNames = [];

        $.ajax( {
            url: this.wfsServer + '?service=WFS&version=1.1.0&request=GetCapabilities',
            success: function ( data )
            {
                // use the tool to parse the data
                var response = (formatter.read( data ));

                //console.log('response', response);

                // this object contains all the GetCapabilities data
                //var capability = response.capability;

                // I want a list of names to use in my queries
                for ( var i = 0; i < response.featureTypeList.featureTypes.length; i++ )
                {
                    var featureType = response.featureTypeList.featureTypes[i];

//                    console.log( 'featureType', featureType );

                    featureTypeNames.push( featureType.name );
                }

                console.log( featureTypeNames );
            }
        } );
    },
    getFeatureTypeSchema: function ( featureTypeName )
    {
        var formatter2 = new OpenLayers.Format.WFSDescribeFeatureType();
        $.ajax( {
            url: this.wfsServer + '?service=WFS&version=1.1.0&request=DescribeFeatureType&typeName=' + featureTypeName,
            dataType: "html",
            success: function ( data )
            {
                var response = (formatter2.read( data ));

//                console.log( 'response', response );
            },
            error: function ( error )
            {
                console.log( 'error', error );
            }
        } );
    },
    getFeature: function (featureTypeName, filter)
    {
        var getFeatureURL = this.wfsServer + "?service=WFS&version=1.1.0&request=GetFeature&typeName=" + featureTypeName + "&outputFormat=GML&filter=" + filter;

        getFeatureURL = "/ogc/wfsParser/proxyWFS?url=" + btoa( getFeatureURL );

        console.log( 'getFeatureURL', getFeatureURL );

        $.ajax( {
            url: getFeatureURL,
            success: function ( data )
            {
                //var results = $.parseJSON( data );

                console.log( data );
            },
            error: function ( error )
            {
                console.log( error );
            }
        } );
    },
    CLASS_NAME: "OGC.WFSClient"
} );