-- !Ups

CREATE TABLE movies (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    imdb_id VARCHAR,
    title VARCHAR
);

CREATE TABLE screenings (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    movie_id INTEGER,
    screening_time VARCHAR,
    price DECIMAL(10, 2),
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);

-- !Downs

DROP TABLE IF EXISTS movies;
DROP TABLE IF EXISTS screenings;