package com.oneandone.snmpman.configuration.type;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import org.snmp4j.smi.OID;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A wildcard {@code OID} is an {@code OID} that can contain the wildcard character "*".
 * <p/>
 * The wildcard will be considered for matching request in the function {@link #matches(org.snmp4j.smi.OID)}.
 * Every sequence of {@code int} array chains can be considered for the wildcard character.
 * <p/>
 * Only one wildcard is allowed for the wildcard {@code OID}.
 */
@EqualsAndHashCode
public class WildcardOID {

    /** The wildcard OID pattern. */
    private static final Pattern WILDCARD_OID_PATTERN = Pattern.compile("((\\.)?[0-9]+(\\.[0-9]+)*)(\\.\\*)?((\\.[0-9]+)*)");
    
    /** The first part of the wildcard {@code OID} (before the "{@code *}" character). */
    private final OID startsWith;

    /** The second part of the wildcard {@code OID} (after the "{@code *}" character). Can be {@code null}. */
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

        final Matcher matcher = WILDCARD_OID_PATTERN.matcher(oid);
        if (matcher.matches()) {
            this.startsWith = new OID(matcher.group(1));
            if (matcher.group(5).isEmpty()) {
                this.endsWith = null;
            } else {
                this.endsWith = new OID(matcher.group(5));
            }
        } else {
            throw new IllegalArgumentException("specified oid \"" + oid + "\" is not a valid wildcard");
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
            return startsWith.toDottedString() + ".*";
        } else {
            return startsWith.toDottedString() + ".*." + endsWith.toDottedString();
        }
    }
}
