package utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractPrice {
    public static String extractPrice(String text) {
        String price = null;
        if(text != null) {
            // Search the text for a price
            Matcher matcher = Pattern.compile("\\d+\\.\\d{2}\\b").matcher(text);
            while (matcher.find()) {
                // If this is the second match found, return null
                if (price != null) {
                    return null;
                }
                // Else, this is the first match found so set the price
                else {
                    price = matcher.group();
                }
            }
        }
        return price;
    }
}