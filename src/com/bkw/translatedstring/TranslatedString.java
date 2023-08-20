package com.bkw.translatedstring;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDate;
import java.util.Map;
import java.util.AbstractMap;

import com.bkw.GenericBean;
import com.bkw.GenericField;
import com.bkw.IGenericField;
import com.bkw.IGenericDataType;
import com.bkw.GenericDataAccess;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;

public class TranslatedString extends GenericBean<IGenericField, Object> {

    public enum Field implements IGenericField{
        STRINGCODE("StringCode",GenericBean.DataType.STRING,true),
        TEXT("Text",GenericBean.DataType.STRING),
        LANGUAGECODE("LanguageCode",GenericBean.DataType.STRING,true),
        COUNTRYCODE("CountryCode",GenericBean.DataType.STRING,true),
        REGIONCODE("RegionCode",GenericBean.DataType.STRING),
        TICKET("Ticket",GenericBean.DataType.STRING);

        private String fieldName;
        private boolean isKey;
        private IGenericDataType dataType;

        Field(String fieldName, IGenericDataType dataType, boolean isKey) {
            this.fieldName = fieldName;
            this.dataType = dataType;
            this.isKey = isKey;
        }

        Field(String fieldName, IGenericDataType dataType) {
            this.fieldName = fieldName;
            this.dataType = dataType;
            this.isKey = false;
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

        @Override
        public String toString() {
            return this.fieldName;
        }
    }

    public TranslatedString(GenericBean<IGenericField, Object> bean) {
        for(TranslatedString.Field field: TranslatedString.Field.values()) {
            if(bean.containsKey(field)) {
                this.put(field,bean.get(field));
            }
        }
    }

    public TranslatedString() {
        super();
    }

    public TranslatedString(String stringCode,String languageCode,String countryCode) {
        super();
        map.put(Field.STRINGCODE,stringCode);
        map.put(Field.LANGUAGECODE,languageCode);
        map.put(Field.COUNTRYCODE,countryCode);
    }
    @Override
    public String getTableName() {
        return "translatedstring";
    }

    @Override
    public String getName() {
        return "TranslatedString";
    }

    public String getStringCode() {
        return (String)this.get(Field.STRINGCODE);
    }

    public void setStringCode(String stringCode) {
        this.put(Field.STRINGCODE,stringCode);
    }

    public String getText() {
        return (String)this.get(Field.TEXT);
    }

    public void setText(String text) {
        this.put(Field.TEXT,text);
    }

    public String getLanguageCode() {
        return(String)this.get(Field.LANGUAGECODE);
    }

    public void setLanguageCode(String languageCode) {
        this.put(Field.LANGUAGECODE,languageCode);
    }

    public String getCountryCode() {
        return (String)this.get(Field.COUNTRYCODE);
    }

    public void setCountryCode(String countryCode) {
        this.put(Field.COUNTRYCODE,countryCode);
    }

    public String getRegionCode() {
        return (String)this.get(Field.REGIONCODE);
    }

    public void setRegionCode(String regionCode) {
        this.put(Field.REGIONCODE,regionCode);
    }

    public String getTicket() {
        return (String)this.get(Field.TICKET);
    }

    public void setTicket(String ticket) {
        this.put(Field.TICKET,ticket);
    }

    public void fromJSONString(String json) {
        // First extract a map of values
        HashMap<String,Object> map=new HashMap();
        Gson gson=new Gson();
        map=gson.fromJson(json,HashMap.class);
        // First map is outer map which contains a single map of field values
        IGenericField TranslatedStrings=new GenericField("TranslatedStrings");

        AbstractMap<String,Object> map2=(AbstractMap)map.get("TranslatedString");
        IGenericField[] fields=TranslatedString.Field.values();

        // Convert the mapped values into a TranslatedString instance
        for(String key:map2.keySet()) {
            Object val=map2.get(key);
            if(val!=null) {
                // Map key to field
                IGenericField field=null;
                for(int i=0; i<fields.length; ++i) {
                    if(fields[i].getFieldName().equalsIgnoreCase(key)) {
                        field=fields[i];
                        break;
                    }
                }
                if(field!=null) {
                    this.put(field,val);
                }
            }
            //System.out.println(key+"["+i+"]="+vals[i]);
        }
    }

    @Override
    public String toString() {
        String result="{\n  \""+this.getName()+"\": {";
        boolean isFirst=true;
        for(TranslatedString.Field field: TranslatedString.Field.values()) {
            if(this.get(field)!=null) {
                String fieldName=field.getFieldName();
                String value="";
                if(field.getDataType()==GenericBean.DataType.STRING)
                    value="\""+this.get(field)+"\"";
                else
                    if(field.getDataType()==GenericBean.DataType.DATE) {
                        Date date=(Date)this.get(field);
                        LocalDate localDate=date.toLocalDate();
                        value="\""+localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)+"\"";
                    }
                    else
                        value=this.get(field).toString();
                result+=(!isFirst?",":"")+"\n  \""+fieldName+"\": "+value;
                isFirst=false;
            }
        }
        result+="\n}}";
        return result;
    }

    public static void main(String[] args) {

        // Read all employees
        TranslatedString ts=new TranslatedString();
        IGenericField[] field=TranslatedString.Field.values();
        List<GenericBean<IGenericField, Object>> list=GenericDataAccess.readAll(ts,TranslatedString.Field.values());
        System.out.println("All:"+list);

        // Read existing employee
        ts=new TranslatedString("SALES_ORDER","en","US");
        ts=(TranslatedString)GenericDataAccess.read(ts,TranslatedString.Field.values());
        System.out.println("Read: "+ts);
        System.out.println("JSON:\n"+ts.toJSONString());
        TranslatedString ts2=new TranslatedString();
        ts2.fromJSONString(ts.toJSONString());
        System.out.println("INFLATED: "+ts2);

/***
        if(ts.getTicket().equals(""))
            ts.setTicket("CME-100");
        else
            ts.setTicket("");
        GenericDataAccess.update(ts,TranslatedString.Field.values());

        // Read updated employee
        TranslatedString ts2=(TranslatedString)GenericDataAccess.read(ts,TranslatedString.Field.values());
        System.out.println("Updated: "+ts2);

        // Create copy of updated employee
        ts2.setCountryCode("GB");
        GenericDataAccess.create(ts2,TranslatedString.Field.values());

        // Delete the created copy
        GenericDataAccess.delete(ts2,TranslatedString.Field.values());
***/
    }
}