
package me.heldplayer.ModeratorGui;

public enum ReportType {
    ISSUE(1), BAN(2), UNBAN(3), PROMOTE(4), DEMOTE(5), UNKNOWN(0);

    private final int id;

    private ReportType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ReportType getType(int id) {
        for (ReportType type : values()) {
            if (type.id == id) {
                return type;
            }
        }

        return UNKNOWN;
    }
}
