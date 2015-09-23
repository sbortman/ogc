package ogc.wfs

import ogc.BindUtil

class WfsFilters
{

  def filters = {
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