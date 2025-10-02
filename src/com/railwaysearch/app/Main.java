package com.railwaysearch.app;

import com.railwaysearch.repository.RouteRepository;
import com.railwaysearch.util.CsvLoader;
import com.railwaysearch.model.Route;
import com.railwaysearch.model.TrainType;
import java.util.List;
import java.util.Scanner;
import com.railwaysearch.util.TimeUtils;





public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // 1️- Create the in-memory repository
        RouteRepository repo = new RouteRepository();

        // 2️- Load data from the CSV file into the repository
        // Adjust the path if your routes.csv is in a different folder.
        String csvPath = "routes.csv";
        CsvLoader.load("routes.csv", repo);

        System.out.println("\n\nWelcome to the European Train Connection Finder");
     /*   
        System.out.println(
                "Please enter your search criteria to search for a connection. You can skip any criteria by pressing Enter");

        System.out.print("1. Departure City: ");
        String departure_city = scanner.nextLine();

        System.out.print("2. Arrival City: ");
        String arrival_city = scanner.nextLine();

        System.out.print("3. Departure Time: ");
        String departure_time = scanner.nextLine();

        System.out.print("4. Arrival Time: ");
        String arrival_time = scanner.nextLine();

        System.out.print("5. Train Type: ");
        String train_type = scanner.nextLine();

        System.out.print("6. Days of Operation: ");
        String days_of_operation = scanner.nextLine().trim();

        System.out.print("7. First class ticket rate (in euro): €");
        String first_class = scanner.nextLine();

        double first_price = 0;
        if(!first_class.equals("") )
        {
            try{
            first_price = Double.parseDouble(first_class);
            }
            catch(NumberFormatException e){
                System.out.println("Not a number");
            }
        }

        System.out.print("8. Second class ticket rate (in euro): €");
        String second_class = scanner.nextLine();

        double second_price = 0;

        if(!second_class.equals("") )
        {
            try{
                second_price = Double.parseDouble(second_class);
            }
            catch(NumberFormatException e){
                System.out.println("Not a number");

            }
        }
*/
        System.out.println("\n=== Printing one stop");
for (List<Route> connection : repo.find1StopConnections("Drammen", "Helsinki")) {
    TimeUtils.printConnection(connection);  // <- prints legs + layovers + total duration
}

System.out.println("\n=== Printing two stop");
for (List<Route> connection : repo.find2StopConnections("Drammen", "Turku")) {
    TimeUtils.printConnection(connection);  // <- prints legs + layovers + total duration
}

    }
}
