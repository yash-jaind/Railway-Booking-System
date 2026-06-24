package models;

public class Passenger {

    private int passengerId;
    private String name;
    private int age;
    private String email;

    public Passenger(int passengerId, String name, int age, String email) {
        this.passengerId = passengerId;
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public Passenger(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public int getPassengerId() { return passengerId; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return "Passenger ID: " + passengerId +
               " | Name: " + name +
               " | Age: " + age +
               " | Email: " + email;
    }
}