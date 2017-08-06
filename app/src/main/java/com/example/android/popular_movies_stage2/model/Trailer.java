package com.example.android.popular_movies_stage2.model;

public class Trailer {
  private String id;
  private String movieID;
  private String key;
  private String name;
  private String site;
  private int size;
  private String type;

  public Trailer() {
    // empty
  }

  public String getId() { return id; }

  public String getMovieId() { return movieID; }
  public void setMovieID(String id) {
    movieID = id;
  }

  public String getKey() {return key; }

  public String getName() { return name; }

  public String getSite() { return site; }

  public int getSize() { return size; }

  public String getType() { return type; }

}
