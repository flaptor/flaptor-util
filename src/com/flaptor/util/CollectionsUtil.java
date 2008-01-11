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

package com.flaptor.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

/**
 * Utility methods for Collections API
 * 
 * @author santip
 *
 */
public class CollectionsUtil {

    /**
     * Merges two sorted lists of comparable elements
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> List<T> mergeLists(List<? extends T> list1, List<? extends T> list2) {
        return CollectionsUtil.<T>mergeLists(list1, list2, CollectionsUtil.<T>naturalComparator());
    }

    /**
     * Merges two sorted lists of elements with a given comparator
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> List<T> mergeLists(List<? extends T> list1, List<? extends T> list2, Comparator<? super T> comparator) {
        return CollectionsUtil.<T>mergeLists(Arrays.asList(list1, list2), comparator);
    }

    /**
     * @return a comparator that provides the natural ordering 
     */
    public static <T extends Comparable<T>> Comparator<T> naturalComparator() {
        return new Comparator<T>() {
            public int compare(T o1, T o2) {
                return o1.compareTo(o2);
            }
        };
    }

    /**
     * Merges a list of sorted lists of comparable elements
     */
    public static <T extends Comparable<T>> List<T> mergeLists(List<? extends List<? extends T>> sources) {
        List<T> merged = new ArrayList<T>();
        CollectionsUtil.<T>mergeLists(sources, merged, CollectionsUtil.<T>naturalComparator());
        return merged;
    }

    /**
     * Merges a list of sorted lists of elements with a given comparator
     */
    public static <T> List<T> mergeLists(List<? extends List<? extends T>> sources, Comparator<? super T> comparator) {
        List<T> merged = new ArrayList<T>();
        CollectionsUtil.<T>mergeLists(sources, merged, comparator);
        return merged;
    }

    /**
     * Merges a list of sorted lists of comparable elements into a target list
     */
    public static <T extends Comparable<T>> void mergeLists(List<? extends List<? extends T>> sources, List<? super T> target) {
        CollectionsUtil.<T>mergeLists(sources, target, CollectionsUtil.<T>naturalComparator());
    }

    /**
     * Merges a list of sorted lists of elements with a given comparator into a target list
     */
    public static <T> void mergeLists(List<? extends List<? extends T>> sources, List<? super T> target, Comparator<? super T> comparator) {
        int size = 0;
        List<ListIterator<? extends T>> iterators = new ArrayList<ListIterator<? extends T>>(sources.size());

        for (List<? extends T> source : sources) {
            size += source.size();
            iterators.add(source.listIterator());
        }

        for (int i = 0; i < size; i++) {
            T min = null;
            int minIndex = -1;
            for (int j = 0; j < iterators.size(); j++) {
                ListIterator<? extends T> it = iterators.get(j);
                if (it.hasNext()) {
                    T t = it.next();
                    if (minIndex == -1 || comparator.compare(t, min) < 0) {
                        minIndex = j;
                        min = t;
                    }
                    it.previous();
                }
            }
            target.add(min);
            iterators.get(minIndex).next();
        }
    }



}
