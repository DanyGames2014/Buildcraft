package net.danygames2014.buildcraft.screen.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class TransparentTextFieldWidget extends TextFieldWidget {
    public TransparentTextFieldWidget(Screen parent, TextRenderer textRenderer, int x, int y, int width, int height, String text) {
        super(parent, textRenderer, x, y, width, height, text);
    }

    @Override
    public void render() {
        if (this.enabled) {
            boolean var1 = this.focused && this.focusedTicks / 6 % 2 == 0;
            this.drawTextWithShadow(this.textRenderer, this.text + (var1 ? "_" : ""), this.x + 4, this.y + (this.height - 8) / 2, 14737632);
        } else {
            this.drawTextWithShadow(this.textRenderer, this.text, this.x + 4, this.y + (this.height - 8) / 2, 7368816);
        }
    }
}
