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
--проведенных в указанном культурном сооружении. (тут все перформансы в здании)
SELECT * FROM performance
WHERE building_id = 7

--Получить список артистов, выступающих более чем в одном жанре с их указанием.
-- а как жанры ещё найти? отдельным запросом?
SELECT * FROM artist
WHERE id IN 
(
	SELECT artist_id FROM artist_to_genre AS ag
	GROUP BY
		ag.artist_id
	HAVING
		COUNT(*) > 1
)

--отдельный запрос на жанры по артисту
SELECT * FROM genre
WHERE id IN
(
    SELECT genre_id FROM artist_to_genre
    WHERE artist_id = :artistId
)

--Получить список призеров указанного конкурса.
-- вообще, надо бы чекнуть, что это конкурс
-- тянет призёров, но потом надо отдельным запросом притянуть места
SELECT * FROM artist
WHERE id IN
(
	SELECT artist_id FROM contest_place
	WHERE performance_id = :3 AND place <= 3
)



--Получить список импресарио определенного жанра.
SELECT * FROM impresario
WHERE id IN
(
	SELECT *
)