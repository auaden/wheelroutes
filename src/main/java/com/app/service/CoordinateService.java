package com.app.service;

import com.app.Utility.GpsUtility;
import com.app.Utility.StopWatch;
import com.app.dao.CoordinateDao;
import com.app.domain.Coordinate;
//import com.sun.tools.corba.se.idl.InterfaceGen;
import com.app.domain.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by adenau on 5/8/16.
 */
@Service
public class CoordinateService {

    @Autowired
    private CoordinateDao coordinateDao;

    @Autowired
    public String coordRawTableName;

    @Autowired
    public String coordProcessedTableName;

    public HashMap<String, Route> retrieveViewCoordinates() {
        ArrayList<Coordinate> coordinates = (ArrayList<Coordinate>) coordinateDao.findAll(coordProcessedTableName, false);
        TreeMap<Integer, ArrayList<Coordinate>> map = splitRoutes(coordinates, 20);
        HashMap<String, Route> displayMap = sortIntoRoutesWithRating(map);
        return displayMap;
    }

    public HashMap<String, Route> retrieveViewCoordinates(int userId) {
        ArrayList<Coordinate> coordinates = (ArrayList<Coordinate>) coordinateDao.findById(userId, coordProcessedTableName, false);
        TreeMap<Integer, ArrayList<Coordinate>> map = splitRoutes(coordinates, 20);
        HashMap<String, Route> displayMap = sortIntoRoutesWithRating(map);
        return displayMap;
    }

    public void processData(int userId, String startDate, String endDate, HashMap<String, Integer> ratingMap) {
        ArrayList<Coordinate> coordinates =
                (ArrayList<Coordinate>)coordinateDao.findByDate(userId, startDate, endDate, coordRawTableName, true);
        System.out.println("running algorithms for routes.. coord size: " + coordinates.size());
        HashMap<String, Route> result = runAlgorithmsForRoutes(coordinates, ratingMap);
        for (Map.Entry<String, Route> entry : result.entrySet()) {
            System.out.println("key : " + entry.getKey() + " , " + entry.getValue());
            Route route = entry.getValue();
            ArrayList<Coordinate> routeCoords = route.getRoute();
            coordinateDao.insertBatch(routeCoords, coordProcessedTableName);
        }
    }

    public void processRestData(ArrayList<Coordinate> coordinates) {

//        coordinates = GpsUtility.removeDuplicatesAndNoFix(coordinates);
//        HashMap<String, ArrayList<Coordinate>> sortedCoordinateMap = GpsUtility.sortCoordinatesIntoMap(coordinates);
//
//        for (Map.Entry<String, ArrayList<Coordinate>> entry : sortedCoordinateMap.entrySet()) {
//            String compositeKey = entry.getKey();
//            ArrayList<Coordinate> coordinatesList = entry.getValue();
//            applyLogicAndInsert(coordinatesList);
//        }
    }

    public TreeMap<Integer, TreeMap<String, Integer>> retrieveOverallCoordData() {
        TreeMap<Integer, TreeMap<String, Integer>> overall = new TreeMap<>();
        overall.putAll(coordinateDao.findOverallDataCollected(coordRawTableName));
        return overall;
    }

    //new algorithms

    //ROUTES VIEW-----------------------------------------------------------------------------------------
    public HashMap<String, Route> startProcessingForRoutes (int userId,
                                                                              String startDate,
                                                                              String endDate,
                                                                              HashMap<String, Integer> ratingMap) {

        ArrayList<Coordinate> coordinates =
                (ArrayList<Coordinate>)coordinateDao.findByDate(userId, startDate, endDate, coordRawTableName, true);
        System.out.println("running algorithms for routes.. coord size: " + coordinates.size());
        return runAlgorithmsForRoutes(coordinates, ratingMap);
    }


    private HashMap<String, Route> runAlgorithmsForRoutes (ArrayList<Coordinate> coordinates,
                                                                   HashMap<String, Integer> ratingMap) {
        coordinates = GpsUtility.removeDuplicatesAndNoFix(coordinates);
        coordinates = removeDuplicates(coordinates);

        coordinates = keepDistanceBetween(coordinates, 2);
        coordinates = reduceStationaryPts(coordinates, 2.0);
        coordinates = setRatingToCoordinate(coordinates, ratingMap);

        TreeMap<Integer, ArrayList<Coordinate>> map = splitRoutes(coordinates, 20);

        map = removeLessDenseClustersForRoutes(map, 50);
        map = smoothRoutes(map);


        TreeMap<Integer, ArrayList<Coordinate>> newMap = new TreeMap<>();

        TreeMap<String, Integer> testMap = new TreeMap<>();

        for (Map.Entry<Integer, ArrayList<Coordinate>> entry : map.entrySet()) {
            ArrayList<Coordinate> coords = entry.getValue();
            for (Coordinate coord : coords) {
                double lat = coord.getLatitude();
                double lng = coord.getLongitude();
                DecimalFormat df = new DecimalFormat("#.####");
                df.setRoundingMode(RoundingMode.HALF_EVEN);
                double newLat = Double.parseDouble(df.format(lat));
                double newLng = Double.parseDouble(df.format(lng));

                coord.setLatitude(newLat);
                coord.setLongitude(newLng);

                String compositeKey = df.format(lat) + "," + df.format(lng);
                Integer coordCountInGrid = testMap.get(compositeKey);
                if (coordCountInGrid == null) {
                    testMap.put(compositeKey, 1);
                } else {
                    coordCountInGrid++;
                    testMap.put(compositeKey, coordCountInGrid);
                }
            }
            //  GpsUtility.applyKalmanFiltering(coords, 1, 15);

        }

        for (Map.Entry<String, Integer> entry : testMap.entrySet()) {
            String key = entry.getKey();
            String[] coord = key.split(",");
            double lat = Double.parseDouble(coord[0]);
            double lng = Double.parseDouble(coord[1]);

        }

        HashMap<String, Route> displayMap = sortIntoRoutesWithRating(map);

        return displayMap;
    }






