
-- New approach on how to weigh certain analyzers

delete from investing.analyzer_weights_v2;

insert into investing.analyzer_weights_v2("version", "name", "center", "lowest", "highest", "invert_lowest", "invert_highest", "sign_direction")
values
-- Fundamentals
(1, 'price-to-earnings-ratio', 0, -10, 40, false, true, 1), -- We want 0+ to start off at the higher scale (hence we invert the high range)
(1, 'earnings-per-share', 0, -10, 40, false, false, 1),

-- Summary of analyzers
(1, 'analyzer-summary-counter-adjust-weight', 20, -10, 50000, false, false, 1),

----------------------------------------------------------
--- 1 Day analyzers
----------------------------------------------------------
(1, 'current-stock-price-compared-to-1-day-previous-close-average-percent-deviation', 0, -0.3, 0.3, false, false, -1), -- invert the sign bit, lower values are better
(1, 'current-stock-price-compared-to-1-day-previous-high-average-percent-deviation', 0, -0.5, 0.5, false, false, -1),
(1, 'current-stock-price-compared-to-1-day-previous-low-average-percent-deviation', 0, -0.3, 0.3, false, false, -1),
(1, 'current-stock-price-compared-to-1-day-previous-lowest-percent-deviation', 0, -0.3, 0.3, false, false, -1),
(1, 'current-stock-price-compared-to-1-day-previous-highest-percent-deviation', 0, -0.5, 0.5, false, false, -1),
(1, 'current-lowest-stock-price-compared-to-1-day-previous-lowest-percent-deviation', 0, -0.2, 0.2, false, false, -1),
(1, 'current-lowest-stock-price-compared-to-1-day-previous-low-average-percent-deviation', 0, -0.2, 0.2, false, false, -1),
(1, 'current-stock-vol-compared-to-1-day-previous-vol-average-percent-deviation', 0, -0.4, 0.4, false, false, -1),
(1, 'current-stock-vol-compared-to-1-day-previous-vol-high-percent-deviation', 0, -0.6, 0.6, false, false, -1),
(1, 'current-stock-vol-compared-to-1-day-previous-vol-low-percent-deviation', 0, -0.6, 0.6, false, false, -1),

----------------------------------------------------------
--- 7 Day analyzers
----------------------------------------------------------
(1, 'current-stock-price-compared-to-7-days-previous-close-average-percent-deviation', 0, -0.3, 0.3, false, false, -1), -- invert the sign bit, lower values are better
(1, 'current-stock-price-compared-to-7-days-previous-high-average-percent-deviation', 0, -0.5, 0.5, false, false, -1),
(1, 'current-stock-price-compared-to-7-days-previous-low-average-percent-deviation', 0, -0.3, 0.3, false, false, -1),
(1, 'current-stock-price-compared-to-7-days-previous-lowest-percent-deviation', 0, -0.3, 0.3, false, false, -1),
(1, 'current-stock-price-compared-to-7-days-previous-highest-percent-deviation', 0, -0.5, 0.5, false, false, -1),
(1, 'current-lowest-stock-price-compared-to-7-days-previous-lowest-percent-deviation', 0, -0.2, 0.2, false, false, -1),
(1, 'current-lowest-stock-price-compared-to-7-days-previous-low-average-percent-deviation', 0, -0.2, 0.2, false, false, -1),
(1, 'current-stock-vol-compared-to-7-days-previous-vol-average-percent-deviation', 0, -0.5, 0.5, false, false, -1),
(1, 'current-stock-vol-compared-to-7-days-previous-vol-high-percent-deviation', 0, -0.6, 0.6, false, false, -1),
(1, 'current-stock-vol-compared-to-7-days-previous-vol-low-percent-deviation', 0, -0.6, 0.6, false, false, -1),
(1, '7-days-close-price-simple-regression-r', 0, -0.4, 1.0, false, false, 1),
(1, '7-days-close-price-simple-regression-slope', 0, -100, 100, false, false, 1),
(1, '7-days-volume-simple-regression-r', 0, -0.6, 1.0, false, false, 1),
(1, '7-days-volume-simple-regression-slope', 0, -10000, 10000, false, false, 1),

