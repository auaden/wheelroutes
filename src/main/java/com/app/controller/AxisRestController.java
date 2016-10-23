package com.app.controller;

import com.app.Utility.GpsUtility;
import com.app.dao.AxisDao;
import com.app.domain.Axis;
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
    public void save(@RequestBody List<Axis> axes) {
        ArrayList<Axis> test = new ArrayList<>();
        for (Axis axis : axes) {
            long ts = axis.getTimestamp().getTime() - (1000 * 60 * 60 * 8);
            Timestamp newTs = new Timestamp(ts);
            axis.setTimestamp(newTs);
            test.add(axis);
        }
        axisDao.insertBatch(test, axisRawTableName);
    }

    @RequestMapping(value = "/axes", method=RequestMethod.GET)
    public List<Axis> list() {
        return axisDao.findAll(axisProcessedTableName);
    }

    @RequestMapping(value = "/{tableName}", method=RequestMethod.GET)
    public ModelAndView getListFromTable(@PathVariable String tableName)  {
        ModelAndView mv = new ModelAndView("database");
        ArrayList<Axis> data = (ArrayList<Axis>)axisDao.findAll(tableName);
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
