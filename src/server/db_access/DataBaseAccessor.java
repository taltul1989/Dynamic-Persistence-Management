package server.db_access;

import server.CRUDOperations;
import server.entities.Entity;

import java.util.List;

/**
 * Created by Tal on 30/12/2017.
 */
public interface DataBaseAccessor extends CRUDOperations {
    List<Entity> getAll() throws Exception;
}
