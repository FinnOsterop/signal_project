package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated alerts for patients.
 *
 * <p>It keeps track of which patients currently have an active alert.
 */
public class AlertGenerator implements PatientDataGenerator {

    // Changed constant randomGenerator to UPPER_SNAKE_CASE 
    /**
     * 
     * Random generator used for alert chances.
     */
    public static final Random RANDOM_GENERATOR = new Random();
    // Changed field AlertStates to lowerCaseCamel
    private boolean[] alertStates; // false = resolved, true = pressed

    /**
     * Creates the alert generator and stores the alert state for each patient.
     *
     * @param patientCount number of patients in the simulation
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Generates an alert change for one patient.
     *
     * <p>The method can trigger a new alert, resolve an existing alert, or do nothing.
     *
     * @param patientId ID of the patient
     * @param outputStrategy strategy used to output alert changes
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                // Changed variable Lambda to lowerCamelCase 
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
