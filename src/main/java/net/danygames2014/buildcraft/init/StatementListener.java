package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.api.energy.EnergyStage;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansions;
import net.danygames2014.buildcraft.api.transport.statement.*;
import net.danygames2014.buildcraft.block.entity.pipe.PipeWire;
import net.danygames2014.buildcraft.block.entity.pipe.gate.expansion.GateExpansionLightSensor;
import net.danygames2014.buildcraft.block.entity.pipe.gate.expansion.GateExpansionPulsar;
import net.danygames2014.buildcraft.block.entity.pipe.gate.expansion.GateExpansionRedstoneFader;
import net.danygames2014.buildcraft.block.entity.pipe.gate.expansion.GateExpansionTimer;
import net.danygames2014.buildcraft.block.entity.pipe.parameter.ActionParameterSignal;
import net.danygames2014.buildcraft.block.entity.pipe.statement.*;
import net.danygames2014.buildcraft.event.StatementRegisterEvent;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.math.Direction;

public class StatementListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;
    
    public static TriggerInternal triggerRedstoneActive;
    public static TriggerInternal triggerRedstoneInactive;
    public static TriggerInternal[] triggerPipeWireActive = new TriggerInternal[PipeWire.values().length];
    public static TriggerInternal[] triggerPipeWireInactive = new TriggerInternal[PipeWire.values().length];
    public static TriggerInternal[] triggerPipe = new TriggerInternal[TriggerPipeContents.PipeContents.values().length];
    public static TriggerInternal[] triggerTimer = new TriggerInternal[TriggerClockTimer.Time.VALUES.length];
    public static TriggerInternal[] triggerRedstoneLevel = new TriggerInternal[15];
    public static TriggerInternal triggerLightSensorBright;
    public static TriggerInternal triggerLightSensorDark;

    public static TriggerExternal triggerEmptyInventory;
    public static TriggerExternal triggerContainsInventory;
    public static TriggerExternal triggerSpaceInventory;
    public static TriggerExternal triggerFullInventory;
    public static TriggerExternal triggerInventoryBelow25;
    public static TriggerExternal triggerInventoryBelow50;
    public static TriggerExternal triggerInventoryBelow75;
    public static TriggerExternal triggerEmptyFluid;
    public static TriggerExternal triggerContainsFluid;
    public static TriggerExternal triggerSpaceFluid;
    public static TriggerExternal triggerFullFluid;
    public static TriggerExternal triggerFluidContainerBelow25;
    public static TriggerExternal triggerFluidContainerBelow50;
    public static TriggerExternal triggerFluidContainerBelow75;
    public static TriggerExternal triggerBlueEngineHeat;
    public static TriggerExternal triggerGreenEngineHeat;
    public static TriggerExternal triggerYellowEngineHeat;
    public static TriggerExternal triggerRedEngineHeat;
    public static TriggerExternal triggerEngineOverheat;
    public static TriggerExternal triggerMachineActive;
    public static TriggerExternal triggerMachineInactive;
    public static TriggerExternal triggerEnergyHigh;
    public static TriggerExternal triggerEnergyLow;
    
    public static ActionInternal[] actionPipeWire = new ActionSignalOutput[PipeWire.values().length];
    public static ActionInternal actionEnergyPulser;
    public static ActionInternal actionSingleEnergyPulse;
    public static ActionInternal actionRedstone;
    public static ActionInternal[] actionRedstoneLevel = new ActionInternal[15];
    public static ActionInternal[] actionPipeDirection = new ActionInternal[16];
    
    @EventListener
    public void registerStatements(StatementRegisterEvent event) {
        for (PipeWire wire : PipeWire.values()) {
            triggerPipeWireActive[wire.ordinal()] = new TriggerPipeSignal(true, wire);
            triggerPipeWireInactive[wire.ordinal()] = new TriggerPipeSignal(false, wire);
            actionPipeWire[wire.ordinal()] = new ActionSignalOutput(wire);
        }

        actionEnergyPulser = new ActionEnergyPulsar();
        actionSingleEnergyPulse = new ActionSingleEnergyPulse();
        actionRedstone = new ActionRedstoneOutput();

        for(Direction direction : Direction.values()){
            actionPipeDirection[direction.ordinal()] = new ActionPipeDirection(direction);
        }

        for (TriggerPipeContents.PipeContents kind : TriggerPipeContents.PipeContents.values()) {
            triggerPipe[kind.ordinal()] = new TriggerPipeContents(kind);
        }

        for (TriggerClockTimer.Time time : TriggerClockTimer.Time.VALUES) {
            triggerTimer[time.ordinal()] = new TriggerClockTimer(time);
        }

        triggerRedstoneActive = new TriggerRedstoneInput(true);
        triggerRedstoneInactive = new TriggerRedstoneInput(false);

        for (int level = 0; level < triggerRedstoneLevel.length; level++) {
            triggerRedstoneLevel[level] = new TriggerRedstoneFaderInput(level + 1);
            actionRedstoneLevel[level] = new ActionRedstoneFaderOutput(level + 1);
        }

        triggerLightSensorBright = new TriggerLightSensor(true);
        triggerLightSensorDark = new TriggerLightSensor(false);

        triggerEmptyInventory = new TriggerInventory(TriggerInventory.State.Empty);
        triggerContainsInventory = new TriggerInventory(TriggerInventory.State.Contains);
        triggerSpaceInventory = new TriggerInventory(TriggerInventory.State.Space);
        triggerFullInventory = new TriggerInventory(TriggerInventory.State.Full);
        triggerInventoryBelow25 = new TriggerInventoryLevel(TriggerInventoryLevel.TriggerType.BELOW25);
        triggerInventoryBelow50 = new TriggerInventoryLevel(TriggerInventoryLevel.TriggerType.BELOW50);
        triggerInventoryBelow75 = new TriggerInventoryLevel(TriggerInventoryLevel.TriggerType.BELOW75);

        triggerEmptyFluid = new TriggerFluidContainer(TriggerFluidContainer.State.Empty);
        triggerContainsFluid = new TriggerFluidContainer(TriggerFluidContainer.State.Contains);
        triggerSpaceFluid = new TriggerFluidContainer(TriggerFluidContainer.State.Space);
        triggerFullFluid = new TriggerFluidContainer(TriggerFluidContainer.State.Full);
        triggerFluidContainerBelow25 = new TriggerFluidContainerLevel(TriggerFluidContainerLevel.TriggerType.BELOW25);
        triggerFluidContainerBelow50 = new TriggerFluidContainerLevel(TriggerFluidContainerLevel.TriggerType.BELOW50);
        triggerFluidContainerBelow75 = new TriggerFluidContainerLevel(TriggerFluidContainerLevel.TriggerType.BELOW75);

        triggerBlueEngineHeat = new TriggerEngineHeat(EnergyStage.BLUE);
        triggerGreenEngineHeat = new TriggerEngineHeat(EnergyStage.GREEN);
        triggerYellowEngineHeat = new TriggerEngineHeat(EnergyStage.YELLOW);
        triggerRedEngineHeat = new TriggerEngineHeat(EnergyStage.RED);
        triggerEngineOverheat = new TriggerEngineHeat(EnergyStage.OVERHEAT);

        triggerMachineActive = new TriggerMachine(true);
        triggerMachineInactive = new TriggerMachine(false);

        triggerEnergyHigh = new TriggerEnergy(true);
        triggerEnergyLow = new TriggerEnergy(false);

        StatementManager.registerTriggerProvider(new PipeTriggerProvider());
        StatementManager.registerTriggerProvider(new DefaultTriggerProvider());

        StatementManager.registerActionProvider(new PipeActionProvider());
        StatementManager.registerActionProvider(new DefaultActionProvider());

        StatementManager.registerParameterClass(TriggerParameterSignal::new);
        StatementManager.registerParameterClass(StatementParameterRedstoneGateSideOnly::new);
        StatementManager.registerParameterClass(StatementParameterItemStack::new);
        StatementManager.registerParameterClass(ActionParameterSignal::new);

        GateExpansions.registerExpansion(GateExpansionPulsar.INSTANCE);
        GateExpansions.registerExpansion(GateExpansionTimer.INSTANCE);
        GateExpansions.registerExpansion(GateExpansionRedstoneFader.INSTANCE);
        GateExpansions.registerExpansion(GateExpansionLightSensor.INSTANCE);
    }
}
