package sample;

import com.healthmarketscience.rmiio.*;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Properties;

public interface FileInterface extends Remote{
    public void sendFile(RemoteInputStream ris, String filename, String extension, long lastModified) throws RemoteException, IOException;
    public void writeToFile(InputStream stream, String filename, String extension, long lastModified) throws IOException, RemoteException;
    public RemoteInputStream passAStream(String filename) throws  RemoteException;

    public boolean checkFileOnServer(String nameOfFile, long modifyDate) throws RemoteException;
    public Properties getSavedFilesList() throws RemoteException;

}