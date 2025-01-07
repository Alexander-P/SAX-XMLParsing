import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RestaurantXMLProcessor {

  private static final String EX6_XML = "ex6.xml";
  private static final String EX6_OUT_JSON = "ex6-out.json";

  public static void processRestaurantXML() {
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
      File inputFile = new File(EX6_XML);

      DefaultHandler handler = new RestaurantHandler();
      saxParser.parse(inputFile, handler);

    } catch (
        Exception e) {
      e.printStackTrace();
    }
  }


  private static class RestaurantHandler extends DefaultHandler {
    private int menuElementCount = 0;
    private String restaurantName = "";
    private String city = "";
    private String country = "";
    private int openDaysCount = 0;
    private String longestDay = "";
    private long longestDuration = 0;
    private double totalDishPrice = 0.0;
    private int dishCount = 0;
    private double mostExpensiveDish = Double.MIN_VALUE;
    private double cheapestDish = Double.MAX_VALUE;
    private final Map<String, String> dayOpeningHours = new HashMap<>();
    private String currentDay = null;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      switch (qName) {
        case "restaurant":
          restaurantName = attributes.getValue("name");
          break;
        case "dish":
          processDish(attributes);
          break;
        case "drink":
          menuElementCount++;
          break;
        case "city":
          city = attributes.getValue("value");
          break;
        case "country":
          country = attributes.getValue("value");
          break;
        case "day":
          processDay(attributes);
          break;
      }
    }

    private void processDish(Attributes attributes) {
      menuElementCount++;
      double price = Double.parseDouble(attributes.getValue("price"));
      totalDishPrice += price;
      dishCount++;
      mostExpensiveDish = Math.max(mostExpensiveDish, price);
      cheapestDish = Math.min(cheapestDish, price);
    }

    private void processDay(Attributes attributes) {
      String dayName = attributes.getValue("name");
      String closed = attributes.getValue("closed");
      if (!"true".equals(closed)) {
        openDaysCount++;
        currentDay = dayName;
      }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      String content = new String(ch, start, length).trim();
      if (!content.isEmpty() && currentDay != null) {
        dayOpeningHours.put(currentDay, content);
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      if (qName.equals("day")) {
        currentDay = null;
      }
    }

    @Override
    public void endDocument() throws SAXException {
      try (FileWriter file = new FileWriter(EX6_OUT_JSON)) {
        calculateLongestDay();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("restaurantName", restaurantName);
        jsonObject.put("menuElementCount", menuElementCount);
        jsonObject.put("city", city);
        jsonObject.put("country", country);
        jsonObject.put("openDaysCount", openDaysCount);
        jsonObject.put("longestDay", longestDay);
        jsonObject.put("averageDishPrice", dishCount > 0 ? totalDishPrice / dishCount : 0.0);
        jsonObject.put("mostExpensiveDish", mostExpensiveDish);
        jsonObject.put("cheapestDish", cheapestDish);
        file.write(jsonObject.toString(4)); // Pretty print with an indent of 4
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private void calculateLongestDay() {
      SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
      for (Map.Entry<String, String> entry : dayOpeningHours.entrySet()) {
        String day = entry.getKey();
        String hours = entry.getValue();
        if (hours != null) {
          String[] splitHours = hours.split(" - ");
          try {
            Date start = format.parse(splitHours[0]);
            Date end = format.parse(splitHours[1]);
            long duration = end.getTime() - start.getTime();
            if (duration > longestDuration) {
              longestDuration = duration;
              longestDay = day;
            }
          } catch (ParseException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
}
