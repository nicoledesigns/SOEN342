package com.trainsystem.app;

import com.trainsystem.model.Route;
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
        criteria.setDepartureCity(scanner.nextLine());

        System.out.print("2. Arrival City: ");
        criteria.setArrivalCity(scanner.nextLine());

        System.out.print("3. Departure Time: ");
        criteria.setDepartureTime(scanner.nextLine());

        System.out.print("4. Arrival Time: ");
        criteria.setArrivalTime(scanner.nextLine());

        System.out.print("5. Train Type: ");
        criteria.setTrainType(scanner.nextLine());

        System.out.print("6. Days of Operation: ");
        criteria.setDaysOfOperation(scanner.nextLine());

        System.out.print("7. First class max price (€): ");
        String first = scanner.nextLine();
        criteria.setFirstClassPrice(first.isEmpty() ? 0 : Double.parseDouble(first));

        System.out.print("8. Second class max price (€): ");
        String second = scanner.nextLine();
        criteria.setSecondClassPrice(second.isEmpty() ? 0 : Double.parseDouble(second));

        System.out.println("\nHow would you like to sort the connections?");
        System.out.println("A. No selection");
        System.out.println("B. Trip duration (low → high)");
        System.out.println("C. First class price (low → high)");
        System.out.println("D. First class price (high → low)");
        System.out.println("E. Second class price (low → high)");
        System.out.println("F. Second class price (high → low)");
        System.out.print("Choice: ");
        criteria.setSortOption(scanner.nextLine());

        SearchResultDTO result = connectionService.searchConnections(criteria);

        List<Route> routes = result.getResults();

        System.out.println("\n=== Search ID: " + result.getSearchId() + " ===");
        if (routes.isEmpty()) {
            System.out.println("No routes found.");
        } else {
            System.out.println("Available connections:");
            for (int i = 0; i < routes.size(); i++) {
                System.out.println((i + 1) + ". " + routes.get(i));
            }

            System.out.print("\nSelect a route to book (number) or press Enter to exit: ");
            String selection = scanner.nextLine();
            if (!selection.isEmpty()) {
                int index = Integer.parseInt(selection) - 1;
                if (index >= 0 && index < routes.size()) {
                    Route chosen = routes.get(index);
                    System.out.println("\nYou selected:");
                    System.out.println(chosen);
                    System.out.println("\n(Booking logic will be handled by TripService later.)");
                } else {
                    System.out.println("Invalid selection.");
                }
            }
        }

        scanner.close();
    }
}
