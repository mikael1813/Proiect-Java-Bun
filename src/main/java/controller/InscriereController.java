package controller;

import domain.Participant;
import domain.Proba;
import domain.enums.Stil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import service.AppException;
import service.IService;
import service.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class InscriereController {
    IService server;

    ObservableList<Proba> modelProbe = FXCollections.observableArrayList();
    ObservableList<Proba> modelProbeParticipant = FXCollections.observableArrayList();

    @FXML
    TextField txtNume;
    @FXML
    TextField txtVarsta;
    @FXML
    TableView<Proba> tableView;
    @FXML
    TableColumn<Proba,Integer> tableColumnId;
    @FXML
    TableColumn<Proba,Float> tableColumnDistanta;
    @FXML
    TableColumn<Proba, Stil> tableColumnStil;
    @FXML
    TableView<Proba> tableViewParticipant;
    @FXML
    TableColumn<Proba,Integer> tableColumnIdP;
    @FXML
    TableColumn<Proba,Float> tableColumnDistantaP;
    @FXML
    TableColumn<Proba, Stil> tableColumnStilP;

    public void setService(IService service) throws AppException {
        this.server = service;
        initModelProbe();
    }

    @FXML
    public void initialize() {
        tableColumnId.setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getId()));
        tableColumnDistanta.setCellValueFactory(new PropertyValueFactory<Proba, Float>("distanta"));
        tableColumnStil.setCellValueFactory(new PropertyValueFactory<Proba, Stil>("stil"));
        tableColumnIdP.setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getId()));
        tableColumnDistantaP.setCellValueFactory(new PropertyValueFactory<Proba, Float>("distanta"));
        tableColumnStilP.setCellValueFactory(new PropertyValueFactory<Proba, Stil>("stil"));
        tableView.setItems(modelProbe);


    }

    private void initModelProbe() throws AppException {
        Proba[] probas = server.getProbe();
        modelProbe.setAll(probas);
    }

    public void handleAdd(MouseEvent mouseEvent) {
        Proba p = tableView.getSelectionModel().getSelectedItem();
        if(modelProbeParticipant.contains(p)){
            MessageAlert.showErrorMessage(null,"Nu poti adauga aceeasi proba de 2 ori");
        }
        else{
            modelProbeParticipant.add(p);
        }
        tableViewParticipant.setItems(modelProbeParticipant);

    }

    public void handleInscrie(MouseEvent mouseEvent) throws AppException {
        Participant participant = new Participant(txtNume.getText(),Integer.parseInt(txtVarsta.getText()));
        List<Proba> probe = modelProbeParticipant;
        Proba[] probas = new Proba[probe.size()];
        for(int i=0;i<probe.size();i++){
            probas[i]=probe.get(i);
        }
        server.Inscrie(participant,probas);

//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(getClass().getResource("/views/Update.fxml"));
//
//        AnchorPane root = null;
//        try {
//            root = loader.load();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Stage stage = new Stage();
//        stage.setTitle("Update");
//        stage.initModality(Modality.WINDOW_MODAL);
//        Scene scene = new Scene(root);
//        stage.setScene(scene);
//
//        UpdateContoller updateContoller = loader.getController();
//        updateContoller.setService(server);
//
//        stage.show();
    }
}