    //COORDINATES VIEW-----------------------------------------------------------------------------------------


    public ArrayList<Coordinate> startProcessingForCoordinates (int userId,
                                                                String startDate,
                                                                String endDate,
                                                                HashMap<String, Integer> ratingMap) {

        ArrayList<Coordinate> coordinates =
                (ArrayList<Coordinate>)coordinateDao.findByDate(userId, startDate, endDate, coordRawTableName, true);
        return runAlgorithmsForCoordinates(coordinates, ratingMap);
    }


    private ArrayList<Coordinate> runAlgorithmsForCoordinates (ArrayList<Coordinate> coordinates,
                                                                            HashMap<String, Integer> ratingMap) {
        coordinates = GpsUtility.removeDuplicatesAndNoFix(coordinates);
        coordinates = removeDuplicates(coordinates);
        coordinates = keepDistanceBetween(coordinates, 2);
        TreeMap<Integer, ArrayList<Coordinate>> map = splitRoutes(coordinates, 5);
        coordinates = removeLessDenseClustersForCoordinates(map, 30);
        coordinates = setRatingToCoordinate(coordinates, ratingMap);
        coordinates = reduceStationaryPts(coordinates, 5.0);
        return coordinates;
    }

    private HashMap<String, Route> sortIntoRoutesWithRating (TreeMap<Integer, ArrayList<Coordinate>> routes ) {

        HashMap<String, Route> displayMap = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<Coordinate>> entry : routes.entrySet()) {
            ArrayList<Coordinate> coordinates = entry.getValue();
            int routeId = entry.getKey();
            System.out.println("route id: " + entry.getKey());

            int routeId2 = 1;

            for (Coordinate coordinate : coordinates) {
                String compositeKey = "" + routeId + "," + routeId2;

                Route route = displayMap.get(compositeKey);
                if (route == null) {
                    route = new Route(new ArrayList<Coordinate>());
                    route.addCoordinateToRoute(coordinate);
                    displayMap.put(compositeKey, route);
                } else {
                    Coordinate lastCoord = route.getLastCoordinate();
                    if (lastCoord != null) {
                        int lastRating = lastCoord.getRating();
                        if (lastRating == coordinate.getRating()) {
                            route.addCoordinateToRoute(coordinate);
                        } else {
                            route.setRating(lastRating);
                            route = new Route(new ArrayList<Coordinate>());
                            route.addCoordinateToRoute(lastCoord);
                            route.addCoordinateToRoute(coordinate);
                            routeId2++;
                            String compositeKey2 = "" + routeId + "," + routeId2;
                            displayMap.put(compositeKey2, route);
                        }
                    }
                }
            }
        }