----------------------------------------------------------
--- 1 month analyzers
----------------------------------------------------------
(1, 'current-stock-price-compared-to-1-month-previous-close-average-percent-deviation', 0, -0.5, 0.5, false, false, -1), -- invert the sign bit, lower values are better
(1, 'current-stock-price-compared-to-1-month-previous-high-average-percent-deviation', 0, -0.6, 0.6, false, false, -1),
(1, 'current-stock-price-compared-to-1-month-previous-low-average-percent-deviation', 0, -0.4, 0.4, false, false, -1),
(1, 'current-stock-price-compared-to-1-month-previous-lowest-percent-deviation', 0, -0.4, 0.4, false, false, -1),
(1, 'current-stock-price-compared-to-1-month-previous-highest-percent-deviation', 0, -0.6, 0.6, false, false, -1),
(1, 'current-lowest-stock-price-compared-to-1-month-previous-lowest-percent-deviation', 0, -0.4, 0.4, false, false, -1),
(1, 'current-lowest-stock-price-compared-to-1-month-previous-low-average-percent-deviation', 0, -0.4, 0.4, false, false, -1),
(1, 'current-stock-vol-compared-to-1-month-previous-vol-average-percent-deviation', 0, -0.5, 0.5, false, false, -1),
(1, 'current-stock-vol-compared-to-1-month-previous-vol-high-percent-deviation', 0, -0.6, 0.6, false, false, -1),
(1, 'current-stock-vol-compared-to-1-month-previous-vol-low-percent-deviation', 0, -0.6, 0.6, false, false, -1),
(1, '1-month-close-price-simple-regression-r', 0, -0.6, 1.0, false, false, 1),
(1, '1-month-close-price-simple-regression-slope', 0, -100, 100, false, false, 1),
(1, '1-month-volume-simple-regression-r', 0, -0.6, 1.0, false, false, 1),
(1, '1-month-volume-simple-regression-slope', 0, -10000, 10000, false, false, 1),

----------------------------------------------------------
--- 3 months analyzers
----------------------------------------------------------
(1, 'current-stock-price-compared-to-3-months-previous-close-average-percent-deviation', 0, -0.6, 0.6, false, false, -1), -- invert the sign bit, lower values are better
(1, 'current-stock-price-compared-to-3-months-previous-high-average-percent-deviation', 0, -0.6, 0.6, false, false, -1),
(1, 'current-stock-price-compared-to-3-months-previous-low-average-percent-deviation', 0, -0.7, 0.7, false, false, -1),
(1, 'current-stock-price-compared-to-3-months-previous-lowest-percent-deviation', 0, -0.8, 0.6, false, false, -1),
(1, 'current-stock-price-compared-to-3-months-previous-highest-percent-deviation', 0, -0.4, 0.4, false, false, -1),
(1, 'current-lowest-stock-price-compared-to-3-months-previous-lowest-percent-deviation', 0, -0.7, 0.7, false, false, -1),
(1, 'current-lowest-stock-price-compared-to-3-months-previous-low-average-percent-deviation', 0, -0.6, 0.6, false, false, -1),
(1, 'current-stock-vol-compared-to-3-months-previous-vol-average-percent-deviation', 0, -0.7, 0.7, false, false, -1),
(1, 'current-stock-vol-compared-to-3-months-previous-vol-high-percent-deviation', 0, -0.8, 0.8, false, false, -1),
(1, 'current-stock-vol-compared-to-3-months-previous-vol-low-percent-deviation', 0, -0.8, 0.8, false, false, -1),
(1, '3-months-close-price-simple-regression-r', 0, -1.0, 1.0, false, false, 1),
(1, '3-months-close-price-simple-regression-slope', 0, -100, 100, false, false, 1),
(1, '3-months-volume-simple-regression-r', 0, -0.6, 0.6, false, false, 1),
(1, '3-months-volume-simple-regression-slope', 0, -10000, 10000, false, false, 1),

