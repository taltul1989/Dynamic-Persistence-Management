package server.subscribers;

import server.CRUDEnum;
import server.entities.Entity;

/**
 * Created by Tal on 30/12/2017.
 */
public interface Subscriber {
    void notify(CRUDEnum crudEnum, Entity entity);
}
