package ogc.wfs

import ogc.BindUtil

class WfsController
{
  def webFeatureService

  def index()
  {
    println params

    def wfsParams = params - params.subMap( ['controller', 'format'] )
    def op = wfsParams.find { it.key.equalsIgnoreCase( 'request' ) }

    //println wfsParams

    switch ( op.value.toUpperCase() )
    {
    case 'GETCAPABILITIES':
      forward( action: 'getCapabilities', params: BindUtil.fixParamNames( GetCapabilitiesRequest, wfsParams ) )
      break
    case 'DESCRIBEFEATURETYPE':
      forward( action: 'describeFeatureType', params: BindUtil.fixParamNames( DescribeFeatureTypeRequest, wfsParams ) )
      break
    case 'GETFEATURE':
      forward( action: 'getFeature', params: BindUtil.fixParamNames( GetFeatureRequest, wfsParams ) )
      break
    }

    //render contentType: results.contentType, text: results.buffer
  }

  def getCapabilities(GetCapabilitiesRequest input)
  {
    println "getCapabilities: ${params} ${request.method} ${input}"

    //def wfsParams = params - params.subMap( ['controller', 'format'] )
    def results = webFeatureService.getCapabilities( input )

    render contentType: results.contentType, text: results.buffer
  }

  def describeFeatureType(DescribeFeatureTypeRequest input)
  {
    println "describeFeatureType: ${params} ${request.method} ${input}"

    //def wfsParams = params - params.subMap( ['controller', 'format'] )
    def results = webFeatureService.describeFeatureType( input )

    render contentType: results.contentType, text: results.buffer
  }

  def getFeature(GetFeatureRequest input)
  {
    println "getFeature: ${params} ${request.method} ${input}"
    //def results = geoserverFeatureService.getFeature( input )
    def results = webFeatureService.getFeature( input )

    render contentType: results.contentType, text: results.buffer
  }
}
