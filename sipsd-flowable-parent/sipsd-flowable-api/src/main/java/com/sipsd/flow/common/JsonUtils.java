package com.sipsd.flow.common;

import java.lang.reflect.Type;
import java.sql.Timestamp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtils {
	public static Object jsonToObj(String json, Class clazz) {
		return getGson().fromJson(json, clazz);
	}

	public static Gson getGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
		Gson GSON = gsonBuilder.create();
		return GSON;
	}

	public static String toJson(Object obj) {
		return getGson().toJson(obj);
	}

	public static String toJson(Object obj, Type typeToken) {
		return getGson().toJson(obj, typeToken);
	}
}
