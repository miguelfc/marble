angular.module('marbleCoreApp')
.factory('HomeInfoFactory', ['$resource', 'getInterceptor', HomeInfoFactory]);

function HomeInfoFactory($resource, getInterceptor) {
    return $resource('/api/info/home', {}, {
        show: {method: 'GET', isArray: false, interceptor: getInterceptor}
    })
};
