angular.module('marbleCoreApp')
  .controller('MainCtrl', ['$scope', 'HomeInfoFactory', function($scope,HomeInfoFactory) {
      
      $scope.info = HomeInfoFactory.show();
  }]);
