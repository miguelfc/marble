angular.module('marbleCoreApp')
.factory('ChartsFactory', ['$resource', 'getInterceptor', ChartsFactory])
.factory('ChartFactory', ['$resource', 'getInterceptor', ChartFactory])
.factory('ChartListFactory',['$resource', 'getInterceptor', ChartListFactory])
.factory('ChartListByTopicNameFactory', ['$resource', 'getInterceptor', ChartListByTopicNameFactory])
.factory('ChartListSearchByTopicNameFactory', ['$resource', 'getInterceptor', ChartListSearchByTopicNameFactory]);

function ChartsFactory($resource, getInterceptor) {
    return $resource('/api/charts', {}, {
        query: {method: 'GET', isArray: false, interceptor: getInterceptor},
        create: { method: 'POST', interceptor: getInterceptor },
        deleteByTopic: {method: 'DELETE', params: {topicName: '@topicName'}, isArray: false, interceptor: getInterceptor}
    })
};

function ChartFactory($resource, getInterceptor) {
    return $resource('/api/charts/:id?projection=chartExtended', {id: '@id'}, {
        show: { method: 'GET' , interceptor: getInterceptor},
        update: { method: 'PUT' , interceptor: getInterceptor},
        delete: { method: 'DELETE' , interceptor: getInterceptor}
    })
};

function ChartListFactory($resource, getInterceptor) {
    return $resource('/api/charts/:id?projection=chartList', {id: '@id'}, {
        show: { method: 'GET', interceptor: getInterceptor }
    })
};

function ChartListByTopicNameFactory($resource, getInterceptor) {
    return $resource('/api/charts/search/findByTopic_name', {}, {
    	searchByName: { method: 'GET', params: {name: '@name', projection: 'chartList'}, isArray: false, interceptor: getInterceptor}
    })
};

function ChartListSearchByTopicNameFactory($resource, getInterceptor) {
    return $resource('/api/charts/search/findByTopic_nameMatches', {}, {
    	searchByName: { method: 'GET', params: {name: '@name', projection: 'chartList'}, isArray: false, interceptor: getInterceptor}
    })
};
