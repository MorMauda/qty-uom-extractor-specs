package com.omnius.challenges.extractors.qtyuom.utils;

import com.omnius.challenges.extractors.qtyuom.QtyUomExtractor;
import com.omnius.challenges.extractors.qtyuom.utils.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements {@link QtyUomExtractor} identifying as <strong>the most relevant UOM</strong> the leftmost UOM found in the articleDescription.
 * The {@link UOM} array contains the list of valid UOMs. The algorithm search for the leftmost occurence of UOM[i], if there are no occurrences then tries UOM[i+1].
 * 
 * Example
 * <ul>
 * <li>article description: "black steel bar 35 mm 77 stck"</li>
 * <li>QTY: "77" (and NOT "35")</li>
 * <li>UOM: "stck" (and not "mm" since "stck" has an higher priority as UOM )</li>
 * </ul>
 *
 * @author <a href="mailto:damiano@searchink.com">Damiano Giampaoli</a>
 * @since 4 May 2018
 */
public class LeftMostUOMExtractor implements QtyUomExtractor {

    /**
     * Array of valid UOM to match. the elements with lower index in the array has higher priority
     */
    //QTY - regex :

    private final static String QTY_REGEX= "(^|"+THOUSAND_SEPARATOR[0]+")+[0-9]{1,3}(("+THOUSAND_SEPARATOR[1]+"|" + THOUSAND_SEPARATOR[0]+")"+
            "[0-9]{3})*(" +THOUSAND_SEPARATOR[0] +"*("+DECIMAL_SEPARATOR[0]+"|"+DECIMAL_SEPARATOR[1]+")"
            +THOUSAND_SEPARATOR[0]+"*[0-9]+)?";;
    public static String[] UOM = {"stk", "stk.", "stck", "stück", "stg", "stg.", "st", "st.", "stange", "stange(n)",
            "tafel", "tfl", "taf", "mtr", "meter", "qm", "kg", "lfm", "mm", "m"};
    
    public LeftMostUOMExtractor() {}
    
    @Override
    public Pair<String, String> extract(String articleDescription) {

        //mock implementation
        if (articleDescription == null) {
            return null;
        }

        String matchingString="";

        for (int i = 0; i < UOM.length ; i++) {
            // (?i)--> makes what comes ofter to case insensitive
            String pairRegex = QTY_REGEX + THOUSAND_SEPARATOR[0] + "*(?i)" + UOM[i] + "("+THOUSAND_SEPARATOR[0]+"+|$)";
            Pattern p = Pattern.compile(pairRegex);
            Matcher m = p.matcher(articleDescription);
            if (m.find()) {
                matchingString = m.group();
                //Get the index that the QTY ends  - 1 to remove the extra space::
                int endIndex = matchingString.length() - 1 - UOM[i].length()-1;
                //Get the wanted String and remove whitespace
                matchingString = matchingString.substring(0, endIndex).replace(" ", "");

                return new Pair<>(matchingString ,UOM[i]);
            }
        }

        return null;
        }

    @Override
    public Pair<Double, String> extractAsDouble(String articleDescription) {
        Pair<String,String> p = extract(articleDescription);
        return new Pair<Double, String>(Double.parseDouble(p.getFirst()),p.getSecond());
    }


}
