import java.sql.SQLOutput;
import java.util.*;

public class Main {
    private static final String[] MENU_ITEMS = {
            "Login as admin",
            "Print all movies",
            "Print all available movies",
            "Rent movie",
            "Return movie",
            "Print rents",
            "Logout",
            "EXIT"
    };
    private static final String[] ADMIN_MENU_ITEMS = {
            "Back to main menu",
            "Add movie",
            "Delete movie",
            "Change the type of movie"
    };

    private static final String[] MOVIE_TYPES = {
            "New releases",
            "Regular films",
            "Old films"
    };

    private static Customer customerSession = null;
    private static final Scanner scanner = new Scanner(System.in);
    private static int idIncrement = 0;
    private static List<Movie> movies = new ArrayList<>();
    private static List<Customer> customerList = new ArrayList<>();
    private static List<Rent> rentList = new ArrayList<>();
    private static final int[] fee = {40,30};

    public static void main(String[] args) {
        String inputString;
        String helloUser = "";
        if(customerSession != null){
            helloUser = customerSession.getUserName();
        }
        System.out.println("Hello "+helloUser+"! Welcome to video rental store! Choose one of them (enter number from list):\n");
        printList(MENU_ITEMS);
        inputString = scanner.nextLine();
        if(!inputString.isEmpty() || Integer.parseInt(inputString)<6){
            switch (Integer.parseInt(inputString)){
                case 1:
                   adminActions(args);
                   break;
                case 2:
                    printMovies(false);
                    main(args);
                case 3:
                    printMovies(true);
                    main(args);
                case 4:
                    rentMovie(args,getCustomer(args));
                    main(args);
                case 5:
                    returnMovie(args);
                    main(args);
                case 6:
                    printRents();
                    main(args);
                case 7:
                    logout();
                    main(args);
                case 8:
                    System.exit(0);
            }
        }else{
            System.out.println("Please enter number from list");
            main(args);
        }

    }

    private static void printRents() {
        for (Rent rent: rentList){
            System.out.println(rent.getCustomer().getUserName() + " rented movie " + rent.getMovie().getName() + " days: " + rent.getDays()+ " sum: " + rent.getPrice());
        }
    }

    private static void logout() {
        customerSession = null;
    }

    private static void printList(String[] listToPrint){
        for (int i = 0; i< listToPrint.length; i++){
            System.out.println(i+1 + ". " + listToPrint[i]);
        }
    }

    private static void adminActions(String[] args){
        String inputString;
        System.out.println("Welcome to admin panel: Choose one of them:");
        printList(ADMIN_MENU_ITEMS);
        inputString = scanner.nextLine();
        if(!inputString.isEmpty() || Integer.parseInt(inputString)<6){
            switch (Integer.parseInt(inputString)){
                case 1:
                    main(args);
                case 2:
                    System.out.println("Enter name of new movie:");
                    String nameOfMovie = scanner.nextLine();
                    System.out.println("Choose type of movie");
                    printList(MOVIE_TYPES);
                    Integer typeOfMovie = Integer.parseInt(scanner.nextLine());
                    if(nameOfMovie.length() < 25 && typeOfMovie<=3){
                        System.out.println(addMovie(nameOfMovie,typeOfMovie));
                        adminActions(args);
                    }else{
                        System.out.println("Incorrect name or type of movie, name can contains maximum 25 characters");
                        adminActions(args);
                    }
                case 3:
                    System.out.println("Choose film to delete:");
                    printMovies(true);
                    String inputDelete = scanner.nextLine();
                    deleteMovie(Integer.parseInt(inputDelete)-1);
                    adminActions(args);
                case 4:
                    System.out.println("Choose film to change type:");
                    printMovies(false);
                    String inputToChange = scanner.nextLine();
                    System.out.println("Choose new type:");
                    printList(MOVIE_TYPES);
                    String newType = scanner.nextLine();
                    changeTypeOfMovie(Integer.parseInt(inputToChange)-1,Integer.parseInt(newType)-1);
            }
        }else{
            System.out.println("Please enter number from list");
            adminActions(args);
        }
    }

    private static void changeTypeOfMovie(int movieId, int typeId) {
    Movie movie = movies.get(movieId);
    movie.setType(MOVIE_TYPES[typeId]);
    movies.set(movieId,movie);
    }

    private static String addMovie(String nameOfMovie, Integer typeOfMovie){
        Movie movie = new Movie();
        movie.setId(idIncrement);
        movie.setName(nameOfMovie);
        movie.setType(MOVIE_TYPES[typeOfMovie-1]);
        movie.setRented(false);
        movies.add(movie);
        idIncrement++;
        return movie.toString() + " added";

    }

    private static void printMovies(boolean isAvailable) {
        for (Movie movie:movies){
            if(isAvailable){
                System.out.println(!movie.getRented()?movie.getId() + 1 + ". " + movie:"");
            }else {
                System.out.println(movie.getId() + 1 + ". " + movie);
            }
        }
    }

