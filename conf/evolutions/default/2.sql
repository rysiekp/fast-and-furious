-- Add reviews

-- !Ups

CREATE TABLE reviews (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    movie_id INTEGER,
    rating INTEGER,
    review VARCHAR,
    author VARCHAR,
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);

-- !Downs

DROP TABLE IF EXISTS reviews;
