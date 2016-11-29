package com.app.controller;

import com.app.Utility.GpsUtility;
import com.app.dao.AxisDao;
import com.app.domain.Axis;
import com.app.domain.AxisRest;
import com.app.service.CoordinateService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by adenau on 18/9/16.
 */
@RestController
@RequestMapping("/axis")
public class AxisRestController {

    @Autowired
    private AxisDao axisDao;

    @Autowired
    private CoordinateService coordinateService;

    @Autowired
    private String axisRawTableName;

    @Autowired
    private String axisTempTableName;


    @RequestMapping(value = "/axes", method= RequestMethod.POST)
    public void save(@RequestBody List<AxisRest> axes) {
        ArrayList<Axis> filteredAxes = new ArrayList<>();
        for (AxisRest axisRest : axes) {
            if (!axisRest.getTimestamp().equals("0")) {
                int userId = axisRest.getUserId();
                Timestamp ts = Timestamp.valueOf(axisRest.getTimestamp());
                double x = axisRest.getxAxis();
                double y = axisRest.getyAxis();
                double z = axisRest.getzAxis();
                filteredAxes.add(new Axis(userId, ts, x, y, z));
            }
        }
        axisDao.insertBatch(filteredAxes, axisTempTableName);
    }

    @RequestMapping(value = "/axes/migrate/migrateAxis", method=RequestMethod.GET)
    public List<AxisRest> list() {

        TreeMap<Integer, TreeMap<String, Integer>> data = coordinateService.retrieveOverallCoordData(axisRawTableName);
        for (Map.Entry<Integer, TreeMap<String, Integer>> entry : data.entrySet()) {
            int userId = entry.getKey();
            for (Map.Entry<String, Integer> entry2 : entry.getValue().entrySet()) {
                String date = entry2.getKey();
                String startDate = date + "00:00";
                String endDate = date + "23:59";

                ArrayList<Axis> axes = (ArrayList<Axis>) axisDao.findAllByDate(userId, startDate, endDate, axisRawTableName);

                JsonArray jArray = new JsonArray();
                int batchCounter = 0;
                for (Axis axis : axes) {
                    if (axis != null) {
                        JsonObject object = new JsonObject();
                        JsonPrimitive userIdElement = new JsonPrimitive(axis.getUserId());
                        JsonPrimitive timestampElement = new JsonPrimitive(axis.getTimestamp().toString());
                        JsonPrimitive xElement = new JsonPrimitive(axis.getxAxis());
                        JsonPrimitive yElement = new JsonPrimitive(axis.getyAxis());
                        JsonPrimitive zElement = new JsonPrimitive(axis.getzAxis());
                        object.add("userId", userIdElement);
                        object.add("timestamp", timestampElement);
                        object.add("xAxis", xElement);
                        object.add("yAxis", yElement);
                        object.add("zAxis", zElement);
                        jArray.add(object);
                    }

                    if (batchCounter == 1000) {
                        //System.out.println(jArray.toString());
                        String postUrl = "http://wheelroutes.icitylab.com/rest/axis/axes";// put in your url
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

                String postUrl = "http://wheelroutes.icitylab.com/rest/axis/axes";// put in your url
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

    @RequestMapping(value = "/{tableName}", method=RequestMethod.GET)
    public ModelAndView getListFromTable(@PathVariable String tableName)  {
        ModelAndView mv = new ModelAndView("database");
        ArrayList<Axis> data = (ArrayList<Axis>)axisDao.findAll(tableName);
        mv.addObject("axisData", data);
        return mv;
    }

    @RequestMapping(value = "/{userId}/{tableName}", method=RequestMethod.GET)
    public ModelAndView getListFromTableAndId(@PathVariable(value="userId") int userId,
                                              @PathVariable(value="tableName") String tableName)  {
        ModelAndView mv = new ModelAndView("database");
        ArrayList<Axis> data = (ArrayList<Axis>) axisDao.findAllById(userId, tableName);
        mv.addObject("axisData", data);
        return mv;
    }

    @RequestMapping(value = "/{userId}/{startDate}/{endDate}/{tableName}", method=RequestMethod.GET)
    public List<Axis> getListFromTableAndId(@PathVariable(value="userId") int userId,
                                              @PathVariable(value="startDate") String startDate,
                                              @PathVariable(value="endDate") String endDate,
                                              @PathVariable(value="tableName") String tableName)  {

        //ArrayList<Axis> data = (ArrayList<Axis>)axisDao.findAllByDate(userId, startDate, endDate, tableName);

        return axisDao.findAllByDate(userId, startDate, endDate, tableName);
    }

}
