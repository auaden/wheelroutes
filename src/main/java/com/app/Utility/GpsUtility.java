package com.app.Utility;

import com.app.domain.Coordinate;
//import com.sun.tools.corba.se.idl.constExpr.Times;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by adenau on 6/9/16.
 */
public class GpsUtility {
    private static final int KM2M = 1000;
    private static final int EARTH_RADIUS = 6371;

    public static Double calculateDistance(double lat1, double lat2, double lon1, double lon2) {
        final double dLat = Math.toRadians(lat2 - lat1);
        final double dLon = Math.toRadians(lon2 - lon1);
        final Double olat1 = Math.toRadians(lat1);
        final Double olat2 = Math.toRadians(lat2);

        final double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(
                olat1) * Math.cos(olat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * KM2M * c;
    }

    private static double calculateRouteTotalDistance(ArrayList<Coordinate> coordinates) {
        double totalDistance = 0;
        for (int i = 0; i < coordinates.size(); i++) {
            Coordinate currentCoord = coordinates.get(i);
            double lat1 = currentCoord.getLatitude();
            double lng1 = currentCoord.getLongitude();
            if (i != coordinates.size()-1) {
                Coordinate nextCoord = coordinates.get(i+1);
                double lat2 = nextCoord.getLatitude();
                double lng2 = nextCoord.getLongitude();
                double currentDistance = GpsUtility.calculateDistance(lat1, lat2, lng1, lng2);
                totalDistance += currentDistance;
            }
        }
        return totalDistance;
    }

    private static double calculateRouteTotalTime(ArrayList<Coordinate> coordinates) {
        Coordinate firstCoord = coordinates.get(0);
        Coordinate lastCoord = coordinates.get(coordinates.size()-1);
        return(lastCoord.getTimestamp().getTime() - firstCoord.getTimestamp().getTime())/1000;
    }

    public static double calculateRouteAvgSpd(ArrayList<Coordinate> coordinates) {
        return GpsUtility.calculateRouteTotalDistance(coordinates)/GpsUtility.calculateRouteTotalTime(coordinates);
    }

    public static ArrayList<Coordinate> applyKalmanFiltering(ArrayList<Coordinate> coordinates, float q, float accuracy) {
        ArrayList<Coordinate> newCoordinates = new ArrayList<>();

        Coordinate stateCoord = coordinates.get(0);
        Timestamp firstTimestamp = stateCoord.getTimestamp();
        double firstLat = stateCoord.getLatitude();
        double firstlng = stateCoord.getLongitude();
        KalmanLatLong kalmanFilter = new KalmanLatLong(q);
        kalmanFilter.setState(firstLat, firstlng, accuracy, firstTimestamp.getTime());
        for (Coordinate coord : coordinates) {
            Timestamp timestamp = coord.getTimestamp();
            double lat = coord.getLatitude();
            double lng = coord.getLongitude();
            kalmanFilter.process(lat, lng, accuracy, timestamp.getTime());
            coord.setLatitude(kalmanFilter.get_lat());
            coord.setLongitude(kalmanFilter.get_lng());
            newCoordinates.add(coord);
        }

        return newCoordinates;
    }

    public static ArrayList<Coordinate> removeDuplicatesAndNoFix(ArrayList<Coordinate> coordinates) {
        HashMap<String, Coordinate> coordMap = new HashMap<>();
        ArrayList<Coordinate> processedCoords = new ArrayList<>();

        for (int i = 0; i < coordinates.size(); i++) {
            Coordinate coordinate = coordinates.get(i);
            if (coordinate != null) {
                String identifier = coordinate.getTimestamp() + "," + coordinate.getLatitude() + "," + coordinate.getLongitude();
                if (coordMap.get(identifier) == null) {
                    coordMap.put(identifier, coordinate);
                }
            }
        }

        Iterator iter = coordMap.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry pair = (Map.Entry) iter.next();
            processedCoords.add((Coordinate) pair.getValue());
        }

        Collections.sort(processedCoords, new Comparator<Coordinate> () {
                public int compare(Coordinate o1, Coordinate o2){

                    int value1;
                    if (o1.getUserId() > o2.getUserId()) {
                        value1 = 1;
                    } else if (o1.getUserId() < o2.getUserId()) {
                        value1 = -1;
                    } else {
                        value1 = 0;
                    }

                    if (value1 == 0) {
                        int value2 = o1.getTimestamp().compareTo(o2.getTimestamp());
                        return value2;
                    } else {
                        return value1;
                    }
                }
            });

        return processedCoords;
    }

