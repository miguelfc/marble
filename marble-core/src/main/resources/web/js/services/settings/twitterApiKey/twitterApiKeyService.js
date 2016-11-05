angular.module('marbleCoreApp')
.factory('TwitterApiKeysFactory', ['$resource', 'getInterceptor', TwitterApiKeysFactory])
.factory('TwitterApiKeyFactory', ['$resource', 'getInterceptor', TwitterApiKeyFactory]);

function TwitterApiKeysFactory($resource, getInterceptor) {
    return $resource('/api/twitterApiKeys', {}, {
        query: { method: 'GET', isArray: false, interceptor: getInterceptor },
        create: { method: 'POST', interceptor: getInterceptor }
    })
};

function TwitterApiKeyFactory($resource, getInterceptor) {
    return $resource('/api/twitterApiKeys/:name', {name: '@id'}, {
        show: { method: 'GET', interceptor: getInterceptor },
        update: { method: 'PUT', interceptor: getInterceptor },
        delete: { method: 'DELETE', interceptor: getInterceptor }
    })
};
