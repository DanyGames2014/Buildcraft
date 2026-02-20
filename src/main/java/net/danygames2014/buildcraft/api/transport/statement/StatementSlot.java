package net.danygames2014.buildcraft.api.transport.statement;

public class StatementSlot {
    public Statement statement;
    public StatementParameter[] parameters;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StatementSlot s)) {
            return false;
        }
        if (s.statement != statement || parameters.length != s.parameters.length) {
            return false;
        }
        for (int i = 0; i < parameters.length; i++) {
            StatementParameter p1 = parameters[i];
            StatementParameter p2 = s.parameters[i];
            if (p1 == null && p2 != null) {
                return false;
            }
            
            if (p1 != null && p2 == null) {
                return false;
            }
            
            if (p1 == null) {
                return true;
            }
            
            if (!(p1.equals(p2))) {
                return false;
            }
        }
        return true;
    }
}
