package net;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sniffer.netAdapter;

public class Controller extends GridPane{

    private Logger logger = LoggerFactory.getLogger(Controller.class);

    @FXML
    private Button toolbarStart;
    @FXML
    private Button startButton;
    @FXML
    private ListView intList;

    private netAdapter adapter = new netAdapter();

    public void initialize(){

        ObservableList<String> nifs = FXCollections.observableArrayList();
        nifs.addAll(adapter.getInterfaceDisplayNames());
        intList.setItems(nifs);

        startButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("resource/start_button_small.png"))));
        toolbarStart.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("resource/start_button_xs.png"))));
    }

    @FXML
    public void startSniffer(){
        logger.info("Sniffer Started!");
    }
}
