import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Element;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.io.InputStream;
//just copied most of it from tutorial's point
public class XML{

    Element root;
    Document document;
    File file;

    private XML(File file){

        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(this.file = file);
        //Document document = builder.parse();
        }catch(Exception e){

            throw e;
        }

        root = document.getDocumentElement();
    }

    public List<String> getSources() {

        ArrayList list = new ArrayList();
        NodeList list2 = root.getElementsByTagName("Source");
        for(int i=0; i<list2.getLength(); i++){

            list.add(((Element)list2.item(i)).getAttribute("value").toString());
        }
        return list;
    }

    private Element getSource(String source) {

        NodeList list2 = root.getElementsByTagName("Source");
        for(int i=0; i<list2.getLength(); i++){

            if(((Element)list2.item(i)).getAttribute("value").contains(source)) return (Element) list2.item(i);
        }
        return null;
    }

/*
<Transactions>
    <Transaction datetime="">
        <description>initial value</description>
        <amount>
            <currency type="nis" int="50" />
        </amount>
    </Transaction>
</Transactions>
<Sum>
    <currency type="nis">
        (float)
    </currency>
</Sum>
*/
    public void addTransaction_noSave(Transaction transaction){

        Element elem = this.getSource(transaction.source);
        System.out.println("element: " + elem);
        Element Transactions = (Element) elem.getElementsByTagName("Transactions").item(0);

        Element ntransaction = document.createElement("Transaction");
        ntransaction.setAttribute("datetime", Long.toString(System.currentTimeMillis()));
        Element description = document.createElement("description");
        description.appendChild(document.createTextNode(transaction.information));
        Element amount = document.createElement("amount");

        /*get the sum*/
        Element sum = (Element) elem.getChildNodes().item(1);

        NodeList currencies = sum.getChildNodes();
        HashMap<String, Integer> currency_sums = new HashMap<String, Integer>();
        for(int i=0; i<currencies.getLength(); i++){

            String type = ((Element) currencies.item(i)).getAttribute("type");
            int namount = Integer.parseInt(((Element) currencies.item(i)).getTextContent());
            currency_sums.put(type, namount);
        }

        for(Transaction.Currency entry : transaction.amount){
                Element currency = document.createElement("currency");

                currency.setAttribute("type", entry.coinType);
                currency.setAttribute("int", Integer.toString(entry.inte));
                amount.appendChild(currency);

                currency_sums.put(entry.coinType, currency_sums.containsKey(entry.coinType) ?
                                    currency_sums.get(entry.coinType) : 0
                                    + entry.inte);
        }

        for(int i=0; i < currencies.getLength(); i++){

            Element current = (Element) currencies.item(i);
            current.setTextContent(Integer.toString(currency_sums.get(current.getAttribute("type"))));
        }

        ntransaction.appendChild(description);
        ntransaction.appendChild(amount);

        Transactions.appendChild(ntransaction);
    }

    public void addTransactions(List<Transaction> transactions){

        for(Transaction transaction : transactions)
            this.addTransaction_noSave(transaction);
        this.save();
    }

    public void addTransaction(Transaction transaction){

        this.addTransaction_noSave(transaction);
        this.save();
    }

    private void save(){

        try{
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);

            transformer.transform(source, result);
        }catch(Exception e){

            e.printStackTrace();
        }
    }

    public Transaction[] getTransactions(String source, int start, int end){

        Element source_elem = getSource(source);
        NodeList list = source_elem.getElementsByTagName("Transaction");

        Transaction[] transactions = new Transaction[end - start + 1];
        for(int i=start; i < end; i++) transactions[i - start] = Transaction.fromElement((Element) list.item(i));

        return transactions;
    }

    public static void main(String[] args){

        try{
            XML xml = new XML();
            System.out.println(xml.getSources());
            xml.addTransaction(new Transaction("Bank Account", "first source",
                new Transaction.Currency[]{ new Transaction.Currency(-500, "nis")}, System.currentTimeMillis()));

        }catch(Exception e){

            e.printStackTrace();
        }
    }
}
