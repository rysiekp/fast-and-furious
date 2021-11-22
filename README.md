# Fast and Furious cinema

Implementation for https://gist.github.com/wbaumann/aaa5ef095e213ffbea35b7ca3cc251a7

## Basic concepts
1. The app was created using Scala Play framework as it's the state-of-the-art solution for REST services in Scala
2. H2 was used in conjunction with Slick as a persistance layer, because of it's known ease of use for developments. For production use-case it may not be enough and other solutions should be considered, depending on the predicted usage of the service like distributed datastores.
3. Simple MVC (minus the View part, as we only provide an API) is used for the app, which allows us faster development and easier usage of the API

## Building and deploying
### Prerequisites
1. Scala 2.11
2. sbt 1.3.*
3. Java 8

### Building and running
1. `sbt clean compile`
2. `sbt run`
3. The application should be available at `localhost:9000`
4. To hit an endpoint suply try going to for example `localhost:9000/movies/`

## Functionality
1. In addition to all points listed in the excersise itself, several improvements were introduced:
   1. We allow users to sign their reviews as well as leave text review in addition to the star rating
   2. We allow to modify and delete existing screenings
   3. We allow getting the movie details both by using the title as well as by using the OMDB id of the movie
2. API documentation is available in the _docs_ directory
3. We don't limit the users to just the Fast & Furious franchise, the reasoning is that such restriction doesn't make sense and users can impose it on themselves

## Changes for production code
1. More unit testing
2. Integration tests, ideally set up with a CI/CD env for fast iteration
3. Additional endpoints to manipulate data - deletion/changing of movies and reviews
4. Additional endpoints to obtain data - for example getting all reviews for a particular movie
5. Security layer to restrict outside customers from interacting with CREATE/EDIT/DELETE part of the API

## Examples
### Movies
Adding movies
```
curl -w '\n' http://localhost:9000/movies/ -H "Content-Type:application/json" -d '{"title": "The Fast and the Furious", "imdbId": "tt0232500"}'
curl -w '\n' http://localhost:9000/movies/ -H "Content-Type:application/json" -d '{"title": "2 Fast 2 Furious", "imdbId": "tt0322259"}'
curl -w '\n' http://localhost:9000/movies/ -H "Content-Type:application/json" -d '{"title": "The Fast and the Furious: Tokyo Drift", "imdbId": "tt0463985"}'
curl -w '\n' http://localhost:9000/movies/ -H "Content-Type:application/json" -d '{"title": "Fast & Furious", "imdbId": "tt1013752"}'
curl -w '\n' http://localhost:9000/movies/ -H "Content-Type:application/json" -d '{"title": "Fast Five", "imdbId": "tt1596343"}'
curl -w '\n' http://localhost:9000/movies/ -H "Content-Type:application/json" -d '{"title": "Fast & Furious 6", "imdbId": "tt1905041"}'
curl -w '\n' http://localhost:9000/movies/ -H "Content-Type:application/json" -d '{"title": "Furious 7", "imdbId": "tt2820852"}'
curl -w '\n' http://localhost:9000/movies/ -H "Content-Type:application/json" -d '{"title": "The Fate of the Furious", "imdbId": "tt4630562"}'
```

Listing movies
```
curl -w '\n' http://localhost:9000/movies/
{"result":"ok","response":[{"id":1,"imdbId":"tt0232500","title":"The Fast and the Furious"},{"id":2,"imdbId":"tt0322259","title":"2 Fast 2 Furious"},{"id":3,"imdbId":"tt0463985","title":"The Fast and the Furious: Tokyo Drift"},{"id":4,"imdbId":"tt1013752","title":"Fast & Furious"},{"id":5,"imdbId":"tt1596343","title":"Fast Five"},{"id":6,"imdbId":"tt1905041","title":"Fast & Furious 6"},{"id":7,"imdbId":"tt2820852","title":"Furious 7"},{"id":8,"imdbId":"tt4630562","title":"The Fate of the Furious"}]}
```

Getting movie details
```
curl -w '\n' http://localhost:9000/movies/details/title/Fast%20Five/
{"result":"ok","response":{"title":"Fast Five","year":"2011","rated":"PG-13","released":"29 Apr 2011","runtime":"130 min","genre":"Action, Adventure, Crime","director":"Justin Lin","writer":"Chris Morgan, Gary Scott Thompson","actors":"Vin Diesel, Paul Walker, Dwayne Johnson","plot":"Former cop Brian O'Conner partners with ex-con Dom Toretto on the opposite side of the law. Since Brian and Mia Toretto broke Dom out of custody, they've blown across many borders to elude authorities. Now backed into a corner in Rio de Janeiro, they must pull one last job in order to gain their freedom. As they assemble their elite team of top racers, the unlikely allies know their only shot of getting out for good means confronting the corrupt businessman who wants them dead. But he's not the only one on their tail. Hard-nosed federal agent Luke Hobbs never misses his target. When he is assigned to track down Dom and Brian, he and his strike team launch an all-out assault to capture them. But as his men tear through Brazil, Hobbs learns he can't separate the good guys from the bad. Now, he must rely on his instincts to corner his prey... before someone else runs them down first.","language":"English, Portuguese, Spanish, Italian, French","country":"United States, Brazil, Japan","awards":"9 wins & 21 nominations","poster":"https://m.media-amazon.com/images/M/MV5BMTUxNTk5MTE0OF5BMl5BanBnXkFtZTcwMjA2NzY3NA@@._V1_SX300.jpg","metascore":"66","imdbRating":"7.3","imdbVotes":"368,801","imdbId":"tt1596343"}}
```

### Screenings
Listing screenings
```
curl -w '\n' http://localhost:9000/screenings/
{"result":"ok","response":[{"id":1,"movieId":1,"screeningTime":"21:37","price":420}]}
```

Adding screening
```
curl -w '\n' http://localhost:9000/screenings/ -H "Content-Type:application/json" -d '{"movieId": 2, "screeningTime":  "21:37", "price": -10}'
```

Adding an incorrect screening
```
curl -w '\n' http://localhost:9000/screenings/ -H "Content-Type:application/json" -d '{"movieId": 2, "screeningTime":  "21:37", "price": -10}'
{"result":"ko","response":null,"error":{"status":400,"message":"Price cannot be less than 0"}}
```

Deleting screening
```
curl -w '\n' -X DELETE http://localhost:9000/screenings/2/
```

### Reviews
Showing all reviews
```
curl -w '\n' http://localhost:9000/reviews/
{"result":"ok","response":[{"id":1,"movieId":2,"rating":3,"review":"Fun and cool"},{"id":2,"movieId":3,"rating":3,"author":"JP"},{"id":3,"movieId":1,"rating":5},{"id":4,"movieId":8,"rating":5},{"id":5,"movieId":1,"rating":4}]}
```

Adding review
```
curl -w '\n' http://localhost:9000/reviews/ -H "Content-Type:application/json" -d '{"movieId": 1, "rating": 5}'
```

Adding incorrect review
```
curl -w '\n' http://localhost:9000/reviews/ -H "Content-Type:application/json" -d '{"movieId": 1, "rating": 8}'
{"result":"ko","response":null,"error":{"status":400,"message":"Rating must be between 1 - 5 starts"}}
```