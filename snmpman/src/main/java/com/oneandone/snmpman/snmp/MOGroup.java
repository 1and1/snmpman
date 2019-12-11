package com.oneandone.snmpman.snmp;

import lombok.extern.slf4j.Slf4j;
import org.snmp4j.agent.DefaultMOScope;
import org.snmp4j.agent.MOScope;
import org.snmp4j.agent.ManagedObject;
import org.snmp4j.agent.request.RequestStatus;
import org.snmp4j.agent.request.SubRequest;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

@Slf4j
public class MOGroup implements ManagedObject {

    /**
     * Sorted map of the variable bindings for this group.
     */
    private final SortedMap<OID, Variable> variableBindings;

    /**
     * The root {@code OID} for this group.
     */
    private final OID root;

    /**
     * The {@link MOScope} for this group.
     */
    private final MOScope scope;

    /**
     * Constructs a new instance of this class.
     * <br>
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
        if (range.getLowerBound().equals(first) && !range.isLowerIncluded()) {
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
        if (scope.getLowerBound().equals(first) && !scope.isLowerIncluded()) {
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
            // TODO remove try / catch if no more errors occur
            // TODO add configuration check with types though (e.g. UInt32 == UInt32 Modifier?)
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

    /**
     * Check two mandatory properties:
     * <ol>
     * <li>OID to set is in the scope of the MOGroup</li>
     * <li>New value has the same Variable type</li>
     * </ol>
     * Depending on process the RequestStatus gets adjust.
     *
     * @param request The SubRequest to handle.
     */
    @Override
    public void prepare(SubRequest request) {
        RequestStatus status = request.getStatus();
        if (request.getIndex() > 0) {
            //Skip rowStatusColumn SubRequest with index 0
            OID oid = request.getVariableBinding().getOid();
            request.setUndoValue(variableBindings.get(oid));
        }
        status.setPhaseComplete(true);
    }

    /**
     * If the prepare method doesn't set {@link org.snmp4j.agent.request.RequestStatus RequestStatus} to
     * {@link  org.snmp4j.mp.SnmpConstants#SNMP_ERROR_SUCCESS SNMP_ERROR_SUCCESS} the new values are written.
     * Otherwise the commit fails and forces an undo operation.
     *
     * @param request The SubRequest to handle.
     */
    @Override
    public void commit(final SubRequest request) {
        if (request.getIndex() > 0) {
            //check specific context
            Variable newValue = request.getVariableBinding().getVariable();
            OID oid = request.getVariableBinding().getOid();
            if (variableBindings.getOrDefault(oid, newValue).getSyntax() == newValue.getSyntax()) {
                variableBindings.put(oid, newValue);
            } else {
                request.getStatus().setErrorStatus(SnmpConstants.SNMP_ERROR_INCONSISTENT_VALUE);
            }
        }
        request.getStatus().setPhaseComplete(true);
    }

    /**
     * If any ErrorStatus, except the implicit {@link org.snmp4j.PDU#noError PDU.noError},
     * has occurred during commit then the old values are written.
     *
     * @param request The SubRequest to handle.
     */
    @Override
    public void undo(final SubRequest request) {
        RequestStatus status = request.getStatus();
        if (request.getIndex() > 0) {
            if (request.getUndoValue() instanceof Variable) {
                variableBindings.put(request.getVariableBinding().getOid(), (Variable) request.getUndoValue());
            } else {
                variableBindings.remove(request.getVariableBinding().getOid());
            }
        }
        status.setPhaseComplete(true);
    }

    @Override
    public void cleanup(final SubRequest request) {
        // do nothing here
    }

    @Override
    public String toString() {
        return "MOGroup[" +
                "variableBindings=" + variableBindings +
                ", root=" + root +
                ", scope=" + scope +
                ']';
    }
}
