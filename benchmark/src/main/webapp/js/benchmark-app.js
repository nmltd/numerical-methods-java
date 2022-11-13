(function () {
    angular.module("benchmarkApp", ['ngSanitize'])

            .config(function ($locationProvider) {
                $locationProvider
                        .html5Mode({enabled: true, requireBase: false})
                        .hashPrefix('!');
            })

            .controller("TabController", function ($scope) {
                $scope.activeTab = 0;

                $scope.activate = function (indx) {
                    $scope.activeTab = indx;
                };

                $scope.isActive = function (indx) {
                    return $scope.activeTab === indx;
                };
            })

            .controller(
                    "BenchmarkController",
                    ['$scope', '$http', '$location', function ($scope, $http, $location) {
                            $scope.category = $location.search().category || 'linearalgebra';
                            if ($location.search().recordId) {
                                $scope.recordId = $location.search().recordId;
                            }

                            $scope.plots = [
                                {
                                    title: "loading records...",
                                    id: "",
                                    chart: function () {
                                        var plotDiv = document.createElement("div");
                                        plotDiv.innerHTML = "please wait";
                                        return plotDiv;
                                    }
                                }
                            ];

                            $scope.selectRecord = function (value) {
                                console.log('selected ' + value);
                                $scope.recordId = value;
                            };

                            var listRecords = function () {
                                console.log('listing records for category "' + $scope.category + '" ...');
                                var dir = "record/" + $scope.category;
                                var fileExtension = ".csv";
                                // retrieve the contents of the folder if the folder is configured as 'browsable'
                                $http.get(dir).then(
                                        function (response) {
                                            var recordIds = [];
                                            $(response.data)
                                                    .find("a:contains(" + fileExtension + ")")
                                                    .each(function () {
                                                        var recordId
                                                                = this.href.substr(this.href.lastIndexOf("/"))
                                                                .replace("/record-", "")
                                                                .replace(".csv", "");
                                                        recordIds.push(recordId);
                                                    });
                                            recordIds.sort();
                                            recordIds.reverse();

                                            $scope.recordIds = recordIds;

                                            console.log('records: ' + $scope.recordIds);
                                            if (!$scope.recordId || $scope.recordIds.indexOf($scope.recordId) === -1) {
                                                $scope.recordId = $scope.recordIds[0];
                                            }
                                        },
                                        function (response) {
                                            console.log('error listing records: ' + response.status);
                                        }
                                );

                            };

                            var loadNotes = function () {
                                console.log('loading notes (category = ' + $scope.category + '; record-id = ' + $scope.recordId + ') ...');
                                var fileLocation = 'notes/' + $scope.category + '/notes-' + $scope.recordId + '.html';
                                $http.get(fileLocation).then(
                                        function (response) {
                                            $scope.notes = response.data;
                                        },
                                        function (response) {
                                            console.log('error loading notes from ' + fileLocation + ', status = ' + response.status);
                                        }
                                );
                            };

                            var loadRecord = function () {
                                console.log('loading record (category = ' + $scope.category + '; record-id = ' + $scope.recordId + ') ...');
                                // grab the CSV record
                                var fileLocation
                                        = 'record/' + $scope.category + '/record-' + $scope.recordId + '.csv';
                                $http.get(fileLocation).then(
                                        function (response) {
                                            var csvString = response.data;
                                            // transform the CSV string into a 2D array
                                            var arrayData = $.csv.toArrays(
                                                    csvString,
                                                    {
                                                        onParseValue: $.csv.hooks.castToScalar
                                                    }
                                            );

                                            console.log('record loaded: ' + arrayData);
                                            var data = google.visualization.arrayToDataTable(arrayData);
                                            var nGroups = data.getNumberOfRows();

                                            $scope.plots.length = 0; // clear the old plots
                                            for (var i = 0; i < nGroups; ++i) {
                                                var view = new google.visualization.DataView(data);
                                                view.setRows([i]);

                                                var options = {
                                                    width: 870,
                                                    height: 640,
                                                    title: 'Operation Performance',
                                                    bar: {groupWidth: '95%'},
                                                    chartArea: {
                                                        top: 50,
                                                        left: 90,
                                                        width: 580,
                                                        height: 480
                                                    },
                                                    hAxis: {
                                                        textStyle: {
                                                            fontSize: 24
                                                        }
                                                    },
                                                    vAxis: {
                                                        title: 'Relative time used (Lower is better)',
                                                        viewWindow: {max: 30},
                                                        textStyle: {
                                                            fontSize: 20
                                                        }
                                                    },
                                                    legend: {
                                                        position: 'right',
                                                        textStyle: {fontSize: 12}
                                                    }

                                                };

                                                var childDiv = document.createElement('div');

                                                $scope.plots.push({
                                                    title: data.getFormattedValue(i, 0),
                                                    id: angular.lowercase(options.title).replace(/ /g, '_'),
                                                    chart: childDiv
                                                });

                                                var chart = new google.visualization.ColumnChart(childDiv);
                                                chart.draw(view, options);
                                            }

                                            console.log('finished charting');
                                        },
                                        function (response) {
                                            console.log('error loading record: ' + response.status);
                                        });
                            };

                            $scope.$watch('recordId', function (newValue, oldValue) {
                                console.log('change record-id: ' + newValue);
                                if ($scope.recordId) {
                                    loadRecord();
                                    loadNotes();
                                }
                            });

                            $scope.$watch('category', function (newValue, oldValue) {
                                console.log('change category: ' + newValue);
                                listRecords();
                            });

                            $scope.$watchCollection('recordIds', function (newValue, oldValue) {
                                console.log('change recordIds: ' + newValue);
                            });

                        }])

            .directive('benchmarkChart', function () {
                return {
                    restrict: 'E',
                    link: function (scope, element, attrs) {
                        element.replaceWith(scope.plot.chart);
                    }
                };
            })

            .directive('benchmarkNotes', function () {
                return {
                    restrict: 'E',
                    replace: true,
                    templateUrl: 'template/notes.html'
                };
            });

    /*
     * Load Google visualization library, before bootstrapping angular apps.
     */
    console.log('loading google visualization library ...');
    google.load('visualization', '1', {packages: ['corechart', 'bar']});
    google.setOnLoadCallback(function () {

        angular.bootstrap(document, ['benchmarkApp']);

    });

})();

