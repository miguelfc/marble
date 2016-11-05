'use strict';

/**
 * @ngdoc directive
 * @name izzyposWebApp.directive:adminPosHeader
 * @description
 * # adminPosHeader
 */
angular.module('marbleCoreApp')
	.directive('timeline',function() {
    return {
        templateUrl:'templates/directives/timeline/timeline.html',
        restrict: 'E',
        replace: true,
    }
  });
