package com.bkw;

import java.util.HashMap;
import java.util.Map;

public class GenericBean<K, V> {
    public enum DataType implements IGenericDataType{
        STRING("String"),
        DATE("Date"),
        INTEGER("Integer"),
        FLOAT("Float"),
        BOOLEAN("Boolean");

        private String dataType;

        DataType(String dataType) {
            this.dataType = dataType;
        }

        public String getDataType() {
            return dataType;
        }
    }


    protected HashMap<K, V> map = new HashMap<>();

    public void put(K key, V value) {
        map.put(key, value);
    }

    public V get(K key) {
        return map.get(key);
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public int size() {
        return map.size();
    }

    @Override
    public String toString() {
        return map.toString();
    }

    public String toJSONString() {
        boolean first=true;
        String json="{\n  \""+this.getName()+"\": {\n";

        for(K key:map.keySet()) {
            V vals=map.get(key);
            if(vals!=null) {
                json+=(first?"    ":",\n    ")+"\""+key.toString()+"\": \""+vals.toString()+"\"";
                first=false;
            }
            //System.out.println(key+"["+i+"]="+vals[i]);
        }
        json+="\n  }\n}\n";
        return json;
    }

    public void fromJSONString(String json) {
    }

    public String getTableName() {
        return "<unknown>";
    }

    public String getName() {
        return "<unknown>";
    }

}