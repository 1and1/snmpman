FROM docker.io/maven:3-eclipse-temurin-17
COPY . /snmpman
RUN cd /snmpman && mvn clean package

FROM docker.io/debian:bookworm
ARG JAVA_VERSION=17
ENV JAVA_HOME=/opt/java/openjdk
COPY --from=docker.io/eclipse-temurin:${JAVA_VERSION} /opt/java/openjdk $JAVA_HOME
ENV PATH="${JAVA_HOME}/bin:${PATH}"

#RUN apt-get update && apt-get install --yes snmp tcpdump strace iproute2 net-tools
RUN useradd snmpman
COPY --chown=snmpman:snmpman --from=0 /snmpman/snmpman-cli/target/snmpman-cli-*bin /snmpman
COPY --chown=snmpman:snmpman --from=0 /snmpman/snmpman-cli/src/main/config/devices/catos.yaml /snmpman/etc/devices/
COPY --chown=snmpman:snmpman --from=0 /snmpman/snmpman-cli/src/main/config/devices/finesse.yaml /snmpman/etc/devices/
COPY --chown=snmpman:snmpman --from=0 /snmpman/snmpman-cli/src/main/config/devices/foundry.yaml /snmpman/etc/devices/
COPY --chown=snmpman:snmpman --from=0 /snmpman/snmpman-cli/src/main/config/devices/iosxr.yaml /snmpman/etc/devices/
COPY --chown=snmpman:snmpman --from=0 /snmpman/snmpman-cli/src/main/config/devices/ios.yaml /snmpman/etc/devices/
COPY --chown=snmpman:snmpman --from=0 /snmpman/snmpman-cli/src/main/config/devices/junos.yaml /snmpman/etc/devices/
COPY --chown=snmpman:snmpman --from=0 /snmpman/snmpman-cli/src/main/config/devices/nxos.yaml /snmpman/etc/devices/
COPY --chown=snmpman:snmpman --from=0 /snmpman/snmpman-cli/src/main/config/walk/example1.walk /snmpman/etc/walk/
COPY docker/docker-entrypoint.sh /docker-entrypoint.sh
USER snmpman
EXPOSE 10000/udp
ENTRYPOINT ["/docker-entrypoint.sh"]
