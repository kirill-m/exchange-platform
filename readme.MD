The Exchanger is a platform for trading goods for cross-platform currency -- Koins.

#### Idea
The idea of the project is that simple: users can put up for sale its goods to the exchange platform with start price. Other users can offer their prices. The seller will accept one of the offers. After that user whose offer has been accepted will get the opportunity to pay for the good with Koin (in-platform currency) and finish the deal.

#### Architecture
Current architecture scheme is monolithic for now and looks like that:

![architecture_scheme](./img/Monolith_exchange.png)

It is going to be broken into microservices architecture in future commits.

#### Project structure
It is a monolith written in Kotlin. Server-side is based on [Ktor](https://github.com/ktorio/ktor) framework. Client-side is a react-js app compiled from Kotlin. The database is embedded for now.

#### For now implemented endpoints
 * `GET "/"`
 * `POST/GET "/login"`
 * `POST "/register"`
 * `POST "/logout"`
 * `POST "/logout"`
 * `POST/GET "/sale/create"`
 * `POST/GET "/sale/{id}/delete"`
 * `POST/GET "/sale/{id}/offer"`

#### How to run
* Create and run docker images using the command: `docker-compose up -d`. This will run web and backend containers.

This will start the app in a browser on `localhost:8080`


