package com.data_management;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Reads patient data from files created by the file output strategy.
 */
public class FileDataReader implements DataReader {
    private final Path directory;

    /**
     * Creates a reader for a directory with simulator output files.
     *
     * @param directory directory that contains the generated text files
     */
    public FileDataReader(Path directory) {
        this.directory = directory;
    }

    /**
     * Reads all text files in the directory and stores the parsed records.
     *
     * @param dataStorage the storage where parsed data will be stored
     * @throws IOException if files cannot be read
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        try (DirectoryStream<Path> files = Files.newDirectoryStream(directory, "*.txt")) {
            for (Path file : files) {
                readFile(file, dataStorage);
            }
        }
    }

    private void readFile(Path file, DataStorage dataStorage) throws IOException {
        for (String line : Files.readAllLines(file)) {
            readLine(line, dataStorage);
        }
    }

    private void readLine(String line, DataStorage dataStorage) {
        String[] parts = line.split(", ");
        if (parts.length != 4) {
            return;
        }

        if (!parts[0].startsWith("Patient ID: ")
                || !parts[1].startsWith("Timestamp: ")
                || !parts[2].startsWith("Label: ")
                || !parts[3].startsWith("Data: ")) {
            return;
        }

        int patientId = Integer.parseInt(parts[0].substring("Patient ID: ".length()));
        long timestamp = Long.parseLong(parts[1].substring("Timestamp: ".length()));
        String label = parts[2].substring("Label: ".length());
        String data = parts[3].substring("Data: ".length());

        dataStorage.addPatientData(patientId, data, label, timestamp);
    }
}
