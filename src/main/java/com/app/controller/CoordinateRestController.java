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
import java.util.ArrayList;
import java.util.List;

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
        coordinateDao.insertRawBatch(filteredCoordinates, coordRawTableName);
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
//                CoordinateRest coordinateRest =
//                        new CoordinateRest(coord.getUserId(),
//                                coord.getTimestamp().toString(),
//                                coord.getLatitude(),
//                                coord.getLongitude(),
//                                coord.getNumSat());
//                coordinateRests.add(coordinateRest);

                JsonObject object = new JsonObject();
                JsonPrimitive userIdElement = new JsonPrimitive(coord.getUserId());
                JsonPrimitive timestampElement = new JsonPrimitive(coord.getTimestamp().toString());
                JsonPrimitive latElement = new JsonPrimitive(coord.getLatitude());
                JsonPrimitive lngElement = new JsonPrimitive(coord.getLongitude());
                JsonPrimitive numSat = new JsonPrimitive(coord.getNumSat());
                object.add("userId", new JsonPrimitive(68));
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

//        System.out.println(jArray.toString());
//        String postUrl = "www.wheelroutes.icitylab.com/coordinate/coordinates";// put in your url
//        Gson gson = new Gson();
//
//        JsonObject object = new JsonObject();
//
//        HttpClient httpClient = HttpClientBuilder.create().build();
//        HttpPost post = new HttpPost(postUrl);
//
//        StringEntity postingString = null;//gson.tojson() converts your pojo to json
//        try {
//            postingString = new StringEntity(gson.toJson(coordinateRests.get(0)));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(postingString.toString());
//
//        post.setEntity(postingString);
//        post.setHeader("Content-type", "application/json");
//        try {
//            HttpResponse response = httpClient.execute(post);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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
