$(document).ready(function() {

    var TICKER_INPUT = "#ticker-input";
    var TICKER_DATALIST = "#tickers-datalist";
    var TICKER_SYMBOL_ATTR_NAME = "data-ticker-symbol";
    var TICKER_INPUT_MINIMUM_LENGTH = 2;

    var searchTextInFlight;
    init();

    function init() {
        
        // $(TICKER_INPUT).on('keyup', onTickerInputKeyUp);

        $(TICKER_INPUT).on('input', function (event) {
            console.log("Input Event: "+event.type +" - "+event.which);

            var val = $(TICKER_INPUT).val();
            var opts = $(TICKER_DATALIST).children();
            for (var i = 0; i < opts.length; i++) {
                var option = opts[i];
                var optionValue = option.value;
                if ( optionValue === val) {

                    // don't search for this ticker anymore
                    searchTextInFlight = optionValue;

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


        // $(TICKER_DATALIST).on('click change', function (event) {
        //     console.log(event.type + " -- " + event.which);
        // });
        //
        // $(TICKER_INPUT).on('keyup', function (event) {
        //     console.log("KeyUp Event: "+event.type +" - "+event.which);
        //     onTickerInput(this);
        //     // if ( event.which == 13) { // enter key
        //     //     // We are selecting something, so don't search anymore
        //     //     var val = this.value;
        //     //
        //     //     var optionSelected = $(TICKER_DATALIST+" option").filter(function(){
        //     //         return this.value === val;
        //     //     });
        //     //
        //     //     if ( optionSelected && optionSelected.length > 0 ) {
        //     //         onTickerSelected(optionSelected[0].getAttribute("data-ticker-symbol"));
        //     //     }
        //     // } else {
        //     //     // search!
        //     //     onTickerInput(this);
        //     // }
        //
        // });
    }

    function onTickerSelected(tickerSymbol, tickerTitle) {
        //console.log("Ticker "+tickerSymbol);
        $("#ticker-title").text(tickerTitle);
    }
    
    function performSearchForTickerInput() {
        var tickerInput = $(TICKER_INPUT);

        var selectedText = tickerInput.val();

        if ( searchTextInFlight === selectedText) {
            return; // already searched this
        }

        if ( !selectedText || selectedText.trim().length < TICKER_INPUT_MINIMUM_LENGTH) {
            console.log("Not enough chars ["+selectedText+"]");
            return;
        }

        console.log("Will search for text ["+selectedText+"]");
        searchTextInFlight = selectedText;

        $(TICKER_DATALIST).empty();

        searchForTicker(selectedText, function (data) {
            // console.log("Received search data ["+data+"]");
            if ( ! (selectedText === searchTextInFlight) ) {
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

    function onError(error) {
        console.log("An error occurred: "+error);
    }

});

