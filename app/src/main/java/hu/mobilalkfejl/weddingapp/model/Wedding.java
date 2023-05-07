package hu.mobilalkfejl.weddingapp.model;

public class Wedding {
    private String id;
    private String name;
    private String location;
    private boolean available;
    private int imageResource;

    public Wedding(String name, String location, boolean available, int imageResource) {
        this.name = name;
        this.location = location;
        this.available = available;
        this.imageResource = imageResource;
    }

    public Wedding() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}
