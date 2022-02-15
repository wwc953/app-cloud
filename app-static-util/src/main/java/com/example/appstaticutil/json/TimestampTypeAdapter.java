package com.example.appstaticutil.json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description: TimestampTypeAdapter
 * @author: wangwc
 * @date: 2020/11/27 17:14
 */
public class TimestampTypeAdapter implements JsonSerializer<Timestamp>,JsonDeserializer<Timestamp>{
    private final DateFormat longFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final DateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd");

    public TimestampTypeAdapter() {
    }

    public JsonElement serialize(Timestamp src, Type arg1, JsonSerializationContext arg2) {
        String dateFormatAsString = this.longFormat.format(new Date(src.getTime()));
        return new JsonPrimitive(dateFormatAsString);
    }

    public Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("The date should be a string value");
        } else {
            try {
                Date date;
                if (json.getAsString().length() == 10) {
                    date = this.shortFormat.parse(json.getAsString());
                } else {
                    date = this.longFormat.parse(json.getAsString());
                }

                return new Timestamp(date.getTime());
            } catch (ParseException var5) {
                throw new JsonParseException(var5);
            }
        }
    }
}
