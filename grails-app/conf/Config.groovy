// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [ // the first one is the default format
    all: '*/*', // 'all' maps to '*' or the first available format in withFormat
    atom: 'application/atom+xml',
    css: 'text/css',
    csv: 'text/csv',
    form: 'application/x-www-form-urlencoded',
    html: ['text/html', 'application/xhtml+xml'],
    js: 'text/javascript',
    json: ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss: 'application/rss+xml',
    text: 'text/plain',
    hal: ['application/hal+json', 'application/hal+xml'],
    xml: ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
  views {
    gsp {
      encoding = 'UTF-8'
      htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
      codecs {
        expression = 'html' // escapes values inside ${}
        scriptlet = 'html' // escapes output from scriptlets in GSPs
        taglib = 'none' // escapes output from taglibs
        staticparts = 'none' // escapes output from static template parts
      }
    }
    // escapes all not-encoded output at final stage of outputting
    // filteringCodecForContentType.'text/html' = 'html'
  }
}


grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart = false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// configure passing transaction's read-only attribute to Hibernate session, queries and criterias
// set "singleSession = false" OSIV mode in hibernate configuration after enabling
grails.hibernate.pass.readonly = false
// configure passing read-only to OSIV session by default, requires "singleSession = false" OSIV mode
grails.hibernate.osiv.readonly = false

environments {
  development {
    grails.logging.jul.usebridge = true
  }
  production {
    grails.logging.jul.usebridge = false
    // TODO: grails.serverURL = "http://www.changeme.com"
  }
}

// log4j configuration
log4j.main = {
  // Example of changing the log pattern for the default console appender:
  //
  //appenders {
  //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
  //}

  error 'org.codehaus.groovy.grails.web.servlet',        // controllers
      'org.codehaus.groovy.grails.web.pages',          // GSP
      'org.codehaus.groovy.grails.web.sitemesh',       // layouts
      'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
      'org.codehaus.groovy.grails.web.mapping',        // URL mapping
      'org.codehaus.groovy.grails.commons',            // core / classloading
      'org.codehaus.groovy.grails.plugins',            // plugins
      'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
      'org.springframework',
      'org.hibernate',
      'net.sf.ehcache.hibernate'
}

