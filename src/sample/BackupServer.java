package sample;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.rmi.*;
import com.healthmarketscience.rmiio.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;


public class BackupServer extends UnicastRemoteObject implements FileInterface, Serializable{

    public BackupServer(String ip,int port) throws RemoteException{
        super(Registry.REGISTRY_PORT);
        try{
            LocateRegistry.createRegistry(port);
            Naming.rebind("rmi://" + ip + ":"+ port +"/BackupServer", this);
            System.err.println("Server is created on port: " + port);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void sendFile(RemoteInputStream ris) throws IOException, RemoteException{
        InputStream input = null;
        try{
            input = RemoteInputStreamClient.wrap(ris);
            writeToFile(input);
        }

        catch (Exception e){
            e.printStackTrace();
        }

    }


    public void writeToFile(InputStream stream) throws IOException, RemoteException {
        FileOutputStream output = null;

        try {
            File file = File.createTempFile("data", ".mp4", new File("D:\\"));
            output = new FileOutputStream(file);
            int chunk = 4096;
            byte [] result = new byte[chunk];

            int readBytes = 0;
            do {
                readBytes = stream.read(result);
                if (readBytes > 0)
                    output.write(result, 0, readBytes);
                System.out.println("Zapisuje...");
            } while(readBytes != -1);
            System.out.println(file.length());
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(output != null){
                output.close();
                System.out.println("Zamykam strumień...");
            }
        }
    }


    public RemoteInputStream passAStream(String filepath) throws RemoteException{
        SimpleRemoteInputStream input = null;
        try{
            input = new SimpleRemoteInputStream(new FileInputStream(filepath));
        }

        catch (Exception e){
            e.printStackTrace();
        }
        return input.export();
    }

}

