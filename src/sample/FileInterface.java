package sample;

import com.healthmarketscience.rmiio.*;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileInterface extends Remote{
    public void sendFile(RemoteInputStream ris, String filename, String extension) throws RemoteException, IOException;
    public void writeToFile(InputStream stream, String filename, String extension) throws IOException, RemoteException;
    public RemoteInputStream passAStream(String filename) throws  RemoteException;

    public boolean checkFileOnServer(String nameOfFile) throws RemoteException;

}