wfs {
  geoserverHome = '/Applications/GeoServer.app/Contents/Java'

  featureTypeNamespaces = [
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

  datastores = [[
      namespaceId: 'tiger',
      datastoreId: 'tiger',
      datastoreParams: [
          url: "file://${geoserverHome}/data_dir/data/nyc" as String,
          namespace: featureTypeNamespaces.find { it.prefix == 'tiger' }.uri
      ]
  ], [
      namespaceId: 'sf',
      datastoreId: 'sf',
      datastoreParams: [
          url: "file://${geoserverHome}/data_dir/data/sf" as String,
          namespace: featureTypeNamespaces.find { it.prefix == 'sf' }.uri
      ]
  ], [
      namespaceId: 'topp',
      datastoreId: 'states_shapefile',
      datastoreParams: [
          url: "file://${geoserverHome}/data_dir/data/shapefiles" as String,
          namespace: featureTypeNamespaces.find { it.prefix == 'topp' }.uri
      ]
  ], [
      namespaceId: 'topp',
      datastoreId: 'taz_shapes',
      datastoreParams: [
          url: "file://${geoserverHome}/data_dir/data/taz_shapes" as String,
          namespace: featureTypeNamespaces.find { it.prefix == 'topp' }.uri
      ]
  ], [
      namespaceId: 'omar',
      datastoreId: 'omar-1.8.19-prod',
      datastoreParams: [
          dbtype: 'postgis',
          host: 'localhost',
          port: '5432',
          database: 'omardb-1.8.19-prod',
          user: 'postgres',
          passwd: 'postgres',
          'Expose primary keys': 'true',
          namespace: featureTypeNamespaces.find { it.prefix == 'omar' }.uri
      ]
  ]]

  featureTypes = [[
      name: 'poly_landmarks',
      title: 'Manhattan (NY) landmarks',
      description: 'Manhattan landmarks, identifies water, lakes, parks, interesting buildings',
      keywords: ['landmarks', 'DS_poly_landmarks', 'manhattan', 'poly_landmarks'] as String[],
      datastoreId: 'tiger'
  ], [
      name: 'poi',
      title: 'Manhattan (NY) points of interest',
      description: 'Points of interest in New York, New York (on Manhattan). One of the attributes contains the name of a file with a picture of the point of interest.',
      keywords: ['poi', 'Manhattan', 'DS_poi', 'points_of_interest'] as String[],
      datastoreId: 'tiger'
  ], [
      name: 'tiger_roads',
      title: 'Manhattan (NY) roads',
      description: 'Highly simplified road layout of Manhattan in New York..',
      keywords: ['DS_tiger_roads', 'tiger_roads', 'roads'] as String[],
      datastoreId: 'tiger'
  ], [
      name: 'archsites',
      title: 'Spearfish archeological sites',
      description: 'Sample data from GRASS, archeological sites location, Spearfish, South Dakota, USA',
      keywords: ['archsites', 'spearfish', 'sfArchsites', 'archeology'] as String[],
      datastoreId: 'sf'
  ], [
      name: 'bugsites',
      title: 'Spearfish bug locations',
      description: 'Sample data from GRASS, bug sites location, Spearfish, South Dakota, USA',
      keywords: ['spearfish', 'sfBugsites', 'insects', 'bugsites', 'tiger_beetles'] as String[],
      datastoreId: 'sf'
  ], [
      name: 'restricted',
      title: 'Spearfish restricted areas',
      description: 'Sample data from GRASS, restricted areas, Spearfish, South Dakota, USA',
      keywords: ['spearfish', 'restricted', 'areas', 'sfRestricted'] as String[],
      datastoreId: 'sf'
  ], [
      name: 'roads',
      title: 'Spearfish roads',
      description: 'Sample data from GRASS, road layout, Spearfish, South Dakota, USA',
      keywords: ['sfRoads', 'spearfish', 'roads'] as String[],
      datastoreId: 'sf'
  ], [
      name: 'streams',
      title: 'Spearfish streams',
      description: 'Sample data from GRASS, streams, Spearfish, South Dakota, USA',
      keywords: ['spearfish', 'sfStreams', 'streams'] as String[],
      datastoreId: 'sf'
  ], [
      name: 'tasmania_cities',
      title: 'Tasmania cities',
      description: 'Cities in Tasmania (actually, just the capital)',
      keywords: ['cities', 'Tasmania'] as String[],
      datastoreId: 'taz_shapes'
  ], [
      name: 'tasmania_roads',
      title: 'Tasmania roads',
      description: 'Main Tasmania roads',
      keywords: ['Roads', 'Tasmania'] as String[],
      datastoreId: 'taz_shapes'
  ], [
      name: 'tasmania_state_boundaries',
      title: 'Tasmania state boundaries',
      description: 'Tasmania state boundaries',
      keywords: ['boundaries', 'tasmania_state_boundaries', 'Tasmania'] as String[],
      datastoreId: 'taz_shapes'
  ], [
      name: 'tasmania_water_bodies',
      title: 'Tasmania water bodies',
      description: 'Tasmania water bodies',
      keywords: ['Lakes', 'Bodies', 'Australia', 'Water', 'Tasmania'] as String[],
      datastoreId: 'taz_shapes'
  ], [
      name: 'states',
      title: 'USA Population',
      description: 'This is some census data on the states.',
      keywords: ['census', 'united', 'boundaries', 'state', 'states'] as String[],
      datastoreId: 'states_shapefile'
  ], [
      name: 'giant_polygon',
      title: 'World rectangle',
      description: 'A simple rectangular polygon covering most of the world, it\'s only used for the purpose of providing a background (WMS bgcolor could be used instead)',
      keywords: ['DS_giant_polygon', 'giant_polygon'] as String[],
      datastoreId: 'tiger'
  ], [
      name: 'raster_entry',
      title: 'raster_entry',
      description: '',
      keywords: ['raster_entry', 'features'] as String[],
      datastoreId: 'omar-1.8.19-prod'
  ], [
      name: 'video_data_set',
      title: 'video_data_set',
      description: '',
      keywords: ['video_data_set', 'features'] as String[],
      datastoreId: 'omar-1.8.19-prod'
  ]]
}