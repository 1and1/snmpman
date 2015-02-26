package com.oneandone.network.snmpman.configuration;

import com.google.common.base.Preconditions;
import org.snmp4j.smi.OID;

/**
 * A wildcard {@code OID} is an {@code OID} that can contain the wildcard character "*".
 * <p/>
 * The wildcard will be considered for matching request in the function {@link #matches(org.snmp4j.smi.OID)}.
 * Every sequence of {@code int} array chains can be considered for the wildcard character.
 * <p/>
 * Only one wildcard is allowed for the wildcard {@code OID}.
 *
 * @author Johann Böhler
 */
public class WildcardOID {

    /**
     * the first part of the wildcard {@code OID} (before the "{@code *}" character).
     */
    private final OID startsWith;

    /**
     * the second part of the wildcard {@code OID} (after the "{@code *}" character).
     */
    private final OID endsWith;

    /**
     * Constructs a new instance of this class.
     *
     * @param oid the {@code OID} that potentially contains a wildcard
     * @throws NullPointerException     if the specified {@code OID} is null
     * @throws IllegalArgumentException if the specified {@code OID} contains more than one wildcard character
     */
    public WildcardOID(final String oid) {
        Preconditions.checkNotNull(oid, "oid may not be null");

        if (oid.contains("*")) {
            final String[] splitted = oid.split("\\.\\*");
            Preconditions.checkArgument(splitted.length == 2, "oid \"" + oid + "\" contains more than one wildcard character, but only one is allowed");
            this.startsWith = new OID(splitted[0]);
            this.endsWith = new OID(splitted[1]);
        } else {
            this.startsWith = new OID(oid);
            this.endsWith = null;
        }
    }

    /**
     * Returns {@code true} if this wildcard {@code OID} matches with the specified {@code OID}.
     * <p/>
     * Here some examples for matching {@code OID}s:
     * <ul>
     * <li>{@code .1.3.3.*.7} and {@code .1.3.3.7}</li>
     * <li>{@code .1.3.3.*.7} and {@code .1.3.3.3.7}</li>
     * <li>{@code .1.3.3.*.7} and {@code .1.3.3.1.2.3.4.5.7}</li>
     * </ul>
     *
     * @param oid the {@code OID} to test
     * @return {@code true} if the {@code OID}s are matching, otherwise {@code false}
     */
    public boolean matches(final OID oid) {
        return oid.startsWith(startsWith) && (endsWith == null || oid.size() >= endsWith.size() && oid.rightMostCompare(endsWith.size(), endsWith) == 0);
    }

    @Override
    public String toString() {
        if (endsWith == null) {
            return startsWith.toDottedString();
        } else {
            return startsWith.toDottedString() + ".*." + endsWith.toDottedString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final WildcardOID that = (WildcardOID) o;
        return !(endsWith != null ? !endsWith.equals(that.endsWith) : that.endsWith != null) && startsWith.equals(that.startsWith);

    }

    @Override
    public int hashCode() {
        int result = startsWith.hashCode();
        result = 31 * result + (endsWith != null ? endsWith.hashCode() : 0);
        return result;
    }
}
