package com.app.controller;

import com.app.Utility.StopWatch;
import com.app.domain.*;
import com.app.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by adenau on 10/9/16.
 */
@Controller
@SessionAttributes("authUser")
public class FrontController {
    @Autowired
    private UserService userService;

    @Autowired
    private CoordinateService coordinateService;

    @Autowired
    private AxisService axisService;

    @Autowired
    private ObstacleService obstacleService;

    @RequestMapping(value = "/landing", method = RequestMethod.GET)
    public ModelAndView toLanding() {
        ModelAndView mv = new ModelAndView("landing", "user", new User());
        ArrayList<Obstacle> obstacles = obstacleService.retrieveAllApproved();
        HashMap<Integer, ArrayList<Route>> routes = coordinateService.retrieveViewCoordinates(false);
        mv.addObject("viewRoutes", routes);
        mv.addObject("obstacles", obstacles);
        return mv;
    }

    @RequestMapping(value = "/gridView", method = RequestMethod.GET)
    public ModelAndView toGridView() {
        ModelAndView mv = new ModelAndView("gridView", "user", new User());
        ArrayList<Obstacle> obstacles = obstacleService.retrieveAllApproved();
        HashMap<Integer, ArrayList<Route>> routes = coordinateService.retrieveViewCoordinates(true);
        mv.addObject("viewRoutes", routes);
        mv.addObject("obstacles", obstacles);
        return mv;
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public ModelAndView toAdminPage() {
        ModelAndView mv = new ModelAndView("admin");
        ArrayList<Obstacle> unapprovedObstacles = obstacleService.findAllUnapproved();
        mv.addObject("unapprovedObstacles", unapprovedObstacles);
        return mv;
    }

    //LOGIN-------------------------------------------------------------------------------------------------

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView toLogin() {
        return new ModelAndView("login", "user", new User());
    }

    @RequestMapping(value = "/process-login", method = RequestMethod.POST)
    public ModelAndView login(@ModelAttribute("user") User user) {
        ModelAndView mv = new ModelAndView();
        String errorMsg = userService.login(user);

        if (errorMsg != null) {
            mv.setViewName("landing");
            HashMap<Integer, ArrayList<Route>> routes = coordinateService.retrieveViewCoordinates(false);
            mv.addObject("viewRoutes", routes);
            mv.addObject("errorMsg", errorMsg);
        } else {
            if (user.getEmail().contains("humblebees")) {
                mv.setViewName("redirect:admin.do");
            } else {
                mv.setViewName("redirect:landing.do");
            }
            user = userService.findUser(user.getEmail());
            mv.addObject("authUser", user);
        }
        return mv;
    }

    //REGISTRATION------------------------------------------------------------------------------------------

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView toRegister() {
        return new ModelAndView("register", "user", new User());
    }

    @RequestMapping(value = "/process-registration", method = RequestMethod.POST)
    public ModelAndView register(@ModelAttribute("user") User user) {
        ModelAndView mv = new ModelAndView();
        String errorMsg = userService.register(user);
        //String errorMsg = userService.register(user, sensitivity);
        
        if (errorMsg != null) {
            mv.addObject("errorMsg", errorMsg);
            mv.setViewName("register");
        }  else {
            mv.addObject("authUser", user);
            mv.setViewName("redirect:landing.do");
        }
        return mv;
    }

    @RequestMapping(value = "/process-logout", method = RequestMethod.GET)
    public ModelAndView logout(SessionStatus status) {
        status.setComplete();
        return new ModelAndView("redirect:landing.do", "user", new User());
    }


    //DATA PROCESSING--------------------------------------------------------------------------------------

    @RequestMapping(value = "/process-data", method = RequestMethod.GET)
    public ModelAndView processData() {
        ModelAndView mv = new ModelAndView();

        StopWatch watch = new StopWatch();
        watch.start();
        TreeMap<Integer, TreeMap<String, Integer>> data = coordinateService.retrieveOverallCoordData();
        for (Map.Entry<Integer, TreeMap<String, Integer>> entry : data.entrySet()) {
            int userId = entry.getKey();
            for (Map.Entry<String, Integer> entry2 : entry.getValue().entrySet()) {
                String date = entry2.getKey();
                //System.out.println("USERID " + userId + " DATE " + date);
                HashMap<String, Integer> ratingMap = axisService.retrieveRatingMap(userId, date + " 00:00", date + " 23:59");
                coordinateService.processData(userId, date + " 00:00", date + " 23:59",ratingMap);
            }
        }
        watch.stop();
        System.out.println("Total processing time: " + TimeUnit.MILLISECONDS.toMinutes(watch.getTime()) + " mins");
        mv.setViewName("redirect:landing.do");
        return mv;
    }

    //OBSTACLES--------------------------------------------------------------------------------------------

    @RequestMapping(value = "/process-upload-obstacle", method = RequestMethod.POST)
    public ModelAndView processUploadObstacle(@RequestParam("description") String description,
                                      @RequestParam("email") String email,
                                      @RequestParam("lat") String lat,
                                      @RequestParam("lng") String lng,
                                      @RequestParam("file") MultipartFile file) {
        Date date = new java.util.Date();
        Timestamp currentTs = new Timestamp(date.getTime());
        obstacleService.save(email, currentTs, description, Double.parseDouble(lat), Double.parseDouble(lng), file);
        return new ModelAndView("redirect:landing.do");
    }

    @RequestMapping(value = "/process-approve-obstacle", method = RequestMethod.POST)
    public ModelAndView processApproveObstacle(@RequestParam("email") String email,
                                               @RequestParam("lat") String lat,
                                               @RequestParam("lng") String lng) {
        obstacleService.approveObstacle(email, Double.parseDouble(lat), Double.parseDouble(lng));
        return new ModelAndView("redirect:admin.do");
    }

    @RequestMapping(value = "/process-delete-obstacle", method = RequestMethod.POST)
    public ModelAndView processDeleteObstacle(@RequestParam("email") String email,
                                               @RequestParam("lat") String lat,
                                               @RequestParam("lng") String lng) {
        obstacleService.deleteObstacle(email, Double.parseDouble(lat), Double.parseDouble(lng));
        return new ModelAndView("redirect:admin.do");
    }


    //FILTER------------------------------------------------------------------------------------------------

    @RequestMapping(value = "/routesView", method = RequestMethod.GET)
    public ModelAndView toRoutesView() {
        return new ModelAndView("routesView");
    }

//

    @RequestMapping(value = "/process-filter-routes", method = RequestMethod.POST)
    public ModelAndView processFilterRoutes(@RequestParam("userId") Integer userId,
                                            @RequestParam("startDate") String startDate,
                                            @RequestParam("endDate") String endDate) {

        HashMap<String, Integer> ratingMap = axisService.retrieveRatingMap(userId, startDate, endDate);

        HashMap<String, Route> coordinates = coordinateService.startProcessingForRoutes(userId, startDate, endDate, ratingMap);
        HashMap<String, Integer> dateMap = sortDateInputIntoMap(userId, startDate, endDate);

        ModelAndView mv = new ModelAndView("routesView");
        mv.addObject("viewCoordinates", coordinates);
        mv.addObject("dateMap", dateMap);
        return mv;
    }

    @RequestMapping(value = "/coordinatesView", method = RequestMethod.GET)
    public ModelAndView toCoordinatesView() {
        return new ModelAndView("coordinatesView");
    }

    @RequestMapping(value = "/process-filter-coordinates", method = RequestMethod.POST)
    public ModelAndView processFilterCoordinates(@RequestParam("userId") Integer userId,
                                            @RequestParam("startDate") String startDate,
                                            @RequestParam("endDate") String endDate) {

        HashMap<String, Integer> ratingMap = axisService.retrieveRatingMap(userId, startDate, endDate);
        ArrayList<Coordinate> coordinates = coordinateService.startProcessingForCoordinates(userId, startDate, endDate, ratingMap);
        HashMap<String, Integer> dateMap = sortDateInputIntoMap(userId, startDate, endDate);

        ModelAndView mv = new ModelAndView("coordinatesView");
        mv.addObject("viewCoordinates", coordinates);
        mv.addObject("dateMap", dateMap);
        return mv;
    }

    //DATA ANALYTICS------------------------------------------------------------------------------------------------
    @GetMapping(value = "/data-analytics")
    public ModelAndView toDataAnalyticsPage() {
        ModelAndView mv = new ModelAndView("data-analytics");
        //user ID, date, number of coordinate count
        TreeMap<Integer, TreeMap<String, Integer>> data = coordinateService.retrieveOverallCoordData();

        //coordinateService.getTimeSpent(data);



        mv.addObject("coordDataByIdAndTimestamp", data);
        return mv;
    }

    //VM DATABASE UI------------------------------------------------------------------------------------------------
    @GetMapping(value = "/database")
    public ModelAndView toDatabase() {
        ModelAndView mv = new ModelAndView("database");
        return mv;
    }

    private HashMap<String, Integer> sortDateInputIntoMap (int userId, String startDate, String endDate) {
        HashMap<String, Integer> dateMap = new HashMap<>();
        dateMap.put("userId", userId);

        startDate = startDate.replaceAll(" ", "-");
        String[] startDateArr = startDate.split("-");

        dateMap.put("startYear", Integer.parseInt(startDateArr[0]));
        dateMap.put("startMonth", Integer.parseInt(startDateArr[1]));
        dateMap.put("startDay", Integer.parseInt(startDateArr[2]));

        String[] timeArr = startDateArr[3].split(":");
        int startHour = Integer.parseInt(timeArr[0]);
        int startMinute = Integer.parseInt(timeArr[1]);
        dateMap.put("startHour", startHour);
        dateMap.put("startMinute", startMinute);

        endDate = endDate.replaceAll(" ", "-");
        String[] endDateArr = endDate.split("-");

        dateMap.put("endYear", Integer.parseInt(endDateArr[0]));
        dateMap.put("endMonth", Integer.parseInt(endDateArr[1]));
        dateMap.put("endDay", Integer.parseInt(endDateArr[2]));

        timeArr = endDateArr[3].split(":");
        int endHour = Integer.parseInt(timeArr[0]);
        int endMinute = Integer.parseInt(timeArr[1]);
        dateMap.put("endHour", endHour);
        dateMap.put("endMinute", endMinute);
        return dateMap;
    }


    @RequestMapping(value = "/process-feedback", method = RequestMethod.POST)
    public ModelAndView processFeedback(@ModelAttribute("authUser") User user,
                                        @RequestParam("optradio") String feedback) {
        System.out.println("feedback value = " + feedback);
        if (feedback.equals("yes")){
            return new ModelAndView("redirect:landing.do");
        }
        int feedbackInt = 0;
        if (feedback.equals("more")) {
            feedbackInt = 1;
        }
        userService.modifySensitivity(user, feedbackInt);
        return new ModelAndView("redirect:landing.do");
    }
}
