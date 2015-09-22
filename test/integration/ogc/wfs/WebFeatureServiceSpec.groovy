package ogc.wfs

import grails.test.spock.IntegrationSpec
import geoscript.workspace.WFS
import spock.lang.Shared


class WebFeatureServiceSpec extends IntegrationSpec
{
  @Shared
  String wfsServer = "http://localhost:9999/ogc/wfs"
  @Shared
  WFS wfs

  def setupSpec()
  {
    wfs = new WFS( [protocol: 'get'], "${wfsServer}?service=WFS&version=1.1.0&request=GetCapabilities" )
  }

  def cleanupSpec()
  {
    wfs?.close()
  }

  void "Test GetCapabilities & DescribeFeatureType"()
  {
  setup: ''
    def y = [
        'omar:raster_entry',
        'omar:video_data_set',
        'sf:archsites',
        'sf:bugsites',
        'sf:restricted',
        'sf:roads',
        'sf:streams',
        'tiger:giant_polygon',
        'tiger:poi',
        'tiger:poly_landmarks',
        'tiger:tiger_roads',
        'topp:states',
        'topp:tasmania_cities',
        'topp:tasmania_roads',
        'topp:tasmania_state_boundaries',
        'topp:tasmania_water_bodies'
    ]

  when: ''
    def x = wfs?.names?.sort()

  then: ''
    //println x
    //println y
    assert x == y
  }
}
