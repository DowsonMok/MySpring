package org.spring.beans;

public class PropertyValue {
	private final String name;

	private final Object value;

	//true表示value被转换为convertedValue
	private boolean converted = false;
	//value被转换过得 如果value为RuntimeBeanReference，则表示被被实例化
	private Object convertedValue;
	
	public PropertyValue(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Object getValue() {
		return this.value;
	}
	public synchronized boolean isConverted() {
		return this.converted;
	}

	
	public synchronized void setConvertedValue(Object value) {
		this.converted = true;
		this.convertedValue = value;
	}
	
	public synchronized Object getConvertedValue() {
		return this.convertedValue;
	}

}