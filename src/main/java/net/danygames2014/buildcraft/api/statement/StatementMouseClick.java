package net.danygames2014.buildcraft.api.statement;

public class StatementMouseClick {
    private int button;
    private boolean shift;

    public StatementMouseClick(int button, boolean shift) {
        this.button = button;
        this.shift = shift;
    }

    public boolean isShift() {
        return shift;
    }

    public int getButton() {
        return button;
    }
}
