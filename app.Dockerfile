FROM gradle:jre15-hotspot AS build
COPY --chown=gradle:gradle . /home/gradle
WORKDIR /home/gradle
RUN gradle assemble --stacktrace

FROM openjdk:11
MAINTAINER grame-of-threads
COPY --from=build /home/gradle/build/libs/schedules-0.0.1-SNAPSHOT.jar schedules.jar
ENTRYPOINT ["java","-jar","/schedules.jar"]