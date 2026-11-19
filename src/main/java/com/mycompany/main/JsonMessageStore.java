package com.mycompany.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Saves stored messages to a JSON file and reads them back.
 *
 * ATTRIBUTION:
 * The Gson calls below follow the "Array Examples" and "Object Examples" in the
 * official Gson User Guide (Google): https://google.github.io/gson/UserGuide.html
 *   - gson.toJson(object, writer)           writes objects as JSON
 *   - gson.fromJson(reader, Type[].class)   reads a JSON array into a Java array
 * Files.newBufferedReader / newBufferedWriter come from the Java documentation:
 * https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/nio/file/Files.html
 *
 * @author Mc Chopper
 * @version 3.0
 */
public class JsonMessageStore {

    // Pretty printing makes the JSON file easy to read
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path filePath;

    /**
     * @param filePath The path of the JSON file, e.g. "messages.json".
     */
    public JsonMessageStore(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Writes the given messages to the JSON file, replacing its old contents.
     *
     * @param messages The stored messages to save.
     * @throws IOException if the file cannot be written.
     */
    public void save(List<Message> messages) throws IOException {
        try (Writer writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            GSON.toJson(messages, writer);
        }
    }

    /**
     * Reads the JSON file into a list of messages.
     *
     * @return The stored messages, or an empty list if the file does not exist yet.
     * @throws IOException if the file exists but cannot be read.
     */
    public List<Message> load() throws IOException {
        if (!Files.exists(filePath)) {
            return Collections.emptyList();
        }
        try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            Message[] messages = GSON.fromJson(reader, Message[].class);
            // fromJson returns null for an empty file
            return messages == null ? Collections.emptyList() : Arrays.asList(messages);
        }
    }
}
