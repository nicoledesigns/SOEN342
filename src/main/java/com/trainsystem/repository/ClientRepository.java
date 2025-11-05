package com.trainsystem.repository;

import com.trainsystem.model.Client;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;


public class ClientRepository {


    private static ClientRepository clientRepository;
    private static final String URL = "jdbc:sqlite:railway.db";

    private ClientRepository() { }

    public static ClientRepository getClientRepository() {
        if (clientRepository == null) {
            clientRepository = new ClientRepository();
        }
        return clientRepository;
    }

    public void addClient(Client client) {
        String sql = "INSERT INTO Client(id, first_name, last_name, age) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, client.getId());
            pstmt.setString(2, client.getFirstName());
            pstmt.setString(3, client.getLastName());
            pstmt.setInt(4, client.getAge());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting client: " + e.getMessage());
        }
    }

    public Client findById(String id) {
        String sql = "SELECT * FROM Client WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return new Client(rs);
        } catch (SQLException e) {
            System.err.println("Error finding client: " + e.getMessage());
        }
        return null;
    }

    public Client findByIdAndLastName(String id, String lastName) {
        String sql = "SELECT * FROM Client WHERE id = ? AND last_name = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, lastName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return new Client(rs);
        } catch (SQLException e) {
            System.err.println("Error finding client: " + e.getMessage());
        }
        return null;
    }

    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM Client";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clients.add(new Client(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving clients: " + e.getMessage());
        }
        return clients;
    }
}
/**  
    private final List<Client> clients;
    private static ClientRepository clientRepository;

    private ClientRepository() {
        clients = new ArrayList<>();
    }

    public static ClientRepository getClientRepository() {
        if (clientRepository == null) {
            clientRepository = new ClientRepository();
        }
        return clientRepository;
    }

    public List<Client> getAllClients() {
        return new ArrayList<>(clients);
    }

    public void addClient(Client client) {
        if (!clients.contains(client)) {
            clients.add(client);
        }
    }

    //find client by id only
    public Client findById(String id) {
        for (Client c : clients) {
            if (c.getId().equals(id)) {
            return c;
        }
    }
        return null;
    }
    
    //find client by id and name
    public Client findByIdAndLastName(String id, String lastName) {
        for (Client c : clients) {
            if (c.getId().equals(id) && c.getLastName().equalsIgnoreCase(lastName)) {
                return c;
            }
        }
        return null;
    }
}
    */


