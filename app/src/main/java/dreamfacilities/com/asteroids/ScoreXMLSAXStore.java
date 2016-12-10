package dreamfacilities.com.asteroids;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by alex on 01/12/16.
 */

public class ScoreXMLSAXStore implements ScoreStore {
    private static String FILE = "scores.xml";
    private Context context;
    private ScoresList list;
    private boolean listLoaded;

    public ScoreXMLSAXStore(Context contexto) {
        this.context = contexto;
        list = new ScoresList();
        listLoaded = false;
    }

    @Override
    public void saveScores(int points, String name, long date) {
        try {
            if (!listLoaded) {
                list.readXML(context.openFileInput(FILE));
            }
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            Log.e("Asteroids", e.getMessage(), e);
        }
        list.nuevo(points, name, date);
        try {
            list.writeXML(context.openFileOutput(FILE, Context.MODE_PRIVATE));
        } catch (Exception e) {
            Log.e("Asteroids", e.getMessage(), e);
        }
    }

    @Override
    public Vector<String> scoresList(int amount) {
        try {
            if (!listLoaded) {
                list.readXML(context.openFileInput(FILE));
            }
        } catch (Exception e) {
            Log.e("Asteroids", e.getMessage(), e);
        }
        return list.toVectorString();
    }

    public class ScoresList {
        private class Score {
            int points;
            String name;
            long date;
        }

        private List<Score> scoreList;

        public ScoresList() {
            scoreList = new ArrayList<Score>();
        }

        public void nuevo(int points, String name, long date) {
            Score score = new Score();
            score.points = points;
            score.name = name;
            score.date = date;
            scoreList.add(score);
        }

        public Vector<String> toVectorString() {
            Vector<String> result = new Vector<String>();
            for (Score score : scoreList) {
                result.add(score.name + " " + score.points);
            }
            return result;
        }


        public void readXML(InputStream input) throws Exception {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            HandlerXML handlerXML = new HandlerXML();
            reader.setContentHandler(handlerXML);
            reader.parse(new InputSource(input));
            listLoaded = true;
        }

        public void writeXML(OutputStream salida) {
            XmlSerializer serializador = Xml.newSerializer();
            try {
                serializador.setOutput(salida, "UTF-8");
                serializador.startDocument("UTF-8", true);
                serializador.startTag("", "score_list");
                for (Score score : scoreList) {
                    serializador.startTag("", "score");
                    serializador.attribute("", "date",
                            String.valueOf(score.date));
                    serializador.startTag("", "name");
                    serializador.text(score.name);
                    serializador.endTag("", "name");
                    serializador.startTag("", "points");
                    serializador.text(String.valueOf(score.points));
                    serializador.endTag("", "points");
                    serializador.endTag("", "score");
                }
                serializador.endTag("", "score_list");
                serializador.endDocument();
            } catch (Exception e) {
                Log.e("Asteroids", e.getMessage(), e);
            }
        }

        class HandlerXML extends DefaultHandler {
            private StringBuilder str;
            private Score score;

            @Override
            public void startDocument() throws SAXException {
                scoreList = new ArrayList<Score>();
                str = new StringBuilder();
            }

            @Override
            public void startElement(String uri, String localName, String nameQualif, Attributes atr) throws SAXException {
                str.setLength(0);
                if (localName.equals("score")) {
                    score = new Score();
                    score.date = Long.parseLong(atr.getValue("date"));
                }
            }

            @Override
            public void characters(char ch[], int start, int lon) {
                str.append(ch, start, lon);
            }

            @Override
            public void endElement(String uri, String localName, String qualifName) throws SAXException {
                if (localName.equals("score")) {
                    score.points = Integer.parseInt(str.toString());
                } else if (localName.equals("name")) {
                    score.name = str.toString();
                } else if (localName.equals("score")) {
                    scoreList.add(score);
                }
            }

            @Override
            public void endDocument() throws SAXException {
            }
        }
    }
}

