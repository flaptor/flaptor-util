/*
Copyright 2008 Flaptor (flaptor.com) 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

    http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License.
*/

package com.flaptor.util.sort;

import java.util.Vector;

/**
 * Sort an array of Objects using Quicksort.
 * 
 * This code is based on the code found in the book
 * "Developing Java Software" by Winder and Roberts
 */
public class QuicksortVector {

    /**
     *  The sort operation.
     *  @param data the data to be sorted.
     *  @param comp the Comparator used to establish the sort order.
     */ 
    public static void sort (Vector data, Comparator comp) {
        quicksort(data, 0, data.size()-1, comp);
    }

    /**
     *  Given the array and two indices, swap the two items in the  array.
     */
    @SuppressWarnings("unchecked")
    private static void swap (Vector data, int a, int b) {
        Object temp = data.elementAt(a);
        data.setElementAt(data.elementAt(b), a);
        data.setElementAt(temp, b);
    }

    /**
     *  Partition an array in two using the pivot value that is at the
     *  center of the array being partitioned.
     *  @param data the array out of which to take a slice.
     *  @param lower the lower bound of this slice.
     *  @param upper the upper bound of this slice.
     *  @param comp the Comparator to be used to define the order.
     */
    private static int partition(Vector data, int lower, int upper, Comparator comp) {
        Object pivotValue = data.elementAt((upper+lower+1)/2);
        while (lower <= upper) {
            while (comp.compare(data.elementAt(lower), pivotValue) < 0) {
                lower++;
            }
            while (comp.compare(pivotValue, data.elementAt(upper)) < 0) {
                upper--;
            }
            if (lower <= upper) {
                if (lower < upper) {
                    swap(data, lower, upper);
                }
                lower++;
                upper--;
            }
        }
        return upper;
    }

    /**
     *  The recursive Quicksort function.
     *  @param data the array out of which to take a slice.
     *  @param lower the lower bound of this slice.
     *  @param upper the upper bound of this slice.
     *  @param comp the Comparator to be used to define the order.
     */
    private static void quicksort(Vector data, int lower, int upper, Comparator comp) {
        int sliceLength = upper-lower+1;
        if (sliceLength > 1) {
            if (sliceLength == 2) {
                if (comp.compare(data.elementAt(upper),data.elementAt(lower)) < 0) {
                    swap (data, lower, upper);
                }
            } else {
                int pivotIndex = partition(data, lower, upper, comp);
                quicksort(data, lower, pivotIndex, comp);
                quicksort(data, pivotIndex+1, upper, comp);
            }
        }
    }

}

