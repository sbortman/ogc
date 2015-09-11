package ogc.wfs

import grails.validation.Validateable
import groovy.transform.ToString
import ogc.CaseInsensitiveBind

/**
 * Created by sbortman on 9/4/15.
 */
@Validateable
@ToString( includeNames = true )
class DescribeFeatureTypeRequest implements CaseInsensitiveBind
{
  static mapWith = 'none'

  String service
  String version
  String request

  String typeName

  static mapping = {
    version false
  }

}
