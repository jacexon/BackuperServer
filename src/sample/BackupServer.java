package sample;

import com.healthmarketscience.rmiio.*;
import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import javafx.fxml.Initializable;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class BackupServer extends UnicastRemoteObject implements FileInterface, Serializable{

    public int numberOfChunks;
    public BackupServer(String ip,int port) throws IOException {
        super(port);

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
            String query = "INSERT INTO backuperdb.files VALUES('" + filename + "','" + newdate + "', '"+(Integer.parseInt(getVersion(filename))+1)+"','" + path + "')";
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
                if (readBytes > 0){
                    output.write(result, 0, readBytes);
                    numberOfChunks++;
                }
            } while(readBytes != -1);
            System.out.println(file.length());

            output.flush();


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ELO");
        } finally{
            if(output != null){
                output.close();
               if(file.renameTo(new File(file.getParent() + "\\" + filename + "-v" + (Integer.parseInt(getVersion(filename))+1) + extension))){

                    System.out.println("Rename succesful");
                }else{
                    System.out.println("Rename failed");
                }
                System.out.println("Zamykam strumień...");
            }

        }
        return "D:\\\\Server\\\\" + filename + "-v" + (Integer.parseInt(getVersion(filename))+1) + extension;

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

    public void resetChunks() throws RemoteException{
        numberOfChunks = 0;
    }

    public RemoteInputStream tableStream() throws RemoteException, IOException{
        SimpleRemoteInputStream input = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(getData());
        oos.flush();
        oos.close();
        InputStream ois = new ByteArrayInputStream(baos.toByteArray());
        try{
            input = new SimpleRemoteInputStream(ois);
        }

        catch (Exception e){
            e.printStackTrace();
        }
        return input.export();

    }

    public int getChunk() throws RemoteException{
        return numberOfChunks;
    }

    public RemoteInputStream chunkStream() throws RemoteException, IOException{
        SimpleRemoteInputStream input = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeInt(numberOfChunks);
        oos.flush();
        oos.close();
        InputStream ois = new ByteArrayInputStream(baos.toByteArray());
        try{
            input = new SimpleRemoteInputStream(ois);
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
            ResultSet rs = st.executeQuery("SELECT * FROM backuperdb.files WHERE (filename=" + "'" + name + "'" + " AND lastmodified= "
                    + "'" + newdate + "')");
            if (rs.next()){
                lol = true;
            }
            else{
                lol = false;
            }
        }
        catch (SQLException e){
            e.getMessage();
        }
        return lol;
    }


    public String getVersion(String filename){
        String ver = "";
        try{
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/backuperdb","Jacek","password");
            Statement st = conn.createStatement();
            String query = "SELECT MAX(version) FROM backuperdb.files WHERE filename=" + "'"+ filename + "'";
            ResultSet rs = st.executeQuery(query);
            if(rs.next()) ver = rs.getString(1);
            if(rs.wasNull()){
                ver = "0";
            }
            else{
                System.out.println(ver);
            }
        }
        catch (SQLException e){
            e.getMessage();
        }
        return ver;
    }


    public String[] getData() throws RemoteException{
        String[] srv = null;
        try{
            ArrayList<String> list = new ArrayList<>();
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/backuperdb","Jacek","password");
            Statement st = conn.createStatement();
            String query = "SELECT * FROM backuperdb.files";
            ResultSet rs = st.executeQuery(query);
            while(rs.next()){
                for(int i = 0; i<rs.getMetaData().getColumnCount(); i++){
                    list.add(rs.getString(i+1));
                }

            }

            srv = list.stream().toArray(String[]::new);
        }
        catch (SQLException e){
            e.getMessage();
        }
            return srv;
    }


    public long getFileSize(String fName, String ver) throws RemoteException {
        long fsize = 0L;

        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/backuperdb","Jacek","password");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM backuperdb.files WHERE (filename=" + "'" + fName + "'" + " AND version= "
                    + "'" + ver + "')");

            if(rs.next()){
                String filePath = rs.getString("path");
                Path fPath = Paths.get(filePath);
                File f = fPath.toFile();
                fsize = f.length();
            }



        } catch (SQLException e) {
            e.printStackTrace();
        }


        return fsize;
    }


    public void deleteFile(String fName, String ver, String fPath) throws RemoteException{

        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/backuperdb", "Jacek", "password");
            Statement st = conn.createStatement();

            Path path = Paths.get(fPath);
            File f = path.toFile();
            f.delete();

            st.executeUpdate("DELETE FROM backuperdb.files WHERE (filename=" + "'" + fName + "'" + " AND version= "
                    + "'" + ver + "')");


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}