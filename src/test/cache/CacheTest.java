package test.cache;

import server.LoadingTypeEnum;
import server.cache.Cache;
import server.db_access.DataBaseAccessor;
import server.db_access.file_db.FileAsDataBase;
import server.entities.Entity;
import server.entities.Person;
import server.entities.Vehicle;
import server.subscribers.SubscriberImp;

/**
 * Created by Tal on 09/01/2018.
 */
public class CacheTest {

    /**
     * this main runs the unit tests operations for lazy loading and eager loading.
     * @param args
     */
    public static void main(String[] args) {
        /**The order here is important!
         The eager is the second case because the lazy loading tests
         creates a file with specific data inside - see function verifyInitOfConstructor()!
         **/
        verifyOperationsForLazyLoading();
        verifyOperationForEagerLoading();
    }

    /**
     * verify all operations works well in eager loading case.
     */
    private static void verifyOperationForEagerLoading() {
        try {
            //init the dataBaseAccessor and cache, and add subscriber.
            DataBaseAccessor dataBaseAccessor = new FileAsDataBase(Person.class);
            Cache eagerCache = new Cache(dataBaseAccessor, Person.class, LoadingTypeEnum.EAGER);
            new SubscriberImp(eagerCache);

            verifyInitOfConstructor(eagerCache);

            //verify all operations.
            addOperation(eagerCache, 10, 11, 12);
            updateOperation(eagerCache, 13, 14);
            getOperation(eagerCache, 15);
            removeOperation(eagerCache, 16);

        }catch (Exception e){
            System.out.println(e);
        }
    }

    /**
     * verify all operations works well in lazy loading case.
     * @return
     */
    private static boolean verifyOperationsForLazyLoading() {
        try {
            //init the dataBaseAccessor and cache, and add subscriber.
            DataBaseAccessor dataBaseAccessor = new FileAsDataBase(Person.class);
            Cache lazyCache = new Cache(dataBaseAccessor, Person.class, LoadingTypeEnum.LAZY);
            new SubscriberImp(lazyCache);

            verifyInitOfConstructor(lazyCache);

            //verify all operations.
            addOperation(lazyCache, 1,2, 3);
            updateOperation(lazyCache, 4, 5);
            getOperation(lazyCache, 6);
            removeOperation(lazyCache, 7);

        }catch (Exception e){
            System.out.println(e);
            return true;
        }
        return false;
    }

    /**
     * this test verify the cases of eager or lazy loading.
     * in case of eager, we will load all data from db to cache.
     * in case of lazy, we will not load any data in constructor.
     */
    private static void verifyInitOfConstructor(Cache cache) throws Exception{
        boolean isValid = false;
        switch (cache.getLoadingType()){
            case EAGER:{
                //after loading the dbFile.txt: it has 6 entities inside!
                isValid = cache.getMappedEntities().size() == 6;
                break;
            }
            case LAZY:{
                isValid = cache.getMappedEntities().size() == 0;
                break;
            }
        }
        if(!isValid){
            throw new Exception("verifyInitOfConstructor(): case Failed.");
        }
    }

    /**
     * this test verify the remove operation.
     * @param cache
     * @param id
     * @throws Exception
     */
    private static void removeOperation(Cache cache, int id) throws Exception{
        verifyRemoveSucceed(cache, id);
        removeUnknownEntity(cache);
    }

    /**
     * this test add some entity and verify its removal.
     * @param cache
     * @param id
     * @throws Exception
     */
    private static void verifyRemoveSucceed(Cache cache, int id) throws Exception{
        Entity entity = new Person(id, "name");
        cache.add(entity);
        Entity removedEntity = cache.remove(entity.getId());
        if(!entity.toString().equals(removedEntity.toString())){
            throw new Exception("verifyRemoveSucceed(): case failed");
        }
    }

    /**
     * this test remove an entity that doesn't exist - its suppose to fail.
     * @param cache
     * @throws Exception
     */
    private static void removeUnknownEntity(Cache cache) throws Exception{
        Entity unknownEntity = new Person(-1, "name");
        boolean exceptionThrown = false;
        try {
            cache.remove(unknownEntity.getId());
        }catch (Exception e){
            exceptionThrown = true;
        }
        if(!exceptionThrown){
            throw new Exception("verifyRemoveSucceed(): case failed");
        }
    }

    /**
     * this test verify the add operation
     * @param cache
     * @param id1
     * @param id2
     * @param id3
     * @throws Exception
     */
    private static void addOperation(Cache cache,  int id1, int id2, int id3) throws Exception{
        verifyAddSucceed(cache, id1, id2);
        verifyOnlyOneAdd(cache, id3);
        verifyOnlyOneClassTypeCanBeAdded(cache);

    }

