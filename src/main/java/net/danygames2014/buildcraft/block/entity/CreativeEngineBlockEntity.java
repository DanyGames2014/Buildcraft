package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.api.energy.EnergyStage;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import javax.annotation.Nullable;

public class CreativeEngineBlockEntity  extends BaseEngineBlockEntity{
    private PowerMode powerMode = PowerMode.M2;

    @Override
    public boolean isBurning() {
        return isRedstonePowered;
    }

    @Override
    public int getBurnTime() {
        return 0;
    }

    @Override
    public int getMaxBurnTime() {
        return 0;
    }

    @Override
    public double getMaxEnergy() {
        return calculateCurrentOutput();
    }

    @Override
    public double getMaxEnergyReceived() {
        return calculateCurrentOutput();
    }

    @Override
    public double getMaxEnergyExtracted() {
        return calculateCurrentOutput();
    }

    @Override
    public double getCurrentEnergyOutput() {
        return calculateCurrentOutput();
    }

    @Override
    public float getExplosionRange() {
        return 0;
    }

    @Override
    public EnergyStage calculateEnergyStage() {
        return EnergyStage.BLUE;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        powerMode = PowerMode.fromId(nbt.getByte("mode"));
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putByte("mode", (byte) powerMode.ordinal());
    }

    @Override
    public float getPistonSpeed() {
        return 0.02F * (powerMode.ordinal() + 1);
    }

    @Override
    protected void engineUpdate() {
        super.engineUpdate();

        if(isRedstonePowered){
            addEnergy(calculateCurrentOutput());
        }
    }

    public int calculateCurrentOutput(){
        return powerMode.maxPower;
    }

    public void switchPowerMode(boolean reverse, @Nullable PlayerEntity player){
        powerMode = reverse ? powerMode.getPrevious() : powerMode.getNext();
        energy = 0;
        if(player != null){
            TranslationStorage translationStorage = TranslationStorage.getInstance();
            player.sendMessage(String.format(translationStorage.get("chat.engine.creative.mode"), powerMode.maxPower));
        }
    }
}
