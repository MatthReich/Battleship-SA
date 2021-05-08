FROM mongo:latest
EXPOSE 27017

ENV MONGO_INITDB_DATABASE=battleship
#ENV MONGO_INITDB_ROOT_USERNAME=battleship
#ENV MONGO_INITDB_ROOT_PASSWORD=battleship

ENV TZ=Europe/Amsterdam

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
	echo $TZ > /etc/timezone