package main;

import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.control.SplitPane;
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


    //create standart element:
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

        for(Transaction transaction : transactions){
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

        for(Map.Entry<String, XYChart.Series> series : map.entrySet()){

            chart.getData().add(series.getValue());
        }
        return chart;
    }

    public void addGraph(LineChart chart){

        Map<String, Object> mapper = loader.getNamespace();
        //check to see if works
        SplitPane pane = new SplitPane();
        VBox box = (VBox) mapper.get("vbox_grapher");
        box.getChildren().add()
    }

    public Element initModule(){

        Map<String, Object> mapper = loader.getNamespace();
        //check to see if works
        SplitPane pane = new SplitPane();
        VBox box = (VBox) mapper.get("vbox_grapher");
    }

    public List<LineChart> createGraphes(int range){

        LinkedList<LineChart> l = new LinkedList<LineChart>();
        Element elem = initModule();
        for(String source : xml.getSources()){
            LineChart c = createGraph(loader, source);
            addGraph(c);
            l.add(c);
        }

        return l;
    }
}
