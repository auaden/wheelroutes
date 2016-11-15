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

    @Autowired
    private String axisTempTableName;

    private static final double X_CHANGE_STATIONARY = 0.096;
    private static final double Y_CHANGE_STATIONARY = 0.132;
    private static final double Z_CHANGE_STATIONARY = 0.052;

    private final double LVL4_SLOPE = 0.3; //100 - anything between NORMAL_SLOPE and STEEP_SLOPE is considered normal slope
    private final double LVL3_SLOPE = 0.25; //100 - anything between NORMAL_SLOPE and STEEP_SLOPE is considered normal slope
    private final double LVL2_SLOPE = 0.2; //50 - anything above SLIGHT_SLOPE and below NORMAL_SLOPE is considered slight slope
    private final double LVL1_SLOPE = 0.15; //50 - anything above SLIGHT_SLOPE and below NORMAL_SLOPE is considered slight slope

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
//        ArrayList<Axis> rawAxes = (ArrayList<Axis>) axisDao.findAllByDate(userId, startDate, endDate, axisTempTableName);
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
                axesInMap = stationaryFilter(axesInMap);
                toReturn.add(new AxisTimeFrame(userId, axesInMap, timestamp));
                //System.out.println("user id: " + userId + " ts: " + timestamp + " size: " + axesInMap.size());
            }
        }

        return toReturn;
    }


    private static final double UP_BUMP_VALUE = 3.000; //compares each 0.1s
    private final double SLOPE_VARIANCE = 0.050;

    private int getBumpinessRating(ArrayList<Axis> sortedAxes, int userSensitivity){

        double initialX = 0.0;
        double initialY = 0.0;
        double initialZ = 0.0;

        double firstChangeX = 0.0;
        double firstChangeY = 0.0;
        double firstChangeZ = 0.0;

        double nextX = 0.0;
        double nextY = 0.0;
        double nextZ = 0.0;

        double firstChangeXYZ = 0.0;

        if(sortedAxes != null && sortedAxes.size() > 1){
            Axis initialAxis = sortedAxes.get(0);
            initialX = initialAxis.getxAxis();
            initialY = initialAxis.getyAxis();
            initialZ = initialAxis.getzAxis();

            Axis nextAxis = sortedAxes.get(1);
            nextX = nextAxis.getxAxis();
            nextY = nextAxis.getyAxis();
            nextZ = nextAxis.getzAxis();

            firstChangeX = nextX - initialX;
            firstChangeY = nextY - initialY;
            firstChangeZ = nextZ - initialZ;

            //first variance record
            firstChangeXYZ = Math.pow(firstChangeX,2) + Math.pow(firstChangeY,2) + Math.pow(firstChangeZ,2);
        }

        double slopeAverage = 0.0;

        double totalChangeOfVariance = firstChangeXYZ;
        double changeOfVariance = 0.0;

        for(int x = 2; x < sortedAxes.size(); x++){

            Axis axis = sortedAxes.get(x);

            double xAxis = axis.getxAxis();
            double yAxis = axis.getyAxis();
            double zAxis = axis.getzAxis();

            //change of 1 record jump
            double nextChangeX = xAxis - nextX;
            double nextChangeY = yAxis - nextY;
            double nextChangeZ = zAxis - nextZ;

            //next variance record
            double nextChangeXYZ = Math.pow(nextChangeX,2) + Math.pow(nextChangeY,2) + Math.pow(nextChangeZ,2);

            changeOfVariance = Math.abs(nextChangeXYZ - firstChangeXYZ);
            totalChangeOfVariance += changeOfVariance;
            firstChangeXYZ = nextChangeXYZ;


            //if it's a hard bump show red instantly
            if(changeOfVariance > UP_BUMP_VALUE){
                return 6;
            }

            slopeAverage += Math.abs(Math.sin(initialX));
            initialX = nextX;
            initialY = nextY;
            initialZ = nextZ;

            nextX = xAxis;
            nextY = yAxis;
            nextZ = zAxis;

            //slopeVariance += Math.abs(Math.sin(initialX) - Math.sin(nextX));

        }

        int rating = 0;

        if(totalChangeOfVariance < LVL1_BUMP){
            rating = 0;
        }else if(totalChangeOfVariance >= LVL1_BUMP && totalChangeOfVariance <LVL2_BUMP){
            rating = 1;
        }else if(totalChangeOfVariance >= LVL2_BUMP && totalChangeOfVariance < LVL3_BUMP){
            rating = 2;
        }else if(totalChangeOfVariance >= LVL3_BUMP && totalChangeOfVariance < LVL4_BUMP){
            rating = 3;
        }else if(totalChangeOfVariance >= LVL4_BUMP){
            rating = 4;
        }

        //slopeVariance = slopeVariance/data.size();
        slopeAverage = slopeAverage/sortedAxes.size();

        if(slopeAverage < LVL1_SLOPE){
            rating += 0;
        }else if (slopeAverage >= LVL1_SLOPE && slopeAverage < LVL2_SLOPE){
            rating += 1;
        }else if (slopeAverage >= LVL2_SLOPE && slopeAverage < LVL3_SLOPE){
            rating += 2;
        }else if (slopeAverage >= LVL3_SLOPE && slopeAverage < LVL4_SLOPE){
            rating += 3;
        }else if (slopeAverage >= LVL4_SLOPE){
            rating += 4;
        }

        if(rating >= 4){
            rating = 4;
        }

        //System.out.println("rating:  " + rating);
        return rating;
    }

    private ArrayList<Axis> stationaryFilter(ArrayList<Axis> dataList){

        ArrayList<Axis> toReturn = new ArrayList<>();

        if(dataList.size() > 0){

            Axis initial = dataList.get(0);
            double initialX = initial.getxAxis();
            double initialY = initial.getyAxis();
            double initialZ = initial.getzAxis();


            for(int i = 0; i < dataList.size(); i++){
                Axis current = dataList.get(i);
                int userId = current.getUserId();
                java.sql.Timestamp ts = current.getTimestamp();
                double currentX = current.getxAxis();
                double currentY = current.getyAxis();
                double currentZ = current.getzAxis();

                double diffX = Math.abs(currentX - initialX);
                double diffY = Math.abs(currentY - initialY);
                double diffZ = Math.abs(currentZ - initialZ);

                if(diffX > X_CHANGE_STATIONARY){
                    initialX = currentX;
                }

                if(diffY > Y_CHANGE_STATIONARY){
                    initialY = currentY;
                }

                if(diffZ > Z_CHANGE_STATIONARY){
                    initialZ = currentZ;
                }

                //System.out.println("xAxis: " + initialX + "  yAxis: " + initialY + "  zAxis: " + initialZ);

                toReturn.add(new Axis(userId, ts, initialX, initialY, initialZ));
            }
        }

        return toReturn;

    }


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

    public void deleteData() {
        axisDao.deleteAll(axisTempTableName);
    }


}
