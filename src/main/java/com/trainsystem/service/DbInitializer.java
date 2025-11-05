package com.trainsystem.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbInitializer {

    private static final String URL = "jdbc:sqlite:railway.db";

    public static void initializeDatabase() {
        String sql = """

            CREATE TABLE IF NOT EXISTS Client (
                id TEXT PRIMARY KEY,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                age INTEGER
            );

            CREATE TABLE IF NOT EXISTS Route (
                route_id TEXT PRIMARY KEY,
                departure_city TEXT NOT NULL,
                arrival_city TEXT NOT NULL,
                departure_time TEXT NOT NULL,
                arrival_time TEXT NOT NULL,
                train_type TEXT NOT NULL,
                days_of_operation TEXT,
                first_class_price REAL,
                second_class_price REAL
            );


            CREATE TABLE IF NOT EXISTS Trip (
                trip_id TEXT PRIMARY KEY,
                date TEXT NOT NULL
            );

            CREATE TABLE IF NOT EXISTS Ticket (
                ticket_id TEXT PRIMARY KEY,
                trip_id TEXT NOT NULL,
                route_id TEXT NOT NULL,
                client_id TEXT NOT NULL,
                FOREIGN KEY (trip_id) REFERENCES Trip(trip_id),
                FOREIGN KEY (route_id) REFERENCES Route(route_id),
                FOREIGN KEY (client_id) REFERENCES Client(id)
            );

            
            CREATE TABLE IF NOT EXISTS TripRoute (
                trip_id TEXT NOT NULL,
                route_id TEXT NOT NULL,
                leg_order INTEGER NOT NULL,
                PRIMARY KEY (trip_id, leg_order),
                FOREIGN KEY (trip_id) REFERENCES Trip(trip_id),
                FOREIGN KEY (route_id) REFERENCES Route(route_id)
            );
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        initializeDatabase();
    }
}
