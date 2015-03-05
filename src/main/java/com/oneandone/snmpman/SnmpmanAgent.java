package com.oneandone.snmpman;

import com.google.common.primitives.UnsignedLong;
import com.oneandone.snmpman.configuration.AgentConfiguration;
import com.oneandone.snmpman.configuration.Device;
import com.oneandone.snmpman.configuration.modifier.ModifiedVariable;
import com.oneandone.snmpman.configuration.modifier.VariableModifier;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.TransportMapping;
import org.snmp4j.agent.*;
import org.snmp4j.agent.io.ImportModes;
import org.snmp4j.agent.mo.ext.StaticMOGroup;
import org.snmp4j.agent.mo.snmp.*;
import org.snmp4j.agent.security.MutableVACM;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.USM;
import org.snmp4j.smi.*;
import org.snmp4j.transport.TransportMappings;
import org.snmp4j.util.ThreadPool;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This is the core class of the {@code Snmpman}. The agent simulates the SNMP-capable devices.
 * <p />
 * This class can be instantiated via the constructor {@link #SnmpmanAgent(com.oneandone.snmpman.configuration.AgentConfiguration)}, which
 * requires an instance of the {@link com.oneandone.snmpman.configuration.AgentConfiguration}.
 */
@Slf4j
public class SnmpmanAgent extends BaseAgent {

    /** The pattern of variable bindings in a walk file. */
    private static final Pattern VARIABLE_BINDING_PATTERN = Pattern.compile("(((iso)?\\.[0-9]+)+) = ((([a-zA-Z0-9-]+): (.*)$)|(\"\"$))");

    /** The configuration of this agent. */
    private final AgentConfiguration configuration;
    
    /** The list of managed object groups. */
    private final List<ManagedObject> groups = new ArrayList<>(0);

    /**
     * Initializes a new instance of an SNMP agent.
     *
     * @param configuration the configuration for this agent
     */
    public SnmpmanAgent(final AgentConfiguration configuration) {
        super(SnmpmanAgent.getBootCounterFile(configuration), SnmpmanAgent.getConfigurationFile(configuration), new CommandProcessor(new OctetString(MPv3.createLocalEngineID())));
        this.agent.setWorkerPool(ThreadPool.create("RequestPool", 3));
        this.configuration = configuration;
    }

    /**
     * Returns the name of {@code this} agent.
     * <p /> 
     * See {@link com.oneandone.snmpman.configuration.AgentConfiguration#name} for more information on the return value.
     *
     * @return the name of {@code this} agent.
     */
    public String getName() {
        return configuration.getName();
    }
    
    /**
     * Returns the boot-counter file for the specified agent.
     * <p>
     * This file will be created in the same directory as the {@link com.oneandone.snmpman.configuration.AgentConfiguration#getWalk()} file.
     *
     * @return the boot-counter file
     */
    private static File getBootCounterFile(final AgentConfiguration configuration) {
        return new File(configuration.getWalk().getParentFile(), SnmpmanAgent.encode(configuration.getName() + ".BC.cfg"));
    }

    /**
     * Returns the configuration file for the specified agent.
     * <p>
     * This file will be created in the same directory as the {@link com.oneandone.snmpman.configuration.AgentConfiguration#getWalk()} file.
     *
     * @return the configuration file
     */
    private static File getConfigurationFile(final AgentConfiguration configuration) {
        return new File(configuration.getWalk().getParentFile(), SnmpmanAgent.encode(configuration.getName() + ".Config.cfg"));
    }

    /**
     * Translates a string into {@code x-www-form-urlencoded} format. The method uses the <i>UTF-8</i> encoding scheme.
     *
     * @param string {@code String} to be translated
     * @return the translated {@code String}
     */
    private static String encode(final String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            log.error("UTF-8 encoding is unsupported");
            return string;
        }
    }

    /**
     * Returns the root OIDs of the bindings.
     *
     * @param bindings the variable bindings
     * @return the roots of the specified variable bindings
     */
    private static List<OID> getRoots(final SortedMap<OID, Variable> bindings) {
        final List<OID> potentialRoots = new ArrayList<>(bindings.size());
        
        OID last = null;
        for (final OID oid : bindings.keySet()) {
            if (last != null) {
                int min = Math.min(oid.size(), last.size());
                while (min > 0) {
                    if (oid.leftMostCompare(min, last) == 0) {
                        OID root = new OID(last.getValue(), 0, min);
                        potentialRoots.add(root);
                        break;
                    }
                    min--;
                }
            }
            last = oid;
        }
        Collections.sort(potentialRoots);
        
        
        final List<OID> roots = new ArrayList<>(potentialRoots.size());
        for (final OID potentialRoot : potentialRoots) {
            if (potentialRoot.size() > 0) {
                OID trimmedPotentialRoot = new OID(potentialRoot.getValue(), 0, potentialRoot.size() - 1);
                while (trimmedPotentialRoot.size() > 0 && Collections.binarySearch(potentialRoots, trimmedPotentialRoot) < 0) {
                    trimmedPotentialRoot.trim(1);
                }
                if (trimmedPotentialRoot.size() == 0 && !roots.contains(potentialRoot)) {
                    roots.add(potentialRoot);
                }
            }
        }

        log.trace("identified roots {}", roots);
        return roots;
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
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    if (value.length() == 2) {
                        return new OctetString();
                    }
                    return new OctetString(value.substring(1, value.length() - 1));
                } else{
                    return new OctetString(value);
                }
            case "OID":
                return new OID(value);
            case "Gauge32":
                return new Gauge32(Long.parseLong(value.replaceAll("[^-?0-9]+", "")));
            case "Timeticks":
                final int openBracket = value.indexOf("(") + 1;
                final int closeBracket = value.indexOf(")");
                if (openBracket < 0 || closeBracket < 0) {
                    throw new IllegalArgumentException("could not parse time tick value in " + value);
                }
                return new TimeTicks(Long.parseLong(value.substring(openBracket, closeBracket)));
            case "Counter32":
                return new Counter32(Long.parseLong(value.replaceAll("[^-?0-9]+", "")));
            case "Counter64":
                // Parse unsigned long
                return new Counter64(UnsignedLong.valueOf(value).longValue());
            case "INTEGER":
                return new Integer32(Integer.valueOf(value.replaceAll("[^-?0-9]+", "")));
            case "Hex-STRING":
                return OctetString.fromHexString(value, ' ');
            case "IpAddress":
                return new IpAddress(value);
            default:
                throw new IllegalArgumentException("illegal type \"" + type + "\" in walk detected");
        }
    }

    /**
     * Starts this agent instance.
     *
     * @throws IOException signals that this agent could not be initialized by the {@link #init()} method
     */
    public void execute() throws IOException {
        this.init();
        this.loadConfig(ImportModes.REPLACE_CREATE);
        this.addShutdownHook();
        this.getServer().addContext(new OctetString("public"));
        this.finishInit();
        this.run();
        this.sendColdStartNotification();
    }

    @Override
    protected void initTransportMappings() throws IOException {
        log.trace("starting to initialize transport mappings for agent \"{}\"", configuration.getName());
        transportMappings = new TransportMapping[1];
        TransportMapping tm = TransportMappings.getInstance().createTransportMapping(configuration.getAddress());
        transportMappings[0] = tm;
    }

    @Override
    protected void registerManagedObjects() {
        log.trace("registering managed objects for agent \"{}\"", configuration.getName());
        try (final FileReader fileReader = new FileReader(configuration.getWalk());
             final BufferedReader reader = new BufferedReader(fileReader, 1024)) {

            final Map<OID, Variable> bindings = new HashMap<>();

            String line;
            while ((line = reader.readLine()) != null) {
                final Matcher matcher = VARIABLE_BINDING_PATTERN.matcher(line);
                if (matcher.matches()) {
                    final OID oid = new OID(matcher.group(1).replace("iso", ".1"));

                    final String type;
                    final String value;
                    if (matcher.group(7) == null) {
                        type = "STRING";
                        value = "\"\"";
                    } else {
                        type = matcher.group(6);
                        value = matcher.group(7);
                    }

                    try {
                        final Variable variable = SnmpmanAgent.getVariable(type, value);
                        bindings.put(oid, variable);
                        log.trace("added binding with oid \"{}\" and variable \"{}\"", oid, variable);
                    } catch (final Exception e) {
                        log.warn("could not parse line \"{}\" of walk file {} with exception: {}", line, configuration.getWalk().getCanonicalPath(), e.getMessage());
                    }
                } else {
                    log.warn("could not parse line \"{}\" of walk file {}", line, configuration.getWalk().getAbsolutePath());
                }
            }

            final SortedMap<OID, Variable> variableBindings = this.getVariableBindings(configuration.getDevice(), bindings);
            final OctetString ctx = new OctetString();

            unregisterDefaultManagedObjects(ctx);

            final List<OID> roots = SnmpmanAgent.getRoots(variableBindings);
            for (final OID root : roots) {
                final ArrayList<VariableBinding> subtree = new ArrayList<>();
                for (final Map.Entry<OID, Variable> binding : variableBindings.entrySet()) {
                    if (binding.getKey().size() >= root.size()) {
                        if (binding.getKey().leftMostCompare(root.size(), root) == 0) {
                            subtree.add(new VariableBinding(binding.getKey(), binding.getValue()));
                        }
                    }
                }

                StaticMOGroup group = new StaticMOGroup(root, subtree.toArray(new VariableBinding[subtree.size()]));
                DefaultMOContextScope scope = new DefaultMOContextScope(ctx, root, true, root.nextPeer(), false);
                ManagedObject mo = server.lookup(new DefaultMOQuery(scope, false));
                if (mo != null) {
                    for (final VariableBinding variableBinding : subtree) {
                        group = new StaticMOGroup(variableBinding.getOid(), new VariableBinding[]{ variableBinding });
                        scope = new DefaultMOContextScope(ctx, variableBinding.getOid(), true, variableBinding.getOid().nextPeer(), false);
                        mo = server.lookup(new DefaultMOQuery(scope, false));
                        if (mo != null) {
                            log.warn("could not register single OID at {} because ManagedObject {} is already registered.", variableBinding.getOid(), mo);
                        } else {
                            groups.add(group);
                            server.register(group, null);
                        }
                    }
                } else {
                    groups.add(group);
                    server.register(group, null);
                }
            }
        } catch (final FileNotFoundException e) {
            log.error("walk file {} not found", configuration.getWalk().getAbsolutePath());
        } catch (final IOException e) {
            log.error("could not read walk file " + configuration.getWalk().getAbsolutePath(), e);
        } catch (final DuplicateRegistrationException e) {
            log.error("duplicate registrations are not allowed", e);
        }
    }

    /**
     * Unregisters all default managed objects in the specified context {@code ctx}.
     *
     * @param ctx the context from which all default managed objects should be unregistred
     */
    private void unregisterDefaultManagedObjects(final OctetString ctx) {
        final OID startOID = new OID(".1");
        final DefaultMOContextScope hackScope = new DefaultMOContextScope(ctx, startOID, true, startOID.nextPeer(), false);
        ManagedObject query;
        while ((query = server.lookup(new DefaultMOQuery(hackScope, false))) != null) {
            server.unregister(query, ctx);
        }
    }

    /**
     * Returns the variable bindings for a device configuration and a list of bindings.
     * <p>
     * In this step the {@link ModifiedVariable} instances will be created as a wrapper for dynamic variables.
     *
     * @param device   the device configuration
     * @param bindings the bindings as the base
     * @return the variable bindings for the specified device configuration
     */
    private SortedMap<OID, Variable> getVariableBindings(final Device device, final Map<OID, Variable> bindings) {
        log.trace("get variable bindings for agent \"{}\"", configuration.getName());
        final SortedMap<OID, Variable> result = new TreeMap<>();

        for (final Map.Entry<OID, Variable> binding : bindings.entrySet()) {
            final List<VariableModifier> modifiers = new ArrayList<>(0);

            modifiers.addAll(device.getModifiers().stream().filter(modifier -> modifier.isApplicable(binding.getKey())).collect(Collectors.toList()));

            if (modifiers.isEmpty()) {
                result.put(binding.getKey(), binding.getValue());
            } else {
                log.trace("created modified variable for OID {}", binding.getKey());
                try {
                    result.put(binding.getKey(), new ModifiedVariable(binding.getValue(), modifiers));
                } catch (final ClassCastException e) {
                    log.error("could not create variable binding for " + binding.getKey().toString() + " and file " + configuration.getWalk().getAbsolutePath(), e);
                }
            }

        }
        return result;
    }

    @Override
    protected void unregisterManagedObjects() {
        log.trace("unregistered managed objects for agent \"{}\"", agent);
        for (final ManagedObject mo : groups) {
            server.unregister(mo, null);
        }
    }

    @Override
    protected void addUsmUser(final USM usm) {
        log.trace("adding usm user {} for agent \"{}\"", usm.toString(), configuration.getName());
        // do nothing here
    }

    @Override
    protected void addNotificationTargets(final SnmpTargetMIB snmpTargetMIB, final SnmpNotificationMIB snmpNotificationMIB) {
        log.trace("adding notification targets {}, {} for agent \"{}\"", snmpTargetMIB.toString(), snmpNotificationMIB.toString(), configuration.getName());
        // do nothing here
    }

    @Override
    protected void addViews(final VacmMIB vacmMIB) {
        log.trace("adding views in the vacm MIB {} for agent \"{}\"", vacmMIB.toString(), configuration.getName());
        vacmMIB.addGroup(SecurityModel.SECURITY_MODEL_SNMPv1, new OctetString(configuration.getCommunity()), new OctetString("v1v2group"), StorageType.nonVolatile);
        vacmMIB.addGroup(SecurityModel.SECURITY_MODEL_SNMPv2c, new OctetString(configuration.getCommunity()), new OctetString("v1v2group"), StorageType.nonVolatile);
        vacmMIB.addGroup(SecurityModel.SECURITY_MODEL_USM, new OctetString("SHADES"), new OctetString("v3group"), StorageType.nonVolatile);
        vacmMIB.addGroup(SecurityModel.SECURITY_MODEL_USM, new OctetString("TEST"), new OctetString("v3test"), StorageType.nonVolatile);
        vacmMIB.addGroup(SecurityModel.SECURITY_MODEL_USM, new OctetString("SHA"), new OctetString("v3restricted"), StorageType.nonVolatile);
        vacmMIB.addGroup(SecurityModel.SECURITY_MODEL_USM, new OctetString("v3notify"), new OctetString("v3restricted"), StorageType.nonVolatile);

        vacmMIB.addAccess(new OctetString("v1v2group"), new OctetString(), SecurityModel.SECURITY_MODEL_ANY, SecurityLevel.NOAUTH_NOPRIV, MutableVACM.VACM_MATCH_EXACT, new OctetString("fullReadView"), new OctetString("fullWriteView"), new OctetString("fullNotifyView"), StorageType.nonVolatile);
        vacmMIB.addAccess(new OctetString("v3group"), new OctetString(), SecurityModel.SECURITY_MODEL_USM, SecurityLevel.AUTH_PRIV, MutableVACM.VACM_MATCH_EXACT, new OctetString("fullReadView"), new OctetString("fullWriteView"), new OctetString("fullNotifyView"), StorageType.nonVolatile);
        vacmMIB.addAccess(new OctetString("v3restricted"), new OctetString(), SecurityModel.SECURITY_MODEL_USM, SecurityLevel.NOAUTH_NOPRIV, MutableVACM.VACM_MATCH_EXACT, new OctetString("restrictedReadView"), new OctetString("restrictedWriteView"), new OctetString("restrictedNotifyView"), StorageType.nonVolatile);
        vacmMIB.addAccess(new OctetString("v3test"), new OctetString(), SecurityModel.SECURITY_MODEL_USM, SecurityLevel.AUTH_PRIV, MutableVACM.VACM_MATCH_EXACT, new OctetString("testReadView"), new OctetString("testWriteView"), new OctetString("testNotifyView"), StorageType.nonVolatile);

        vacmMIB.addViewTreeFamily(new OctetString("fullReadView"), new OID("1.3"), new OctetString(), VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
        vacmMIB.addViewTreeFamily(new OctetString("fullWriteView"), new OID("1.3"), new OctetString(), VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
        vacmMIB.addViewTreeFamily(new OctetString("fullNotifyView"), new OID("1.3"), new OctetString(), VacmMIB.vacmViewIncluded, StorageType.nonVolatile);

        vacmMIB.addViewTreeFamily(new OctetString("restrictedReadView"), new OID("1.3.6.1.2"), new OctetString(), VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
        vacmMIB.addViewTreeFamily(new OctetString("restrictedWriteView"), new OID("1.3.6.1.2.1"), new OctetString(), VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
        vacmMIB.addViewTreeFamily(new OctetString("restrictedNotifyView"), new OID("1.3.6.1.2"), new OctetString(), VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
        vacmMIB.addViewTreeFamily(new OctetString("restrictedNotifyView"), new OID("1.3.6.1.6.3.1"), new OctetString(), VacmMIB.vacmViewIncluded, StorageType.nonVolatile);

        vacmMIB.addViewTreeFamily(new OctetString("testReadView"), new OID("1.3.6.1.2"), new OctetString(), VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
        vacmMIB.addViewTreeFamily(new OctetString("testReadView"), new OID("1.3.6.1.2.1.1"), new OctetString(), VacmMIB.vacmViewExcluded, StorageType.nonVolatile);
        vacmMIB.addViewTreeFamily(new OctetString("testWriteView"), new OID("1.3.6.1.2.1"), new OctetString(), VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
        vacmMIB.addViewTreeFamily(new OctetString("testNotifyView"), new OID("1.3.6.1.2"), new OctetString(), VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
    }

    @Override
    protected void addCommunities(final SnmpCommunityMIB snmpCommunityMIB) {
        log.trace("adding communities {} for agent \"{}\"", snmpCommunityMIB.toString(), configuration.getName());
        final Variable[] com2sec = new Variable[]{
                new OctetString(configuration.getCommunity()),                 // community name
                new OctetString(configuration.getCommunity()),                 // security name
                getAgent().getContextEngineID(),                               // local engine ID
                new OctetString(),                                             // default context name
                new OctetString(),                                             // transport tag
                new Integer32(StorageType.readOnly),                           // storage type
                new Integer32(RowStatus.active)                                // row status
        };
        final SnmpCommunityMIB.SnmpCommunityEntryRow row = snmpCommunityMIB.getSnmpCommunityEntry().createRow(new OctetString("public2public").toSubIndex(true), com2sec);
        snmpCommunityMIB.getSnmpCommunityEntry().addRow(row);
    }

}
