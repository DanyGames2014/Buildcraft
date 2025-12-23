package net.danygames2014.buildcraft.statements;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.statement.ActionInternal;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.block.entity.pipe.statement.BCStatement;
import net.minecraft.client.resource.language.TranslationStorage;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;

public class ActionEnergyPulsar extends BCStatement implements ActionInternal {
    public ActionEnergyPulsar(){
        super(Buildcraft.NAMESPACE.id("pulsar.constant"));
    }

    @Override
    public String getDescription() {
        return TranslationStorage.getInstance().get("gate.action.pulsar.constant");
    }

    @Override
    public void registerTextures() {
        icon = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/action_pulsar"));
    }

    @Override
    public void actionActivate(StatementContainer source, StatementParameter[] parameters) {

    }
}
