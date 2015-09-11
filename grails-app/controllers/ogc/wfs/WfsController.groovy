package ogc.wfs

class WfsController
{
  def geoserverWfsProxyService
  def webFeatureService

  def index()
  {
    def wfsParams = params - params.subMap( ['controller', 'format'] )
    def op = wfsParams.find { it.key.equalsIgnoreCase( 'request' ) }

    //println wfsParams

    switch ( op.value.toUpperCase() )
    {
    case 'GETCAPABILITIES':
      forward( action: 'getCapabilities', params: new GetCapabilitiesRequest().fixParamNames( wfsParams ) )
      break
    case 'DESCRIBEFEATURETYPE':
      forward( action: 'describeFeatureType', params: new DescribeFeatureTypeRequest().fixParamNames( wfsParams ) )
      break
    case 'GETFEATURE':

      break
    }

    //render contentType: results.contentType, text: results.buffer
  }

  def getCapabilities(GetCapabilitiesRequest input)
  {
    println input

    //def wfsParams = params - params.subMap( ['controller', 'format'] )
    def results = webFeatureService.getCapabilities( input )

    render contentType: results.contentType, text: results.buffer
  }

  def describeFeatureType(DescribeFeatureTypeRequest input)
  {
    println input

    //def wfsParams = params - params.subMap( ['controller', 'format'] )
    def results = webFeatureService.describeFeatureType( input )

    render contentType: results.contentType, text: results.buffer
  }

  def getFeature()
  {

  }
}
