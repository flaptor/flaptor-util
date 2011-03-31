package com.flaptor.util.timeplot;

import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.flaptor.util.DomUtil;
import com.google.common.collect.Ordering;

/**
 * This class consists exclusively of static methods that can be used
 * to work with the TimePlot javascript API
 * 
 * @author Ignacio Perez (iperez)
 *
 */
public class TimePlotUtils {
    
    /**
     * Generate the String input for TimePlots. The source data is a map from dates to numeric values, where the
     * dates are the time checkpoints and the numbers are the values of the diferent columns to plot.<br> 
     * The lists of numbers represent the values for each column and hence all entries must have lists with the 
     * same size and the columns must be in the same order.<br>
     * The dataset will be generated in the javascript inline format unless a different <code>rowSeparator</code> 
     * (for instance: a "\n" to output to a data file) is specified.  
     *
     * When using this method, the dataset will be generated in the javascript inline format using the "\n" string 
     * (not an enter) as a separator.   
     *  
     * @param source A {@link Map} from {@link Date} to {@link List<Number>} with the values for each checkpoint.  
     * @param precisionDecimals The number of decimals to preseve for non integer numbers 
     * @return The String dataset
     */
    public static String generateInputDate(Map<Date, List<Number>> source) {
        return generateInputDate(source, 5, "\\n");
    }
    
    /**
     * Generate the String input for TimePlots. The source data is a map from dates to numeric values, where the
     * dates are the time checkpoints and the numbers are the values of the diferent columns to plot.<br> 
     * The lists of numbers represent the values for each column and hence all entries must have lists with the 
     * same size and the columns must be in the same order.<br>
     * The dataset will be generated in the javascript inline format unless a different <code>rowSeparator</code> 
     * (for instance: a "\n" to output to a data file) is specified.<br>
     * 
     * When using this method, the non integer values will preserve up to 5 decimals in its string representation.  
     * 
     * @param source A {@link Map} from {@link Date} to {@link List<Number>} with the values for each checkpoint.  
     * @param rowSeparator The string that will be put between rows (depending on the type of output, a different separator can be used) 
     * @return The String dataset
     */
    public static String generateInputDate(Map<Date, List<Number>> source, String rowSeparator) {
        return generateInputDate(source, 5, rowSeparator);
    }
        
    /**
     * Generate the String input for TimePlots. The source data is a map from dates to numeric values, where the
     * dates are the time checkpoints and the numbers are the values of the diferent columns to plot.<br> 
     * The lists of numbers represent the values for each column and hence all entries must have lists with the 
     * same size and the columns must be in the same order.<br>
     * 
     * @param source A {@link Map} from {@link Date} to {@link List<Number>} with the values for each checkpoint.  
     * @param precisionDecimals The number of decimals to preseve for non integer numbers 
     * @param rowSeparator The string that will be put between rows (depending on the type of output, a different separator can be used) 
     * @return The String dataset
     */
    public static String generateInputDate(Map<Date, List<Number>> source, int precisionDecimals, String rowSeparator) {
        if (precisionDecimals < 0) {
            throw new IllegalArgumentException("Argument precisionDecimals must be 0 or greater.");
        }
        
        char[] decimals = new char[precisionDecimals];
        Arrays.fill(decimals, '#');
        String pattern = "0" + (precisionDecimals == 0 ? "" : ".") + new String(decimals);
        
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        DateFormat dateFormat = getDateFormat();
        
        List<Date> dates = Ordering.natural().sortedCopy(source.keySet());
        
        StringBuffer buffer = new StringBuffer();
        Integer columns = null;
        
        for (Date date : dates) {
            List<Number> values = source.get(date);
            int columnsForDate = values.size();
            
            if (columns == null) {
                columns = columnsForDate;            
            }
            
            if (!columns.equals(columnsForDate)) {
                throw new IllegalArgumentException("The data for " + dateFormat.format(date) + " has " + columnsForDate + " columns and it is a " + columns + " columns dataset");
            }
            
            String valuesRepresentation = "";
            for (Number value : values) {
                valuesRepresentation += ",";
                valuesRepresentation += decimalFormat.format(value);
            }
            
            buffer.append(dateFormat.format(date) + valuesRepresentation + rowSeparator);            
        }
        
        return buffer.toString();
    }

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
    }



    public static String generateEventXml(List<EventInfo> events) {
    
        DateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss 'GMT'");

        Document dom = DocumentHelper.createDocument();
        Element root = dom.addElement("data");
        for (EventInfo event: events) {
            if (event.isNull()) {
                //logger.warn("got null eventInfo .. weird");
            }
            Element el = root.addElement("event");
            if (null != event.getEventStart()) {
                el.addAttribute("start",dateFormat.format(event.getEventStart()));
            }
            if (null != event.getEventEnd()) {
                el.addAttribute("end",dateFormat.format(event.getEventEnd()));
                el.addAttribute("isDuration","true");
            }else {
                el.addAttribute("isDuration","false");
            }
            if (null != event.getTitle()) {
                el.addAttribute("title",event.getTitle());
            }
            if (null != event.getLink()) {
                el.addAttribute("link",event.getLink().toString());
            }
            if (null != event.getDescription()){
                el.setText(event.getDescription());
            }

        }

        return DomUtil.domToString(dom).replaceAll("\\n","");
    }






    // TODO write javadoc
    public static class EventInfo {
    
        private Date eventStart;
        private Date eventEnd;
        private URL link;
        private String title;
        private String description;

        public EventInfo(){
        }


        public void setEventStart(Date date){
            this.eventStart = date;
        }
        
        public void setEventEnd(Date date){
            this.eventEnd = date;
        }
    
        public void setLink(URL url) {
            this.link = url;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setDescription (String desc) {
            this.description = desc;
        }

        public boolean isNull() {
            return (null == eventStart  &&
                    null == eventEnd    &&
                    null == link        &&
                    null == title       &&
                    null == description);
        }

        public Date getEventStart(){
            return eventStart;
        }
        public Date getEventEnd(){
            return eventEnd;
        }
        public String getDescription(){
            return description;
        }
        public String getTitle(){
            return title;
        }
        public URL getLink(){
            return link;
        }


    }


}