    private static Customer getCustomer(String[] args){
        if(customerSession == null) {
            System.out.println("Enter your username:");
            String username = scanner.nextLine();
            if (username.length() > 10) {
                System.out.println("username should contain less than 10 characters, try again:");
                getCustomer(args);
            } else {
                Customer customer = getOrCreateCustomer(username);
                return customer;
            }
        }
        return customerSession;
    }

    private static void rentMovie(String[] args, Customer customer){
        if (countAviailableMovies()>0) {
            System.out.println("Choose movie for rent:");
            printMovies(true);
            String movieId = scanner.nextLine();
            System.out.println("Enter number of days to rent:");
            String days = scanner.nextLine();
            try{
                Movie choosenMovie = movies.get(Integer.parseInt(movieId) - 1);
                int daysToRent = Integer.parseInt(days);
                String totalSum = calculatePrice(daysToRent,choosenMovie,customer.getPoints());
                    System.out.println("Total is:" + totalSum + "\n");
                System.out.println("Do you want to rent? (enter yes or no)");
                String answer = scanner.nextLine();
                if(answer.toLowerCase().equals("no") || answer.toLowerCase().equals("n"))
                    main(args);
                choosenMovie.setRented(true);
                movies.set(choosenMovie.getId(), choosenMovie);
                if (customer.getRentedMovie() == null) {
                    List<Movie> newMovies = new ArrayList<>();
                    newMovies.add(choosenMovie);
                    customer.setRentedMovie(newMovies);
                } else {
                    customer.getRentedMovie().add(choosenMovie);
                }
                customer.setPoints(calculatePoints(daysToRent,choosenMovie));
                customerSession = customer;
                customerList.add(customer);
                String[] sumStr = totalSum.split("euro");
                int sum = Integer.parseInt(sumStr[0].replace(" ",""));
                Rent rent = new Rent();
                rent.setCustomer(customer);
                rent.setMovie(choosenMovie);
                rent.setDays(daysToRent);
                rent.setPrice(sum);
                rentList.add(rent);
            }catch (NumberFormatException ex){
                System.out.println("In correct choice try again");
                rentMovie(args,customerSession);
            }
        }else {
            System.out.println("no available movies for rent");
            main(args);
        }
    }

    private static void returnMovie(String[] args){
        if(customerSession == null || customerSession.getRentedMovie() == null){
            System.out.println("You don't have rented movies");
            main(args);
        }else {
            List<Movie> customerMovies = customerSession.getRentedMovie();
            for (int i = 0; i<customerMovies.size(); i++){
                System.out.println(i+1 + ". " + customerMovies.get(i).getName());
            }
            String inputString = scanner.nextLine();
            try{
                Movie toReturn = customerMovies.get(Integer.parseInt(inputString)-1);
                toReturn.setRented(false);
                movies.set(toReturn.getId(),toReturn);
                for (Rent rent:rentList){
                    if(rent.getCustomer() == customerSession && rent.getMovie() == toReturn)
                        rentList.remove(rent);
                }
                main(args);
            }catch (NumberFormatException ex){
                System.out.println("Incorrect choice, try again");
                returnMovie(args);
            }
        }
    }
    private static String calculatePrice(int days,Movie movie, int points){
        int originalDays = days;
        int originalPoints = points;
        if(points>=25){
           int pointDays = points/25;
           points = points%25;
           int result = days-pointDays;
           if (result>0) {
               days = result;
           }else{
               points+=Math.abs(result);
           }
        }
        int usedPoints = originalPoints-points;
        int payedByBonusDays = originalDays-days;
        if(movie.getType().equals(MOVIE_TYPES[0])){
           return String.valueOf(days*fee[0])+ " euro " + "used points: " + usedPoints + " days payed by bonus: " + payedByBonusDays;
       }else if(movie.getType().equals(MOVIE_TYPES[1])){
            int sum = days<3 ? fee[1] :fee[1] + days*fee[1];
           return String.valueOf(sum)+ " euro " + "used points: " + usedPoints + " days payed by bonus: " + payedByBonusDays;
       }else if(movie.getType().equals(MOVIE_TYPES[2])){
            int sum = days<5 ? fee[1] :fee[1] + days*fee[1];
           return  String.valueOf(sum)+ " euro" + " used points: " + usedPoints + " days payed by bonus: " + payedByBonusDays;
       }
       return String.valueOf(0);
    }

    private static int calculatePoints(int days,Movie movie){
        if(movie.getType().equals(MOVIE_TYPES[0])){
            return 25;
        }
        return 1;
    }

    private static Customer getOrCreateCustomer(String username){
        if(customerList.contains(username)) {
            for (Customer customer : customerList) {
                if (customer.getUserName().equals(username))
                    customerSession = customer;
                    return customer;
            }
        }
        Customer customer = new Customer();
        customer.setUserName(username);
        customer.setPoints(0);
        customerSession = customer;
        return customer;
    }

    private static int countAviailableMovies(){
        int counter = 0;
        for (Movie movie:movies) {
            if(movie.getRented()==false)
                counter++;
        }
        if(counter>0) {
            return counter;
        }
        return 0;
    }
    private static void deleteMovie(int movieId){
        movies.remove(movieId);
    }
}
