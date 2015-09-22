package ogc.wfs

class NamespaceInfo
{
  String prefix
  String uri

  static constraints = {
    prefix( unique: true, blank: false )
    uri( url: true )
  }
}
