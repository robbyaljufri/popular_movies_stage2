package com.example.android.popular_movies_stage2.model;

public class Review {

  private String id;
  private String movieID;
  private String author;
  private String content;
  private String url;

  public Review() {
    //empty
  }

  public String getId() {
    return id;
  }

  public String getMovieID() {
    return movieID;
  }

  public void setMovieID(String id) {
    movieID = id;
  }

  public String getAuthor() {
    return author;
  }

  public String getContent() {
    return content;
  }

  public String getUrl() {
    return url;
  }


}
