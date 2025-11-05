package com.trainsystem.service;

import com.trainsystem.model.Client;
import com.trainsystem.model.RouteConnection;
import com.trainsystem.repository.ClientRepository;
import com.trainsystem.repository.RouteRepository;

import java.util.List;

public class ClientService {

    private static ClientService instance;
    private final ClientRepository clientRepository;
    private final RouteRepository routeRepository;
    private final TripService tripService;

    private ClientService(ClientRepository clientRepository, RouteRepository routeRepository, TripService tripService) {
        this.clientRepository = clientRepository;
        this.routeRepository = routeRepository;
        this.tripService = tripService;
    }

    public static ClientService getClientService() {
        if (instance == null) {
            instance = new ClientService(
                    ClientRepository.getClientRepository(),
                    RouteRepository.getRouteRepository(),
                    TripService.getTripService()
            );
        }
        return instance;
    }

    // --- Client registration ---
    public Client registerClient(String id, String firstName, String lastName, int age) {
        if (clientRepository.findById(id) != null) {
            System.out.println("❌ Error: A client with this ID already exists!");
            return null;
        }
        Client newClient = new Client(id, firstName, lastName, age);
        clientRepository.addClient(newClient);
        System.out.println("✅ New client registered successfully: " + newClient);
        return newClient;
    }

    // --- Find clients ---
    public Client findById(String id) {
        return clientRepository.findById(id);
    }

    public Client findClient(String id, String lastName) {
        return clientRepository.findByIdAndLastName(id, lastName);
    }

    public void listAllClients() {
        System.out.println("\n=== Registered Clients ===");
        for (Client c : clientRepository.getAllClients()) {
            System.out.println(c);
        }
    }

    // --- View connections for a route ---
    public List<RouteConnection> viewConnections(String departureCity, String arrivalCity) {
        List<RouteConnection> connections = routeRepository.findAllConnections(departureCity, arrivalCity);
        if (connections.isEmpty()) {
            System.out.println("No connections found from " + departureCity + " to " + arrivalCity);
        } else {
            System.out.println("\n=== Available RouteConnection ===");
            connections.forEach(System.out::println);
        }
        return connections;
    }

    // --- Book trips for a client ---
    public void bookTrip(String clientId, String lastName, List<RouteConnection> connections) {
        Client client = findClient(clientId, lastName);
        if (client == null) {
            System.out.println("❌ Client not found!");
            return;
        }

        for (RouteConnection connection : connections) {
            if (tripService.validateBooking(connection, List.of(client))) {
                tripService.generateTrip(connection, List.of(client));
                System.out.println("✅ Trip booked successfully for " + client.getFirstName() + " " + client.getLastName());
            } else {
                System.out.println("❌ Trip booking failed: Client already has a ticket for one of these routes.");
            }
        }
    }
}
