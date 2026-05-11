package com.alerts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private List<Alert> alerts;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.alerts = new ArrayList<>();
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        List<PatientRecord> records = patient.getAllRecords();
        Collections.sort(records, new Comparator<PatientRecord>() {
            @Override
            public int compare(PatientRecord first, PatientRecord second) {
                return Long.compare(first.getTimestamp(), second.getTimestamp());
            }
        });

        checkBloodPressureAlerts(patient, records);
        checkSaturationAlerts(patient, records);
        checkCombinedAlert(patient, records);
        checkEcgAlerts(patient, records);
        checkTriggeredAlerts(patient, records);
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        alerts.add(alert);
    }

    /**
     * Returns all alerts that were triggered by this generator.
     *
     * @return list of generated alerts
     */
    public List<Alert> getAlerts() {
        return new ArrayList<>(alerts);
    }

    /**
     * Clears all generated alerts.
     */
    public void clearAlerts() {
        alerts.clear();
    }

    private void checkBloodPressureAlerts(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> systolicRecords = recordsOfType(records, "SystolicPressure");
        List<PatientRecord> diastolicRecords = recordsOfType(records, "DiastolicPressure");

        for (PatientRecord record : systolicRecords) {
            double value = record.getMeasurementValue();
            if (value > 180 || value < 90) {
                triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Critical systolic pressure",
                        record.getTimestamp()));
            }
        }

        for (PatientRecord record : diastolicRecords) {
            double value = record.getMeasurementValue();
            if (value > 120 || value < 60) {
                triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Critical diastolic pressure",
                        record.getTimestamp()));
            }
        }

        if (hasTrend(systolicRecords)) {
            triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Blood pressure trend",
                    systolicRecords.get(systolicRecords.size() - 1).getTimestamp()));
        }

        if (hasTrend(diastolicRecords)) {
            triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Blood pressure trend",
                    diastolicRecords.get(diastolicRecords.size() - 1).getTimestamp()));
        }
    }

    private void checkSaturationAlerts(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> saturationRecords = recordsOfType(records, "Saturation");

        for (PatientRecord record : saturationRecords) {
            if (record.getMeasurementValue() < 92) {
                triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Low oxygen saturation",
                        record.getTimestamp()));
            }
        }

        for (int i = 1; i < saturationRecords.size(); i++) {
            PatientRecord previous = saturationRecords.get(i - 1);
            PatientRecord current = saturationRecords.get(i);
            long timeDifference = current.getTimestamp() - previous.getTimestamp();
            double drop = previous.getMeasurementValue() - current.getMeasurementValue();

            if (timeDifference <= 10 * 60 * 1000 && drop >= 5) {
                triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Rapid oxygen saturation drop",
                        current.getTimestamp()));
            }
        }
    }

    private void checkCombinedAlert(Patient patient, List<PatientRecord> records) {
        PatientRecord latestSystolic = latestRecord(records, "SystolicPressure");
        PatientRecord latestSaturation = latestRecord(records, "Saturation");

        if (latestSystolic == null || latestSaturation == null) {
            return;
        }

        if (latestSystolic.getMeasurementValue() < 90 && latestSaturation.getMeasurementValue() < 92) {
            long timestamp = latestSystolic.getTimestamp();
            if (latestSaturation.getTimestamp() > timestamp) {
                timestamp = latestSaturation.getTimestamp();
            }
            triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Hypotensive hypoxemia", timestamp));
        }
    }

    private void checkEcgAlerts(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> ecgRecords = recordsOfType(records, "ECG");
        int windowSize = 3;

        for (int i = windowSize; i < ecgRecords.size(); i++) {
            double average = 0.0;
            for (int j = i - windowSize; j < i; j++) {
                average += ecgRecords.get(j).getMeasurementValue();
            }
            average = average / windowSize;

            PatientRecord current = ecgRecords.get(i);
            if (current.getMeasurementValue() > average + 0.5) {
                triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Abnormal ECG peak",
                        current.getTimestamp()));
            }
        }
    }

    private void checkTriggeredAlerts(Patient patient, List<PatientRecord> records) {
        for (PatientRecord record : recordsOfType(records, "Alert")) {
            if ("triggered".equalsIgnoreCase(record.getRawValue())) {
                triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Alert button triggered",
                        record.getTimestamp()));
            } else if ("resolved".equalsIgnoreCase(record.getRawValue())) {
                triggerAlert(new Alert(Integer.toString(patient.getPatientId()), "Alert button resolved",
                        record.getTimestamp()));
            }
        }
    }

    private List<PatientRecord> recordsOfType(List<PatientRecord> records, String recordType) {
        List<PatientRecord> matchingRecords = new ArrayList<>();
        for (PatientRecord record : records) {
            if (recordType.equals(record.getRecordType())) {
                matchingRecords.add(record);
            }
        }
        return matchingRecords;
    }

    private PatientRecord latestRecord(List<PatientRecord> records, String recordType) {
        List<PatientRecord> matchingRecords = recordsOfType(records, recordType);
        if (matchingRecords.isEmpty()) {
            return null;
        }
        return matchingRecords.get(matchingRecords.size() - 1);
    }

    private boolean hasTrend(List<PatientRecord> records) {
        if (records.size() < 3) {
            return false;
        }

        for (int i = 2; i < records.size(); i++) {
            double firstChange = records.get(i - 1).getMeasurementValue() - records.get(i - 2).getMeasurementValue();
            double secondChange = records.get(i).getMeasurementValue() - records.get(i - 1).getMeasurementValue();

            boolean increasing = firstChange > 10 && secondChange > 10;
            boolean decreasing = firstChange < -10 && secondChange < -10;
            if (increasing || decreasing) {
                return true;
            }
        }

        return false;
    }
}
