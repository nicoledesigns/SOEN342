package com.trainsystem.repository;

import com.trainsystem.model.RouteConnection;
import com.trainsystem.model.Route;
import com.trainsystem.model.TrainType;
import com.trainsystem.util.DayUtils;
import com.trainsystem.util.TimeUtils;

import java.time.DayOfWeek;
import java.util.*;
import java.time.LocalDate; 
import java.sql.*; //added sql database

public class RouteRepository {

    private static RouteRepository instance;
    private final List<Route> routes;
    private static final String DB_URL = "jdbc:sqlite:railway.db"; //for database


    private RouteRepository() {
        this.routes= new ArrayList<>();
        loadAllRoutesFromDb(); // load from DB on startup

    }

    public static RouteRepository getRouteRepository() {
        if (instance == null) {
            instance = new RouteRepository();
        }
        return instance;
    }

    public void addRoute(Route route) {
        routes.add(route);
        saveRoute(route);   // Add route in-memory and persist to DB

    }

    public List<Route> getAllRoutes() {
        return Collections.unmodifiableList(routes);
    }


    // ---------- Database Methods ----------
    private void saveRoute(Route route) {
        String sql = "INSERT OR IGNORE INTO Route(route_id, departure_city, arrival_city, departure_time, arrival_time, train_type, days_of_operation, first_class_price, second_class_price) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, route.getRouteId());
            pstmt.setString(2, route.getDepartureCity());
            pstmt.setString(3, route.getArrivalCity());
            pstmt.setString(4, route.getDepartureTime());
            pstmt.setString(5, route.getArrivalTime());
            pstmt.setString(6, route.getTrainType().name());
            pstmt.setString(7, route.getDaysOfOperation());
            pstmt.setDouble(8, route.getFirstClassPrice());
            pstmt.setDouble(9, route.getSecondClassPrice());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving route: " + e.getMessage());
        }
    }

    public void loadAllRoutesFromDb() {
        String sql = "SELECT * FROM Route";

        try (java.sql.Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                TrainType type = TrainType.valueOf(rs.getString("train_type"));
                Route route = new Route(
                        rs.getString("route_id"),
                        rs.getString("departure_city"),
                        rs.getString("arrival_city"),
                        rs.getString("departure_time"),
                        rs.getString("arrival_time"),
                        type,
                        rs.getString("days_of_operation"),
                        rs.getDouble("first_class_price"),
                        rs.getDouble("second_class_price")
                );
                routes.add(route);
            }

        } catch (SQLException e) {
            System.err.println("Error loading routes: " + e.getMessage());
        }
    }

    // ---------- Search Methods ----------
    public List<RouteConnection> findDirectConnections(String departureCity, String arrivalCity) {
        List<RouteConnection> results = new ArrayList<>();
        for (Route route : routes) {
            if (route.getDepartureCity().equalsIgnoreCase(departureCity)
                    && route.getArrivalCity().equalsIgnoreCase(arrivalCity)) {
                results.add(new RouteConnection(List.of(route), LocalDate.now()));
            }
        }
        return results;
       }
