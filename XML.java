import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

//just copied most of it from tutorial's point
public class XML{

    Element root;
    public XML() throws Exception{

        Document document;
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new File("records.xml"));
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

    public static void main(String[] args){
        try{
            XML xml = new XML();
            System.out.println(xml.getSources());
        }catch(Exception e){

            System.out.println(e);
        }
    }
}
