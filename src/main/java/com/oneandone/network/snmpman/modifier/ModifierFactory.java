package com.oneandone.network.snmpman.modifier;

import com.oneandone.network.snmpman.configuration.device.AbstractModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlTransient;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * This factory creates a {@link VariableModifier} by the class name found in {@link #getModifierClass()} in the {@link #create()} function.
 *
 * @author Johann Böhler
 */
@XmlTransient
public class ModifierFactory extends AbstractModifier {

    /**
     * The logging instance for this class.
     */
    private static final transient Logger LOG = LoggerFactory.getLogger(ModifierFactory.class);

    /**
     * This method will return an instance of a {@link VariableModifier} for the class name defined in {@link #modifierClass}.
     * <p/>
     * If such class was not found, or the required constructor could not be found {@code null} will be returned.
     *
     * @return a variable modifier for the defined class name or {@code null}
     */
    @SuppressWarnings("unchecked")
    public VariableModifier create() {
        try {
            final Class<VariableModifier> variableModifierClass = (Class<VariableModifier>) Class.forName(this.getModifierClass());
            final Constructor<VariableModifier> constructor = variableModifierClass.getDeclaredConstructor(List.class);
            return constructor.newInstance(this.getParam());
        } catch (final ClassNotFoundException e) {
            LOG.error("could not find modifier class " + this.getModifierClass(), e);
        } catch (final NoSuchMethodException e) {
            LOG.error("could not find constructor with the specified parameters for modifier class " + this.modifierClass, e);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            LOG.error("could not create an instance of the modifier " + this.modifierClass, e);
        }
        return null;
    }

}
