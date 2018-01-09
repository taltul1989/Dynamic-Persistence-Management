package server;

import server.entities.Entity;

/**
 * Created by Tal on 30/12/2017.
 */
public interface CRUDOperations {

    void add(Entity entity) throws Exception;
    void update(Entity entity) throws Exception;
    Entity remove(int id) throws Exception;
    Entity get(int id) throws Exception;
}
