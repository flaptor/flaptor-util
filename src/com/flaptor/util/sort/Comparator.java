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

