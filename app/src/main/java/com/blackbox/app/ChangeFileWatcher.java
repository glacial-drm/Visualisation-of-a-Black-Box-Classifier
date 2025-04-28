package com.blackbox.app;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Watches a file for changes.
 * Calls a callback with only the new parts appended to the file.
 */
public class ChangeFileWatcher extends FileWatcher {
    int previousContentsLen = 0;

    public ChangeFileWatcher(Path path, Consumer<String> userOnChange) {
        super(path, s -> {});
        onChange = contents -> {
            String change = contents.substring(previousContentsLen).trim();
            if (change.isBlank()) return;
            userOnChange.accept(change);
            previousContentsLen = contents.length();
        };
    }
}
