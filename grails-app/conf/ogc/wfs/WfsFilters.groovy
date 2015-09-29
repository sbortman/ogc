package ogc.wfs

import ogc.BindUtil

class WfsFilters
{

  def filters = {

//    wfs( uri: '/wfs/**', controllerName: 'wfs' ) {
//      before = {
//        def op = params.find { it.key.toLowerCase() == 'request' }
//
//        switch ( op?.value?.toLowerCase() )
//        {
//        case 'getcapabilities':
//          action = 'getCapabilities'
//          BindUtil.fixParamNames( GetCapabilitiesRequest, params )
//          break
//        case 'describefeaturetype':
//          action = 'describeFeatureType'
//          BindUtil.fixParamNames( DescribeFeatureTypeRequest, params )
//          break
//        case 'getfeature':
//          action = 'getFeature'
//          BindUtil.fixParamNames( GetFeatureRequest, params )
//          break
//        }
//      }
//    }
    wfsGetCapabilities( uri: '/wfs/getCapabilities' ) {
      before = {
        println "before: ${params}"
        BindUtil.fixParamNames( GetCapabilitiesRequest, params )
        println "after: ${params}"
      }
      after = { Map model ->

      }
      afterView = { Exception e ->

      }
    }
    wfsDescribeFeatureType( uri: '/wfs/describeFeatureType' ) {
      before = {
        println "before: ${params}"
        BindUtil.fixParamNames( DescribeFeatureTypeRequest, params )
        println "after: ${params}"
      }
      after = { Map model ->

      }
      afterView = { Exception e ->

      }
    }
    wfsGetFeature( uri: '/wfs/getFeature' ) {
      before = {
        println "before: ${params}"
        BindUtil.fixParamNames( GetFeatureRequest, params )
        println "after: ${params}"
      }
      after = { Map model ->

      }
      afterView = { Exception e ->

      }
    }
  }
}