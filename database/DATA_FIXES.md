
#### September 03 2017

Fixed the ticker symbols - had a caret (^) in the symbol.

```
update investing.tickers
set symbol = replace(symbol, '^', '-')
where symbol like '%^%'
```


