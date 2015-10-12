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
            OpenLayers.Format.WFSCapabilities.v1.prototype.readers = {
                "wfs": {
                    "WFS_Capabilities": function ( node, obj )
                    {
                        this.readChildNodes( node, obj );
                    },
                    "FeatureTypeList": function ( node, request )
                    {
                        request.featureTypeList = {
                            featureTypes: []
                        };
                        this.readChildNodes( node, request.featureTypeList );
                    },
                    "FeatureType": function ( node, featureTypeList )
                    {
                        var featureType = {};
                        this.readChildNodes( node, featureType );
                        featureTypeList.featureTypes.push( featureType );
                    },
                    "Name": function ( node, obj )
                    {
                        var name = this.getChildValue( node );
                        if ( name )
                        {
                            var parts = name.split( ":" );
                            obj.name = name; //parts.pop();
                            if ( parts.length > 0 )
                            {
                                obj.featureNS = this.lookupNamespaceURI( node, parts[0] );
                            }
                        }
                    },
                    "Title": function ( node, obj )
                    {
                        var title = this.getChildValue( node );
                        if ( title )
                        {
                            obj.title = title;
                        }
                    },
                    "Abstract": function ( node, obj )
                    {
                        var abst = this.getChildValue( node );
                        if ( abst )
                        {
                            obj["abstract"] = abst;
                        }
                    }
                }
            };

            var formatter = new OpenLayers.Format.WFSCapabilities();

            var that = this;

            var params = {
                service: 'WFS',
                version: '1.1.0',
                request: 'GetCapabilities'
            };

            //$.ajax( {
            //    url: this.wfsServer,
            //    data: params,
            //    async: isAsync, // TODO: Refactor with better solution.  This is not recommended:
            //    // http://stackoverflow.com/a/14220323/4437795
            //    success: function ( data )
            //    {
            //        // use the tool to parse the data
            //        var response = (formatter.read( data ));
            //
            //        //console.log( 'formatter', formatter );
            //        //console.log( 'namespaces', formatter.namespaces );
            //        //console.log( 'namespaceAlias', formatter.namespaceAlias );
            //
            //        //console.log('response', response);
            //
            //        // this object contains all the GetCapabilities data
            //        //var capability = response.capability;
            //
            //        // I want a list of names to use in my queries
            //        for ( var i = 0; i < response.featureTypeList.featureTypes.length; i++ )
            //        {
            //            var featureType = response.featureTypeList.featureTypes[i];
            //
            //            //                    console.log( 'featureType', featureType );
            //
            //            localFeatureTypes.push( featureType );
            //
            //            //console.log(formatter.namespaceAlias);
            //
            //            //console.log('fullName', formatter.namespaceAlias[featureType.featureNS] + ':' + featureType.name)
            //        }
            //
            //        //console.log( featureTypeNames );
            //        //return featureTypeNames;
            //
            //    }
            //} );
            //that.featureTypes = localFeatureTypes;
            //console.log( 'fetching...' );

            OpenLayers.Request.GET( {
                url: this.wfsServer,
                async: isAsync,
                params: params,
                success: function ( request )
                {

                    var doc = request.responseXML;
                    if ( !doc || !doc.documentElement )
                    {
                        doc = request.responseText;
                    }

                    // use the tool to parse the data
                    var response = (formatter.read( doc ));
                    console.log('######## response ########', response);

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
    getFeatureTypeSchema: function ( featureTypeName, namespace, callback )
    {
        var formatter2 = new OpenLayers.Format.WFSDescribeFeatureType();
        var parts = featureTypeName.split( ":" );
        var typeName = parts.pop();
        var prefix;

        if ( parts.length > 0 )
        {
            prefix = parts.pop();
        }
        else
        {
            prefix = 'ns1';
        }

        var params = {
            service: 'WFS',
            version: '1.1.0',
            request: 'DescribeFeatureType',
            typeName: prefix + ':' + typeName,
            namespace: 'xmlns(' + prefix + '=' + namespace + ')'
        };

        var isAsync = (callback instanceof Function);
        var results;

        OpenLayers.Request.GET( {
            url: this.wfsServer,
            params: params,
            //dataType: "html",
            async: isAsync,
            success: function ( request )
            {
                var doc = request.responseXML;
                if ( !doc || !doc.documentElement )
                {
                    doc = request.responseText;
                }

                // use the tool to parse the data
                var response = (formatter2.read( doc ));

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
    getFeature: function ( featureTypeName, namespace, filter, callback )
    {
        var parts = featureTypeName.split( ":" );
        console.log('parts', parts);
        var typeName = parts.pop();
        console.log('typeName', typeName)
        var prefix;
        console.log('parts.length', parts.length)

        if ( parts.length > 0 )
        {
            prefix = parts.pop();
        }
        else
        {
            prefix = 'ns1';
        }

        var params = {
            service: 'WFS',
            version: '1.1.0',
            request: 'GetFeature',
            typeName: prefix + ':' + typeName,
            namespace: 'xmlns(' + prefix + '=' + namespace + ')',
            outputFormat: 'GML2',
            filter: filter || ''
        };

        console.log('getFeature params', params)

        var isAsync = (callback instanceof Function);
        var format = new OpenLayers.Format.GML.v3();

        OpenLayers.Request.GET( {
            url: this.wfsServer,
            params: params,
            success: function ( request )
            {
                console.log('request', request);

                var doc = request.responseXML;

                if ( !doc || !doc.documentElement )
                {
                    doc = request.responseText;
                }

                // use the tool to parse the data
                var response = (format.read( doc ));

                console.log('response', response );

                if ( isAsync )
                {
                    callback( response );
                }
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
    function init( params )
    {
        if ( params.wfsProxy )
        {
            console.log(params.wfsProxy);
            OpenLayers.ProxyHost = params.wfsProxy;
        }

//        var wfsServer = "http://localhost/geoserver/wfs";
//        var wfsServer = 'http://demo.boundlessgeo.com/geoserver/wfs';
//	    var wfsServer = 'http://clc.developpement-durable.gouv.fr/geoserver/wfs';

        var wfsClient = new OGC.WFS.Client( params.wfsServer );

        console.log( 'getFeatureTypes', wfsClient.getFeatureTypes() );

        /*
        console.log( 'getFeatureTypeSchema for topp:states',
            wfsClient.getFeatureTypeSchema( 'states', 'http://www.openplans.org/topp' ) );

        wfsClient.getFeature(
            'states',
            'http://www.openplans.org/topp',
            "STATE_ABBR='IN'",
            function ( it )
            {
                console.log( "getFeature for topp:states where STATE_ABBR='IN'", it );
            }
        );
        */

        wfsClient.getFeature('raster_entry', 'http://omar.ossim.org', "file_type='nitf'", function(it) {
            console.log('getFeature', it);
        } );

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


var TestWFS = (function ()
{
    var wfsServer;

    function getName( node, obj )
    {
        var name = this.getChildValue( node );
        if ( name )
        {
            // console.log('here');
            var parts = name.split( ":" );
            obj.name = name; // parts.pop();
            if ( parts.length > 0 )
            {
                obj.featureNS = this.lookupNamespaceURI( node, parts[0] );
            }
        }
    }

    var wfsVersions = ['v1', 'v1_0_0', 'v1_1_0', 'v2_0_0'];

    for ( var i = 0; i < wfsVersions.length; i++ )
    {
        OpenLayers.Util.extend( OpenLayers.Format.WFSCapabilities[wfsVersions[i]].prototype.readers.wfs, {
            Name: getName
        } );
    }

    function getCapabilities( callback )
    {
        var capabilities = new OpenLayers.Format.WFSCapabilities();

        OpenLayers.Request.GET( {
            url: wfsServer,
            params: {
                SERVICE: "WFS",
                VERSION: "1.1.0",
                REQUEST: "GetCapabilities"
            },
            success: function ( request )
            {
                var doc = request.responseText;
                if ( !doc || !doc.documentElement )
                {
                    doc = request.responseXML;
                }

                //console.log(doc);

                var response = capabilities.read( doc );

                if ( callback )
                {
                    callback( response );
                }
                // console.log('parser', capabilities.parser.CLASS_NAME);
            },
            failure: function ()
            {
                alert( "Trouble getting capabilities doc" );
                OpenLayers.Console.error.apply( OpenLayers.Console, arguments );
            }
        } );
    }

    function describeFeatureType( typeName, callback )
    {
        var schema = new OpenLayers.Format.WFSDescribeFeatureType();

        OpenLayers.Request.GET( {
            url: wfsServer,
            params: {
                SERVICE: "WFS",
                VERSION: "1.1.0",
                REQUEST: "DescribeFeatureType",
                typeName: typeName
            },
            success: function ( request )
            {
                var doc = request.responseText;
                if ( !doc || !doc.documentElement )
                {
                    doc = request.responseXML;
                }

                //console.log(doc);
                var response = schema.read( doc );

                if ( callback )
                {
                    callback( response );
                }
            },
            failure: function ()
            {
                alert( "Trouble getting capabilities doc" );
                OpenLayers.Console.error.apply( OpenLayers.Console, arguments );
            }
        } );
    }

    function convertCqlToXml( filterCql )
    {
        var cql = new OpenLayers.Format.CQL();
        var xml = new OpenLayers.Format.XML();
        var filterXml = xml.write( new OpenLayers.Format.Filter( {version: "1.1.0"} ).write( cql.read( filterCql ) ) );

        return filterXml;
    }

    function getFeature( typeName, filter, callback )
    {
        var gml = new OpenLayers.Format.GML.v3();

        // console.log( 'filter', filter );

        OpenLayers.Request.GET( {
            url: wfsServer,
            params: {
                SERVICE: "WFS",
                VERSION: "1.1.0",
                REQUEST: "GetFeature",
                typeName: typeName,
                filter: filter,
                outputFormat: 'gml3'
            },
            success: function ( request )
            {
                var doc = request.responseText;
                if ( !doc || !doc.documentElement )
                {
                    doc = request.responseXML;
                }

                //console.log(doc);
                var response = gml.read( doc );

                if ( callback )
                {
                    callback( response );
                }
            },
            failure: function ()
            {
                alert( "Trouble getting capabilities doc" );
                OpenLayers.Console.error.apply( OpenLayers.Console, arguments );
            }
        } );
    }

    function init( params )
    {
        wfsServer = params.wfsServer;
        OpenLayers.ProxyHost = params.wfsProxy;

        getCapabilities( function ( response )
        {
            console.log( 'getCapabilities', response );

            var featureTypes = response.featureTypeList.featureTypes;

            $.each( featureTypes, function ( i, featureType )
            {
                describeFeatureType( featureType.name, function ( it )
                {
                    var prefix = it.targetPrefix;
                    var typeName = it.featureTypes[0].typeName;

                    console.log( 'describeFeatureType for ' + prefix + ':' + typeName, it );
                } );
            } );
        } );

        var typeName = 'topp:states';
        var cqlFilter = "STATE_ABBR='IN'";

        getFeature( typeName, convertCqlToXml( cqlFilter ), function ( it )
        {
            console.log( 'getFeature for ' + typeName + ' where ' + cqlFilter, it );
        } );
    }

    return {
        init: init
    };
})();