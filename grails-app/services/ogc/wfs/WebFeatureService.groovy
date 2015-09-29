package ogc.wfs

import geoscript.feature.Schema
import geoscript.filter.Filter
import geoscript.layer.Layer
import geoscript.layer.io.GmlWriter
import geoscript.workspace.Workspace
import groovy.xml.StreamingMarkupBuilder

import org.geotools.factory.CommonFactoryFinder
import org.opengis.filter.capability.FunctionName

class WebFeatureService
{
  static transactional = false

  def grailsLinkGenerator

  static final Map<String, String> typeMappings = [
      'Double': 'xsd:double',
      'Integer': 'xsd:int',
      'Long': 'xsd:long',
      'Polygon': 'gml:PolygonPropertyType',
      'MultiPolygon': 'gml:MultiPolygonPropertyType',
      'MultiLineString': 'gml:MultiLineStringPropertyType',
      'String': 'xsd:string',
      'java.lang.Boolean': 'xsd:boolean',
      'java.math.BigDecimal': 'xsd:decimal',
      'java.sql.Timestamp': 'xsd:dateTime'
  ]


  static final Map<String, String> ogcNamespacesByPrefix = [
      // These are OGC/XML specs
      xsi: "http://www.w3.org/2001/XMLSchema-instance",
      wfs: "http://www.opengis.net/wfs",
      ows: "http://www.opengis.net/ows",
      gml: "http://www.opengis.net/gml",
      ogc: "http://www.opengis.net/ogc",
      xlink: "http://www.w3.org/1999/xlink",
  ]

  static final List<String> outputFormats = [
      'text/xml; subtype=gml/3.1.1',
      'GML2',
      'KML',
      'SHAPE-ZIP',
      'application/gml+xml; version=3.2',
      'application/json',
      'application/vnd.google-earth.kml xml',
      'application/vnd.google-earth.kml+xml',
      'csv',
      'gml3',
      'gml32',
      'json',
      'text/xml; subtype=gml/2.1.2',
      'text/xml; subtype=gml/3.2'
  ]

  static final List<String> geometryOperands = [
      'gml:Envelope',
      'gml:Point',
      'gml:LineString',
      'gml:Polygon'
  ]

  static final List<String> spatialOperators = [
      "Disjoint",
      "Equals",
      "DWithin",
      "Beyond",
      "Intersects",
      "Touches",
      "Crosses",
      "Within",
      "Contains",
      "Overlaps",
      "BBOX"
  ]

  static final List<String> comparisonOperators = [
      'LessThan',
      'GreaterThan',
      'LessThanEqualTo',
      'GreaterThanEqualTo',
      'EqualTo',
      'NotEqualTo',
      'Like',
      'Between',
      'NullCheck'
  ]

  private static def listFunctions2()
  {
    List names = []
    CommonFactoryFinder.getFunctionFactories().each { f ->
      f.functionNames.each { fn ->
        if ( fn instanceof FunctionName )
        {
          names << [name: fn.functionName.toString(), argCount: fn.argumentCount]
        }
      }
    }
    names.sort { a, b -> a.name.compareToIgnoreCase b.name }
  }

