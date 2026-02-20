package net.danygames2014.buildcraft.api.transport.statement;

import net.danygames2014.buildcraft.Buildcraft;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.*;

public class StatementManager {
    public static Map<Identifier, Statement> statements = new HashMap<>();
    public static Map<Identifier, Class<? extends StatementParameter>> parameters = new HashMap<>();
    private static List<TriggerProvider> triggerProviders = new LinkedList<>();
    private static List<ActionProvider> actionProviders = new LinkedList<>();

    /**
     * Deactivate constructor
     */
    private StatementManager() {
    }

    public static void registerTriggerProvider(TriggerProvider provider) {
        if (provider != null && !triggerProviders.contains(provider)) {
            triggerProviders.add(provider);
        }
    }

    public static void registerActionProvider(ActionProvider provider) {
        if (provider != null && !actionProviders.contains(provider)) {
            actionProviders.add(provider);
        }
    }

    public static void registerStatement(Statement statement) {
        statements.put(statement.getIdentifier(), statement);
    }

    public static void registerParameterClass(Class<? extends StatementParameter> param) {
        parameters.put(createParameter(param).getIdentifier(), param);
    }

    @Deprecated
    public static void registerParameterClass(Identifier identifier, Class<? extends StatementParameter> param) {
        parameters.put(identifier, param);
    }

    public static List<TriggerExternal> getExternalTriggers(Direction side, BlockEntity blockEntity) {
        List<TriggerExternal> result;

        if (blockEntity instanceof OverrideDefaultStatements) {
            result = ((OverrideDefaultStatements) blockEntity).overrideTriggers();
            if (result != null) {
                return result;
            }
        }

        result = new LinkedList<>();

        for (TriggerProvider provider : triggerProviders) {
            Collection<TriggerExternal> toAdd = provider.getExternalTriggers(side, blockEntity);

            if (toAdd != null) {
                for (TriggerExternal t : toAdd) {
                    if (!result.contains(t)) {
                        result.add(t);
                    }
                }
            }
        }

        return result;
    }

    public static List<ActionExternal> getExternalActions(Direction side, BlockEntity blockEntity) {
        List<ActionExternal> result = new LinkedList<>();

        if (blockEntity instanceof OverrideDefaultStatements) {
            result = ((OverrideDefaultStatements) blockEntity).overrideActions();
            if (result != null) {
                return result;
            } else {
                result = new LinkedList<>();
            }
        }

        for (ActionProvider provider : actionProviders) {
            Collection<ActionExternal> toAdd = provider.getExternalActions(side, blockEntity);

            if (toAdd != null) {
                for (ActionExternal t : toAdd) {
                    if (!result.contains(t)) {
                        result.add(t);
                    }
                }
            }
        }

        return result;
    }

    public static List<TriggerInternal> getInternalTriggers(StatementContainer container) {
        List<TriggerInternal> result = new LinkedList<>();

        for (TriggerProvider provider : triggerProviders) {
            Collection<TriggerInternal> toAdd = provider.getInternalTriggers(container);

            if (toAdd != null) {
                for (TriggerInternal t : toAdd) {
                    if (!result.contains(t)) {
                        result.add(t);
                    }
                }
            }
        }

        return result;
    }

    public static List<ActionInternal> getInternalActions(StatementContainer container) {
        List<ActionInternal> result = new LinkedList<>();

        for (ActionProvider provider : actionProviders) {
            Collection<ActionInternal> toAdd = provider.getInternalActions(container);

            if (toAdd != null) {
                for (ActionInternal t : toAdd) {
                    if (!result.contains(t)) {
                        result.add(t);
                    }
                }
            }
        }

        return result;
    }

    public static StatementParameter createParameter(String kind) {
        // TODO: That hashmap is Identifier -> Statement, not String -> Statement. -Dany
        return createParameter(parameters.get(kind));
    }

    private static StatementParameter createParameter(Class<? extends StatementParameter> param) {
        try {
            return param.newInstance(); // TODO: Switch to either a factory pattern or getDeclaredConstructor
        } catch (InstantiationException | IllegalAccessException e) {
            Buildcraft.LOGGER.error("Failed to create parameter " + param.getName(), e);
        }

        return null;
    }

    /**
     * Generally, this function should be called by every mod implementing
     * the Statements API ***as a container*** (that is, adding its own gates)
     * on the client side from a given Item of choice.
     */
    @Environment(EnvType.CLIENT)
    public static void registerTextures() {
        for (Statement statement : statements.values()) {
            statement.registerTextures();
        }

        for (Class<? extends StatementParameter> parameter : parameters.values()) {
            createParameter(parameter).registerTextures();
        }
    }
}
