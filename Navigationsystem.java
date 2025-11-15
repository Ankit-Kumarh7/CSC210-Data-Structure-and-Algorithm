import java.util.*;

class Building {
    String this_buildings_name;
    java.util.Map<String, Double> map_of_connected_buildings_and_their_weights;

    Building(String this_buildings_name) {
        this.this_buildings_name = this_buildings_name;
        this.map_of_connected_buildings_and_their_weights = new java.util.HashMap<>();
    }

    void addConnection(String destination_building_name, double weight_of_connection_to_destination) {
        map_of_connected_buildings_and_their_weights.put(destination_building_name, weight_of_connection_to_destination);
    }

    void showConnections() {
        System.out.print(this_buildings_name + " -> ");
        boolean has_printed_any_connection = false;

        if (!map_of_connected_buildings_and_their_weights.isEmpty()) {
            for (Map.Entry<String, Double> single_connection_entry_from_map : map_of_connected_buildings_and_their_weights.entrySet()) {
                if (single_connection_entry_from_map.getValue() > 0) {
                    System.out.print(single_connection_entry_from_map.getKey() + " (" + single_connection_entry_from_map.getValue() + "), ");
                    has_printed_any_connection = true;
                }
            }
        }

        if (!has_printed_any_connection) {
            System.out.print("None");
        }

        System.out.println();
    }
}

class Campus {

    java.util.Map<String, Building> map_of_all_buildings_on_campus_keyed_by_name = new java.util.HashMap<>();

    class BuildingDistancePair implements Comparable<BuildingDistancePair> {
        String name_of_building_in_pair;
        double distance_from_start_to_this_building;

        BuildingDistancePair(String name_of_building_in_pair, double distance_from_start_to_this_building) {
            this.name_of_building_in_pair = name_of_building_in_pair;
            this.distance_from_start_to_this_building = distance_from_start_to_this_building;
        }

        public int compareTo(BuildingDistancePair other_building_distance_pair_to_compare) {
            return Double.compare(this.distance_from_start_to_this_building, other_building_distance_pair_to_compare.distance_from_start_to_this_building);
        }
    }

    void addBuilding(String name_of_building_to_add) {
        String uppercase_building_name_key = name_of_building_to_add.toUpperCase();
        map_of_all_buildings_on_campus_keyed_by_name.putIfAbsent(uppercase_building_name_key, new Building(uppercase_building_name_key));
    }

    boolean hasBuilding(String name_of_building_to_check_for_existence) {
        if (name_of_building_to_check_for_existence == null) return false;
        return map_of_all_buildings_on_campus_keyed_by_name.containsKey(name_of_building_to_check_for_existence.toUpperCase());
    }

    void connect(String source_building_name, String destination_building_name, double weight_of_connection_between_buildings) {
        if (source_building_name == null || destination_building_name == null || weight_of_connection_between_buildings < 0) return;

        String uppercase_source_building_name = source_building_name.toUpperCase();
        String uppercase_destination_building_name = destination_building_name.toUpperCase();

        if (!map_of_all_buildings_on_campus_keyed_by_name.containsKey(uppercase_source_building_name) || !map_of_all_buildings_on_campus_keyed_by_name.containsKey(uppercase_destination_building_name)) return;

        Building source_building_object = map_of_all_buildings_on_campus_keyed_by_name.get(uppercase_source_building_name);
        Building destination_building_object = map_of_all_buildings_on_campus_keyed_by_name.get(uppercase_destination_building_name);

        if (source_building_object != null) {
            source_building_object.addConnection(uppercase_destination_building_name, weight_of_connection_between_buildings);
        }
        if (destination_building_object != null) {
            destination_building_object.addConnection(uppercase_source_building_name, weight_of_connection_between_buildings);
        }
    }

