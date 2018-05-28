package com.example.customersupportapp.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.asprise.ocr.Ocr;
import com.example.customersupportapp.component.SampleProperty;

@RestController
public class PdfReaderController {				
	JSONObject completeJsonObject = new JSONObject();
	String completeXmlString = "";
	Object table = null;
	Object block = null;
	Object page = null;
	JSONObject tableObject;
	
	String company="";
	String referenceNumber="";
	String dateCode="";
	String tableStart="";
	String tableEnd="";
	
	List<String> companyName = new ArrayList<>();
	List<String> companyNameInCaps = new ArrayList<>();
	List<String> listReferenceNumber = new ArrayList<>();
	List<String> listDateCode = new ArrayList<>();
	List<String> listTableStart = new ArrayList<>();
	List<String> listTableEnd = new ArrayList<>();
	
	 @Autowired
	 private SampleProperty sampleProperty;
	 	
	@RequestMapping(value="/readPdf", produces=MediaType.APPLICATION_JSON_VALUE)
	private Map<String, String> checkOCR() throws IOException, JSONException {
			Map<String, String> check = new HashMap<>();
			JSONArray data = new JSONArray();
			JSONArray finalArray = new JSONArray();
			JSONArray dataRequired = new JSONArray();
			JSONArray invoiceData = new JSONArray();
			String[] filePath={"C:/Users/sheka/Desktop/veena/SITA UK LTD R-20906224.tif","C:/Users/sheka/Desktop/veena/SG_170425_00032_EUR_5.pdf","C:/Users/sheka/Desktop/veena/40296.30 Herbert Temmel.pdf"};			
			for(int i=0;i<filePath.length;i++){				
				JSONObject inrObj = new JSONObject();
				finalArray = checkOCROne(filePath[i]);		
				fetchValues();
				dataRequired = extractDataBetweenTwoElements(sortJsonArray(finalArray, "y"), tableStart, tableEnd);
				System.out.println("finalArray::::::::::::::::::::::::"+sortJsonArray(finalArray, "y"));
				System.out.println("dataRequired::::::::::::::::::::::::"+dataRequired);
				invoiceData=extractInvoices(dataRequired);
				System.out.println(invoiceData);	
				inrObj.put("invoiceData", invoiceData);				
				inrObj.put("company", company);
				inrObj.put("referenceNumber", referenceNumber);
				inrObj.put("dateCode", dateCode);
				System.out.println(inrObj);
				data.put(inrObj);
				/*check.put("invoiceData", invoiceData.toString());
				check.put("company", company);
				check.put("referenceNumber", referenceNumber);
				check.put("date", dateCode);*/
			}
			check.put("data", data.toString());
			return check;
		}
	 
	/* To Check Company Name whether exists in the pdf or not */
//	@RequestMapping(value="/fetch", produces=MediaType.APPLICATION_JSON_VALUE)
	public void fetchValues() throws IOException, JSONException{
		
		companyName = sampleProperty.getListProp();
		companyNameInCaps = sampleProperty.getListPropInCaps();
		listReferenceNumber = sampleProperty.getListReferenceNumber();
		listDateCode = sampleProperty.getListDateCode();
		listTableStart = sampleProperty.getListTableStart();
		listTableEnd = sampleProperty.getListTableEnd();
		
		String completeObject = completeJsonObject.toString().toLowerCase();
		String completeObjectOriginal = completeJsonObject.toString();
		

		for(int i = 0; i < companyName.size(); i++) {
//			System.out.println(companyName.get(i));
			if(completeObject.contains(companyName.get(i))) {
				System.out.println("value::; " +completeObject.contains(companyName.get(i)) + " Company Name : " + companyNameInCaps.get(i));
				
				company = companyNameInCaps.get(i);
				referenceNumber = listReferenceNumber.get(i);
				dateCode = listDateCode.get(i);
				tableStart = listTableStart.get(i);
				tableEnd = listTableEnd.get(i);
				break;
			}
		}
		referenceNumber = fetchDataAfterTheCodeValue(referenceNumber, completeObjectOriginal);
		dateCode = fetchDataAfterTheCodeValue(dateCode, completeObjectOriginal);
		
	}

