FROM hseeberger/scala-sbt:8u222_1.3.5_2.13.1 AS model-api
EXPOSE 8080
WORKDIR /model-api
ADD . /model-api
CMD sbt run