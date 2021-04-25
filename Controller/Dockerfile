FROM hseeberger/scala-sbt:8u222_1.3.5_2.13.1 AS controller-api
EXPOSE 8081
WORKDIR /controller-api
ADD . /controller-api
CMD sbt run