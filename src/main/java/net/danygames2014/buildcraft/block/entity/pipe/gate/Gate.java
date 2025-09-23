package net.danygames2014.buildcraft.block.entity.pipe.gate;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultiset;
import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansion;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansionController;
import net.danygames2014.buildcraft.api.transport.statement.*;
import net.danygames2014.buildcraft.api.transport.statement.container.RedstoneStatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.container.SidedStatementContainer;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeWire;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public final class Gate implements net.danygames2014.buildcraft.api.transport.gate.Gate, SidedStatementContainer, RedstoneStatementContainer {
    public static int MAX_STATEMENTS = 8;
    public static int MAX_PARAMETERS = 3;

    public final PipeBlockEntity pipe;
    public final GateMaterial material;
    public final GateLogic logic;
    public final BiMap<GateExpansion, GateExpansionController> expansions = HashBiMap.create();

    public Statement[] triggers = new Statement[MAX_STATEMENTS];
    public StatementParameter[][] triggerParameters = new StatementParameter[MAX_STATEMENTS][MAX_PARAMETERS];

    public Statement[] actions = new Statement[MAX_STATEMENTS];
    public StatementParameter[][] actionParameters = new StatementParameter[MAX_STATEMENTS][MAX_PARAMETERS];

    public ActionActiveState[] actionsState = new ActionActiveState[MAX_STATEMENTS];
    public ArrayList<StatementSlot> activeActions = new ArrayList<StatementSlot>();

    public BitSet broadcastSignal = new BitSet(PipeWire.values().length);
    public BitSet prevBroadcastSignal = new BitSet(PipeWire.values().length);
    public int redstoneOutput = 0;
    public int redstoneOutputSide = 0;

    /**
     * this is the internal pulsing state of the gate. Intended to be managed
     * by the server side only, the client is supposed to be referring to the
     * state of the renderer, and update moveStage accordingly.
     */
    public boolean isPulsing = false;
    private Direction direction;

    private HashMultiset<Statement> statementCounts = HashMultiset.create();
    private int[] actionGroups = new int [] {0, 1, 2, 3, 4, 5, 6, 7};

    // / CONSTRUCTOR
    public Gate(PipeBlockEntity pipe, GateMaterial material, GateLogic logic, Direction direction) {
        this.pipe = pipe;
        this.material = material;
        this.logic = logic;
        this.direction = direction;

        Arrays.fill(actionsState, ActionActiveState.Deactivated);
    }

    public void setTrigger(int position, Statement trigger) {
        if (trigger != triggers[position]) {
            Arrays.fill(triggerParameters[position], null);
        }
        triggers[position] = trigger;
    }

    public Statement getTrigger(int position) {
        return triggers[position];
    }

    public void setAction(int position, Statement action) {
        // HUGE HACK! TODO - Remove in an API rewrite by adding
        // ways for actions to fix their state on removal.
//        if (actions[position] instanceof ActionValve && pipe != null && pipe.transporter != null) {
//            for (Direction side : Direction.values()) {
//                pipe.transporter.allowInput(side, true);
//                pipe.transporter.allowOutput(side, true);
//            }
//        }

        if (action != actions[position]) {
            for (int i = 0; i < actionParameters[position].length; i++) {
                actionParameters[position][i] = null;
            }
        }

        actions[position] = action;

        recalculateActionGroups();
    }

    public Statement getAction(int position) {
        return actions[position];
    }

    public void setTriggerParameter(int trigger, int param, StatementParameter p) {
        triggerParameters[trigger][param] = p;
    }

    public void setActionParameter(int action, int param, StatementParameter p) {
        actionParameters[action][param] = p;

        recalculateActionGroups();
    }

    public StatementParameter getTriggerParameter(int trigger, int param) {
        return triggerParameters[trigger][param];
    }

    public StatementParameter getActionParameter(int action, int param) {
        return actionParameters[action][param];
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void addGateExpansion(GateExpansion expansion) {
        if (!expansions.containsKey(expansion)) {
            expansions.put(expansion, expansion.makeController(pipe != null ? pipe : null));
        }
    }

    public void writeStatementsToNBT(NbtCompound data) {
        for (int i = 0; i < material.numSlots; ++i) {
            if (triggers[i] != null) {
                data.putString("trigger[" + i + "]", triggers[i].getIdentifier().toString());
            }

            if (actions[i] != null) {
                data.putString("action[" + i + "]", actions[i].getIdentifier().toString());
            }

            for (int j = 0; j < material.numTriggerParameters; ++j) {
                if (triggerParameters[i][j] != null) {
                    NbtCompound cpt = new NbtCompound();
                    cpt.putString("kind", triggerParameters[i][j].getIdentifier().toString());
                    triggerParameters[i][j].writeNBT(cpt);
                    data.put("triggerParameters[" + i + "][" + j + "]", cpt);
                }
            }

            for (int j = 0; j < material.numActionParameters; ++j) {
                if (actionParameters[i][j] != null) {
                    NbtCompound cpt = new NbtCompound();
                    cpt.putString("kind", actionParameters[i][j].getIdentifier().toString());
                    actionParameters[i][j].writeNBT(cpt);
                    data.put("actionParameters[" + i + "][" + j + "]", cpt);
                }
            }
        }
    }

    // / SAVING & LOADING
    public void writeToNBT(NbtCompound data) {
        data.putString("material", material.name());
        data.putString("logic", logic.name());
        data.putInt("direction", direction.ordinal());
        NbtList exList = new NbtList();
        for (GateExpansionController con : expansions.values()) {
            NbtCompound conNBT = new NbtCompound();
            conNBT.putString("type", con.getType().getIdentifier().toString());
            NbtCompound conData = new NbtCompound();
            con.writeNBT(conData);
            conNBT.put("data", conData);
            exList.add(conNBT);
        }
        data.put("expansions", exList);

        writeStatementsToNBT(data);

        for (PipeWire wire : PipeWire.values()) {
            data.putBoolean("wireState[" + wire.ordinal() + "]", broadcastSignal.get(wire.ordinal()));
        }

        data.putByte("redstoneOutput", (byte) redstoneOutput);
    }

    public void readStatementsFromNBT(NbtCompound data) {
        // Clear
        for (int i = 0; i < material.numSlots; ++i) {
            triggers[i] = null;
            actions[i] = null;
            for (int j = 0; j < material.numTriggerParameters; j++) {
                triggerParameters[i][j] = null;
            }
            for (int j = 0; j < material.numActionParameters; j++) {
                actionParameters[i][j] = null;
            }
        }

        // Read
        for (int i = 0; i < material.numSlots; ++i) {
            if (data.contains("trigger[" + i + "]")) {
                triggers[i] = StatementManager.statements.get(data.getString("trigger[" + i + "]"));
            }

            if (data.contains("action[" + i + "]")) {
                actions[i] = StatementManager.statements.get(data.getString("action[" + i + "]"));
            }

            // This is for legacy trigger loading
            if (data.contains("triggerParameters[" + i + "]")) {
                triggerParameters[i][0] = new StatementParameterItemStack();
                triggerParameters[i][0].readNBT(data.getCompound("triggerParameters[" + i + "]"));
            }

            for (int j = 0; j < material.numTriggerParameters; ++j) {
                if (data.contains("triggerParameters[" + i + "][" + j + "]")) {
                    NbtCompound cpt = data.getCompound("triggerParameters[" + i + "][" + j + "]");
                    triggerParameters[i][j] = StatementManager.createParameter(cpt.getString("kind"));
                    triggerParameters[i][j].readNBT(cpt);
                }
            }

            for (int j = 0; j < material.numActionParameters; ++j) {
                if (data.contains("actionParameters[" + i + "][" + j + "]")) {
                    NbtCompound cpt = data.getCompound("actionParameters[" + i + "][" + j + "]");
                    actionParameters[i][j] = StatementManager.createParameter(cpt.getString("kind"));
                    actionParameters[i][j].readNBT(cpt);
                }
            }
        }

        recalculateActionGroups();
    }

    public boolean verifyGateStatements() {
        List<Statement> triggerList = getAllValidTriggers();
        List<Statement> actionList = getAllValidActions();
        boolean warning = false;

        for (int i = 0; i < MAX_STATEMENTS; ++i) {
            if ((triggers[i] != null || actions[i] != null) && i >= material.numSlots) {
                triggers[i] = null;
                actions[i] = null;
                warning = true;
                continue;
            }

            if (triggers[i] != null) {
                if (!triggerList.contains(triggers[i]) ||
                            triggers[i].minParameters() > material.numTriggerParameters) {
                    triggers[i] = null;
                    warning = true;
                }
            }

            if (actions[i] != null) {
                if (!actionList.contains(actions[i]) ||
                            actions[i].minParameters() > material.numActionParameters) {
                    actions[i] = null;
                    warning = true;
                }
            }
        }

        if (warning) {
            recalculateActionGroups();
        }

        return !warning;
    }

    public void readNBT(NbtCompound data) {
        readStatementsFromNBT(data);

        for (PipeWire wire : PipeWire.values()) {
            broadcastSignal.set(wire.ordinal(), data.getBoolean("wireState[" + wire.ordinal() + "]"));
        }

        redstoneOutput = data.getByte("redstoneOutput");
    }

    // GUI // TODO: implement gui
    public void openGui(PlayerEntity player) {
        if (!player.world.isRemote) {
//            player.openGui(BuildCraftTransport.instance, GuiIds.GATES, pipe.container.getWorldObj(), pipe.container.xCoord, pipe.container.yCoord, pipe.container.zCoord);
//            ((ContainerGateInterface) player.openContainer).setGate(direction.ordinal());
        }
    }

    // UPDATING
    public void tick() {
        for (GateExpansionController expansion : expansions.values()) {
            expansion.tick(this);
        }
    }

//    public ItemStack getGateItem() {
//        return ItemGate.makeGateItem(this);
//    }
//
//    public void dropGate() {
//        pipe.dropItem(getGateItem());
//    }

    public void resetGate() {
        if (redstoneOutput != 0) {
            redstoneOutput = 0;
            pipe.updateNeighbors(true);
        }
    }

    public boolean isGateActive() {
        for (ActionActiveState state : actionsState) {
            if (state == ActionActiveState.Activated) {
                return true;
            }
        }

        return false;
    }

    public boolean isGatePulsing() {
        return isPulsing;
    }

    public int getRedstoneOutput(){
        return redstoneOutput;
    }

    public int getSidedRedstoneOutput() {
        return redstoneOutputSide;
    }

    public void startResolution() {
        for (GateExpansionController expansion : expansions.values()) {
            expansion.startResolution();
        }
    }

    public void resolveActions() {
        int oldRedstoneOutput = redstoneOutput;
        redstoneOutput = 0;

        int oldRedstoneOutputSide = redstoneOutputSide;
        redstoneOutputSide = 0;

        boolean wasActive = activeActions.size() > 0;

        BitSet temp = prevBroadcastSignal;
        temp.clear();
        prevBroadcastSignal = broadcastSignal;
        broadcastSignal = temp;

        // Tell the gate to prepare for resolving actions. (Disable pulser)
        startResolution();

        // Computes the actions depending on the triggers
        for (int it = 0; it < MAX_STATEMENTS; ++it) {
            actionsState[it] = ActionActiveState.Deactivated;

            Statement trigger = triggers[it];
            StatementParameter[] parameter = triggerParameters[it];

            if (trigger != null) {
                if (isTriggerActive(trigger, parameter)) {
                    actionsState[it] = ActionActiveState.Partial;
                }
            }
        }

        activeActions.clear();

        for (int it = 0; it < MAX_STATEMENTS; ++it) {
            boolean allActive = true;
            boolean oneActive = false;

            if (actions[it] == null) {
                continue;
            }

            for (int j = 0; j < MAX_STATEMENTS; ++j) {
                if (actionGroups[j] == it) {
                    if (actionsState[j] != ActionActiveState.Partial) {
                        allActive = false;
                    } else {
                        oneActive = true;
                    }
                }
            }

            if ((logic == GateLogic.AND && allActive && oneActive) || (logic == GateLogic.OR && oneActive)) {
                if (logic == GateLogic.AND) {
                    for (int j = 0; j < MAX_STATEMENTS; ++j) {
                        if (actionGroups[j] == it) {
                            actionsState[j] = ActionActiveState.Activated;
                        }
                    }
                }

                StatementSlot slot = new StatementSlot();
                slot.statement = actions[it];
                slot.parameters = actionParameters[it];
                activeActions.add(slot);
            }

            if (logic == GateLogic.OR && actionsState[it] == ActionActiveState.Partial) {
                actionsState[it] = ActionActiveState.Activated;
            }
        }

        statementCounts.clear();

        for (int it = 0; it < MAX_STATEMENTS; ++it) {
            if (actionsState[it] == ActionActiveState.Activated) {
                statementCounts.add(actions[it], 1);
            }
        }

        // Activate the actions
        for (StatementSlot slot : activeActions) {
            Statement action = slot.statement;
            if (action instanceof ActionInternal) {
                ((ActionInternal) action).actionActivate(this, slot.parameters);
            } else if (action instanceof ActionExternal) {
                for (Direction side: Direction.values()) {
                    BlockEntity blockEntity = this.getPipe().getBlockEntity(side);
                    if (blockEntity != null) {
                        ((ActionExternal) action).actionActivate(blockEntity, side, this, slot.parameters);
                    }
                }
            } else {
                continue;
            }

            // Custom gate actions take precedence over defaults.
            if (resolveAction(action)) {
                continue;
            }

            for (Direction side : Direction.values()) {
                BlockEntity blockEntity = pipe.getBlockEntity(side);
                if (blockEntity instanceof ActionReceptor) {
                    ActionReceptor recept = (ActionReceptor) blockEntity;
                    recept.actionActivated(action, slot.parameters);
                }
            }
        }

        pipe.actionsActivated(activeActions);

        if (oldRedstoneOutput != redstoneOutput || oldRedstoneOutputSide != redstoneOutputSide) {
            pipe.updateNeighbors(true);
        }

        if (!prevBroadcastSignal.equals(broadcastSignal)) {
            pipe.updateSignalState();
        }

        boolean isActive = activeActions.size() > 0;

        if (wasActive != isActive) {
            pipe.scheduleRenderUpdate();
        }
    }

    public boolean resolveAction(Statement action) {
        for (GateExpansionController expansion : expansions.values()) {
            if (expansion.resolveAction(action, statementCounts.count(action))) {
                return true;
            }
        }
        return false;
    }

    public boolean isTriggerActive(Statement trigger, StatementParameter[] parameters) {
        if (trigger == null) {
            return false;
        }

        if (trigger instanceof TriggerInternal) {
            if (((TriggerInternal) trigger).isTriggerActive(this, parameters)) {
                return true;
            }
        } else if (trigger instanceof TriggerExternal) {
            for (Direction side: Direction.values()) {
                BlockEntity blockEntity = this.getPipe().getBlockEntity(side);
                if (blockEntity != null) {
                    if (blockEntity instanceof TriggerExternalOverride) {
                        TriggerExternalOverride.Result result = ((TriggerExternalOverride) blockEntity).override(side, this, parameters);
                        if (result == TriggerExternalOverride.Result.TRUE) {
                            return true;
                        } else if (result == TriggerExternalOverride.Result.FALSE) {
                            continue;
                        }
                    }
                    if (((TriggerExternal) trigger).isTriggerActive(blockEntity, side, this, parameters)) {
                        return true;
                    }
                }
            }
        }

        // TODO: This can probably be refactored with regular triggers instead
        // of yet another system.
        for (GateExpansionController expansion : expansions.values()) {
            if (expansion.isTriggerActive(trigger, parameters)) {
                return true;
            }
        }

        return false;
    }

    // TRIGGERS
    public void addTriggers(List<TriggerInternal> list) {
        for (PipeWire wire : PipeWire.values()) {
            if (pipe.wireSet[wire.ordinal()] && wire.ordinal() < 4) {
                list.add(Buildcraft.triggerPipeWireActive[wire.ordinal()]);
                list.add(Buildcraft.triggerPipeWireInactive[wire.ordinal()]);
            }
        }

        for (GateExpansionController expansion : expansions.values()) {
            expansion.addTriggers(list);
        }
    }

    public List<Statement> getAllValidTriggers() {
        ArrayList<Statement> allTriggers = new ArrayList<Statement>(64);
        allTriggers.addAll(StatementManager.getInternalTriggers(this));

        for (Direction o : Direction.values()) {
            BlockEntity tile = pipe.getBlockEntity(o);
            allTriggers.addAll(StatementManager.getExternalTriggers(o, tile));
        }

        return allTriggers;
    }

    // ACTIONS
    public void addActions(List<ActionInternal> list) {
        for (PipeWire wire : PipeWire.values()) {
            if (pipe.wireSet[wire.ordinal()] && wire.ordinal() < 4) {
                list.add(Buildcraft.actionPipeWire[wire.ordinal()]);
            }
        }

        for (GateExpansionController expansion : expansions.values()) {
            expansion.addActions(list);
        }
    }

    public List<Statement> getAllValidActions() {
        ArrayList<Statement> allActions = new ArrayList<>(64);
        allActions.addAll(StatementManager.getInternalActions(this));

        for (Direction o : Direction.values()) {
            BlockEntity tile = pipe.getBlockEntity(o);
            allActions.addAll(StatementManager.getExternalActions(o, tile));
        }

        return allActions;
    }

    //@Override TODO
    public void setPulsing(boolean pulsing) {
        if (pulsing != isPulsing) {
            isPulsing = pulsing;
            pipe.scheduleRenderUpdate();
        }
    }

    private void recalculateActionGroups() {
        for (int i = 0; i < MAX_STATEMENTS; ++i) {
            actionGroups[i] = i;

            for (int j = i - 1; j >= 0; --j) {
                if (actions[i] != null && actions[j] != null
                            && actions[i].getIdentifier().equals(actions[j].getIdentifier())) {
                    boolean sameParams = true;

                    for (int p = 0; p < MAX_PARAMETERS; ++p) {
                        if ((actionParameters[i][p] != null && actionParameters[j][p] == null)
                                    || (actionParameters[i][p] == null && actionParameters[j][p] != null)
                                    || (actionParameters[i][p] != null
                                                && actionParameters[j][p] != null
                                                && !actionParameters[i][p].equals(actionParameters[j][p]))) {
                            sameParams = false;
                            break;
                        }
                    }

                    if (sameParams) {
                        actionGroups[i] = j;
                    }
                }
            }
        }
    }

    public void broadcastSignal(PipeWire color) {
        broadcastSignal.set(color.ordinal());
    }

    @Override
    public PipeBlockEntity getPipe() {
        return pipe;
    }

    @Override
    public List<Statement> getTriggers() {
        return Arrays.asList(triggers).subList(0, material.numSlots);
    }

    @Override
    public List<Statement> getActions() {
        return Arrays.asList(actions).subList(0, material.numSlots);
    }

    @Override
    public List<StatementSlot> getActiveActions() {
        return activeActions;
    }

    @Override
    public List<StatementParameter> getTriggerParameters(int index) {
        if (index < 0 || index >= material.numSlots) {
            return null;
        }
        return Arrays.asList(triggerParameters[index]).subList(0, material.numTriggerParameters);
    }

    @Override
    public List<StatementParameter> getActionParameters(int index) {
        if (index < 0 || index >= material.numSlots) {
            return null;
        }
        return Arrays.asList(actionParameters[index]).subList(0, material.numActionParameters);
    }

    @Override
    public int getRedstoneInput(@Nullable Direction side) {
        return side == null ? pipe.redstoneInput : pipe.redstoneInputSide[side.ordinal()];
    }

    public void setRedstoneOutput(boolean sideOnly, int value) {
        redstoneOutputSide = value;

        if (!sideOnly) {
            redstoneOutput = value;
        }
    }

    @Override
    public boolean setRedstoneOutput(@Nullable Direction side, int value) {
        if (side != this.getSide() && side != null) {
            return false;
        }

        setRedstoneOutput(side != null, value);
        return true;
    }

    @Override
    public Direction getSide() {
        return this.direction;
    }

    @Override
    public BlockEntity getBlockEntity() {
        return pipe;
    }
}
