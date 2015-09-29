/**
 * Created by sbortman on 9/28/15.
 */
//= require jquery.js
//= require OpenLayersLite-formats.js
//= require_self

var OGC = OGC || {WFS: {}};

OGC.WFS.Client = OpenLayers.Class( {
    initialize: function ( wfsServer )
    {
        this.wfsServer = wfsServer;
        this.wfsFeatureTypes = this.getFeatureTypes();
        this.wfsFeatureTypeSchemas = null; // = this.getFeatureTypeSchema();

        //console.log(this.wfsServer);
    },
    getFeatureTypes: function ( cb )
    {
        var localFeatureTypes = [];
        var isAsync = (cb instanceof Function);

        if ( this.wfsFeatureTypes === undefined )
        {

            var formatter = new OpenLayers.Format.WFSCapabilities();
            var that = this;

            var params = {
                service: 'WFS',
                version: '1.1.0',
                request: 'GetCapabilities'
            };

            $.ajax( {
                url: this.wfsServer,
                data: params,
                async: isAsync, // TODO: Refactor with better solution.  This is not recommended:
                // http://stackoverflow.com/a/14220323/4437795
                success: function ( data )
                {
                    // use the tool to parse the data
                    var response = (formatter.read( data ));

                    //console.log( 'formatter', formatter );
                    //console.log( 'namespaces', formatter.namespaces );
                    //console.log( 'namespaceAlias', formatter.namespaceAlias );

                    //console.log('response', response);

                    // this object contains all the GetCapabilities data
                    //var capability = response.capability;

                    // I want a list of names to use in my queries
                    for ( var i = 0; i < response.featureTypeList.featureTypes.length; i++ )
                    {
                        var featureType = response.featureTypeList.featureTypes[i];

                        //                    console.log( 'featureType', featureType );

                        localFeatureTypes.push( featureType );

                        //console.log(formatter.namespaceAlias);

                        //console.log('fullName', formatter.namespaceAlias[featureType.featureNS] + ':' + featureType.name)
                    }

                    //console.log( featureTypeNames );
                    //return featureTypeNames;

                }
            } );
            that.featureTypes = localFeatureTypes;
            //console.log( 'fetching...' );

        }
        else
        {

            //console.log( 'cached...' );

        }

        if ( isAsync )
        {
            //console.log( 'We are ASync!' );
            cb( this.featureTypes );
        }
        //console.log( isAsync );

        return this.featureTypes;
    },
    getFeatureTypeSchemas: function ()
    {
        var that = this;

        return this.getFeatureTypes().map( function ( it )
        {
            console.log( it );
            return that.getFeatureTypeSchema( it.name );
        } );
    },
    getFeatureTypeSchema: function ( featureTypeName, namespace, callback )
    {
        var formatter2 = new OpenLayers.Format.WFSDescribeFeatureType();

        var params = {
            service: 'WFS',
            version: '1.1.0',
            request: 'DescribeFeatureType',
            typeName: 'ns1:' + featureTypeName,
            namespace: 'xmlns(ns1=' + namespace + ')'
        };

        var isAsync = (callback instanceof Function);
        var results;

        $.ajax( {
            url: this.wfsServer,
            data: params,
            dataType: "html",
            async: isAsync,
            success: function ( data )
            {
                var response = (formatter2.read( data ));

                //console.log( 'response', response );
                results = response;
            },
            error: function ( error )
            {
                console.log( 'error', error );
            }
        } );

        return results;
    },
    getFeature: function ( featureTypeName, filter )
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
    CLASS_NAME: "OGC.WFS.Client"
} );

var WFSClient = (function ()
{
    function init(params)
    {

//        var wfsServer = "http://localhost/geoserver/wfs";
//        var wfsServer = 'http://demo.boundlessgeo.com/geoserver/wfs';
//	    var wfsServer = 'http://clc.developpement-durable.gouv.fr/geoserver/wfs';

        var wfsClient = new OGC.WFS.Client( params.wfsServer );

        console.log( wfsClient.getFeatureTypes() );

        console.log( wfsClient.getFeatureTypeSchema('states', 'http://www.openplans.org/topp'));

        //
        //wfsClient.getFeatureTypes( function ( w )
        //{
        //    console.log( '2', w.name );
        //} );
        //
        //console.log( '3', wfsClient.getFeatureTypes().map( function ( it )
        //{
        //    return it.name;
        //} ) );
        //
        //var featureTypeName = 'topp:states';
        //wfsClient.getFeatureTypeSchema( featureTypeName );
        //wfsClient.getFeature( featureTypeName, "STATE_ABBR='IN'" );
        //
        //var getFeatureURL = "http://localhost:8080/geoserver/wfs";
//        var getFeatureURL = "http://clc.developpement-durable.gouv.fr/geoserver/wfs";
    }

    return {
        init: init
    }
})();