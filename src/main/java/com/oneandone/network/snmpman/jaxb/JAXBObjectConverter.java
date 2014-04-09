package com.oneandone.network.snmpman.jaxb;

import com.google.common.base.Preconditions;

import java.io.File;

/**
 * Class containing utility functions to convert data definitions to specific <i>Java</i> types.
 *
 * @author Johann Böhler
 */
public final class JAXBObjectConverter {

    /**
     * Hidden constructor for utility class.
     */
    private JAXBObjectConverter() {
        // nothing to do here
    }

    /**
     * Returns a {@link File} object by the specified path.
     *
     * @param path the file path
     * @return the {@link File} object for the path
     * @throws NullPointerException     if the specified path is {@code null}
     * @throws IllegalArgumentException if the specified path is empty
     */
    public static File parseFile(final String path) {
        Preconditions.checkNotNull(path, "path should not be null");
        Preconditions.checkArgument(!path.isEmpty(), "path should not be empty");
        return new File(path);
    }

    /**
     * Returns the absolute path of the specified file.
     *
     * @param file the file to convert to a {@link String}
     * @return the absolute path of the specified file
     * @throws NullPointerException if the specified file is {@code null}
     */
    public static String printFile(final File file) {
        Preconditions.checkNotNull(file, "file should not be null");
        return file.getAbsolutePath();
    }

    /**
     * Returns a {@link WildcardOID} object by the specified {@link String} definition.
     *
     * @param oid the {@link String} of the wildcard {@code OID}
     * @return {@link WildcardOID} object by the specified {@link String} definition
     * @throws NullPointerException     if the specified {@code OID} is {@code null}
     * @throws IllegalArgumentException if the specified {@code OID} is empty
     */
    public static WildcardOID parseWildcardOID(final String oid) {
        Preconditions.checkNotNull(oid, "oid should not be null");
        Preconditions.checkArgument(!oid.isEmpty(), "oid should not be empty");
        return new WildcardOID(oid);
    }

    /**
     * Returns the {@link String} representation of the specified {@code OID}.
     *
     * @param oid the oid to convert to a {@link String}
     * @return the {@link String} representation of the specified {@code OID}
     * @throws NullPointerException if the specified {@code OID} is {@code null}
     */
    public static String printWildcardOID(final WildcardOID oid) {
        Preconditions.checkNotNull(oid, "oid should not be null");
        return oid.toString();
    }

}
