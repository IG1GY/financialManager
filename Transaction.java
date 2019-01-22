import java.util.Map;
import java.util.List;
import java.util.LinkedList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Transaction{

    String source, information;

    Currency[] amount = null;  //a map pointing a string "coin type" | "currency" to an int
    long time = 0;

    public Transaction(String source, String information, Currency[] amount, long time){

        this.source = source;
        this.information = information;
        this.amount = amount;
        this.time = time;
    }

    /*
    <Transaction datetime="">
        <description>initial value</description>
        <amount>
            <currency type="nis" int="50" />
        </amount>
    </Transaction>
    */
    public static Transaction fromElement(Element elem){

        Element description = (Element) elem.getElementsByTagName("description").item(0);
        Element amount = (Element) elem.getElementsByTagName("amount").item(0);

        NodeList currencies = amount.getElementsByTagName("currency");
        Currency[] list = new Currency[currencies.getLength()];

        for(int i=0; i < list.length; i++){

            Element currency = (Element) currencies.item(i);

            String type = currency.getAttribute("type");
            int inte = Integer.parseInt(currency.getAttribute("int"));
            list[i] = new Currency(inte, type);
        }

        String descrip = description.getTextContent();
        long datetime = Long.parseLong(elem.getAttribute("datetime"));

        String source = ((Element)((Element) elem.getParentNode()).getParentNode()).getAttribute("value");
        return new Transaction(source, descrip, list, datetime);
    }

    static class Currency {

        int inte;
        String coinType;

        public Currency(int inte, String coinType){

            this.inte = inte;
            this.coinType = coinType;
        }

        public String toString(){

            return Integer.toString(inte) + "|" + coinType; 
        }
    }
}
