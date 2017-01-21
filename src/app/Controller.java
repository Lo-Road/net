package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sniffer.Sniffer;
import sniffer.netAdapter;
import sniffer.netInterface;

import java.util.ArrayList;
import java.util.Arrays;


public class Controller extends GridPane{

    private Logger logger = LoggerFactory.getLogger(Controller.class);

    @FXML private Button toolbarStart, startButton, toolbarStop;
    @FXML private ListView<String> intList;
    @FXML private TableView<PacketCell> packetTable;
    @FXML private TabPane tabs;
    @FXML private ListView<String> propertiesTableGeneral, propertiesTable1, propertiesTable2, propertiesTable3;
    @FXML private TextArea raw;
    @FXML private Label propertiesLabel1, propertiesLabel2, propertiesLabel3;

    private netAdapter adapter = new netAdapter();
    private static boolean sniffing = false;
    private static PacketCellFactory factory;
    private packetPropertiesLayout layout;

    public void initialize(){

        ObservableList<String> nifs = FXCollections.observableArrayList();
        nifs.addAll(adapter.getInterfaceDisplayNames());
        intList.setItems(nifs);

        startButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("resource/start_button_small.png"))));
        toolbarStart.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("resource/start_button_xs.png"))));
        toolbarStop.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("resource/stop_button_xs.png"))));

        initPropertiesLayout();

        packetTable.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            layout.generateLayout(factory.getPacket(packetTable.getSelectionModel().getSelectedIndex()), newValue);
        }));
    }

    @FXML
    public void startSniffer(){

        ObservableList<String> selectedSniffers =
                intList.getSelectionModel().getSelectedItems();

        if(selectedSniffers.size()!=1){

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Network Interface Configuration");
            alert.setContentText("Please select one and only one Network Interface for capture");
            alert.showAndWait();

        }else{

            String interfaceName = selectedSniffers.get(0);
            netInterface nif = adapter.getInterfaceByDisplayName(interfaceName);
            Sniffer sniffer = null;

            try {
                sniffer = new Sniffer(Pcaps.getDevByAddress(nif.getAddresses().get(0)), false);
            }catch (PcapNativeException e){
                logger.error("Invalid interface used");
                logger.debug(e.getMessage());
                Dialog<Void> dialog = new Dialog<>();
                dialog.setContentText("Unable to capture from this interface. Please select another.");
                dialog.show();
            }

            if(sniffer!=null) {
                sniffing = true;
                tabs.getSelectionModel().select(1);
                factory = new PacketCellFactory(sniffer, packetTable);
                factory.start();
                toolbarStart.setDisable(true);
                toolbarStop.setDisable(false);
            }

        }


    }

    @FXML
    void stopSniffer(){
        if(sniffing){
            factory.stop();
        }
        sniffing=false;
        toolbarStart.setDisable(false);
        toolbarStop.setDisable(true);
    }

    static boolean isSniffing(){
        return sniffing;
    }

    private void initPropertiesLayout(){
        ArrayList<ListView<String>> lists = new ArrayList<>(
                Arrays.asList(propertiesTableGeneral, propertiesTable1, propertiesTable2, propertiesTable3));
        ArrayList<Label> labels = new ArrayList<>(Arrays.asList(propertiesLabel1, propertiesLabel2, propertiesLabel3));
        layout = new packetPropertiesLayout(lists, labels, raw);
    }

}
