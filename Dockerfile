FROM docker.io/maven:3-jdk-11
COPY . /snmpman
RUN cd /snmpman && mvn clean package

FROM docker.io/openjdk:11-bullseye
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
