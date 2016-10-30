package com.app.controller;

import com.app.Utility.GpsUtility;
import com.app.dao.CoordinateDao;
import com.app.domain.Coordinate;
import com.app.domain.CoordinateRest;
import com.app.service.CoordinateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.xml.ws.RequestWrapper;
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

    @RequestMapping(value = "/coordinates", method=RequestMethod.GET)
    public List<Coordinate> list() {
        return coordinateDao.findAll(coordProcessedTableName, false);
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
