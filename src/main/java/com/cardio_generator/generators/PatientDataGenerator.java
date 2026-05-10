package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Interface for classes that generate data for a patient.
 *
 * <p>Each generator makes one type of measurement and sends it to an output strategy.
 */
public interface PatientDataGenerator {
    /**
     * Geerates a new value for one patient.
     *
     * @param patientId ID of the patient
     * @param outputStrategy strategy used to output the data
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
