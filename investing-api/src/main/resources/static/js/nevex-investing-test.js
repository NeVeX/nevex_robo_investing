$(document).ready(function() {

    var currentStockPriceChart;

    init();

    function init() {

        createTestChart();
    }

    function createTestChart() {
        if ( currentStockPriceChart ) {
            // destroy it
            currentStockPriceChart.destroy();
        }

        var ctx = document.getElementById("stock-chart").getContext('2d');

        var chartDates = ["2017-10-23", "2017-10-22", "2017-10-21", "2017-10-20", "2017-10-19", "2017-10-18", "2017-10-17", "2017-10-16", "2017-10-15", "2017-10-14",
            "2017-10-12", "2017-10-11", "2017-10-10", "2017-10-09", "2017-10-08", "2017-10-07", "2017-10-06", "2017-10-05", "2017-10-04", "2017-10-03", "2017-10-02",
            "2017-10-01", "2017-09-30", "2017-09-29", "2017-09-28", "2017-09-27", "2017-09-26", "2017-09-25", "2017-09-24", "2017-09-23", "2017-09-22", "2017-09-21"];
        var chartPrices = [10, 11, 12 ,13, 14, 15, 16, 17, 18, 14, 12, 20, 23, 23, 23, 23, 22, 21, 23, 17, 19, 23, 22, 23, 22, 21, 20, 19, 15, 20, 22, 20];

        var dateFormat = "YYYY-MM-DD";

        currentStockPriceChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: chartDates,
                datasets: [{
                    label: "test-ticker",
                    data: chartPrices,
                    fontColor: "#ffffff",
                    color: "#ffffff",
                    borderWidth: 1,
                    pointRadius: 1, // no points
                    pointHitRadius: 2, // see here for more: http://www.chartjs.org/docs/latest/charts/line.html
                    backgroundColor: [
                        'rgba(247,61,40, 0.2)'
                    ],
                    borderColor: [
                        'rgba(247,61,40,1)'
                    ]
                }]
            },
            options: {
                maintainAspectRatio: false,
                responsive: true,
                // title:{
                //     text: "MSFT"
                // },
                scales: {
                    xAxes: [{
                        type: "time",
                        ticks: {
                            // minRotation: 90,
                            autoSkip: true,
                            maxTicksLimit: 15,
                            fontColor : "#ffffff",
                            format: dateFormat
                            // callback: function(value) {
                            //     console.log(value);
                            //     return "12324"; //new Date(value).toLocaleDateString('de-DE', {month:'short', year:'numeric'});
                            // }
                        },
                        gridLines: {
                            display: false,
                            color: "#ffffff"
                        },
                        time: {
                            format: dateFormat,
                            unit: 'day',
                            // tooltipFormat: 'll HH:mm'
                            displayFormats: {
                                'day': dateFormat
                            }
                        },
                        scaleLabel: {
                            display: false,
                            format: dateFormat
                        }
                    } ],
                    yAxes: [{
                        ticks: {
                            fontColor : "#ffffff"
                        },
                        gridLines: {
                            display: true,
                            color: "#ffffff",
                            lineWidth: 0.2
                        },
                        scaleLabel: {
                            display: true,
                            labelString: 'Price (USD)',
                            fontColor: "#ffffff"
                        }
                    }]
                },
                legend: {
                    display: false,

                    labels: {
                        fontColor: '#ffffff'
                    }
                }
            }
        });
    }



});

