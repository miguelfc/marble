angular.module('marbleCoreApp').controller(
        'CorpusCtrl',
        [
                '$scope',
                '$compile',
                '$state',
                'Upload',
                '$timeout',
                'ProcessorsInfoFactory',
                'ProcessorValidationFactory',
                function($scope, $compile, $state, Upload, $timeout, ProcessorsInfoFactory, ProcessorValidationFactory) {

                    $scope.senticnet = {};
                    $scope.uploadSenticnet = function() {
                        uploadCorpus($scope.senticnet, '/api/corpus/senticnet');
                    };

                    $scope.sentiWordNet = {};
                    $scope.uploadSentiWordNet = function() {
                        uploadCorpus($scope.sentiWordNet, '/api/corpus/sentiwordnet');
                    };

                    $scope.validation = {};
                    $scope.uploadValidation = function() {
                        uploadCorpus($scope.validation, '/api/corpus/validation');
                    };

                    var uploadCorpus = function(element, url) {
                        element.file.upload = Upload.upload({
                            url : url,
                            data : {
                                file : element.file
                            },
                        });
                        element.file.upload.then(function(response) {
                            $timeout(function() {
                                element.error = '';
                                element.progress = '';
                                element.success = "The file was processed succesfully."
                            });
                        }, function(response) {
                            if (response.data == null) {
                                element.error = 'An error occurred while uploading the file ' + element.file.name + '. No response was received.';
                            } else if (response.status == "401") {
                                element.error = 'You are not authorized to do this. Please login as a user with priviledges to do this operation.';
                            } else {
                                element.error = 'An error occurred while uploading the file ' + element.file.name + ': ' + response.data.message;
                                if (response.data.message != null && response.data.message.indexOf('The reference to entity') != -1) {
                                    element.error += ' Most probably you have rogue "&" characters in your file and you need to fix them'
                                            + ' (Hint: look for master&commander and replace it with master&amp;commander).';
                                }
                            }
                            element.progress = '';
                            element.success = '';
                            +' (status ' + response.status + ')';
                        }, function(evt) {
                            var percent = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
                            element.error = '';
                            element.progress = 'Loading... (' + percent + '% Completed)';
                            element.success = '';
                        });
                    };

                    ProcessorsInfoFactory.show().$promise.then(function(data) {
                        /*
                        for ( var i in data) {
                            // Remove Parameters
                            console.log(data[i]);
                            if (data[i].parameters) {
                                data[i].parameters = [];
                            }
                        }
                        */
                        $scope.validateOptions = data;
                    });
                    
                    $scope.validate = function(options) {
                        ProcessorValidationFactory.validate({}, options).$promise.then(function(data) {
                            // Handle execution error
                            var jobId = data.id;
                            $scope.processModal = false;
                            $state.go('dashboard.job.view', {
                                'jobId' : jobId
                            });
                        });
                    };

                } ]);
