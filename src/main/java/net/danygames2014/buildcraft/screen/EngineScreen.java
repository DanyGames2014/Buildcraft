package net.danygames2014.buildcraft.screen;

import net.danygames2014.buildcraft.block.entity.BaseEngineBlockEntity;
import net.danygames2014.buildcraft.init.TextureListener;
import net.danygames2014.buildcraft.util.ScreenUtil;
import net.danygames2014.uniwrench.init.ItemListener;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;

public abstract class EngineScreen extends BuildcraftScreen{
    public EngineScreen(ScreenHandler container, Inventory inventory) {
        super(container, inventory);
    }

    @Override
    protected void initLedgers(Inventory inventory) {
        super.initLedgers(inventory);
        ledgerManager.add(new EngineLedger((BaseEngineBlockEntity) blockEntity));
    }

    protected class EngineLedger extends Ledger{
        BaseEngineBlockEntity engine;
        int headerColor = 0xE1C92F;
        int subheaderColor = 0xAAAFB8;
        int textColor = 0x000000;
        public EngineLedger(BaseEngineBlockEntity engine){
            this.engine = engine;
            maxHeight = 94;
            overlayColor = 0xD46C1F;
        }

        @Override
        public void draw(int x, int y) {
            drawBackground(x, y);
            ScreenUtil.drawSprite(TextureListener.energySprite, x + 3, y + 4, 16, 16, zOffset);


            if(!isFullyOpened()){
                return;
            }

            TranslationStorage translationStorage = TranslationStorage.getInstance();

            textRenderer.drawWithShadow(translationStorage.get("gui.buildcraft.engine.energy"), x + 22, y + 8, headerColor);
            textRenderer.drawWithShadow(translationStorage.get("gui.buildcraft.engine.current_output") + ":", x + 22, y + 20, subheaderColor);
            textRenderer.draw(String.format("%.1f MJ/t", engine.getCurrentEnergyOutput()), x + 22, y + 32, textColor);
            textRenderer.drawWithShadow(translationStorage.get("gui.buildcraft.engine.stored") + ":", x + 22, y + 44, subheaderColor);
            textRenderer.draw(String.format("%.1f MJ", engine.getEnergyStored()), x + 22, y + 56, textColor);
            textRenderer.drawWithShadow(translationStorage.get("gui.buildcraft.engine.heat") + ":", x + 22, y + 68, subheaderColor);
            textRenderer.draw(String.format("%.2f \u00B0C", (engine.getHeat() / 100.0) + 20.0), x + 22, y + 80, textColor);
        }

        @Override
        public String getTooltip() {
            return engine.getCurrentEnergyOutput() + " MJ/t";
        }
    }
}
