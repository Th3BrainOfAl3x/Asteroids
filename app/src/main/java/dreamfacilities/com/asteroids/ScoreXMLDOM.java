package dreamfacilities.com.asteroids;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by alex on 01/12/16.
 */

public class ScoreXMLDOM implements ScoreStore {
    private static String FILE = "scores.xml";
    private Context context;
    private Document document;
    private boolean documentLoaded;

    public ScoreXMLDOM(Context contexto) {
        this.context = contexto;
        documentLoaded = false;
    }

    @Override
    public void saveScores(int points, String name, long date) {
        try {
            if (!documentLoaded) {
                readXML(context.openFileInput(FILE));
            }
        } catch (FileNotFoundException e) {
            createXML();
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
        nuevo(points, name, date);
        try {
            escribirXML(context.openFileOutput(FILE, Context.MODE_PRIVATE));
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
    }

    @Override
    public Vector<String> scoresList(int amount) {
        try {
            if (!documentLoaded) {
                readXML(context.openFileInput(FILE));
            }
        } catch (FileNotFoundException e) {
            createXML();
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
        return toVectorString();
    }

    public void createXML() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder constructor = factory.newDocumentBuilder();
            document = constructor.newDocument();
            Element root = document.createElement("score_list");
            document.appendChild(root);
            documentLoaded = true;
        } catch (Exception e) {
            Log.e("Asteroids", e.getMessage(), e);
        }
    }

    public void readXML(InputStream input) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder constructor = factory.newDocumentBuilder();
        document = constructor.parse(input);
        documentLoaded = true;
    }

    public void nuevo(int points, String name, long date) {
        Element score = document.createElement("score");
        score.setAttribute("date", String.valueOf(date));

        Element el_name = document.createElement("name");
        Text txt = document.createTextNode(name);
        el_name.appendChild(txt);
        score.appendChild(el_name);

        Element el_points = document.createElement("points");
        txt = document.createTextNode(String.valueOf(points));
        el_points.appendChild(txt);
        score.appendChild(el_points);

        Element raiz = document.getDocumentElement();
        raiz.appendChild(score);
    }

    public Vector<String> toVectorString() {
        Vector<String> result = new Vector<String>();
        String name = "", points = "";

        Element root = document.getDocumentElement();
        NodeList scores = root.getElementsByTagName("score");
        for (int i = 0; i < scores.getLength(); i++) {
            Node puntuacion = scores.item(i);
            NodeList propiedades = puntuacion.getChildNodes();
            for (int j = 0; j < propiedades.getLength(); j++) {
                Node property = propiedades.item(j);
                String label = property.getNodeName();
                if (label.equals("name")) {
                    name = property.getFirstChild().getNodeValue();
                } else if (label.equals("points")) {
                    points = property.getFirstChild().getNodeValue();
                }
            }
            result.add(name + " " + points);
        }
        return result;
    }

    public void escribirXML(OutputStream output) throws Exception {

        /* > API 8
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformador = factory.newTransformer();
            transformador.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformador.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(output);
            transformador.transform(source, result);
         */

        String s = serialize(document.getDocumentElement());
        output.write(s.getBytes("UTF-8"));
    }

    public static String serialize(Node root) throws IOException {

        StringBuilder result = new StringBuilder();

        if (root.getNodeType() == Node.TEXT_NODE)
            result.append(root.getNodeValue());
        else {
            if (root.getNodeType() != Node.DOCUMENT_NODE) {
                StringBuffer attributes = new StringBuffer();

                for (int i = 0; i < root.getAttributes().getLength(); ++i) {
                    attributes.append(" ")
                            .append(root.getAttributes().item(i).getNodeName())
                            .append("=\"")
                            .append(root.getAttributes().item(i).getNodeValue())
                            .append("\" ");
                }
                result.append("<").append(root.getNodeName()).append(" ").append(attributes).append(">");
            } else {
                result
                        .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            }
            NodeList nodesList = root.getChildNodes();
            for (int i = 0; i < nodesList.getLength(); i++) {
                Node node = nodesList.item(i);
                result.append(serialize(node));
            }
            if (root.getNodeType() != Node.DOCUMENT_NODE) {
                result.append("</").append(root.getNodeName()).append(">");
            }
        }

        return result.toString();

    }
}

