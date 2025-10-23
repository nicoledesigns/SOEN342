package com.trainsystem.repository;

import com.trainsystem.model.Client;
import com.trainsystem.model.Trip;

import java.util.ArrayList;
import java.util.List;

public class ClientRepository {

    private final List<Client> clients;
    private static ClientRepository clientRepository;

    private ClientRepository() {
        clients = new ArrayList<>();
    }

    public static ClientRepository getClientRepository() {
        if (clientRepository==null) {
            return new ClientRepository();
        }
        else return clientRepository;
    }

    public void addClient(Client client) {
        if (!clients.contains(client)) {
            clients.add(client);
        }
    }
}
