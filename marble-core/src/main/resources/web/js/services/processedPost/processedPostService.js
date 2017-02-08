angular.module('marbleCoreApp')
.factory('ProcessedPostsFactory', ['$resource', 'getInterceptor', ProcessedPostsFactory]);

function ProcessedPostsFactory($resource, getInterceptor) {
    return $resource('/api/processedPosts', {}, {
        deleteByTopic: {method: 'DELETE', params: {topicName: '@topicName'}, isArray: false, interceptor: getInterceptor}
    })
};
