angular.module('marbleCoreApp')
.factory('CorpusSenticnetFactory', ['$resource', 'getInterceptor', CorpusSenticnetFactory]);

function CorpusSenticnetFactory($resource, getInterceptor) {
    return $resource('/api/corpus/senticnet', {}, {
        query: {method: 'GET', isArray: false, interceptor: getInterceptor},
        create: { method: 'POST', interceptor: getInterceptor }
    })
};
