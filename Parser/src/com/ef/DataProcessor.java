package com.ef;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.ef.entity.AccessLog;
import com.ef.entity.BlockedIP;
import com.ef.util.HibernateUtil;

/**
 * The {@code DataProcessor} class provides the functionality to read a log
 * file, load the data to database and checks if a given IP makes more than a
 * certain number of requests for the given duration.
 * <p>
 * 
 * @author Pavan Reddy
 * @version 1.0
 */
public class DataProcessor {

	// Hibernate session
	private Session session;

	public DataProcessor() {
		session = HibernateUtil.getSessionFactory().openSession();
	}

	/**
	 * Method takes the request parameters {@code inputParams} received from the
	 * command line and processes the functionality to load the data to database
	 * and check if a given IP makes more than a certain number of requests for
	 * the given duration.
	 * 
	 * @param inputParams
	 *            command line arguments
	 **/
	public void processData(HashMap<String, String> inputParams) {
		HashMap<String, Integer> ipAddressMap = new HashMap<>();

		// Process each line in the access log file
		Transaction transaction = session.beginTransaction();
		String filePath = inputParams.get(Constants.ACCESS_LOG);
		System.out.println("Request Recieved : Trying to process the file " + filePath);
		System.out.println(Constants.LINES);
		System.out.println("Processing each entry in the file: This might take a while...");
		try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
			stream.forEach(currentLine -> {
				try {
					// Save the access log record
					session.save(getAccessLogData(currentLine, getStartDatePlusDuration(inputParams), ipAddressMap));
				} catch (IOException e) {
					// Roll back the transaction in case of failure
					transaction.rollback();
					System.out.println("An error occured while trying to process one of the access log records");
				}
			});
		} catch (IOException e) {
			// Roll back the transaction in case of failure
			transaction.rollback();
			System.out.println("An error occured while trying to read the access log file and storing data");
		}
		System.out.println("Successfuly processed all the entry(s)");
		System.out.println(Constants.LINES);
		Integer threshold = Integer.valueOf(inputParams.get(Constants.THRESHOLD));
		System.out.println("Trying to get all IP addresses which have made more than " + threshold + " requests...");
		for (String ipAddress : ipAddressMap.keySet()) {
			if (ipAddressMap.get(ipAddress) > threshold) {
				// Save the access log record
				session.save(new BlockedIP(ipAddress, "Blocked: Made more than " + threshold + " calls"));
				System.out.println(ipAddress + " Blocked: Made more than " + threshold + " calls");
			}
		}
		System.out.println(Constants.LINES);
		transaction.commit();
	}

	/**
	 * The method would create a map containing the start and end date based on
	 * the duration input parameter
	 * 
	 * @param inputParams
	 *            command line arguments
	 **/
	private HashMap<String, Date> getStartDatePlusDuration(HashMap<String, String> inputParams) {
		HashMap<String, Date> durationDateMap = new HashMap<>();
		Date startDate = parseDate(inputParams.get(Constants.START_DATE), Constants.CMD_DATEFORMAT);
		durationDateMap.put(Constants.DURATION_START_DATE, startDate);
		Calendar calendarObj = Calendar.getInstance();
		calendarObj.setTime(startDate);
		// Add 1 hour if the duration is hourly
		if (inputParams.get(Constants.DURATION).contentEquals(Constants.HOURLY)) {
			calendarObj.add(Calendar.HOUR, 1);
			durationDateMap.put(Constants.DURATION_END_DATE, calendarObj.getTime());
		} else {
			// Otherwise add 24
			calendarObj.add(Calendar.HOUR, 24);
			durationDateMap.put(Constants.DURATION_END_DATE, calendarObj.getTime());
		}
		return durationDateMap;
	}

	/**
	 * The method would process the log entry data and sets the data into a
	 * {@code AccessLog} entity
	 * 
	 * @param currentLine
	 *            log entry
	 * @param durationDates
	 *            range entered in command line
	 **/
	public AccessLog getAccessLogData(String currentLine, HashMap<String, Date> durationDates,
			HashMap<String, Integer> ipAddressMap) throws IOException {
		// Get the pipe separated log entry details
		String[] logEntryDetails = currentLine.split(Constants.ESCAPED_PIPE);
		if (logEntryDetails.length == 5) {
			AccessLog accessData = new AccessLog();
			Date startDate = parseDate(logEntryDetails[0], Constants.ACCESSLOG_DATEFORMAT);
			// Set the log entry data to the entity object
			accessData.setDate(startDate);
			accessData.setIp(logEntryDetails[1]);
			accessData.setRequest(logEntryDetails[2]);
			accessData.setResponseCode(logEntryDetails[3]);
			accessData.setDeviceMetadata(logEntryDetails[4]);
			// Post-process the IP details
			processIPData(logEntryDetails[1], durationDates, startDate, ipAddressMap);
			return accessData;
		} else {
			throw new IOException();
		}
	}

	/**
	 * The method would process the log entry IP data and stores the IPs
	 * matching the time range requested
	 * 
	 * @param ipAddress
	 *            ip address being processed
	 * @param durationDates
	 *            range entered in command line
	 * @param actualStartDate
	 *            request date of the ip
	 **/
	public void processIPData(String ipAddress, HashMap<String, Date> durationDate, Date actualStartDate,
			HashMap<String, Integer> ipAddressMap) {
		// Filter based on the time range
		if (actualStartDate.after(durationDate.get(Constants.DURATION_START_DATE))
				&& actualStartDate.before(durationDate.get(Constants.DURATION_END_DATE))) {
			Integer count = ipAddressMap.get(ipAddress);
			if (null == count) {
				ipAddressMap.put(ipAddress, 1);
			} else {
				ipAddressMap.put(ipAddress, count + 1);
			}
		}
	}

	/**
	 * This method would parse a given string date based on the format
	 * {@code dateFormat} and returns an object of {@code Date}
	 * 
	 * @param dateString
	 *            Date in string format
	 * @param dateFormat
	 *            Format of the date string
	 * @return Date
	 */
	public static Date parseDate(String dateString, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
		ParsePosition pp = new ParsePosition(0);
		return sdf.parse(dateString, pp);
	}

}