	private JSONArray extractInvoices(JSONArray dataRequired) throws JSONException {
		JSONArray invoices = new JSONArray();
		boolean flag=true;
		int x=0;
		if(flag){
			for(int counter=0;counter<dataRequired.length();counter++){
				x=(int) dataRequired.getJSONObject(counter).get("x");
				flag=false;
				break;
			}
		}	
		for(int counter=0;counter<dataRequired.length();counter++){
			int y=(int) dataRequired.getJSONObject(counter).get("x");
			if((Math.abs(x-y)<20)){
				invoices.put(dataRequired.getJSONObject(counter).get("content"));
			}
		}
		return invoices;
	}

	private JSONArray extractDataBetweenTwoElements(JSONArray finalArray, String string1, String string2) throws JSONException {
		String string3 ="";
		JSONArray dataBetweenTwoWords = new JSONArray();
		boolean flag = false;
		for(int finalarraycounter=0;finalarraycounter<finalArray.length();finalarraycounter++){
			string3=String.valueOf(finalArray.getJSONObject(finalarraycounter).get("content"));
			if(string3.toLowerCase().contains(string1.toLowerCase())){
				flag=true;
			}	
			if(string3.toLowerCase().contains(string2.toLowerCase())){
				flag=false;
			}
			if(flag){
				dataBetweenTwoWords.put(finalArray.getJSONObject(finalarraycounter));
			}
		}
		return dataBetweenTwoWords;
	}

	private JSONArray checkOCROne(String filePath) throws IOException, JSONException {
		JSONArray listOne = new JSONArray();
		JSONArray listTwo = new JSONArray();
		JSONArray tableJsonArray = new JSONArray();	
		Ocr.setUp(); // one time setup
		Ocr ocr = new Ocr(); // create a new OCR engine
		ocr.startEngine("eng", Ocr.SPEED_FASTEST); // English

		completeXmlString = ocr.recognize(new File[] {new File(filePath)}, Ocr.RECOGNIZE_TYPE_ALL, Ocr.OUTPUT_FORMAT_XML);		
		// completeJsonObject has full data converted from Xml to json
		completeJsonObject = XML.toJSONObject(completeXmlString);	
		System.out.println("Cpmplete Object::"+completeJsonObject);
		page = completeJsonObject.getJSONObject("asprise-ocr").get("page");
		
		if (page instanceof JSONArray) {
		    // It's an array
			page = completeJsonObject.getJSONObject("asprise-ocr").getJSONArray("page");
			for(int pageCounter=0;pageCounter<completeJsonObject.getJSONObject("asprise-ocr").getJSONArray("page").length();pageCounter++){
				if(completeJsonObject.getJSONObject("asprise-ocr").getJSONArray("page").getJSONObject(pageCounter).has("table")){
					table = identifyTheTablesWithTwoPages(completeJsonObject.getJSONObject("asprise-ocr").getJSONArray("page").getJSONObject(pageCounter));
					if (table instanceof JSONArray) {
					    // It's an array	
						tableJsonArray = (JSONArray)table;								
						listOne = retrieveDataFromTable(tableJsonArray, "array");
					}
					else if (table instanceof JSONObject) {
					    // It's an object
						tableObject = (JSONObject)table;
						listOne = retrieveDataFromTable(tableObject, "object");
					}else{
						System.out.println("table is neighter object nor array");
					}	
				}
				if((completeJsonObject.getJSONObject("asprise-ocr").getJSONArray("page").getJSONObject(pageCounter).has("block"))){
					listTwo = (JSONArray) identifyTheBlocks(completeJsonObject);		
					//listOne.put(arrangeTableCells((JSONArray) block));
				}
			}
		}else{
			System.out.println("Page is not an Array");
		}
		
		if (page instanceof JSONObject) {
		    // It's an object
			if(completeJsonObject.getJSONObject("asprise-ocr").getJSONObject("page").has("table")){
				table = identifyTheTables(completeJsonObject);
				if (table instanceof JSONArray) {
				    // It's an array	
					listOne = retrieveDataFromTable(table, "array");		
					
				}
				else if (table instanceof JSONObject) {
				    // It's an object
					tableObject = (JSONObject)table;
					listOne = retrieveDataFromTable(tableObject, "object");
				}else{
					System.out.println("table is neighter object nor array");
				}	
			}
			
			if(completeJsonObject.getJSONObject("asprise-ocr").getJSONObject("page").has("block")){
				listTwo = (JSONArray) identifyTheBlocks(completeJsonObject);
				//System.out.println(block);
				//listOne.put(arrangeTableCells((JSONArray) block));
			}
			
		}else{
			System.out.println("Page is not an object");
		}
		ocr.stopEngine();
		return getMergeJson(listOne, listTwo);
	}
	
