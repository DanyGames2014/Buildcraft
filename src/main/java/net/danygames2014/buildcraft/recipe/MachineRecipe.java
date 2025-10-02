package net.danygames2014.buildcraft.recipe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.recipe.input.RecipeInput;
import net.danygames2014.buildcraft.recipe.output.RecipeOutput;
import net.danygames2014.buildcraft.recipe.output.RecipeOutputType;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Random;

@SuppressWarnings("StringConcatenationArgumentToLogCall")
public class MachineRecipe {
    public final ObjectArrayList<RecipeInput> inputs;
    public final ObjectArrayList<RecipeOutput> outputs;
    public final int recipeTime;

    public MachineRecipe(int recipeTime) {
        this.recipeTime = recipeTime;
        this.inputs = new ObjectArrayList<>();
        this.outputs = new ObjectArrayList<>();
    }

    public MachineRecipe() {
        this(100);
    }

    public MachineRecipe addInput(RecipeInput input) {
        for (RecipeInput i : inputs) {
            if (i.equals(input)) {
                Buildcraft.LOGGER.warn("Input already exists in recipe " + input);
                return this;
            }
        }

        inputs.add(input);
        return this;
    }

    public MachineRecipe addOutput(RecipeOutput output) {
        for (RecipeOutput i : outputs) {
            if (i.equals(output)) {
                Buildcraft.LOGGER.warn("Output already exists in recipe " + output);
                return this;
            }
        }

        outputs.add(output);
        return this;
    }

    public boolean matches(ItemStack[] inputs) {
        // This is used to keep track of whether an ingredient has already been "consumed"
        boolean[] used = new boolean[inputs.length];

        if (this.inputs.isEmpty()) {
            return false;
        }

        // Loop over the required ingredients
        for (RecipeInput input : this.inputs) {
            // Check all the provided ingredients
            boolean satisfied = false;

            for (int i = 0; i < inputs.length; i++) {
                if (used[i]) {
                    continue;
                }

                ItemStack inputStack = inputs[i];
                if (inputStack == null) {
                    continue;
                }

                if (input.matches(inputStack)) {
                    satisfied = true;
                    used[i] = true;
                    break;
                }
            }

            // If not satisfied, return false
            if (!satisfied) {
                return false;
            }
        }

        return true;
    }

    public boolean consume(ItemStack[] inputs) {
        // Check if the inputs actually match the recipe
        if (matches(inputs)) {
            // This is used to keep track of whether an ingredient has already been "consumed"
            boolean[] used = new boolean[inputs.length];

            // Loop over the required ingredients
            for (RecipeInput input : this.inputs) {
                // Check all the provided ingredients
                for (int i = 0; i < inputs.length; i++) {
                    if (used[i]) {
                        continue;
                    }

                    ItemStack inputStack = inputs[i];
                    if (inputStack == null) {
                        continue;
                    }

                    if (input.matches(inputStack)) {
                        used[i] = true;
                        inputStack.count -= input.getRequiredAmount();
                        break;
                    }
                }
            }

            return true;
        }

        return false;
    }

    public HashMap<RecipeOutputType, ObjectArrayList<ItemStack>> getOutputs(Random random) {
        HashMap<RecipeOutputType, ObjectArrayList<ItemStack>> out = new HashMap<>();

        for (RecipeOutput recipeOutput : this.outputs) {
            if (!out.containsKey(recipeOutput.type)) {
                out.put(recipeOutput.type, new ObjectArrayList<>());
            }

            ItemStack outputStack = recipeOutput.getOutput(random);
            if (outputStack != null) {
                out.get(recipeOutput.type).add(outputStack);
            }
        }

        return out;
    }

    public ObjectArrayList<ItemStack> getCompactOutputs(Random random) {
        ObjectArrayList<ItemStack> outputs = new ObjectArrayList<>();

        for (RecipeOutput recipeOutput : this.outputs) {
            ItemStack outputStack = recipeOutput.getOutput(random);

            boolean found = false;
            for (var output : outputs) {
                if (output.equals(outputStack)) {
                    int excess = 0;

                    if (output.count + outputStack.count > output.getMaxCount()) {
                        excess = output.getMaxCount() - (output.count + outputStack.count);

                        ItemStack excessStack = outputStack.copy();
                        excessStack.count = excess;
                        outputs.add(excessStack);
                    }

                    output.count += (outputStack.count - excess);
                    found = true;
                    break;
                }
            }

            if (!found) {
                outputs.add(outputStack);
            }
        }

        return outputs;
    }
}
