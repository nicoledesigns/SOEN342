package com.trainsystem.app;

import com.trainsystem.model.Connection;
import com.trainsystem.repository.RouteRepository;
import com.trainsystem.service.ConnectionService;
import com.trainsystem.model.SearchCriteria;
import com.trainsystem.dto.SearchResultDTO;
import java.util.List;
import java.util.Scanner;

public class TrainApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        RouteRepository repo = new RouteRepository();
        ConnectionService connectionService = new ConnectionService(repo);

        System.out.println("\n=== Welcome to the European Train Connection System ===");
        System.out.println("Please enter your search criteria (press Enter to skip any).");

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
        System.out.print("7. First class max price (€): ");
        String first = scanner.nextLine().trim();
        criteria.setFirstClassPrice(first.isEmpty() ? 0 : Double.parseDouble(first));
        System.out.print("8. Second class max price (€): ");
        String second = scanner.nextLine().trim();
        criteria.setSecondClassPrice(second.isEmpty() ? 0 : Double.parseDouble(second));
        System.out.println("\nSort options:");
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
        List<Connection> connections = result.getResults();

        System.out.println("\n=== Search ID: " + result.getSearchId() + " ===");
        if (connections.isEmpty()) {
            System.out.println("No connections found.");
        } else {
            System.out.println("Available connections:");
            for (int i = 0; i < connections.size(); i++) {
                System.out.println((i + 1) + ". " + connections.get(i));
            }

            System.out.print("\nSelect a connection to book (number) or press Enter to exit: ");
            String selection = scanner.nextLine().trim();
            if (!selection.isEmpty()) {
                try {
                    int index = Integer.parseInt(selection) - 1;
                    if (index >= 0 && index < connections.size()) {
                        Connection chosen = connections.get(index);
                        System.out.println("\nYou selected:");
                        System.out.println(chosen);
                        System.out.println("\n(Booking logic will be handled by TripService later.)");
                    } else {
                        System.out.println("Invalid selection.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input — please enter a number next time.");
                }
            }
        }

        System.out.println("\nThank you for using the European Train Connection System!");
        scanner.close();
    }
}
