import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;

public class SAXParsing {
  public static void main(String[] args) {
    try {
      // Create a SAXParserFactory instance
      SAXParserFactory factory = SAXParserFactory.newInstance();

      // Create a SAXParser
      SAXParser saxParser = factory.newSAXParser();

      // Specify the XML file to parse
      File inputFile = new File("course.xml");

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
}