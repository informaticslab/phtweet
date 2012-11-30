package org.phiresearchlab.twitter.client;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author <a href="mailto:jmrives@spiral-soft.com">Joel M. Rives</a>
 * Created on Aug 3, 2011
 *
 */
public class ColorMapper
{
    private static ColorMapper INSTANCE = null;
    
    public static ColorMapper getInstance() {
        if (null == INSTANCE)
            INSTANCE = new ColorMapper();
        return INSTANCE;
    }
    
    private Map<String, String> colorToHex = new HashMap<String, String>();
    private Map<String, String> hexToColor = new HashMap<String, String>();
    private Map<String, String> hexToFaded = new HashMap<String, String>();
    
    // This is private because we want a singleton
    private ColorMapper() {
        colorToHex.put("teal", "#0DAAB4");
        colorToHex.put("pink", "#C0077C");
        colorToHex.put("gold", "#E8AA0D");
        colorToHex.put("yellow_green", "#7AA105");
        colorToHex.put("blue", "#4072D7");
        colorToHex.put("orange", "#D46205");
        colorToHex.put("dk_green", "#028411");
        colorToHex.put("red", "#B50411");
        colorToHex.put("gray", "#53514C");
        colorToHex.put("purple", "#6F0CB4");
        colorToHex.put("brown", "#785C2B");
        colorToHex.put("bright_green", "#08B919");

        hexToColor.put("#0DAAB4", "teal");
        hexToColor.put("#C0077C", "pink");
        hexToColor.put("#E8AA0D", "gold");
        hexToColor.put("#7AA105", "yellow_green");
        hexToColor.put("#4072D7", "blue");
        hexToColor.put("#D46205", "orange");
        hexToColor.put("#028411", "dk_green");
        hexToColor.put("#B50411", "red");
        hexToColor.put("#53514C", "gray");
        hexToColor.put("#6F0CB4", "purple");
        hexToColor.put("#785C2B", "brown");
        hexToColor.put("#08B919", "bright_green");
        
        hexToFaded.put("#0DAAB4", "#E2F5F6");
        hexToFaded.put("#C0077C", "#F7E1EF");
        hexToFaded.put("#E8AA0D", "#FCF9E2");
        hexToFaded.put("#7AA105", "#EFF4E1");
        hexToFaded.put("#4072D7", "#E8EEFA");
        hexToFaded.put("#D46205", "#FAECE1");
        hexToFaded.put("#028411", "#E0F0E2");
        hexToFaded.put("#B50411", "#F6E0E2");
        hexToFaded.put("#53514C", "#EAEAEA");
        hexToFaded.put("#6F0CB4", "#EDE1F6");
        hexToFaded.put("#785C2B", "#EFEBE5");
        hexToFaded.put("#08B919", "#E1F6E3");
    }
    
    public String getColor(String hex) {
        return hexToColor.get(hex);
    }
    
    public String getFaded(String hex) {
    	return hexToFaded.get(hex);
    }
    
    public String getHex(String color) {
        return colorToHex.get(color);
    }
    
}
