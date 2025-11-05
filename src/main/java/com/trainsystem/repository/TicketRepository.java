package com.trainsystem.repository;
import com.trainsystem.model.Client;
import com.trainsystem.model.Route;
import com.trainsystem.model.Ticket;
import com.trainsystem.repository.ClientRepository;
import com.trainsystem.repository.RouteRepository;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class TicketRepository {

    private final List<Ticket> tickets;
    private static TicketRepository ticketRepository;
    private static final String DB_URL = "jdbc:sqlite:railway.db"; //for database

    private TicketRepository() {
        tickets = new ArrayList<>();
        loadAllTicketsFromDb();

    }
    public List<Ticket> getAllTickets() {
    return new ArrayList<>(tickets); // return a copy to avoid external modification
}


    public static TicketRepository getTicketRepository() {
        if (ticketRepository==null) {
            ticketRepository = new TicketRepository();
        }
        return ticketRepository;
    }

    public void addTicket(Ticket ticket) {
        //tickets.add(ticket);
        saveTicketToDb(ticket);

    }


     // ---------- Database methods ----------
    private void saveTicketToDb(Ticket ticket) {
        String insertTicket = "INSERT OR IGNORE INTO Ticket(ticket_id, trip_id, route_id, client_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertTicket)) {

            pstmt.setString(1, ticket.getId());
            // ticket's trip_id will be set via TripService when Trip is created
            pstmt.setString(2, ticket.getTripId()); // temporarily null, TripService will update Trip_id
            pstmt.setString(3, ticket.getRoute().getRouteId());
            pstmt.setString(4, ticket.getClient().getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving ticket: " + e.getMessage());
        }
    }

    public void loadAllTicketsFromDb() {
        String selectTickets = "SELECT * FROM Ticket";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectTickets)) {

            RouteRepository routeRepo = RouteRepository.getRouteRepository();
            ClientRepository clientRepo = ClientRepository.getClientRepository();

            while (rs.next()) {
                String ticketId = rs.getString("ticket_id");
                String routeId = rs.getString("route_id");
                String clientId = rs.getString("client_id");

                Route route = routeRepo.getAllRoutes().stream()
                        .filter(r -> r.getRouteId().equals(routeId))
                        .findFirst().orElse(null);

                Client client = clientRepo.findById(clientId);

                if (route != null && client != null) {
                    Ticket ticket = new Ticket(ticketId, route, client);
                    tickets.add(ticket);
                    client.addTicket(ticket); // maintain client-tickets link in memory
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading tickets: " + e.getMessage());
        }
    }
}