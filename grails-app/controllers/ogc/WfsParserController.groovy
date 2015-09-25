package ogc

class WfsParserController
{

  def index() {}

  def proxyWFS()
  {
    def url = new String( params.url.decodeBase64() )
    println url

    //render file: url.bytes
    url.toURL().withInputStream {
      response.outputStream << it
    }
  }
}
