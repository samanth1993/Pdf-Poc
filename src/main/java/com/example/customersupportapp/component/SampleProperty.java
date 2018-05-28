package com.example.customersupportapp.component;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "prefix")
@Component
public class SampleProperty {

    private List<String> listProp;
    private List<String> listPropInCaps;
    private List<String> listReferenceNumber;
    private List<String> listDateCode;
    private List<String> listTableStart;
    private List<String> listTableEnd;

	public List<String> getListProp() {
		return listProp;
	}

	public void setListProp(List<String> listProp) {
		this.listProp = listProp;
	}

	public List<String> getListPropInCaps() {
		return listPropInCaps;
	}

	public void setListPropInCaps(List<String> listPropInCaps) {
		this.listPropInCaps = listPropInCaps;
	}

	public List<String> getListReferenceNumber() {
		return listReferenceNumber;
	}

	public void setListReferenceNumber(List<String> listReferenceNumber) {
		this.listReferenceNumber = listReferenceNumber;
	}

	public List<String> getListDateCode() {
		return listDateCode;
	}

	public void setListDateCode(List<String> listDateCode) {
		this.listDateCode = listDateCode;
	}

	public List<String> getListTableStart() {
		return listTableStart;
	}

	public void setListTableStart(List<String> listTableStart) {
		this.listTableStart = listTableStart;
	}

	public List<String> getListTableEnd() {
		return listTableEnd;
	}

	public void setListTableEnd(List<String> listTableEnd) {
		this.listTableEnd = listTableEnd;
	}
	
    
    
}