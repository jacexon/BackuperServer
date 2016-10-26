package sample;
import java.io.*;
import java.rmi.*;
import com.healthmarketscience.rmiio.*;

public interface FileInterface extends Remote{
    public void sendFile(RemoteInputStream ris) throws RemoteException, IOException;
    public void writeToFile(InputStream stream) throws IOException, RemoteException;
    public RemoteInputStream passAStream(String filename) throws  RemoteException;

}
