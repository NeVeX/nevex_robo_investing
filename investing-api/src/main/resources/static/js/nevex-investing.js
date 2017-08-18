$(document).ready(function() {

    var TICKER_INPUT = "#ticker-input";
    var TICKER_DATALIST = "#tickers-datalist";
    
    var searchInFlight;
    init();

    function init() {
        
        // $(TICKER_INPUT).on('keyup', onTickerInputKeyUp);

        $(TICKER_DATALIST).on('click change', function (event) {
            console.log(event.type + " -- " + event.which);
        });

        $(TICKER_INPUT).on('keyup', function (event) {
            console.log(event.type + " -- "+event.which);
            if ( event.which == 13 || event.which == 1) { // enter key or mouse left click
                // We are selecting something, so don't search anymore
                var val = this.value;

                var optionSelected = $(TICKER_DATALIST+" option").filter(function(){
                    return this.value === val;
                });

                if ( optionSelected && optionSelected.length > 0 ) {
                    onTickerSelected(optionSelected[0].getAttribute("data-ticker-symbol"));
                }
            } else {
                // search!
                onTickerInput(this);
            }

        });
    }

    function onTickerSelected(tickerSymbol) {
        console.log("Ticker "+tickerSymbol);
    }
    
    function onTickerInput(tickerInput) {

        var selectedText = tickerInput.value;
        if ( !selectedText || selectedText.trim().length < 3) {
            console.log("Not enough chars ["+selectedText+"]");
            return;
        }
        console.log("Will search for text ["+selectedText+"]");
        searchInFlight = selectedText;

        $(TICKER_DATALIST).empty();

        searchForTicker(selectedText, function (data) {
            console.log("Received search data ["+data+"]");
            if ( ! (selectedText === searchInFlight) ) {
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
                $("<option data-ticker-symbol='+tickerSymbol+'/>").html(optionText).appendTo(TICKER_DATALIST);
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

