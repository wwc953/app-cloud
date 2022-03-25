package com.example.appstaticutil.entity;

import java.io.Reader;
import java.sql.Clob;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class EntityUtils {
    public static Map<String, Object> clobToString(Map<String, Object> map) {
        Set<Map.Entry<String, Object>> entrySet = map.entrySet();
        Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Clob) {
                Clob clob = (Clob) value;
                try {
                    Reader inStream = clob.getCharacterStream();
                    long length = clob.length();
                    char[] c = new char[(int) length];
                    inStream.read(c);
                    String attrMap = new String(c);
                    map.put(key, attrMap);
                } catch (Exception throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return map;
    }
}
