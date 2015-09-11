package ogc.wfs

import geoscript.feature.Schema
import geoscript.workspace.Workspace
import groovy.xml.StreamingMarkupBuilder

import org.geotools.factory.CommonFactoryFinder
import org.opengis.filter.capability.FunctionName

class WebFeatureService
{
  static transactional = false

  def grailsLinkGenerator

  def typeMappings = [
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


  def ogcNamespacesByPrefix = [
      // These are OGC/XML specs
      xsi: "http://www.w3.org/2001/XMLSchema-instance",
      wfs: "http://www.opengis.net/wfs",
      ows: "http://www.opengis.net/ows",
      gml: "http://www.opengis.net/gml",
      ogc: "http://www.opengis.net/ogc",
      xlink: "http://www.w3.org/1999/xlink",
  ]

  def outputFormats = [
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

  def geometryOperands = [
      'gml:Envelope',
      'gml:Point',
      'gml:LineString',
      'gml:Polygon'
  ]

  def spatialOperators = [
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

  def comparisonOperators = [
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

  def geoserverHome = '/Applications/GeoServer.app/Contents/Java'

  def featureTypeNamespaces = [
      // These are feature types
      [prefix: 'it.geosolutions', uri: "http://www.geo-solutions.it"],
      [prefix: 'cite', uri: "http://www.opengeospatial.net/cite"],
      [prefix: 'omar', uri: "http://omar.ossim.org"],
      [prefix: 'tiger', uri: "http://www.census.gov"],
      [prefix: 'sde', uri: "http://geoserver.sf.net"],
      [prefix: 'topp', uri: "http://www.openplans.org/topp"],
      [prefix: 'sf', uri: "http://www.openplans.org/spearfish"],
      [prefix: 'nurc', uri: "http://www.nurc.nato.int"]
  ]

  def datastores = [[
      namespaceId: 'tiger',
      datastoreId: 'tiger',
      datastoreParams: [
          url: "file://${geoserverHome}/data_dir/data/nyc".toURL(),
          namespace: featureTypeNamespaces.find { it.prefix == 'tiger' }.uri
      ]
  ], [
      namespaceId: 'sf',
      datastoreId: 'sf',
      datastoreParams: [
          url: "file://${geoserverHome}/data_dir/data/sf".toURL(),
          namespace: featureTypeNamespaces.find { it.prefix == 'sf' }.uri
      ]
  ], [
      namespaceId: 'topp',
      datastoreId: 'states_shapefile',
      datastoreParams: [
          url: "file://${geoserverHome}/data_dir/data/shapefiles".toURL(),
          namespace: featureTypeNamespaces.find { it.prefix == 'topp' }.uri
      ]
  ], [
      namespaceId: 'topp',
      datastoreId: 'taz_shapes',
      datastoreParams: [
          url: "file://${geoserverHome}/data_dir/data/taz_shapes".toURL(),
          namespace: featureTypeNamespaces.find { it.prefix == 'topp' }.uri
      ]
  ], [
      namespaceId: 'omar',
      datastoreId: 'omar-1.8.19-prod',
      datastoreParams: [
          dbtype: 'postgis',
          host: 'localhost',
          port: 5432,
          database: 'omardb-1.8.19-prod',
          user: 'postgres',
          passwd: 'postgres',
          'Expose primary keys': true,
          namespace: featureTypeNamespaces.find { it.prefix == 'omar' }.uri
      ]
  ]]

  def featureTypes = [[
      name: 'poly_landmarks',
      title: 'Manhattan (NY) landmarks',
      description: 'Manhattan landmarks, identifies water, lakes, parks, interesting buildings',
      keywords: ['landmarks', 'DS_poly_landmarks', 'manhattan', 'poly_landmarks'],
      datastoreId: 'tiger'
  ], [
      name: 'poi',
      title: 'Manhattan (NY) points of interest',
      description: 'Points of interest in New York, New York (on Manhattan). One of the attributes contains the name of a file with a picture of the point of interest.',
      keywords: ['poi', 'Manhattan', 'DS_poi', 'points_of_interest'],
      datastoreId: 'tiger'
  ], [
      name: 'tiger_roads',
      title: 'Manhattan (NY) roads',
      description: 'Highly simplified road layout of Manhattan in New York..',
      keywords: ['DS_tiger_roads', 'tiger_roads', 'roads'],
      datastoreId: 'tiger'
  ], [
      name: 'archsites',
      title: 'Spearfish archeological sites',
      description: 'Sample data from GRASS, archeological sites location, Spearfish, South Dakota, USA',
      keywords: ['archsites', 'spearfish', 'sfArchsites', 'archeology'],
      datastoreId: 'sf'
  ], [
      name: 'bugsites',
      title: 'Spearfish bug locations',
      description: 'Sample data from GRASS, bug sites location, Spearfish, South Dakota, USA',
      keywords: ['spearfish', 'sfBugsites', 'insects', 'bugsites', 'tiger_beetles'],
      datastoreId: 'sf'
  ], [
      name: 'restricted',
      title: 'Spearfish restricted areas',
      description: 'Sample data from GRASS, restricted areas, Spearfish, South Dakota, USA',
      keywords: ['spearfish', 'restricted', 'areas', 'sfRestricted'],
      datastoreId: 'sf'
  ], [
      name: 'roads',
      title: 'Spearfish roads',
      description: 'Sample data from GRASS, road layout, Spearfish, South Dakota, USA',
      keywords: ['sfRoads', 'spearfish', 'roads'],
      datastoreId: 'sf'
  ], [
      name: 'streams',
      title: 'Spearfish streams',
      description: 'Sample data from GRASS, streams, Spearfish, South Dakota, USA',
      keywords: ['spearfish', 'sfStreams', 'streams'],
      datastoreId: 'sf'
  ], [
      name: 'tasmania_cities',
      title: 'Tasmania cities',
      description: 'Cities in Tasmania (actually, just the capital)',
      keywords: ['cities', 'Tasmania'],
      datastoreId: 'taz_shapes'
  ], [
      name: 'tasmania_roads',
      title: 'Tasmania roads',
      description: 'Main Tasmania roads',
      keywords: ['Roads', 'Tasmania'],
      datastoreId: 'taz_shapes'
  ], [
      name: 'tasmania_state_boundaries',
      title: 'Tasmania state boundaries',
      description: 'Tasmania state boundaries',
      keywords: ['boundaries', 'tasmania_state_boundaries', 'Tasmania'],
      datastoreId: 'taz_shapes'
  ], [
      name: 'tasmania_water_bodies',
      title: 'Tasmania water bodies',
      description: 'Tasmania water bodies',
      keywords: ['Lakes', 'Bodies', 'Australia', 'Water', 'Tasmania'],
      datastoreId: 'taz_shapes'
  ], [
      name: 'states',
      title: 'USA Population',
      description: 'This is some census data on the states.',
      keywords: ['census', 'united', 'boundaries', 'state', 'states'],
      datastoreId: 'states_shapefile'
  ], [
      name: 'giant_polygon',
      title: 'World rectangle',
      description: 'A simple rectangular polygon covering most of the world, it\'s only used for the purpose of providing a background (WMS bgcolor could be used instead)',
      keywords: ['DS_giant_polygon', 'giant_polygon'],
      datastoreId: 'tiger'
  ], [
      name: 'raster_entry',
      title: 'raster_entry',
      description: '',
      keywords: ['raster_entry', 'features'],
      datastoreId: 'omar-1.8.19-prod'
  ], [
      name: 'video_data_set',
      title: 'video_data_set',
      description: '',
      keywords: ['video_data_set', 'features'],
      datastoreId: 'omar-1.8.19-prod'
  ]]

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
    def featureTypeNamespacesByPrefix = featureTypeNamespaces.inject( [:] ) { a, b -> a[b.prefix] = b.uri; a }
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
          featureTypes.each { featureType ->
            def datastore = datastores.find { it.datastoreId == featureType.datastoreId }
            //def ns = featureTypeNamespaces.find { it.prefix == ds.namespaceId }

            Workspace.withWorkspace( datastore?.datastoreParams ) { Workspace workspace ->
              def layer = workspace[featureType.name]
              def uri = layer?.schema?.uri
              def prefix = featureTypeNamespaces.find { it.uri == uri }?.prefix
              def geoBounds = ( layer?.proj?.epsg == 4326 ) ? layer?.bounds : layer?.bounds?.reproject( 'epsg:4326' )
              FeatureType( "xmlns:${prefix}": uri ) {
                Name( "${prefix}:${featureType.name}" )
                Title( featureType.title )
                Abstract( featureType.description )
                ows.Keywords {
                  featureType.keywords?.each { keyword ->
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
          gml: 'gml="http://www.opengis.net/gml',
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

  def describeFeatureType(DescribeFeatureTypeRequest wfsParams)
  {
    def (namespaceId, typeName) = wfsParams?.typeName?.split( ':' )

    println "${namespaceId} ${typeName}"

    def datastoreIds = featureTypes.findAll { it.name == typeName }?.datastoreId
    def datastore = datastores.find { it.datastoreId in datastoreIds && it.namespaceId == namespaceId }

    println datastore

    String schemaLocation = grailsLinkGenerator.link( absolute: true, uri: '/' )
    def xml = null

    Workspace.withWorkspace( datastore?.datastoreParams ) { Workspace workspace ->
      Schema schema = workspace[typeName].schema
      String prefix = featureTypeNamespaces.find { it.uri == schema.uri }.prefix

      xml = generateSchema( schema, prefix, schemaLocation )
    }
    [contentType: 'text/xml', buffer: xml]
  }

  def getFeature(def wfsParams)
  {

  }
}
