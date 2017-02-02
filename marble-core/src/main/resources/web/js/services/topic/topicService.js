angular.module('marbleCoreApp')
.factory('TopicsFactory', ['$resource', 'getInterceptor', TopicsFactory])
.factory('TopicsSearchByNameFactory', ['$resource', 'getInterceptor', TopicsSearchByNameFactory])
.factory('TopicFactory', ['$resource', 'getInterceptor', TopicFactory])
.factory('TopicInfoFactory', ['$resource', 'getInterceptor', TopicInfoFactory])
.factory('TopicExtractFactory', ['$resource', 'getInterceptor', TopicExtractFactory])
.factory('TopicStreamFactory', ['$resource', 'getInterceptor', TopicStreamFactory])
.factory('TopicProcessFactory', ['$resource', 'getInterceptor', TopicProcessFactory])
.factory('TopicPlotFactory', ['$resource', 'getInterceptor', TopicPlotFactory]);

function TopicsFactory($resource, getInterceptor) {
    return $resource('/api/topics', {}, {
        query: {method: 'GET', isArray: false, interceptor: getInterceptor},
        create: { method: 'POST', interceptor: getInterceptor }
    })
};

function TopicsSearchByNameFactory($resource, getInterceptor) {
    return $resource('/api/topics/search/findByNameMatches', {}, {
    	searchByName: { method: 'GET', params: {name: '@name'}, isArray: false, interceptor: getInterceptor}
    })
};

function TopicFactory($resource, getInterceptor) {
    return $resource('/api/topics/:name', {}, {
        show: { method: 'GET', interceptor: getInterceptor },
        update: { method: 'PUT', params: {name: '@name'}, interceptor: getInterceptor },
        delete: { method: 'DELETE', params: {name: '@name'}, interceptor: getInterceptor }
    })
};

function TopicInfoFactory($resource, getInterceptor) {
    return $resource('/api/topics/:name/info', {}, {
        show: { method: 'GET', interceptor: getInterceptor }
    })
};

function TopicExtractFactory($resource, getInterceptor) {
    return $resource('/api/topics/:name/extract', {}, {
        extract: { method: 'POST', params: {name: '@name'}, interceptor: getInterceptor}
    })
};

function TopicStreamFactory($resource, getInterceptor) {
	return $resource('/api/topics/:name/stream', {}, {
		stream: { method: 'POST', params: {name: '@name'}, interceptor: getInterceptor},
		stopStream: { method: 'DELETE', params: {name: '@name'}, interceptor: getInterceptor}
	})
};

function TopicProcessFactory($resource, getInterceptor) {
    return $resource('/api/topics/:name/process', {}, {
        process: { method: 'POST', params: {name: '@name'}, interceptor: getInterceptor}
    })
};

function TopicPlotFactory($resource, getInterceptor) {
    return $resource('/api/topics/:name/plot', {}, {
        plot: { method: 'POST', params: {name: '@name'}, interceptor: getInterceptor}
    })
};
