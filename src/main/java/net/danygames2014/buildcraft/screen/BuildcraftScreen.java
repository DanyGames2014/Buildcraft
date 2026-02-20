package net.danygames2014.buildcraft.screen;

import net.danygames2014.buildcraft.util.ScreenUtil;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public abstract class BuildcraftScreen extends HandledScreen {

    public static final String LEDGER_TEXTURE = "/assets/buildcraft/stationapi/textures/gui/ledger.png";
    public final LedgerManager ledgerManager = new LedgerManager(this);
    protected BlockEntity blockEntity;

    public BuildcraftScreen(ScreenHandler container, Inventory inventory) {
        super(container);
        if(inventory instanceof BlockEntity){
            blockEntity = (BlockEntity) inventory;
        }

        initLedgers(inventory);
    }

    protected void initLedgers(Inventory inventory) {
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
        ledgerManager.drawLedgers(mouseX, mouseY);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        ledgerManager.handleMouseClicked(mouseX, mouseY, button);
    }

    public void drawSprite(Atlas.Sprite sprite, int x, int y, int width, int height) {
        ScreenUtil.drawSprite(sprite, x, y, width, height, zOffset);
    }

    public class LedgerManager {
        private BuildcraftScreen gui;
        @SuppressWarnings("rawtypes")
        private static Class openedLedger;
        protected ArrayList<Ledger> ledgers = new ArrayList<>();

        public LedgerManager(BuildcraftScreen gui){
            this.gui = gui;
        }

        @SuppressWarnings("rawtypes")
        public static void setOpenedLedger(Class ledgerClass){
            openedLedger = ledgerClass;
        }

        @SuppressWarnings("rawtypes")
        public static Class getOpenedLedger(){
            return openedLedger;
        }

        public void add(Ledger ledger){
            this.ledgers.add(ledger);
            if (getOpenedLedger() != null && ledger.getClass().equals(getOpenedLedger())) {
                ledger.setFullyOpen();
            }
        }

        public void insert(Ledger ledger){
            this.ledgers.add(ledgers.size() - 1, ledger);
        }

        protected Ledger getAtPosition(int mX, int mY){

            int xShift = ((gui.width - gui.backgroundWidth) / 2) + gui.backgroundWidth;
            int yShift = ((gui.height - gui.backgroundHeight) / 2) + 8;

            for (Ledger ledger : ledgers) {
                if (!ledger.isVisible()) {
                    continue;
                }

                ledger.currentShiftX = xShift;
                ledger.currentShiftY = yShift;
                if (ledger.intersectsWith(mX, mY, xShift, yShift)) {
                    return ledger;
                }

                yShift += ledger.getHeight();
            }

            return null;
        }

        protected void drawLedgers(int mouseX, int mouseY){
            int yPos = 8;
            for (Ledger ledger : ledgers) {

                ledger.update();
                if (!ledger.isVisible()){
                    continue;
                }

                ledger.draw(((width - backgroundWidth) / 2) + 176, ((height - backgroundHeight) / 2) + yPos);
                yPos += ledger.getHeight();
            }

            Ledger ledger = getAtPosition(mouseX, mouseY);
            if (ledger != null){
                // These offsets were wrong
//                int startX = mouseX - ((gui.width - gui.backgroundWidth) / 2) + 12;
//                int startY = mouseY - ((gui.height - gui.backgroundHeight) / 2) - 12;

                String tooltip = ledger.getTooltip();
                int textWidth = textRenderer.getWidth(tooltip);
                fillGradient(mouseX + 10 - 3, mouseY - 10 - 3, mouseX + 10 + textWidth + 3, mouseY - 10 + 8 + 3, 0xc0000000, 0xc0000000);
                textRenderer.drawWithShadow(tooltip, mouseX + 10, mouseY - 10, -1);
            }
        }

        public void handleMouseClicked(int x, int y, int mouseButton){
            if (mouseButton == 0) {
                Ledger ledger = this.getAtPosition(x, y);

                // Default action only if the mouse click was not handled by the
                // ledger itself.
                if (ledger != null && !ledger.handleMouseClicked(x, y, mouseButton)){

                    for (Ledger other : ledgers) {
                        if (other != ledger && other.isOpen()) {
                            other.toggleOpen();
                        }
                    }
                    ledger.toggleOpen();
                }
            }
        }
    }

    public abstract class Ledger {
        private boolean open;
        protected int overlayColor = 0xffffff;
        public int currentShiftX = 0;
        public int currentShiftY = 0;
        protected int limitWidth = 128;
        protected int maxWidth = 124;
        protected int minWidth = 24;
        protected int currentWidth = minWidth;
        protected int maxHeight = 24;
        protected int minHeight = 24;
        protected int currentHeight = minHeight;

        public void update(){
            // Width
            if (open && currentWidth < maxWidth){
                currentWidth += 4;
            } else if (!open && currentWidth > minWidth){
                currentWidth -= 4;
            }

            // Height
            if (open && currentHeight < maxHeight){
                currentHeight += 4;
            } else if (!open && currentHeight > minHeight){
                currentHeight -= 4;
            }
        }

        public int getHeight(){
            return currentHeight;
        }

        public abstract void draw(int x, int y);

        public abstract String getTooltip();

        public boolean handleMouseClicked(int x, int y, int mouseButton) {
            return false;
        }

        public boolean intersectsWith(int mouseX, int mouseY, int shiftX, int shiftY) {
            return mouseX >= shiftX && mouseX <= shiftX + currentWidth && mouseY >= shiftY && mouseY <= shiftY + getHeight();
        }

        public void setFullyOpen() {
            open = true;
            currentWidth = maxWidth;
            currentHeight = maxHeight;
        }

        public void toggleOpen() {
            if (open) {
                open = false;
                LedgerManager.setOpenedLedger(null);
            } else {
                open = true;
                LedgerManager.setOpenedLedger(this.getClass());
            }
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        public boolean isVisible() {
            return true;
        }

        public boolean isOpen() {
            return this.open;
        }

        protected boolean isFullyOpened() {
            return currentWidth >= maxWidth;
        }

        protected void drawBackground(int x, int y) {
            float red = (float) (overlayColor >> 16 & 255) / 255.0F;
            float green = (float) (overlayColor >> 8 & 255) / 255.0F;
            float blue = (float) (overlayColor & 255) / 255.0F;
            GL11.glColor4f(red, green, blue, 1.0F);

            minecraft.textureManager.bindTexture(minecraft.textureManager.getTextureId(LEDGER_TEXTURE));
            drawTexture(x, y, 0, 256 - currentHeight, 4, currentHeight);
            drawTexture(x + 4, y, 256 - currentWidth + 4, 0, currentWidth - 4, 4);
            // Add in top left corner again
            drawTexture(x, y, 0, 0, 4, 4);

            drawTexture(x + 4, y + 4, 256 - currentWidth + 4, 256 - currentHeight + 4, currentWidth - 4, currentHeight - 4);

            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
        }

        // TODO: possibly move this to a helper method
    }
}