	private Object identifyTheBlocks(JSONObject completeJsonObject) throws JSONException {
		return completeJsonObject.getJSONObject("asprise-ocr").getJSONObject("page").getJSONArray("block");
	}

	private JSONArray retrieveDataFromTable(Object object, String type) throws JSONException {
		JSONArray tableJsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		//objects used inside for loop
		JSONArray cellsJsonArray = new JSONArray();
		Object     cell;
		JSONArray  cellJsonArray = new JSONArray();
		JSONObject cellObject;
		if(type == "array"){
			tableJsonArray = (JSONArray)object;	
			for(int tableArrayCounter=0; tableArrayCounter<tableJsonArray.length(); tableArrayCounter++){
				jsonObject = tableJsonArray.getJSONObject(tableArrayCounter);
			    cell = jsonObject.get("cell");
				if (cell instanceof JSONArray) {
					cellJsonArray = (JSONArray)cell;
					for(int cellCounter=0; cellCounter<cellJsonArray.length(); cellCounter++){
						JSONObject tempJsonObj = new JSONObject();
						tempJsonObj.put("x", cellJsonArray.getJSONObject(cellCounter).get("x"));
						tempJsonObj.put("y", cellJsonArray.getJSONObject(cellCounter).get("y"));
						tempJsonObj.put("row", cellJsonArray.getJSONObject(cellCounter).get("row"));
						tempJsonObj.put("col", cellJsonArray.getJSONObject(cellCounter).get("col"));				
						if(cellJsonArray.getJSONObject(cellCounter).has("block")){						
							if (cellJsonArray.getJSONObject(cellCounter).get("block") instanceof JSONArray) {			
								for(int blockCounter=0; blockCounter<cellJsonArray.getJSONObject(cellCounter).getJSONArray("block").length(); blockCounter++){
									JSONObject tempJsonObj1 = new JSONObject();
									tempJsonObj1.put("x", cellJsonArray.getJSONObject(cellCounter).getJSONArray("block").getJSONObject(blockCounter).get("x"));
									tempJsonObj1.put("y", cellJsonArray.getJSONObject(cellCounter).getJSONArray("block").getJSONObject(blockCounter).get("y"));
									tempJsonObj1.put("content", cellJsonArray.getJSONObject(cellCounter).getJSONArray("block").getJSONObject(blockCounter).get("content"));
									cellsJsonArray.put(tempJsonObj1);
								}	
							}
							else if (cellJsonArray.getJSONObject(cellCounter).get("block") instanceof JSONObject) {
							   tempJsonObj.put("content", cellJsonArray.getJSONObject(cellCounter).getJSONObject("block").get("content"));
							   cellsJsonArray.put(tempJsonObj);
							}else{
								System.out.println("table is neighter object nor array");
							}	
						}
					}
					
				}else if (cell instanceof JSONObject) {
					JSONObject tempJsonObj = new JSONObject();
					cellObject = (JSONObject)cell;
					tempJsonObj.put("x", cellObject.get("x"));
					tempJsonObj.put("y", cellObject.get("y"));
					tempJsonObj.put("row", cellObject.get("row"));
					tempJsonObj.put("col", cellObject.get("col"));
					if(cellObject.has("block")){						
						if (cellObject.get("block") instanceof JSONArray) {			
							for(int blockCounter=0; blockCounter<cellObject.getJSONArray("block").length(); blockCounter++){
								JSONObject tempJsonObj1 = new JSONObject();
								tempJsonObj1.put("x", cellObject.getJSONArray("block").getJSONObject(blockCounter).get("x"));
								tempJsonObj1.put("y", cellObject.getJSONArray("block").getJSONObject(blockCounter).get("y"));
								tempJsonObj1.put("content", cellObject.getJSONArray("block").getJSONObject(blockCounter).get("content"));
								cellsJsonArray.put(tempJsonObj1);
							}			
						}
						else if (cellObject.get("block") instanceof JSONObject) {	
						   tempJsonObj.put("content", cellObject.getJSONObject("block").get("content"));
						   cellsJsonArray.put(tempJsonObj);
						}else{
							System.out.println("table is neighter object nor array");
						}	
					}
				}else{
					
				}
			}
		}else{
			jsonObject = (JSONObject)object;
			cell = jsonObject.get("cell");
			if (cell instanceof JSONArray) {
				cellJsonArray = (JSONArray)cell;
				for(int cellCounter=0; cellCounter<cellJsonArray.length(); cellCounter++){
					JSONObject tempJsonObj = new JSONObject();
					tempJsonObj.put("x", cellJsonArray.getJSONObject(cellCounter).get("x"));
					tempJsonObj.put("y", cellJsonArray.getJSONObject(cellCounter).get("y"));
					tempJsonObj.put("row", cellJsonArray.getJSONObject(cellCounter).get("row"));
					tempJsonObj.put("col", cellJsonArray.getJSONObject(cellCounter).get("col"));				
					if(cellJsonArray.getJSONObject(cellCounter).has("block")){						
						if (cellJsonArray.getJSONObject(cellCounter).get("block") instanceof JSONArray) {			
							for(int blockCounter=0; blockCounter<cellJsonArray.getJSONObject(cellCounter).getJSONArray("block").length(); blockCounter++){
								JSONObject tempJsonObj1 = new JSONObject();
								tempJsonObj1.put("x", cellJsonArray.getJSONObject(cellCounter).getJSONArray("block").getJSONObject(blockCounter).get("x"));
								tempJsonObj1.put("y", cellJsonArray.getJSONObject(cellCounter).getJSONArray("block").getJSONObject(blockCounter).get("y"));
								tempJsonObj1.put("content", cellJsonArray.getJSONObject(cellCounter).getJSONArray("block").getJSONObject(blockCounter).get("content"));
								cellsJsonArray.put(tempJsonObj1);
							}	
						}
						else if (cellJsonArray.getJSONObject(cellCounter).get("block") instanceof JSONObject) {
						   tempJsonObj.put("content", cellJsonArray.getJSONObject(cellCounter).getJSONObject("block").get("content"));
						   cellsJsonArray.put(tempJsonObj);
						}else{
							System.out.println("table is neighter object nor array");
						}	
					}
				}
				
			}else if (cell instanceof JSONObject) {
				JSONObject tempJsonObj = new JSONObject();
				cellObject = (JSONObject)cell;
				tempJsonObj.put("x", cellObject.get("x"));
				tempJsonObj.put("y", cellObject.get("y"));
				tempJsonObj.put("row", cellObject.get("row"));
				tempJsonObj.put("col", cellObject.get("col"));
				if(cellObject.has("block")){						
					if (cellObject.get("block") instanceof JSONArray) {			
						for(int blockCounter=0; blockCounter<cellObject.getJSONArray("block").length(); blockCounter++){
							JSONObject tempJsonObj1 = new JSONObject();
							tempJsonObj1.put("x", cellObject.getJSONArray("block").getJSONObject(blockCounter).get("x"));
							tempJsonObj1.put("y", cellObject.getJSONArray("block").getJSONObject(blockCounter).get("y"));
							tempJsonObj1.put("content", cellObject.getJSONArray("block").getJSONObject(blockCounter).get("content"));
							cellsJsonArray.put(tempJsonObj1);
						}			
					}
					else if (cellObject.get("block") instanceof JSONObject) {	
					   tempJsonObj.put("content", cellObject.getJSONObject("block").get("content"));
					   cellsJsonArray.put(tempJsonObj);
					}else{
						System.out.println("table is neighter object nor array");
					}	
				}
			}else{
				
			}
	
		}
		//System.out.println("All Elements"+cellsJsonArray);
		//return arrangeTableCells(cellsJsonArray);
		return cellsJsonArray;
	}

