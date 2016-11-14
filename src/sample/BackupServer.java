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
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BackupServer extends UnicastRemoteObject implements FileInterface, Serializable{


    public BackupServer(String ip,int port) throws IOException {
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

    public void sendFile(RemoteInputStream ris,String filename, String extension, long lastModified) throws IOException, RemoteException{
        InputStream input = null;
        try{
            input = RemoteInputStreamClient.wrap(ris);
            String path = writeToFile(input, filename, extension, lastModified);
            Date date = new Date(lastModified);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String newdate = sdf.format(date);
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/backuperdb","Jacek","password");
            Statement st = conn.createStatement();
            String query = "INSERT INTO backuperdb.files VALUES('" + filename + "','" + newdate + "', '3','" + path + "'";
            st.executeUpdate(query);
            System.out.println("Zapisano w bazie danych VALUES("+ filename + " " + newdate);
        }

        catch (Exception e){
            e.getMessage();
        }

    }


    public String writeToFile(InputStream stream, String filename, String extension, long lastModified) throws IOException, RemoteException {
        FileOutputStream output = null;
        File file = null;
        try {
            file = File.createTempFile(filename, extension, new File("D:\\Server"));
            output = new FileOutputStream(file);

            int chunk = 4096;
            byte [] result = new byte[chunk];

            int readBytes;
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
               if(file.renameTo(new File(file.getParent() + "\\" + filename + "-v" + "3" + extension))){

                    System.out.println("Rename succesful");
                }else{
                    System.out.println("Rename failed");
                }
                System.out.println("Zamykam strumień...");
            }


        }
        return file.getPath();

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

    public boolean checkFileOnServer(String name, Date date) throws RemoteException{
        boolean lol = false;
        try{

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String newdate = sdf.format(date);
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/backuperdb","Jacek","password");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT name, lastmodified FROM files WHERE name=" + "'" + name + "'" + " AND date= "
                    + "'" + newdate + "'");
            if (rs.next()){
                lol = true;
            }
            else lol = false;
        }
        catch (SQLException e){
            e.getMessage();
        }
        return lol;
    }

}