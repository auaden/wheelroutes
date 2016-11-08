package com.app.service;

import com.app.Utility.AccelUtility;
import com.app.dao.AxisDao;
import com.app.domain.Axis;
import com.app.domain.AxisTimeFrame;
import java.sql.Timestamp;
import java.util.*;

//import com.sun.tools.corba.se.idl.constExpr.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by adenau on 6/9/16.
 */
@Service
public class AxisService {

    @Autowired
    private AxisDao axisDao;

    @Autowired
    private String axisProcessedTableName;

    @Autowired
    private String axisRawTableName;

    private final double LVL6_SLOPE = 0.4; //150 - anything above is steep
    private final double LVL5_SLOPE = 0.35; //150 - anything above is steep
    private final double LVL4_SLOPE = 0.3; //100 - anything between NORMAL_SLOPE and STEEP_SLOPE is considered normal slope
    private final double LVL3_SLOPE = 0.25; //100 - anything between NORMAL_SLOPE and STEEP_SLOPE is considered normal slope
    private final double LVL2_SLOPE = 0.2; //50 - anything above SLIGHT_SLOPE and below NORMAL_SLOPE is considered slight slope
    private final double LVL1_SLOPE = 0.15; //50 - anything above SLIGHT_SLOPE and below NORMAL_SLOPE is considered slight slope

    private final double LVL6_BUMP = 1.08; //500
    private final double LVL5_BUMP = 1.07; //300
    private final double LVL4_BUMP = 1.06; //100
    private final double LVL3_BUMP = 1.05; //500
    private final double LVL2_BUMP = 1.04; //300
    private final double LVL1_BUMP = 1.03; //100

    private final int numSecondsCompare = 2; //number of seconds to calculate bumpiness, only an estimate
    private int userSensitivity = 0;

    //retrieve rating map
    public HashMap<String, Integer> retrieveRatingMap() {
        ArrayList<Axis> rawAxes = (ArrayList<Axis>) axisDao.findAll(axisRawTableName);
        ArrayList<AxisTimeFrame> axisTimeFrames = (ArrayList<AxisTimeFrame>) retrieveSortedAxisTimeFrame(rawAxes);
        HashMap<String, Integer> toReturn = loadRatingIntoMap(axisTimeFrames);
        return toReturn;
    }

    public HashMap<String, Integer> retrieveRatingMap(int userId, String startDate, String endDate) {
        ArrayList<Axis> rawAxes = (ArrayList<Axis>) axisDao.findAllByDate(userId, startDate, endDate, axisRawTableName);
        ArrayList<AxisTimeFrame> axisTimeFrames = (ArrayList<AxisTimeFrame>) retrieveSortedAxisTimeFrame(rawAxes);
        //timestamp, rating
        HashMap<String, Integer> toReturn = loadRatingIntoMap(axisTimeFrames);
        return toReturn;
    }

    private List<AxisTimeFrame> retrieveSortedAxisTimeFrame(ArrayList<Axis> rawAxes){
        //sorts list of Axis readings by time, put in ArrayList
        TreeMap<Integer, TreeMap<Timestamp, ArrayList<Axis>>> userMap = new TreeMap<>();

        ArrayList<Axis> axes = AccelUtility.removeNoFix(rawAxes);

        for (Axis axis : axes) {
            Timestamp timestamp = axis.getTimestamp();
            int userId = axis.getUserId();
            TreeMap<Timestamp, ArrayList<Axis>> sortedMap = userMap.get(userId);
            if (sortedMap == null) {
                TreeMap<Timestamp, ArrayList<Axis>> newSortedMap = new TreeMap<>();
                ArrayList<Axis> newList = new ArrayList<>();
                newList.add(axis);
                newSortedMap.put(timestamp, newList);
                userMap.put(userId, newSortedMap);
            } else {
                ArrayList<Axis> newAxes = sortedMap.get(timestamp);
                if (newAxes == null) {
                    ArrayList<Axis> newList = new ArrayList<>();
                    newList.add(axis);
                    sortedMap.put(timestamp, newList);
                } else {
                    newAxes.add(axis);
                }
            }
        }

        List<AxisTimeFrame> toReturn = new ArrayList<>();
        for (Map.Entry<Integer, TreeMap<Timestamp, ArrayList<Axis>>> entry : userMap.entrySet()) {
            int userId = entry.getKey();
            TreeMap<Timestamp, ArrayList<Axis>> sortedMap = entry.getValue();
            for (Map.Entry<Timestamp, ArrayList<Axis>> entry2 : sortedMap.entrySet()) {
                Timestamp timestamp = entry2.getKey();
                ArrayList<Axis> axesInMap = entry2.getValue();
                toReturn.add(new AxisTimeFrame(userId, axesInMap, timestamp));
                //System.out.println("user id: " + userId + " ts: " + timestamp + " size: " + axesInMap.size());
            }
        }

        return toReturn;
    }

