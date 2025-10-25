package com.trainsystem.repository;

import com.trainsystem.model.Client;

import java.util.ArrayList;
import java.util.List;

public class ClientRepository {

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
