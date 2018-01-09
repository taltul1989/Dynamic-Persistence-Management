package server.entities;

/**
 * Created by Tal on 30/12/2017.
 */
public class Person implements Entity {

    private int id;
    private String name;

    public Person(){}


    public Person(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
       return
               "id:" + this.id + "," +
                "name:" + this.name;
    }
}
