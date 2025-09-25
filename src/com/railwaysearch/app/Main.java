package com.railwaysearch.app;

import com.railwaysearch.repository.RouteRepository;
import com.railwaysearch.util.CsvLoader;
import com.railwaysearch.model.Route;
import com.railwaysearch.model.TrainType;


public class Main {

    public static void main(String[] args) {

        // 1️- Create the in-memory repository
        RouteRepository repo = new RouteRepository();

        // 2️- Load data from the CSV file into the repository
        // Adjust the path if your routes.csv is in a different folder.
        String csvPath = "routes.csv";
        CsvLoader.load("routes.csv", repo);

        // 3️- Show total number of routes loaded
        System.out.println("\nTotal routes loaded: " + repo.size());

        // 4️- Example queries
        System.out.println("\n=== Routes departing from A Coruña ===");
        for (Route r : repo.findByDepartureCity("A Coruña")) {
            System.out.println(r);
        }

        System.out.println("\n=== Routes arriving at Santander ===");
        for (Route r : repo.findByArrivalCity("Santander")) {
            System.out.println(r);
        }

        System.out.println("\n=== All RJX train type routes ===");
        for (Route r : repo.findByTrainType(TrainType.RJX)) {
            System.out.println(r);
        }

        // 5️- Program finished
        System.out.println("\nDemo complete.");
    }
}
