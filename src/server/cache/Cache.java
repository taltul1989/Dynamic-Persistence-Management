package server.cache;

import server.CRUDEnum;
import server.CRUDOperations;
import server.LoadingTypeEnum;
import server.db_access.DataBaseAccessor;
import server.db_access.file_db.FileAsDataBase;
import server.entities.Entity;
import server.entities.Person;
import server.entities.Vehicle;
import server.subscribers.Subscriber;
import server.subscribers.SubscriberImp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tal on 30/12/2017.
 */
public class Cache implements CRUDOperations {
    private List<Subscriber> subscribers;
    private Map<Integer, Entity> mappedEntities;
    private Class classOfEntity;
    private DataBaseAccessor dataBaseAccessor;
    private LoadingTypeEnum loadingType;

    public Cache(DataBaseAccessor dataBaseAccessor, Class classOfEntity, LoadingTypeEnum loadingType) throws Exception {
        subscribers = new ArrayList<>();
        this.dataBaseAccessor = dataBaseAccessor;
        this.classOfEntity = classOfEntity;
        this.loadingType = loadingType;
        this.setByLoadingType();
    }

    public LoadingTypeEnum getLoadingType() {
        return loadingType;
    }

    private void setByLoadingType() throws Exception {
        mappedEntities = new HashMap<>();
        switch (this.loadingType) {
            case EAGER: {
                //get all entities from db
                List<Entity> entities = dataBaseAccessor.getAll();
                for (Entity entity : entities) {
                    mappedEntities.put(entity.getId(), entity);
                }
                break;
            }
            case LAZY: {
                //do nothing
                break;
            }
        }
    }

    public void subscribe(Subscriber subscriber) {
        this.subscribers.add(subscriber);
    }

    private void notifyAllSubscribers(CRUDEnum crudEnum, Entity entity) {
        for (Subscriber subscriber : this.subscribers) {
            subscriber.notify(crudEnum, entity);
        }
    }

    @Override
    public synchronized void add(Entity entity) throws Exception {
        if (isEntityHasTheSameClassType(entity)) {
            if (!isEntityAlreadyExist(entity.getId())) {
                //first add to db and then to map.
                dataBaseAccessor.add(entity);
                this.mappedEntities.put(entity.getId(), entity);
                notifyAllSubscribers(CRUDEnum.ADD, entity);
            } else {
                throw new Exception("entity with id: " + entity.getId() + " already exist.");
            }
        } else {
            throw new Exception("entity with the type of: " + entity.getClass() + " has a different class type. suppose to be: " + classOfEntity);
        }
    }

    @Override
    public synchronized void update(Entity entity) throws Exception {
        if (isEntityHasTheSameClassType(entity)) {
            if (isEntityAlreadyExist(entity.getId())) {
                //get the old entity values
                Entity oldEntity = this.get(entity.getId());
                //first update db and then the map.
                dataBaseAccessor.update(entity);
                this.mappedEntities.replace(entity.getId(), oldEntity, entity);
                notifyAllSubscribers(CRUDEnum.UPDATE, entity);
            } else {
                throw new Exception("entity with id: " + entity.getId() + " doesn't exist in cache.");
            }
        } else {
            throw new Exception("entity with the type of: " + entity.getClass() + " has a different class type. suppose to be: " + classOfEntity);
        }
    }

    @Override
    public synchronized Entity remove(int id) throws Exception {
        Entity entityToBeRemoved = this.get(id);
        //first remove from db and then from map
        dataBaseAccessor.remove(id);
        this.mappedEntities.remove(id);
        notifyAllSubscribers(CRUDEnum.REMOVE, entityToBeRemoved);

        return entityToBeRemoved;
    }

    @Override
    public synchronized Entity get(int id) throws Exception {
        Entity entity = null;
        switch (this.loadingType) {
            case EAGER: {
                entity = this.mappedEntities.get(id);
                if (entity == null) {
                    throw new Exception("entity with id: " + id + " doesn't exist.");
                }
                break;
            }
            case LAZY: {
                entity = this.mappedEntities.get(id);
                //if entity not in map, then ask it from db and then add it to map.
                if (entity == null) {
                    entity = dataBaseAccessor.get(id);
                    this.mappedEntities.put(id, entity);
                }
                break;
            }
        }

        notifyAllSubscribers(CRUDEnum.GET, entity);
        return entity;
    }

    private boolean isEntityAlreadyExist(int id) {
        try {
            this.get(id);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean isEntityHasTheSameClassType(Entity entity) {
        return this.classOfEntity == entity.getClass();
    }


    //for test only!!!!
    public Map<Integer, Entity> getMappedEntities(){
        return this.mappedEntities;
    }


}
