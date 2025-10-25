package com.trainsystem.service;

import com.trainsystem.model.Client;
import com.trainsystem.repository.ClientRepository;

public class ClientService {

    private static ClientService instance;
    private final ClientRepository clientRepository;

    private ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }
    public static ClientService getClientService() {
        if (instance == null) {
            instance = new ClientService(ClientRepository.getClientRepository());
        }
        return instance;
    }
/** 
    public Client registerClient(String firstName, String lastName, int age) {
        Client newClient = new Client(firstName, lastName, age);
        clientRepository.addClient(newClient);
        System.out.println("New client registered successfully: " + newClient);
        return newClient;
    }
*/
//new method with id
    public Client registerClient(String id, String firstName, String lastName, int age) {
        // Check if client already exists with same ID
        if (clientRepository.findById(id) != null) {
         System.out.println("Error: A client with this ID already exists!");
         return null;
    }

        Client newClient = new Client(id, firstName, lastName, age);
        clientRepository.addClient(newClient);
        System.out.println("New client registered successfully: " + newClient);
        return newClient;
    }

//find client by id only
    public Client findById(String id) {
        return clientRepository.findById(id);
    }

//find client by id and name
    public Client findClient(String id, String lastName) {
        return clientRepository.findByIdAndLastName(id, lastName);
    }

    public void listAllClients() {
        System.out.println("\n=== Registered Clients ===");
        for (Client c : clientRepository.getAllClients()) {
            System.out.println(c);
        }
    }
}