    Map<String, Double> getWeightedNeighbors(String name_of_building_to_get_neighbors_for) {
        if (name_of_building_to_get_neighbors_for == null) return Collections.emptyMap();

        String uppercase_building_name_key = name_of_building_to_get_neighbors_for.toUpperCase();
        Building retrieved_building_object = map_of_all_buildings_on_campus_keyed_by_name.get(uppercase_building_name_key);

        if (retrieved_building_object == null) return Collections.emptyMap();

        return retrieved_building_object.map_of_connected_buildings_and_their_weights;
    }

    List<String> getNeighbors(String name_of_building_to_get_neighbors_for) {
        Map<String, Double> map_of_weighted_neighbors = getWeightedNeighbors(name_of_building_to_get_neighbors_for);
        if (map_of_weighted_neighbors.isEmpty()) return Collections.emptyList();
        return new ArrayList<>(map_of_weighted_neighbors.keySet());
    }

    List<String> findShortestPath(String start_building_name, String end_building_name) {
        if (start_building_name == null || end_building_name == null) return null;

        String uppercase_start_building_name = start_building_name.toUpperCase();
        String uppercase_end_building_name = end_building_name.toUpperCase();

        if (!map_of_all_buildings_on_campus_keyed_by_name.containsKey(uppercase_start_building_name) || !map_of_all_buildings_on_campus_keyed_by_name.containsKey(uppercase_end_building_name)) return null;

        PriorityQueue<BuildingDistancePair> min_heap_priority_queue_of_buildings_to_explore = new PriorityQueue<>();
        Map<String, Double> map_of_shortest_distances_from_start = new HashMap<>();
        Map<String, String> map_to_reconstruct_path_from_child_to_parent = new HashMap<>();

        for (String building_name_key_from_map : map_of_all_buildings_on_campus_keyed_by_name.keySet()) {
            map_of_shortest_distances_from_start.put(building_name_key_from_map, Double.POSITIVE_INFINITY);
        }

        map_of_shortest_distances_from_start.put(uppercase_start_building_name, 0.0);
        min_heap_priority_queue_of_buildings_to_explore.add(new BuildingDistancePair(uppercase_start_building_name, 0.0));
        map_to_reconstruct_path_from_child_to_parent.put(uppercase_start_building_name, null);

        while (!min_heap_priority_queue_of_buildings_to_explore.isEmpty()) {
            BuildingDistancePair current_building_distance_pair_from_queue = min_heap_priority_queue_of_buildings_to_explore.poll();
            String current_building_name_being_processed = current_building_distance_pair_from_queue.name_of_building_in_pair;
            double current_shortest_distance_to_this_building = current_building_distance_pair_from_queue.distance_from_start_to_this_building;

            if (current_building_name_being_processed.equals(uppercase_end_building_name)) {
                break;
            }

            if (current_shortest_distance_to_this_building > map_of_shortest_distances_from_start.get(current_building_name_being_processed)) {
                continue;
            }

            Map<String, Double> map_of_weighted_neighbors_of_current_building = getWeightedNeighbors(current_building_name_being_processed);

            for (Map.Entry<String, Double> single_neighbor_entry_from_map : map_of_weighted_neighbors_of_current_building.entrySet()) {
                String neighbor_building_name = single_neighbor_entry_from_map.getKey();
                double weight_of_edge_to_this_neighbor = single_neighbor_entry_from_map.getValue();

                double new_potential_shorter_distance_through_current_building = current_shortest_distance_to_this_building + weight_of_edge_to_this_neighbor;

                if (new_potential_shorter_distance_through_current_building < map_of_shortest_distances_from_start.get(neighbor_building_name)) {
                    map_of_shortest_distances_from_start.put(neighbor_building_name, new_potential_shorter_distance_through_current_building);
                    map_to_reconstruct_path_from_child_to_parent.put(neighbor_building_name, current_building_name_being_processed);
                    min_heap_priority_queue_of_buildings_to_explore.add(new BuildingDistancePair(neighbor_building_name, new_potential_shorter_distance_through_current_building));
                }
            }
        }

        if (!map_to_reconstruct_path_from_child_to_parent.containsKey(uppercase_end_building_name)) return null;

        LinkedList<String> reconstructed_shortest_path_list = new LinkedList<>();
        String current_building_name_for_path_reconstruction = uppercase_end_building_name;

        while (current_building_name_for_path_reconstruction != null) {
            reconstructed_shortest_path_list.addFirst(current_building_name_for_path_reconstruction);
            current_building_name_for_path_reconstruction = map_to_reconstruct_path_from_child_to_parent.get(current_building_name_for_path_reconstruction);
        }

        if (!reconstructed_shortest_path_list.getFirst().equals(uppercase_start_building_name)) {
            return null;
        }

        return reconstructed_shortest_path_list;
    }

