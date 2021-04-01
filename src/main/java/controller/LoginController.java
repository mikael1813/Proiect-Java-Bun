package controller;

import domain.Utilizator;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import service.AppException;
import service.IService;
import service.Service;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoginController {
    IService server;
    ListeProbeController controller;

    @FXML
    TextField txtUsername;
    @FXML
    TextField txtPassword;
    @FXML
    Button button;
    Parent mainChatParent;

    public void setParent(Parent p) {
        mainChatParent = p;
    }

    private Utilizator crtUser;

    public void setService(IService service) {
        this.server = service;
    }


    public void eventHandler(MouseEvent mouseEvent) {
//        AtomicBoolean ok = new AtomicBoolean(false);
//        service.getUtilizatori().forEach(x->{
//            if(x.getUser().equals(txtUsername.getText()) && x.getParola().equals(txtPassword.getText())){
//                ok.set(true);
//            }
//        });
//        if (!ok.get()){
//            MessageAlert.showErrorMessage(null,"Username sau parola gresita");
//        }
//        else{
//            Stage stage1 = (Stage) button.getScene().getWindow();
//            stage1.close();
//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(getClass().getResource("/views/ListaProbe.fxml"));
//
//            AnchorPane root = null;
//            try {
//                root = loader.load();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            Stage stage = new Stage();
//            stage.setTitle("Main");
//            stage.initModality(Modality.WINDOW_MODAL);
//            Scene scene = new Scene(root);
//            stage.setScene(scene);
//
//            ListeProbeController listeProbeController = loader.getController();
//            listeProbeController.setService(service);
//
//            stage.show();
//        }
//Parent root;
        String nume = txtUsername.getText();
        String passwd = txtPassword.getText();
        crtUser = new Utilizator(nume, passwd);


        try {
            server.login(crtUser, controller);
            // Util.writeLog("User succesfully logged in "+crtUser.getId());
            Stage stage = new Stage();
            stage.setTitle("Window for " + crtUser.getUser());
            stage.setScene(new Scene(mainChatParent));

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    controller.logout();
                    System.exit(0);
                }
            });

            stage.show();
            controller.setUser(crtUser);
            controller.initModelProbe();
            ((Node) (mouseEvent.getSource())).getScene().getWindow().hide();

        } catch (AppException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MPP chat");
            alert.setHeaderText("Authentication failure");
            alert.setContentText("Wrong username or password");
            alert.showAndWait();
        }
    }

    public void setListeProbeController(ListeProbeController chatCtrl) {
        this.controller = chatCtrl;
    }
}
