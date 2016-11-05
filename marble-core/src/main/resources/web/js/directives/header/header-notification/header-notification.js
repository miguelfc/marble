'use strict';

/**
 * @ngdoc directive
 * @name izzyposWebApp.directive:adminPosHeader
 * @description
 * # adminPosHeader
 */

angular.module('marbleCoreApp')
	.directive('headerNotification',function(){
		return {
        templateUrl:'templates/directives/header/header-notification/header-notification.html',
        restrict: 'E',
        replace: true,
    	}
	});


