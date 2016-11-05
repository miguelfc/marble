'use strict';

/**
 * @ngdoc directive
 * @name izzyposWebApp.directive:adminPosHeader
 * @description
 * # adminPosHeader
 */
angular.module('marbleCoreApp')
	.directive('chat',function(){
		return {
        templateUrl:'templates/directives/chat/chat.html',
        restrict: 'E',
        replace: true,
    	}
	});


