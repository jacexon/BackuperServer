package sample;
import com.healthmarketscience.rmiio.RemoteRetry;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javax.swing.*;
import javafx.event.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

public class ServerTable implements Serializable{
    private static final long serialVersionUID = 20120731125400L;
    private final SimpleStringProperty FileName;
    private final SimpleStringProperty lastModified;
    private final SimpleStringProperty version;
    private final SimpleStringProperty path;

    public ServerTable(String filename, String lastModified, String version, String path){
        this.FileName = new SimpleStringProperty(filename);
        this.lastModified = new SimpleStringProperty(lastModified);
        this.version = new SimpleStringProperty(version);
        this.path = new SimpleStringProperty(path);

    }

    public String getFileName(){
        return FileName.get();
    }

    public String getVersion(){
        return version.get();
    }

    public String getLastModified() {return lastModified.get();}

    public String getPath() { return path.get();}

    public void setFileName(String v){ FileName.set(v);}
    public void setVersion(String v){
        version.set(v);
    }
    public void setLastModified(String v) {lastModified.set(v);}
    public void setPath(String v) {path.set(v);}
}
