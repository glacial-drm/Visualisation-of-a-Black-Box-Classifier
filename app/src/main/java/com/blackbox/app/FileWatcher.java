package com.blackbox.app;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.tinylog.Logger;

/**
 * Watches a file for changes.
 * Calls a callback with the file contents.
 */
public class FileWatcher {
    private final Duration timeout = Duration.ofMillis(100);

    /**
     * Path of the watched file.
     */
    private final Path path;

    /**
     * Callback for when the file has changed.
     */
    private final Consumer<String> onChange;

    /**
     * The watcher.
     * Watches the parent directory, which we then filter for the wanted file.
     */
    private WatchService watcher;

    /**
     * The thread polling the {@link WatchService}.
     * Loops until stopped by setting {@link FileWatcher#running running}.
     */
    private Thread thread;

    /**
     * Whether the main loop thread should keep running.
     * Set to false to stop the thread.
     */
    private boolean running = true;

    public FileWatcher(Path path, Consumer<String> onChange) {
        this.path = path;
        this.onChange = onChange;
    }

    private boolean mainLoop() {
        WatchKey key;
        try {
            key = watcher.poll(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            Logger.error("Watcher error : {}", e);
            return true;
        }

        // On timeout repeat loop
        if (key == null) return false;

        for (WatchEvent<?> event : key.pollEvents()) {
            // Check it is a modify event
            if (event.kind() != StandardWatchEventKinds.ENTRY_MODIFY) continue;

            @SuppressWarnings("unchecked")
            WatchEvent<Path> modifyEvent = (WatchEvent<Path>)event;

            // Check it is the correct file
            if (!modifyEvent.context().equals(path.getFileName())) continue;

            // Read the File
            try {
                onChange.accept(Files.readString(path));
            } catch (IOException e) {
                Logger.error("Failed to read watched file '{}' : {}", path, e);
            }
        }

        if (!key.reset()) {
            Logger.error("Failed to continue filesystem watcher");
            return true;
        }

        return false;
    }

    /**
     * Start the watcher thread.
     * Loops until stopped.
     */
    public void start() {
        thread = new Thread(() -> {
            try {
                watcher = FileSystems.getDefault().newWatchService();
                path.getParent().register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            } catch (IOException e) {
                Logger.error("Failed to create filesystem watcher : {}", e);
                return;
            }

            while (!mainLoop() && running);
        });

        thread.start();
    }

    /**
     * Stop the thread.
     *
     * Won't stop immediately, could take up to 100ms.
     * (or {@link FileWatcher#timeout})
     */
    public void stop() {
        running = false;
    }
}
