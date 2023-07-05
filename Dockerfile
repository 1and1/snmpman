###########
#
# Build with:
# podman build -tsnmpman .
#
# Run with:
# podman run -p127.0.0.1:10000:10000/udp snmpman 
#
###########

#
# maven build
#
FROM docker.io/maven:3-eclipse-temurin-17 as maven-build
COPY . /snmpman
RUN cd /snmpman && mvn clean package

#
# create JRE
#
FROM docker.io/debian:bookworm as jre-build
ARG JAVA_VERSION=17
ENV JAVA_HOME=/opt/java/openjdk
COPY --from=docker.io/eclipse-temurin:${JAVA_VERSION} /opt/java/openjdk $JAVA_HOME
ENV PATH="${JAVA_HOME}/bin:${PATH}"

RUN apt-get update && apt-get install --yes binutils
# Create a custom Java runtime
RUN $JAVA_HOME/bin/jlink \
         --add-modules java.base,java.xml,java.desktop,java.management,java.naming \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /javaruntime

#
# combine JRE + JAR
#
FROM docker.io/debian:bookworm
ENV JAVA_HOME=/opt/java/openjdk
COPY --from=jre-build /javaruntime $JAVA_HOME
ENV PATH="${JAVA_HOME}/bin:${PATH}"

#RUN apt-get update && apt-get install --yes snmp tcpdump strace iproute2 net-tools
RUN useradd snmpman
COPY --chown=snmpman:snmpman --from=maven-build /snmpman/snmpman-cli/target/snmpman-cli-*bin /snmpman
COPY --chown=snmpman:snmpman --from=maven-build /snmpman/snmpman-cli/src/main/config/devices/catos.yaml /snmpman/etc/devices/
COPY --chown=snmpman:snmpman --from=maven-build /snmpman/snmpman-cli/src/main/config/devices/finesse.yaml /snmpman/etc/devices/
COPY --chown=snmpman:snmpman --from=maven-build /snmpman/snmpman-cli/src/main/config/devices/foundry.yaml /snmpman/etc/devices/
COPY --chown=snmpman:snmpman --from=maven-build /snmpman/snmpman-cli/src/main/config/devices/iosxr.yaml /snmpman/etc/devices/
COPY --chown=snmpman:snmpman --from=maven-build /snmpman/snmpman-cli/src/main/config/devices/ios.yaml /snmpman/etc/devices/
COPY --chown=snmpman:snmpman --from=maven-build /snmpman/snmpman-cli/src/main/config/devices/junos.yaml /snmpman/etc/devices/
COPY --chown=snmpman:snmpman --from=maven-build /snmpman/snmpman-cli/src/main/config/devices/nxos.yaml /snmpman/etc/devices/
COPY --chown=snmpman:snmpman --from=maven-build /snmpman/snmpman-cli/src/main/config/walk/example1.walk /snmpman/etc/walk/
COPY docker/docker-entrypoint.sh /docker-entrypoint.sh
USER snmpman
EXPOSE 10000/udp
ENTRYPOINT ["/docker-entrypoint.sh"]
