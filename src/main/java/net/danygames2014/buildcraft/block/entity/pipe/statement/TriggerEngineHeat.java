package net.danygames2014.buildcraft.block.entity.pipe.statement;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.energy.EnergyStage;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.api.transport.statement.TriggerExternal;
import net.danygames2014.buildcraft.block.entity.BaseEngineBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.TranslationStorage;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Locale;

public class TriggerEngineHeat extends BCStatement implements TriggerExternal {
    public EnergyStage stage;

    public TriggerEngineHeat(EnergyStage stage){
        super(Buildcraft.NAMESPACE.id("engine.stage." + stage.name().toLowerCase(Locale.ENGLISH)));
        this.stage = stage;
    }

    @Override
    public String getDescription() {
        return TranslationStorage.getInstance().get("gate.buildcraft.trigger.engine." + stage.name().toLowerCase(Locale.ENGLISH));
    }

    @Override
    public boolean isTriggerActive(BlockEntity target, Direction side, StatementContainer source, StatementParameter[] parameters) {
        if(target instanceof BaseEngineBlockEntity engine){
            return engine.getEnergyStage() == stage;
        }

        return false;
    }

    @Override
    public void registerTextures() {
        icon = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_engineheat_" + stage.name().toLowerCase(Locale.ENGLISH)));
    }
}
