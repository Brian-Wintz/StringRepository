package com.bkw;

public class GenericField implements IGenericField {

    String fieldName;
    IGenericDataType dataType;
    boolean isKey;

    public GenericField(String fieldName) {
        this.fieldName=fieldName;
        this.dataType=GenericBean.DataType.STRING;
        this.isKey=false;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public IGenericDataType getDataType() {
        return this.dataType;
    }

    public boolean isKey() {
        return this.isKey;
    }

}