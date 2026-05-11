package data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.PatientRecord;

class FileDataReaderTest {

    @Test
    void testReadDataFromFileOutput() throws IOException {
        Path tempDir = Files.createTempDirectory("file-reader-test");
        Path file = tempDir.resolve("Saturation.txt");
        Files.write(file, Arrays.asList(
                "Patient ID: 1, Timestamp: 1000, Label: Saturation, Data: 91%",
                "Patient ID: 1, Timestamp: 2000, Label: Saturation, Data: 96%"));

        DataStorage storage = new DataStorage();
        FileDataReader reader = new FileDataReader(tempDir);

        reader.readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0, 3000);
        assertEquals(2, records.size());
        assertEquals(91.0, records.get(0).getMeasurementValue());
        assertEquals("91%", records.get(0).getRawValue());
    }

    @Test
    void testInvalidLinesAreSkipped() throws IOException {
        Path tempDir = Files.createTempDirectory("file-reader-test");
        Path file = tempDir.resolve("ECG.txt");
        Files.write(file, Arrays.asList(
                "This line is not valid",
                "Patient ID: 2, Timestamp: 1000, Label: ECG, Data: 0.4"));

        DataStorage storage = new DataStorage();
        FileDataReader reader = new FileDataReader(tempDir);

        reader.readData(storage);

        assertEquals(1, storage.getRecords(2, 0, 2000).size());
    }
}