    /**
     * this test verify we can add only one type of Entity!
     * @param cache
     * @throws Exception
     */
    private static void verifyOnlyOneClassTypeCanBeAdded(Cache cache) throws Exception {
        //try to add Vehicle class
        boolean isExceptionThrown = false;
        try{
            cache.add(new Vehicle(6, "Blue"));
        }catch (Exception e){
            isExceptionThrown = true;
        }
        if(!isExceptionThrown){
            throw new Exception("verifyOnlyOneClassTypeCanBeAdded()- Case Failed.");
        }
    }

    /**
     * this test verify that we can add only one Entity with the same id.
     * @param cache
     * @param id
     * @throws Exception
     */
    private static void verifyOnlyOneAdd(Cache cache, int id) throws Exception {
        Entity entity = new Person(id, "name");
        //try to add e1 again - suppose to throw exception
        boolean isExceptionThrown = false;
        try{
            cache.add(entity);
            cache.add(entity);
        }catch (Exception e){
            isExceptionThrown = true;
        }

        if(!isExceptionThrown){
            throw new Exception("verifyOnlyOneAdd()- Case Failed.");
        }
    }

    /**
     * this test verify we can add entity without exception.
     * @param cache
     * @param id1
     * @param id2
     * @throws Exception
     */
    private static void verifyAddSucceed(Cache cache, int id1, int id2) throws Exception {
        Entity e1 = new Person(id1, "tal");
        Entity e2 = new Person(id2, "Israel");

        try {
            cache.add(e1);
            //verify e1 has been added
            cache.get(e1.getId());

            cache.add(e2);
            //verify e2 has been added
            cache.get(e2.getId());

            //verify e1 hasn't been overwritten when adding other entity.
            cache.get(e1.getId());
        }catch (Exception e){
            throw new Exception("verifyAddSucceed() : case failed");
        }
    }

    /**
     * this test verify update operation
     * @param cache
     * @param id1
     * @param id2
     * @throws Exception
     */
    private static void updateOperation(Cache cache,  int id1, int id2) throws Exception{

        verifyUpdateSucceed(cache, id1);
        checkUpdateForUnknownId(cache);
        verifyOnlyOneClassTypeCanBeUpdated(cache, id2);
    }

    /**
     * this test verify an update operation works well
     * @param cache
     * @param id
     * @throws Exception
     */
    private static void verifyUpdateSucceed(Cache cache, int id) throws Exception {

        try {
            //add entity
            Entity e = new Person(id, "someone");
            cache.add(e);
            //change the entity
            Entity eUpdated = new Person(id, "other");
            //try to update
            cache.update(eUpdated);
        }catch (Exception e){
            throw new Exception("verifyUpdateSucceed(): case failed");
        }

    }

    /**
     * this test tries to update entity that doesn't exist.
     * @param cache
     * @throws Exception
     */
    private static void checkUpdateForUnknownId(Cache cache) throws Exception {
        Entity eUpdated = new Person(-1, "name");
        boolean isExceptionThrown = false;
        try{
            cache.update(eUpdated);
        }catch (Exception e){
            isExceptionThrown = true;
        }
        if(!isExceptionThrown){
            throw new Exception("checkUpdateForUnknownId()- Case Failed");
        }
    }

    /**
     * this test verify that we can only update entity with same type of Entity.
     * @param cache
     * @param id
     * @throws Exception
     */
    private static void verifyOnlyOneClassTypeCanBeUpdated(Cache cache, int id) throws Exception {
        //try to update Vehicle class with same id in cache
        cache.add(new Person(id, "name"));
        boolean isExceptionThrown = false;
        try{
            cache.update(new Vehicle(5, "Blue"));
        }catch (Exception e){
            isExceptionThrown = true;
        }
        if(!isExceptionThrown){
            throw new Exception("verifyOnlyOneClassTypeCanBeUpdated()- Case Failed.");
        }
    }

    /**
     * this test verify get operation
     * @param cache
     * @param id
     * @throws Exception
     */
    private static void getOperation(Cache cache, int id) throws Exception{
        verifyGetSucceed(cache, id);
        getUnknownEntity(cache);
    }

    /**
     * this test verify get operation works well.
     * @param cache
     * @param id
     * @throws Exception
     */
    private static void verifyGetSucceed(Cache cache, int id) throws Exception{
        Entity entity = new Person(id, "some name");
        cache.add(entity);
        Entity entityFromGetCall = cache.get(entity.getId());
        if(!entity.toString().equals(entityFromGetCall.toString())){
            throw new Exception("verifyGetSucceed() - case failed.");
        }
    }

    /**
     * this test tries to get an Entity that doesn't exist.
     * @param cache
     * @throws Exception
     */
    private static void getUnknownEntity(Cache cache) throws Exception{
        Entity entityNotInDB = new Person(-1, "name");
        boolean isExceptionThrown = false;
        try {
            cache.get(entityNotInDB.getId());
        } catch (Exception e){
            isExceptionThrown = true;
        }
        if(!isExceptionThrown){
            throw new Exception("getUnknownEntity() - case failed.");
        }
    }

}


