package main;

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

/***
    so, this is the XML file, supposed to handle xml.
    it's main functions are:
        (for input)
        addTransaction_noSave(Transaction)
        save()

        addTransactions(Transaction[] | List<Transaction>)
        *) addTransaction(Transaction)
        (for output)
        getSources()
        getTransactions(Source, start, end){
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
    private Element getSource(String source) {

        NodeList list2 = root.getElementsByTagName("Source");
        for(int i=0; i<list2.getLength(); i++){

            if(((Element)list2.item(i)).getAttribute("value").contains(source)) return (Element) list2.item(i);
        }

        //if no such Source exists, create a new one.
        Element src = this.document.createElement("Source");
        src.setAttribute("value", source);
        Element transactions = this.document.createElement("Transactions");
        Element sum = this.document.createElement("Sum");

        src.appendChild(transactions);
        src.appendChild(sum);
        root.appendChild(sum);

        return src;
    }

    public List<Transaction.Currency> getSum(tring source){

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

        System.out.println("document: " + document);
        System.out.println("element: " + elem);
        Element Transactions = (Element) elem.getElementsByTagName("Transactions").item(0);

        Element ntransaction = document.createElement("Transaction");
        ntransaction.setAttribute("datetime", Long.toString(System.currentTimeMillis()));
        Element description = document.createElement("description");
        description.appendChild(document.createTextNode(transaction.information));
        Element amount = document.createElement("amount");

        /*get the sum*/
        Element sum = (Element) elem.getElementsByTagName("Sum").item(0);

        //supposed to contain all available currencies
        NodeList currencies = sum.getChildNodes();
        HashMap<String, Integer> currency_sums = new HashMap<String, Integer>();

        //get currencies in sum
        for(int i=0; i<currencies.getLength(); i++){

            try{

                String type = ((Element) currencies.item(i)).getAttribute("type");
                int namount = Integer.parseInt(((Element) currencies.item(i)).getAttribute("int"));
                currency_sums.put(type, namount);
            }catch(Exception e){

                System.out.println(currencies.item(i));
            }
        }

        for(Transaction.Currency entry : transaction.amount){
                Element currency = document.createElement("currency");

                currency.setAttribute("type", entry.coinType);
                currency.setAttribute("int", Integer.toString(entry.inte));
                amount.appendChild(currency);

                if(!currency_sum.containsKey) sum.appendChild(currency);

                currency_sums.put(entry.coinType, currency_sums.containsKey(entry.coinType) ?
                                    currency_sums.get(entry.coinType) : 0
                                    + entry.inte);
        }

        //save sum
        for(int i=0; i < currencies.getLength(); i++){
            try{
                Element current = (Element) currencies.item(i);
                current.setAttribute("int", Integer.toString(currency_sums.get(current.getAttribute("type"))));
            }catch(Exception e){

                System.out.println("bad currency: " + currencies.item(i));
                e.printStackTrace();
            }
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
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(source, result);
        }catch(Exception e){

            e.printStackTrace();
        }
    }

    public Transaction[] getTransactions(String source, int start, int end){

        Element source_elem = getSource(source);
        NodeList list = source_elem.getElementsByTagName("Transaction");

        Transaction[] transactions = new Transaction[end - start];
        for(int i=start; i < end; i++) transactions[i - start] = Transaction.fromElement((Element) list.item(i));

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