        return displayMap;
    }

    private ArrayList<Coordinate> removeDuplicates (ArrayList<Coordinate> coordinates) {
        ArrayList<Coordinate> accepted = new ArrayList<>();
        for (int i =0; i < coordinates.size(); i++) {
            Coordinate coordinate = coordinates.get(i);
            if (i < coordinates.size() - 1) {
                Coordinate nextCoordinate = coordinates.get(i + 1);
                //System.out.println(coordinate.toString() + " distance to next: " + coordinate.distanceFrom(nextCoordinate));
                double distance = coordinate.distanceFrom(nextCoordinate);
                if (distance > 0.0) {
                    accepted.add(coordinate);
                }
            }
        }
        return accepted;
    }

    private ArrayList<Coordinate> keepDistanceBetween (ArrayList<Coordinate> coordinates, int distanceThreshold){
        ArrayList<Coordinate> accepted = new ArrayList<>();
        for (int i =0; i < coordinates.size(); i++) {
            Coordinate coordinate = coordinates.get(i);
            if (i < coordinates.size() - 1) {
                Coordinate nextCoordinate = coordinates.get(i + 1);
                double distance = coordinate.distanceFrom(nextCoordinate);
                if (distance < distanceThreshold) {
                    accepted.add(coordinate);
                }
//                System.out.println(coordinate.toString() + " distance to next: " + coordinate.distanceFrom(nextCoordinate));
            }
        }
        return accepted;
    }

    private TreeMap<Integer, ArrayList<Coordinate>> splitRoutes (ArrayList<Coordinate> coordinates, long timeThreshold){
        int routeNo = 1;
        TreeMap<Integer, ArrayList<Coordinate>> map = new TreeMap<>();
        for (int i = 0; i < coordinates.size(); i++) {
            Coordinate coordinate = coordinates.get(i);
            if (i < coordinates.size() - 1) {
                Coordinate nextCoordinate = coordinates.get(i + 1);
                long timeInSec = coordinate.timeFromCurrent(nextCoordinate);
                double distance = coordinate.distanceFrom(nextCoordinate);
                //System.out.println("time: " + timeInSec + " dist: " + distance + " avg spd: " + avgSpd);

                ArrayList<Coordinate> accepted3 = map.get(routeNo);
                if (accepted3 == null) {
                    accepted3 = new ArrayList<>();
                    map.put(routeNo, accepted3);
                }

                if (timeInSec < timeThreshold) {
                    accepted3.add(coordinate);
                } else {
                    if (distance < 3.0) {
                        accepted3.add(coordinate);
                    } else {
                        routeNo++;
                    }
                }
            }
        }
        return map;
    }

    private ArrayList<Coordinate> removeLessDenseClustersForCoordinates (TreeMap<Integer, ArrayList<Coordinate>> map,
                                                          int densityThreshold) {
        ArrayList<Coordinate> accepted = new ArrayList<>();
        for (Map.Entry<Integer, ArrayList<Coordinate>> entry : map.entrySet()) {
            int routeNo2 = entry.getKey();
            ArrayList<Coordinate> coordinates2 = entry.getValue();
            if (coordinates2.size() > densityThreshold) {
                accepted.addAll(coordinates2);
            }
        }
        return accepted;
    }

    private TreeMap<Integer, ArrayList<Coordinate>> removeLessDenseClustersForRoutes (TreeMap<Integer, ArrayList<Coordinate>> map,
                                                           int densityThreshold) {
        ArrayList<Integer> intList = new ArrayList<>();
        for (Map.Entry<Integer, ArrayList<Coordinate>> entry : map.entrySet()) {
            ArrayList<Coordinate> coordinates2 = entry.getValue();
//            System.out.println("route no: " + entry.getKey() + " size: " + coordinates2.size());
            if (coordinates2.size() < densityThreshold) {
                intList.add(entry.getKey());
            }
        }
        for (int i : intList) {
            map.remove(i);
        }
        return map;
    }

    private ArrayList<Coordinate> setRatingToCoordinate (ArrayList<Coordinate> coordinates, HashMap<String, Integer> ratingMap) {
        for (Coordinate coordinate : coordinates) {
            Timestamp timestamp = coordinate.getTimestamp();
            int userId = coordinate.getUserId();
            String key = userId + "," + timestamp;
            Integer rating = ratingMap.get(key);
            if (rating == null) {
                coordinate.setRating(-1);
            } else {
                coordinate.setRating(rating);
            }
        }
        return coordinates;
    }

    private TreeMap<Integer, ArrayList<Coordinate>> smoothRoutes (TreeMap<Integer, ArrayList<Coordinate>> routes) {
        for (Map.Entry<Integer, ArrayList<Coordinate>> entry : routes.entrySet()) {
            ArrayList<Coordinate> coordinates2 = entry.getValue();
            GpsUtility.applyKalmanFiltering(coordinates2, 1, 5);
        }
        return routes;
    }

    private ArrayList<Coordinate> reduceStationaryPts(ArrayList<Coordinate> coordinates, double minDist) {
        ArrayList<Coordinate> test = new ArrayList<>();
        Iterator iter = coordinates.iterator();
        Coordinate forgottenCoordinate = null;
        while(iter.hasNext()){
            Coordinate coordinate;
            Coordinate nextCoordinate;
            if (forgottenCoordinate == null) {
                coordinate = (Coordinate) iter.next();
                nextCoordinate = (Coordinate) iter.next();
            } else {
                coordinate = forgottenCoordinate;
                nextCoordinate = (Coordinate) iter.next();
            }

            double dist = coordinate.distanceFrom(nextCoordinate);
            if (dist < minDist) {
                iter.remove();
                forgottenCoordinate = coordinate;
            } else {
                forgottenCoordinate = nextCoordinate;
                if (iter.hasNext()) {
                    dist = coordinate.distanceFrom(nextCoordinate);
                    if (dist < minDist) {
                        test.add(forgottenCoordinate);
                    }
                    forgottenCoordinate = nextCoordinate;
                }
            }
        }
        coordinates.removeAll(test);
        return coordinates;
    }

}
