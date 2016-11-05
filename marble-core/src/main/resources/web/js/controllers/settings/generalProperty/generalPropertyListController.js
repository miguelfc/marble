'use strict';
angular.module('marbleCoreApp')
.controller('GeneralPropertyListCtrl', ['$scope', '$compile', '$state', '$timeout', 'GeneralPropertiesSearchByNameFactory',
function ($scope, $compile, $state, $timeout, GeneralPropertiesSearchByNameFactory) {

	$scope.tableState = {};
	$scope.tableState.name = ".*";
	$scope.tableState.size = "10";
	$scope.tableState.page = "0";
	$scope.tableState.sort = "";

	$scope.edit = function(generalPropertyName) {
		$state.go('dashboard.settings.generalProperty.view', {
			'generalPropertyName' : generalPropertyName
		});
	};

	$scope.create = function() {
		$state.go('dashboard.settings.generalProperty.create');
	};

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
					field : 'name',
					displayName : 'Name'
				},
				{
					field : 'value',
					displayName : 'Value',
					enableFiltering : false
				},
				{
					name : 'actions',
					displayName : 'Actions',
					enableFiltering : false,
					cellTemplate : '<div class="grid-action-cell"><a data-ng-click="grid.appScope.edit(row.entity.name)" class="btn btn-default"><i class="fa fa-info-circle"></i><span class="hidden-xs hidden-sm"> Details</span></a></div>'
				} ],
		onRegisterApi : function(gridApi) {
			$scope.gridApi = gridApi;
			$scope.gridApi.core.on.filterChanged($scope, function() {
				var grid = this.grid;

				var nameRegex = grid.columns[0].filters[0].term;
				if (typeof nameRegex === 'undefined' || nameRegex == null) {
					nameRegex = '.*';
				}
				$scope.tableState.name = nameRegex;
				updateTable($scope);

			});
			$scope.gridApi.core.on.sortChanged($scope, function(grid,
					sortColumns, pageSize) {
				if (sortColumns.length == 0) {
					$scope.tableState.sort = null;
				} else {
					$scope.tableState.sort = sortColumns[0].field + ","
							+ sortColumns[0].sort.direction;
				}
				updateTable($scope);
			});
			$scope.gridApi.pagination.on.paginationChanged($scope, function(
					newPage, pageSize) {
				$scope.tableState.size = pageSize;
				$scope.tableState.page = newPage - 1;
				updateTable($scope);
			});
		}
	};

	var updateTable = function($scope) {
		var topicsList = GeneralPropertiesSearchByNameFactory.searchByName({
			name : $scope.tableState.name,
			size : $scope.tableState.size,
			page : $scope.tableState.page,
			sort : $scope.tableState.sort,
		});
		topicsList.$promise.then(function(data) {
			$scope.gridData = data._embedded.generalProperties;
			$scope.gridOptions.totalItems = data.page.totalElements;
		});
	};

	updateTable($scope);
}]);
