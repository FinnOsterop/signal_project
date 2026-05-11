package alerts;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;

class AlertGeneratorTest {

    @Test
    void testCriticalBloodPressureAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 181.0, "SystolicPressure", 1000);

        AlertGenerator generator = evaluateFirstPatient(storage);

        assertHasAlert(generator, "Critical systolic pressure");
    }

    @Test
    void testBloodPressureTrendAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 100.0, "SystolicPressure", 1000);
        storage.addPatientData(1, 112.0, "SystolicPressure", 2000);
        storage.addPatientData(1, 125.0, "SystolicPressure", 3000);

        AlertGenerator generator = evaluateFirstPatient(storage);

        assertHasAlert(generator, "Blood pressure trend");
    }

    @Test
    void testLowSaturationAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, "91%", "Saturation", 1000);

        AlertGenerator generator = evaluateFirstPatient(storage);

        assertHasAlert(generator, "Low oxygen saturation");
    }

    @Test
    void testRapidSaturationDropAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, "98%", "Saturation", 1000);
        storage.addPatientData(1, "92%", "Saturation", 2000);

        AlertGenerator generator = evaluateFirstPatient(storage);

        assertHasAlert(generator, "Rapid oxygen saturation drop");
    }

    @Test
    void testHypotensiveHypoxemiaAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 89.0, "SystolicPressure", 1000);
        storage.addPatientData(1, "91%", "Saturation", 2000);

        AlertGenerator generator = evaluateFirstPatient(storage);

        assertHasAlert(generator, "Hypotensive hypoxemia");
    }

    @Test
    void testEcgPeakAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 0.1, "ECG", 1000);
        storage.addPatientData(1, 0.1, "ECG", 2000);
        storage.addPatientData(1, 0.1, "ECG", 3000);
        storage.addPatientData(1, 1.0, "ECG", 4000);

        AlertGenerator generator = evaluateFirstPatient(storage);

        assertHasAlert(generator, "Abnormal ECG peak");
    }

    @Test
    void testTriggeredAlertButton() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, "triggered", "Alert", 1000);

        AlertGenerator generator = evaluateFirstPatient(storage);

        assertHasAlert(generator, "Alert button triggered");
    }

    private AlertGenerator evaluateFirstPatient(DataStorage storage) {
        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);
        return generator;
    }

    private void assertHasAlert(AlertGenerator generator, String condition) {
        boolean found = false;
        for (Alert alert : generator.getAlerts()) {
            if (condition.equals(alert.getCondition())) {
                found = true;
            }
        }
        assertTrue(found, "Expected alert condition: " + condition);
    }
}
