package ogc.wfs

class GeoserverWfsProxyService
{
  static transactional = false

  def geoserverBase = 'http://localhost:8080/geoserver'
  def grailsLinkGenerator

  def getCapabilities(GetCapabilitiesRequest wfsParams)
  {
    def ogcBase = grailsLinkGenerator.serverBaseURL
    def cacheFile = new File( "/tmp", "geoserver-WFS-GetCapabilities.xml" )
    def contentType = 'application/xml'
    def buffer = null

    if ( !cacheFile.exists() )
    {
      def url = grailsLinkGenerator.link( base: geoserverBase, absolute: true, uri: '/wfs',
          params: wfsParams.toParamMap() ).toURL()

      buffer = url.text.replace(
          grailsLinkGenerator.link( base: geoserverBase, uri: '/wfs' ),
          grailsLinkGenerator.link( absolute: true, uri: '/wfs' )
      )

      buffer = buffer.replace( geoserverBase, ogcBase )
      cacheFile.write( buffer )
    }
    else
    {
      buffer = cacheFile.text
    }

    return [contentType: contentType, buffer: buffer]
  }

  def describeFeatureType(DescribeFeatureTypeRequest wfsParams)
  {
    def ogcBase = grailsLinkGenerator.serverBaseURL
//    def name = wfsParams.find { it.key.equalsIgnoreCase( 'typeName' ) }?.value?.replace( ':', '_' )
    def name = wfsParams.typeName?.replace( ':', '_' )
    def cacheFile = new File( "/tmp", "geoserver-WFS-DescribeFeatureTypeRequest-${name}.xml" )
    def contentType = 'text/xml'
    def buffer = null
//      println cacheFile.absolutePath

    if ( !cacheFile.exists() )
    {
      def url = grailsLinkGenerator.link( base: geoserverBase, absolute: true, uri: '/wfs',
          params: wfsParams.toParamMap() ).toURL()

//        println url

      buffer = url.text.replace( geoserverBase, ogcBase )
      cacheFile.write( buffer )
    }
    else
    {
      buffer = cacheFile.text
    }

    [contentType: contentType, buffer: buffer]
  }
}
