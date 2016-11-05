angular.module('marbleCoreApp')
.factory('ProcessorValidationFactory', ['$resource', 'getInterceptor', ProcessorValidationFactory]);

function ProcessorValidationFactory($resource, getInterceptor) {
    return $resource('/api/info/validation/process', {}, {
        validate: {method: 'POST', params: {}, interceptor: getInterceptor}
    })
};
