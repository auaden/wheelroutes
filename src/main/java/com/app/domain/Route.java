package com.app.domain;

import java.util.ArrayList;

/**
 * Created by adenau on 26/10/16.
 */
public class Route {

    private ArrayList<Coordinate> route;
    private int rating;

    public Route(ArrayList<Coordinate> route) {
        this.route = route;
    }

    public ArrayList<Coordinate> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<Coordinate> route) {
        this.route = route;
    }

    public void addCoordinateToRoute (Coordinate coordinate) {
        route.add(coordinate);
    }

    public Coordinate getLastCoordinate() {
        if (route.isEmpty()) {
            return null;
        } else {
            return route.get(route.size() - 1);
        }
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
