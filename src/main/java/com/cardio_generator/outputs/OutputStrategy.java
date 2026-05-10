package com.cardio_generator.outputs;

/**
 * Interface for different ways to output generated health data.
 *
 * <p>This makes it possible to output the same data to different places, like files or the
 * console.
 */
public interface OutputStrategy {
    /**
     * Outputs one measurement
     *
     * @param patientId ID of the patient
     * @param timestamp time of the measurement
     * @param label type of measurement
     * @param data measurement value
     */
    void output(int patientId, long timestamp, String label, String data);
}
