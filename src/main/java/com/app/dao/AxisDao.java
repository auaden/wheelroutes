package com.app.dao;

import com.app.domain.Axis;
import com.app.jdbc_exception.UpdateFailedException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by adenau on 18/9/16.
 */
@Repository
public class AxisDao {

    private JdbcTemplate jdbcTemplate;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public void insertBatch(final List<Axis> axes, String tableName) {
        int[] counts = jdbcTemplate.batchUpdate(
                "insert into " + tableName + "(\"userId\", \"timestamp\", \"xAxis\", \"yAxis\", \"zAxis\") values(?,?,?,?,?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        Axis axis = axes.get(i);
                        preparedStatement.setInt(1, axis.getUserId());
                        preparedStatement.setString(2, axis.getTimestamp().toString());
                        preparedStatement.setDouble(3, axis.getxAxis());
                        preparedStatement.setDouble(4, axis.getyAxis());
                        preparedStatement.setDouble(5, axis.getzAxis());
                    }
                    public int getBatchSize() {
                        return axes.size();
                    }
                }
        );
        int i = 0;
        for (int count:counts) {
            if (count == 0) throw new UpdateFailedException("Row not updated : " + i);
            i++;
        }
    }

    public List<Axis> findAll(String tableName) {
        List<Axis> axes;

        String query = "select * from " + tableName + " order by \"userId\", timestamp";
        axes = executeQueryForList(query);
        return axes;
    }

    public List<Axis> findAllById(int userId, String tableName) {
        String query = "select * from " + tableName + " where \"userId\" = " + userId + " order by timestamp";
        System.out.println(query);
        List<Axis> axes = executeQueryForList(query);
        System.out.println("size retrieved: " + axes.size());
        return axes;
    }

    public List<Axis> findAllByDate(int userId, String startDateTime, String endDateTime, String tableName) {
        String startDate = startDateTime.replace("T", " ");
        String endDate = endDateTime.replace("T", " ");
        String queryString = "select * from " + tableName
                + " where \"userId\" = " + userId
                + " and timestamp >= \'"
                + startDate+ "\'"
                + " and timestamp<= \'"
                + endDate+ "\' order by timestamp";
        System.out.println(queryString);
        List<Axis> axes = executeQueryForList(queryString);
        System.out.println("size retrieved: " + axes.size());
        return axes;
    }

    public void deleteAll(String tableName) {
        int count = jdbcTemplate.update("delete from " + tableName);
    }

    private List<Axis> executeQueryForList(String query) {
        List<Axis> axes;

        try {
            axes = jdbcTemplate.query(
                    query, new RowMapper<Axis>() {
                        public Axis mapRow(ResultSet resultSet, int i) throws SQLException {
                            if (resultSet.getString("timestamp").trim().equals("0")) {
                                return null;
                            } else {
                                int userId = Integer.parseInt(resultSet.getString("userId").trim());
                                Timestamp timestamp = Timestamp.valueOf(resultSet.getString("timestamp"));
                                double xAxis = Double.parseDouble(resultSet.getString("xAxis"));
                                double yAxis = Double.parseDouble(resultSet.getString("yAxis"));
                                double zAxis = Double.parseDouble(resultSet.getString("zAxis"));
                                Axis axis = new Axis(userId, timestamp, xAxis, yAxis, zAxis);
                                return axis;
                            }
                        }
                    });
        } catch (IllegalArgumentException iae) {
            System.out.println("illegal argument exception");
            iae.printStackTrace();
            return null;
        }
        return axes;
    }


}
