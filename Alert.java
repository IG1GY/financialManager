import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.Parent;

import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import javafx.scene.control.Button;

import java.util.Map;
//thanks to a guy named bucky. (yeah thanks btw).
//used as reference: https://github.com/buckyroberts/Source-Code-from-Tutorials/blob/master/JavaFX/005_creatingAlertBoxes/AlertBox.java
//to Modality and the showAndWait function;

public class Alert{

    boolean didFinish = false;
    boolean canceled = false;
    private Stage mstage;
    private FXMLLoader mloader;
    private String source = "";
    private String information = "";
    private int amount = 0;

    public Alert(FXMLLoader mloader) throws IOException{

        this.mloader = mloader;
        Scene scene = new Scene(mloader.load());
        Stage mstage = new Stage();

        Map<String, Object> mapper = mloader.getNamespace();

        System.out.println(mapper);
        Button cancel = (Button) mapper.get("cancel");
        Button submit = (Button) mapper.get("submit");
        ComboBox box = (ComboBox) mapper.get("source");

        if(box == null){

            System.out.println("yeah, it's null");
        }
        box.getItems().addAll(
            "Cash",
            "Bank",
            "Mastercard",
            "Bitcoin"
        );

        cancel.setOnAction(e -> {

            didFinish = true;
            canceled = true;
            mstage.close();
        });
        submit.setOnAction(e -> {

            TextArea information = (TextArea) mapper.get("description");
            TextField  amount = (TextField) mapper.get("amount");

            this.information = information.getText();
            //handle what to do when this is not an integer...
            this.amount = Integer.parseInt(amount.getText());
            this.source = box.getValue().toString();
            didFinish = true;
            mstage.close();
        });


        mstage.initModality(Modality.APPLICATION_MODAL);
        mstage.setTitle("financial report");
        mstage.setScene(scene);
        mstage.setResizable(false);

        this.mstage = mstage;
        mstage.showAndWait();
    }

    public String getInfo(){

        String st = "source: " + source + "\n\n" +
                    "information: " + information + "\n\n" +
                    "amount: " + amount + "\n\n";
        return st;
    }
}
