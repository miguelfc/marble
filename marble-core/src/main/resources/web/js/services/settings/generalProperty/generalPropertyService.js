angular.module('marbleCoreApp')
.factory('GeneralPropertiesFactory', ['$resource', 'getInterceptor', GeneralPropertiesFactory])
.factory('GeneralPropertyFactory', ['$resource', 'getInterceptor', GeneralPropertyFactory])
.factory('GeneralPropertiesSearchByNameFactory', ['$resource', 'getInterceptor', GeneralPropertiesSearchByNameFactory]);

function GeneralPropertiesFactory($resource, getInterceptor) {
    return $resource('/api/generalProperties', {}, {
        query: {method: 'GET', isArray: false, interceptor: getInterceptor },
        create: { method: 'POST', interceptor: getInterceptor }
    })
};

function GeneralPropertyFactory($resource, getInterceptor) {
    return $resource('/api/generalProperties/:name', {name: '@name'}, {
        show: { method: 'GET', interceptor: getInterceptor },
        update: { method: 'PUT', interceptor: getInterceptor },
        delete: { method: 'DELETE', interceptor: getInterceptor }
    })
};

function GeneralPropertiesSearchByNameFactory($resource, getInterceptor) {
    return $resource('/api/generalProperties/search/findByNameMatches', {}, {
    	searchByName: { method: 'GET', params: {name: '@name'}, isArray: false, interceptor: getInterceptor}
    })
};