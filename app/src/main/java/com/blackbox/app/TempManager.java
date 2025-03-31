package com.blackbox.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.tinylog.Logger;

/**
 * Manages a temporary folder for runtime files.
 */
public class TempManager {
    private static final String root = System.getProperty("java.io.tmpdir");
    public static final String path = root + File.separator + "blackbox-python";

    /**
     * Remove all files in the previous temp folder, if it exists.
     * Then create a new temp empty temp folder.
     *
     * @throws IOException when the previous temporary folder exists
     *                     but we don't have permissions to delete it
     */
    public static void init() throws IOException {
        // Delete old tmp folder
        if (Path.of(path).toFile().exists()) {
            Files.walk(Path.of(path))
                .sorted(Comparator.reverseOrder())
                .forEachOrdered(path -> path.toFile().delete());
        }

        // Create tmp folder
        File folder = new File(path);
        folder.mkdirs();
        Logger.info("Created tmp folder at {}", folder.getAbsolutePath());
    }

    /**
     * Get the temporary folder.
     */
    public static File getFolder() {
        return new File(path);
    }

    /**
     * Create a new file in the temporary folder.
     *
     * Returns null if the file can't be created.
     */
    public static File createFile(String name) {
        File file = new File(path, name);

        try {
            file.createNewFile();
        } catch (IOException e) {
            Logger.error("Failed to create temporary file: {}", e);
            return null;
        }

        return file;
    }
}
