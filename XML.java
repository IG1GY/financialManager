import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

//just copied most of it from tutorial's point
public class XML{

    Element root;
    Document document;
    File file;

    public XML() throws Exception{

        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(file = new File("records.xml"));
        //Document document = builder.parse();
        }catch(Exception e){

            throw e;
        }
        root = document.getDocumentElement();
    }

    public List<String> getSources() throws Exception{

        ArrayList list = new ArrayList();
        NodeList list2 = root.getElementsByTagName("Source");
        for(int i=0; i<list2.getLength(); i++){

            list.add(((Element)list2.item(i)).getAttribute("value").toString());
        }
        return list;
    }

/*
<Transaction datetime="">
    <description>initial value</description>
    <amount>
        <currency type="nis" int="50" />
    </amount>
</Transaction>
*/

    public void addTransactions(List<Transaction> transactions){

        NodeList list2 = root.getElementsByTagName("Source");
        Element source = null;

        for(int i=0; i<list2.getLength() && elem == null; i++)
            if(list2.item(i).getAttributeNode("value") == transaction.source) elem = (Element) list2.item(i);

        if(elem == null)
            throw new Exception("no such source: " + source);

        Element Transactions = (Element) elem.getElementsByTagName("Transactions").item(0);

        for(Transaction transaction : transactions){

            Element ntransaction = document.createElement("Transaction");
            ntransaction.setAttribute("datetime", Long.toString(System.currentTimeMillis()));

            Element description = document.createElement("description");
            description.appendChild(document.createTextNode(transaction.information));

            Element amount = document.createElement("amount");

            for(Map.Entry<String, Integer> entry : transaction.amount.entrySet()){
                Element currency = document.createElement("currency");

                currency.setAttribute("type", entry.getKey());
                currency.setAttrobute("type", entry.getValue());
                amount.appendChild(currency);
            }

            ntransaction.appendChild(description);
            ntransaction.appendChild(amount);

            Transactions.appendChild(ntransaction);
        }
        this.save();
    }

    private void save(){

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(this.file);

        transformer.transform(source, result);
    }
    public static void main(String[] args){

        try{
            XML xml = new XML();
            System.out.println(xml.getSources());
        }catch(Exception e){

            System.out.println(e);
        }
    }
}
