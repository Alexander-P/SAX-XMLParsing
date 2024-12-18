
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

      htmlWriter.write("<html><head><title>Takeout Document</title></head><body>");

      DefaultHandler handler = new DefaultHandler() {
        private StringBuilder currentValue = new StringBuilder();
        private Map<String, String> dishMap = new HashMap<>();
        private Map<String, String> personnelMap = new HashMap<>();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
          currentValue.setLength(0);
          try {
            handleTakeoutStart(qName, attributes);
            handleOfferedDishesStart(qName, attributes);
            handlePersonnelStart(qName, attributes);
            handleOrdersStart(qName, attributes);
          } catch (IOException e) {
            throw new SAXException(e);
          }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
          currentValue.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
          try {
            handleNameEnd(qName);
            handleTransportEnd(qName);
            handleAddressEnd(qName);
            handleGenericEnd(qName);
          } catch (IOException e) {
            throw new SAXException(e);
          }
        }

        private void handleTakeoutStart(String qName, Attributes attributes) throws IOException {
          if ("takeout".equals(qName)) {
            htmlWriter.write("<h1>Takeout Document</h1>");
          }
        }

        private void handleOfferedDishesStart(String qName, Attributes attributes) throws IOException {
          switch (qName) {
            case "offered-dishes":
              htmlWriter.write("<h2>Menu</h2><ul>");
              break;
            case "dish":
              String dishId = attributes.getValue("id");
              String dishName = attributes.getValue("name");
              dishMap.put(dishId, dishName);
              htmlWriter.write("<li>" + dishName + " (Vegetarian: " + attributes.getValue("vegetarian") + ")</li>");
              break;
          }
        }

        private void handlePersonnelStart(String qName, Attributes attributes) throws IOException {
          switch (qName) {
            case "personnel":
              htmlWriter.write("<h2>Personnel</h2><ul>");
              break;
            case "person":
              String personId = attributes.getValue("id");
              personnelMap.put(personId, null);
              htmlWriter.write("<li>ID: " + personId + "<br>");
              break;
          }
        }

        private void handleOrdersStart(String qName, Attributes attributes) throws IOException {
          switch (qName) {
            case "current-orders":
              htmlWriter.write("</ul><h2>Orders</h2>");
              break;
            case "order":
              htmlWriter.write("<h3>Order ID: " + attributes.getValue("order-id") + "</h3><ul>");
              break;
            case "address":
              String deliveredBy = attributes.getValue("deliveredBy");
              String driverName = personnelMap.getOrDefault(deliveredBy, "Unknown Driver");
              htmlWriter.write("<li>Delivered By: " + driverName + " (ID: " + deliveredBy + ")</li>");
              break;
            case "self-pickup":
              htmlWriter.write("<li>Self-Pickup (Client Name: " + attributes.getValue("client-name") + ")</li>");
              break;
            case "item":
              String itemDishId = attributes.getValue("dish");
              String itemDishName = dishMap.getOrDefault(itemDishId, "Unknown Dish");
              htmlWriter.write("<li>Dish: " + itemDishName + ", Price: " + attributes.getValue("price") + "</li>");
              break;
          }
        }

        private void handleNameEnd(String qName) throws IOException {
          if ("name".equals(qName)) {
            String name = currentValue.toString().trim();
            personnelMap.entrySet().stream()
                .filter(entry -> entry.getValue() == null)
                .findFirst()
                .ifPresent(entry -> personnelMap.put(entry.getKey(), name));
            htmlWriter.write(name + "<br>");
          }
        }

        private void handleTransportEnd(String qName) throws IOException {
          if ("transport".equals(qName)) {
            htmlWriter.write("Transport: " + currentValue.toString().trim() + "</li>");
          }
        }

        private void handleAddressEnd(String qName) throws IOException {
          if ("address".equals(qName)) {
            htmlWriter.write("<li>Address: " + currentValue.toString().trim() + "</li>");
          }
        }

        private void handleGenericEnd(String qName) throws IOException {
          switch (qName) {
            case "order":
            case "personnel":
              htmlWriter.write("</ul>");
              break;
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