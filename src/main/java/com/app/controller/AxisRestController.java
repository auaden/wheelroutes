package com.app.controller;

import com.app.Utility.GpsUtility;
import com.app.dao.AxisDao;
import com.app.domain.Axis;
import com.app.domain.AxisRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adenau on 18/9/16.
 */
@RestController
@RequestMapping("/axis")
public class AxisRestController {

    @Autowired
    private AxisDao axisDao;

    @Autowired
    private String axisProcessedTableName;

    @Autowired
    private String axisRawTableName;

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
        axisDao.insertBatch(filteredAxes, axisRawTableName);
    }

    @RequestMapping(value = "/axes/{tableName}", method=RequestMethod.GET)
    public List<AxisRest> list(@PathVariable String tableName) {
        ArrayList<Axis> axes = (ArrayList<Axis>) axisDao.findAll(tableName);
        ArrayList<AxisRest> axisRests = new ArrayList<>();

        for (Axis axis : axes) {
            if (axis != null) {
                AxisRest axisRest =
                        new AxisRest(axis.getUserId(),
                                axis.getTimestamp().toString(),
                                axis.getxAxis(),
                                axis.getyAxis(),
                                axis.getzAxis());

                axisRests.add(axisRest);
            }
        }

        return axisRests;
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
    public ModelAndView getListFromTableAndId(@PathVariable(value="userId") int userId,
                                              @PathVariable(value="startDate") String startDate,
                                              @PathVariable(value="endDate") String endDate,
                                              @PathVariable(value="tableName") String tableName)  {
        ModelAndView mv = new ModelAndView("database");
        ArrayList<Axis> data = (ArrayList<Axis>)axisDao.findAllByDate(userId, startDate, endDate, tableName);
        mv.addObject("axisData", data);
        return mv;
    }

}
