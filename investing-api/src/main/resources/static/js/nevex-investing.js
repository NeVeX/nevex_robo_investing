$(document).ready(function() {

    var TICKER_INPUT = "#ticker-input";
    var TICKER_DATALIST = "#tickers-datalist";
    var TICKER_SYMBOL_ATTR_NAME = "data-ticker-symbol";
    var TICKER_INPUT_MINIMUM_LENGTH = 2;

    var tickerInUse;
    var searchInputInFlight;
    init();

    function hideTickerInformation() {
        $("#ticker-info-div").fadeTo(0, 0);
    }

    function showTickerInformation() {
        $("#ticker-info-div").fadeTo(0, 1);
    }

    function init() {

        hideTickerInformation();

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

            if ( ! (tickerInUse === tickerSymbol)) {
                return; // another ticker is selected
            }

            $("#ticker-info-title").text(tickerTitle);
            $("#ticker-info-symbol").text(tickerData.ticker);
            $("#ticker-info-name").text(tickerData.name);
            $("#ticker-info-sector").text(tickerData.sector);
            $("#ticker-info-industry").text(tickerData.industry);
            $("#ticker-info-exchange").text(tickerData.stock_exchange);
            $("#ticker-info-isTradable").text(tickerData.is_tradable == "true" ? "Yes" : "No");

            showTickerInformation();
        });


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

    function onError(error) {
        console.log("An error occurred: "+error);
    }

});

