'use strict';
angular
		.module('marbleCoreApp')
		.controller(
				'ChartListCtrl',
				function($scope, $compile, $state, ChartListSearchByTopicNameFactory) {

					$scope.tableState = {};
					$scope.tableState.name = ".*";
					$scope.tableState.size = "10";
					$scope.tableState.page = "0";
					$scope.tableState.sort = "";

					$scope.gridOptions = {
						data : 'gridData',
						rowHeight : 40,
						enableFiltering : true,
						useExternalFiltering : true,
						paginationPageSizes : [ 10, 25, 50, 100 ],
						paginationPageSize : 10,
						useExternalPagination : true,
						useExternalSorting : true,
						columnDefs : [
								{
									field : 'topicName',
									displayName : 'Topic',
									cellTemplate : '<div class="grid-action-cell"><a ui-sref="dashboard.topic.view({topicName: row.entity.topicName})" class="btn btn-default"><i class="fa fa-tags fa-fw"></i><span class="hidden-xs hidden-sm"> {{row.entity.topicName}}</span></a></div>'
								},
								{
									field : 'name',
									displayName : 'Name',
									enableFiltering : false,
									cellTooltip : true
								},
								{
									field : 'description',
									displayName : 'Description',
									enableFiltering : false,
									cellTooltip : true
								},
								{
									field : 'createdAt',
									displayName : 'Created At',
									enableFiltering : false,
									cellFilter : "date:'yyyy-MM-dd HH:mm:ss (Z)'",
									cellTooltip : true
								},
								{
									name : 'actions',
									displayName : 'Actions',
									enableFiltering : false,
									cellTemplate : '<div class="grid-action-cell"><a ui-sref="dashboard.chart.view({chartId: row.entity.id})" class="btn btn-default"><i class="fa fa-info-circle"></i><span class="hidden-xs hidden-sm"> Details</span></a></div>'
								} ],
						onRegisterApi : function(gridApi) {
							$scope.gridApi = gridApi;
							// console.log($scope.gridApi);
							$scope.gridApi.core.on.filterChanged($scope, function() {
								var grid = this.grid;

								var nameRegex = grid.columns[0].filters[0].term;
								if (typeof nameRegex === 'undefined' || nameRegex == null) {
									nameRegex = '.*';
								}
								$scope.tableState.name = nameRegex;
								updateTable($scope);

							});
							$scope.gridApi.core.on.sortChanged($scope,
									function(grid, sortColumns, pageSize) {
										if (sortColumns.length == 0) {
											$scope.tableState.sort = null;
										} else {
											$scope.tableState.sort = sortColumns[0].field + ","
													+ sortColumns[0].sort.direction;
										}
										updateTable($scope);
									});
							$scope.gridApi.pagination.on.paginationChanged($scope, function(newPage, pageSize) {
								$scope.tableState.size = pageSize;
								$scope.tableState.page = newPage - 1;
								$scope.gridOptions.minRowsToShow = pageSize;
								$scope.gridOptions.virtualizationThreshold = pageSize;
								updateTable($scope);
							});
						}
					};

					var updateTable = function($scope) {
						var chartsList = ChartListSearchByTopicNameFactory.searchByName({
							name : $scope.tableState.name,
							size : $scope.tableState.size,
							page : $scope.tableState.page,
							sort : $scope.tableState.sort,
						});
						chartsList.$promise.then(function(data) {
							$scope.gridData = data._embedded.charts;
							$scope.gridOptions.totalItems = data.page.totalElements;
						});
					};

					updateTable($scope);

				});
