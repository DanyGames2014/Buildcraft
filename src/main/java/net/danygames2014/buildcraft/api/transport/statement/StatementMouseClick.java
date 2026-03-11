package net.danygames2014.buildcraft.api.transport.statement;

@SuppressWarnings("ClassCanBeRecord")
public class StatementMouseClick {
    private final int button;
    private final boolean shift;

    public StatementMouseClick(int button, boolean shift) {
        this.button = button;
        this.shift = shift;
    }

    public boolean isShiftClick() {
        return shift;
    }

    public int getButton() {
        return button;
    }
}
