package server.subscribers;

import server.CRUDEnum;
import server.cache.Cache;
import server.entities.Entity;

/**
 * Created by Tal on 30/12/2017.
 */
public class SubscriberImp implements Subscriber {

    private final int ID = 1;

    public SubscriberImp(Cache cache){
        cache.subscribe(this);
    }

    @Override
    public void notify(CRUDEnum crudEnum, Entity entity) {
        System.out.println("Subscriber with ID: " + ID + "- The action is: " + crudEnum.toString() + " and the entity is: " + entity.toString());
    }
}
