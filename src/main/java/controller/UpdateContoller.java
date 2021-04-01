package controller;

import domain.Proba;
import domain.enums.Stil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import service.AppException;
import service.IService;
import service.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UpdateContoller {
    IService service;
    ObservableList<Proba> modelProbe = FXCollections.observableArrayList();

    @FXML
    TableView<Proba> tableViewProbe;
    @FXML
    TableColumn<Proba,Integer> tableColumnId;
    @FXML
    TableColumn<Proba,Float> tableColumnDistanta;
    @FXML
    TableColumn<Proba, Stil> tableColumnStil;
    @FXML
    TableColumn<Proba, Integer> tableColumnNr;

    public void setService(IService service) throws AppException {
        this.service = service;
        initModelProbe();
    }

    @FXML
    public void initialize() {
        tableColumnId.setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getId()));
        tableColumnDistanta.setCellValueFactory(new PropertyValueFactory<Proba, Float>("distanta"));
        tableColumnStil.setCellValueFactory(new PropertyValueFactory<Proba, Stil>("stil"));
        tableColumnNr.setCellValueFactory(param -> {
            try {
                return new SimpleObjectProperty(service.getNrParticipanti(param.getValue()));
            } catch (AppException e) {
                e.printStackTrace();
            }
            return new SimpleObjectProperty(0);
        });
        tableViewProbe.setItems(modelProbe);

    }

    private void initModelProbe() throws AppException {
        Proba[] probas = service.getProbe();
        modelProbe.setAll(probas);
        //numar.setText(String.valueOf(service.getNrParticipanti()));
    }
}
