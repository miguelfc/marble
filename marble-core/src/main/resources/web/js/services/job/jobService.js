angular.module('marbleCoreApp')
.factory('JobsFactory', ['$resource', 'getInterceptor', JobsFactory])
.factory('JobFactory', ['$resource', 'getInterceptor', JobFactory])
.factory('JobExtendedFactory', ['$resource', 'getInterceptor', JobExtendedFactory])
.factory('JobsSearchByTopicNameFactory', ['$resource', 'getInterceptor', JobsSearchByTopicNameFactory])
.factory('JobsByTopicNameFactory', ['$resource', 'getInterceptor', JobsByTopicNameFactory]);

function JobsFactory($resource, getInterceptor) {
    return $resource('/api/jobs', {}, {
        query: {method: 'GET', isArray: false, interceptor: getInterceptor},
        create: { method: 'POST', interceptor: getInterceptor },
        deleteByTopic: {method: 'DELETE', params: {topicName: '@topicName'}, isArray: false, interceptor: getInterceptor}
    })
};

function JobFactory($resource, getInterceptor) {
    return $resource('/api/jobs/:id', {id: '@id'}, {
        show: { method: 'GET' , interceptor: getInterceptor},
        update: { method: 'PUT' , interceptor: getInterceptor},
        delete: { method: 'DELETE' , interceptor: getInterceptor}
    })
};

function JobExtendedFactory($resource, getInterceptor) {
    return $resource('/api/jobs/:id?projection=jobExtended', {id: '@id'}, {
        show: { method: 'GET' , interceptor: getInterceptor}
    })
};

function JobsByTopicNameFactory($resource, getInterceptor) {
    return $resource('/api/jobs/search/findByTopic_name', {}, {
    	searchByName: { method: 'GET', params: {name: '@name', projection: 'jobExtended'}, isArray: false, interceptor: getInterceptor}
    })
};

function JobsSearchByTopicNameFactory($resource, getInterceptor) {
    return $resource('/api/jobs/search/findByTopic_nameMatches', {}, {
    	searchByName: { method: 'GET', params: {name: '@name', projection: 'jobExtended'}, isArray: false, interceptor: getInterceptor}
    })
};
