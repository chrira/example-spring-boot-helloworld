FROM fabric8/java-centos-openjdk8-jdk

MAINTAINER Christoph Raaflaub <raaflaub@puzzle.ch>

EXPOSE 8080 9000

LABEL io.k8s.description="Example Spring Boot App" \
      io.k8s.display-name="APPUiO Spring Boot App" \
      io.openshift.expose-services="8080:http" \
      io.openshift.tags="builder,springboot"
      
RUN ls -la /tmp/*

RUN cp -a /tmp/build/libs/springboots2idemo-0.0.1-SNAPSHOT.jar /deployments/springboots2idemo.jar
