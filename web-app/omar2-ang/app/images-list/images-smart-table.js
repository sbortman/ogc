'use strict';
omarApp.controller('wfsCtrl', ['$scope', '$http', '$filter', '$log', function ($scope, $http, $filter, $log) {
	OpenLayers.ProxyHost = "/ogc/wfsParser/proxyWFS?url="

    $scope.source = [];
	$scope.endPoint = 'http://demo.boundlessgeo.com/geoserver/wfs';
    //$scope.endPoint = 'http://omar.ossim.org/omar/wfs';
    //$scope.endPoint = 'http://localhost:8080/geoserver/wfs';
    //$scope.endPoint = 'http://demo.opengeo.org/geoserver/wfs';
    //$scope.endPoint = 'http://giswebservices.massgis.state.ma.us/geoserver/wfs';
    //$scope.endPoint = 'http://clc.developpement-durable.gouv.fr/geoserver/wfs';
    //$scope.endPoint = 'http://localhost:8080/omar/wfs'
    //$scope.endPoint = 'http://10.0.10.183:9999/wfs';
    //$scope.endPoint = 'http://10.0.10.183/geoserver/wfs';

    $scope.showFeatureTypeSelect = false;
    $scope.showFeatureTypeTable = false;

    $scope.featureTypeItem = {};

	$scope.featureTypes = [];
     
    $scope.getCapabilitiesUrl = '?service=WFS&version=1.1.0&request=GetCapabilities';
    $scope.describeFeatureUrl = '?service=WFS&version=1.1.0&request=DescribeFeatureType&typeName=';
	$scope.getFeatureUrl = '?service=WFS&version=1.1.0&request=GetFeature&outputFormat=JSON&maxFeatures=50&typeName=';
 	
 	var wfsClient; 

 	//TODO: Move to a factory
	//Refactored: 10.05.2015 - GetCapabilities 
	$scope.getCapabilities = function () {

		wfsClient = new OGC.WFS.Client($scope.endPoint);

		$scope.showFeatureTypeSelect = false;
		$scope.showFeatureTypeTable = false;

		//Refactored: 10-06.2015 - Use the Client request from wfsClient library
        $scope.capabilities = wfsClient.getFeatureTypes();
        $log.debug('$scope.capabilities', $scope.capabilities);
        $log.debug('$scope.capabilities.length', $scope.capabilities.length);
        if ($scope.capabilities.length >= 1){
			// angular.forEach($scope.capabilities, function(value, key){
			// 	$log.debug( key + ':' + value.featureNS);
			// 	$scope.featureTypeItem = '{id: ' + key + ', name: ' +  value.name + '}';
			// 	//$log.debug(featureTypeItem);
			// 	$scope.featureTypes.push

			// });
			$scope.showFeatureTypeSelect = true;
		}
		else{
			$scope.showFeatureTypeSelect = false;
			$scope.showFeatureTypeTable = false;
			alert('Error. Could not retrieve data from end point.  Please check the URL and try again.')
		}

		//$scope.describeFeature();
		//$log.debug($scope.selectedCapability)
	}
	
	//TODO: Move to a factory
	// DescribeFeature
    $scope.describeFeature = function () {
	  //   $http.get($scope.endPoint + $scope.describeFeatureUrl + $scope.selectedCapability)
	  //   	.success(
	  //   		function(data){
	    			
	  //   			var formatDescribeFeature = new OpenLayers.Format.WFSDescribeFeatureType();
	    			
	  //   			$scope.schema = formatDescribeFeature.read(data);
	  //   			//$log.debug('schema', $scope.schema.featureTypes[0].properties);

	  //   			$scope.columns = $scope.schema.featureTypes[0].properties;
	  //   			$log.debug('describeFeature', $scope.columns);
	  //   			$log.debug('$scope.columns.length', $scope.columns.length);
	  //   			if($scope.columns.length >= 1){
	  //   				$scope.getFeature();
	  //   				$scope.showFeatureTypeTable = true;
	  //   			}

			// 	}
			// //Refactored 10.06.2015: Need and error callback to check status
			// ).catch(function(err){
			// 	$log.debug(err.stack);
			// });
		//Refactored (MA-78) 10.07.2015: Now using WFSClient to fetch the data instead of $http 
		$scope.columns = wfsClient.getFeatureTypeSchema($scope.selectedCapability.name, $scope.selectedCapability.featureNS).featureTypes[0].properties;
		$log.debug('$scope.columns (describeFeature)', $scope.columns); 
		$log.debug('$scope.columns.length', $scope.columns.length);
		if($scope.columns.length >= 1){
			$scope.getFeature();
			$scope.showFeatureTypeTable = true;
		}

	}
	
	//TODO: Move to a factory
	// GetFeature
	$scope.getFeature = function () {

		// $http.get($scope.endPoint + $scope.getFeatureUrl + $scope.selectedCapability.name)
		// 	.then(
		// 		function(data){
		// 			$scope.features = data.data.features;
		// 			$log.debug('getFeature', $scope.features);
		// 	}).catch(function(err){
		// 		$log.debug(err.stack);
		// 	});

		
		//Refactored (MA-78) 10.07.2015: Now using WFSClient to fetch the data instead of $http
		// $scope.getFeatureObj = wfsClient.getFeature('omar:raster_entry', 'http://omar.ossim.org', "file_type='nitf'", function(it) {
  //           $log.warn('getFeature', it);
  //       } );
  //       $log.warn('$scope.getFeatureObj', $scope.getFeatureObj);

  		// $scope.rowCollection = [];  // base collection
    //     $scope.itemsByPage = 5;
    //     $scope.displayedCollection = [].concat($scope.rowCollection);  // displayed collection
		
		wfsClient.getFeature($scope.selectedCapability.name, $scope.selectedCapability.featureNS, 'GML3', undefined, function(it) {
            	$scope.features = it;

            	$scope.rowCollection = it;
            	//$scope.displayedCollection = [].concat($scope.rowCollection);
            	//console.log($scope.rowCollection);

            	$log.warn('$scope.features', $scope.features);
        	});

	}


}]);