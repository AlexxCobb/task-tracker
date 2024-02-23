package org.tasktracker.model.enums;

public enum ParseIndexes {
    ID_INDEX(0),
    TYPE_INDEX(1),
    NAME_OF_TASK_INDEX(2),
    STATUS_INDEX(3),
    DESC_INDEX(4),
    START_TIME_INDEX(5),
    DURATION_INDEX(6),
    ;

    public int getIndex() {
        return index;
    }

    private final int index;

    ParseIndexes(int index) {
        this.index = index;
    }
}
