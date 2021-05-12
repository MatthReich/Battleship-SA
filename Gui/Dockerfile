FROM hseeberger/scala-sbt:8u222_1.3.5_2.13.1 AS gui-api
EXPOSE 8083
RUN apt-get update && \
      apt-get install -y sbt libxrender1 libxtst6 libxi6
WORKDIR /gui-api
ADD . /gui-api
CMD sbt run