    //calculate tilt
    //flat ground slope
    public int getSlopeRating(ArrayList<Axis> sortedAxes, int userSensitivity){
        double maxAbsValue = 0;
        //double maxRealValue = 0;
        //Timestamp time = null;

        for(Axis axis:sortedAxes){
            double currentAbsValue = Math.abs(Math.sin(axis.getxAxis()));
            if (currentAbsValue>maxAbsValue){
                maxAbsValue = currentAbsValue;
            }
        }

        if(maxAbsValue <= LVL1_SLOPE){
            return 0;
        }else if(maxAbsValue < LVL2_SLOPE){
            return 1 + userSensitivity;
        }else if(maxAbsValue < LVL3_SLOPE){
            return 2 + userSensitivity;
        }else if(maxAbsValue < LVL4_SLOPE){
            return 3 + userSensitivity;
        }else if(maxAbsValue < LVL5_SLOPE){
            return 4 + userSensitivity;
        }else if(maxAbsValue < LVL6_SLOPE){
            return 5 + userSensitivity;
        }else {
            return 6 + userSensitivity;
        }
    }

    //calculate bumpiness
    //try 2 logic

    private static final double UP_BUMP_VALUE = 3.000; //compares each 0.1s
    private final double SLOPE_VARIANCE = 0.050;
    public int getBumpinessRating(ArrayList<Axis> sortedAxes, int userSensitivity){
        double totalDiff = 0;
        for(Axis axis:sortedAxes){
            double xyz = Math.sqrt(Math.pow(axis.getxAxis(),2)+Math.pow(axis.getyAxis(),2)+Math.pow(axis.getxAxis(),2)); //pythagorean theorem in 3D
            totalDiff += xyz;
        }

        totalDiff = (totalDiff / sortedAxes.size()) + (userSensitivity / 100); //convert userSensitivity by 100 to match variability

        if(totalDiff <= LVL1_BUMP){
            return 0;
        } else if(totalDiff <= LVL2_BUMP){
            return 1;
        } else if(totalDiff <= LVL3_BUMP){
            return 2;
        } else if(totalDiff <= LVL4_BUMP){
            return 3;
        } else if(totalDiff <= LVL5_BUMP){
            return 4;
        } else if(totalDiff <= LVL6_BUMP){
            return 5;
        } else {
            return 6;
        }

//
//        double initialX = 0.0;
//        double initialY = 0.0;
//        double initialZ = 0.0;
//
//        double nextX = 0.0;
//        double nextY = 0.0;
//        double nextZ = 0.0;
//
//        if(sortedAxes != null && sortedAxes.size() > 0){
//            Axis initialAxis = sortedAxes.get(0);
//            initialX = initialAxis.getxAxis();
//            initialY = initialAxis.getyAxis();
//            initialZ = initialAxis.getzAxis();
//
//            Axis nextAxis = sortedAxes.get(1);
//            nextX = nextAxis.getxAxis();
//            nextY = nextAxis.getyAxis();
//            nextZ = nextAxis.getzAxis();
//        }
//
//        double totalChange = 0.0;
//        double slopeAverage = 0.0;
//        double slopeVariance = 0.0;
//        double changeOfVariance = 0.0;
//        boolean noChangeForVariance = false;
//
//
//        for(int x = 2; x < sortedAxes.size(); x++){
//
//            Axis axis = sortedAxes.get(x);
//
//            double xAxis = axis.getxAxis();
//            double yAxis = axis.getyAxis();
//            double zAxis = axis.getzAxis();
//
//            //change of 2 record jump
//            double changeX = xAxis - initialX;
//            double changeY = yAxis - initialY;
//            double changeZ = zAxis - initialZ;
//
//            //change of 1 record jump
//            double nextChangeX = xAxis - nextX;
//            double nextChangeY = yAxis - nextY;
//            double nextChangeZ = zAxis - nextZ;
//
//            double changeXYZ = Math.pow(changeX,2) + Math.pow(changeY,2) + Math.pow(changeZ,2);
//            double nextChangeXYZ = Math.pow(nextChangeX,2) + Math.pow(nextChangeY,2) + Math.pow(nextChangeZ,2);
//
////            //if it's a hard bump show red instantly
////            if(nextChangeXYZ > UP_BUMP_VALUE){
////                return 6;
////            }
//
//            initialX = nextX;
//            initialY = nextY;
//            initialZ = nextZ;
//
//            nextX = xAxis;
//            nextY = yAxis;
//            nextZ = zAxis;
//
//            slopeVariance += Math.abs(Math.sin(initialX) - Math.sin(nextX));
//            slopeAverage += Math.abs(Math.sin(initialX));
//            totalChange += nextChangeXYZ;
//        }
//
//        int rating = 1;
//
//        if(totalChange < LVL1_BUMP){
//            return 0;
//        }else if(totalChange >= LVL1_BUMP && totalChange <LVL2_BUMP){
//            return 1;
//        }else if(totalChange >= LVL2_BUMP && totalChange < LVL3_BUMP){
//            return 2;
//        }else if(totalChange >= LVL3_BUMP && totalChange < LVL4_BUMP){
//            return 3;
//        }else if(totalChange >= LVL4_BUMP && totalChange < LVL5_BUMP){
//            return 4;
//        }else if(totalChange >= LVL5_BUMP && totalChange < LVL6_BUMP){
//            return 5;
//        }else if(totalChange >= LVL6_BUMP){
//            return 6;
//        }
//
//        slopeVariance = slopeVariance/sortedAxes.size();
//        slopeAverage = slopeAverage/sortedAxes.size();
//
//        if(slopeVariance > SLOPE_VARIANCE){
//            //if the slopeAverage is affected by the bumpy path, the slopeAverage is not useful for measuring slope
//            rating = rating;
//        } else if(slopeAverage < LVL1_SLOPE){
//            return 0;
//        }else if (slopeAverage >= LVL1_SLOPE && slopeAverage < LVL2_SLOPE){
//            return 1;
//        }else if (slopeAverage >= LVL2_SLOPE && slopeAverage < LVL3_SLOPE){
//            return 2;
//        }else if (slopeAverage >= LVL3_SLOPE && slopeAverage < LVL4_SLOPE){
//            return 3;
//        }else if (slopeAverage >= LVL4_SLOPE && slopeAverage < LVL5_SLOPE){
//            return 4;
//        }else if(slopeAverage >= LVL5_SLOPE){
//            return 5;
//        }else if(slopeAverage >= LVL6_SLOPE){
//            return 6;
//        }
//
//
//        return 1;


    }