    public List<List<String>> findAllPaths(String start_building_name, String end_building_name) {
        List<List<String>> all_paths_found = new ArrayList<>();
        if (start_building_name == null || end_building_name == null) return all_paths_found;

        String uppercase_start_building_name = start_building_name.toUpperCase();
        String uppercase_end_building_name = end_building_name.toUpperCase();

        if (!hasBuilding(uppercase_start_building_name) || !hasBuilding(uppercase_end_building_name)) {
            return all_paths_found;
        }

        Set<String> visited_buildings = new LinkedHashSet<>();
        List<String> current_path = new ArrayList<>();

        findAllPathsRecursive(uppercase_start_building_name, uppercase_end_building_name, visited_buildings, current_path, all_paths_found);

        return all_paths_found;
    }

    private void findAllPathsRecursive(
            String current_building_name,
            String end_building_name,
            Set<String> visited_buildings,
            List<String> current_path,
            List<List<String>> all_paths_found) {

        visited_buildings.add(current_building_name);
        current_path.add(current_building_name);

        if (current_building_name.equals(end_building_name)) {
            all_paths_found.add(new ArrayList<>(current_path));
        } else {
            Map<String, Double> map_of_weighted_neighbors = getWeightedNeighbors(current_building_name);

            for (Map.Entry<String, Double> single_neighbor_entry : map_of_weighted_neighbors.entrySet()) {
                String neighbor_name = single_neighbor_entry.getKey();
                double weight = single_neighbor_entry.getValue();

                if (!visited_buildings.contains(neighbor_name)) {
                    findAllPathsRecursive(neighbor_name, end_building_name, visited_buildings, current_path, all_paths_found);
                }
            }
        }

        current_path.remove(current_path.size() - 1);
        visited_buildings.remove(current_building_name);
    }

    void showAllLocationNames() {
        System.out.println("Available locations:");
        List<String> sorted_list_of_all_location_names = new ArrayList<>(map_of_all_buildings_on_campus_keyed_by_name.keySet());
        Collections.sort(sorted_list_of_all_location_names);
        System.out.println(String.join(", ", sorted_list_of_all_location_names));
    }

    void showAdjacencyList() {
        System.out.println("Campus connections:");

        List<String> sorted_list_of_all_building_names = new ArrayList<>(map_of_all_buildings_on_campus_keyed_by_name.keySet());
        Collections.sort(sorted_list_of_all_building_names);

        for (String building_name_from_sorted_list : sorted_list_of_all_building_names) {
            Building building_object_instance = map_of_all_buildings_on_campus_keyed_by_name.get(building_name_from_sorted_list);
            if (building_object_instance != null) {
                building_object_instance.showConnections();
            }
        }
    }
}

class NavigationUI {
    private Campus campus_graph_instance;
    private Scanner user_input_scanner_instance;

    public NavigationUI(Campus campus_graph_to_use, Scanner user_input_scanner_to_use) {
        this.campus_graph_instance = campus_graph_to_use;
        this.user_input_scanner_instance = user_input_scanner_to_use;
    }

