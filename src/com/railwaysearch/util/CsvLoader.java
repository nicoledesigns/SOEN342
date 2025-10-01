// this class reads the routes.csv file, converts each row into a Route, and stores it in the RouteRepository.
package com.railwaysearch.util;

import com.railwaysearch.model.Route;
import com.railwaysearch.model.TrainType;
import com.railwaysearch.repository.RouteRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CsvLoader {

    /**
     * Loads all routes from a CSV file into the repository.
     * @param csvFilePath path to routes.csv
     * @param repo repository where parsed routes will be stored
     */
    public static void load(String csvFilePath, RouteRepository repo) {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(csvFilePath))) {

            // Skip header line
            String line = br.readLine();
            if (line == null) {
                System.err.println("CSV file is empty.");
                return;
            }

            while ((line = br.readLine()) != null) {
                try {
                    // --- simple split (works if you don't have quoted commas) ---
                    // If you have quoted commas in Days column, use a CSV library like OpenCSV.
                    String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                    if (tokens.length < 9) {
                        System.err.println("Skipping invalid row: " + line);
                        continue;
                    }

                    String routeId = tokens[0].trim();
                    String departure = tokens[1].trim();
                    String arrival = tokens[2].trim();
                    String departureTime = tokens[3].trim();
                    String arrivalTime = tokens[4].trim();
                    TrainType type = TrainType.valueOf(tokens[5].trim().toUpperCase());
                    String days = tokens[6].replace("\"", "").trim();
                    double firstClassPrice = Double.parseDouble(tokens[7].trim());
                    double secondClassPrice = Double.parseDouble(tokens[8].trim());
                    //route ID stays private!
                    Route route = new Route(departure, arrival,
                                            departureTime, arrivalTime,
                                            type, days,
                                            firstClassPrice, secondClassPrice);

                    repo.addRoute(route);

                } catch (Exception e) {
                    System.err.println("Error parsing row: " + line + " (" + e.getMessage() + ")");
                }
            }

 //           System.out.println("CSV loading complete. Routes loaded: " + repo.size());

        } catch (IOException e) {
            System.err.println("Failed to read CSV file: " + e.getMessage());
        }
    }
}
