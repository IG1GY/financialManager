package main;

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
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Alert{

    boolean didFinish = false;
    boolean canceled = false;
    private Stage mstage;
    private FXMLLoader mloader;
    private String source = "";
    private String information = "";
    private Transaction.Currency amount = null;
    private Map<String, Object> mapper;

    public Alert(FXMLLoader mloader) throws IOException{

        this.mloader = mloader;
        Scene scene = new Scene(mloader.load());
        Stage mstage = new Stage();

        mapper = mloader.getNamespace();

        System.out.println(mapper);
        Button cancel = (Button) mapper.get("cancel");
        Button submit = (Button) mapper.get("submit");
        ComboBox box = (ComboBox) mapper.get("source");

        if(box == null){

            System.out.println("yeah, it's null");
        }
        box.getItems().addAll(
            "Cash",
            "Bank Account",
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
            this.amount = toCurrency(amount.getText());
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

    public String toString(){

        String st = "source: " + source + "\n\n" +
                    "information: " + information + "\n\n" +
                    "amount: " + amount + "\n\n";
        return st;
    }

    public Transaction toTransaction(){

        return new Transaction(this.source, this.information,
        new Transaction.Currency[]{ this.amount },
        System.currentTimeMillis());
    }

    // regex : "(\d+)\s*(\w+)?". example: 320nis | 320 nis
    public Transaction.Currency toCurrency(String str){

        Pattern ptrn = Pattern.compile("(\\d+)\\s*(\\w+)?");
        Matcher m = ptrn.matcher(str);
        if(m.matches()){

            int inte = Integer.parseInt(m.group(1));
            String coinType;
            if(m.groupCount() == 2)
                coinType = m.group(2);
            else
                coinType = "default";

            return new Transaction.Currency(inte, coinType);
        }else{

            System.out.println("invalid input! amount doesn't match the pattern! exiting...");
            this.canceled = true;
            return null;
        }
    }
}

/*
    bug report/todo:
        remove the toolbar.
        (if you exit normally the window crashes.
        only use the cancel button.)
*/
