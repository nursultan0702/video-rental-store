import java.util.List;

public class Customer {
    String userName;
    int points;
    List<Movie> rentedMovie;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<Movie> getRentedMovie() {
        return rentedMovie;
    }

    public void setRentedMovie(List<Movie> rentedMovie) {
        this.rentedMovie = rentedMovie;
    }
}
