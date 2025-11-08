package com.trainsystem.app;

import com.trainsystem.dto.SearchResultDTO;
import com.trainsystem.model.Client;
import com.trainsystem.model.RouteConnection;
import com.trainsystem.model.SearchCriteria;
import com.trainsystem.model.Ticket;
import com.trainsystem.repository.ClientRepository;
import com.trainsystem.service.ClientService;
import com.trainsystem.service.ConnectionService;
import com.trainsystem.service.TripService;
import com.trainsystem.db.DbInitializer;
import com.trainsystem.model.Route;
import com.trainsystem.util.TimeUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class TrainApp {
    private static ConnectionService connectionService;
    private static ClientService clientService;
    private static ClientRepository clientRepository;
    private static TripService tripService;

    private static final String DB_PATH = System.getProperty("user.dir") + "/railway.db";


  public static Connection getDatabaseConnection() {
        try {
            // Load SQLite driver
            Class.forName("org.sqlite.JDBC");

            // Connect directly to the persistent DB
            String jdbcUrl = "jdbc:sqlite:" + DB_PATH;
            return DriverManager.getConnection(jdbcUrl);

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error connecting to DB: " + e.getMessage());
            return null;
        }
    }






    public static void main(String[] args) {

            DbInitializer.initializeDatabase();

          Connection conn = getDatabaseConnection();
        if (conn != null) {
            System.out.println("Connected to SQLite successfully: " + conn);
        }
System.out.println("Initializing services...");
connectionService = ConnectionService.getConnectionService();
clientService = ClientService.getClientService();
clientRepository = ClientRepository.getClientRepository();
tripService = TripService.getTripService();
System.out.println("Services initialized.");
 // ======== TEST LAYOUTS FOR TIMEUTILS & LAYOVER POLICY ========
    System.out.println("\n=== TEST: Layover & Duration ===");
    List<Route> testConnection = new ArrayList<>();
    testConnection.add(new Route("R00007", "A Coruña", "Santiago de Compostela", "16:50", "20:08", null, "", 0, 0));
    testConnection.add(new Route("R00008", "Santiago de Compostela", "Vigo", "21:00", "23:00", null, "", 0, 0));

    TimeUtils.printConnection(testConnection);

    int totalDuration = TimeUtils.getTripDuration(testConnection);
    System.out.println("Total duration in minutes: " + totalDuration);

    int layover = TimeUtils.getLayoverMinutes(
            testConnection.get(0).getArrivalTime(),
            testConnection.get(1).getDepartureTime()
    );
    System.out.println("Layover in minutes: " + layover);


        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("\n=== Welcome to the European Train Connection System ===");
System.out.flush();

        while (running) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Register Clients");
            System.out.println("2. Search for Connections");
            System.out.println("3. Check My Trips");
            System.out.println("4. Exit System");
            System.out.println("5. View All Clients");  // ← add this line
            System.out.print("Select an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    registerClients(scanner, clientService);
                    break;

                case "2":
                    searchConnections(scanner, connectionService, tripService, clientRepository);
                    break;

                case "3":
                    displayCurrentAndPastTrips(scanner, tripService);
                    break;

                case "4":
                    System.out.println("\nThank you for using the European Train Connection System!");
                    running = false;
                    break;
                case "5":
                    clientService.listAllClients();  // ← add this line
                    break;
                default:
                    System.out.println("Invalid option. Please select 1–5.");
            }
        }

        scanner.close();
    }
   /*  private static void registerClients(Scanner scanner, ClientService clientService) {
        boolean addingClients = true;

        while (addingClients) {
            System.out.println("\n=== Register a New Client ===");

            System.out.print("First Name: ");
            String firstName = scanner.nextLine().trim();

            System.out.print("Last Name: ");
            String lastName = scanner.nextLine().trim();

            System.out.print("Age: ");
            int age = Integer.parseInt(scanner.nextLine().trim());

            var client = clientService.registerClient(firstName, lastName, age);
            System.out.println("\nClient registered successfully!");
            System.out.println(client);
            System.out.println("Please remember your ID (" + client.getId() + ") for booking and viewing trips.");
            System.out.print("\nWould you like to add another client? (y/n): ");
            String again = scanner.nextLine().trim().toLowerCase();
            if (!again.equals("y")) {
                addingClients = false;
                System.out.println("Returning to main menu...");
            }
        }
    }
*/ 
private static void registerClients(Scanner scanner, ClientService clientService) {
    boolean addingClients = true;

    while (addingClients) {
        System.out.println("\n=== Register a New Client ===");

        System.out.print("Enter your unique ID (e.g., passport or license number): ");
        String id = scanner.nextLine().trim();

        System.out.print("First Name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Last Name: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Age: ");
        int age = Integer.parseInt(scanner.nextLine().trim());

        // Attempt to register the client
        var client = clientService.registerClient(id, firstName, lastName, age);

        if (client != null) {
            System.out.println("\nClient registered successfully!");
            System.out.println(client);
            System.out.println("Your ID (" + client.getId() + ") will be used for booking and viewing trips.");
        } else {
            System.out.println("\n⚠️ Registration failed: A client with this ID already exists. Please try again with a different ID.");
        }

        System.out.print("\nWould you like to add another client? (y/n): ");
        String again = scanner.nextLine().trim().toLowerCase();
        if (!again.equals("y")) {
            addingClients = false;
            System.out.println("Returning to main menu...");
        }
    }
}
    private static void displayCurrentAndPastTrips(Scanner scanner, TripService tripService) {
        System.out.println("\n=== View My Trips ===");

        System.out.print("Enter your Client ID: ");
        String clientId = scanner.nextLine().trim();

        System.out.print("Enter your Last Name: ");
        String lastName = scanner.nextLine().trim();

        // Call the service method to show trips
        tripService.viewTrips(clientId, lastName);
}

    private static void searchConnections(Scanner scanner, ConnectionService connectionService, TripService tripService, ClientRepository clientRepository) {
        System.out.println("\n=== Search for Train Connections ===");
        System.out.println("(Press Enter to skip any criteria)");
        System.out.println("\n⚠️ Note: Layovers follow our policy:");
        System.out.println("- Daytime (08:00–17:00): max 2 hours");
        System.out.println("- After-hours (<08:00 or >17:00): max 30 minutes");


        SearchCriteria criteria = new SearchCriteria();

        System.out.print("1. Departure City: ");
        criteria.setDepartureCity(scanner.nextLine().trim());
        System.out.print("2. Arrival City: ");
        criteria.setArrivalCity(scanner.nextLine().trim());
        System.out.print("3. Departure Time: ");
        criteria.setDepartureTime(scanner.nextLine().trim());
        System.out.print("4. Arrival Time: ");
        criteria.setArrivalTime(scanner.nextLine().trim());
        System.out.print("5. Train Type: ");
        criteria.setTrainType(scanner.nextLine().trim());
        System.out.print("6. Days of Operation: ");
        criteria.setDaysOfOperation(scanner.nextLine().trim());

        System.out.print("7. Max First Class Price (€): ");
        String first = scanner.nextLine().trim();
        criteria.setFirstClassPrice(first.isEmpty() ? 0 : Double.parseDouble(first));

        System.out.print("8. Max Second Class Price (€): ");
        String second = scanner.nextLine().trim();
        criteria.setSecondClassPrice(second.isEmpty() ? 0 : Double.parseDouble(second));

        System.out.println("\nSort Options:");
        System.out.println("A. None");
        System.out.println("B. Duration (shortest first)");
        System.out.println("C. First class (low → high)");
        System.out.println("D. First class (high → low)");
        System.out.println("E. Second class (low → high)");
        System.out.println("F. Second class (high → low)");
        System.out.print("Choice: ");
        criteria.setSortOption(scanner.nextLine().trim());

        System.out.println("\nSearching for connections...");
        SearchResultDTO result = connectionService.searchConnections(criteria);
        List<RouteConnection> connections = result.getResults();

        System.out.println("\n=== Search ID: " + result.getSearchId() + " ===");
        if (connections.isEmpty()) {
            System.out.println("No connections found.");
        } else {
            System.out.println("Available connections:");
            for (int i = 0; i < connections.size(); i++) {
                System.out.println((i + 1) + ". " + connections.get(i));
            }
   /**      
     // --- DEBUG: Print layovers for this connection ---
RouteConnection chosenConn = connections.get(0); // first connection
List<Route> legs = chosenConn.getRoutes();
System.out.println("Connection 1:");
for (int j = 0; j < legs.size(); j++) {
    Route leg = legs.get(j);
    System.out.println("   Leg " + (j + 1) + ": " + leg.getDepartureCity() + " -> " + leg.getArrivalCity() +
            ", Dep=" + leg.getDepartureTime() + ", Arr=" + leg.getArrivalTime());

    if (j < legs.size() - 1) {
        int layover = TimeUtils.getLayoverMinutes(leg.getArrivalTime(), legs.get(j + 1).getDepartureTime());
        System.out.println("      Layover before next leg: " + layover + " minutes");
    }
}
*/
        
            System.out.print("\nSelect a connection to book (number) or press Enter to return: ");
            String selection = scanner.nextLine().trim();
            if (!selection.isEmpty()) {
                try {
                    int index = Integer.parseInt(selection) - 1;
                    if (index >= 0 && index < connections.size()) {
                        RouteConnection chosen = connections.get(index);
                        System.out.println("\nYou selected:");
                        System.out.println(chosen);

                        List<Client> bookingClients = new ArrayList<>();
                        boolean addingClients = true;

                        while (addingClients) {
                            System.out.print("\nEnter client last name: ");
                            String lastName = scanner.nextLine().trim();
                            System.out.print("Enter client ID: ");
                            String id = scanner.nextLine().trim();

                            Client client = clientRepository.findByIdAndLastName(id, lastName);

                            if (client != null) {
                                bookingClients.add(client);
                                System.out.println("Added " + client.getFirstName() + " " + client.getLastName() + " to booking.");
                            } else {
                                System.out.println("No client found with that ID and last name.");
                            }

                            System.out.print("Add another client? (y/n): ");
                            String again = scanner.nextLine().trim().toLowerCase();
                            if (!again.equals("y")) {
                                addingClients = false;
                            }
                        }

                        if (tripService.validateBooking(chosen, bookingClients)) {
                            List<Ticket> tickets = tripService.generateTrip(chosen, bookingClients);
                            System.out.println("\nBooking confirmed!");
                            System.out.println("Tickets generated:");
                            for (Ticket t : tickets) {
                                System.out.println("\n" + t.toString());
                            }
                        } else {
                            System.out.println("\nBooking could not be validated.");
                        }

                    } else {
                        System.out.println("Invalid selection.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input — please enter a number next time.");
                }
            }
        }
    }
}
