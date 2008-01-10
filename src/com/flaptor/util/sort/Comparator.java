package com.flaptor.util.sort;

/**
 * This interface defines a comparator so items can be sorted
 */
public interface Comparator {

    /**
     * The relation that this Comparator represents.
     * @param a The first object to be compared.
     * @param b The object to be compared against.
     * @return &lt;0 if a&lt;b; &gt;0 if a&gt;b; 0 if a==b;
     */    
    public int compare (Object a, Object b);

}

