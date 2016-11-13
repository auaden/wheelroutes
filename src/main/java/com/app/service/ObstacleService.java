package com.app.service;

import com.app.dao.ObstacleDao;
import com.app.domain.Obstacle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by adenau on 20/9/16.
 */
@Service
public class ObstacleService {


    @Autowired
    private ObstacleDao obstacleDao;

    public ArrayList<Obstacle> retrieveAllApproved() {
        return (ArrayList<Obstacle>) obstacleDao.findAll("obstacle");
    }

    public void save(String email,
                     Timestamp timestamp,
                     String description,
                     double lat,
                     double lng,
                     MultipartFile imageFile) {

        byte[] imageBytes= null;
        try {
            imageBytes = imageFile.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Obstacle obstacle = new Obstacle(email, timestamp, description, lat, lng, imageBytes, false);
        obstacleDao.insert(obstacle);
    }

    public ArrayList<Obstacle> findAllUnapproved() {return (ArrayList<Obstacle>) obstacleDao.findAllUnapproved();}

    public byte[] getImage(double lat, double lng) {
        Obstacle obstacle = obstacleDao.find(lat, lng);
        if (obstacle == null) {
            return null;
        } else {
            return obstacle.getImage();
        }
    }

    public void approveObstacle(String email, double lat, double lng) {
        Obstacle prevObs = obstacleDao.find(lat,lng);
        prevObs.setApproved(true);
        obstacleDao.delete(email, lat, lng);
        obstacleDao.insert(prevObs);
    }

    public void deleteObstacle(String email, double lat, double lng) {
        obstacleDao.delete(email, lat, lng);
    }

}
