package com.ef;

import java.util.HashMap;

/**
 * The {@code Parser} class provides the functionality to parse a log file, load
 * the data to database and checks if a given IP makes more than a certain
 * number of requests for the given duration.
 * <p>
 * 
 * @author Pavan Reddy
 * @version 1.0
 */
public class Parser {

	/**
	 * Method takes the request parameters {@code arts} received from the
	 * command line and processes the functionality to load the data to database
	 * and check if a given IP makes more than a certain number of requests for
	 * the given duration.
	 * 
	 * Expected command line format java -cp "parser.jar" com.ef.Parser
	 * --accesslog=/path/to/file --startDate=2017-01-01.13:00:00
	 * --duration=hourly --threshold=100
	 * 
	 * 
	 * @param args
	 *            command line arguments
	 **/
	public static void main(String[] args) {

		if (args.length == 4) {
			HashMap<String, String> inputParams = new HashMap<>();
			// Process the parameters into key-value pairs
			try {
				inputParams.put(Constants.ACCESS_LOG, args[0].split(Constants.EQUALS)[1]);
				inputParams.put(Constants.START_DATE, args[1].split(Constants.EQUALS)[1]);
				inputParams.put(Constants.DURATION, args[2].split(Constants.EQUALS)[1]);
				inputParams.put(Constants.THRESHOLD, args[3].split(Constants.EQUALS)[1]);
				DataProcessor dataLoader = new DataProcessor();
				// Process the access log data
				dataLoader.processData(inputParams);
			} catch (ArrayIndexOutOfBoundsException e) {
				printInvalidFormatMesage();
			}
		} else {
			printInvalidFormatMesage();
		}
	}

	/**
	 * Prints invalid input arguments message to the console
	 **/
	public static void printInvalidFormatMesage() {
		System.out.println("An error occured while trying to parse your request arguments");
		System.out.println("Expected format");
		System.out.println(
				"--accesslog=<Path to the file> --startDate=<Date> --duration=<Duration> --threshold=<Threshold>");
	}

}
