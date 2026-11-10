package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class GenericDAO<T> {

    protected Connection connection;

    public GenericDAO(Connection connection) {
        this.connection = connection;
    }

    public abstract boolean save(T entity);
    public abstract boolean update(T entity);
    public abstract boolean delete(Long id);
    public abstract T findById(Long id);
    public abstract List<T> findAll();

    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;
}