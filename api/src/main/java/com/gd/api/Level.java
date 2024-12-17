package com.gd.api;

public class Level {
    private int id;
    private String name;
    private String creator;
    private String verifier;
    private String difficulty;
    private double rating;
    private String length;
    private int attempts;
    private int completionPercentage;


    public Level() {}

    public Level(int id, String name, String creator, String verifier, String difficulty, 
                double rating, String length, int attempts, int completionPercentage) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.verifier = verifier;
        this.difficulty = difficulty;
        this.rating = rating;
        this.length = length;
        this.attempts = attempts;
        this.completionPercentage = completionPercentage;
    }


    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }

    public String getVerifier() { return verifier; }
    public void setVerifier(String verifier) { this.verifier = verifier; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getLength() { return length; }
    public void setLength(String length) { this.length = length; }

    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }

    public int getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(int completionPercentage) { this.completionPercentage = completionPercentage; }
}
