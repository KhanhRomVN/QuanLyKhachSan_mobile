package com.example.hotel_management.data.model;

public class Hotel {
    private String id;
    private String name;
    private String address;
    private String description;
    private String ownerId;
    private String imagePath;
    private double price;
    private float rating;
    private String amenities;

    public Hotel() {}

    public Hotel(String id, String name, String address, String description, String ownerId, String imagePath, double price, float rating, String amenities) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.ownerId = ownerId;
        this.imagePath = imagePath;
        this.price = price;
        this.rating = rating;
        this.amenities = amenities;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }
}
