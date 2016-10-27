package sample;

import com.healthmarketscience.rmiio.*;
import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;

import java.io.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class BackupServer extends UnicastRemoteObject implements FileInterface, Serializable{

    private SavedFilesList savedFilesList;
    File file = null;


    public BackupServer(String ip,int port) throws RemoteException{
        super(Registry.REGISTRY_PORT);
        savedFilesList = new SavedFilesList();

        try{
            LocateRegistry.createRegistry(port);
            Naming.rebind("rmi://" + ip + ":"+ port +"/BackupServer", this);
            System.err.println("Server is created on port: " + port);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void sendFile(RemoteInputStream ris, String filename, String extension) throws IOException, RemoteException{
        InputStream input = null;
        try{
            input = RemoteInputStreamClient.wrap(ris);
            writeToFile(input, filename, extension);
        }

        catch (Exception e){
            e.printStackTrace();
        }

    }


    public void writeToFile(InputStream stream, String filename, String extension) throws IOException, RemoteException {
        FileOutputStream output = null;

        try {
            file = File.createTempFile(filename, extension, new File("D:\\ojojo"));
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
                if(file.renameTo(new File(file.getParent() + "\\" + filename + extension))){
                    System.out.println("Rename succesful");
                }else{
                    System.out.println("Rename failed");
                }
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

    @Override
    public boolean checkFileOnServer(String nameOfFile) throws RemoteException {
        if(savedFilesList.fileOnList(nameOfFile))
            return true;
        else
            return false;
    }

}