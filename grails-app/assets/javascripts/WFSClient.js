/**
 * Created by sbortman on 9/28/15.
 */
//= require jquery.js
//= require OpenLayersLite-formats.js
//= require_self

var OGC = OGC || {WFS:{} };

OGC.WFS.Client = OpenLayers.Class( {
    initialize: function ( wfsServer )
    {
        this.wfsServer = wfsServer;
        this.wfsFeatureTypes = this.getFeatureTypes();
        this.wfsFeatureTypeSchemas = this.getFeatureTypeSchema();
        //console.log(this.wfsServer);
    },
    getFeatureTypes: function (cb)
    {

        var localFeatureTypes = [];
        var isAsync = (cb instanceof Function);
        if(this.wfsFeatureTypes === undefined){

            var formatter = new OpenLayers.Format.WFSCapabilities();

            var that = this;

            $.ajax( {
                url: this.wfsServer + '?service=WFS&version=1.1.0&request=GetCapabilities',
                async: isAsync, // TODO: Refactor with better solution.  This is not recommended:
                // http://stackoverflow.com/a/14220323/4437795
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

                        localFeatureTypes.push( featureType );
                    }

                    //console.log( featureTypeNames );
                    //return featureTypeNames;

                }
            } );
            that.featureTypes = localFeatureTypes;
            console.log('fetching...');

        } else {

            console.log('cached...');

        }

        if (isAsync){
            console.log('We are ASync!');
            cb(this.featureTypes);
        }
        console.log(isAsync);

        return this.featureTypes;

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
    CLASS_NAME: "OGC.WFS.Client"
} );