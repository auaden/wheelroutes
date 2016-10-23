package com.app.dao;

import com.app.domain.Obstacle;
import com.app.jdbc_exception.InsertFailedException;
import com.app.jdbc_exception.UpdateFailedException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.sql.*;
import java.util.*;

/**
 * Created by adenau on 20/9/16.
 */
@Repository
public class ObstacleDao {
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public List<Obstacle> findAll(String tableName) {
        List<Obstacle> obstacles;
        try {
            obstacles = jdbcTemplate.query(
                    "select * from " + tableName, new RowMapper<Obstacle>() {
                        public Obstacle mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                            Obstacle obstacle = new Obstacle();
                            obstacle.setEmail(resultSet.getString("email"));
                            obstacle.setTimestamp(Timestamp.valueOf(resultSet.getString("timestamp")));
                            obstacle.setDescription(resultSet.getString("description"));
                            obstacle.setLatitude(resultSet.getDouble("latitude"));
                            obstacle.setLongitude(resultSet.getDouble("longitude"));
                            obstacle.setImage(resultSet.getBytes("image"));
                            obstacle.setApproved(resultSet.getBoolean("approved"));
                            return obstacle;
                        }
                    });

        } catch (IllegalArgumentException iae) {
            System.out.println("illegal argument exception");
            iae.printStackTrace();
            return null;
        }
        return obstacles;
    }

    public Obstacle find(double lat, double lng) {
        Map<String, Double> paramMap = new HashMap<>();
        paramMap.put("lat", lat);
        paramMap.put("lng", lng);

        List<Obstacle> result =  namedParameterJdbcTemplate.query(
                "select * from \"obstacle\" where latitude = :lat and longitude = :lng",
                paramMap,
                new RowMapper<Obstacle>() {
                    public Obstacle mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                        Obstacle obstacle = new Obstacle();
                        obstacle.setEmail(resultSet.getString("email"));
                        obstacle.setTimestamp(Timestamp.valueOf(resultSet.getString("timestamp")));
                        obstacle.setDescription(resultSet.getString("description"));
                        obstacle.setLatitude(resultSet.getDouble("latitude"));
                        obstacle.setLongitude(resultSet.getDouble("longitude"));
                        obstacle.setImage(resultSet.getBytes("image"));
                        obstacle.setApproved(resultSet.getBoolean("approved"));
                        return obstacle;
                    }
                });
        if (result.size() == 0) {
            return null;
        } else {
            return result.get(0);
        }
    }

    public void insert(final Obstacle obstacle) {

        int count = jdbcTemplate.update(
                "INSERT INTO \"obstacle\" (email, \"timestamp\", description, latitude, longitude, image, approved) values(?,?,?,?,?,?,?)",
                new PreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setString(1, obstacle.getEmail());
                        preparedStatement.setString(2, obstacle.getTimestamp().toString());
                        preparedStatement.setString(3, obstacle.getDescription());
                        preparedStatement.setDouble(4, obstacle.getLatitude());
                        preparedStatement.setDouble(5, obstacle.getLongitude());
                        preparedStatement.setBytes(6, obstacle.getImage());
                        preparedStatement.setBoolean(7, obstacle.isApproved());
                    }
                }
        );
        if (count !=1 ) throw new InsertFailedException("Cannot insert Account");
    }

}
