import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONObject;

public class RestaurantXMLProcessor {

  private static final String EX6_XML = "ex6.xml";
  private static final String EX6_OUT_JSON = "ex6-out.json";

  public static void processRestaurantXML() {
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
      File inputFile = new File(EX6_XML);

      DefaultHandler handler = new DefaultHandler() {
        private int menuElementCount = 0;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
          if (qName.equals("dish") || qName.equals("drink")) {
            menuElementCount++;
          }
        }

        @Override
        public void endDocument() throws SAXException {
          try (FileWriter file = new FileWriter(EX6_OUT_JSON)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("menuElementCount", menuElementCount);
            file.write(jsonObject.toString(4)); // Pretty print with an indent of 4
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      };

      saxParser.parse(inputFile, handler);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
