package taass.payload;


public class News {
    String user;
    String title;
    String message;
    Long rentID;

    public News(String user, Long rentID, String title, String message){
        this.user = user;
        this.title = title;
        this.message = message;
        this.rentID = rentID;
    }

    public Long getRentID() {
        return rentID;
    }

    public void setRentID(Long rentID) {
        this.rentID = rentID;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
