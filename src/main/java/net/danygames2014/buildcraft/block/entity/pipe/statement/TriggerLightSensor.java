package net.danygames2014.buildcraft.block.entity.pipe.statement;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.core.Position;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.api.transport.statement.TriggerInternal;
import net.danygames2014.buildcraft.api.transport.statement.container.SidedStatementContainer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.TranslationStorage;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;

public class TriggerLightSensor extends BCStatement implements TriggerInternal {
    private final boolean bright;

    public TriggerLightSensor(boolean bright) {
        super(Buildcraft.NAMESPACE.id("light_" + (bright ? "bright" : "dark")));
        this.bright = bright;
    }

    @Override
    public String getDescription() {
        return TranslationStorage.getInstance().get("gate.buildcraft.trigger.light." + (bright ? "bright" : "dark"));
    }

    @Override
    public boolean isTriggerActive(StatementContainer source, StatementParameter[] parameters) {
        BlockEntity blockEntity = source.getBlockEntity();
        Position pos = new Position(blockEntity);
        pos.orientation = ((SidedStatementContainer) source).getSide();
        pos.moveForwards(1.0);

        int lightLevel = blockEntity.world.getLightLevel((int) pos.x, (int) pos.y, (int) pos.z);

        return (lightLevel < 8) ^ bright;
    }

    @Override
    public void registerTextures() {
        icon = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_light_" + (bright ? "bright" : "dark")));
    }
}
