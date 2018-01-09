package server.db_access.file_db;

import server.db_access.DataBaseAccessor;
import server.entities.Entity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tal on 30/12/2017.
 */
public class FileAsDataBase implements DataBaseAccessor {

    private final String FILE_NAME = "dbFile.txt";
    private File file;
    private Class clazz;

    public FileAsDataBase(Class clazz) throws IOException{
        this.clazz = clazz;
        file = new File(FILE_NAME);
        synchronized (FileAsDataBase.class){
            file.createNewFile();
        }
    }

    @Override
    public void add(Entity entity) throws Exception {
        //check if entity is not inside db already
        boolean isEntityExistInDB = true;
        try {
            this.get(entity.getId());
        }
        catch (Exception e){
            isEntityExistInDB = false;
        }

        //if its not in db, then write it in file. else throw exception.
        if(!isEntityExistInDB){
            FileWriter fileWriter = null;
            try{
                fileWriter = new FileWriter(file,true);
                fileWriter.write(FileAsDataBaseUtilities.fromEntityToString(entity) + System.lineSeparator());//appends the string to the file
            }catch (Exception e){
                throw new Exception(e);
            } finally {
                if(fileWriter != null){
                    fileWriter.close();
                }
            }
        } else {
            throw new Exception("entity with id: " + entity.getId() + " already exist in db.");
        }

    }

    @Override
    public void update(Entity entity) throws Exception {
        remove(entity.getId()); //remove the entity from file
        this.add(entity); //add it back to file but with updated properties
    }

    @Override
    public List<Entity> getAll() throws Exception{

        List<Entity> entities = new ArrayList<>();
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        try{
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                //get the entity inside the list of entities
                entities.add(FileAsDataBaseUtilities.fromStringToEntity(line, clazz));
                line = bufferedReader.readLine();
            }
        }catch (Exception e){
            throw new Exception(e);
        } finally {
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(fileReader != null){
                fileReader.close();
            }
        }

        return entities;
    }


    /**
     * create a temp file, and put all data from source file but without the requested entity.
     * @param id of entity to remove
     * @throws Exception
     */
    @Override
    public Entity remove(int id) throws Exception {
        Entity entityToBeRemove = null;

        File tempFile = new File("tempFile.txt");
        BufferedReader reader = null;
        BufferedWriter writer = null;
        FileReader fileReader = null;
        FileWriter fileWriter = null;
        try{
            fileReader = new FileReader(file);
            fileWriter = new FileWriter(tempFile);
            reader = new BufferedReader(fileReader);
            writer = new BufferedWriter(fileWriter);

            String line;
            while((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                //if the line is the requested Entity to remove, then doesn't write it in the temp file
                if(FileAsDataBaseUtilities.isEntityWithTheSameId(id, trimmedLine)){
                    entityToBeRemove = FileAsDataBaseUtilities.fromStringToEntity(line, clazz);
                    continue;
                }
                writer.write(line + System.lineSeparator());
            }
        }catch (Exception e){
            throw new Exception(e);
        }finally {
            if(fileReader != null){
                fileReader.close();
            }
            if(writer != null){
                writer.close();
            }
            if(fileWriter != null){
                fileWriter.close();
            }
            if(reader != null){
                reader.close();
            }
        }
        //delete the source file and rename the tempFile to the source file.
        boolean successful = file.delete() && tempFile.renameTo(file);
        //if couldn't delete or rename or entity wasn't found in db, then throw exception.
        if(!successful || entityToBeRemove == null){
            throw new Exception("exception in remove entity with id: " + id);
        }
        return entityToBeRemove;
    }

    @Override
    public Entity get(int id) throws Exception {

        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        try{
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                //check if its the right entity by id
                if(FileAsDataBaseUtilities.isEntityWithTheSameId(id, line)){
                    return FileAsDataBaseUtilities.fromStringToEntity(line, clazz);
                }
                line = bufferedReader.readLine();
            }
        }catch (Exception e){
            throw new Exception(e);
        } finally {
            if(fileReader != null){
                fileReader.close();
            }

            if(bufferedReader != null){
                bufferedReader.close();
            }
        }

        throw new Exception("can't find entity with id: " + id);
    }
}
