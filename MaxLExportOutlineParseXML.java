package outlineReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import java.util.concurrent.TimeUnit;

/***
 * 
 * @author Harry Gates
 * @email harry.gates@gmail.com
 * @blog http://harrygates-essbase-blog.blogspot.com

Usage
 * 1) Use the MaxL "export outline" command to export a dimension of the outline
 *   export outline "/path_to_otl/Sample.otl" list dimensions {"Account"} with alias_table "Default" to xml_file "/path_to_save_xml/sample_account_dim.xml";
 * 2) Change the delimiter, inputFile, and outputFile
 * 3) Compile and run MaxLExportOutlineParseXML.java
 */
public class MaxLExportOutlineParseXML {
	static String currentElement = "";
	static String sDimension = "";
	static String delimiter;
	static Member mbr = null;
	static QName qMbrName = new QName("name");
	static QName qMbrNameRef = new QName("nameRef");
	static Stack<String> parents = new Stack<String>();
	static String inputXMLFile;
	static String outputFile;
	static String header = "";
	static int udaCountTotal = 0;
	
	public static void main(String[] args) {
		inputXMLFile = "/Users/harry/parseMaxLXML/AlrgBs1_Customer.xml";
		outputFile = "/Users/harry/parseMaxLXML/AlrgBs1_Customer1.txt";
		delimiter = "?";
		
		long startTime = System.currentTimeMillis();
		
		convertXML();
		
		long finishTime = System.currentTimeMillis();
	    String diff = millisToShortDHMS(finishTime - startTime);
	    System.out.println("outline parse took: " + diff);
	}

	private static void convertXML() {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		inputFactory.setProperty("javax.xml.stream.isCoalescing", true);
		
		try {
			InputStream in = new FileInputStream(inputXMLFile);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

			FileWriter output = new FileWriter(outputFile);
			
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					currentElement = startElement.getName().toString();

					if (currentElement.equals("Dimension")) {
						sDimension = startElement.getAttributeByName(qMbrName).getValue();
						parents.push(sDimension);
						header = "PARENT0," + sDimension + delimiter + "CHILD0," + sDimension + delimiter + "ALIAS0," + sDimension
							     + delimiter + "PROPERTY0," + sDimension + delimiter + "FORMULA0," + sDimension + delimiter;
					} else if (currentElement.equals("AttributeDimension")) {
						header += startElement.getAttributeByName(qMbrNameRef).getValue() + "0," + sDimension + delimiter;
					} else if (currentElement.equals("Member")) {
						String mbrName = startElement.getAttributeByName(qMbrName).getValue();
						
						if (mbr != null) {
							output.append(mbr.toString());
							output.append("\n");
							udaCountTotal = mbr.countUDA() > udaCountTotal ? mbr.countUDA() : udaCountTotal;
						}
						
						mbr = new Member(mbrName, delimiter);
						if (!parents.isEmpty())
							mbr.setParent(parents.peek());

						
						parents.push(mbrName);
						mbr = setMemberAttributes(startElement, mbr);
						
					} else if (currentElement.equals("AttributeMember")) {
						mbr = setAttributeMembers(startElement, mbr);
					}
				}
				if (event.isEndElement()) {
					currentElement = "";
					
					if (!parents.isEmpty()) {
						if (event.asEndElement().getName().toString().equals("Member"))
							parents.pop();
						
						// get last member
						if (event.asEndElement().getName().toString().equals("Dimension"))
							output.append(mbr.toString());
					}
				}
				if (event.isCharacters()) {
					if (currentElement.equals("Alias")) {
						if (mbr != null)
							mbr.setAlias(event.asCharacters().getData());
					} else if (currentElement.equals("UDA")) {
						if (mbr != null)
							mbr.addUDA(event.asCharacters().getData());
					}
				}
			}
			output.flush();
			output.close();
			
		    String udaHeader = "";
		    for (int i = 0; i < udaCountTotal; i++) {
		     udaHeader += "UDA0," + sDimension + delimiter;
		    }
		    System.out.println(header += udaHeader);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static Member setMemberAttributes(StartElement startElement, Member mbr) {
		@SuppressWarnings("unchecked")
		Iterator<Attribute> it1 = startElement.getAttributes();
		while (it1.hasNext()) {
			Attribute attribute = it1.next();
			String nodName = attribute.getName().toString();
			if (nodName.equals("Consolidation")) {
				mbr.setConsolidation(attribute.getValue());
			} else if (nodName.equals("DataStorage")) {
				mbr.setDataStorage(attribute.getValue());
			} else if (nodName.equals("MemberFormula")) {
				mbr.setMemberFormula(attribute.getValue());
			} else if (nodName.equals("TwoPassCalc")) {
				mbr.setTwoPassCalc(attribute.getValue());
			} else if (nodName.equals("TimeBalance")) {
				mbr.setTimeBalance(attribute.getValue());
			} else if (nodName.equals("VarianceReporting")) {
				mbr.setVarianceReporting(attribute.getValue());
			} else {
				// System.out.println(nodName + ": " + attribute.getValue());
			}
		}
		return mbr;
	}

	private static Member setAttributeMembers(StartElement startElement, Member mbr) {
		@SuppressWarnings("unchecked")
		Iterator<Attribute> it1 = startElement.getAttributes();
		while (it1.hasNext()) {
			Attribute attribute = it1.next();
			if (attribute.getName().toString().equals("name"))
				mbr.addAttribute(attribute.getValue());
		}
		return mbr;
	}
	
	/**
	   * converts time (in milliseconds) to human-readable format
	   *  "<dd:>hh:mm:ss"
	   */
	  public static String millisToShortDHMS(long duration) {
	    String res = "";
	    long days  = TimeUnit.MILLISECONDS.toDays(duration);
	    long hours = TimeUnit.MILLISECONDS.toHours(duration)
	                   - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
	    long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
	                     - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
	    long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
	                   - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
	    if (days == 0) {
	      res = String.format("%02d:%02d:%02d", hours, minutes, seconds);
	    }
	    else {
	      res = String.format("%dd%02d:%02d:%02d", days, hours, minutes, seconds);
	    }
	    return res;
	  }
	
}