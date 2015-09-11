/**
 * Created by sbortman on 9/3/15.
 */
package ogc

trait CaseInsensitiveBind
{
  def fixParamNames(def params)
  {
    def nonPersistent = ["log", "class", "constraints", "properties", "errors", "mapping", "metaClass", 'mapWith']
    def names = getMetaClass()?.properties?.grep { it.field }?.name?.sort() - nonPersistent

    def newParams = params?.inject( [:] ) { a, b ->
      def propName = names.find { it.equalsIgnoreCase( b.key ) && b.value != null }
      if ( propName )
      {
        //println "${propName}=${b.value}"
        a[propName] = b.value
      }
      else
      {
        a[b.key] = b.value
      }
      a
    }

    params?.clear()
    params.putAll( newParams )
    params
  }

  def toParamMap()
  {
    def nonPersistent = ["log", "class", "constraints", "properties", "errors", "mapping", "metaClass", 'mapWith']
    def newMap = [:]
    getProperties().each { property ->
      if (!nonPersistent.contains(property.key)) {
        newMap.put property.key, property.value
      }
    }
    newMap
  }
}