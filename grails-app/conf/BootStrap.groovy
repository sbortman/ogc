import ogc.wfs.LayerInfo
import ogc.wfs.NamespaceInfo
import ogc.wfs.WorkspaceInfo

class BootStrap
{
  def grailsApplication
  def messageSource

  def init = { servletContext ->
    grailsApplication.config.wfs.featureTypeNamespaces.each {
      NamespaceInfo.findOrSaveByPrefixAndUri( it.prefix, it.uri )
    }

    grailsApplication.config.wfs.datastores.each { datastore ->
      def workspaceInfo = WorkspaceInfo.findOrCreateByName( datastore.datastoreId )

      workspaceInfo.with {
        namespaceInfo = NamespaceInfo.findByPrefix( datastore.namespaceId )
        workspaceParams = datastore.datastoreParams
        save()
      }

      if ( workspaceInfo.hasErrors() )
      {
        workspaceInfo.errors.allErrors.each { println messageSource.getMessage( it, null ) }
      }
    }

    grailsApplication.config.wfs.featureTypes.each { featureType ->
      WorkspaceInfo.withTransaction {
        def workspaceInfo = WorkspaceInfo.findByName( featureType.datastoreId )
        def layerInfo = LayerInfo.findOrCreateByNameAndWorkspaceInfo( featureType.name, workspaceInfo )

        layerInfo.with {
          title = featureType.title
          description = featureType.description
          keywords = featureType.keywords
        }

        workspaceInfo.addToLayerInfoList( layerInfo )
        workspaceInfo.save()

        if ( workspaceInfo.hasErrors() )
        {
          workspaceInfo.errors.allErrors.each { println messageSource.getMessage( it, null ) }
        }
      }
    }
  }

  def destroy = {
  }
}
