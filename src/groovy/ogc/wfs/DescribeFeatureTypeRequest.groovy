package ogc.wfs

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by sbortman on 9/4/15.
 */
@Validateable
@ToString( includeNames = true )
class DescribeFeatureTypeRequest
{
  static mapWith = 'none'

  String service
  String version
  String request

  String typeName
  String namespace

  static mapping = {
    version false
  }

}
