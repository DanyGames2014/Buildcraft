package net.danygames2014.buildcraft.screen;

import net.danygames2014.buildcraft.api.transport.statement.Statement;
import net.danygames2014.buildcraft.api.transport.statement.StatementMouseClick;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.gate.ActionActiveState;
import net.danygames2014.buildcraft.block.entity.pipe.gate.Gate;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateMaterial;
import net.danygames2014.buildcraft.screen.handler.GateInterfaceScreenHandler;
import net.danygames2014.buildcraft.screen.slot.AdvancedSlot;
import net.danygames2014.buildcraft.screen.slot.StatementParameterSlot;
import net.danygames2014.buildcraft.screen.slot.StatementSlot;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.inventory.Inventory;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Iterator;

public class GateInterfaceScreen extends AdvancedInterfaceScreen{
    Inventory playerInventory;
    private final GateInterfaceScreenHandler screenHandler;
    private final GateInterfaceScreen instance;
    private final PipeBlockEntity pipe;
    private Gate gate;

    private class TriggerSlot extends StatementSlot {
        public TriggerSlot(int x, int y, PipeBlockEntity pipe, int slot) {
            super(instance, x, y, slot);
        }

        @Override
        public Statement getStatement() {
            return gate.getTrigger(slot);
        }
    }

    private class ActionSlot extends StatementSlot {
        public ActionSlot(int x, int y, PipeBlockEntity pipe, int slot) {
            super(instance, x, y, slot);
        }

        @Override
        public Statement getStatement() {
            return gate.getAction(slot);
        }
    }

    class TriggerParameterSlot extends StatementParameterSlot {
        public TriggerParameterSlot(int x, int y, PipeBlockEntity pipe, int slot, StatementSlot iStatementSlot) {
            super(instance, x, y, slot, iStatementSlot);
        }

        @Override
        public StatementParameter getParameter() {
            return gate.getTriggerParameter(statementSlot.slot, slot);
        }

        @Override
        public void setParameter(StatementParameter param, boolean notifyServer) {
            screenHandler.setTriggerParameter(statementSlot.slot, slot, param, notifyServer);
        }
    }

    class ActionParameterSlot extends StatementParameterSlot {
        public ActionParameterSlot(int x, int y, PipeBlockEntity pipe, int slot, StatementSlot iStatementSlot) {
            super(instance, x, y, slot, iStatementSlot);
        }

        @Override
        public StatementParameter getParameter() {
            return gate.getActionParameter(statementSlot.slot, slot);
        }

        @Override
        public void setParameter(StatementParameter param, boolean notifyServer) {
            screenHandler.setActionParameter(statementSlot.slot, slot, param, notifyServer);
        }
    }

    public GateInterfaceScreen(Inventory inventory, PipeBlockEntity pipe) {
        super(new GateInterfaceScreenHandler(inventory, pipe), null);

        screenHandler = (GateInterfaceScreenHandler) this.container;
        screenHandler.gateCallback = this;
        this.pipe = pipe;
        this.playerInventory = inventory;
        this.instance = this;
    }

    public void setGate(Gate gate) {
        this.gate = gate;
        init();
    }

