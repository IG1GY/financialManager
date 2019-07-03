package main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import javafx.collections.ObservableList;

import javafx.util.StringConverter;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Grapher{

    FXMLLoader loader;
    XML xml;
    HashMap<String , LineChart<Number, Number>> charts;

    public Grapher(FXMLLoader loader, XML xml){

        this.loader = loader;
        this.xml = xml;
        this.charts = new HashMap<String, LineChart<Number, Number>>();
    }

    public LineChart<Number,Number> createGraph(FXMLLoader loader, String source){

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("time");
        xAxis.setTickLabelFormatter(new StringConverter<Number>(){

            public String toString(Number n){

                Date date = new Date(n.longValue());
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/mm/dd HH:mm:ss");
                String formatted = formatter.format(date);

                return formatted;
            }

            //definitely not needed...
            //not implemented because I had to add extra code to catch parse exception for it to compile.
            public Number fromString(String s){

                return 0;
            }
        });

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("amount");
        LineChart<Number, Number> chart = new LineChart<Number, Number>(xAxis, yAxis);

        int boundary = 10;
        Transaction[] transactions = xml.getTransactions(source, -1, (-1)*boundary);
        HashMap<String, XYChart.Series> map = new HashMap<String, XYChart.Series>();

        if(transactions.length != 0){
            for(Transaction transaction : transactions){
                if(transaction == null || transaction.amount == null) continue; //prevent nullpointer exception
                for(Transaction.Currency currency : transaction.amount){
                    if(map.containsKey(currency.coinType))
                        map.get(currency.coinType).getData().add(new XYChart.Data<Long, Integer>(transaction.time, currency.inte));
                    else{
                        XYChart.Series<Long, Integer> series = new XYChart.Series();
                        series.setName(currency.coinType);

                        map.put(currency.coinType, series);
                    }
                }
            }
        }

        for(Map.Entry<String, XYChart.Series> series : map.entrySet()){

            chart.getData().add(series.getValue());
        }
        return chart;
    }


    /*
    public void clear(Group elem){

        for(Object obj : elem.getChildren()) elem.getChildren().remove(obj);
    }
    public void clear(Pane elem){

        for(Object obj : elem.getChildren()) elem.getChildren().remove(obj);
    }

    /*
    <AnchorPane minHeight="0.0" minWidth="0.0" prefHeight="245.0" prefWidth="326.0">
       <children>
          <LineChart layoutY="30.0" prefHeight="186.0" prefWidth="487.0" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0">
             <xAxis>
                <CategoryAxis side="BOTTOM" />
             </xAxis>
             <yAxis>
                <NumberAxis side="LEFT" />
             </yAxis>
          </LineChart>
          <Label alignment="CENTER" layoutY="6.0" text="descriptor" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0" />
          <ProgressBar layoutX="25.0" layoutY="211.0" prefHeight="20.0" prefWidth="298.0" progress="0.0" AnchorPane.leftAnchor="12.0" AnchorPane.rightAnchor="12.0" />
       </children>
    </AnchorPane>
    */
    public void addGraph(LineChart chart, VBox module){

        //create pane:
        AnchorPane pane = new AnchorPane();

        Label label = new Label();
        ProgressBar bar = new ProgressBar();

        label.setText("descriptor");
        //label, bar set center:

        pane.getChildren().add(chart);
        pane.getChildren().add(label);
        pane.getChildren().add(bar);

        //anchors
        pane.setLeftAnchor(chart, 0.0);
        pane.setRightAnchor(chart, 0.0);
        //
        pane.setLeftAnchor(label, 0.0);
        pane.setRightAnchor(label, 0.0);
        //
        pane.setLeftAnchor(bar, 12.0);
        pane.setRightAnchor(bar, 12.0);
        //-----------------------------

        //add pane
        module.getChildren().add(pane);
    }

    public List<LineChart> createGraphes(int range, VBox module){

        LinkedList<LineChart> l = new LinkedList<LineChart>();
        for(String source : xml.getSources()){
            LineChart c = createGraph(loader, source);
            addGraph(c, module);
            l.add(c);
        }

        return l;
    }

    public void initModule(){

        //check to see if works
        SplitPane pane = new SplitPane();
        VBox module = (VBox) loader.getNamespace().get("vbox_grapher");

        //clear(module);
        createGraphes(-1, module);
    }
}
