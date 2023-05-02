--building types
INSERT INTO building_type(name) VALUES ('театр'); --5
INSERT INTO building_type(name) VALUES ('концертная площадь'); --6
INSERT INTO building_type(name) VALUES ('эстрада'); --7
INSERT INTO building_type(name) VALUES ('дворец культуры'); --8

--buildings
INSERT INTO building(name, type_id) VALUES ('театр победы', 5);
INSERT INTO building(name, type_id) VALUES ('театр поражения', 5);
INSERT INTO building(name, type_id) VALUES ('у гк', 8);
INSERT INTO building(name, type_id) VALUES ('у моря', 6);
INSERT INTO building(name, type_id) VALUES ('юрмальская', 7);

--theaters
INSERT INTO theater(id, capacity) VALUES (7, 200);
INSERT INTO theater(id, capacity) VALUES (8, 100);

--performance venues
INSERT INTO performance_venue(id, area) VALUES (10, 400);

--estrades
INSERT INTO estrade(id, scene_height_centimeters) VALUES (11, 1.5);

--palaces of culture
INSERT INTO palace_of_culture(id, floor_num) VALUES (9, 2);

--genres
INSERT INTO genre(name) VALUES ('кантри');
INSERT INTO genre(name) VALUES ('блюз');
INSERT INTO genre(name) VALUES ('джаз');

--artists
INSERT INTO artist(name, surname) VALUES ('Петр', 'Кабанов');
INSERT INTO artist(name, surname) VALUES ('Анна', 'Рыжая');
INSERT INTO artist(name, surname) VALUES ('Данил', 'Куляев');
INSERT INTO artist(name, surname) VALUES ('Альберт', 'Давыдов');

--artists to genres
INSERT INTO artist_to_genre(artist_id, genre_id) VALUES (1, 1);
INSERT INTO artist_to_genre(artist_id, genre_id) VALUES (1, 3);
INSERT INTO artist_to_genre(artist_id, genre_id) VALUES (2, 2);
INSERT INTO artist_to_genre(artist_id, genre_id) VALUES (3, 3);
INSERT INTO artist_to_genre(artist_id, genre_id) VALUES (3, 2);

--impresario
INSERT INTO artist(name, surname) VALUES ('Зураб', 'Хаджиев');
INSERT INTO artist(name, surname) VALUES ('Иван', 'Ефремов');
INSERT INTO artist(name, surname) VALUES ('Лев', 'Кручин');

--artists to impresarios
INSERT INTO artist_to_impresario(artist_id, impresario_id) VALUES (1, 1);
INSERT INTO artist_to_impresario(artist_id, impresario_id) VALUES (1, 2);
INSERT INTO artist_to_impresario(artist_id, impresario_id) VALUES (2, 3);
INSERT INTO artist_to_impresario(artist_id, impresario_id) VALUES (3, 2);

--sponsor
INSERT INTO artist(name, surname) VALUES ('Сергей', 'Петров');
INSERT INTO artist(name, surname) VALUES ('Валентина', 'Самойлова');
INSERT INTO artist(name, surname) VALUES ('Кирилл', 'Муртазин');

--performance types
INSERT INTO artist(name) VALUES ('конкурс');
INSERT INTO artist(name) VALUES ('благотворительный концерт');
INSERT INTO artist(name) VALUES ('гала концерт');
INSERT INTO artist(name) VALUES ('памятный концерт'); -- в память джоржда буша мл.

--performances
INSERT INTO performance(name, type_id, sponsor_id, building_id, perfarmance_date)
VALUES ('я лучший 2022', 1, 1, 8, '2022-07-16');
INSERT INTO performance(name, type_id, sponsor_id, building_id, perfarmance_date)
VALUES ('я лучший 2020', 1, 2, 5, '2020-05-16');
INSERT INTO performance(name, type_id, sponsor_id, building_id, perfarmance_date)
VALUES ('вечерок', 3, 3, 6, '2022-06-26');
INSERT INTO performance(name, type_id, sponsor_id, building_id, perfarmance_date)
VALUES ('концерт в память сергея петровича', 4, 2, 5, '2023-09-1');

--artists to performances
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (1, 1);
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (1, 2);
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (2, 3);
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (2, 4);
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (2, 1);
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (3, 1);
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (4, 4);
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (4, 2);

--contest places
INSERT INTO contest_place(artist_id, performance_id, place) VALUES (1, 1, 1);
INSERT INTO contest_place(artist_id, performance_id, place) VALUES (1, 2, 2);
INSERT INTO contest_place(artist_id, performance_id, place) VALUES (2, 1, 2);
INSERT INTO contest_place(artist_id, performance_id, place) VALUES (3, 1, 3);
INSERT INTO contest_place(artist_id, performance_id, place) VALUES (4, 2, 1);

