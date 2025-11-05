package com.trainsystem;

import com.trainsystem.repository.RouteRepository;
import com.trainsystem.util.CsvLoader;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbTest {

    public static void main(String[] args) {
        // Step 1: Connect to SQLite database
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:railway.db")) {
            if (conn != null) {
                System.out.println("Connected to SQLite successfully. File: railway.db");
            }
        } catch (SQLException e) {
            System.err.println("DB connection failed: " + e.getMessage());
            return; // stop if DB connection fails
        }

        // Step 2: Load routes CSV
        RouteRepository repo = RouteRepository.getRouteRepository();
        InputStream csvStream = DbTest.class.getResourceAsStream("/routes.csv"); // make sure CSV is in src/main/resources
        if (csvStream == null) {
            System.err.println("Failed to find routes.csv in resources!");
            return;
        }

        CsvLoader.load(csvStream, repo);

        // Step 3: Print total routes loaded
        System.out.println("Total routes loaded: " + repo.getAllRoutes().size());
    }
}
