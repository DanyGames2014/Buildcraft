package net.danygames2014.buildcraft.block.entity;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.inventory.SimpleInventory;
import net.danygames2014.buildcraft.inventory.UnhandledAutocraftingTableInventory;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.danygames2014.nyalib.item.ItemHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.CraftingRecipeManager;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class AutocraftingTableBlockEntity extends BlockEntity implements Inventory, ItemHandler {
    // 0 - 8 - Crafting Matrix
    //     9 - Result Slot
    //    10 - Visual Result Slot
    SimpleInventory inventory = new SimpleInventory(11, "Autocrafting Table", this::markDirty);
    UnhandledAutocraftingTableInventory craftingMatrix = new UnhandledAutocraftingTableInventory(null, this);
    int autoCraftDelay;

    @Override
    public void tick() {
        super.tick();

        if (autoCraftDelay > 0) {
            autoCraftDelay--;
        } else {
            if (inventory.getStack(9) == null) {
                inventory.setStack(9, autoCraft());
            }
        }
    }

    public boolean canAutoCraft() {
        if (autoCraftDelay > 0) {
            return false;
        }

        NeighborHandler neighborHandler = findInventory();
        ItemHandlerBlockCapability chest = neighborHandler != null ? neighborHandler.handler : null;
        Int2IntOpenHashMap usedItems = new Int2IntOpenHashMap();

        for (int i = 0; i < this.craftingMatrix.size(); ++i) {
            ItemStack stack = this.craftingMatrix.getStack(i);

            if (stack == null) {
                continue;
            }

            // If the item has a return item, check if another one exists in an adjacent inventory
            if (stack.getItem().hasCraftingReturnItem()) {
                boolean found = false;

                if (neighborHandler != null) {
                    ItemStack[] inv = chest.getInventory(neighborHandler.side.getOpposite());
                    for (int chestSlot = 0; chestSlot < inv.length; chestSlot++) {
                        if (inv[chestSlot] == null) {
                            continue;
                        }

                        if (stack.isItemEqual(inv[chestSlot]) && usedItems.getOrDefault(chestSlot, 0) < inv[chestSlot].count) {
                            usedItems.putIfAbsent(chestSlot, 0);
                            usedItems.put(chestSlot, usedItems.get(chestSlot) + 1);
                            found = true;
                            break;
                        }
                    }
                }

                // If no item was found, auto craft cannot commence
                if (!found) {
                    return false;
                }
            } else {
                // For items which do not have a return item, we check their amount
                boolean enough = false;

                // If the stack has more than one item
                if (stack.count <= 1) {

                    // If there is one, check adjacent inventory for if it contains an usable item
                    if (neighborHandler != null) {
                        ItemStack[] inv = chest.getInventory(neighborHandler.side.getOpposite());
                        for (int chestSlot = 0; chestSlot < inv.length; chestSlot++) {
                            if (inv[chestSlot] == null) {
                                continue;
                            }
                            
                            if (stack.isItemEqual(inv[chestSlot]) && usedItems.getOrDefault(chestSlot, 0) < inv[chestSlot].count) {
                                usedItems.putIfAbsent(chestSlot, 0);
                                usedItems.put(chestSlot, usedItems.get(chestSlot) + 1);
                                enough = true;
                                break;
                            }
                        }
                    }
                } else {
                    enough = true;
                }

                if (!enough) {
                    return false;
                }
            }
        }

        return true;
    }

    public ItemStack autoCraft() {
        if (!canAutoCraft()) {
            return null;
        }

        autoCraftDelay = 20;

        return craft(true);
    }

    public ItemStack craft(boolean auto) {
        NeighborHandler neighborHandler = findInventory();
        ItemHandlerBlockCapability chest = neighborHandler != null ? neighborHandler.handler : null;
        Direction chestFace = neighborHandler != null ? neighborHandler.side.getOpposite() : null;
        
        ItemStack result = CraftingRecipeManager.getInstance().craft(this.craftingMatrix);
        result = result != null ? result.copy() : null;
        
        if (result == null) {
            return null;
        }

        for (int matrixSlot = 0; matrixSlot < this.craftingMatrix.size(); ++matrixSlot) {
            ItemStack stack = this.craftingMatrix.getStack(matrixSlot);

            if (stack == null) {
                continue;
            }

            // If the item has a return item, check if another one exists in an adjacent inventory
            if (stack.getItem().hasCraftingReturnItem()) {
                ItemStack returnStack = new ItemStack(stack.getItem().getCraftingReturnItem(), 1);

                if (neighborHandler != null) {
                    for (int slot = 0; slot < chest.getItemSlots(chestFace); slot++) {
                        ItemStack potentialStack = chest.getItem(slot, chestFace);
                        if (potentialStack != null && stack.isItemEqual(potentialStack)) {
                            // Set the replacement item into the slot
                            this.craftingMatrix.setStack(matrixSlot, chest.extractItem(slot, 1, chestFace));

                            // Try inserting the return item into the inventory
                            returnStack = chest.insertItem(returnStack, chestFace);
                            break;
                        }
                    }
                } else {
                    if (auto) {
                        // This should not happen
                        Buildcraft.LOGGER.warn("There was a return item during automatic crafting, but no inventory to deposit into! This should not have been allowed by the canAutoCraft check.");
                    } else {
                        // When manual crafting and no neighbor handler, just set the return stack into the slot
                        this.craftingMatrix.setStack(matrixSlot, returnStack);
                    }
                }

                // If the return item was not dealt with, drop it
                if (returnStack != null) {
                    world.spawnEntity(new ItemEntity(world, x + 0.5D, y + 1.5D, z + 0.5D, returnStack));
                }
            } else {
                ItemStack removedStack = this.craftingMatrix.removeStack(matrixSlot, 1);

                // The stack became null, try to refill it
                if (craftingMatrix.getStack(matrixSlot) == null) {
                    if (neighborHandler != null) {
                        for (int chestSlot = 0; chestSlot < chest.getItemSlots(chestFace); chestSlot++) {
                            ItemStack potentialStack = chest.getItem(chestSlot, chestFace);
                            if (potentialStack != null && potentialStack.isItemEqual(removedStack)) {
                                craftingMatrix.setStack(matrixSlot, chest.extractItem(chestSlot, 1, chestFace));
                                break;
                            }
                        }
                    } else {
                        Buildcraft.LOGGER.warn("Stack needed to be refilled in an autocrafting table but no inventory was found. This should not have been allowed by the canAutoCraft check.");
                    }
                }
            }

            if (stack.count <= 0) {
                Buildcraft.LOGGER.warn("Stack in slot " + matrixSlot + " of an Autocrafting Table has the size of zero!");
                craftingMatrix.setStack(matrixSlot, null);
            }
        }

        return result;
    }

    private NeighborHandler findInventory() {
        for (Direction side : Direction.values()) {
            ItemHandlerBlockCapability cap = CapabilityHelper.getCapability(world, x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ(), ItemHandlerBlockCapability.class);
            if (cap != null) {
                return new NeighborHandler(cap, side);
            }
        }

        return null;
    }

    // Inventory
    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return inventory.removeStack(slot, amount);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.setStack(slot, stack);
    }

    @Override
    public String getName() {
        return inventory.getName();
    }

    @Override
    public int getMaxCountPerStack() {
        return inventory.getMaxCountPerStack();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        inventory.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        inventory.readNbt(nbt);
    }

    // ItemHandler
    @Override
    public boolean canExtractItem(@Nullable Direction side) {
        return true;
    }

    @Override
    public ItemStack extractItem(@Nullable Direction side) {
        return extractItem(Integer.MAX_VALUE, side);
    }

    @Override
    public ItemStack extractItem(int amount, @Nullable Direction side) {
        return extractItem(9, amount, side);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, @Nullable Direction side) {
        if (slot != 9) {
            return null;
        }

        return autoCraft();
    }

    @Override
    public boolean canInsertItem(@Nullable Direction side) {
        return true;
    }

    @Override
    public ItemStack insertItem(ItemStack stack, int slot, @Nullable Direction side) {
        if (slot >= getItemSlots(side) || slot < 0) {
            return stack;
        }

        ItemStack slotStack;

        slotStack = inventory.getStack(slot);

        if (slotStack == null) {
            inventory.setStack(slot, stack);
            return null;
        }

        if (slotStack.isItemEqual(stack)) {
            int addedCount = Math.min(slotStack.getItem().getMaxCount() - slotStack.count, stack.count);

            slotStack.count += addedCount;

            if (addedCount >= stack.count) {
                return null;
            } else {
                return new ItemStack(stack.getItem(), stack.count - addedCount, stack.getDamage());
            }
        }

        return stack;
    }

    @Override
    public ItemStack insertItem(ItemStack stack, @Nullable Direction side) {
        ItemStack insertedStack = stack.copy();

        for (int i = 0; i < this.getItemSlots(side); ++i) {
            insertedStack = insertItem(insertedStack, i, side);
            if (insertedStack == null) {
                return insertedStack;
            }
        }

        return insertedStack;
    }

    @Override
    public ItemStack getItem(int slot, @Nullable Direction side) {
        if (slot >= getItemSlots(side) || slot < 0) {
            return null;
        }

        return inventory.getStack(slot);
    }

    @Override
    public boolean setItem(ItemStack stack, int slot, @Nullable Direction side) {
        if (slot >= getItemSlots(side) || slot < 0) {
            return false;
        }

        inventory.setStack(slot, stack);
        return true;
    }

    @Override
    public int getItemSlots(@Nullable Direction side) {
        return inventory.size();
    }

    @Override
    public ItemStack[] getInventory(@Nullable Direction side) {
        return inventory.stacks;
    }

    @Override
    public boolean canConnectItem(Direction side) {
        return true;
    }

    // NeighborHandler
    private static class NeighborHandler {
        ItemHandlerBlockCapability handler;
        Direction side;

        public NeighborHandler(ItemHandlerBlockCapability handler, Direction side) {
            this.handler = handler;
            this.side = side;
        }
    }
}
