package com.cardio_generator;

import java.io.IOException;

import com.data_management.DataStorage;

/**
 * Main class that can start either the simulator or the data storage demo.
 */
public class Main {

    /**
     * Starts DataStorage when the first argument is "DataStorage", otherwise starts the simulator.
     *
     * @param args command-line arguments
     * @throws IOException if the simulator cannot create a file output directory
     */
    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equals("DataStorage")) {
            DataStorage.main(new String[] {});
        } else {
            HealthDataSimulator.main(args);
        }
    }
}
