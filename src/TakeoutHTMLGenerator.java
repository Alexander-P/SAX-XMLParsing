import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TakeoutHTMLGenerator {

  private static final String TAKEOUT_XML = "takeout.xml";

  public static void generateTakeoutHTML() {
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
      File inputFile = new File(TAKEOUT_XML);
      FileWriter htmlWriter = new FileWriter("takeout.html");

      htmlWriter.write("<html><head><title>Takeout Menu and Orders</title></head><body>");

      Map<String, String> dishMap = new HashMap<>(); // Map to store dish ID and names
      Map<String, String> delivererMap = new HashMap<>(); // Map to store deliverer ID and names

      DefaultHandler handler = new DefaultHandler() {
        private StringBuilder currentValue = new StringBuilder();
        private boolean inMenu = false;
        private boolean inOrders = false;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
          currentValue.setLength(0);
          if (qName.equals("offered-dishes")) {
            inMenu = true;
            try {
              htmlWriter.write("<h2>Menu</h2><ul>");
            } catch (IOException e) {
              throw new SAXException(e);
            }
          } else if (qName.equals("dish")) {
            String dishId = attributes.getValue("id");
            String dishName = attributes.getValue("name");
            dishMap.put(dishId, dishName);
            try {
              htmlWriter.write("<li>" + dishName + " (Vegetarian: " + attributes.getValue("vegetarian") + ")</li>");
            } catch (IOException e) {
              throw new SAXException(e);
            }
          } else if (qName.equals("person")) {
            String personId = attributes.getValue("id");
            String personName = null;
          } else if (qName.equals("current-orders")) {
            inOrders = true;
            try {
              htmlWriter.write("<h2>Orders</h2>");
            } catch (IOException e) {
              throw new SAXException(e);
            }
          } else if (qName.equals("order")) {
            try {
              htmlWriter.write("<h3>Order ID: " + attributes.getValue("order-id") + "</h3><ul>");
            } catch (IOException e) {
              throw new SAXException(e);
            }
          } else if (qName.equals("item")) {
            String dishId = attributes.getValue("dish");
            String dishName = dishMap.getOrDefault(dishId, "Unknown Dish");
            try {
              htmlWriter.write("<li>Dish: " + dishName + ", Price: " + attributes.getValue("price") + "</li>");
            } catch (IOException e) {
              throw new SAXException(e);
            }
          } else if (qName.equals("address")) {
            String deliveredBy = attributes.getValue("deliveredBy");
            String delivererName = delivererMap.getOrDefault(deliveredBy, "Unknown Deliverer");
            try {
              htmlWriter.write("<li>Delivery Address: " + currentValue.toString() + " (Delivered By: " + delivererName + ")</li>");
            } catch (IOException e) {
              throw new SAXException(e);
            }
          } else if (qName.equals("self-pickup")) {
            try {
              htmlWriter.write("<li>Self-Pickup by: " + attributes.getValue("client-name") + "</li>");
            } catch (IOException e) {
              throw new SAXException(e);
            }
          }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
          currentValue.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
          if (qName.equals("offered-dishes")) {
            inMenu = false;
            try {
              htmlWriter.write("</ul>");
            } catch (IOException e) {
              throw new SAXException(e);
            }
          } else if (qName.equals("order")) {
            try {
              htmlWriter.write("</ul>");
            } catch (IOException e) {
              throw new SAXException(e);
            }
          }
        }
      };

      saxParser.parse(inputFile, handler);

      htmlWriter.write("</body></html>");
      htmlWriter.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
