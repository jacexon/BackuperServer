package sample;

import java.io.File;
import java.util.Properties;

public class SavedFilesList {
    private File file = null;
    private static Properties filesList;

    public SavedFilesList(){

        filesList = new Properties();
    }

    public void setFile(File f){file = f;}

    public void addFileToList(File f){
        filesList.setProperty(f.getName(),f.getPath());
    }

    public String getFilePath(String name){
        return filesList.getProperty(name);
    }

    public void removeFileFromList(String name){
        filesList.remove(name);
    }

    public boolean fileOnList(String name){
        if(filesList.getProperty(name) != null)
            return true;
        else
            return false;
    }
}