/**  
    public List<RouteConnection> find1StopConnections(String departureCity, String arrivalCity) {
        List<RouteConnection> results = new ArrayList<>();
        for (Route firstLeg : routes) {
            if (firstLeg.getDepartureCity().equalsIgnoreCase(departureCity)) {
                for (Route secondLeg : routes) {
                    if (secondLeg.getDepartureCity().equalsIgnoreCase(firstLeg.getArrivalCity())
                            && secondLeg.getArrivalCity().equalsIgnoreCase(arrivalCity)) {

                        long wait = TimeUtils.getDurationMinutes(firstLeg.getArrivalTime(), secondLeg.getDepartureTime());
                        if (wait >= 0) results.add(new RouteConnection(List.of(firstLeg, secondLeg), LocalDate.now()));
                    }
                }
            }
        }
        return results;
    }


    public List<RouteConnection> find2StopConnections(String departureCity, String arrivalCity) {
        List<RouteConnection> results = new ArrayList<>();
        for (Route firstLeg : routes) {
            if (firstLeg.getDepartureCity().equalsIgnoreCase(departureCity)) {
                for (Route secondLeg : routes) {
                    if (secondLeg.getDepartureCity().equalsIgnoreCase(firstLeg.getArrivalCity())) {
                        for (Route thirdLeg : routes) {
                            if (thirdLeg.getDepartureCity().equalsIgnoreCase(secondLeg.getArrivalCity())
                                    && thirdLeg.getArrivalCity().equalsIgnoreCase(arrivalCity)) {

                                long wait1 = TimeUtils.getDurationMinutes(firstLeg.getArrivalTime(), secondLeg.getDepartureTime());
                                long wait2 = TimeUtils.getDurationMinutes(secondLeg.getArrivalTime(), thirdLeg.getDepartureTime());

                                if (wait1 >= 0 && wait2 >= 0) results.add(new RouteConnection(List.of(firstLeg, secondLeg, thirdLeg), LocalDate.now()));
                            }
                        }
                    }
                }
            }
        }
        return results;
    }
*/
public List<RouteConnection> find1StopConnections(String departureCity, String arrivalCity) {
    List<RouteConnection> results = new ArrayList<>();
    for (Route firstLeg : routes) {
        if (firstLeg.getDepartureCity().equalsIgnoreCase(departureCity)) {
            for (Route secondLeg : routes) {
                if (secondLeg.getDepartureCity().equalsIgnoreCase(firstLeg.getArrivalCity())
                        && secondLeg.getArrivalCity().equalsIgnoreCase(arrivalCity)) {

                    long wait = TimeUtils.getDurationMinutes(firstLeg.getArrivalTime(), secondLeg.getDepartureTime());
                    System.out.println("DEBUG 1-STOP: " + firstLeg.getRouteId() + " -> " + secondLeg.getRouteId() +
                            ", Arrival=" + firstLeg.getArrivalTime() +
                            ", Departure=" + secondLeg.getDepartureTime() +
                            ", Wait=" + wait + " mins");

                    if (wait >= 0) {
                        // --- LAYOVER POLICY ---
                        java.time.LocalTime arrivalTime = java.time.LocalTime.parse(firstLeg.getArrivalTime());
                        boolean afterHours = arrivalTime.isBefore(java.time.LocalTime.of(8, 0))
                                || arrivalTime.isAfter(java.time.LocalTime.of(17, 0));
                        boolean layoverAllowed = afterHours ? (wait <= 30) : (wait <= 120);

                        System.out.println("DEBUG 1-STOP: afterHours=" + afterHours + ", layoverAllowed=" + layoverAllowed);

                        if (layoverAllowed) {
                            results.add(new RouteConnection(List.of(firstLeg, secondLeg), java.time.LocalDate.now()));
                            System.out.println("DEBUG 1-STOP: Added connection " + firstLeg.getRouteId() + " -> " + secondLeg.getRouteId());
                        }
                    }
                }
            }
        }
    }
    return results;
}

