package org.jdownloader.myjdownloader.client.bindings;

public class CleanupActionOptions {

    public enum Action {
        DELETE_ALL,
        DELETE_DISABLED,
        DELETE_FAILED,
        DELETE_FINISHED,
        DELETE_OFFLINE,
        DELETE_DUPE,
        DELETE_MODE
    }

    public enum Mode {
        REMOVE_LINKS_AND_DELETE_FILES,
        REMOVE_LINKS_AND_RECYCLE_FILES,
        REMOVE_LINKS_ONLY
    }

    public enum SelectionType {
        SELECTED,
        UNSELECTED,
        ALL,
        NONE
    }
}