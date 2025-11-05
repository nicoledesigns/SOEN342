package com.trainsystem.repository;

import com.trainsystem.model.Trip;
import com.trainsystem.model.Ticket;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;    // for date comparisons
import java.util.stream.Collectors;   // for Collectors.toList()
import java.sql.*; //for database

public class TripRepository {

    private final List<Trip> trips; //current trips
    private static TripRepository tripRepository;
    private final List<Trip> historyTrips; //past trips
    private static final String DB_URL = "jdbc:sqlite:railway.db"; //for database



    private TripRepository() {
        trips = new ArrayList<>(); //current trips
        historyTrips = new ArrayList<>(); //past trips
        loadAllTripsFromDb();
    }

    public static TripRepository getTripRepository() {
        if (tripRepository==null) {
            tripRepository = new TripRepository();
        }
        return tripRepository;
    }

    public void addTrip(Trip trip) {
        if (!trips.contains(trip)) {
            trips.add(trip);
            saveTripToDb(trip);
        }
    }
    
    //before history
    public List<Trip> getAllTrips() {
        return trips;
    }

public List<Trip> getTripsByClient(String id) {
    return trips.stream()
            .filter(t -> t.getTickets().stream()
                          .anyMatch(ticket -> ticket.getClient().getId().equalsIgnoreCase(id)))
            .collect(Collectors.toList());
}

public List<Trip> getHistoryTripsByClient(String id) {
    return historyTrips.stream()
            .filter(t -> t.getTickets().stream()
                          .anyMatch(ticket -> ticket.getClient().getId().equalsIgnoreCase(id)))
            .collect(Collectors.toList());
}


    // Automatically moves past trips to history
    public void updateTripHistory() {
        LocalDate today = LocalDate.now();
        List<Trip> pastTrips = trips.stream()
            .filter(t -> t.getDate() != null && t.getDate().isBefore(today))
            .collect(Collectors.toList());

        historyTrips.addAll(pastTrips);
        trips.removeAll(pastTrips);       
    }


    // ---------- Database Methods ----------

    private void saveTripToDb(Trip trip) {
        String insertTrip = "INSERT OR IGNORE INTO Trip(trip_id, date) VALUES (?, ?)";
        String insertTripRoute = "INSERT OR IGNORE INTO TripRoute(trip_id, route_id, leg_order) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Save trip
            try (PreparedStatement pstmt = conn.prepareStatement(insertTrip)) {
                pstmt.setString(1, trip.getId());
                pstmt.setString(2, trip.getDate().toString());
                pstmt.executeUpdate();
            }
            // Save TripRoute for each ticket
            int legOrder = 1;
            for (Ticket ticket : trip.getTickets()) {
                String insertTicketSql = "UPDATE Ticket SET trip_id = ? WHERE ticket_id = ?";
                try (PreparedStatement pstmtTicket = conn.prepareStatement(insertTicketSql)) {
                    pstmtTicket.setString(1, trip.getId());
                    pstmtTicket.setString(2, ticket.getId());
                    pstmtTicket.executeUpdate();
                }

                try (PreparedStatement pstmtTripRoute = conn.prepareStatement(insertTripRoute)) {
                    pstmtTripRoute.setString(1, trip.getId());
                    pstmtTripRoute.setString(2, ticket.getRoute().getRouteId());
                    pstmtTripRoute.setInt(3, legOrder++);
                    pstmtTripRoute.executeUpdate();
                }
            }

        } catch (SQLException e) {
            System.err.println("Error saving trip: " + e.getMessage());
        }
    }

    public void loadAllTripsFromDb() {
        String selectTrips = "SELECT * FROM Trip";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rsTrips = stmt.executeQuery(selectTrips)) {

            TicketRepository ticketRepo = TicketRepository.getTicketRepository();

            while (rsTrips.next()) {
                String tripId = rsTrips.getString("trip_id");
                LocalDate date = LocalDate.parse(rsTrips.getString("date"));

                // Collect all tickets with this trip_id
                List<Ticket> tripTickets = ticketRepo.getAllTickets().stream()
                        .filter(t -> {
                            try {
                                String sql = "SELECT trip_id FROM Ticket WHERE ticket_id = ?";
                                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                                    pstmt.setString(1, t.getId());
                                    ResultSet rs = pstmt.executeQuery();
                                    return rs.next() && tripId.equals(rs.getString("trip_id"));
                                }
                            } catch (SQLException e) {
                                return false;
                            }
                        })
                        .collect(Collectors.toList());

                Trip trip = new Trip(tripId, date, tripTickets);
                trips.add(trip);
            }

        } catch (SQLException e) {
            System.err.println("Error loading trips: " + e.getMessage());
        }
    }
}

