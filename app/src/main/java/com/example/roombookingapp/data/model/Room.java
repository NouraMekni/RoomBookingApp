package com.example.roombookingapp.data.model;

public class Room {
    private String id;
    private String name;
    private int capacity;
    private String description;

    public Room() {}

    public Room(String id, String name, int capacity, String description) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.description = description;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public String getDescription() { return description; }
}