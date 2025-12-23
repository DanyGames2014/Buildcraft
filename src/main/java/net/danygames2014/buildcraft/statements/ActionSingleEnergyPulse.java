package net.danygames2014.buildcraft.statements;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.statement.ActionInternal;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.block.entity.pipe.statement.BCStatement;
import net.minecraft.client.resource.language.TranslationStorage;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;

public class ActionSingleEnergyPulse extends BCStatement implements ActionInternal {
    public ActionSingleEnergyPulse(){
        super(Buildcraft.NAMESPACE.id("pulsar.single"));
    }

    @Override
    public String getDescription() {
        return TranslationStorage.getInstance().get("gate.action.pulsar.single");
    }

    @Override
    public void registerTextures() {
        icon = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/action_single_pulsar"));
    }

    @Override
    public void actionActivate(StatementContainer source, StatementParameter[] parameters) {

    }
}
