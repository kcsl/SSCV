package com.kcsl.sscv;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.kcsl.sscv.library.Formatter;

/*
 * SSCV 4
 * 
 * This code is based on an flight plan management application. A user can add list of airports and flights connecting them and the application displays them in a matrix format.
 * 
 * Question : Is there a side channel in space which enables the attacker to find out number of airports in the matrix being generated.
 *
 * Patterns covered - Pattern 4
 */

public class SSCV_4 {

	private static final int CELL_WIDTH = 10;
	private Formatter cellFormatter;
	
	public Map<String,String> generateMatrix(List<Airport> airports, List<Flight> flights) {
		Formatter f = new Formatter(CELL_WIDTH);
		Map<String,String> map = new HashMap<String,String>();
		map.put("rows", generateRows(airports, flights));
		return map;
	}
	
	public String generateRows(List<Airport> airports, List<Flight> flights) {
        Map<String, String> map = new HashMap<>();
        Map<Airport, Integer> airportToFlightLimit = new LinkedHashMap<>();
		for (int a = 0; a < airports.size(); a++) {
			new Matrix(airports, map, airportToFlightLimit, a).invoke();
		}
		return map.toString();
	}
	
	private String format(String data){
        return cellFormatter.format(data, CELL_WIDTH, Formatter.Justification.RIGHT, false);
    }

	private class Matrix {
		private List<Airport> airports;
        private Map<String, String> map;
        private Map<Airport, Integer> airportToFlightLimit;
        private int a;
		
		public Matrix(List<Airport> airports, Map<String, String> map,
				Map<Airport, Integer> airportToFlightLimit, int a) {
			this.airports = airports;
            this.map = map;
            this.airportToFlightLimit = airportToFlightLimit;
            this.a = a;
		}

		public void invoke() {
			Airport airport = airports.get(a);
            map.clear();

            Map<Airport, Integer> copy = new LinkedHashMap<>(airportToFlightLimit);
            map.put("cells", generateOneRow(airport, copy));
		}
		
		public String generateOneRow(Airport origin, Map<Airport, Integer> airportToFlightLimit) {
			
			for (Airport airport : airportToFlightLimit.keySet()) {
                map.clear();
                String numFlights = Integer.toString(airportToFlightLimit.get(airport));
                map.put("cell", format(numFlights));
			}
			
			return map.toString();
			
		}
	}
	
	class Airport {
		
	}
	
	class Flight {
		
	}
}