----------------------------------------------------------
--- 6 months analyzers
----------------------------------------------------------
(1, 'current-stock-price-compared-to-6-months-previous-close-average-percent-deviation', 0, -1.0, 1.0, false, false, -1), -- invert the sign bit, lower values are better
(1, 'current-stock-price-compared-to-6-months-previous-high-average-percent-deviation', 0, -1.0, 1.0, false, false, -1),
(1, 'current-stock-price-compared-to-6-months-previous-low-average-percent-deviation', 0, -1.0, 1.0, false, false, -1),
(1, 'current-stock-price-compared-to-6-months-previous-lowest-percent-deviation', 0, -1.0, 1.0, false, false, -1),
(1, 'current-stock-price-compared-to-6-months-previous-highest-percent-deviation', 0, -1.0, 1.0, false, false, -1),
(1, 'current-lowest-stock-price-compared-to-6-months-previous-lowest-percent-deviation', 0, -1.0, 1.0, false, false, -1),
(1, 'current-lowest-stock-price-compared-to-6-months-previous-low-average-percent-deviation', 0, -1.0, 1.0, false, false, -1),
(1, 'current-stock-vol-compared-to-6-months-previous-vol-average-percent-deviation', 0, -1.0, 1.0, false, false, -1),
(1, 'current-stock-vol-compared-to-6-months-previous-vol-high-percent-deviation', 0, -1.0, 1.0, false, false, -1),
(1, 'current-stock-vol-compared-to-6-months-previous-vol-low-percent-deviation', 0, -1.0, 1.0, false, false, -1),
(1, '6-months-close-price-simple-regression-r', 0, -1.0, 1.0, false, false, 1),
(1, '6-months-close-price-simple-regression-slope', 0, -100, 100, false, false, 1),
(1, '6-months-volume-simple-regression-r', 0, -1.0, 1.0, false, false, 1),
(1, '6-months-volume-simple-regression-slope', 0, -10000, 10000, false, false, 1),

----------------------------------------------------------
--- 1 year analyzers
----------------------------------------------------------
(1, 'current-stock-price-compared-to-1-year-previous-close-average-percent-deviation', 0, -2.0, 2.0, false, false, -1), -- invert the sign bit, lower values are better
(1, 'current-stock-price-compared-to-1-year-previous-high-average-percent-deviation', 0, -2.0, 2.0, false, false, -1),
(1, 'current-stock-price-compared-to-1-year-previous-low-average-percent-deviation', 0, -2.0, 2.0, false, false, -1),
(1, 'current-stock-price-compared-to-1-year-previous-lowest-percent-deviation', 0, -2.0, 2.0, false, false, -1),
(1, 'current-stock-price-compared-to-1-year-previous-highest-percent-deviation', 0, -2.0, 2.0, false, false, -1),
(1, 'current-lowest-stock-price-compared-to-1-year-previous-lowest-percent-deviation', 0, -2.0, 2.0, false, false, -1),
(1, 'current-lowest-stock-price-compared-to-1-year-previous-low-average-percent-deviation', 0, -2.0, 2.0, false, false, -1),
(1, 'current-stock-vol-compared-to-1-year-previous-vol-average-percent-deviation', 0, -2.0, 2.0, false, false, -1),
(1, 'current-stock-vol-compared-to-1-year-previous-vol-high-percent-deviation', 0, -2.0, 2.0, false, false, -1),
(1, 'current-stock-vol-compared-to-1-year-previous-vol-low-percent-deviation', 0, -2.0, 2.0, false, false, -1),
(1, '1-year-close-price-simple-regression-r', 0, -2.0, 2.0, false, false, 1),
(1, '1-year-close-price-simple-regression-slope', 0, -1000, 1000, false, false, 1),
(1, '1-year-volume-simple-regression-r', 0, -2.0, 2.0, false, false, 1),
(1, '1-year-volume-simple-regression-slope', 0, -10000, 10000, false, false, 1),


