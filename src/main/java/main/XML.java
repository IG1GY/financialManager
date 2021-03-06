package main;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import org.w3c.dom.Element;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.io.InputStream;

/***
    so, this is the XML file, supposed to handle xml.
    it's main functions are:
        (for input)
        addTransaction_noSave(Transaction)                  - bare add transaction
        save()                                              - saves changes conducted to the virtual xml file to hard disk. best used in bulk

        addTransactions(Transaction[] | List<Transaction>)  - add and save a list of transactions
        *) addTransaction(Transaction)                      - adds and saves one transaction. not a good idea. experimental.


        (for output)
        getSources()                                        - get all available sources as listed in the xml file.
        getSum(source)                                      - returns the current sum of the source.
        getTransactions(Source, start, end){                - gets all transactions from a certain source by index selection. return an array of transactions.
            source - source name; String.
            start;end - int: get transactions from source start to end.
            [end not inclusive.]
        }

        note:
            of course that with the large xml files it's supposed to handle,
            constantly saving is really slow and inefficient. so either use addTransaction_noSave+save
            or addTransactions to save in bulk.
            also, this is supplemented by the fact that the xml shouldn't be over 1000-10,000 transactions long imo.
            beyond that, send it to a cluster or a database for later retrieval.

            also, the transaction seems relatively bulky and overly detailed. it's up to the database implementation
            to add main details concerning the user. I'm not supposed to be the main user of this. the AI is.
            this is just for supervising it's actions.

            yes, the class seems a bit messy, but it's well organized via documentation.
            I hate xml on java, but if it works it works.
***/
public class XML{

    Element root;
    Document document;
    File file;

    public XML(String fileLocation){

        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            //so here's the problem...
            document = builder.parse(this.file = new File(fileLocation));
        //Document document = builder.parse();
        }catch(Exception e){

            e.printStackTrace();
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
    /*
    <Source value="value">
        <Transactions>
        </Transactions>
        <Sum>

        </Sum>
    </Source>
    */

    //I added the hash map when I created the Grapher class.
    //the grapher class does repeated search, so the hash map
    //should make repeated search much faster
    HashMap<String, Element> sources = null;
    private Element getSource(String source) {

        if(sources == null){

            sources = new HashMap<String, Element>();
            NodeList list2 = root.getElementsByTagName("Source");
            for(int i=0; i<list2.getLength(); i++){

                String str = ((Element)list2.item(i)).getAttribute("value");
                Element elem = (Element) list2.item(i);
                sources.put(str, elem);
            }
        }
        if(sources.containsKey(source)) return sources.get(source);

        //if no such Source exists, create a new one.
        Element src = this.document.createElement("Source");
        src.setAttribute("value", source);
        Element transactions = this.document.createElement("Transactions");
        Element sum = this.document.createElement("Sum");

        src.appendChild(transactions);
        src.appendChild(sum);
        root.appendChild(sum);

        sources.put(source, src);
        return src;
    }

    public List<Transaction.Currency> getSum(String source){

            Element elem = this.getSource(source);
            return getSum(elem);
        }
    public List<Transaction.Currency> getSum(Element source){

            Element elem = source;
            /*get the sum*/
            Element sum = (Element) elem.getElementsByTagName("Sum").item(0);

            ArrayList<Transaction.Currency> returnCurrencies = new ArrayList<Transaction.Currency>();

            //supposed to contain all available currencies
            NodeList currencies = sum.getChildNodes();
            for(int i=0; i<currencies.getLength(); i++){

                try{

                    String type = ((Element) currencies.item(i)).getAttribute("type");
                    int inte = Integer.parseInt(((Element) currencies.item(i)).getAttribute("int"));
                    returnCurrencies.add(new Transaction.Currency(inte, type));
                }catch(Exception e){

                    System.out.println(currencies.item(i));
                }
            }

            return returnCurrencies;
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
        Element Transactions = (Element) elem.getElementsByTagName("Transactions").item(0);

        Element ntransaction = document.createElement("Transaction");
        ntransaction.setAttribute("datetime", Long.toString(System.currentTimeMillis()));
        Element description = document.createElement("description");
        description.appendChild(document.createTextNode(transaction.information));
        Element amount = document.createElement("amount");

        /*get the sum*/
        Element sum = (Element) elem.getElementsByTagName("Sum").item(0);

        NodeList currencies = sum.getChildNodes();
        HashMap<String, Integer> currency_sums = new HashMap<String, Integer>();

        //create a currency to sum map:
        for(int i=0; i<currencies.getLength(); i++){

            if(currencies.item(i).getNodeType() == Node.TEXT_NODE) continue;
            String type = ((Element) currencies.item(i)).getAttribute("type");
            int namount = Integer.parseInt(((Element) currencies.item(i)).getAttribute("int"));
            currency_sums.put(type, namount);
        }

        //actually converts the entries to xml
        //update the map and sum for new currencies if necessary.
        for(Transaction.Currency entry : transaction.amount){

                Element currency = document.createElement("currency");

                currency.setAttribute("type", entry.coinType);
                currency.setAttribute("int", Integer.toString(entry.inte));
                amount.appendChild(currency);

                //fixed bug: instead of appending currency, append a copy of currency
                if(!currency_sums.containsKey(entry.coinType)){

                    Element currency_copy = document.createElement("currency");

                    currency_copy.setAttribute("type", entry.coinType);
                    currency_copy.setAttribute("int", Integer.toString(entry.inte));
                    sum.appendChild(currency_copy);
                }

                currency_sums.put(entry.coinType, (currency_sums.containsKey(entry.coinType) ?
                                    currency_sums.get(entry.coinType) : 0)
                                    + entry.inte);
        }

        //update currencies since sum may have new child nodes.
        currencies = sum.getChildNodes();

        //save sum to currencies:
        for(int i=0; i < currencies.getLength(); i++){

            if(currencies.item(i).getNodeType() == Node.TEXT_NODE || currencies.item(i) == null) continue;

            Element current = (Element) currencies.item(i);
            current.setAttribute("int", Integer.toString(currency_sums.get(current.getAttribute("type"))));
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
    public void addTransactions(Transaction[] transactions){

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

            //get and save sum
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "1");
            transformer.transform(source, result);
        }catch(Exception e){

            e.printStackTrace();
        }
    }

    public Transaction[] getTransactions(String source, int start, int end){

        Element source_elem = getSource(source);
        NodeList list = source_elem.getElementsByTagName("Transaction");

        //transform to list...
        Transaction[] transactions = new Transaction[Math.abs(end - start)];

        int length = list.getLength();
        if(length == 0)
            return transactions;
        while(start < 0)
            start += length;
        while(end < 0)
            end += length;

        int direction = 1;
        if(start > end)
            direction = -1;

        //might just work
        for(int i=start; i < end && i < list.getLength(); i += direction)
            transactions[i - start] = Transaction.fromElement((Element) list.item(i));

        return transactions;
    }
   /*
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
    */
}
