package com.example.customersupportapp.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.asprise.ocr.Ocr;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

@RestController
public class CustomerSupportController {
	
	@RequestMapping(value="/pdfCheck")
	public void check() throws IOException {
		System.out.println("Inside pdff");
		File file = new File("C:/Users/Edvenswa/Desktop/veena/SG_170425_00032_EUR_5.pdf");
	      PDDocument document = PDDocument.load(file);

	      //Instantiate PDFTextStripper class
	      PDFTextStripper pdfStripper = new PDFTextStripper();

	      //Retrieving text from PDF document
	      String text = pdfStripper.getText(document);
	      System.out.println(text);

	      //Closing the document
	      document.close();
	}
	
	
//	@CrossOrigin(origins = "http://127.0.0.1:58295/index.html#/login", maxAge = 3600)
	@RequestMapping(value="/pdfOCR", produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, String> checkOCR() throws IOException, JSONException {
		List<JSONArray> finalList = new ArrayList<>();
		HashMap<String, Object> modelMap = new HashMap<>();
		ObjectMapper om = new ObjectMapper();
		String[] array;
		JSONObject item = new JSONObject();
		JSONObject headers = new JSONObject();
		JSONObject objToStoreRowData = new JSONObject();
		Ocr.setUp(); // one time setup
		Ocr ocr = new Ocr(); // create a new OCR engine
		ocr.startEngine("eng", Ocr.SPEED_FASTEST); // English
		String s = ocr.recognize(new File[] {new File("C:/Users/sheka/Desktop/veena/SG_170425_00032_EUR_32(Rotated).pdf")}, Ocr.RECOGNIZE_TYPE_ALL, Ocr.OUTPUT_FORMAT_XML);
		String rtf = ocr.recognize(new File[] {new File("C:/Users/sheka/Desktop/veena/SG_170425_00032_EUR_32(Rotated).pdf")}, Ocr.RECOGNIZE_TYPE_ALL, Ocr.OUTPUT_FORMAT_RTF,"PROP_RTF_OUTPUT_FILE=ocr-result.rtf");
		JSONObject xmlJSONObj1 = XML.toJSONObject(s);
		System.out.println("fullObject::::::::::"+xmlJSONObj1);
		for(int w =0; w<xmlJSONObj1.getJSONObject("asprise-ocr").getJSONObject("page").getJSONArray("table").length(); w++){
			if(w==1){
				//System.out.println("*-*-*-*-**-*-*-*-*-*-*"+xmlJSONObj1.getJSONObject("asprise-ocr").getJSONObject("page").getJSONArray("table").getJSONObject(w).getJSONArray("cell"));
				for(int e=0;e<xmlJSONObj1.getJSONObject("asprise-ocr").getJSONObject("page").getJSONArray("table").getJSONObject(w).getJSONArray("cell").length(); e++){
					if(e==3){						
						xmlJSONObj1.getJSONObject("asprise-ocr").getJSONObject("page").getJSONArray("table").getJSONObject(w).getJSONArray("cell").getJSONObject(e).put("row", 0);
						System.out.println("*-*-*-*-**-*-*-*-*-*-*"+xmlJSONObj1.getJSONObject("asprise-ocr").getJSONObject("page").getJSONArray("table").getJSONObject(w).getJSONArray("cell").getJSONObject(e).get("row"));
					}
				}
			}
		}
					
		JSONObject xmlJSONObj2 = (JSONObject) xmlJSONObj1.get("asprise-ocr");
		JSONObject xmlJSONObj3 = (JSONObject) xmlJSONObj2.get("page");
		JSONArray xmlJSONObj4 = (JSONArray) xmlJSONObj3.get("table");
		JSONObject finalJSON = new JSONObject();
		Map<String, String> check = new HashMap<>();		
		for(int i=0; i<xmlJSONObj4.length(); i++){
			int j = (int) xmlJSONObj4.optJSONObject(i).get("cells");
			//j is number of boxes in the pdf
			if(j>5){
				JSONArray xmlJSONObj5 = (JSONArray) xmlJSONObj4.optJSONObject(i).get("cell");
				for(int k=0; k<xmlJSONObj5.length(); k++){					
					JSONObject json = xmlJSONObj5.getJSONObject(k);
					Object     block;
					JSONArray  blockJsonArray;
					JSONObject blockObject;
				    block = json.get("block");
					if (block instanceof JSONArray) {
					    // It's an array
						blockJsonArray = (JSONArray)block;
						for(int l=0; l<blockJsonArray.length(); l++){						
							if(xmlJSONObj5.getJSONObject(k).get("row").toString().equalsIgnoreCase("0")){
								headers.accumulate(xmlJSONObj5.getJSONObject(k).get("col").toString(), blockJsonArray.getJSONObject(l).get("content").toString());
							}else{
								item.accumulate(xmlJSONObj5.getJSONObject(k).get("col").toString(), blockJsonArray.getJSONObject(l).get("content").toString());
								//array.put(item);
							}							
						}
					}
					else if (block instanceof JSONObject) {
					    // It's an object
						blockObject = (JSONObject)block;
						if(xmlJSONObj5.getJSONObject(k).get("row").toString().equalsIgnoreCase("0")){
							headers.accumulate(xmlJSONObj5.getJSONObject(k).get("col").toString(), blockObject.getString("content").toString());
							headers.accumulate(xmlJSONObj5.getJSONObject(k).get("col").toString(), xmlJSONObj5.getJSONObject(k).get("col").toString());
							headers.accumulate(xmlJSONObj5.getJSONObject(k).get("col").toString(), xmlJSONObj5.getJSONObject(k).get("colspan").toString());							
						}else{
							item.accumulate(xmlJSONObj5.getJSONObject(k).get("col").toString(), blockObject.getString("content").toString());							
						}						
					}
					else {
					    // It's something else, like a string or number
					}					
				}
					for(int m=0; m<item.length(); m++){
						String mtoString = String.valueOf(m);
						finalList.add((JSONArray) item.get(mtoString));				
					}
					System.out.println(finalList);
					for(int counter=0; counter<finalList.size();counter++){
						JSONArray jsonArray= finalList.get(counter);
						for(int counter2=0;counter2<finalList.get(counter).length();counter2++){
							String toPassAsValue = String.valueOf(counter2);
							objToStoreRowData.accumulate(toPassAsValue, jsonArray.getString(counter2));
						}
					}
				
					System.out.println("headers"+j+":::::::::::::::::::::::::::::::"+headers);
					System.out.println("final:::::::::::::::::::::"+objToStoreRowData);
					modelMap.put("headers", headers);
					modelMap.put("rows", objToStoreRowData);
					
					check.put("headers", headers.toString());
					check.put("rows", objToStoreRowData.toString());															
			}
		}
		
		ocr.stopEngine();
		
		return check;
	}
	
	@RequestMapping(value="/checkOCRSGAP", produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, String> checkOCRSGAP() throws IOException, JSONException {
		ObjectMapper om = new ObjectMapper();
		String[] array;
		JSONObject item = new JSONObject();
		JSONObject headers = new JSONObject();
		JSONObject objToStoreRowData = new JSONObject();
		Ocr.setUp(); // one time setup
		Ocr ocr = new Ocr(); // create a new OCR engine
		ocr.startEngine("eng", Ocr.SPEED_FASTEST); // English
		String s = ocr.recognize(new File[] {new File("C:/Users/sheka/Desktop/veena/SG_170425_00032_EUR_25.pdf")}, Ocr.RECOGNIZE_TYPE_ALL, Ocr.OUTPUT_FORMAT_XML);
		String rtf = ocr.recognize(new File[] {new File("C:/Users/sheka/Desktop/veena/SG_170425_00032_EUR_25.pdf")}, Ocr.RECOGNIZE_TYPE_ALL, Ocr.OUTPUT_FORMAT_RTF,"PROP_RTF_OUTPUT_FILE=ocr-result.rtf");
		JSONObject xmlJSONObj1 = XML.toJSONObject(s);
		System.out.println("fullObject::::::::::"+xmlJSONObj1);
		
		return null;
	}
	
	@RequestMapping(value="/rotatePDF")
	public void rotate() throws IOException, DocumentException {
		String RESULT = "C:/Users/sheka/Desktop/veena/SG_170425_00032_EUR_32.pdf";
		String FINAL_RESULT = "C:/Users/sheka/Desktop/veena/SG_170425_00032_EUR_32(Rotated).pdf";
		PdfReader reader = new PdfReader(RESULT);
        int n = reader.getNumberOfPages();
        int rot;
        PdfDictionary pageDict;
        for (int i = 1; i <= n; i++) {
            rot = reader.getPageRotation(i);
            pageDict = reader.getPageN(i);
            pageDict.put(PdfName.ROTATE, new PdfNumber(rot + 90));
        }
        
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(FINAL_RESULT));
        System.out.println("stamper:: " + stamper);
        stamper.close();
        reader.close();
	}
	
	

}