  def getCapabilities(GetCapabilitiesRequest wfsParams)
  {
    def wfsServiceAddress = grailsLinkGenerator.link( absolute: true, uri: '/wfs' )
    def wfsSchemaLocation = grailsLinkGenerator.link( absolute: true, uri: '/schemas/wfs/1.1.0/wfs.xsd' )
    def featureTypeNamespacesByPrefix = NamespaceInfo.list().inject( [:] ) { a, b -> a[b.prefix] = b.uri; a }
    def functionNames = listFunctions2()

    def x = {
      mkp.xmlDeclaration()
      mkp.declareNamespace( ogcNamespacesByPrefix )
      mkp.declareNamespace( featureTypeNamespacesByPrefix )
      wfs.WFS_Capabilities( version: "1.1.0", xmlns: "http://www.opengis.net/wfs",
          'xsi:schemaLocation': "http://www.opengis.net/wfs ${wfsSchemaLocation}"
      ) {
        ows.ServiceIdentification {
          ows.Title( 'My WFS Server' ) // Put in config
          ows.Abstract( 'This is a test of the emergency broadcast system' ) // Put in config
          ows.Keywords {
            def keywords = ['WFS', 'WMS', 'OMAR'] // Put in config
            keywords.each { keyword ->
              ows.Keyword( keyword )
            }
          }
          ows.ServiceType( 'WFS' )
          ows.ServiceTypeVersion( '1.1.0' ) // Put in config?
          ows.Fees( 'NONE' )
          ows.AccessConstraints( 'NONE' )
        }
        ows.ServiceProvider {
          ows.ProviderName( 'OSSIM Labs' )  // Put in config?
          ows.ServiceContact {
            ows.IndividualName( 'Scott Bortman' ) // Put in config?
            ows.PositionName( 'OMAR Developer' ) // Put in config?
            ows.ContactInfo {
              ows.Phone {
                ows.Voice()
                ows.Facsimile()
              }
              ows.Address {
                ows.DeliveryPoint()
                ows.City()
                ows.AdministrativeArea()
                ows.PostalCode()
                ows.Country()
                ows.ElectronicMailAddress()
              }
            }
          }
        }
        ows.OperationsMetadata {
          ows.Operation( name: "GetCapabilities" ) {
            ows.DCP {
              ows.HTTP {
                ows.Get( 'xlink:href': wfsServiceAddress )
                //ows.Post('xlink:href': wfsServiceAddress )
              }
            }
            ows.Parameter( name: "AcceptVersions" ) {
              //ows.Value( '1.0.0' )
              ows.Value( '1.1.0' )
            }
            ows.Parameter( name: "AcceptFormats" ) {
              ows.Value( 'text/xml' )
            }
          }
          ows.Operation( name: "DescribeFeatureType" ) {
            ows.DCP {
              ows.HTTP {
                ows.Get( 'xlink:href': wfsServiceAddress )
                //ows.Post( 'xlink:href': wfsServiceAddress )
              }
            }
            ows.Parameter( name: "outputFormat" ) {
              ows.Value( 'text/xml; subtype=gml/3.1.1' )
            }
          }
          ows.Operation( name: "GetFeature" ) {
            ows.DCP {
              ows.HTTP {
                ows.Get( 'xlink:href': wfsServiceAddress )
                //ows.Post( 'xlink:href': wfsServiceAddress )
              }
            }
            ows.Parameter( name: "resultType" ) {
              ows.Value( 'results' )
              ows.Value( 'hits' )
            }
            ows.Parameter( name: "outputFormat" ) {
              outputFormats.each { outputFormat ->
                ows.Value( outputFormat )
              }
            }
            // ows.Constraint( name: "LocalTraverseXLinkScope" ) {
            //   ows.Value( 2 )
            // }
          }
          // ows.Operation( name: "GetGmlObject" ) {
          //   ows.DCP {
          //     ows.HTTP {
          //       ows.Get( 'xlink:href': wfsServiceAddress )
          //       ows.Post( 'xlink:href': wfsServiceAddress )
          //     }
          //   }
          // }
          // ows.Operation( name: "LockFeature" ) {
          //   ows.DCP {
          //     ows.HTTP {
          //       ows.Get( 'xlink:href': wfsServiceAddress )
          //       ows.Post( 'xlink:href': wfsServiceAddress )
          //     }
          //   }
          //   ows.Parameter( name: "releaseAction" ) {
          //     ows.Value( 'ALL' )
          //     ows.Value( 'SOME' )
          //   }
          // }
          // ows.Operation( name: "GetFeatureWithLock" ) {
          //   ows.DCP {
          //     ows.HTTP {
          //       ows.Get( 'xlink:href': wfsServiceAddress )
          //       ows.Post( 'xlink:href': wfsServiceAddress )
          //     }
          //   }
          //   ows.Parameter( name: "resultType" ) {
          //     ows.Value( 'results' )
          //     ows.Value( 'hits' )
          //   }
          //   ows.Parameter( name: "outputFormat" ) {
          //   	outputFormats.each { outputFormat ->
          //     	ows.Value( outputFormat )
          //   	}
          //   }
          // }
          // ows.Operation( name: "Transaction" ) {
          //   ows.DCP {
          //     ows.HTTP {
          //       ows.Get( 'xlink:href': wfsServiceAddress )
          //       ows.Post( 'xlink:href': wfsServiceAddress )
          //     }
          //   }
          //   ows.Parameter( name: "inputFormat" ) {
          //     ows.Value( 'text/xml; subtype=gml/3.1.1' )
          //   }
          //   ows.Parameter( name: "idgen" ) {
          //     ows.Value( 'GenerateNew' )
          //     ows.Value( 'UseExisting' )
          //     ows.Value( 'ReplaceDuplicate' )
          //   }
          //   ows.Parameter( name: "releaseAction" ) {
          //     ows.Value( 'ALL' )
          //     ows.Value( 'SOME' )
          //   }
          // }
        }
        FeatureTypeList {
          Operations {
            Operation( 'Query' )
            // Operation( 'Insert' )
            // Operation( 'Update' )
            // Operation( 'Delete' )
            // Operation( 'Lock' )
          }
          LayerInfo.list().each { layerInfo ->
            WorkspaceInfo workspaceInfo = WorkspaceInfo.findByName( layerInfo.workspaceInfo.name )

            Workspace.withWorkspace( workspaceInfo?.workspaceParams ) { Workspace workspace ->
              def layer = workspace[layerInfo.name]
              def uri = layer?.schema?.uri
              def prefix = NamespaceInfo.findByUri( uri )?.prefix
              def geoBounds = ( layer?.proj?.epsg == 4326 ) ? layer?.bounds : layer?.bounds?.reproject( 'epsg:4326' )
              FeatureType( "xmlns:${prefix}": uri ) {
                Name( "${prefix}:${layerInfo.name}" )
                Title( layerInfo.title )
                Abstract( layerInfo.description )
                ows.Keywords {
                  layerInfo.keywords?.each { keyword ->
                    ows.Keyword( keyword )
                  }
                }
                DefaultSRS( "urn:x-ogc:def:crs:${layer?.proj?.id}" )
                ows.WGS84BoundingBox {
                  ows.LowerCorner( "${geoBounds?.minX} ${geoBounds?.minY}" )
                  ows.UpperCorner( "${geoBounds?.maxX} ${geoBounds?.maxY}" )
                }
              }
            }
          }
        }
        ogc.Filter_Capabilities {
          ogc.Spatial_Capabilities {
            ogc.GeometryOperands {
              geometryOperands.each { geometryOperand ->
                ogc.GeometryOperand( geometryOperand )
              }
            }
            ogc.SpatialOperators {
              spatialOperators.each { spatialOperator ->
                ogc.SpatialOperator( name: spatialOperator )
              }
            }
          }
          ogc.Scalar_Capabilities {
            ogc.LogicalOperators()
            ogc.ComparisonOperators {
              comparisonOperators.each { comparisonOperator ->
                ogc.ComparisonOperator( comparisonOperator )
              }
            }
            ogc.ArithmeticOperators {
              ogc.SimpleArithmetic()
              ogc.Functions {
                ogc.FunctionNames {
                  functionNames.each { functionName ->
                    ogc.FunctionName( nArgs: functionName.argCount, functionName.name )
                  }
                }
              }
            }
          }
          ogc.Id_Capabilities {
            ogc.FID()
            ogc.EID()
          }
        }
      }
    }

    def xml = new StreamingMarkupBuilder( encoding: 'utf-8' ).bind( x )
    def contentType = 'application/ xml '

    return [contentType: contentType, buffer: xml.toString()]
  }


