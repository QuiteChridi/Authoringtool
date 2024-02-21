package model;

public class User {

    private int userId;
    private String username;
    private String email;
    private String password;

    private int coins;

    public User(int userId, String username, String email, String password, int coins) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.coins=coins;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
    public int getCoins(){
        return coins;
    }


    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCoins(int coins){
        this.coins=coins;
    }

}
