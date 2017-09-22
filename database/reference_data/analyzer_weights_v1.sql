

SET search_path TO investing;
-- delete from analyzer_weights_v1;

insert into analyzer_weights_v1("name", "start", "end", "weight")
values
('price-to-earnings-ratio', -10000, -30, -1.0),
('price-to-earnings-ratio', -30, -10, -0.6),
('price-to-earnings-ratio', -10, 0.001, -0.3),
('price-to-earnings-ratio', 0.001, 0.01, 0),
('price-to-earnings-ratio', 0.01, 5, 0.8),
('price-to-earnings-ratio', 5, 10, 0.7),
('price-to-earnings-ratio', 10, 20, 0.6),
('price-to-earnings-ratio', 20, 30, 0.5),
('price-to-earnings-ratio', 30, 50, 0.4),
('price-to-earnings-ratio', 50, 10000, 0.1),

('analyzer-summary-counter-adjust-weight', 0, 5, 0.05),
('analyzer-summary-counter-adjust-weight', 5, 10, 0.1),
('analyzer-summary-counter-adjust-weight', 10, 20, 0.15),
('analyzer-summary-counter-adjust-weight', 20, 50, 0.2),
('analyzer-summary-counter-adjust-weight', 50, 100000, 0.22);


select * from analyzer_weights_v1;

