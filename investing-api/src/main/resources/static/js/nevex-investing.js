$(document).ready(function() {

    var TICKER_INPUT = "#ticker-input";
    var TICKER_DATALIST = "#tickers-datalist";
    var TICKER_SYMBOL_ATTR_NAME = "data-ticker-symbol";
    var TICKER_INPUT_MINIMUM_LENGTH = 2;

    var tickerInUse;
    var searchInputInFlight;
    var currentStockPriceChart;
    var currentStockPriceChartData = [];
    var stockPriceChartOptionNames = ["#stock_price_option_seven_days", "#stock_price_option_one_month", "#stock_price_option_three_months", "#stock_price_option_all"]

    init();

    function hideTickerInformation() {
        $("#ticker-info-div").fadeTo(0, 0);
    }

    function showTickerInformation() {
        $("#ticker-info-div").fadeTo(0, 1);
    }

    function hideStockChart() {
        $("#stock-chart-div").fadeTo(0, 0);
    }

    function showStockChart() {
        $("#stock-chart-div").fadeTo(0, 1);
    }

    function showStockPriceOverview() {
        $("#stock-price-overview-div").fadeTo(0, 1);
    }

    function hideStockPriceOverview() {
        $("#stock-price-overview-div").fadeTo(0, 0);
    }

    function isTickerStillInUse(tickerSymbol) {
       return tickerInUse === tickerSymbol;
    }

    function setupCheckBoxListeners() {
        for ( var i = 0; i < stockPriceChartOptionNames.length; i++) {
            $(stockPriceChartOptionNames[i]).change(function () {
                if ( this.checked ) {

                    var checkBoxName = this.getAttribute("data-name");
                    console.log("Checkbox ["+checkBoxName+"] checked");
                    setDataIntoStockPriceChart(checkBoxName);
                }
            })
        }
    }

    function init() {

        hideTickerInformation();
        hideStockChart();
        hideStockPriceOverview();
        setupCheckBoxListeners();
        createChart();

        $(TICKER_INPUT).on('input', function (event) {
            console.log("Input Event: "+event.type +" - "+event.which);

            var val = $(TICKER_INPUT).val();
            var opts = $(TICKER_DATALIST).children();
            for (var i = 0; i < opts.length; i++) {
                var option = opts[i];
                var optionValue = option.value;
                if ( optionValue === val) {

                    var tickerValue = option.getAttribute(TICKER_SYMBOL_ATTR_NAME);

                    console.log("Selected "+optionValue);
                    console.log("Selected ticker data attr "+tickerValue);
                    onTickerSelected(tickerValue, optionValue);
                    break;
                }
            }
        });

        // keep looping on if there's any input to the text box
        setInterval(performSearchForTickerInput, 500);
    }

    function onTickerSelected(tickerSymbol, tickerTitle) {

        // don't search for this ticker anymore
        tickerInUse = tickerSymbol;

        getTickerInformation(tickerSymbol, function (tickerData) {

            if ( ! isTickerStillInUse(tickerSymbol) ) {
                return; // another ticker is selected
            }

            $("#ticker-info-title").text(tickerTitle);
            $("#ticker-info-symbol").text(tickerData.ticker);
            $("#ticker-info-name").text(tickerData.name);
            $("#ticker-info-sector").text(tickerData.sector);
            $("#ticker-info-industry").text(tickerData.industry);
            $("#ticker-info-exchange").text(tickerData.stock_exchange);
            $("#ticker-info-isTradable").text(tickerData.is_tradable ? "Yes" : "No");

            showTickerInformation();

        });

        getCurrentStockPrice(tickerSymbol, function (currentPrice) {
            if ( ! isTickerStillInUse(tickerSymbol) ) {
                return; // another ticker is selected
            }
            if ( !currentPrice ) {
                console.log("Did not get any current price info for stock ["+tickerSymbol+"]");
                return;
            }

            $("#stock-price").text("$"+roundToAlways2Places(currentPrice.close, 2));
            $("#stock-price-low").text("$"+roundToAlways2Places(currentPrice.low, 2));
            $("#stock-price-high").text("$"+roundToAlways2Places(currentPrice.high, 2));
            $("#stock-volume").text(getNiceNumber(currentPrice.volume));

            showStockPriceOverview();
        });

        getHistoricalStockPrices(tickerSymbol, function (pricesData) {

            if ( ! isTickerStillInUse(tickerSymbol) ) {
                return; // another ticker is selected
            }

            if ( ! pricesData || pricesData.length == 0) {
                console.log("No prices data returned for "+tickerSymbol);
                return;
            }

            currentStockPriceChartData = [];

            for ( var i = 0; i < pricesData.length; i++) {
                var chartDataEntry = {
                    date: pricesData[i].date,
                    closePrice: pricesData[i].close
                };
                currentStockPriceChartData.push(chartDataEntry);
            }

            currentStockPriceChartData.sort(function(a,b) {
                if (a.date < b.date) { return 1; }
                if (a.date > b.date) { return -1; }
                return 0;
            });

            setDataIntoStockPriceChart("seven_days");

        });
    }

    function setStockPriceOption(optionToSelect) {
        for ( var i = 0; i < stockPriceChartOptionNames.length; i++ ) {
            var optionName = stockPriceChartOptionNames[i];
            var isChecked = false;
            if ( optionName.indexOf(optionToSelect) !== -1) {
                isChecked = true;
            }
            $(optionName).prop('checked', isChecked);
        }
    }
    
    function setDataIntoStockPriceChart(optionRange) {

        var chartDates = [];
        var chartPrices = [];
        var dataAmountToShow = currentStockPriceChartData.length;
        if ( optionRange == "seven_days") {
            dataAmountToShow = 7;
        } else if ( optionRange == "one_month") {
            dataAmountToShow = 30;
        } else if ( optionRange == "three_months") {
            dataAmountToShow = 90;
        }

        for ( var i = 0; i < dataAmountToShow && i < currentStockPriceChartData.length; i++) {
            chartDates.push(currentStockPriceChartData[i].date);
            chartPrices.push(currentStockPriceChartData[i].closePrice);
        }

        currentStockPriceChart.data.datasets.pop(); // will only be one

        currentStockPriceChart.data.labels = chartDates;
        currentStockPriceChart.data.datasets.push(createChartData(chartPrices));

        currentStockPriceChart.update();
        // var previousWidth;
        // var previousHeight;

        // if ( currentStockPriceChart ) {
        //     // previousWidth = currentStockPriceChart.width;
        //     // previousHeight = currentStockPriceChart.height
        //     // destroy it before add another one
        //     // currentStockPriceChart.destroy();
        // }

        // if ( previousWidth && previousWidth ) {
        //     currentStockPriceChart.width = previousWidth;
        //     currentStockPriceChart.height = previousHeight;
        // }

        setStockPriceOption(optionRange);
        showStockChart();
    }

    
    function performSearchForTickerInput() {
        var tickerInput = $(TICKER_INPUT);

        var selectedText = tickerInput.val();

        if ( searchInputInFlight === selectedText) {
            return; // already searched this
        }

        if ( !selectedText || selectedText.trim().length < TICKER_INPUT_MINIMUM_LENGTH) {
            console.log("Not enough chars ["+selectedText+"]");
            return;
        }

        console.log("Will search for text ["+selectedText+"]");
        searchInputInFlight = selectedText;

        $(TICKER_DATALIST).empty();

        searchForTicker(selectedText, function (data) {
            // console.log("Received search data ["+data+"]");
            if ( ! (selectedText === searchInputInFlight) ) {
                // the search has changed - so don't do anything here
                return;
            }

            if ( ! data ) {
                // no data ....
                return;
            }

            $(TICKER_DATALIST).empty();
            for (var d in data) {
                var tickerSymbol = data[d].ticker;
                var optionText = tickerSymbol + " - " + data[d].name;
                $("<option "+TICKER_SYMBOL_ATTR_NAME+"="+tickerSymbol+"/>").html(optionText).appendTo(TICKER_DATALIST);
            }

            tickerInput.focus();
        });

    }

    function searchForTicker(text, onSearchReturned) {
        $.ajax({
            type: "GET",
            url: "api/search?name="+text,
            success: function(data) {
                onSearchReturned(data);
            },
            error: function(error) {
                onError(error);
            }
        })
    }

    function getTickerInformation(tickerSymbol, onTickerInfoReturned) {
        $.ajax({
            type: "GET",
            url: "api/tickers/"+tickerSymbol,
            success: function(data) {
                onTickerInfoReturned(data);
            },
            error: function(error) {
                onError(error);
            }
        })
    }

    function getCurrentStockPrice(tickerSymbol, onCurrentStockPriceReturned) {
        $.ajax({
            type: "GET",
            url: "api/tickers/"+tickerSymbol+"/prices",
            success: function(data) {
                onCurrentStockPriceReturned(data);
            },
            error: function(error) {
                onError(error);
            }
        })
    }

    function getHistoricalStockPrices(tickerSymbol, onHistoricalPricesReturned) {
        $.ajax({
            type: "GET",
            url: "api/tickers/"+tickerSymbol+"/prices/historical",
            success: function(data) {
                onHistoricalPricesReturned(data);
            },
            error: function(error) {
                onError(error);
            }
        })
    }

    function onError(error) {
        console.log("An error occurred: "+error);
    }

    // https://stackoverflow.com/questions/36734201/how-to-convert-numbers-to-million-in-javascript
    function getNiceNumber(inputNumber)
    {
        var niceNumber;
        var niceNumberPostFix;
        // Nine Zeroes for Billions
        if ( Math.abs(Number(inputNumber)) >= 1.0e+9) {
            niceNumber = Math.abs(Number(inputNumber)) / 1.0e+9;
            niceNumberPostFix = "B";
        } else if (Math.abs(Number(inputNumber)) >= 1.0e+6) {
            niceNumber = Math.abs(Number(inputNumber)) / 1.0e+6;
            niceNumberPostFix = "M";
        } else if (Math.abs(Number(inputNumber)) >= 1.0e+3) {
            niceNumber = Math.abs(Number(inputNumber)) / 1.0e+3;
            niceNumberPostFix = "K";
        } else {
            niceNumber = Math.abs(Number(inputNumber));
        }
        var roundedNumber = round(niceNumber, 2);
        return roundedNumber + niceNumberPostFix;
    }

    // http://www.jacklmoore.com/notes/rounding-in-javascript/
    function round(value, decimals) {
        return Number(Math.round(value+'e'+decimals)+'e-'+decimals);
    }

    function roundToAlways2Places(value) {
        return round(value, 2).toFixed(2);
    }

    function createChartData(chartDates) {
        // labels: chartDates,
        return {
            // label: tickerSymbol,
            data: chartDates,
            fontColor: "#ffffff",
            color: "#ffffff",
            pointRadius: 1, // no points
            pointHitRadius: 3, // see here for more: http://www.chartjs.org/docs/latest/charts/line.html
            borderWidth: 1,

            backgroundColor: [
                'rgba(247,61,40, 0.2)'
            ],
            borderColor: [
                'rgba(247,61,40,1)'
            ]
        };
    }

    function createChart() {
        var dateFormat = "YYYY-MM-DD";
        var ctx = document.getElementById("stock-chart").getContext('2d');
        currentStockPriceChart = new Chart(ctx, {
            type: 'line',
            options: {
                maintainAspectRatio: false,
                responsive: false,
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