    @Override
    public void init() {
        super.init();

        if (gate == null) {
            return;
        }

        backgroundWidth = 176;
        backgroundHeight = gate.material.guiHeight;

        int position = 0;

        slots.clear();

        if (gate.material == GateMaterial.REDSTONE) {
            slots.add(new TriggerSlot(62, 26, pipe, 0));
            slots.add(new ActionSlot(98, 26, pipe, 0));
        } else if (gate.material == GateMaterial.IRON) {
            slots.add(new TriggerSlot(62, 26, pipe, 0));
            slots.add(new TriggerSlot(62, 44, pipe, 1));
            slots.add(new ActionSlot(98, 26, pipe, 0));
            slots.add(new ActionSlot(98, 44, pipe, 1));
        } else if (gate.material == GateMaterial.GOLD) {
            for (int k = 0; k < 4; ++k) {
                slots.add(new TriggerSlot(53, 26 + 18 * k, pipe, position));
                position++;
            }

            for (int k = 0; k < 4; ++k) {
                slots.add(new ActionSlot(107, 26 + 18 * k, pipe, position - 4));
                position++;
            }

            for (int k = 0; k < 4; ++k) {
                slots.add(new TriggerParameterSlot(71, 26 + 18 * k, pipe, 0, (TriggerSlot) slots.get(k)));
                position++;

            }
        } else if (gate.material == GateMaterial.DIAMOND) {
            for (int k = 0; k < 4; ++k) {
                slots.add(new TriggerSlot(8, 26 + 18 * k, pipe, position));
                position++;
                slots.add(new TriggerSlot(98, 26 + 18 * k, pipe, position));
                position++;
            }

            for (int k = 0; k < 4; ++k) {
                slots.add(new ActionSlot(62, 26 + 18 * k, pipe, position - 8));
                position++;
                slots.add(new ActionSlot(152, 26 + 18 * k, pipe, position - 8));
                position++;
            }

            for (int k = 0; k < 4; ++k) {
                slots.add(new TriggerParameterSlot(26, 26 + 18 * k, pipe, 0,
                        (TriggerSlot) slots.get(position - 16)));
                position++;
                slots.add(new TriggerParameterSlot(116, 26 + 18 * k, pipe, 0,
                        (TriggerSlot) slots.get(position - 16)));
                position++;
            }
        }
        // TODO: what is this?
        //initGui();
    }

    @Override
    protected void drawForeground() {
        if (gate == null) {
            return;
        }
        String name = screenHandler.getGateName();

        textRenderer.draw(name, (backgroundWidth / 2) - (textRenderer.getWidth(name) / 2), 10, 0x404040);
        textRenderer.draw(TranslationStorage.getInstance().get("gui.inventory"), 8, backgroundHeight - 97, 0x404040);

        //drawTooltipForSlotAt(par1, par2);
    }

    @Override
    protected void drawBackground(float tickDelta) {
        screenHandler.synchronize();

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        if(gate == null){
            return;
        }

        String texture = screenHandler.getGateGuiFile();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.textureManager.bindTexture(minecraft.textureManager.getTextureId(texture));

        drawTexture(x, y, 0, 0, backgroundWidth, backgroundHeight);

        for (AdvancedSlot slot : slots) {
            if (slot instanceof TriggerSlot) {
                boolean halfWidth = screenHandler.actionsState[((TriggerSlot) slot).slot] == ActionActiveState.Partial;

                if (screenHandler.actionsState[((TriggerSlot) slot).slot] != ActionActiveState.Deactivated) {
                    minecraft.textureManager.bindTexture(minecraft.textureManager.getTextureId(texture));

                    drawTexture(x + slot.x + 17 + 18 * gate.material.numTriggerParameters, y
                                                                                                                   + slot.y + 6, 176, 18, halfWidth ? 9 : 18, 4);
                }
            } else if (slot instanceof StatementParameterSlot) {
                StatementParameterSlot paramSlot = (StatementParameterSlot) slot;
                StatementSlot statement = paramSlot.statementSlot;

                minecraft.textureManager.bindTexture(minecraft.textureManager.getTextureId(texture));

                if (statement.isDefined()) {
                    if (!paramSlot.isAllowed()) {
                        drawTexture(x + slot.x - 1, y + slot.y - 1, 176, 0, 18, 18);
                    } else if (paramSlot.isRequired() && paramSlot.getItemStack() == null) {
                        drawTexture(x + slot.x - 1, y + slot.y - 1, 176, 22, 18, 18);
                    }
                } else {
                    drawTexture(x + slot.x - 1, y + slot.y - 1, 176, 0, 18, 18);
                }
            }
        }

        drawBackgroundSlots();
    }

