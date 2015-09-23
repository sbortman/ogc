package ogc.wfs

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by sbortman on 9/22/15.
 */
@Validateable
@ToString( includeNames = true )
class GetFeatureRequest
{
  static mapWith = 'none'

  String service
  String version
  String request

  String typeName
  String filter
  String resultType
  String outputFormat
  String sortBy
  String propertyName

  Integer maxFeatures
  Integer startIndex

  static mapping = {
    version false
  }
}
