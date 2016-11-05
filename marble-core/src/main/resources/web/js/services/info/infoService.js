angular.module('marbleCoreApp')
.factory('HomeInfoFactory', ['$resource', 'getInterceptor', HomeInfoFactory])
.factory('ProcessorsInfoFactory', ['$resource', 'getInterceptor', ProcessorsInfoFactory])
.factory('PlottersInfoFactory', ['$resource', 'getInterceptor', PlottersInfoFactory]);

function HomeInfoFactory($resource, getInterceptor) {
    return $resource('/api/info/home', {}, {
        show: {method: 'GET', isArray: false, interceptor: getInterceptor}
    })
};

function ProcessorsInfoFactory($resource, getInterceptor) {
    return $resource('/api/info/processors', {}, {
        show: {method: 'GET', isArray: true, interceptor: getInterceptor}
    })
};

function PlottersInfoFactory($resource, getInterceptor) {
    return $resource('/api/info/plotters', {}, {
        show: {method: 'GET', isArray: true, interceptor: getInterceptor}
    })
};

