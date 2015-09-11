package ogc.wfs

class WfsFilters
{

  def filters = {
    wfsGetCapabilities( uri: '/wfs/getCapabilities' ) {
      before = {
        println "before: ${params}"
        new GetCapabilitiesRequest().fixParamNames( params )
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
        new DescribeFeatureTypeRequest().fixParamNames( params )
        println "after: ${params}"
      }
      after = { Map model ->

      }
      afterView = { Exception e ->

      }


    }

  }
}