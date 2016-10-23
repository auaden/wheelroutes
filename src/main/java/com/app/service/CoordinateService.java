package com.app.service;

import com.app.Utility.GpsUtility;
import com.app.Utility.StopWatch;
import com.app.dao.CoordinateDao;
import com.app.domain.Coordinate;
//import com.sun.tools.corba.se.idl.InterfaceGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.sql.Timestamp;
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

    public HashMap<Integer, HashMap<Integer, ArrayList<Coordinate>>> retrieveViewCoordinates(boolean isViewTable) {
        String tableName = "";
        ArrayList<Coordinate> coordinates;
        if (isViewTable) {
            tableName = coordProcessedTableName;
            coordinates = (ArrayList<Coordinate>)coordinateDao.findAll(tableName, false);
        } else {
            tableName = coordRawTableName;
            coordinates = (ArrayList<Coordinate>)coordinateDao.findAll(tableName, true);
        }

        if (coordinates == null) {
            return null;
        }
        coordinates = GpsUtility.removeDuplicatesAndNoFix(coordinates);
        //test filtering stationery points

        HashMap<Integer, HashMap<Integer, ArrayList<Coordinate>>> sortedCoordinates = GpsUtility.sortCoordinates(coordinates);
        return sortedCoordinates;
    }

    public void processData(HashMap<String, Integer> ratingMap) {
        ArrayList<Coordinate> coordinates = (ArrayList<Coordinate>)coordinateDao.findAll(coordRawTableName, true);
        if (coordinates == null) {
            return;
        }

        coordinates = GpsUtility.removeDuplicatesAndNoFix(coordinates);

        HashMap<String, ArrayList<Coordinate>> sortedCoordinateMap = GpsUtility.sortCoordinatesIntoMap(coordinates);

        for (Map.Entry<String, ArrayList<Coordinate>> entry : sortedCoordinateMap.entrySet()) {
            String compositeKey = entry.getKey();
            ArrayList<Coordinate> coordinatesList = entry.getValue();

            for (Coordinate coordinate : coordinatesList) {
                Timestamp timestamp = coordinate.getTimestamp();
                int userId = coordinate.getUserId();
                String key = userId + "," + timestamp;
                Integer rating = ratingMap.get(key);
                if (rating == null) {
                    System.out.println("NULL!" + timestamp);
                    coordinate.setRating(-1);
                } else {
                    coordinate.setRating(rating);
                }
            }

            applyLogicAndInsert(coordinatesList);
        }
    }

    public void processRestData(ArrayList<Coordinate> coordinates) {
        coordinateDao.insertRawBatch(coordinates, coordRawTableName);
//        coordinates = GpsUtility.removeDuplicatesAndNoFix(coordinates);
//        HashMap<String, ArrayList<Coordinate>> sortedCoordinateMap = GpsUtility.sortCoordinatesIntoMap(coordinates);
//
//        for (Map.Entry<String, ArrayList<Coordinate>> entry : sortedCoordinateMap.entrySet()) {
//            String compositeKey = entry.getKey();
//            ArrayList<Coordinate> coordinatesList = entry.getValue();
//            applyLogicAndInsert(coordinatesList);
//        }
    }

    private void applyLogicAndInsert(ArrayList<Coordinate> coordinatesList) {
        double avgSpd = GpsUtility.calculateRouteAvgSpd(coordinatesList);
        if (coordinatesList.size() > 100 && avgSpd <3) {
            coordinatesList = GpsUtility.applyKalmanFiltering(coordinatesList, 1, 10);
            coordinateDao.insertBatch(coordinatesList, coordProcessedTableName);
        }
    }

    public HashMap<Integer, HashMap<Integer, ArrayList<Coordinate>>> retrieveDataByDateAndUserId(int userId, String startDate, String endDate) {
        ArrayList<Coordinate> coordinates = (ArrayList<Coordinate>) coordinateDao.findByDate(userId, startDate, endDate, coordProcessedTableName, false);
        return GpsUtility.sortCoordinates(coordinates);
    }

    public TreeMap<Integer, TreeMap<String, Integer>> retrieveOverallCoordData() {
        TreeMap<Integer, TreeMap<String, Integer>> overall = new TreeMap<>();
        overall.putAll(coordinateDao.findOverallDataCollected(coordRawTableName));
        return overall;
    }

    //new algorithms

    public HashMap<ArrayList<Coordinate>, Integer> startProcessing (HashMap<String, Integer> ratingMap) {
        ArrayList<Coordinate> coordinates = (ArrayList<Coordinate>)coordinateDao.findAll(coordRawTableName, true);
        return runAlgorithms(coordinates, ratingMap);
    }

    public HashMap<ArrayList<Coordinate>, Integer> startProcessing (int userId,
                                                  String startDate,
                                                  String endDate,
                                                  HashMap<String, Integer> ratingMap) {

        ArrayList<Coordinate> coordinates =
                (ArrayList<Coordinate>)coordinateDao.findByDate(userId, startDate, endDate, coordRawTableName, true);
        return runAlgorithms(coordinates, ratingMap);
    }

    private HashMap<ArrayList<Coordinate>, Integer> runAlgorithms (ArrayList<Coordinate> coordinates, HashMap<String, Integer> ratingMap) {
        coordinates = GpsUtility.removeDuplicatesAndNoFix(coordinates);
        coordinates = removeDuplicates(coordinates);
        coordinates = keepDistanceBetween(coordinates, 2);
        TreeMap<Integer, ArrayList<Coordinate>> map = splitRoutes(coordinates, 5);
        coordinates = removeLessDenseClusters(map, 30);
        coordinates = setRatingToCoordinate(coordinates, ratingMap);

        coordinates = reduceStationaryPts(coordinates, 5.0);
        TreeMap<Integer, ArrayList<Coordinate>> routes = splitRoutes(coordinates, 120);
        routes = removeLessDenseClusters2(routes, 30);
        routes = smoothRoutes(routes);

        ArrayList<Coordinate> storedKey = null;

        HashMap<ArrayList<Coordinate>, Integer> viewMap = new HashMap<>();
        for (Map.Entry<Integer, ArrayList<Coordinate>> entry : routes.entrySet()) {
            ArrayList<Coordinate> coordinates2 = entry.getValue();

            for (int i = 0; i < coordinates2.size(); i++) {
                int currentRating = coordinates2.get(i).getRating();

                if (i < coordinates2.size() - 1) {
                    int nextRating = coordinates2.get(i + 1).getRating();

                    if (currentRating != nextRating && storedKey == null) {
                        ArrayList<Coordinate> newList = new ArrayList<>();
                        newList.add(coordinates2.get(i));
                        newList.add(coordinates2.get(i + 1));
                        int maxRating = getMaxRating(newList);
                        viewMap.put(newList, maxRating);
                        storedKey = null;
                    } else if (currentRating != nextRating) {
                        storedKey.add(coordinates2.get(i));
                        storedKey.add(coordinates2.get(i + 1));
                        int maxRating = getMaxRating(storedKey);
                        viewMap.put(storedKey, maxRating);
                        storedKey = null;
                    } else if (storedKey == null){
                        ArrayList<Coordinate> newList = new ArrayList<>();
                        newList.add(coordinates2.get(i));
                        storedKey = newList;
                    } else {
                        storedKey.add(coordinates2.get(i));
                    }
                }

            }
            storedKey = null;
        }

        return viewMap;
    }

    private int getMaxRating (ArrayList<Coordinate> coordinates) {
        int maxRating = 0;
        for (Coordinate coordinate : coordinates) {
            int rating = coordinate.getRating();
            if (rating > maxRating) {
                maxRating = rating;
            }
        }
        return maxRating;
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

    private TreeMap<Integer, ArrayList<Coordinate>> splitRoutes (ArrayList<Coordinate> coordinates, int timestampThreshold){
        int routeNo = 1;
        TreeMap<Integer, ArrayList<Coordinate>> map = new TreeMap<>();
        for (int i = 0; i < coordinates.size(); i++) {
            Coordinate coordinate = coordinates.get(i);
            if (i < coordinates.size() - 1) {
                Coordinate nextCoordinate = coordinates.get(i + 1);
                long timeInSec = coordinate.timeFromCurrent(nextCoordinate);
//                System.out.println(coordinate.toString() + " time to next: " + timeInSec);

                ArrayList<Coordinate> accepted3 = map.get(routeNo);
                if (accepted3 == null) {
                    accepted3 = new ArrayList<>();
                    map.put(routeNo, accepted3);
                }

                if (timeInSec < timestampThreshold) {
                    accepted3.add(coordinate);
                } else {
                    routeNo++;
                }
            }
        }
        return map;
    }

    private ArrayList<Coordinate> removeLessDenseClusters (TreeMap<Integer, ArrayList<Coordinate>> map,
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

    private TreeMap<Integer, ArrayList<Coordinate>> removeLessDenseClusters2 (TreeMap<Integer, ArrayList<Coordinate>> map,
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
                //System.out.println("NULL!" + timestamp);
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
