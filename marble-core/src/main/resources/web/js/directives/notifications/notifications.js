'use strict';

/**
 * @ngdoc directive
 * @name izzyposWebApp.directive:adminPosHeader
 * @description
 * # adminPosHeader
 */
angular.module('marbleCoreApp')
	.directive('notifications',function(){
		return {
        templateUrl:'templates/directives/notifications/notifications.html',
        restrict: 'E',
        replace: true,
    	}
	});


