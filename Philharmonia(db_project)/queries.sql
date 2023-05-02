-- Получить список импресарио указанного артиста.
-- from impresario
SELECT * FROM impresario
WHERE impresario.id IN
(
	SELECT artist_id FROM artist_to_impresario
	WHERE artist_id = 4
);

-- Получить список импресарио определенного жанра.


-- Получить список артистов, выступающих в некотором жанре.
SELECT * FROM artist
WHERE id IN
(
	SELECT artist_id FROM artist_to_genre
	WHERE genre_id = 3
)

--Получить список артистов, работающих с некоторым импресарио.
SELECT * FROM artist
WHERE id IN
(
	SELECT artist_id FROM artist_to_impresario
	WHERE impresario_id = 3
)

--Получить перечень концертных мероприятий, 
--проведенных в указанном культурном сооружении.
SELECT * FROM performance
WHERE building_id = 7