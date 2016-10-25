package com.app.dao;

import com.app.domain.User;
import com.app.jdbc_exception.DeleteFailedException;
import com.app.jdbc_exception.InsertFailedException;
import com.app.jdbc_exception.UpdateFailedException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Collections;
import java.util.List;

@Repository
public class UserDao {
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(User user) {
        PreparedStatementCreatorFactory preparedStatementCreatorFactory =
                new PreparedStatementCreatorFactory(
                        "INSERT INTO \"user\" (email, password) values(?,?)",
                        new int[] {Types.VARCHAR, Types.VARCHAR}
                );

        int count = jdbcTemplate.update(
                preparedStatementCreatorFactory.newPreparedStatementCreator(new Object[] {
                        user.getEmail(), user.getPassword()}));

        if (count !=1 ) throw new InsertFailedException("Cannot insert Account");
    }
    
    public void insert(User user, int sensitivity) {
        PreparedStatementCreatorFactory preparedStatementCreatorFactory =
                new PreparedStatementCreatorFactory(
                        "INSERT INTO \"user\" (email, password, sensitivity) values(?,?,?)",
                        new int[] {Types.VARCHAR, Types.VARCHAR, Types.INTEGER}
                );

        int count = jdbcTemplate.update(
                preparedStatementCreatorFactory.newPreparedStatementCreator(new Object[] {
                        user.getEmail(), user.getPassword()}));

        if (count !=1 ) throw new InsertFailedException("Cannot insert Account");
    }

    public void update(User user) {
        int count = jdbcTemplate.update(
                "update \"user\" set(email, password) = (?,?) where email=?",
                user.getEmail(), user.getPassword());
        if (count != 1) throw new UpdateFailedException("Cannot Update Account");
    }

    public User find(String email) {
        //normal method
        String sql = "select email, password from \"user\" where email = ?";
        //String sql = "select email, password, sensitivity from \"user\" where email = ?";
        try {
            User toReturn = jdbcTemplate.queryForObject(
                    sql,
                    new RowMapper<User>() {
                        public User mapRow(ResultSet resultSet, int i) throws SQLException {
                            if (resultSet == null) {
                                return null;
                            }
                            User user = new User();
                            user.setEmail(resultSet.getString("email"));
                            user.setPassword(resultSet.getString("password"));
                            //user.setSensitivity(resultSet.getInt("sensitivity"));
                            return user;
                        }
                    }, email);
            return toReturn;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public void delete(String email) {
        int count = jdbcTemplate.update("delete from \"user\" where email =?", email);
        if (count != 1) throw new DeleteFailedException("Cannot delete account");
    }
    
    public void modifySensitivity(User user, int difference){
        int count = jdbcTemplate.update(
                "update \"user\" set(email, sensitivity) = (?,?) where email=?",
                user.getEmail(), user.getSensitivity() + difference);
        if (count != 1) throw new UpdateFailedException("Update sensitivity failed");
    }

}
