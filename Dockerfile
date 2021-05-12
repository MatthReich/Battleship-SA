FROM hseeberger/scala-sbt:8u222_1.3.5_2.13.1 AS game
EXPOSE 8079
WORKDIR /game
ADD . /game
CMD sbt run