    public static HashMap<Integer, ArrayList<Coordinate>> sortById(ArrayList<Coordinate> coordinates) {
        HashMap<Integer, ArrayList<Coordinate>> toReturn = new HashMap<>();

        for (Coordinate coordinate : coordinates) {
            int userId = coordinate.getUserId();
            if (toReturn.get(userId) == null) {
                ArrayList<Coordinate> listToBeAdded = new ArrayList<>();
                listToBeAdded.add(coordinate);
                toReturn.put(userId, listToBeAdded);
            } else {
                ArrayList<Coordinate> list = toReturn.get(userId);
                list.add(coordinate);
            }
        }
        return toReturn;
    }

    public static HashMap<String, ArrayList<Coordinate>> sortCoordinatesIntoMap (ArrayList<Coordinate> coordinates) {
        HashMap<String, ArrayList<Coordinate>> coordinateMap = new HashMap<>();

        int routeId = 1;
        Coordinate forgottenCoord = null;
        for (Coordinate currentCoordinate: coordinates) {
            //System.out.println(currentCoordinate.toString());
            int userId = currentCoordinate.getUserId();
            String compositeKey = " " + userId + "," + routeId;
            ArrayList<Coordinate> coordinateList = coordinateMap.get(compositeKey);

            if (coordinateList == null) {
                //first instance
                coordinateList = new ArrayList<>();
                if (forgottenCoord != null) {
                    coordinateList.add(forgottenCoord);
                    forgottenCoord = null;
                }
                coordinateList.add(currentCoordinate);
                coordinateMap.put(compositeKey, coordinateList);
            } else {
                //second instance onward
                Coordinate prevCoordinate = coordinateList.get(coordinateList.size()-1);
                long currentTimestamp = currentCoordinate.getTimestamp().getTime();
                long prevTimestamp = prevCoordinate.getTimestamp().getTime();
                long diff = (currentTimestamp - prevTimestamp)/1000;
                if (diff < 5)  {
                    coordinateList.add(currentCoordinate);
                } else {
                    forgottenCoord = currentCoordinate;
                    routeId++;
                }
            }
        }
        /* for debugging
        int countAfter = 0;
        for (Map.Entry<String, ArrayList<Coordinate>> entry : coordinateMap.entrySet()) {
            String compositeKey = entry.getKey();
            ArrayList<Coordinate> coordinatesList = entry.getValue();
            String[] splitKey = compositeKey.split(",");
            String userId = splitKey[0];
            int routeId2 = Integer.parseInt(splitKey[1]);
            if (routeId2 < 100) {
                System.out.println("Composite Key:" + compositeKey + " Size of list:" + coordinatesList.size());
            }

            countAfter += coordinatesList.size();
            //
        }

        System.out.println("Count before: " + coordinates.size() + " count after:" + countAfter);s
        */
        return coordinateMap;
    }



    public static HashMap<Integer, HashMap<Integer, ArrayList<Coordinate>>> sortCoordinates (ArrayList<Coordinate> coordinates) {
        HashMap<Integer, HashMap<Integer, ArrayList<Coordinate>>> toReturn = new HashMap<>();

        int sessionId = 0;
        for (int i = 0; i < coordinates.size(); i++) {
            Coordinate coord = coordinates.get(i);
            Timestamp timestamp = coord.getTimestamp();
            double lat = coord.getLatitude();
            double lng = coord.getLongitude();

            if (i < coordinates.size() - 1) {
                Coordinate nextCoord = coordinates.get(i + 1);
                Timestamp nextTimestamp = nextCoord.getTimestamp();
                double nextLat = nextCoord.getLatitude();
                double nextLng = nextCoord.getLongitude();

                double distanceInMeters = GpsUtility.calculateDistance(lat, nextLat, lng, nextLng);

                long timeInSeconds = (nextTimestamp.getTime() - timestamp.getTime()) / 1000;

                if (timeInSeconds < 5 && distanceInMeters < 20) {
                    HashMap<Integer, ArrayList<Coordinate>> sessionIdMap = toReturn.get(coord.getUserId());
                    if (sessionIdMap != null) {
                        ArrayList<Coordinate> sessionCoordinates = sessionIdMap.get(sessionId);
                        if (sessionCoordinates == null) {
                            //put filter here
//                            if (coord.getUserId() == 6) {
                            sessionCoordinates = new ArrayList<>();
                            sessionCoordinates.add(coord);
                            sessionIdMap.put(sessionId, sessionCoordinates);
//                            }

                        } else {
                            //put filter here
//                            if (coord.getUserId() == 6) {
                            sessionCoordinates.add(coord);
//                            }
                        }

                    } else {
                        sessionIdMap = new HashMap<>();
                        toReturn.put(coord.getUserId(), sessionIdMap);
                    }
                } else {
                    sessionId++;
                }
            }
        }
        return toReturn;
    }
}