----------------------------------------------------------
--- 3 years analyzers
----------------------------------------------------------
(1, 'current-stock-price-compared-to-3-years-previous-close-average-percent-deviation', 0, -3.0, 3.0, false, false, -1), -- invert the sign bit, lower values are better
(1, 'current-stock-price-compared-to-3-years-previous-high-average-percent-deviation', 0, -3.0, 3.0, false, false, -1),
(1, 'current-stock-price-compared-to-3-years-previous-low-average-percent-deviation', 0, -3.0, 3.0, false, false, -1),
(1, 'current-stock-price-compared-to-3-years-previous-lowest-percent-deviation', 0, -3.0, 3.0, false, false, -1),
(1, 'current-stock-price-compared-to-3-years-previous-highest-percent-deviation', 0, -3.0, 3.0, false, false, -1),
(1, 'current-lowest-stock-price-compared-to-3-years-previous-lowest-percent-deviation', 0, -3.0, 3.0, false, false, -1),
(1, 'current-lowest-stock-price-compared-to-3-years-previous-low-average-percent-deviation', 0, -3.0, 3.0, false, false, -1),
(1, 'current-stock-vol-compared-to-3-years-previous-vol-average-percent-deviation', 0, -3.0, 3.0, false, false, -1),
(1, 'current-stock-vol-compared-to-3-years-previous-vol-high-percent-deviation', 0, -3.0, 3.0, false, false, -1),
(1, 'current-stock-vol-compared-to-3-years-previous-vol-low-percent-deviation', 0, -3.0, 3.0, false, false, -1),
(1, '3-years-close-price-simple-regression-r', 0, -3.0, 3.0, false, false, 1),
(1, '3-years-close-price-simple-regression-slope', 0, -10000, 10000, false, false, 1),
(1, '3-years-volume-simple-regression-r', 0, -3.0, 3.0, false, false, 1),
(1, '3-years-volume-simple-regression-slope', 0, -10000, 10000, false, false, 1),

----------------------------------------------------------
--- 5 years analyzers
----------------------------------------------------------
(1, 'current-stock-price-compared-to-5-years-previous-close-average-percent-deviation', 0, -4.0, 4.0, false, false, -1), -- invert the sign bit, lower values are better
(1, 'current-stock-price-compared-to-5-years-previous-high-average-percent-deviation', 0, -4.0, 4.0, false, false, -1),
(1, 'current-stock-price-compared-to-5-years-previous-low-average-percent-deviation', 0, -4.0, 4.0, false, false, -1),
(1, 'current-stock-price-compared-to-5-years-previous-lowest-percent-deviation', 0, -4.0, 4.0, false, false, -1),
(1, 'current-stock-price-compared-to-5-years-previous-highest-percent-deviation', 0, -4.0, 4.0, false, false, -1),
(1, 'current-lowest-stock-price-compared-to-5-years-previous-lowest-percent-deviation', 0, -4.0, 4.0, false, false, -1),
(1, 'current-lowest-stock-price-compared-to-5-years-previous-low-average-percent-deviation', 0, -4.0, 4.0, false, false, -1),
(1, 'current-stock-vol-compared-to-5-years-previous-vol-average-percent-deviation', 0, -4.0, 4.0, false, false, -1),
(1, 'current-stock-vol-compared-to-5-years-previous-vol-high-percent-deviation', 0, -4.0, 4.0, false, false, -1),
(1, 'current-stock-vol-compared-to-5-years-previous-vol-low-percent-deviation', 0, -4.0, 4.0, false, false, -1),
(1, '5-years-close-price-simple-regression-r', 0, -4.0, 4.0, false, false, 1),
(1, '5-years-close-price-simple-regression-slope', 0, -100000, 100000, false, false, 1),
(1, '5-years-volume-simple-regression-r', 0, -4.0, 4.0, false, false, 1),
(1, '5-years-volume-simple-regression-slope', 0, -100000, 100000, false, false, 1);


select * from investing.analyzer_weights_v2;
