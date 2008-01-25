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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.flaptor.hist4j.AdaptiveHistogram;

/**
 * class for keeping statistics of events
 * 
 * @author Martin Massera
 */
public class Statistics {
	private static Statistics instance = new Statistics(60000);
	
	//returns a static instance that updates every minute 
	public static Statistics getStatistics() {
		return instance;
	}
	
	private Map<String, Pair<EventStats,EventStats>> eventStatistics = new HashMap<String, Pair<EventStats,EventStats>>();
	private int periodLength;
	
	Statistics(int periodLength) {
		this.periodLength = periodLength;
        new Timer().schedule(new StatisticsTask(), 0, periodLength);
	}

	public void notifyEventValue(String eventName, float value) {
		Pair<EventStats,EventStats> eventStats = getOrCreateStats(eventName);
		eventStats.first().addSample(value);
		eventStats.last().addSample(value);		
	}

	public void notifyEventError(String eventName) {
		Pair<EventStats,EventStats> eventStats = getOrCreateStats(eventName);
		eventStats.first().addError();
		eventStats.last().addError();		
	}

	private Pair<EventStats,EventStats> getOrCreateStats(String eventName) {
		Pair<EventStats,EventStats> eventStats = eventStatistics.get(eventName);
		if (eventStats == null) {
			eventStats = new Pair<EventStats,EventStats>(new EventStats(), new EventStats());
			eventStatistics.put(eventName, eventStats);
		}
		return eventStats;
	}

	public Pair<EventStats,EventStats> getStats(String eventName) {
		return eventStatistics.get(eventName);
	}

	public EventStats getThisPeriodStats(String eventName) {
		return eventStatistics.get(eventName).first();
	}

	public EventStats getAccumulatedStats(String eventName) {
		return eventStatistics.get(eventName).last();
	}

	public EventStats clearAccumulatedStats(String eventName) {
		return eventStatistics.get(eventName).last();
	}

	public int getPeriodLength() {
		return periodLength;
	}
	
	public static class EventStats implements Serializable {
		public long numCorrectSamples;
		public long numErrors;
		public float errorRatio;

		public float sampleAverage;
		
		public float maximum;
		public float minimum;
		
		AdaptiveHistogram histogram = new AdaptiveHistogram();
		
		public EventStats() {
			clear();
		}
		
		private synchronized void clear() {
			numErrors = 0;
			numCorrectSamples = 0;
			errorRatio = 0;
			sampleAverage = 0;
			maximum= -100000000000000.0f;
			minimum= 100000000000000.0f;
			histogram.reset();
		}
		
		private synchronized void addSample(float value) {
			numCorrectSamples++;
			sampleAverage = (numCorrectSamples * sampleAverage + value) / numCorrectSamples ;
			
			if (value < minimum) minimum = value;
			if (value > maximum) maximum = value;

			histogram.addValue(value);
			
            errorRatio = numErrors / (numErrors + numCorrectSamples);
		}

		private synchronized void addError() {
			numErrors++;
			errorRatio = numErrors / (numErrors + numCorrectSamples);
		}
	}
	
	private class StatisticsTask extends TimerTask {
		public void run() {
			for (Map.Entry<String, Pair<EventStats, EventStats>> entry : eventStatistics.entrySet()) {
				entry.getValue().first().clear();
			}
		}
	}
}
