package com.oneandone.network.snmpman.snmp;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.agent.DefaultMOScope;
import org.snmp4j.agent.MOScope;
import org.snmp4j.agent.ManagedObject;
import org.snmp4j.agent.request.SubRequest;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

@Slf4j
@ToString(of = { "variableBindings", "root", "scope" })
public class MOGroup implements ManagedObject {

    /** Sorted map of the variable bindings for this group. */
    private final SortedMap<OID, Variable> variableBindings;

    /** The root {@code OID} for this group. */
    private final OID root;

    /** The {@link MOScope} for this group. */
    private final MOScope scope;

    /**
     * Constructs a new instance of this class.
     * <p/>
     * The specified {@code OID} and variable will be set as the only data stored
     * in the map of {@link #variableBindings}.
     *
     * @param root     the root {@code OID}
     * @param oid      the {@code OID} for a variable binding
     * @param variable the variable of the variable binding
     */
    public MOGroup(final OID root, final OID oid, final Variable variable) {
        this.root = root;
        this.scope = new DefaultMOScope(root, true, root.nextPeer(), false);
        this.variableBindings = new TreeMap<>();
        this.variableBindings.put(oid, variable);
    }

    /**
     * Constructs a new instance of this class.
     *
     * @param root             the root {@code OID}
     * @param variableBindings the map of variable bindings for this instance
     */
    public MOGroup(final OID root, final SortedMap<OID, Variable> variableBindings) {
        this.root = root;
        this.scope = new DefaultMOScope(root, true, root.nextPeer(), false);
        this.variableBindings = variableBindings;
    }

    @Override
    public MOScope getScope() {
        return scope;
    }

    @Override
    public OID find(final MOScope range) {
        final SortedMap<OID, Variable> tail = variableBindings.tailMap(range.getLowerBound());
        final OID first = tail.firstKey();
        if (range.getLowerBound().equals(first) && (!range.isLowerIncluded())) {
            if (tail.size() > 1) {
                final Iterator<OID> it = tail.keySet().iterator();
                it.next();
                return it.next();
            }
        } else {
            return first;
        }
        return null;
    }

    @Override
    public void get(final SubRequest request) {
        final OID oid = request.getVariableBinding().getOid();
        final Variable variable = variableBindings.get(oid);
        if (variable == null) {
            request.getVariableBinding().setVariable(Null.noSuchInstance);
        } else {
            request.getVariableBinding().setVariable((Variable) variable.clone());
        }
        request.completed();
    }

    @Override
    public boolean next(final SubRequest request) {
        final MOScope scope = request.getQuery().getScope();
        final SortedMap<OID, Variable> tail = variableBindings.tailMap(scope.getLowerBound());
        OID first = tail.firstKey();
        if (scope.getLowerBound().equals(first) && (!scope.isLowerIncluded())) {
            if (tail.size() > 1) {
                final Iterator<OID> it = tail.keySet().iterator();
                it.next();
                first = it.next();
            } else {
                return false;
            }
        }
        if (first != null) {
            final Variable variable = variableBindings.get(first);
            try {
                if (variable == null) {
                    request.getVariableBinding().setVariable(Null.noSuchInstance);
                } else {
                    request.getVariableBinding().setVariable((Variable) variable.clone());
                }
                request.getVariableBinding().setOid(first);
            } catch (IllegalArgumentException e) {
                if (variable != null) {
                    log.error("error occurred on variable class " + variable.getClass().getName() + " with first OID " + first.toDottedString(), e);
                }
            }
            request.completed();
            return true;
        }
        return false;
    }

    @Override
    public void prepare(final SubRequest request) {
        request.setErrorStatus(PDU.notWritable);
    }

    @Override
    public void commit(final SubRequest request) {
        request.setErrorStatus(PDU.commitFailed);
    }

    @Override
    public void undo(final SubRequest request) {
        request.setErrorStatus(PDU.undoFailed);
    }

    @Override
    public void cleanup(final SubRequest request) {
        // do nothing here
    }
}
