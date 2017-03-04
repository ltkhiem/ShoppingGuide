package com.nimah.khiem.shoppingguide;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khiem on 9/23/2016.
 */


public class Landmark {
    private String name;
    private float score;
    private List<MapCoord> vertices;
    private String imgUrl = "";
    private String wikiUrl = "";
    private String youtubeUrl = "";
    private String homepageUrl = "";
    private MapCoord coord;

    public Landmark(String name, float score, List<MapCoord> vertices) {
        this.name = name;
        this.score = score;
        this.vertices = vertices;
    }


    public Landmark(String name, String imgUrl, String wikiUrl, String youtubeUrl, String homepageUrl, MapCoord coord) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.wikiUrl = wikiUrl;
        this.youtubeUrl = youtubeUrl;
        this.homepageUrl = homepageUrl;
        this.coord = coord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public List<MapCoord> getVertices() {
        return vertices;
    }

    public void setVertices(List<MapCoord> vertices) {
        this.vertices = vertices;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getWikiUrl() {
        return wikiUrl;
    }

    public void setWikiUrl(String wikiUrl) {
        this.wikiUrl = wikiUrl;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getHomepageUrl() {
        return homepageUrl;
    }

    public void setHomepageUrl(String homepageUrl) {
        this.homepageUrl = homepageUrl;
    }

    public MapCoord getCoord() {
        return coord;
    }

    public void setCoord(MapCoord coord) {
        this.coord = coord;
    }
}