  private def generateSchema(Schema schema, String prefix, String schemaLocation)
  {
    def x = {
      mkp.xmlDeclaration()
      mkp.declareNamespace(
          gml: 'http://www.opengis.net/gml',
          "${prefix}": schema.uri,
          xsd: 'http://www.w3.org/2001/XMLSchema'
      )
      xsd.schema( elementFormDefault: 'qualified', targetNamespace: schema.uri ) {
        xsd.import( namespace: 'http://www.opengis.net/gml', schemaLocation: "${schemaLocation}/schemas/gml/3.1.1/base/gml.xsd" )
        xsd.complexType( name: "${schema.name}Type" ) {
          xsd.complexContent {
            xsd.extension( base: 'gml:AbstractFeatureType' ) {
              xsd.sequence {
                schema.fields.each { field ->
                  def descr = schema.featureType.getDescriptor( field.name )
                  xsd.element( maxOccurs: "${descr.maxOccurs}", minOccurs: "${descr.minOccurs}",
                      name: "${field.name}", nillable: "${descr.nillable}",
                      type: "${typeMappings.get( field.typ, field.typ )}" )
                }
              }
            }
          }
        }
        xsd.element( name: schema.name, substitutionGroup: 'gml:_Feature', type: "${prefix}:${schema.name}Type" )
      }
    }

    def xml = new StreamingMarkupBuilder( encoding: 'utf-8' ).bind( x )

    xml.toString()
  }