	//Method to identify tables
	private Object identifyTheTables(JSONObject completeJsonObject){
		try {
			table = completeJsonObject.getJSONObject("asprise-ocr").getJSONObject("page").get("table");				
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return table;
	}
	
	//Method to identify tables if they are in two pages
	private Object identifyTheTablesWithTwoPages(JSONObject jsonObject){
			try {
				table = jsonObject.get("table");				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return table;
		}

	private JSONArray arrangeTableCells(JSONArray array) throws JSONException {
		JSONArray cellsJsonArray = new JSONArray();
		cellsJsonArray = sortJsonArray(array, "y");
		JSONArray array1 = new JSONArray();
		JSONArray array2 = new JSONArray();
		//System.out.println("All Elements"+cellsJsonArray);
		int yvalue=0;
		int ytemp=0;
		for(int cellsJsonArrayCounter=0; cellsJsonArrayCounter<cellsJsonArray.length(); cellsJsonArrayCounter++){
			yvalue=(int) cellsJsonArray.getJSONObject(cellsJsonArrayCounter).get("y");
			if((Math.abs(yvalue-ytemp)<5) || (ytemp==0)){
				array2.put(cellsJsonArray.getJSONObject(cellsJsonArrayCounter));
				ytemp=yvalue;
			}else{
				//array1.put(array2);
				array1.put(sortJsonArray(array2, "x"));
				array2 = new JSONArray(new ArrayList<String>());
				array2.put(cellsJsonArray.getJSONObject(cellsJsonArrayCounter));
				ytemp=0;
				yvalue=0;
			}
		}		
		array1.put(sortJsonArray(array2, "x"));
		//System.out.println("Formated Array"+array1);
		return array1;
	}

	private JSONArray sortJsonArray(JSONArray jsonArr, String value) throws JSONException{		
		//System.out.println("Before Sort::::"+jsonArr);
	    JSONArray sortedJsonArray = new JSONArray();

	    List<JSONObject> jsonValues = new ArrayList<JSONObject>();
	    for (int i = 0; i < jsonArr.length(); i++) {
	        jsonValues.add(jsonArr.getJSONObject(i));
	    }
	    Collections.sort( jsonValues, new Comparator<JSONObject>() {
	        //You can change "Name" with "ID" if you want to sort by ID
	        private final String KEY_NAME = value;

	        @Override
	        public int compare(JSONObject a, JSONObject b) {
	            String valA = new String();
	            String valB = new String();

	            try {
	                valA = String.valueOf(a.get(KEY_NAME));
	                valB = String.valueOf(b.get(KEY_NAME));
	            } 
	            catch (JSONException e) {
	                //do something
	            }

	            //return -valA.compareTo(valB);
	            //if you want to change the sort order, simply use the following:
	            return Integer.parseInt(valA) - Integer.parseInt(valB);	           
	        }
	    });
	    
	    for (int i = 0; i < jsonArr.length(); i++) {
	    	//System.out.println("After Sort:::"+jsonValues.get(i));
	        sortedJsonArray.put(jsonValues.get(i));
	    }
		//System.out.println("After Sort:::"+sortedJsonArray);
		return sortedJsonArray;
	}

	public JSONArray getMergeJson(JSONArray array1, JSONArray array2) throws JSONException{
		ArrayList<JSONArray> arraylist = new ArrayList<JSONArray>();
		arraylist.add(array1);
		arraylist.add(array2);
	    JSONArray result=null;
	    JSONObject obj= new JSONObject();
	    obj.put("key",result);
	    for(JSONArray tmp:arraylist){
	        for(int i=0;i<tmp.length();i++){
	         obj.accumulate("key", tmp.getJSONObject(i));   ;
	        }

	            }
	    return obj.getJSONArray("key");
	}

	public String fetchDataAfterTheCodeValue(String codeValue, String dataArray) {
		

		/* for herbert pdf 
		Pattern p = Pattern.compile("UID-Nr: [a-zA-Z]{3}\\d{7}"); */  // the pattern to search for

		String regexPattern  = codeValue + ".*";
		Pattern p = Pattern.compile(regexPattern);
	    Matcher m = p.matcher(dataArray);
	    String finalValue = "";
	    if (m.find()){
	        System.out.println("Found a match");
	    	System.out.println(m.group());
	    	String found = m.group();
	    	System.out.println("value::: " + found.substring(0, found.indexOf("\"")));
	    	finalValue =  found.substring(0, found.indexOf("\""));
	    }
	      else
	        System.out.println("Did not find a match");
	    return finalValue;
		
	}
	
}