    //based on calculated tilt and bumpiness, come up with a calculation for coloring
//    public List<AxisTimeFrame> calculateAccessibility(int userSensitivity){
//        //initiate all variables
//        //what variables do i have?
////        List<AxisTimeFrame> sortedAxisTimeFrames = retrieveSortedAxisTimeFrame("mtaxis");
////
////        for (AxisTimeFrame axisTimeFrame:sortedAxisTimeFrames) {
////            int bRate = getBumpinessRating((ArrayList<Axis>)axisTimeFrame.getAxes(), userSensitivity);
////            int sRate = getSlopeRating((ArrayList<Axis>)axisTimeFrame.getAxes(), userSensitivity);
////            axisTimeFrame.setAccessibilityRate(bRate + sRate);
////        }
////
////        return sortedAxisTimeFrames;
//
//    }

    private HashMap<String, Integer> loadRatingIntoMap (ArrayList<AxisTimeFrame> axisTimeFrames) {
        HashMap<String, Integer> toReturn = new HashMap<>();
        for (AxisTimeFrame axisTimeFrame : axisTimeFrames) {
            int userId = axisTimeFrame.getUserId();
            Timestamp timestamp = axisTimeFrame.getStartTime();
            ArrayList<Axis> axes = (ArrayList<Axis>) axisTimeFrame.getAxes();
            int bumpinessRating = getBumpinessRating(axes, 75);
            toReturn.put(userId + "," + timestamp, bumpinessRating);
        }
        return toReturn;
    }


}
