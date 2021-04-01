package controller;

import domain.Participant;
import domain.Proba;
import domain.Utilizator;
import domain.enums.Stil;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import service.AppException;
import service.IAppObserver;
import service.IService;
import service.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ListeProbeController implements Initializable, IAppObserver {
    IService server;

    Stage stageInscriere;
    ObservableList<Proba> modelProbe = FXCollections.observableArrayList();
    ObservableList<Proba> modelProbe2 = FXCollections.observableArrayList();
    ObservableList<Participant> modelParticipanti = FXCollections.observableArrayList();
    private Utilizator user;
    @FXML
    Button button;
    @FXML
    TableView<Proba> tableViewProbe;
    @FXML
    TableView<Participant> tableViewParticipanti;
    @FXML
    TableColumn<Proba, Integer> tableColumnId;
    @FXML
    TableColumn<Proba, Integer> tableColumnIDP;
    @FXML
    TableColumn<Proba, Float> tableColumnDistantaP;
    @FXML
    TableColumn<Proba, Stil> tableColumnStilP;
    @FXML
    TableColumn<Proba, Float> tableColumnDistanta;
    @FXML
    TableColumn<Proba, Stil> tableColumnStil;
    @FXML
    TableColumn<Participant, String> tableColumnNume;
    @FXML
    TableColumn<Participant, Integer> tableColumnIdParticipant;
    @FXML
    TableColumn<Participant, Integer> tableColumnVarsta;
    @FXML
    TableView<Proba> tableViewProbeParticipant;
    boolean probaClicked = false;
    Proba lastClicked;

    public void setServer(IService service) throws AppException {
        this.server = service;
    }

    @FXML
    public void initialize() {
        tableColumnId.setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getId()));
        tableColumnDistanta.setCellValueFactory(new PropertyValueFactory<Proba, Float>("distanta"));
        tableColumnStil.setCellValueFactory(new PropertyValueFactory<Proba, Stil>("stil"));
        tableViewProbe.setItems(modelProbe);


    }

    public void initModelProbe() throws AppException {
        initialize();
        Proba[] probas = server.getProbe();
        List<Proba> list = new ArrayList<>();
        for (int i = 0; i < probas.length; i++) {
            list.add(probas[i]);
        }
        System.out.println(probas[0].getId());
        modelProbe.setAll(list);
    }

    public void handleProbaClicked(MouseEvent mouseEvent) throws AppException {
        probaClicked = true;
        Proba p = tableViewProbe.getSelectionModel().getSelectedItem();
        lastClicked = p;

        Participant[] participants = server.getParticipantiDupaProba(p);
        System.out.println(participants.length);
        List<Participant> list = new ArrayList<>();
        for (int i = 0; i < participants.length; i++) {
            list.add(participants[i]);
        }
        modelParticipanti.setAll(list);
        System.out.println("ID:");
        System.out.println(list.get(1).getId());
        tableColumnNume.setCellValueFactory(new PropertyValueFactory<Participant, String>("nume"));
        tableColumnVarsta.setCellValueFactory(new PropertyValueFactory<Participant, Integer>("varsta"));
        tableColumnIdParticipant.setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getId()));
        tableViewParticipanti.setItems(modelParticipanti);
    }

    public void handleParticipantClicked(MouseEvent mouseEvent) throws AppException {
        Participant p = tableViewParticipanti.getSelectionModel().getSelectedItem();
        System.out.println(p.getId());
        Proba[] probas = server.getProbeDupaParticipanti(p);
        List<Proba> list = new ArrayList<>();
        for (int i = 0; i < probas.length; i++) {
            list.add(probas[i]);
        }
        modelProbe2.setAll(list);

        tableColumnIDP.setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getId()));
        tableColumnDistantaP.setCellValueFactory(new PropertyValueFactory<Proba, Float>("distanta"));
        tableColumnStilP.setCellValueFactory(new PropertyValueFactory<Proba, Stil>("stil"));
        tableViewProbeParticipant.setItems(modelProbe2);

    }

    public void handleButton(MouseEvent mouseEvent) throws AppException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/Inscriere.fxml"));

        AnchorPane root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stageInscriere = new Stage();
        stageInscriere.setTitle("Inscrieri");
        stageInscriere.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(root);
        stageInscriere.setScene(scene);

        InscriereController inscriereController = loader.getController();
        inscriereController.setService(server);

        stageInscriere.show();
    }

    public void handleLogout(MouseEvent mouseEvent) {
        logout();
        ((Node) (mouseEvent.getSource())).getScene().getWindow().hide();
        stageInscriere.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    void logout() {
        try {
            server.logout(user, this);
        } catch (AppException e) {
            System.out.println("Logout error " + e);
        }

    }

    public void setUser(Utilizator crtUser) {
        this.user = crtUser;
    }

    @Override
    public void newInscriere() throws AppException {
        Platform.runLater(() -> {
            System.out.println("Update");
            try {
                if (probaClicked) {
                    Participant[] participants = new Participant[0];
                    participants = server.getParticipantiDupaProba(lastClicked);
                    System.out.println(participants.length);
                    List<Participant> list = new ArrayList<>();
                    for (int i = 0; i < participants.length; i++) {
                        list.add(participants[i]);
                    }
                    modelParticipanti.setAll(list);
                    System.out.println("ID:");
                    System.out.println(list.get(1).getId());
                    tableColumnNume.setCellValueFactory(new PropertyValueFactory<Participant, String>("nume"));
                    tableColumnVarsta.setCellValueFactory(new PropertyValueFactory<Participant, Integer>("varsta"));
                    tableColumnIdParticipant.setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getId()));
                    tableViewParticipanti.setItems(modelParticipanti);
                }
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/Update.fxml"));

                AnchorPane root = null;
                try {
                    root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Stage stage = new Stage();
                stage.setTitle("Update");
                stage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                stage.setScene(scene);

                UpdateContoller updateContoller = loader.getController();
                updateContoller.setService(server);

                stage.show();
            } catch (AppException e) {
                e.printStackTrace();
            }
        });
    }
}
