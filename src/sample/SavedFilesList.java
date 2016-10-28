package sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class SavedFilesList {
    private File file = null;
    private static Properties filesList;

    public SavedFilesList() throws FileNotFoundException, IOException{

        File cfgfile = new File("savedFileList.properties");
        FileInputStream fis = new FileInputStream(cfgfile);
        Properties filesList = new Properties();
        filesList.load(fis);
        //fis.close();
    }

    public void setFile(File f){file = f;}

    public void addFileToList(File f, long lastModified)
    {
        String nameAndDateOfFile = f.getName()+ " " +Long.toString(lastModified);
        filesList.setProperty(nameAndDateOfFile,f.getPath());
    }

    public String getFilePath(String name){
        return filesList.getProperty(name);
    }

    public void removeFileFromList(String name){
        filesList.remove(name);
    }

    public Properties getFilesList(){return filesList;}

    public boolean fileOnList(String name, long modifyDate) {

        if (!filesList.isEmpty()) {
            Properties temporaryList = filesList;
            Set<String> filesNames = filesList.stringPropertyNames();
            int found = 0, notFound = 0;

            for (String s : filesNames) {
                if (s.contains(name) && s.contains(Long.toString(modifyDate)))
                    found++;
                else {
                    notFound++;
                }
            }

            if (found > 0)
                return true;
            else
                return false;
        }
        return false;
    }

}