package com.oneandone.snmpman.configuration;

import com.google.common.primitives.UnsignedLong;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.Counter64;
import org.snmp4j.smi.Gauge32;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.Variable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Helper class for reading SNMP walks.
 * */
@Slf4j
public class Walks {

    /**
     * The default charset for files being read.
     */
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    /**
     * The pattern of variable bindings in a walk file.
     */
    private static final Pattern VARIABLE_BINDING_PATTERN = Pattern.compile("(((iso)?\\.[0-9]+)+) = ((([a-zA-Z0-9-]+): (.*)$)|(\"\"$))");

    /**
     * The pattern of a Hex-STRING continuation line in a walk file.
     */
    private static final Pattern HEX_STRING_PATTERN = Pattern.compile("([0-9a-fA-F]{2})( [0-9a-fA-F]{2})+");

    private Walks() {

    }

    /** Reads a walk from a file.
     * @param walk the walk file to read.
     * @return the map of oid to variable binding from the file.
     * @throws IOException if the file could not be read.
     * */
    public static Map<OID, Variable> readWalk(final File walk) throws IOException {
        log.debug("Reading walk from file {}", walk);
        try (final FileInputStream fileInputStream = new FileInputStream(walk);
             final BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, DEFAULT_CHARSET))) {
            Map<OID, Variable> result = readVariableBindings(walk, reader);
            log.debug("Walk contains {} variable bindings", result.size());
            return result;
        }
        catch (final FileNotFoundException e) {
            log.error("walk file {} not found", walk.getAbsolutePath());
            throw e;
        } catch (final IOException e) {
            log.error("could not read walk file " + walk.getAbsolutePath(), e);
            throw e;
        }
    }

    /**
     * Reads all variable bindings using {@link #VARIABLE_BINDING_PATTERN}.
     *
     * @param reader the reader to read the bindings from.
     * @return the map of oid to variable binding.
     */
    private static Map<OID, Variable> readVariableBindings(final File walk, final BufferedReader reader) throws IOException {
        final Map<OID, Variable> bindings = new HashMap<>();
        OID lastOid = null;
        String lastType = null;
        String line;
        while ((line = reader.readLine()) != null) {
            boolean match = false;
            Matcher matcher = VARIABLE_BINDING_PATTERN.matcher(line);
            if (matcher.matches()) {
                match = true;
                final OID oid = new OID(matcher.group(1).replace("iso", ".1"));
                lastOid = oid;

                try {
                    final Variable variable;
                    if (matcher.group(7) == null) {
                        lastType = "STRING";
                        variable = getVariable("STRING", "\"\"");
                    } else {
                        lastType = matcher.group(6);
                        variable = getVariable(lastType, matcher.group(7));
                    }

                    bindings.put(oid, variable);
                    log.trace("added binding with oid \"{}\" and variable \"{}\"", oid, variable);
                } catch (final Exception e) {
                    log.warn("could not parse line \"{}\" of walk file {} with exception: {}", line, walk.getCanonicalPath(), e.getMessage());
                }
            }

            // if we have a continuation line for a Hex-STRING, append to it
            if (!match && lastType != null && lastOid != null && lastType.equals("STRING")) {
                OctetString octetStringToExtend = (OctetString) bindings.get(lastOid);
                if (octetStringToExtend != null) {
                    match = true;
                    String oldString = octetStringToExtend.toString();
                    String newString;
                    if (line.endsWith("\"")) {
                        newString = line.substring(0, line.length() - 1);
                    } else {
                        newString = line;
                    }
                    String combined = oldString + "\n" + newString;
                    bindings.put(lastOid, new OctetString(combined));
                } else {
                    log.warn("Could not find the previous octet string of OID {} in walk file {}", lastOid);
                }
            }

            // if we have a continuation line for a Hex-STRING, append to it
            if (!match && lastType != null && lastOid != null && lastType.equals("Hex-STRING")) {
                matcher = HEX_STRING_PATTERN.matcher(line);
                if (matcher.matches()) {
                    match = true;
                    OctetString octetStringToExtend = (OctetString) bindings.get(lastOid);
                    if (octetStringToExtend != null) {
                        byte[] oldBytes = octetStringToExtend.getValue();
                        byte[] newBytes = OctetString.fromHexString(matcher.group(0), ' ').toByteArray();
                        byte[] combined = new byte[oldBytes.length + newBytes.length];
                        System.arraycopy(oldBytes, 0, combined, 0, oldBytes.length);
                        System.arraycopy(newBytes, 0, combined, oldBytes.length, newBytes.length);
                        bindings.put(lastOid, new OctetString(combined));
                    } else {
                        log.warn("Could not find the previous octet string of OID {} in walk file {}", lastOid);
                    }
                }
            }

            if (!match) {
                log.warn("could not parse line \"{}\" of walk file {}", line, walk.getAbsolutePath());
            }
        }
        return bindings;
    }

    /**
     * Returns a {@link Variable} instance for the specified parameters.
     *
     * @param type  the type of the variable
     * @param value the value of this variable
     * @return a a {@link Variable} instance with the specified type and value
     * @throws IllegalArgumentException if the type could not be mapped to a {@link Variable} implementation
     */
    private static Variable getVariable(final String type, final String value) {
        switch (type) {
            // TODO add "BITS" support
            case "STRING":
                String use = value;
                if (use.startsWith("\"")) {
                    use = use.substring(1);
                }
                if (use.endsWith("\"")) {
                    use = use.substring(0, use.length() - 1);
                }
                if (use.length() == 0) {
                    return new OctetString();
                }
                return new OctetString(use);
            case "OID":
                return new OID(value);
            case "Gauge32":
                return new Gauge32(Long.parseLong(value.replaceAll("[^-?0-9]+", "")));
            case "Timeticks":
                final int openBracket = value.indexOf("(") + 1;
                final int closeBracket = value.indexOf(")");
                if (openBracket == 0 || closeBracket < 0) {
                    throw new IllegalArgumentException("could not parse time tick value in " + value);
                }
                return new TimeTicks(Long.parseLong(value.substring(openBracket, closeBracket)));
            case "Counter32":
                return new Counter32(Long.parseLong(value.replaceAll("[^-?0-9]+", "")));
            case "Counter64":
                // Parse unsigned long
                return new Counter64(UnsignedLong.valueOf(value).longValue());
            case "INTEGER":
                return new Integer32(Integer.parseInt(value.replaceAll("[^-?0-9]+", "")));
            case "Hex-STRING":
                return OctetString.fromHexString(value, ' ');
            case "IpAddress":
                return new IpAddress(value);
            default:
                throw new IllegalArgumentException("illegal type \"" + type + "\" in walk detected");
        }
    }
}
