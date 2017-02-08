angular.module('marbleCoreApp')
.factory('PostsFactory', ['$resource', 'getInterceptor', PostsFactory])
.factory('PostFactory', ['$resource', 'getInterceptor', PostFactory])
.factory('PostWithTopicFactory', ['$resource', 'getInterceptor',PostWithTopicFactory])
.factory('PostsSearchByTopicNameFactory', ['$resource', 'getInterceptor', PostsSearchByTopicNameFactory])
.factory('PostsByTopicNameFactory', ['$resource', 'getInterceptor', PostsByTopicNameFactory])
.factory('PostTagFactory', ['$resource', 'getInterceptor', PostTagFactory]);

function PostsFactory($resource, getInterceptor) {
    return $resource('/api/posts', {}, {
        query: {method: 'GET', isArray: false, interceptor: getInterceptor},
        deleteByTopic: {method: 'DELETE', params: {topicName: '@topicName'}, isArray: false, interceptor: getInterceptor}
    })
};

function PostFactory($resource, getInterceptor) {
    return $resource('/api/posts/:id', {id: '@id'}, {
        show: { method: 'GET' , interceptor: getInterceptor},
        update: { method: 'PUT' , interceptor: getInterceptor},
        delete: { method: 'DELETE' , interceptor: getInterceptor}
    })
};

function PostWithTopicFactory($resource, getInterceptor) {
    return $resource('/api/posts/:id?projection=postWithTopic', {id: '@id'}, {
        show: { method: 'GET' , interceptor: getInterceptor}
    })
};

function PostsByTopicNameFactory($resource, getInterceptor) {
    return $resource('/api/posts/search/findByTopicName', {}, {
    	searchByName: { method: 'GET', params: {name: '@name', projection: 'postWithTopic'}, isArray: false, interceptor: getInterceptor}
    })
};

function PostsSearchByTopicNameFactory($resource, getInterceptor) {
    return $resource('/api/posts/search/findByTopicNameMatches', {}, {
    	searchByName: { method: 'GET', params: {name: '@name', projection: 'postWithTopic'}, isArray: false, interceptor: getInterceptor}
    })
};

function PostTagFactory($resource, getInterceptor) {
    return $resource('/api/posts/tag/:id', {id: '@id'}, {
        tag: { method: 'PATCH' , params: {user: '@user', polarity: '@polarity'}, interceptor: getInterceptor},
        untag: { method: 'PATCH' , params: {user: '@user'}, interceptor: getInterceptor}
    })
};