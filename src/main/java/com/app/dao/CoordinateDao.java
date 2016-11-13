package com.app.dao;

import com.app.domain.Coordinate;
import com.app.jdbc_exception.UpdateFailedException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by adenau on 18/9/16.
 */
public class CoordinateDao {
    private JdbcTemplate jdbcTemplate;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    // for processedcoord table <with rating>
    public void insertBatch(final List<Coordinate> coordinates, String tableName) {
        String query = "insert into " + tableName + "(\"userId\", \"timestamp\", latitude, longitude, \"numSatellite\", rating) values(?,?,?,?,?,?)";
        int[] counts = jdbcTemplate.batchUpdate(
                query,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        Coordinate coordinate = coordinates.get(i);
                        preparedStatement.setInt(1, coordinate.getUserId());
                        preparedStatement.setString(2, coordinate.getTimestamp().toString());
                        preparedStatement.setDouble(3, coordinate.getLatitude());
                        preparedStatement.setDouble(4, coordinate.getLongitude());
                        preparedStatement.setInt(5, coordinate.getNumSat());
                        preparedStatement.setInt(6, coordinate.getRating());
                    }
                    public int getBatchSize() {
                        return coordinates.size();
                    }
                }
        );
        int i = 0;
        for (int count:counts) {
            if (count == 0) throw new UpdateFailedException("Row not updated : " + i);
            i++;
        }
    }

    public List<Coordinate> findAll(String tableName, boolean isRawData) {
        String queryString = "select * from " + tableName + " order by \"userId\", timestamp";
        System.out.println("querying... " + queryString);
        return isRawDecider(isRawData, queryString);
    }

    public List<Coordinate> findById(int userId, String tableName, boolean isRawData) {
        String queryString = "select * from " + tableName + " where \"userId\" = " + userId +  " order by timestamp";
        System.out.println("querying... " + queryString);
        return isRawDecider(isRawData, queryString);
    }

    public List<Coordinate> findByDate(int userId,
                                       String startDateTime,
                                       String endDateTime,
                                       String tableName,
                                       boolean isRawData) {
        String startDate = startDateTime.replace("T", " ");
        String endDate = endDateTime.replace("T", " ");
        String queryString = "select * from " + tableName
                + " where \"userId\" = " + userId
                + " and timestamp >= \'"
                + startDate+ "\'"
                + " and timestamp<= \'"
                + endDate+ "\' order by timestamp";
        System.out.println("querying... " + queryString);
        return isRawDecider(isRawData, queryString);
    }



    public TreeMap<Integer, TreeMap<String, Integer>> findOverallDataCollected(String tableName) {
        final TreeMap<Integer, TreeMap<String, Integer>> toReturn = new TreeMap<>();
        String query = "select \"userId\", timestamp::date, count(\"userId\")" +
                " from " + tableName +
                " where timestamp != '0'" +
                " group by timestamp::date, \"userId\" order by \"userId\", timestamp asc";

        try {
            jdbcTemplate.query(query, new ResultSetExtractor<Object>() {
                public Object extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                    while (resultSet.next()) {

                        int userId = resultSet.getInt("userId");
                        String date = resultSet.getString("timestamp").substring(0,10);
                        int count = resultSet.getInt("count");

                        if (toReturn.get(userId) == null) {
                            toReturn.put(userId, new TreeMap<String, Integer>());
                            toReturn.get(userId).put(date, count);
                        } else {
                            toReturn.get(userId).put(date, count);
                        }
                    }
                    return null;
                }
            });
        } catch(IllegalArgumentException iae) {
            System.out.println("illegal argument exception");
            iae.printStackTrace();
            return null;
        }
        return toReturn;
    }

    public void deleteAll(String tableName) {
        int count = jdbcTemplate.update("delete from " + tableName);
    }

    //for raw data
    public void insertRawBatch(final List<Coordinate> coordinates, String tableName) {
        String query = "insert into " + tableName + "(\"userId\", \"timestamp\", latitude, longitude, \"numSatellite\") values(?,?,?,?,?)";
        int[] counts = jdbcTemplate.batchUpdate(
                query,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        Coordinate coordinate = coordinates.get(i);
                        preparedStatement.setInt(1, coordinate.getUserId());
                        preparedStatement.setString(2, coordinate.getTimestamp().toString());
                        preparedStatement.setDouble(3, coordinate.getLatitude());
                        preparedStatement.setDouble(4, coordinate.getLongitude());
                        preparedStatement.setInt(5, coordinate.getNumSat());
                    }
                    public int getBatchSize() {
                        return coordinates.size();
                    }
                }
        );
        int i = 0;
        for (int count:counts) {
            if (count == 0) throw new UpdateFailedException("Row not updated : " + i);
            i++;
        }
    }

    private List<Coordinate> executeQueryForRawList(String query) {
        List<Coordinate> coordinates;
        try {
            coordinates = jdbcTemplate.query(
                    query, new RowMapper<Coordinate>() {
                        public Coordinate mapRow(ResultSet resultSet, int i) throws SQLException {
                            int userId = resultSet.getInt("userId");
                            double lat = resultSet.getDouble("latitude");
                            double lng = resultSet.getDouble("longitude");
                            int numSat = resultSet.getInt("numSatellite");
                            //if (resultSet.getString("timestamp").trim().equals("0") || lat == 0 || lng == 0 || numSat < 6) {
                            if (resultSet.getString("timestamp").trim().equals("0")) {
                                return null;
                            } else {
                                Timestamp timestamp = Timestamp.valueOf(resultSet.getString("timestamp"));
                                Coordinate coordinate = new Coordinate(userId, timestamp, lat, lng, numSat);
                                return coordinate;
                            }
                        }
                    });

        } catch (IllegalArgumentException iae) {
            System.out.println("illegal argument exception");
            iae.printStackTrace();
            return null;
        }
        return coordinates;
    }

    private List<Coordinate> executeQueryForList(String query) {
        List<Coordinate> coordinates;
        try {
            coordinates = jdbcTemplate.query(
                    query, new RowMapper<Coordinate>() {
                        public Coordinate mapRow(ResultSet resultSet, int i) throws SQLException {
                            int userId = resultSet.getInt("userId");
                            double lat = resultSet.getDouble("latitude");
                            double lng = resultSet.getDouble("longitude");
                            int numSat = resultSet.getInt("numSatellite");
                            int rating = resultSet.getInt("rating");
                            if (resultSet.getString("timestamp").trim().equals("0") || lat == 0 || lng == 0 || numSat < 6) {
                                return null;
                            } else {
                                Timestamp timestamp = Timestamp.valueOf(resultSet.getString("timestamp"));
                                Coordinate coordinate = new Coordinate(userId, timestamp, lat, lng, numSat, rating);
                                return coordinate;
                            }
                        }
                    });

        } catch (IllegalArgumentException iae) {
            System.out.println("illegal argument exception");
            iae.printStackTrace();
            return null;
        }
        return coordinates;
    }

    private List<Coordinate> isRawDecider (boolean isRawData, String queryString) {
        if (isRawData) {
            return executeQueryForRawList(queryString);
        } else {
            return executeQueryForList(queryString);
        }
    }

}
