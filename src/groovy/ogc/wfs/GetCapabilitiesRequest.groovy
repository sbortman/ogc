package ogc.wfs

import grails.validation.Validateable
import groovy.transform.ToString
import ogc.CaseInsensitiveBind

/**
 * Created by sbortman on 9/3/15.
 */
@Validateable
@ToString( includeNames = true )
class GetCapabilitiesRequest implements CaseInsensitiveBind
{
  static mapWith = 'none'

  String service
  String version
  String request

  static mapping = {
    version false
  }
}
