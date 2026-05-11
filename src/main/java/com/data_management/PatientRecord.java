package com.data_management;

/**
 * Represents a single record of patient data at a specific point in time.
 * This class stores all necessary details for a single observation or
 * measurement
 * taken from a patient, including the type of record (such as ECG, blood
 * pressure),
 * the measurement value, and the exact timestamp when the measurement was
 * taken.
 */
public class PatientRecord {
    private int patientId;
    private String recordType; // Example: ECG, blood pressure, etc.
    private double measurementValue; // Example: heart rate
    private String rawValue;
    private long timestamp;

    /**
     * Constructs a new patient record with specified details.
     * 
     * @param patientId        the unique identifier for the patient
     * @param measurementValue the numerical value of the recorded measurement
     * @param recordType       the type of measurement (e.g., "ECG", "Blood
     *                         Pressure")
     * @param timestamp        the time at which the measurement was recorded, in
     *                         milliseconds since epoch
     */
    public PatientRecord(int patientId, double measurementValue, String recordType, long timestamp) {
        this.patientId = patientId;
        this.measurementValue = measurementValue;
        this.rawValue = Double.toString(measurementValue);
        this.recordType = recordType;
        this.timestamp = timestamp;
    }

    /**
     * Constructs a new patient record from a text value.
     *
     * <p>This is useful for data read from output files, because values like saturation may include
     * a percent sign and alert values may be text such as "triggered".
     *
     * @param patientId the unique identifier for the patient
     * @param rawValue the original value from the data source
     * @param recordType the type of measurement
     * @param timestamp the time at which the measurement was recorded
     */
    public PatientRecord(int patientId, String rawValue, String recordType, long timestamp) {
        this.patientId = patientId;
        this.rawValue = rawValue;
        this.measurementValue = parseMeasurementValue(rawValue);
        this.recordType = recordType;
        this.timestamp = timestamp;
    }

    private double parseMeasurementValue(String value) {
        try {
            return Double.parseDouble(value.replace("%", "").trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Returns the patient ID associated with this record.
     * 
     * @return the patient ID
     */
    public int getPatientId() {
        return patientId;
    }

    /**
     * Returns the measurement value of this record.
     * 
     * @return the measurement value
     */
    public double getMeasurementValue() {
        return measurementValue;
    }

    /**
     * Returns the original value as it was read from the data source.
     *
     * @return the raw value text
     */
    public String getRawValue() {
        return rawValue;
    }

    /**
     * Returns the timestamp when this record was taken.
     * 
     * @return the timestamp in milliseconds since epoch
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the type of record (e.g., "ECG", "Blood Pressure").
     * 
     * @return the record type
     */
    public String getRecordType() {
        return recordType;
    }
}
