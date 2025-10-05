package net.danygames2014.buildcraft.screen.handler;

import net.danygames2014.buildcraft.api.transport.statement.Statement;
import net.danygames2014.buildcraft.api.transport.statement.StatementManager;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.gate.ActionActiveState;
import net.danygames2014.buildcraft.block.entity.pipe.gate.Gate;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateDefinition;
import net.danygames2014.buildcraft.packet.CommandPacket;
import net.danygames2014.buildcraft.packet.command.CommandReceiver;
import net.danygames2014.buildcraft.packet.command.CommandWriter;
import net.danygames2014.buildcraft.screen.GateInterfaceScreen;
import net.fabricmc.api.EnvType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.packet.Packet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class GateInterfaceScreenHandler extends ScreenHandler implements CommandReceiver {
    public ActionActiveState[] actionsState = new ActionActiveState[8];
    public GateInterfaceScreen gateCallback;

    Inventory playerInventory;
    private final PipeBlockEntity pipe;
    private Gate gate;
    private final NavigableSet<Statement> potentialTriggers = new TreeSet<>(Comparator.comparing(Statement::getIdentifier));

    private final NavigableSet<Statement> potentialActions = new TreeSet<>(Comparator.comparing(Statement::getIdentifier));
    private boolean isSynchronized = false;
    private boolean isNetInitialized = false;
    private int lastTriggerState = 0;

    public GateInterfaceScreenHandler(Inventory playerInventory, PipeBlockEntity pipe){
        for (int i = 0; i < actionsState.length; ++i) {
            actionsState[i] = ActionActiveState.Deactivated;
        }

        this.pipe = pipe;
        this.playerInventory = playerInventory;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 0));
            }
        }

        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(playerInventory, x, 8 + x * 18, 0));
        }
    }

    public void init() {
        if (gate == null) {
            return;
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                getSlot(x + y * 9).y = gate.material.guiHeight - 84 + y * 18;
            }
        }

        for (int x = 0; x < 9; x++) {
            getSlot(x + 27).y = gate.material.guiHeight - 26;
        }

        // Do not attempt to create a list of potential actions and triggers on
        // the client.
        if (!pipe.world.isRemote) {
            potentialTriggers.addAll(gate.getAllValidTriggers());
            potentialActions.addAll(gate.getAllValidActions());

            Iterator<Statement> it = potentialTriggers.iterator();

            while (it.hasNext()) {
                Statement trigger = it.next();

                if (trigger.minParameters() > gate.material.numTriggerParameters) {
                    it.remove();
                }
            }

            it = potentialActions.iterator();

            while (it.hasNext()) {
                Statement action = it.next();

                if (action.minParameters() > gate.material.numActionParameters) {
                    it.remove();
                }
            }
        }
        if (gateCallback != null) {
            gateCallback.setGate(gate);
        }
    }

    private static String[] statementsToStrings(Collection<Statement> statements) {
        final int size = statements.size();
        String[] array = new String[size];
        int pos = 0;
        for (Statement statement : statements) {
            array[pos++] = statement.getIdentifier().toString();
        }
        return array;
    }

    private static void stringsToStatements(Collection<Statement> statements, String[] strings) {
        statements.clear();
        for (String id : strings) {
            statements.add(StatementManager.statements.get(id));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return gate != null;
    }

    /**
     * CLIENT SIDE *
     */
    /**
     * Marks client side gate container as needing to be synchronized with the
     * server.
     */
    public void markDirty() {
        isSynchronized = false;
    }

    /**
     * Initializes the list of triggers and actions on the gate and
     * (re-)requests the current selection on the gate if needed.
     */
    public void synchronize() {
        if (!isNetInitialized && pipe.isRemoved()) {
            isNetInitialized = true;
            PacketHelper.send(new CommandPacket(this, "initRequest", null));
        }

        if (!isSynchronized && pipe.world.isRemote && gate != null) {
            isSynchronized = true;
            PacketHelper.send(new CommandPacket(this, "selectionRequest", null));
        }
    }

    public void updateProgressBar(int id, int state) {
        if (id == 0 /* Action state update */) {
            for (int i = 0; i < 8; i++) {
                /* Bit mask of triggers */
                actionsState[i] = ActionActiveState.values()[(state >> (i * 2)) & 0x03];
            }
        }
    }

    /**
     * SERVER SIDE *
     */
    private int calculateTriggerState() {
        if (gate == null) {
            return 0;
        }

        int state = 0;

        for (int i = 0; i < actionsState.length; i++) {
            actionsState[i] = getActionState(i);
            state |= (actionsState[i].ordinal() & 0x03) << i * 2;
        }

        return state;
    }
// TODO: decide if this will need to be used

//    @Override
//    public void detectAndSendChanges() {
//        super.detectAndSendChanges();
//
//        int state = calculateTriggerState();
//
//        if (state != lastTriggerState) {
//            for (Object crafter : this.crafters) {
//                ICrafting viewingPlayer = (ICrafting) crafter;
//
//                viewingPlayer.sendProgressBarUpdate(this, 0 /* State update */, state);
//            }
//
//            lastTriggerState = state;
//        }
//    }

    /**
     * TRIGGERS *
     */
    public boolean hasTriggers() {
        return potentialTriggers.size() > 0;
    }

    public Statement getFirstTrigger() {
        if (potentialTriggers.isEmpty()) {
            return null;
        } else {
            return potentialTriggers.first();
        }
    }

    public Statement getLastTrigger() {
        if (potentialTriggers.isEmpty()) {
            return null;
        } else {
            return potentialTriggers.last();
        }
    }

    public Iterator<Statement> getTriggerIterator(boolean descending) {
        return descending ? potentialTriggers.descendingIterator() : potentialTriggers.iterator();
    }

    public ActionActiveState getActionState(int i) {
        if (gate == null) {
            return ActionActiveState.Deactivated;
        } else {
            return gate.actionsState [i];
        }
    }

    public StatementParameter getTriggerParameter(int trigger, int param) {
        if (gate == null) {
            return null;
        } else {
            return gate.getTriggerParameter(trigger, param);
        }
    }

    /**
     * ACTIONS *
     */
    public boolean hasActions() {
        return !potentialActions.isEmpty();
    }

    public Statement getFirstAction() {
        if (potentialActions.isEmpty()) {
            return null;
        } else {
            return potentialActions.first();
        }
    }

    public Statement getLastAction() {
        if (potentialActions.isEmpty()) {
            return null;
        } else {
            return potentialActions.last();
        }
    }

    public Iterator<Statement> getActionIterator(boolean descending) {
        return descending ? potentialActions.descendingIterator() : potentialActions.iterator();
    }

    // PACKET GENERATION
    public Packet getStatementPacket(final String name, final int slot, final Statement statement) {
        final String statementKind = statement != null ? statement.getIdentifier().toString() : null;
        return new CommandPacket(this, name, new CommandWriter() {
            public void write(DataOutputStream data) {
                try {
                    data.writeByte(slot);
                    data.writeUTF(statementKind);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public Packet getStatementParameterPacket(final String name, final int slot,
                                              final int paramSlot, final StatementParameter parameter) {
        final String parameterName = parameter != null ? parameter.getIdentifier().toString() : null;
        final NbtCompound parameterNBT = new NbtCompound();
        if (parameter != null) {
            parameter.writeNBT(parameterNBT);
        }

        return new CommandPacket(this, name, new CommandWriter() {
            public void write(DataOutputStream data) {
                try {
                    data.writeByte(slot);
                    data.writeByte(paramSlot);
                    data.writeUTF(parameterName);
                    NbtIo.write(parameterNBT, data);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void setGate(int direction) {
        this.gate = (Gate) pipe.getGate(Direction.byId(direction));
        init();
    }

    @Override
    public void receiveCommand(String command, EnvType side, Object sender, DataInputStream stream) {
        try {
            if (side == EnvType.SERVER) {
                PlayerEntity player = (PlayerEntity) sender;
                if ("initRequest".equals(command)) {
                    final String[] triggerStrings = statementsToStrings(potentialTriggers);
                    final String[] actionStrings = statementsToStrings(potentialActions);

                    PacketHelper.sendTo(player, new CommandPacket(this, "init", new CommandWriter() {
                        public void write(DataOutputStream data) {
                            try {
                                data.writeByte(gate.getDirection().ordinal());
                                data.writeShort(triggerStrings.length);
                                data.writeShort(actionStrings.length);
                                for (String trigger : triggerStrings) {
                                    data.writeUTF(trigger);
                                }
                                for (String action : actionStrings) {
                                    data.writeUTF(action);
                                }
                            } catch (IOException e){

                            }
                        }
                    }));
                } else if ("selectionRequest".equals(command)) {
                    for (int position = 0; position < gate.material.numSlots; position++) {
                        Statement action = gate.getAction(position);
                        Statement trigger = gate.getTrigger(position);
                        PacketHelper.sendTo(player, getStatementPacket("setAction", position, action));
                        PacketHelper.sendTo(player, getStatementPacket("setTrigger", position, trigger));
                        for (int p = 0; p < gate.material.numActionParameters; ++p) {
                            PacketHelper.sendTo(player, getStatementParameterPacket(
                                    "setActionParameter", position, p, gate.getActionParameter(position, p)));
                        }
                        for (int q = 0; q < gate.material.numTriggerParameters; ++q) {
                            PacketHelper.sendTo(player, getStatementParameterPacket(
                                    "setTriggerParameter", position, q, gate.getTriggerParameter(position, q)));
                        }
                    }
                }
            } else if (side == EnvType.CLIENT) {
                if ("init".equals(command)) {
                    setGate(stream.readByte());
                    String[] triggerStrings = new String[stream.readShort()];
                    String[] actionStrings = new String[stream.readShort()];
                    for (int i = 0; i < triggerStrings.length; i++) {
                        triggerStrings[i] = stream.readUTF();
                    }
                    for (int i = 0; i < actionStrings.length; i++) {
                        actionStrings[i] = stream.readUTF();
                    }

                    stringsToStatements(this.potentialTriggers, triggerStrings);
                    stringsToStatements(this.potentialActions, actionStrings);
                }
            }

            if ("setAction".equals(command)) {
                setAction(stream.readUnsignedByte(), Identifier.tryParse(stream.readUTF()), false);
            } else if ("setTrigger".equals(command)) {
                setTrigger(stream.readUnsignedByte(), Identifier.tryParse(stream.readUTF()), false);
            } else if ("setActionParameter".equals(command) || "setTriggerParameter".equals(command)) {
                int slot = stream.readUnsignedByte();
                int param = stream.readUnsignedByte();
                String parameterName = stream.readUTF(stream);
                NbtCompound parameterData = NbtIo.read(stream);
                StatementParameter parameter = null;
                if (parameterName != null && parameterName.length() > 0) {
                    parameter = StatementManager.createParameter(parameterName);
                }

                if (parameter != null) {
                    parameter.readNBT(parameterData);
                    if ("setActionParameter".equals(command)) {
                        setActionParameter(slot, param, parameter, false);
                    } else {
                        setTriggerParameter(slot, param, parameter, false);
                    }
                }
            }
        } catch (IOException e){

        }
    }

    public void setAction(int action, Identifier tag, boolean notifyServer) {
        if (gate == null) {
            return;
        }

        Statement statement = null;

        if (tag != null) {
            statement = StatementManager.statements.get(tag);
        }
        gate.setAction(action, statement);

        if (pipe.world.isRemote && notifyServer) {
            PacketHelper.send(getStatementPacket("setAction", action, statement));
        }
    }

    public void setTrigger(int trigger, Identifier tag, boolean notifyServer) {
        if (gate == null) {
            return;
        }

        Statement statement = null;

        if (tag != null) {
            statement = StatementManager.statements.get(tag);
        }
        gate.setTrigger(trigger, statement);

        if (pipe.world.isRemote && notifyServer) {
            PacketHelper.send(getStatementPacket("setTrigger", trigger, statement));
        }
    }

    public void setActionParameter(int action, int param, StatementParameter parameter, boolean notifyServer) {
        if (gate == null) {
            return;
        }

        gate.setActionParameter(action, param, parameter);

        if (pipe.world.isRemote && notifyServer) {
            PacketHelper.send(getStatementParameterPacket("setActionParameter", action, param, parameter));
        }
    }

    public void setTriggerParameter(int trigger, int param, StatementParameter parameter, boolean notifyServer) {
        if (gate == null) {
            return;
        }

        gate.setTriggerParameter(trigger, param, parameter);

        if (pipe.world.isRemote && notifyServer) {
            PacketHelper.send(getStatementParameterPacket("setTriggerParameter", trigger, param, parameter));
        }
    }

    /**
     * GATE INFORMATION *
     */
    // TODO: probably return right gui texture here
    public String  getGateGuiFile() {
        return gate.material.backgroundTexture;
    }

    public String getGateName() {
        return GateDefinition.getLocalizedName(gate.material, gate.logic);
    }
}
