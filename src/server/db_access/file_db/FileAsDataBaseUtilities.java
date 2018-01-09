package server.db_access.file_db;

import server.entities.Entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tal on 30/12/2017.
 */
public class FileAsDataBaseUtilities {

    protected static String fromEntityToString(Entity entity) throws Exception{
        return entity.toString();
        }


    /**
     * get a line from file and return new Entity().
     * handle with reflection
     */
    protected static Entity fromStringToEntity(String line, Class clazz) throws Exception{

        //create the instance of Entity (Person / Vehicle)
        Constructor <Entity> constructor = clazz.getConstructor();
        Entity entity = constructor.newInstance();

        //get a map contains fields members in class and their values.
        Map<String, String> propertiesMap = toProperties(line);

        //invoke each field setter and set the field value in the entity.
        Field[] fields = clazz.getDeclaredFields();
        for(Field field: fields){
            String fieldName = field.getName();
            String getMethod = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1, fieldName.length());
            Class fieldClass = entity.getClass().getDeclaredMethod(getMethod).getReturnType();
            Object value = getValueOfField(propertiesMap, fieldName, fieldClass);
            field.setAccessible(true);
            field.set(entity, value);
            field.setAccessible(false);
        }
        return entity;
    }

    /**
     * return the value of a field in integer or string and etc.
     * @param propertiesMap
     * @param fieldName
     * @param fieldClass
     * @return
     */
    private static Object getValueOfField(Map<String, String> propertiesMap, String fieldName, Class fieldClass) {
        Object value = propertiesMap.get(fieldName);
        if(fieldClass.getName().equals("int") || fieldClass.getName().equals("Integer")){
            value = Integer.parseInt((String)value);
        }
        //todo: can add conditions for boolean, double and etc.
        return value;
    }

    /**
     * get a map of properties (field member and its value) from a line in the file
     * @param line
     * @return
     */
    private static Map<String, String> toProperties(String line){
        Map<String, String> propertiesMap = new HashMap<>();
        String[] parts = line.split(",");
        for(String part: parts){
            String[] separatedKeyValue = part.split(":");
            propertiesMap.put(separatedKeyValue[0], separatedKeyValue[1]);
        }
        return propertiesMap;
    }

    protected static boolean isEntityWithTheSameId(int id, String line){
        return line.startsWith("id:" + id);
    }

}
