FROM java:8
MAINTAINER Michael Krog <mic@apaq.dk>

VOLUME /tmp

RUN apt-get update
RUN apt-get upgrade -y

# HEALTHCHECK --interval=5m --timeout=5s CMD curl -f http://localhost:8080/manage/health || exit 1

ADD account-service.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
EXPOSE 8080