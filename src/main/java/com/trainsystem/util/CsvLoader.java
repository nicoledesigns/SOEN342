package com.trainsystem.util;

import com.trainsystem.model.Route;
import com.trainsystem.model.TrainType;
import com.trainsystem.repository.RouteRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CsvLoader {

    // Map CSV train type strings to enum values
    private static final Map<String, TrainType> TRAIN_TYPE_MAP = new HashMap<>();

    static {
        TRAIN_TYPE_MAP.put("RJX", TrainType.RJX);
        TRAIN_TYPE_MAP.put("ICE", TrainType.ICE);
        TRAIN_TYPE_MAP.put("AVE", TrainType.AVE);
        TRAIN_TYPE_MAP.put("INTERCITY", TrainType.INTERCITY);
        TRAIN_TYPE_MAP.put("FRECCIAROSSA", TrainType.FRECCIAROSSA);
        TRAIN_TYPE_MAP.put("REGIOEXPRESS", TrainType.REGIOEXPRESS);
        TRAIN_TYPE_MAP.put("EUROCITY", TrainType.EUROCITY);
        TRAIN_TYPE_MAP.put("TGV", TrainType.TGV);
        TRAIN_TYPE_MAP.put("ITALO", TrainType.ITALO);
        TRAIN_TYPE_MAP.put("RE", TrainType.RE);
        TRAIN_TYPE_MAP.put("NIGHTJET", TrainType.NIGHTJET);
        TRAIN_TYPE_MAP.put("INTERCITÉS", TrainType.INTERCITÉS);
        TRAIN_TYPE_MAP.put("THALYS", TrainType.THALYS);
        TRAIN_TYPE_MAP.put("EUROSTAR", TrainType.EUROSTAR);
        TRAIN_TYPE_MAP.put("TER", TrainType.TER);
        TRAIN_TYPE_MAP.put("RAILJET", TrainType.RAILJET);
        TRAIN_TYPE_MAP.put("IC", TrainType.IC);
    }

    public static void load(InputStream inputStream, RouteRepository repo) {
        if (inputStream == null) {
            System.err.println("CSV InputStream is null!");
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            // Skip header
            String line = br.readLine();
            if (line == null) return;

            while ((line = br.readLine()) != null) {
                try {
                    String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    if (tokens.length < 9) continue;

                    String routeId = tokens[0].trim();
                    String departure = tokens[1].trim();
                    String arrival = tokens[2].trim();
                    String departureTime = tokens[3].trim();
                    String arrivalTime = tokens[4].trim();

                    // Map CSV string to enum safely
                    String typeStr = tokens[5].trim().toUpperCase().replace(" ", "");
                    TrainType type = TRAIN_TYPE_MAP.get(typeStr);
                    if (type == null) {
                        System.err.println("Unknown TrainType for row: " + line);
                        continue; // skip this row
                    }

                    String days = tokens[6].replace("\"", "").trim();
                    double firstClassPrice = Double.parseDouble(tokens[7].trim());
                    double secondClassPrice = Double.parseDouble(tokens[8].trim());

                    Route route = new Route(departure, arrival, departureTime, arrivalTime, type, days, firstClassPrice, secondClassPrice);
                    repo.addRoute(route);

                } catch (Exception e) {
                    System.err.println("Error parsing CSV row: " + line + " (" + e.getMessage() + ")");
                }
            }

            System.out.println("CSV loading completed. Total routes: " + repo.getAllRoutes().size());

        } catch (IOException e) {
            System.err.println("Failed to read CSV: " + e.getMessage());
        }
    }
}