  private def generateHitCount(def hitCount, def namespaceInfo)
  {
    def namespaces = [
        gml: "http://www.opengis.net/gml",
        ogc: "http://www.opengis.net/ogc",
        ows: "http://www.opengis.net/ows",
        wfs: "http://www.opengis.net/wfs",
        xlink: "http://www.w3.org/1999/xlink",
        xs: "http://www.w3.org/2001/XMLSchema",
        xsi: "http://www.w3.org/2001/XMLSchema-instance",
    ]

    namespaces[namespaceInfo.prefix] = namespaceInfo.uri

    def x = {
      mkp.xmlDeclaration()
      mkp.declareNamespace( namespaces )
      wfs.FeatureCollection(
          numberOfFeatures: hitCount,
          timeStamp: new Date().format( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone( 'GMT' ) ),
          'xsi:schemaLocation': "http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.1.0/wfs.xsd"
      )
    }

    def xml = new StreamingMarkupBuilder( encoding: 'utf-8' ).bind( x )

    xml.toString()
  }

  def describeFeatureType(DescribeFeatureTypeRequest wfsParams)
  {
    def x = wfsParams.typeName?.split( ':' )
    def namespacePrefix
    def layerName

    switch ( x.size() )
    {
    case 1:
      layerName = x?.last()
      break
    case 2:
      (namespacePrefix, layerName) = x
      break
    }

    def namespaceInfo

    if ( wfsParams.namespace )
    {
      def pattern = /xmlns\(\w+=(.*)\)/
      def matcher = wfsParams.namespace =~ pattern

      if ( matcher )
      {
        def uri = matcher[0][1]

        namespaceInfo = NamespaceInfo.findByUri( uri )
      }
      else
      {
        println "${'*' * 20} No Match ${'*' * 20}"
      }

      layerName = wfsParams?.typeName?.split( ':' )?.last()
    }
    else
    {
      namespaceInfo = NamespaceInfo.findByPrefix( namespacePrefix )
    }

    println "${namespaceInfo} ${layerName}"

    LayerInfo layerInfo = LayerInfo.where {
      name == layerName && workspaceInfo.namespaceInfo == namespaceInfo
    }.get()

    String schemaLocation = grailsLinkGenerator.serverBaseURL
    def xml = null

    Workspace.withWorkspace( layerInfo?.workspaceInfo?.workspaceParams ) {
      Workspace workspace ->

        Schema schema = workspace[layerName].schema
        String prefix = NamespaceInfo.findByUri( schema.uri ).prefix

        xml = generateSchema( schema, prefix, schemaLocation )
    }

//    println xml

    [contentType: 'text/xml', buffer: xml]
  }

  def getFeature(GetFeatureRequest wfsParams)
  {
    println wfsParams

    def (namespacePrefix, layerName) = wfsParams?.typeName?.split( ':' )

    LayerInfo layerInfo = LayerInfo.where {
      name == layerName && workspaceInfo.namespaceInfo.prefix == namespacePrefix
    }.list().first()

    def xml = null

    Workspace.withWorkspace( layerInfo.workspaceInfo?.workspaceParams ) { Workspace workspace ->
      def layer = workspace[layerName]
      def filter = wfsParams.filter ?: Filter.PASS

      switch ( wfsParams?.resultType?.toLowerCase() )
      {
      case "hits":
        def hitCount = layer?.count( filter: filter )

        xml = generateHitCount( hitCount, layerInfo?.workspaceInfo?.namespaceInfo )
        break
      case "results":
        def writer = new GmlWriter()
        def propertyNames = wfsParams?.propertyName?.split( ',' )
        def newSchema = ( propertyNames ) ? layer?.schema?.includeFields( propertyNames as List, layer?.name ) : new Schema( layer?.name, layer?.schema?.fields, layer?.schema?.uri )
        def newLayer = new Layer( newSchema.name, newSchema )

        layer.eachFeature( filter: filter, fields: propertyNames ) {
          newLayer.add( it )
        }

        xml = writer.write( newLayer, 3.0, false, false, true, namespacePrefix )
        break

      default:
        def writer = new GmlWriter()
        def propertyNames = wfsParams?.propertyName?.split( ',' )
        def newSchema = ( propertyNames ) ? layer?.schema?.includeFields( propertyNames as List, layer?.name ) : new Schema( layer?.name, layer?.schema?.fields, layer?.schema?.uri )
        def newLayer = new Layer( newSchema.name, newSchema )

        layer.eachFeature( filter: filter, fields: propertyNames as List ) {
          newLayer.add( it )
        }

        xml = writer.write( newLayer, 3.0, false, false, true, namespacePrefix )
        break
      }
    }

    [contentType: 'text/xml', buffer: xml]
  }

}
