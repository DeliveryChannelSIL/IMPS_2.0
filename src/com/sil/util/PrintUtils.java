package com.sil.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PrintUtils {

    private static final ObjectMapper om = new ObjectMapper();

	public static void print(Object object) {
		String result;
		try {
			result = om.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            System.out.println(result);
        } catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}