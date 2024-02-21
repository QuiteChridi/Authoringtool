package model;

public class Joker {
    private int idJoker;
    private String nameJoker;
    private int costJoker;

    private String descriptionJoker;

    public Joker(int id, String name, int cost, String description){
        this.costJoker= cost;
        this.nameJoker= name;
        this.idJoker= id;
        this.descriptionJoker=description;
    }

    public int getIdJoker() {
        return idJoker;
    }

    public void setIdJoker(int idJoker) {
        this.idJoker = idJoker;
    }

    public String getNameJoker() {
        return nameJoker;
    }

    public void setNameJoker(String nameJoker) {
        this.nameJoker = nameJoker;
    }

    public int getCostJoker() {
        return costJoker;
    }

    public void setCostJoker(int costJoker) {
        this.costJoker = costJoker;
    }

    public String getDescriptionJoker() {
        return descriptionJoker;
    }

    public void setDescriptionJoker(String descriptionJoker) {
        this.descriptionJoker = descriptionJoker;
    }
}
