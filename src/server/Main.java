package server;

import server.cache.Cache;
import server.db_access.DataBaseAccessor;
import server.db_access.file_db.FileAsDataBase;
import server.entities.Entity;
import server.entities.Person;
import server.subscribers.SubscriberImp;

/**
 * Created by Tal on 30/12/2017.
 */
public class Main {


    public static void main(String[] args) {
        Class clazz = Person.class;
        try {
            DataBaseAccessor dataBaseAccessor = new FileAsDataBase(clazz);
            Cache cache = new Cache(dataBaseAccessor, clazz, LoadingTypeEnum.EAGER);
            new SubscriberImp(cache);
            int id = 1;
            cache.add(new Person(id, "Israel Israeli"));
            cache.update(new Person(id, "Tal Ohana"));
            Entity entity = cache.get(id);
            cache.remove(entity.getId());

        } catch (Exception e){
            System.out.println(e);
        }
    }
}
