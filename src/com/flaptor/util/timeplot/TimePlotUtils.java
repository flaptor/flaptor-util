package com.flaptor.util.timeplot;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class TimePlotUtils {

    public static String generateInputDate(Map<Date, List<Number>> data, int precisionDecimals, String rowSeparator) {
        return generateInputDate(data, precisionDecimals, false, rowSeparator);
    }
    
    public static String generateInputDate(Map<Date, List<Number>> data, boolean invertedOrder, String rowSeparator) {
        return generateInputDate(data, 5, invertedOrder, rowSeparator);
    }
    
    public static String generateInputDate(Map<Date, List<Number>> data, String rowSeparator) {
        return generateInputDate(data, 5, false, rowSeparator);
    }
    
    
    public static String generateInputDate(Map<Date, List<Number>> data, int precisionDecimals, final boolean invertedOrder, String rowSeparator) {
        if (precisionDecimals < 0) {
            throw new IllegalArgumentException("Argument precisionDecimals must be 0 or greater.");
        }
        
        if (rowSeparator == null) {
            rowSeparator = "\\n";
        }
        
        char[] decimals = new char[precisionDecimals];
        Arrays.fill(decimals, '#');
        String pattern = "0" + (precisionDecimals == 0 ? "" : ".") + new String(decimals);
        
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        List<Date> dates = Lists.sortedCopy(data.keySet(), new Comparator<Date>() {
            public int compare(Date date1, Date date2) {
                int result = date1.compareTo(date2);
                if (invertedOrder) {
                    result = - result;
                }
                return result;
            }
        });
        
        StringBuffer buffer = new StringBuffer();
        Integer columns = null;
        
        for (Date date : dates) {
            List<Number> values = data.get(date);
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
            
            buffer.append(dateFormat.format(date) + valuesRepresentation + "\\n");            
        }
        
        return buffer.toString();
    }
    
}
