package server.entities;

/**
 * Created by Tal on 30/12/2017.
 */
public class Vehicle implements Entity {

    //id needs to be first!!
    private int id;
    private String color;

    public Vehicle(int id, String color){
        this.id = id;
        this.color =color;
    }

    public Vehicle(){}

    @Override
    public int getId() {
        return 0;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return
                "id:" + this.id + "," +
                "name:" + this.color;
    }
}
