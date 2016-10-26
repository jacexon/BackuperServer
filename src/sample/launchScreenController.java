package sample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javax.annotation.Resource;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.*;

public class launchScreenController {
    @FXML
    private TextField port_textField;

    @FXML
    private TextField ip_textField;

    @FXML
    private Button stop_button;

    @FXML
    private Label running_label;

    @FXML
    private CheckBox localhost_checkbox;



    public void handleLaunchButton(ActionEvent event) throws RemoteException {
        if ((!(port_textField.getText().isEmpty())) && (!(ip_textField.getText().isEmpty()))) {
            if (Integer.parseInt(port_textField.getText()) > 1024) {
                BackupServer server = new BackupServer(ip_textField.getText(), Integer.parseInt(port_textField.getText()));
                running_label.setText("Server is running...");
            } else {
                running_label.setText("Invalid port! Try again.");
                ip_textField.clear();
                port_textField.clear();
            }
        } else {
            running_label.setText("Invalid port! Try again.");
            ip_textField.clear();
            port_textField.clear();


        }
    }

    public void handleStopButton(ActionEvent event){
        System.exit(0);
    }

    public void handleLocalhostCheckbox(ActionEvent event){
        if (localhost_checkbox.isSelected()){
            ip_textField.setText("127.0.0.1");
            port_textField.setText("1099");
            ip_textField.setEditable(false);
            port_textField.setEditable(false);
        }

        else {
            ip_textField.setEditable(true);
            port_textField.setEditable(true);
        }
    }

}
