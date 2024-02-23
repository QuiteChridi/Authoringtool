package model;

public class AmountJoker {
    int userId;
    int jokerId;
    int amount;

    public AmountJoker(int userId, int jokerId, int amount){
        this.userId=userId;
        this.jokerId=jokerId;
        this.amount=amount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getJokerIdId() {
        return jokerId;
    }

    public void setJokerId(int jokerId) {
        this.jokerId = jokerId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