    public void start() {
        System.out.println("Welcome to the Navigation System!");
        System.out.println("Available features:");
        System.out.println("1) Multiple route suggestions\n2) Shortest distance calculation\n3) Campus view");

        System.out.print("Would you like to share your location? (1 = Yes, 0 = No): ");
        int user_choice_for_sharing_location;

        while (true) {
            if (!user_input_scanner_instance.hasNextInt()) {
                user_input_scanner_instance.next();
                System.out.print("Please enter 1 (Yes) or 0 (No): ");
                continue;
            }
            user_choice_for_sharing_location = user_input_scanner_instance.nextInt();
            user_input_scanner_instance.nextLine();
            if (user_choice_for_sharing_location == 0 || user_choice_for_sharing_location == 1) break;
            System.out.print("Please enter 1 (Yes) or 0 (No): ");
        }

        if (user_choice_for_sharing_location == 1) {
            runMainNavigationLoop();
        } else {
            runNoLocationMenu();
        }
    }

    private void runMainNavigationLoop() {
        System.out.print("Enter your current location: ");
        String current_user_location_building_name = user_input_scanner_instance.nextLine().trim().toUpperCase();

        while (!campus_graph_instance.hasBuilding(current_user_location_building_name)) {
            System.out.print("Invalid location name. Enter again (available locations shown):\n");
            campus_graph_instance.showAllLocationNames();
            System.out.print("Your location: ");
            current_user_location_building_name = user_input_scanner_instance.nextLine().trim().toUpperCase();
        }

        Deque<String> stack_of_user_visited_location_history = new ArrayDeque<>();
        stack_of_user_visited_location_history.push(current_user_location_building_name);

        boolean flag_is_user_sharing_location_loop_active = true;

        while (flag_is_user_sharing_location_loop_active) {
            System.out.println("\nCurrent location: " + current_user_location_building_name);
            System.out.println("Choose an option:");
            System.out.println("1) Enter your destination");
            System.out.println("2) Backtrack to previous location");
            System.out.println("3) Show campus connections");
            System.out.println("4) Show visited path (stack)");
            System.out.println("5) Stop sharing location (Exit)");

            int user_menu_choice;
            if (!user_input_scanner_instance.hasNextInt()) {
                user_input_scanner_instance.next();
                System.out.println("Please enter a valid number.");
                continue;
            }
            user_menu_choice = user_input_scanner_instance.nextInt();
            user_input_scanner_instance.nextLine();

            switch (user_menu_choice) {
                case 1:
                    current_user_location_building_name = handleDestinationChoice(current_user_location_building_name, stack_of_user_visited_location_history);
                    break;
                case 2:
                    current_user_location_building_name = handleBacktrack(current_user_location_building_name, stack_of_user_visited_location_history);
                    break;
                case 3:
                    campus_graph_instance.showAdjacencyList();
                    break;
                case 4:
                    showVisitedPath(stack_of_user_visited_location_history);
                    break;
                case 5:
                    System.out.println("Stop sharing location. Goodbye!");
                    flag_is_user_sharing_location_loop_active = false;
                    break;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private String handleDestinationChoice(String current_user_location_building_name, Deque<String> stack_of_user_visited_location_history) {
        System.out.print("Enter your destination: ");
        String user_destination_building_name = user_input_scanner_instance.nextLine().trim().toUpperCase();

        if (!campus_graph_instance.hasBuilding(user_destination_building_name)) {
            System.out.println("Invalid destination. Use option 3 to view campus connections.");
            return current_user_location_building_name;
        }

        System.out.println("Which feature do you want to use?");
        System.out.println("1) Multiple routes (Show all paths)");
        System.out.println("2) Shortest distance route");

        int user_choice_for_route_feature;
        if (!user_input_scanner_instance.hasNextInt()) {
            user_input_scanner_instance.next();
            System.out.println("Invalid input. Returning to main menu.");
            return current_user_location_building_name;
        }
        user_choice_for_route_feature = user_input_scanner_instance.nextInt();
        user_input_scanner_instance.nextLine();

        if (user_choice_for_route_feature == 1) {
            return handleMultipleRoutesNavigation(current_user_location_building_name, user_destination_building_name, stack_of_user_visited_location_history);
        } else if (user_choice_for_route_feature == 2) {
            return handleShortestPathNavigation(current_user_location_building_name, user_destination_building_name, stack_of_user_visited_location_history);
        } else {
            System.out.println("Invalid route choice!");
            return current_user_location_building_name;
        }
    }

    private String handleMultipleRoutesNavigation(String current_user_location_building_name, String user_destination_building_name, Deque<String> stack_of_user_visited_location_history) {

        List<List<String>> all_paths_found = campus_graph_instance.findAllPaths(current_user_location_building_name, user_destination_building_name);

        if (all_paths_found.isEmpty()) {
            System.out.println("No routes (with >0 weight) found from " + current_user_location_building_name + " to " + user_destination_building_name);
            return current_user_location_building_name;
        }

        System.out.println("Found " + all_paths_found.size() + " routes:");
        for (int i = 0; i < all_paths_found.size(); i++) {
            System.out.println((i + 1) + ") " + String.join(" -> ", all_paths_found.get(i)));
        }
        System.out.println("0) Cancel");
        System.out.print("Choose a route to follow: ");

        if (!user_input_scanner_instance.hasNextInt()) {
            user_input_scanner_instance.next();
            System.out.println("Invalid input. Returning to main menu.");
            return current_user_location_building_name;
        }
        int user_chosen_route_index = user_input_scanner_instance.nextInt();
        user_input_scanner_instance.nextLine();

        if (user_chosen_route_index == 0) {
            System.out.println("Cancelled.");
            return current_user_location_building_name;
        }
        if (user_chosen_route_index < 1 || user_chosen_route_index > all_paths_found.size()) {
            System.out.println("Index out of range. Cancelled.");
            return current_user_location_building_name;
        }

        List<String> chosen_path_list = all_paths_found.get(user_chosen_route_index - 1);

        System.out.println("You chose: " + String.join(" -> ", chosen_path_list));
        System.out.println("Do you want to: 1) Move directly to destination  2) Move step-by-step  0) Cancel");

        if (!user_input_scanner_instance.hasNextInt()) {
            user_input_scanner_instance.next();
            System.out.println("Invalid input. Returning to main menu.");
            return current_user_location_building_name;
        }
        int user_choice_for_how_to_move = user_input_scanner_instance.nextInt();
        user_input_scanner_instance.nextLine();

        String new_location_after_move = current_user_location_building_name;

        if (user_choice_for_how_to_move == 0) {
            System.out.println("Cancelled.");
        } else if (user_choice_for_how_to_move == 1) {
            for (int i = 1; i < chosen_path_list.size(); i++) {
                stack_of_user_visited_location_history.push(chosen_path_list.get(i));
            }
            new_location_after_move = user_destination_building_name;
            System.out.println("Moved directly to " + user_destination_building_name);
        } else if (user_choice_for_how_to_move == 2) {
            for (int i = 1; i < chosen_path_list.size(); i++) {
                String building_name_step_in_route = chosen_path_list.get(i);
                stack_of_user_visited_location_history.push(building_name_step_in_route);
                new_location_after_move = building_name_step_in_route;
                System.out.println("Moved to " + building_name_step_in_route);
            }
            System.out.println("You have arrived at destination: " + user_destination_building_name);
        } else {
            System.out.println("Invalid choice. Returning to main menu.");
        }

        return new_location_after_move;
    }

    private String handleShortestPathNavigation(String current_user_location_building_name, String user_destination_building_name, Deque<String> stack_of_user_visited_location_history) {
        List<String> list_of_buildings_in_shortest_path_route = campus_graph_instance.findShortestPath(current_user_location_building_name, user_destination_building_name);

        if (list_of_buildings_in_shortest_path_route == null || list_of_buildings_in_shortest_path_route.isEmpty()) {
            System.out.println("No route found from " + current_user_location_building_name + " to " + user_destination_building_name);
            return current_user_location_building_name;
        }

        System.out.println("Shortest route found: " + String.join(" -> ", list_of_buildings_in_shortest_path_route));
        System.out.println("Do you want to: 1) Move directly to destination  2) Move step-by-step  0) Cancel");

        if (!user_input_scanner_instance.hasNextInt()) {
            user_input_scanner_instance.next();
            System.out.println("Invalid input. Returning to main menu.");
            return current_user_location_building_name;
        }
        int user_choice_for_how_to_move_on_shortest_path = user_input_scanner_instance.nextInt();
        user_input_scanner_instance.nextLine();

        String new_location_after_move = current_user_location_building_name;

        if (user_choice_for_how_to_move_on_shortest_path == 0) {
            System.out.println("Cancelled.");
        } else if (user_choice_for_how_to_move_on_shortest_path == 1) {
            for (int path_step_index = 1; path_step_index < list_of_buildings_in_shortest_path_route.size(); path_step_index++) {
                String building_name_step_in_route = list_of_buildings_in_shortest_path_route.get(path_step_index);
                stack_of_user_visited_location_history.push(building_name_step_in_route);
            }
            new_location_after_move = user_destination_building_name;
            System.out.println("Moved directly to " + user_destination_building_name);
        } else if (user_choice_for_how_to_move_on_shortest_path == 2) {
            for (int path_step_index = 1; path_step_index < list_of_buildings_in_shortest_path_route.size(); path_step_index++) {
                String building_name_step_in_route = list_of_buildings_in_shortest_path_route.get(path_step_index);
                stack_of_user_visited_location_history.push(building_name_step_in_route);
                new_location_after_move = building_name_step_in_route;
                System.out.println("Moved to " + building_name_step_in_route);
            }
            System.out.println("You have arrived at destination: " + user_destination_building_name);
        } else {
            System.out.println("Invalid choice. Returning to main menu.");
        }

        return new_location_after_move;
    }

    private String handleBacktrack(String current_user_location_building_name, Deque<String> stack_of_user_visited_location_history) {
        if (stack_of_user_visited_location_history.size() <= 1) {
            System.out.println("Cannot backtrack further. You are at the starting location: " + stack_of_user_visited_location_history.peek());
            return current_user_location_building_name;
        } else {
            String popped_building_name_on_backtrack = stack_of_user_visited_location_history.pop();
            String previous_building_name_on_backtrack = stack_of_user_visited_location_history.peek();
            System.out.println("Backtracked from " + popped_building_name_on_backtrack + " to " + previous_building_name_on_backtrack);
            return previous_building_name_on_backtrack;
        }
    }

    private void showVisitedPath(Deque<String> stack_of_user_visited_location_history) {
        if (stack_of_user_visited_location_history.isEmpty()) {
            System.out.println("No visited path.");
        } else {
            StringBuilder string_builder_for_visited_path = new StringBuilder();
            Iterator<String> iterator_for_visited_path_stack = stack_of_user_visited_location_history.descendingIterator();

            while (iterator_for_visited_path_stack.hasNext()) {
                string_builder_for_visited_path.append(iterator_for_visited_path_stack.next());
                if (iterator_for_visited_path_stack.hasNext()) string_builder_for_visited_path.append(" -> ");
            }
            System.out.println("Visited path (start -> current): " + string_builder_for_visited_path.toString());
        }
    }

    private void runNoLocationMenu() {
        System.out.println("You chose not to share your location.");
        System.out.println("Choose an option:");
        System.out.println("1) Show campus connections");
        System.out.println("2) Exit");

        int user_menu_option_when_not_sharing_location;
        if (!user_input_scanner_instance.hasNextInt()) {
            user_input_scanner_instance.next();
            System.out.println("Invalid input. Program exit.");
            return;
        }
        user_menu_option_when_not_sharing_location = user_input_scanner_instance.nextInt();
        user_input_scanner_instance.nextLine();

        switch (user_menu_option_when_not_sharing_location) {
            case 1:
                campus_graph_instance.showAdjacencyList();
                break;
            case 2:
                System.out.println("Exit Goodbye!");
                break;
            default:
                System.out.println("Invalid choice. Program exit.");
        }
    }
}

public class Navigationsystem {

    private static void setupCampusData(Campus campus_object_to_be_setup) {
        String[] array_of_initial_building_names = {
                "GICT", "SAS", "AMSOM", "UC", "BK",
                "SPORTS", "FABRICATION SHOP", "AG",
                "VENTURE STUDIO", "OFFICE", "NOBURU", "BIONEST","SV DROP", "GYM",
                "TURF", "BADMINTON", "LIBRARY", "BLACK BOX", "BOOKSTORE", "CAFE", "HERITAGE"
        };

        for (String building_name_from_initial_array : array_of_initial_building_names) {
            campus_object_to_be_setup.addBuilding(building_name_from_initial_array);
        }

        campus_object_to_be_setup.connect("GICT", "SAS", 4.0);
        campus_object_to_be_setup.connect("SAS", "HERITAGE", 1.0);
        campus_object_to_be_setup.connect("GICT", "AMSOM", 3.0);
        campus_object_to_be_setup.connect("GICT", "SPORTS", 1.0);
        campus_object_to_be_setup.connect("SAS", "AMSOM", 1.0);
        campus_object_to_be_setup.connect("AMSOM", "SPORTS", 1.0);
        campus_object_to_be_setup.connect("AMSOM", "BIONEST", 2.0);
        campus_object_to_be_setup.connect("SPORTS", "BIONEST", 1.0);
        campus_object_to_be_setup.connect("SV DROP", "SAS", 2.0);
        campus_object_to_be_setup.connect("SAS", "BLACK BOX", 0.0);
        campus_object_to_be_setup.connect("GICT", "LIBRARY", 0.0);
        campus_object_to_be_setup.connect("UC", "TURF", 0.0);
        campus_object_to_be_setup.connect("UC", "BOOKSTORE", 0.0);
        campus_object_to_be_setup.connect("UC", "CAFE", 0.0);
        campus_object_to_be_setup.connect("UC", "GYM", 0.0);
        campus_object_to_be_setup.connect("UC", "OFFICE", 6.0);
        campus_object_to_be_setup.connect("SAS", "OFFICE", 7.0);
        campus_object_to_be_setup.connect("OFFICE", "NOBURU", 1.0);
        campus_object_to_be_setup.connect("UC", "BADMINTON", 0.0);
        campus_object_to_be_setup.connect("UC", "FABRICATION SHOP", 4.0);
        campus_object_to_be_setup.connect("SAS", "FABRICATION SHOP", 1.0);
        campus_object_to_be_setup.connect("SAS", "UC", 4.0);
        campus_object_to_be_setup.connect("AMSOM", "UC", 5.0);
        campus_object_to_be_setup.connect("UC", "GICT", 2.0);
        campus_object_to_be_setup.connect("GICT", "FABRICATION SHOP", 1.0);
        campus_object_to_be_setup.connect("SAS", "SPORTS", 5.0);
        campus_object_to_be_setup.connect("SAS", "BK", 8.0);
        campus_object_to_be_setup.connect("BK", "UC", 10.0);
        campus_object_to_be_setup.connect("UC", "AG", 6.0);
        campus_object_to_be_setup.connect("VENTURE STUDIO", "AG", 2.0);
    }

    public static void main(String[] command_line_arguments) {
        Campus campus_graph_object = new Campus();
        setupCampusData(campus_graph_object);

        Scanner user_input_scanner = new Scanner(System.in);
        NavigationUI console_user_interface_object = new NavigationUI(campus_graph_object, user_input_scanner);

        console_user_interface_object.start();
    }
}