public List<RouteConnection> find2StopConnections(String departureCity, String arrivalCity) {
    List<RouteConnection> results = new ArrayList<>();
    for (Route firstLeg : routes) {
        if (firstLeg.getDepartureCity().equalsIgnoreCase(departureCity)) {
            for (Route secondLeg : routes) {
                if (secondLeg.getDepartureCity().equalsIgnoreCase(firstLeg.getArrivalCity())) {
                    for (Route thirdLeg : routes) {
                        if (thirdLeg.getDepartureCity().equalsIgnoreCase(secondLeg.getArrivalCity())
                                && thirdLeg.getArrivalCity().equalsIgnoreCase(arrivalCity)) {

                            long wait1 = TimeUtils.getDurationMinutes(firstLeg.getArrivalTime(), secondLeg.getDepartureTime());
                            long wait2 = TimeUtils.getDurationMinutes(secondLeg.getArrivalTime(), thirdLeg.getDepartureTime());

                            System.out.println("DEBUG 2-STOP: " + firstLeg.getRouteId() + " -> " + secondLeg.getRouteId() +
                                    " -> " + thirdLeg.getRouteId() +
                                    ", Wait1=" + wait1 + " mins, Wait2=" + wait2 + " mins");

                            if (wait1 >= 0 && wait2 >= 0) {
                                // --- LAYOVER POLICY #1 ---
                                java.time.LocalTime arr1 = java.time.LocalTime.parse(firstLeg.getArrivalTime());
                                boolean afterHours1 = arr1.isBefore(java.time.LocalTime.of(8, 0))
                                        || arr1.isAfter(java.time.LocalTime.of(17, 0));
                                boolean layover1 = afterHours1 ? (wait1 <= 30) : (wait1 <= 120);

                                // --- LAYOVER POLICY #2 ---
                                java.time.LocalTime arr2 = java.time.LocalTime.parse(secondLeg.getArrivalTime());
                                boolean afterHours2 = arr2.isBefore(java.time.LocalTime.of(8, 0))
                                        || arr2.isAfter(java.time.LocalTime.of(17, 0));
                                boolean layover2 = afterHours2 ? (wait2 <= 30) : (wait2 <= 120);

                                System.out.println("DEBUG 2-STOP: Layover1 allowed=" + layover1 + ", afterHours1=" + afterHours1 +
                                        "; Layover2 allowed=" + layover2 + ", afterHours2=" + afterHours2);

                                if (layover1 && layover2) {
                                    results.add(new RouteConnection(
                                            List.of(firstLeg, secondLeg, thirdLeg),
                                            java.time.LocalDate.now()));
                                    System.out.println("DEBUG 2-STOP: Added connection " +
                                            firstLeg.getRouteId() + " -> " + secondLeg.getRouteId() + " -> " + thirdLeg.getRouteId());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    return results;
}


    public List<RouteConnection> findAllConnections(String departureCity, String arrivalCity) {
        List<RouteConnection> all = new ArrayList<>();
        all.addAll(findDirectConnections(departureCity, arrivalCity));
        all.addAll(find1StopConnections(departureCity, arrivalCity));
        all.addAll(find2StopConnections(departureCity, arrivalCity));
        return all;
    }

    public int size() {
        return routes.size();
    }
}




/**  
    public List<Connection> findDirectConnections(String departureCity, String arrivalCity) {
        List<Connection> results = new ArrayList<>();

        for (Route route : routes) {
            if (route.getDepartureCity().equalsIgnoreCase(departureCity)
                    && route.getArrivalCity().equalsIgnoreCase(arrivalCity)) {
        results.add(new Connection(List.of(route), LocalDate.now())); // added travel date
            }
        }

        return results;
    }

    public List<Connection> find1StopConnections(String departureCity, String arrivalCity) {
        List<Connection> results = new ArrayList<>();

        for (Route firstLeg : routes) {
            if (firstLeg.getDepartureCity().equalsIgnoreCase(departureCity)) {
                for (Route secondLeg : routes) {
                    if (secondLeg.getDepartureCity().equalsIgnoreCase(firstLeg.getArrivalCity())
                            && secondLeg.getArrivalCity().equalsIgnoreCase(arrivalCity)) {

                        long wait = TimeUtils.getDurationMinutes(firstLeg.getArrivalTime(), secondLeg.getDepartureTime());
                        if (wait >= 0) {
                        results.add(new Connection(List.of(firstLeg, secondLeg), LocalDate.now()));//added local date
                        }
                    }
                }
            }
        }

        return results;
    }

    public List<Connection> find2StopConnections(String departureCity, String arrivalCity) {
        List<Connection> results = new ArrayList<>();

        for (Route firstLeg : routes) {
            if (firstLeg.getDepartureCity().equalsIgnoreCase(departureCity)) {

                for (Route secondLeg : routes) {
                    if (secondLeg.getDepartureCity().equalsIgnoreCase(firstLeg.getArrivalCity())) {

                        for (Route thirdLeg : routes) {
                            if (thirdLeg.getDepartureCity().equalsIgnoreCase(secondLeg.getArrivalCity())
                                    && thirdLeg.getArrivalCity().equalsIgnoreCase(arrivalCity)) {

                                long wait1 = TimeUtils.getDurationMinutes(firstLeg.getArrivalTime(), secondLeg.getDepartureTime());
                                long wait2 = TimeUtils.getDurationMinutes(secondLeg.getArrivalTime(), thirdLeg.getDepartureTime());

                                if (wait1 >= 0 && wait2 >= 0) {
                                results.add(new Connection(List.of(firstLeg, secondLeg, thirdLeg), LocalDate.now()));//added local date
                                }
                            }
                        }
                    }
                }
            }
        }

        return results;
    }

    public List<Connection> findAllConnections(String departureCity, String arrivalCity) {
        List<Connection> all = new ArrayList<>();
        all.addAll(findDirectConnections(departureCity, arrivalCity));
        all.addAll(find1StopConnections(departureCity, arrivalCity));
        all.addAll(find2StopConnections(departureCity, arrivalCity));
        return all;
    }

    public int size() {
        return routes.size();
    }
}
*/