    private void doSlotClick(AdvancedSlot slot, int k) {
        if (slot instanceof TriggerSlot && screenHandler.hasTriggers()) {
            TriggerSlot triggerSlot = (TriggerSlot) slot;

            Statement changed = null;

            if (isShiftKeyDown()) {
                changed = null;
            } else {
                if (triggerSlot.getStatement() == null) {
                    if (k == 0) {
                        changed = screenHandler.getFirstTrigger();
                    } else {
                        changed = screenHandler.getLastTrigger();
                    }
                } else {
                    Iterator<Statement> it = screenHandler.getTriggerIterator(k != 0);

                    for (; it.hasNext();) {
                        Statement trigger = it.next();

                        if (!it.hasNext()) {
                            changed = null;
                            break;
                        }

                        if (trigger == triggerSlot.getStatement()) {
                            changed = it.next();
                            break;
                        }
                    }
                }
            }

            if (changed == null) {
                screenHandler.setTrigger(triggerSlot.slot, null, true);
            } else {
                screenHandler.setTrigger(triggerSlot.slot, changed.getIdentifier(), true);
            }

            for (StatementParameterSlot p : triggerSlot.parameters) {
                StatementParameter parameter = null;
                if (changed != null && p.slot < changed.minParameters()) {
                    parameter = changed.createParameter(p.slot);
                }
                screenHandler.setTriggerParameter(triggerSlot.slot, p.slot, parameter, true);
            }
        } else if (slot instanceof ActionSlot) {
            ActionSlot actionSlot = (ActionSlot) slot;

            Statement changed = null;

            if (isShiftKeyDown()) {
                changed = null;
            } else {
                if (actionSlot.getStatement() == null) {
                    if (k == 0) {
                        changed = screenHandler.getFirstAction();
                    } else {
                        changed = screenHandler.getLastAction();
                    }

                } else {
                    Iterator<Statement> it = screenHandler.getActionIterator(k != 0);

                    for (; it.hasNext();) {
                        Statement action = it.next();

                        if (!it.hasNext()) {
                            changed = null;
                            break;
                        }

                        if (action == actionSlot.getStatement()) {
                            changed = it.next();
                            break;
                        }
                    }
                }
            }

            if (changed == null) {
                screenHandler.setAction(actionSlot.slot, null, true);
            } else {
                screenHandler.setAction(actionSlot.slot, changed.getIdentifier(), true);
            }

            for (StatementParameterSlot p : actionSlot.parameters) {
                StatementParameter parameter = null;
                if (changed != null && p.slot < changed.minParameters()) {
                    parameter = changed.createParameter(p.slot);
                }
                screenHandler.setActionParameter(actionSlot.slot, p.slot, parameter, true);
            }
        } else if (slot instanceof StatementParameterSlot) {
            StatementParameterSlot paramSlot = (StatementParameterSlot) slot;
            StatementSlot statement = paramSlot.statementSlot;

            if (statement.isDefined() && statement.getStatement().maxParameters() != 0) {
                StatementParameter param = paramSlot.getParameter();

                if (param == null) {
                    param = statement.getStatement().createParameter(paramSlot.slot);
                }

                if (param != null) {
                    param.click(gate, statement.getStatement(), minecraft.player.inventory.getCursorStack(),
                            new StatementMouseClick(k, isShiftKeyDown()));
                    paramSlot.setParameter(param, true);
                }
            }
        }

        screenHandler.markDirty();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (gate == null) {
            return;
        }
        super.mouseClicked(mouseX, mouseY, button);

        AdvancedSlot slot = getSlotAtLocation(mouseX, mouseX);

        if (slot != null) {
            doSlotClick(slot, button);
        }
    }

    @Override
    public void onMouseEvent() {
        super.onMouseEvent();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            int i = Mouse.getEventX() * this.width / this.minecraft.displayWidth;
            int j = this.height - Mouse.getEventY() * this.height / this.minecraft.displayHeight - 1;
            doSlotClick(getSlotAtLocation(i, j), wheel > 0 ? 0 : 1);
        }
    }

    public boolean isShiftKeyDown(){
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
    }
}
