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
INSERT INTO estrade(id, scene_height_centimeters) VALUES (11, 25);

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
INSERT INTO artist(name, surname) VALUES ('Зураб', 'Хаджиев');
INSERT INTO artist(name, surname) VALUES ('Иван', 'Ефремов');
INSERT INTO artist(name, surname) VALUES ('Лев', 'Кручин');

--artists to genres
INSERT INTO artist_to_genre(artist_id, genre_id) VALUES (1, 1);
INSERT INTO artist_to_genre(artist_id, genre_id) VALUES (1, 3);
INSERT INTO artist_to_genre(artist_id, genre_id) VALUES (2, 2);
INSERT INTO artist_to_genre(artist_id, genre_id) VALUES (3, 3);
INSERT INTO artist_to_genre(artist_id, genre_id) VALUES (9, 2);
INSERT INTO artist_to_genre(artist_id, genre_id) VALUES (10, 1);
INSERT INTO artist_to_genre(artist_id, genre_id) VALUES (10, 3);
INSERT INTO artist_to_genre(artist_id, genre_id) VALUES (11, 1);

--impresario
INSERT INTO impresario(name, surname) VALUES ('Кирилл', 'Клементев');
INSERT INTO impresario(name, surname) VALUES ('Юрий', 'Галка');
INSERT INTO impresario(name, surname) VALUES ('Борис', 'Смирнов');
INSERT INTO impresario(name, surname) VALUES ('Григорий', 'Кениг');

--artists to impresarios
INSERT INTO artist_to_impresario(artist_id, impresario_id) VALUES (1, 1);
INSERT INTO artist_to_impresario(artist_id, impresario_id) VALUES (1, 2);
INSERT INTO artist_to_impresario(artist_id, impresario_id) VALUES (2, 3);
INSERT INTO artist_to_impresario(artist_id, impresario_id) VALUES (3, 2);
INSERT INTO artist_to_impresario(artist_id, impresario_id) VALUES (4, 3);
INSERT INTO artist_to_impresario(artist_id, impresario_id) VALUES (9, 2);
INSERT INTO artist_to_impresario(artist_id, impresario_id) VALUES (10, 1);
INSERT INTO artist_to_impresario(artist_id, impresario_id) VALUES (10, 4);
INSERT INTO artist_to_impresario(artist_id, impresario_id) VALUES (11, 3);
INSERT INTO artist_to_impresario(artist_id, impresario_id) VALUES (11, 1);

--sponsor
INSERT INTO sponsor(name, surname) VALUES ('Сергей', 'Петров');
INSERT INTO sponsor(name, surname) VALUES ('Валентина', 'Самойлова');
INSERT INTO sponsor(name, surname) VALUES ('Кирилл', 'Муртазин');

--performance types
INSERT INTO performance_type(name) VALUES ('конкурс');
INSERT INTO performance_type(name) VALUES ('благотворительный концерт');
INSERT INTO performance_type(name) VALUES ('гала концерт');
INSERT INTO performance_type(name) VALUES ('памятный концерт');

--performances
INSERT INTO performance(name, type_id, sponsor_id, building_id, performance_date)
VALUES ('я лучший 2022', 1, 1, 8, '2022-07-16');
INSERT INTO performance(name, type_id, sponsor_id, building_id, performance_date)
VALUES ('я лучший 2020', 1, 2, 7, '2020-05-16');
INSERT INTO performance(name, type_id, sponsor_id, building_id, performance_date)
VALUES ('вечерок', 3, 3, 10, '2022-06-26');
INSERT INTO performance(name, type_id, sponsor_id, building_id, performance_date)
VALUES ('концерт в память сергея петровича', 4, 2, 11, '2023-09-1');
INSERT INTO performance(name, type_id, sponsor_id, building_id, performance_date)
VALUES ('подари жизнь', 2, 1, 9, '2021-08-11');
INSERT INTO performance(name, type_id, sponsor_id, building_id, performance_date)
VALUES ('галактика', 3, 3, 9, '2020-08-13');

--artists to performances
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (1, 3);
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (1, 4);
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (2, 5);
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (2, 6);
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (2, 3);
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (3, 3);
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (4, 6);
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (10, 7);
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (10, 4);
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (11, 5);
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (11, 6);
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (10, 6);
INSERT INTO artist_to_performance(artist_id, performance_id) VALUES (11, 4);

--contest places
INSERT INTO contest_place(artist_id, performance_id, place) VALUES (1, 3, 1);
INSERT INTO contest_place(artist_id, performance_id, place) VALUES (1, 4, 2);
INSERT INTO contest_place(artist_id, performance_id, place) VALUES (2, 3, 2);
INSERT INTO contest_place(artist_id, performance_id, place) VALUES (3, 3, 3);
INSERT INTO contest_place(artist_id, performance_id, place) VALUES (4, 4, 1);
INSERT INTO contest_place(artist_id, performance_id, place) VALUES (10, 4, 3);
INSERT INTO contest_place(artist_id, performance_id, place) VALUES (11, 4, 4);

