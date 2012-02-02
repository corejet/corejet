package org.corejet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Read out the numbers from a string as longs and then.  If there are no numbers sort alphabetically;
 * This won't do things like 7a then 7b
 * Also won't work for things like "Scenario 2 do something 4 times" 
 */
@SuppressWarnings("rawtypes")
public class AlphanumComparator implements Comparator {

    
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object raw1, Object raw2)
    {
        if (!(raw1 instanceof String) || !(raw2 instanceof String))
        {
            return 0;
        }
        String string1 = (String)raw1;
        String string2 = (String)raw2;

       	Long number1 = getNumber(string1);
       	Long number2 = getNumber(string2);
       	
       	if (null!=number1 && null!=number2){
       		return number1.compareTo(number2);
       	} else {
       		return string1.compareToIgnoreCase(string2);
       	}
    }

	private Long getNumber(String string) {
		List<String> numbers = new ArrayList<String>();
       	StringBuilder numberBuilder = new StringBuilder();
       	
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(string); 
        while (matcher.find()) {
        	numbers.add(matcher.group());
        }
        for (String number : numbers) {
			numberBuilder.append(number);
		}
        try {    	
        	return Long.parseLong(numberBuilder.toString());
        } catch (NumberFormatException e) {
			return null;
		}
	}
}
