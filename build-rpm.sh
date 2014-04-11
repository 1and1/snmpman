#!/bin/sh

# The directory of this 'build-rpm.sh' file
BUILD_FILE_DIRECTORY="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "... redhat package will be created"

echo "... will set general file access"
find ${BUILD_FILE_DIRECTORY}/src/ -type d -exec chmod 775 {} + || { echo "ERROR: could not set directory acccess!" >&2; exit 1; }
find ${BUILD_FILE_DIRECTORY}/src/ -type f -exec chmod 644 {} + || { echo "ERROR: could not set file acccess!" >&2; exit 1; }

echo "... will set execute rights to binaries"
chmod 0755 ${BUILD_FILE_DIRECTORY}/src/main/linux/redhat/dataset/bin/snmpman.sh

echo "... maven build will be started"
mvn -q -f ${BUILD_FILE_DIRECTORY}/pom.xml clean package -P redhat || { echo "ERROR: could not build application!" >&2; exit 1; }
echo "... build process completed successfully"

if ! [ -z "${1}" ]; then
	cp ${BUILD_FILE_DIRECTORY}/target/rpm/snmpman/RPMS/noarch/*.rpm ${1}
else
	echo "... no argument supplied, rpm will be copied to working directory"
	cp ${BUILD_FILE_DIRECTORY}/target/rpm/snmpman/RPMS/noarch/*.rpm $( pwd )/.
fi
