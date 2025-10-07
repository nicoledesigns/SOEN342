package com.railwaysearch.app;

import com.railwaysearch.repository.RouteRepository;
import com.railwaysearch.util.CsvLoader;
import com.railwaysearch.model.Route;
import com.railwaysearch.model.TrainType;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import com.railwaysearch.util.TimeUtils;
public class Main {

    public static void main(String[] args) {

        int count =0;

        Scanner scanner = new Scanner(System.in);

        // 1️- Create the in-memory repository
        RouteRepository repo = new RouteRepository();

        // 2️- Load data from the CSV file into the repository
        // Adjust the path if your routes.csv is in a different folder.
        String csvPath = "routes.csv";
        CsvLoader.load("routes.csv", repo);

        System.out.println("\n\nWelcome to the European Train Connection System");
     
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

        System.out.println("8. Second class ticket rate (in euro): €");
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


        System.out.println("How would you like to sort the connections? Please enter the corresponding letter.");
        System.out.println("A. No selection");
        System.out.println("B. Trip duration (low to high)");
        System.out.println("C. First class price (low to high)");
        System.out.println("D. First class price (high to low)");
        System.out.println("E. Second class price (low to high)");
        System.out.println("F. Second class price (high to low)");

        String sorting = scanner.next(); 


        List<Route> routes = repo.findRoutes(departure_city, arrival_city, departure_time, arrival_time,train_type, days_of_operation, first_price, second_price    );
          
          if(sorting.charAt(0) == 'B' || sorting.charAt(0) == 'b')
          {
            routes.sort(Comparator.comparing(Route::getTripDurationMinutes));

            for(Route r : routes  )
            {
                System.out.println(r);
                count++;
            }
          }

          else if(sorting.charAt(0) == 'C' ||sorting.charAt(0) == 'c')
          {
            routes.sort(Comparator.comparing(Route::getFirstClassPrice));

            for(Route r : routes  )
            {
                System.out.println(r);
                count++;
            }
          }

         else if(sorting.charAt(0) == 'D' || sorting.charAt(0) == 'd')
          {
            routes.sort(Comparator.comparing(Route::getFirstClassPrice).reversed());

            for(Route r : routes  )
            {
                System.out.println(r);
                count++;
            }
          }
         else if(sorting.charAt(0) == 'E' ||sorting.charAt(0) == 'e')
          {
            routes.sort(Comparator.comparing(Route::getSecondClassPrice));

            for(Route r : routes  )
            {
                System.out.println(r);
                count++;
            }
          }

         else if(sorting.charAt(0) == 'F' || sorting.charAt(0) == 'f')
          {
            routes.sort(Comparator.comparing(Route::getSecondClassPrice).reversed());

            for(Route r : routes  )
            {
                System.out.println(r);
                count++;
            }
          }

          else
          {
            for(Route r : routes  )
            {
                System.out.println(r);
                count++;
            }
          }

        //    for(Route routes: repo.findRoutes(departure_city, arrival_city, departure_time, arrival_time,train_type, days_of_operation, first_price, second_price    ))
        //    {
        //     System.out.println(routes);

        //    }
           

        // for(Route r : routes  )
        //     {
        //         System.out.println(r);
        //     }
    
        if(count==0){

        System.out.println("\n=== Printing one stop");
for (List<Route> connection : repo.find1StopConnections(departure_city, arrival_city)) {
    
    TimeUtils.printConnection(connection);  // <- prints legs + layovers + total duration
}



System.out.println("\n=== Printing two stop");
for (List<Route> connection : repo.find2StopConnections(departure_city, arrival_city)) {
    TimeUtils.printConnection(connection);  // <- prints legs + layovers + total duration
}
        }

    }
}
