CREATE TABLE IF NOT EXISTS building_type(
	type_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS building(
	building_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	name VARCHAR NOT NULL UNIQUE,
	type_id BIGINT NOT NULL REFERENCES building_type
);


--concrete types
CREATE TABLE IF NOT EXISTS theater(
	building_id BIGINT PRIMARY KEY REFERENCES building ON DELETE CASCADE,
	capacity INT NOT NULL CHECK(capacity > 0)
);

CREATE TABLE IF NOT EXISTS performance_venue(
	building_id BIGINT PRIMARY KEY REFERENCES building ON DELETE CASCADE,
	area INT NOT NULL CHECK(area > 0)
);

CREATE TABLE IF NOT EXISTS estrade(
	building_id BIGINT PRIMARY KEY REFERENCES building ON DELETE CASCADE,
	scene_height_meters INT NOT NULL CHECK(scene_height_meters > 0)
);

CREATE TABLE IF NOT EXISTS palace_of_culture(
	building_id BIGINT PRIMARY KEY REFERENCES building ON DELETE CASCADE,
	floor_num INT NOT NULL CHECK(floor_num > 0)
);


--artists and genres
CREATE TABLE IF NOT EXISTS genre(
	genre_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS artist(
	artist_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	name VARCHAR NOT NULL,
	surname VARCHAR NOT NULL,
	UNIQUE(name, surname)
);

CREATE TABLE IF NOT EXISTS artist_to_genre(
	artist_id BIGINT NOT NULL REFERENCES artist ON DELETE CASCADE,
	genre_id BIGINT NOT NULL REFERENCES genre,
	PRIMARY KEY(artist_id, genre_id)
);

CREATE TABLE IF NOT EXISTS impresario(
	impresario_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	name VARCHAR NOT NULL,
	surname VARCHAR NOT NULL,
	UNIQUE(name, surname)
);

CREATE TABLE IF NOT EXISTS artist_to_impresario(
	artist_id BIGINT NOT NULL REFERENCES artist(artist_id) ON DELETE CASCADE,
	impresario_id BIGINT NOT NULL REFERENCES impresario(impresario_id) ON DELETE CASCADE,
	PRIMARY KEY(artist_id, impresario_id)
);

CREATE TABLE IF NOT EXISTS organizator(
	organizator_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	name VARCHAR NOT NULL,
	surname VARCHAR NOT NULL,
	UNIQUE(name, surname)
);

CREATE TABLE IF NOT EXISTS performance_type(
	type_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS performance(
	performance_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	name VARCHAR NOT NULL,
	type_id BIGINT NOT NULL REFERENCES performance_type(type_id),
	organizator_id BIGINT NOT NULL REFERENCES organizator(organizator_id),
	building_id BIGINT NOT NULL REFERENCES building(building_id) ON DELETE CASCADE,
	performance_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS artist_to_performance(
	performance_id BIGINT NOT NULL REFERENCES performance(performance_id) ON DELETE CASCADE,
	artist_id BIGINT NOT NULL REFERENCES artist(artist_id) ON DELETE CASCADE,
	PRIMARY KEY(performance_id, artist_id)
);

CREATE TABLE IF NOT EXISTS contest_place(
	performance_id BIGINT NOT NULL REFERENCES performance(performance_id) ON DELETE CASCADE,
	artist_id BIGINT NOT NULL REFERENCES artist(artist_id) ON DELETE CASCADE,
	place INT NOT NULL CHECK(place > 0),
	PRIMARY KEY(performance_id, artist_id)
);

