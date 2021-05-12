FROM mysql:latest
EXPOSE 3306

ENV MYSQL_ROOT_PASSWORD=battleship
ENV MYSQL_DATABASE=battleship
ENV MYSQL_USER=battleship
ENV MYSQL_PASSWORD=battleship

ENV TZ=Europe/Amsterdam

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
	echo $TZ > /etc/timezone
