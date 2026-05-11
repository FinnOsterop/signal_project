package data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.data_management.Patient;
import com.data_management.PatientRecord;

class PatientTest {

    @Test
    void testGetRecordsFiltersByTimeRange() {
        Patient patient = new Patient(1);
        patient.addRecord(100.0, "ECG", 1000);
        patient.addRecord(200.0, "ECG", 2000);
        patient.addRecord(300.0, "ECG", 3000);

        List<PatientRecord> records = patient.getRecords(1500, 2500);

        assertEquals(1, records.size());
        assertEquals(200.0, records.get(0).getMeasurementValue());
    }

    @Test
    void testGetRecordsReturnsEmptyListWhenNoRecordsMatch() {
        Patient patient = new Patient(1);
        patient.addRecord(100.0, "ECG", 1000);

        assertEquals(0, patient.getRecords(2000, 3000).size());
    }
}
