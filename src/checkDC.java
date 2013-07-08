import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;


public class checkDC {
	public static void main(String[] args) {
		String donorsChooseDataLoc = "/Users/allensogis-hernandez/projects/donorsChoose/data/donorschoose-org-1may2011-v1-projects.csv";
		File donorsChooseData = new File(donorsChooseDataLoc);
		
		//declare and fill a hashmap with key/value pairs denoting the school district
		//and how many times it appears in the dc database
		HashMap<String, Integer> dcCounts = new HashMap<String, Integer>();
		try {
			CSVReader reader = new CSVReader(new FileReader(donorsChooseData));
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				String districtName = nextLine[10];
				if (dcCounts.containsKey(districtName)) {
					int count = dcCounts.get(districtName);
					count++;
					dcCounts.put(districtName, count);
				} else {
					dcCounts.put(districtName, 1);
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			List<String> acsDistricts = importACSData();
			writeOutHashMap(dcCounts, acsDistricts);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * iterate through hashmap and write out all data using the writeData method
	 * 
	 * @param map
	 */
	public static void writeOutHashMap(HashMap<String, Integer> map, List<String> acs) {
		Iterator<Entry<String, Integer>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> pairs = (Entry<String, Integer>) it.next();
			String district = pairs.getKey();
			int occurenceCount = pairs.getValue();
			String[] toWrite = new String[4];
			toWrite[0] = String.valueOf(occurenceCount);
			toWrite[1] = district;
			String filteredDistrict = filterData(district);
			toWrite[2] = filteredDistrict;
			if (acs.contains(filteredDistrict)) {
				toWrite[3] = "1";
			} else {
				toWrite[3] = "0";
			}
			try {
				writeData(toWrite);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Create and return a List containing every district from the ACS file,
	 * also splits the ACS District into two pieces to get rid of the state
	 * abbreviation attached to it.
	 * 
	 * @return a List containing every ACS School District 
	 * @throws IOException
	 */
	public static List<String> importACSData() throws IOException {
		String acsDataLoc = "/Users/allensogis-hernandez/projects/donorsChoose/data/ACS0711_SDU.csv";
		File acsData = new File(acsDataLoc);
		List<String> acsDistricts = new ArrayList<String>();
		CSVReader reader = new CSVReader(new FileReader(acsData));
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			String acsDistrict = nextLine[2];
			String[] acsPieces = acsDistrict.split(",");
			acsDistrict = acsPieces[0];
			if (!acsDistricts.contains(acsDistrict)) {
				acsDistricts.add(acsDistrict);
			} else {
				//skip
			}
		}
		reader.close();
		return acsDistricts;
	}
	
	/**
	 * accept a string, split it into pieces and test each individual
	 * piece and transalte it if necessary
	 * 
	 * @param toTest
	 * @return converted string=
	 */
	public static String filterData(String toTest) {
		String[] pieces = toTest.split(" ");
		for (int i=0; i<pieces.length; i++) {
			String test = pieces[i];
			if (test.equals("Dist")) {
				pieces[i] = "District";
			} else if (test.equals("Sch")) {
				pieces[i] = "School";
			} else if(test.equals("Co")) {
				pieces[i] = "County";
			} else if(test.equals("Ind")) {
				pieces[i] = "Independent";
			} else if(test.equals("Cmty")) {
				pieces[i] = "Community";
			} else if(test.equals("Elem")) {
				pieces[i] = "Elementary";
			} else if(test.equals("Twp")) {
				pieces[i] = "Township";
			} else if(test.equals("Hts")) {
				pieces[i] = "Heights";
			} else if(test.equals("Ctl")) {
				pieces[i] = "Central";
			} else if(test.equals("Mt")) {
				pieces[i] = "Mount";
			} else if(test.equals("SD")) {
				pieces[i] = "School District";
			} else if(test.equals("Sd")) {
				pieces[i] = "School District";
			} else if(test.equals("Pub")) {
				pieces[i] = "Public";
			} else if(test.equals("Ft")) {
				pieces[i] = "Fort";
			} else if(test.equals("Unif")) {
				pieces[i] = "Unified";
			} else if(test.equals("Msd")) {
				pieces[i] = "Metropolitan School District";
			} else if(test.equals("Ctr")) {
				pieces[i] = "Center";
			} else if(test.equals("Cons")) {
				pieces[i] = "Consolidated";
			} else if(test.equals("Vlg")) {
				pieces[i] = "Village";
			} else if(test.equals("St")) {
				pieces[i] = "Saint";
			}
		}
		String reassembled = "";
		for (String s: pieces) {
			reassembled += s + " ";
		}
		reassembled = reassembled.trim();
		
		return reassembled;
	}
	
	/**
	 * write an array of data to a local file
	 * @param data
	 * @throws IOException
	 */
	public static void writeData(String[] data) throws IOException {
		File dcMiniKey = new File("/Users/allensogis-hernandez/projects/donorsChoose/data/dcMiniKey.tsv");
		CSVWriter writer = new CSVWriter(new FileWriter(dcMiniKey, true), '\t');
		writer.writeNext(data);
		writer.close();
	}
}
