import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;

public class SAXParsing {

  private static final String COURSE_XML = "course.xml";
  private static final String TAKEOUT_XML = "takeout.xml";


  public static void main(String[] args) {
    basicExampleUse();
    System.out.println();
    takeoutExampleUse();
  }

  public static void basicExampleUse() {
    try {
      // Create a SAXParserFactory instance
      SAXParserFactory factory = SAXParserFactory.newInstance();

      // Create a SAXParser
      SAXParser saxParser = factory.newSAXParser();

      // Specify the XML file to parse
      File inputFile = new File(COURSE_XML);

      // Define the handler
      DefaultHandler handler = new DefaultHandler() {
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
          System.out.println("startElement: " + qName);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
          System.out.println("characters: " + new String(ch, start, length).trim());
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
          System.out.println("endElement: " + qName);
        }
      };

      // Parse the file
      saxParser.parse(inputFile, handler);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void takeoutExampleUse() {
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
      File inputFile = new File(TAKEOUT_XML);

      DefaultHandler handler = new DefaultHandler() {
        private StringBuilder currentValue = new StringBuilder();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
          currentValue.setLength(0);
          // Handle dish element and its attributes
          if (qName.equals("dish")) {
            System.out.println("Dish: " + attributes.getValue("name") + " (Vegetarian: " + attributes.getValue("vegetarian") + ")");
            // Handle person element to display ID
          } else if (qName.equals("person")) {
            System.out.println("Person ID: " + attributes.getValue("id"));
            // Handle order element to display order ID
          } else if (qName.equals("order")) {
            System.out.println("Order ID: " + attributes.getValue("order-id"));
            // Handle item element and display its details
          } else if (qName.equals("item")) {
            System.out.println("  Item: " + attributes.getValue("dish") + ", Price: " + attributes.getValue("price"));
          }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
          currentValue.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
          // Handle address content within an order
          if (qName.equals("address")) {
            System.out.println("  Address: " + currentValue.toString().trim());
            // Handle name content for personnel or clients
          } else if (qName.equals("name") && !currentValue.toString().trim().isEmpty()) {
            System.out.println("  Name: " + currentValue.toString().trim());
            // Handle transport content for personnel
          } else if (qName.equals("transport")) {
            System.out.println("  Transport: " + currentValue.toString().trim());
          }
        }
      };

      saxParser.parse(inputFile, handler);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
