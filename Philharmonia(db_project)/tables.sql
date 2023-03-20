DROP TABLE IF EXISTS building_type;
DROP TABLE IF EXISTS building;
DROP TABLE IF EXISTS theater;
DROP TABLE IF EXISTS performance_venue;
DROP TABLE IF EXISTS estrade;
DROP TABLE IF EXISTS artist;

CREATE TABLE IF NOT EXISTS building_type(
	type_id SERIAL PRIMARY KEY,
	building_id BIGINT UNIQUE REFERENCES building(building_id),
	name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS building(
	building_id BIGSERIAL PRIMARY KEY,
	name VARCHAR NOT NULL UNIQUE
);

--concrete types
CREATE TABLE IF NOT EXISTS theater(
	theater_id SERIAL PRIMARY KEY,
	type_id BIGINT UNIQUE REFERENCES building_type(type_id),
	capacity INT NOT NULL CHECK(capacity > 0)
);

CREATE TABLE IF NOT EXISTS performance_venue(
	venue_id SERIAL PRIMARY KEY,
	type_id BIGINT UNIQUE REFERENCES building_type(type_id),
	area INT NOT NULL CHECK(area > 0)
);

CREATE TABLE IF NOT EXISTS estrade(
	estrade_id SERIAL PRIMARY KEY,
	type_id BIGINT UNIQUE REFERENCES building_type(type_id),
	scene_height_meters INT NOT NULL CHECK(scene_height > 0)
);

CREATE TABLE IF NOT EXISTS palace_of_culture(
	palace_id SERIAL PRIMARY KEY,
	type_id BIGINT UNIQUE REFERENCES building_type(type_id),
	floor_num INT NOT NULL CHECK(floor_num > 0)
);


--artists and genres
CREATE TABLE IF NOT EXISTS genre(
	genre_id SERIAL PRIMARY KEY,
	name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS artist(
	artist_id BIGSERIAL PRIMARY KEY,
	name VARCHAR NOT NULL,
	surname VARCHAR NOT NULL,
	UNIQUE(name, surname)
);

CREATE TABLE IF NOT EXISTS artist_to_genre(
	artist_id BIGINT REFERENCES artist(artist_id),
	genre_id INT REFERENCES genre(genre_id),
	PRIMARY KEY(artist_id, genre_id)
);

CREATE TABLE IF NOT EXISTS impresario(
	impresario_id BIGSERIAL PRIMARY KEY,
	name VARCHAR NOT NULL,
	surname VARCHAR NOT NULL,
	UNIQUE(name, surname)
);

CREATE TABLE IF NOT EXISTS artist_to_impresario(
	artist_id BIGINT REFERENCES artist,
	impresario_id BIGINT REFERENCES impresario(impresario_id),
	PRIMARY KEY(artist_id, impresario_id)
);

CREATE TABLE IF NOT EXISTS organizator(
	organizator_id BIGSERIAL PRIMARY KEY,
	name VARCHAR NOT NULL,
	surname VARCHAR NOT NULL,
	UNIQUE(name, surname)
);

CREATE TABLE IF NOT EXISTS performance_type(
	type_id SERIAL PRIMARY KEY,
	name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS performance(
	performance_id BIGSERIAL PRIMARY KEY,
	name VARCHAR NOT NULL UNIQUE,
	type_id INT REFERENCES performance_type(type_id),
	organizator_id BIGINT REFERENCES organizator(organizator_id),
	building_id BIGINT REFERENCES building(building_id)
);

CREATE TABLE IF NOT EXISTS contest(
	contest_id BIGSERIAL PRIMARY KEY,
	type_id INT REFERENCES performance_type(type_id),
);

CREATE TABLE IF NOT EXISTS places_distribution(
	distribution_id BIGSERIAL PRIMARY KEY,
	contest_id BIGINT REFERENCES contest(contest_id),
	artist_id BIGINT REFERENCES artist(artist_id),
	place INT NOT NULL
);