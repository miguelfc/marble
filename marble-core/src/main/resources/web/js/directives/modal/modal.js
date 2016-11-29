'use strict';

angular
		.module('marbleCoreApp')
		.directive(
				'appModal',
				function() {
					return {
						restrict : 'E',
						scope : {
							modalTitle : '@',
							modalType : '@',
							modalText : '@',
							modalButton : '@',
							modalIcon : '@',
							modalOptions : '=',
							modalAction : '&'
						},
						controller : function($scope, $uibModal) {
							$scope.openModal = function() {
								var modalInstance = $uibModal.open({
									resolve : {
										modalProperties : function() {
											var modalProperties = {};
											modalProperties.title = $scope.modalTitle;
											modalProperties.type = $scope.modalType;
											modalProperties.text = $scope.modalText;
											modalProperties.button = $scope.modalButton;
											modalProperties.modalIcon = $scope.modalIcon;
											modalProperties.modalOptions = $scope.modalOptions;
											return modalProperties;
										},
										modalAction : function() {
											return $scope.modalAction;
										}
									},
									templateUrl : 'templates/directives/modal/modal.html',
									controller : function($scope, $uibModalInstance, modalProperties, modalAction) {
										$scope.modalProperties = modalProperties;
										$scope.modalAction = modalAction;

										if ($scope.modalProperties.type == 'plot') {
											$scope.plot = {};
                                            $scope.plotterRecipe = JSON.stringify($scope.modalProperties.modalOptions, undefined, 2);
                                            $scope.plotAction = function() {
                                                var options = $scope.plotterRecipe;
                                                $scope.modalAction({
                                                    options : options
                                                });
                                            }
										}
										else if ($scope.modalProperties.type == 'process') {
                                            $scope.process = {};
                                            $scope.processRecipe = JSON.stringify($scope.modalProperties.modalOptions, undefined, 2);
                                            $scope.processAction = function() {
                                                var options = $scope.processRecipe;
                                                $scope.modalAction({
                                                    options : options
                                                });
                                            }
                                        }

										$scope.ok = function() {
											if (!$scope.modalProperties.type) {
												$scope.modalAction();
											}
											$uibModalInstance.close('Success');
										};

										$scope.cancel = function() {
											$uibModalInstance.dismiss('Dismissed');
										}
									}
								});

								modalInstance.result.then(function(success) {
									// alert(success);
								}, function(error) {
									// alert(error);
								});
							}
						},
						template : '<button class="btn btn-default btn-block" ng-click="openModal()"><i class="fa fa-fw {{modalIcon}}"></i> {{modalButton}}</button>'
					};
				});
