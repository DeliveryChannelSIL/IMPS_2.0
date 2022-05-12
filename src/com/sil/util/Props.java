package com.sil.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Props {
	public	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(Props.class);	
	public static final Properties batchProps = new Properties();
	
	static {
		FileInputStream batchIN = null;
		try {
			File batchFile = new File("ini\\batch.properties");
			batchIN = new FileInputStream(batchFile);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			batchProps.load(batchIN);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static String getBatchProperty(String property)
	{
		return batchProps.getProperty(property);
	}
	public static void main(String[] args) {
		logger.error("Property IMPS Value : "+getBatchProperty("IMPS"));
		System.out.println("Property IMPS Value : "+getBatchProperty("ABB"));
	}
}
