angular.module('marbleCoreApp')
.factory('PlotsFactory', ['$resource', 'getInterceptor', PlotsFactory])
.factory('PlotFactory', ['$resource', 'getInterceptor', PlotFactory])
.factory('PlotListFactory',['$resource', 'getInterceptor', PlotListFactory])
.factory('PlotListByTopicNameFactory', ['$resource', 'getInterceptor', PlotListByTopicNameFactory])
.factory('PlotListSearchByTopicNameFactory', ['$resource', 'getInterceptor', PlotListSearchByTopicNameFactory]);

function PlotsFactory($resource, getInterceptor) {
    return $resource('/api/plots', {}, {
        query: {method: 'GET', isArray: false, interceptor: getInterceptor},
        create: { method: 'POST', interceptor: getInterceptor },
        deleteByTopic: {method: 'DELETE', params: {topicName: '@topicName'}, isArray: false, interceptor: getInterceptor}
    })
};

function PlotFactory($resource, getInterceptor) {
    return $resource('/api/plots/:id?projection=plotExtended', {id: '@id'}, {
        show: { method: 'GET' , interceptor: getInterceptor},
        update: { method: 'PUT' , interceptor: getInterceptor},
        delete: { method: 'DELETE' , interceptor: getInterceptor}
    })
};

function PlotListFactory($resource, getInterceptor) {
    return $resource('/api/plots/:id?projection=plotList', {id: '@id'}, {
        show: { method: 'GET', interceptor: getInterceptor }
    })
};

function PlotListByTopicNameFactory($resource, getInterceptor) {
    return $resource('/api/plots/search/findByTopic_name', {}, {
    	searchByName: { method: 'GET', params: {name: '@name', projection: 'plotList'}, isArray: false, interceptor: getInterceptor}
    })
};

function PlotListSearchByTopicNameFactory($resource, getInterceptor) {
    return $resource('/api/plots/search/findByTopic_nameMatches', {}, {
    	searchByName: { method: 'GET', params: {name: '@name', projection: 'plotList'}, isArray: false, interceptor: getInterceptor}
    })
};
