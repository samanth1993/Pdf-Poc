package com.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.asprise.ocr.Ocr;

@RestController
public class PdfReaderController {				
	JSONObject completeJsonObject = new JSONObject();
	String completeXmlString = "";
	Object table = null;
	Object block = null;
	Object page = null;
	JSONObject tableObject;
	JSONArray globalArray = new JSONArray();
	
	
	@RequestMapping(value="/readPdf", produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, String> checkOCR() throws IOException, JSONException {
		Map<String, String> check = new HashMap<>();
		JSONArray finalArrayOne = new JSONArray();
		JSONArray finalArrayTwo = new JSONArray();
		JSONArray finalArrayThree = new JSONArray();
		finalArrayOne = checkOCROne();
		finalArrayTwo = checkOCRTwo();
		finalArrayThree = checkOCRThree();
		System.out.println("finalArrayOne::::::::::::::::::::::::"+finalArrayOne);
		System.out.println("finalArrayTwo::::::::::::::::::::::::"+finalArrayTwo);
		System.out.println("finalArrayThree::::::::::::::::::::::"+finalArrayThree);
		check.put("finalArrayOne", finalArrayOne.toString());
		check.put("finalArrayTwo", finalArrayTwo.toString());
		check.put("finalArrayThree", finalArrayThree.toString());
		return check;
	}
	
	public JSONArray checkOCROne() throws IOException, JSONException {
		JSONArray listOne = new JSONArray();
		JSONArray tableJsonArray = new JSONArray();	
		Ocr.setUp(); // one time setup
		Ocr ocr = new Ocr(); // create a new OCR engine
		ocr.startEngine("eng", Ocr.SPEED_FASTEST); // English
		completeXmlString = ocr.recognize(new File[] {new File("C:/Users/sheka/Desktop/demo_pdf/SG_170425_00032_EUR_5.pdf")}, Ocr.RECOGNIZE_TYPE_ALL, Ocr.OUTPUT_FORMAT_XML);
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
						for(int tableArrayCounter=0; tableArrayCounter<tableJsonArray.length(); tableArrayCounter++){
							listOne.put(formSparseMatrix(tableJsonArray.getJSONObject(tableArrayCounter)));
						}
					}
					else if (table instanceof JSONObject) {
					    // It's an object
						tableObject = (JSONObject)table;
						listOne.put(formSparseMatrix(tableObject));
					}else{
						System.out.println("table is neighter object nor array");
					}	
				}else{
					block = identifyTheBlocks(completeJsonObject);
					//System.out.println(block);					
					listOne.put(arrangeTableCells((JSONArray) block));
				}
			}
		}
		else if (page instanceof JSONObject) {
		    // It's an object
			if(completeJsonObject.getJSONObject("asprise-ocr").getJSONObject("page").has("table")){
				table = identifyTheTables(completeJsonObject);
				if (table instanceof JSONArray) {
				    // It's an array	
					tableJsonArray = (JSONArray)table;			
					for(int tableArrayCounter=0; tableArrayCounter<tableJsonArray.length(); tableArrayCounter++){
						listOne.put(formSparseMatrix(tableJsonArray.getJSONObject(tableArrayCounter)));
					}
				}
				else if (table instanceof JSONObject) {
				    // It's an object
					tableObject = (JSONObject)table;
					listOne.put(formSparseMatrix(tableObject));
				}else{
					System.out.println("table is neighter object nor array");
				}	
			}else{
				block = identifyTheBlocks(completeJsonObject);
				//System.out.println(block);
				listOne.put(arrangeTableCells((JSONArray) block));
				return listOne;
			}
		}else{
			System.out.println("table is neighter object nor array");
		}
		ocr.stopEngine();
		return listOne;
	}
	
	public JSONArray checkOCRTwo() throws IOException, JSONException {
		JSONArray listTwo = new JSONArray();
		JSONArray tableJsonArray = new JSONArray();	
		Ocr.setUp(); // one time setup
		Ocr ocr = new Ocr(); // create a new OCR engine
		ocr.startEngine("eng", Ocr.SPEED_FASTEST); // English
		completeXmlString = ocr.recognize(new File[] {new File("C:/Users/sheka/Desktop/demo_pdf/40296.30 Herbert Temmel.pdf")}, Ocr.RECOGNIZE_TYPE_ALL, Ocr.OUTPUT_FORMAT_XML);
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
						for(int tableArrayCounter=0; tableArrayCounter<tableJsonArray.length(); tableArrayCounter++){
							listTwo.put(formSparseMatrix(tableJsonArray.getJSONObject(tableArrayCounter)));
						}
					}
					else if (table instanceof JSONObject) {
					    // It's an object
						tableObject = (JSONObject)table;
						listTwo.put(formSparseMatrix(tableObject));
					}else{
						System.out.println("table is neighter object nor array");
					}	
				}else{
					block = identifyTheBlocks(completeJsonObject);
					//System.out.println(block);
					listTwo.put(arrangeTableCells((JSONArray) block));
				}
			}
		}
		else if (page instanceof JSONObject) {
		    // It's an object
			if(completeJsonObject.getJSONObject("asprise-ocr").getJSONObject("page").has("table")){
				table = identifyTheTables(completeJsonObject);
				if (table instanceof JSONArray) {
				    // It's an array	
					tableJsonArray = (JSONArray)table;			
					for(int tableArrayCounter=0; tableArrayCounter<tableJsonArray.length(); tableArrayCounter++){
						listTwo.put(formSparseMatrix(tableJsonArray.getJSONObject(tableArrayCounter)));
					}
				}
				else if (table instanceof JSONObject) {
				    // It's an object
					tableObject = (JSONObject)table;
					listTwo.put(formSparseMatrix(tableObject));
				}else{
					System.out.println("table is neighter object nor array");
				}	
			}else{
				block = identifyTheBlocks(completeJsonObject);
				//System.out.println(block);
				listTwo.put(arrangeTableCells((JSONArray) block));
				return listTwo;
			}
		}else{
			System.out.println("table is neighter object nor array");
		}
		ocr.stopEngine();
		return listTwo;								
	}
	
	public JSONArray checkOCRThree() throws IOException, JSONException {
		JSONArray listThree = new JSONArray();
		JSONArray tableJsonArray = new JSONArray();	
		Ocr.setUp(); // one time setup
		Ocr ocr = new Ocr(); // create a new OCR engine
		ocr.startEngine("eng", Ocr.SPEED_FASTEST); // English
		completeXmlString = ocr.recognize(new File[] {new File("C:/Users/sheka/Desktop/demo_pdf/SITA UK LTD R-20906224.tif")}, Ocr.RECOGNIZE_TYPE_ALL, Ocr.OUTPUT_FORMAT_XML);
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
						for(int tableArrayCounter=0; tableArrayCounter<tableJsonArray.length(); tableArrayCounter++){
							listThree.put(formSparseMatrix(tableJsonArray.getJSONObject(tableArrayCounter)));
						}
					}
					else if (table instanceof JSONObject) {
					    // It's an object
						tableObject = (JSONObject)table;
						listThree.put(formSparseMatrix(tableObject));
					}else{
						System.out.println("table is neighter object nor array");
					}	
				}else{
					block = identifyTheBlocks(completeJsonObject);
					//System.out.println(block);
					listThree.put(arrangeTableCells((JSONArray) block));
				}
			}
		}
		else if (page instanceof JSONObject) {
		    // It's an object
			if(completeJsonObject.getJSONObject("asprise-ocr").getJSONObject("page").has("table")){
				table = identifyTheTables(completeJsonObject);
				if (table instanceof JSONArray) {
				    // It's an array	
					tableJsonArray = (JSONArray)table;			
					for(int tableArrayCounter=0; tableArrayCounter<tableJsonArray.length(); tableArrayCounter++){
						getAllElements(tableJsonArray.getJSONObject(tableArrayCounter));						
					}
					block = identifyTheBlocks(completeJsonObject);			
					System.out.println("ai"+getMergeJson(globalArray, (JSONArray) block));
					listThree.put(arrangeTableCells(getMergeJson(globalArray, (JSONArray) block)));
					System.out.println(listThree);
					globalArray = new JSONArray(new ArrayList<String>());
				}
				else if (table instanceof JSONObject) {
				    // It's an object
					tableObject = (JSONObject)table;
					listThree.put(formSparseMatrix(tableObject));
				}else{
					System.out.println("table is neighter object nor array");
				}	
			}else{
				block = identifyTheBlocks(completeJsonObject);
				//System.out.println(block);
				listThree.put(arrangeTableCells((JSONArray) block));
				return listThree;
			}
		}else{
			System.out.println("table is neighter object nor array");
		}
		ocr.stopEngine();
		return listThree;								
	}
	
	private Object identifyTheBlocks(JSONObject completeJsonObject) throws JSONException {
		return completeJsonObject.getJSONObject("asprise-ocr").getJSONObject("page").getJSONArray("block");
	}

	private JSONArray formSparseMatrix(JSONObject jsonObject) throws JSONException {
		JSONArray cellsJsonArray = new JSONArray();
		System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");		
		Object     cell;
		JSONArray  cellJsonArray = new JSONArray();
		JSONObject cellObject;
	    cell = jsonObject.get("cell");
		if (cell instanceof JSONArray) {
		    // It's an array	
			cellJsonArray = (JSONArray)cell;
			//System.out.println("cell as array:::"+cellJsonArray);
			for(int cellCounter=0; cellCounter<cellJsonArray.length(); cellCounter++){
				JSONObject tempJsonObj = new JSONObject();
				//System.out.println("("+"x="+cellJsonArray.getJSONObject(cellCounter).get("x")+", "+"y="+cellJsonArray.getJSONObject(cellCounter).get("y")+", "+"row="+cellJsonArray.getJSONObject(cellCounter).get("row")+", "+"col="+cellJsonArray.getJSONObject(cellCounter).get("col")+")");
				tempJsonObj.put("x", cellJsonArray.getJSONObject(cellCounter).get("x"));
				tempJsonObj.put("y", cellJsonArray.getJSONObject(cellCounter).get("y"));
				tempJsonObj.put("row", cellJsonArray.getJSONObject(cellCounter).get("row"));
				tempJsonObj.put("col", cellJsonArray.getJSONObject(cellCounter).get("col"));				
				if(cellJsonArray.getJSONObject(cellCounter).has("block")){						
					if (cellJsonArray.getJSONObject(cellCounter).get("block") instanceof JSONArray) {						
					    // It's an array
						//String s = "";
						for(int blockCounter=0; blockCounter<cellJsonArray.getJSONObject(cellCounter).getJSONArray("block").length(); blockCounter++){
							JSONObject tempJsonObj1 = new JSONObject();
							//System.out.println(cellJsonArray.getJSONObject(cellCounter).getJSONArray("block").getJSONObject(blockCounter).get("content"));
							//s=s+" "+cellJsonArray.getJSONObject(cellCounter).getJSONArray("block").getJSONObject(blockCounter).get("content");
							tempJsonObj1.put("x", cellJsonArray.getJSONObject(cellCounter).getJSONArray("block").getJSONObject(blockCounter).get("x"));
							tempJsonObj1.put("y", cellJsonArray.getJSONObject(cellCounter).getJSONArray("block").getJSONObject(blockCounter).get("y"));
							tempJsonObj1.put("content", cellJsonArray.getJSONObject(cellCounter).getJSONArray("block").getJSONObject(blockCounter).get("content"));
							cellsJsonArray.put(tempJsonObj1);
						}
						//tempJsonObj1.put("content", s);
						
					}
					else if (cellJsonArray.getJSONObject(cellCounter).get("block") instanceof JSONObject) {
					    // It's an object
					   //System.out.println(cellJsonArray.getJSONObject(cellCounter).getJSONObject("block").get("content"));		
					   tempJsonObj.put("content", cellJsonArray.getJSONObject(cellCounter).getJSONObject("block").get("content"));
					   cellsJsonArray.put(tempJsonObj);
					}else{
						System.out.println("table is neighter object nor array");
					}	
				}
				//System.out.println("***************************************************************");
			}
			
		}
		else if (cell instanceof JSONObject) {
			JSONObject tempJsonObj = new JSONObject();
		    // It's an object
			cellObject = (JSONObject)cell;
			/*System.out.println("cell as object:::"+cellObject);
			System.out.println("("+"x="+cellObject.get("x")+", "+"y="+cellObject.get("y")+", "+"row="+cellObject.get("row")+", "+"col="+cellObject.get("col")+")");
			*/
			tempJsonObj.put("x", cellObject.get("x"));
			tempJsonObj.put("y", cellObject.get("y"));
			tempJsonObj.put("row", cellObject.get("row"));
			tempJsonObj.put("col", cellObject.get("col"));
			if(cellObject.has("block")){						
				if (cellObject.get("block") instanceof JSONArray) {					
				    // It's an array
					//String s = "";
					for(int blockCounter=0; blockCounter<cellObject.getJSONArray("block").length(); blockCounter++){
						JSONObject tempJsonObj1 = new JSONObject();
						//System.out.println(cellObject.getJSONArray("block").getJSONObject(blockCounter).get("content"));
						tempJsonObj1.put("x", cellObject.getJSONArray("block").getJSONObject(blockCounter).get("x"));
						tempJsonObj1.put("y", cellObject.getJSONArray("block").getJSONObject(blockCounter).get("y"));
						tempJsonObj1.put("content", cellObject.getJSONArray("block").getJSONObject(blockCounter).get("content"));
						//s=s+"**"+cellObject.getJSONArray("block").getJSONObject(blockCounter).get("content");
						cellsJsonArray.put(tempJsonObj1);
					}
					//tempJsonObj1.put("content", s);					
				}
				else if (cellObject.get("block") instanceof JSONObject) {
				    // It's an object
				 //  System.out.println(cellObject.getJSONObject("block").get("content"));		
				   tempJsonObj.put("content", cellObject.getJSONObject("block").get("content"));
				   cellsJsonArray.put(tempJsonObj);
				}else{
					System.out.println("table is neighter object nor array");
				}	
			}
		}else{
			
		}
		System.out.println("All Elements"+cellsJsonArray);
		return arrangeTableCells(cellsJsonArray);
	}

	//Method to identify tables
	public Object identifyTheTables(JSONObject completeJsonObject){
		try {
			table = completeJsonObject.getJSONObject("asprise-ocr").getJSONObject("page").get("table");				
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return table;
	}
	
	//Method to identify tables if they are in two pages
		public Object identifyTheTablesWithTwoPages(JSONObject jsonObject){
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
		System.out.println("Formated Array"+array1);
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

	private void getAllElements(JSONObject jsonObject) throws JSONException {
		System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");		
		Object     cell;
		JSONArray  cellJsonArray = new JSONArray();
		JSONObject cellObject;
	    cell = jsonObject.get("cell");
		if (cell instanceof JSONArray) {
		    // It's an array	
			cellJsonArray = (JSONArray)cell;
			//System.out.println("cell as array:::"+cellJsonArray);
			for(int cellCounter=0; cellCounter<cellJsonArray.length(); cellCounter++){
				JSONObject tempJsonObj = new JSONObject();
				//System.out.println("("+"x="+cellJsonArray.getJSONObject(cellCounter).get("x")+", "+"y="+cellJsonArray.getJSONObject(cellCounter).get("y")+", "+"row="+cellJsonArray.getJSONObject(cellCounter).get("row")+", "+"col="+cellJsonArray.getJSONObject(cellCounter).get("col")+")");
				tempJsonObj.put("x", cellJsonArray.getJSONObject(cellCounter).get("x"));
				tempJsonObj.put("y", cellJsonArray.getJSONObject(cellCounter).get("y"));
				tempJsonObj.put("row", cellJsonArray.getJSONObject(cellCounter).get("row"));
				tempJsonObj.put("col", cellJsonArray.getJSONObject(cellCounter).get("col"));				
				if(cellJsonArray.getJSONObject(cellCounter).has("block")){						
					if (cellJsonArray.getJSONObject(cellCounter).get("block") instanceof JSONArray) {						
					    // It's an array
						//String s = "";
						for(int blockCounter=0; blockCounter<cellJsonArray.getJSONObject(cellCounter).getJSONArray("block").length(); blockCounter++){
							JSONObject tempJsonObj1 = new JSONObject();
							//System.out.println(cellJsonArray.getJSONObject(cellCounter).getJSONArray("block").getJSONObject(blockCounter).get("content"));
							//s=s+" "+cellJsonArray.getJSONObject(cellCounter).getJSONArray("block").getJSONObject(blockCounter).get("content");
							tempJsonObj1.put("x", cellJsonArray.getJSONObject(cellCounter).getJSONArray("block").getJSONObject(blockCounter).get("x"));
							tempJsonObj1.put("y", cellJsonArray.getJSONObject(cellCounter).getJSONArray("block").getJSONObject(blockCounter).get("y"));
							tempJsonObj1.put("content", cellJsonArray.getJSONObject(cellCounter).getJSONArray("block").getJSONObject(blockCounter).get("content"));
							globalArray.put(tempJsonObj1);
						}
						//tempJsonObj1.put("content", s);
						
					}
					else if (cellJsonArray.getJSONObject(cellCounter).get("block") instanceof JSONObject) {
					    // It's an object
					   //System.out.println(cellJsonArray.getJSONObject(cellCounter).getJSONObject("block").get("content"));		
					   tempJsonObj.put("content", cellJsonArray.getJSONObject(cellCounter).getJSONObject("block").get("content"));
					   globalArray.put(tempJsonObj);
					}else{
						System.out.println("table is neighter object nor array");
					}	
				}
				//System.out.println("***************************************************************");
			}
			
		}
		else if (cell instanceof JSONObject) {
			JSONObject tempJsonObj = new JSONObject();
		    // It's an object
			cellObject = (JSONObject)cell;
			/*System.out.println("cell as object:::"+cellObject);
			System.out.println("("+"x="+cellObject.get("x")+", "+"y="+cellObject.get("y")+", "+"row="+cellObject.get("row")+", "+"col="+cellObject.get("col")+")");
			*/
			tempJsonObj.put("x", cellObject.get("x"));
			tempJsonObj.put("y", cellObject.get("y"));
			tempJsonObj.put("row", cellObject.get("row"));
			tempJsonObj.put("col", cellObject.get("col"));
			if(cellObject.has("block")){						
				if (cellObject.get("block") instanceof JSONArray) {					
				    // It's an array
					//String s = "";
					for(int blockCounter=0; blockCounter<cellObject.getJSONArray("block").length(); blockCounter++){
						JSONObject tempJsonObj1 = new JSONObject();
						//System.out.println(cellObject.getJSONArray("block").getJSONObject(blockCounter).get("content"));
						tempJsonObj1.put("x", cellObject.getJSONArray("block").getJSONObject(blockCounter).get("x"));
						tempJsonObj1.put("y", cellObject.getJSONArray("block").getJSONObject(blockCounter).get("y"));
						tempJsonObj1.put("content", cellObject.getJSONArray("block").getJSONObject(blockCounter).get("content"));
						//s=s+"**"+cellObject.getJSONArray("block").getJSONObject(blockCounter).get("content");
						globalArray.put(tempJsonObj1);
					}
					//tempJsonObj1.put("content", s);					
				}
				else if (cellObject.get("block") instanceof JSONObject) {
				    // It's an object
				 //  System.out.println(cellObject.getJSONObject("block").get("content"));		
				   tempJsonObj.put("content", cellObject.getJSONObject("block").get("content"));
				   globalArray.put(tempJsonObj);
				}else{
					System.out.println("table is neighter object nor array");
				}	
			}
		}else{
			
		}
		System.out.println("All Elements"+globalArray);
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

}