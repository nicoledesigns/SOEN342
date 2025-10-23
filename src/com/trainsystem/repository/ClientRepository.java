package com.railwaysearch.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.ArrayList;
import com.railwaysearch.model.Client;

public class ClientRepository {

    private static final String file = "clients.csv";
    private List<Client> clients = new ArrayList<>();

    public ClientRepository() {
        loadClientsFromCSV();
    }

    private void loadClientsFromCSV() {
        clients.clear();
        File file = new File(this.file);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(this.file))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] client_info = line.split(",");
                if (client_info.length == 4) {
                    String id = client_info[0];
                    String firstName = client_info[1];
                    String lastName = client_info[2];
                    int age = Integer.parseInt(client_info[3]);
                    clients.add(new Client(firstName, lastName, age, id));

                }

            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveClientsToCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(this.file))) {
            pw.println("id,first_name,last_name,age");
            for (Client client : clients) {
                pw.println(client.getId() + "," + client.getFirstName() + "," + client.getLastName() + ","
                        + client.getAge());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addClient(Client client) {
            clients.add(client);
            saveClientsToCSV();
    }


    public Client findbyIDAndLastName(String id,String last_name) {
        for (Client c : clients) {
            if (c.getId().equals(id) && c.getLastName().equals(last_name))
                return c;
        }
        return null;

    }

}
