package cn.cym.codetoolkit.entity;

/**
 * 
 * 字段对象
 * @author xgchen
 *
 */
public class Field {

	public String field;
	public String property;
	public String fieldType;
	public String propertyType;
	
	public Field(String field, String property, String fieldType, String propertyType) {
		this.field = field;
		this.property = property;
		this.fieldType = fieldType;
		this.propertyType = propertyType;
	}

	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getPropertyType() {
		return propertyType;
	}
	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}
	
	
}
