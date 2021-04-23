package cn.cym.codetoolkit.entity;

import java.util.List;

/**
 * 表对象
 * @author xgchen
 */
public class Table2 {
	
	public String name;
	public String objName;
	public String objType;
	public List<Field> fieldList;
	
	public Table2(String name, String objName, String objType, List<Field> fieldList) {
		this.name = name;
		this.objName = objName;
		this.objType = objType;
		this.fieldList = fieldList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getObjName() {
		return objName;
	}

	public void setObjName(String objName) {
		this.objName = objName;
	}

	public String getObjType() {
		return objType;
	}

	public void setObjType(String objType) {
		this.objType = objType;
	}

	public List<Field> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<Field> fieldList) {
		this.fieldList = fieldList;
	}

}
