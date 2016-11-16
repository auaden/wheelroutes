package com.app.controller;

import com.app.Utility.GpsUtility;
import com.app.dao.CoordinateDao;
import com.app.domain.Coordinate;
import com.app.domain.CoordinateRest;
import com.app.service.CoordinateService;
import com.google.gson.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.xml.ws.RequestWrapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by adenau on 12/9/16.
 */

@RestController
@RequestMapping("/coordinate")
public class CoordinateRestController {

    @Autowired
    private CoordinateDao coordinateDao;

    @Autowired
    private CoordinateService coordinateService;

    @Autowired
    private String coordRawTableName;

    @Autowired
    private String coordProcessedTableName;

    @Autowired
    private String coordTempTableName;

    @RequestMapping(value="/{tableName}", method=RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteTable(@PathVariable("tableName") String tableName) {
        coordinateDao.deleteAll(tableName);
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }

    @RequestMapping(value = "/coordinates", method= RequestMethod.POST)
    public void save(@RequestBody List<CoordinateRest> coordinates) {
        System.out.println("RESTED.. SAVE");
        ArrayList<Coordinate> filteredCoordinates = new ArrayList<>();
        for (CoordinateRest coordinateRest : coordinates) {
            System.out.println("timestamp: " + coordinateRest.getTimestamp());
            if (!coordinateRest.getTimestamp().equals("0")) {
                int userId = coordinateRest.getUserId();
                Timestamp ts = Timestamp.valueOf(coordinateRest.getTimestamp());
                double lat = coordinateRest.getLatitude();
                double lng = coordinateRest.getLongitude();
                int numSat = coordinateRest.getNumSat();
                filteredCoordinates.add(new Coordinate(userId, ts, lat, lng, numSat));
            }
        }
        for (Coordinate c : filteredCoordinates) {
            System.out.println(c.toString());
        }
        coordinateDao.insertRawBatch(filteredCoordinates, coordTempTableName);
    }

    @RequestMapping(value = "/process/coordinates", method= RequestMethod.POST)
    public void saveProcessed(@RequestBody List<CoordinateRest> coordinates) {
        ArrayList<Coordinate> filteredCoordinates = new ArrayList<>();
        for (CoordinateRest coordinateRest : coordinates) {
            System.out.println("timestamp: " + coordinateRest.getTimestamp());
            if (!coordinateRest.getTimestamp().equals("0")) {
                int userId = coordinateRest.getUserId();
                Timestamp ts = Timestamp.valueOf(coordinateRest.getTimestamp());
                double lat = coordinateRest.getLatitude();
                double lng = coordinateRest.getLongitude();
                int numSat = coordinateRest.getNumSat();
                int rating = coordinateRest.getRating();
                filteredCoordinates.add(new Coordinate(userId, ts, lat, lng, numSat, rating));
            }
        }
        coordinateDao.insertBatch(filteredCoordinates, coordProcessedTableName);
    }

    @RequestMapping(value = "/coordinates/{tableName}", method=RequestMethod.GET)
    public List<CoordinateRest> list(@PathVariable String tableName) {
        ArrayList<Coordinate> coordinates = (ArrayList<Coordinate>) coordinateDao.findAll(tableName, true);
        System.out.println("Coordinates size retrieved : " + coordinates.size());
        ArrayList<CoordinateRest> coordinateRests = new ArrayList<>();

        for (Coordinate coord : coordinates) {
            if (coord != null) {
                CoordinateRest coordinateRest =
                        new CoordinateRest(coord.getUserId(),
                                coord.getTimestamp().toString(),
                                coord.getLatitude(),
                                coord.getLongitude(),
                                coord.getNumSat());
                coordinateRests.add(coordinateRest);
            }
        }


        return coordinateRests;
    }


    @RequestMapping(value = "/send/coordinates", method=RequestMethod.GET)
    public List<CoordinateRest> sendProcessedJSON() {

        TreeMap<Integer, TreeMap<String, Integer>> data = coordinateService.retrieveOverallCoordData("processedcoord");

        for (Map.Entry<Integer, TreeMap<String, Integer>> entry : data.entrySet()) {
            int userId = entry.getKey();
            for (Map.Entry<String, Integer> entry2 : entry.getValue().entrySet()) {
                String date = entry2.getKey();
                System.out.println("user is " + userId + " date is " + date);
                ArrayList<Coordinate> coordinates = coordinateService.retrieveProcessedData(userId, date + " 00:00", date + " 23:59");
                int counter = 0;
                System.out.println("size retrieved " + coordinates.size());
                JsonArray jArray = new JsonArray();

                int batchCounter = 0;
                for (Coordinate coord : coordinates) {
                    if (coord != null) {
                        JsonObject object = new JsonObject();
                        JsonPrimitive userIdElement = new JsonPrimitive(coord.getUserId());
                        JsonPrimitive timestampElement = new JsonPrimitive(coord.getTimestamp().toString());
                        JsonPrimitive latElement = new JsonPrimitive(coord.getLatitude());
                        JsonPrimitive lngElement = new JsonPrimitive(coord.getLongitude());
                        JsonPrimitive numSat = new JsonPrimitive(coord.getNumSat());
                        JsonPrimitive rating = new JsonPrimitive(coord.getRating());
                        object.add("userId", userIdElement);
                        object.add("timestamp", timestampElement);
                        object.add("latitude", latElement);
                        object.add("longitude", lngElement);
                        object.add("numSat", numSat);
                        object.add("rating", rating);
                        jArray.add(object);

                    } else {
                        counter++;
                    }

                    if (batchCounter == 1000) {
                        //System.out.println(jArray.toString());

                        String postUrl = "http://wheelroutes.icitylab.com/rest/coordinate/process/coordinates";// put in your url
                        Gson gson = new Gson();
                        HttpClient httpClient = HttpClientBuilder.create().build();
                        HttpPost post = new HttpPost(postUrl);

                        StringEntity postingString = null;//gson.tojson() converts your pojo to json
                        try {
                            postingString = new StringEntity(gson.toJson(jArray));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        post.setEntity(postingString);
                        post.setHeader("Content-type", "application/json");
                        try {
                            HttpResponse response = httpClient.execute(post);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        jArray = new JsonArray();
                        batchCounter = 0;
                    }

                    batchCounter++;
                }

                String postUrl = "http://wheelroutes.icitylab.com/rest/coordinate/process/coordinates";// put in your url
                Gson gson = new Gson();
                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpPost post = new HttpPost(postUrl);

                StringEntity postingString = null;//gson.tojson() converts your pojo to json
                try {
                    postingString = new StringEntity(gson.toJson(jArray));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                post.setEntity(postingString);
                post.setHeader("Content-type", "application/json");
                try {
                    HttpResponse response = httpClient.execute(post);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }






    @RequestMapping(value = "/coordinates/{userId}/{tableName}", method=RequestMethod.GET)
    public List<CoordinateRest> list(@PathVariable(value="userId") int userId,
                                     @PathVariable(value="tableName") String tableName) {
        ArrayList<Coordinate> coordinates = (ArrayList<Coordinate>) coordinateDao.findById(userId, tableName, true);

        System.out.println("Coordinates size retrieved : " + coordinates.size());
        ArrayList<CoordinateRest> coordinateRests = new ArrayList<>();

        int counter = 0;
        JsonArray jArray = new JsonArray();

        int batchCounter = 0;
        for (Coordinate coord : coordinates) {
            if (coord != null) {
                JsonObject object = new JsonObject();
                JsonPrimitive userIdElement = new JsonPrimitive(coord.getUserId());
                JsonPrimitive timestampElement = new JsonPrimitive(coord.getTimestamp().toString());
                JsonPrimitive latElement = new JsonPrimitive(coord.getLatitude());
                JsonPrimitive lngElement = new JsonPrimitive(coord.getLongitude());
                JsonPrimitive numSat = new JsonPrimitive(coord.getNumSat());
                object.add("userId", userIdElement);
                object.add("timestamp", timestampElement);
                object.add("latitude", latElement);
                object.add("longitude", lngElement);
                object.add("numSat", numSat);
                jArray.add(object);

            } else {
                counter++;
            }

            if (batchCounter == 1000) {
                //System.out.println(jArray.toString());

                String postUrl = "http://wheelroutes.icitylab.com/rest/coordinate/coordinates";// put in your url
                Gson gson = new Gson();
                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpPost post = new HttpPost(postUrl);

                StringEntity postingString = null;//gson.tojson() converts your pojo to json
                try {
                    postingString = new StringEntity(gson.toJson(jArray));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                post.setEntity(postingString);
                post.setHeader("Content-type", "application/json");
                try {
                    HttpResponse response = httpClient.execute(post);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                jArray = new JsonArray();
                batchCounter = 0;
            }

            batchCounter++;
        }

        String postUrl = "http://wheelroutes.icitylab.com/rest/coordinate/coordinates";// put in your url
        Gson gson = new Gson();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(postUrl);

        StringEntity postingString = null;//gson.tojson() converts your pojo to json
        try {
            postingString = new StringEntity(gson.toJson(jArray));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        try {
            HttpResponse response = httpClient.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("number of nulls : " + counter);
        System.out.println("number going in : " + coordinateRests.size());
        return null;
    }

    @RequestMapping(value = "/{tableName}", method=RequestMethod.GET)
    public ModelAndView getListFromTable(@PathVariable String tableName)  {
        ModelAndView mv = new ModelAndView("database");
        boolean isRaw;
        if (tableName.equals(coordProcessedTableName)) {
            isRaw = false;
        } else {
            isRaw = true;
        }
        ArrayList<Coordinate> data = (ArrayList<Coordinate>)coordinateDao.findAll(tableName, isRaw);
        data = GpsUtility.removeDuplicatesAndNoFix(data);
        mv.addObject("data", data);
        return mv;
    }

    @RequestMapping(value = "/{userId}/{tableName}", method=RequestMethod.GET)
    public ModelAndView getListFromTableAndId(@PathVariable(value="userId") int userId,
                                                   @PathVariable(value="tableName") String tableName) {
        ModelAndView mv = new ModelAndView("database");
        boolean isRaw;
        if (tableName.equals(coordProcessedTableName)) {
            isRaw = false;
        } else {
            isRaw = true;
        }
        ArrayList<Coordinate> data = (ArrayList<Coordinate>) coordinateDao.findById(userId, tableName, isRaw);
        data = GpsUtility.removeDuplicatesAndNoFix(data);
        mv.addObject("data", data);
        return mv;
    }

    @RequestMapping(value = "/{userId}/{startDate}/{endDate}/{tableName}", method=RequestMethod.GET)
    public ModelAndView getListFromTableAndId(@PathVariable(value="userId") int userId,
                                                   @PathVariable(value="startDate") String startDate,
                                                   @PathVariable(value="endDate") String endDate,
                                                   @PathVariable(value="tableName") String tableName)  {
        ModelAndView mv = new ModelAndView("database");
        boolean isRaw;
        if (tableName.equals(coordProcessedTableName)) {
            isRaw = false;
        } else {
            isRaw = true;
        }
        ArrayList<Coordinate> data = (ArrayList<Coordinate>)coordinateDao.findByDate(userId, startDate, endDate, tableName, isRaw);
        data = GpsUtility.removeDuplicatesAndNoFix(data);
        mv.addObject("data", data);
        return mv;
    }
}
