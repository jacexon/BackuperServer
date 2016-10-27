package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("launchScreen.fxml"));
        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(root, 550, 350));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
        //String ext1 = FilenameUtils.getExtension("/path/to/file/foo.txt");
    }
}
