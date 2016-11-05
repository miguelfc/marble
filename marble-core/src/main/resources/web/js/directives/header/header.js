'use strict';

/**
 * @ngdoc directive
 * @name izzyposWebApp.directive:adminPosHeader
 * @description
 * # adminPosHeader
 */
angular.module('marbleCoreApp')
	.directive('header',function(){
		return {
        templateUrl:'templates/directives/header/header.html',
        restrict: 'E',
        replace: true,
    	}
	});


