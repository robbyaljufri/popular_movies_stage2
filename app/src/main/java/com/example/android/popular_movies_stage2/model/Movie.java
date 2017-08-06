package com.example.android.popular_movies_stage2.model;

public class Movie  {

  private int id;
  private String poster_path;
  private String overview;
  private String release_date;
  private String original_title; // title
  private double vote_average; // rating
  private double popularity; // popularity
  private String[] reviews; //


  public Movie () {
    // empty
  }

  @Override
  public String toString() {

    StringBuffer toReturn = new StringBuffer();
    toReturn.append("ID: " + Integer.toString(id) + "\n");
    toReturn.append("Title: " + original_title + "\n");
    toReturn.append("Overview: " + overview + "\n");
    toReturn.append("Release Date: " + getReleaseDate() + "\n");
    toReturn.append("Rating: " + getRating() + "\n");
    toReturn.append("Popularity: " + Double.toString(popularity) + "\n");
    toReturn.append("Poster path: " + poster_path);
    return toReturn.toString();

  }

  public int getId() {
    return id;
  }

  public double getRating() {
    return vote_average;
  }

  public double getPopularity() {
    return popularity;
  }

  public String getOverview() {
    return overview;
  }

  public String getTitle() {
    return original_title;
  }

  public String getPosterPath() {
    return poster_path;
  }

  public String getReleaseDate() {
    return release_date;
  }
}
