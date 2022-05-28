package com.sil.prop;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationLoader {
	private static Properties systemConfig = null;
	private static Properties msgConfig = null;
	private static Properties branchNums = null;
	private static Properties bin = null;
	private static Properties param = null;
	private static Properties impsISOMessageConfig = null;
	
	/**
	 * This method is use to get the system configuration.
	 * 
	 * @param reload
	 *            if true it will reload the property file.
	 * @return properties object
	 */
	public static Properties getSystemConfig(boolean reload) {
		if (systemConfig == null || reload) {
			try {
				systemConfig = new Properties();
				systemConfig.load(new FileInputStream("ini\\system.properties"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return systemConfig;
	}
	public static Properties getParameters(boolean reload) {
		if (param == null || reload) {
			try {
				param = new Properties();
				param.load(new FileInputStream("ini/parameters.properties"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return param;
	}
	public static Properties getBinProperties(boolean reload) {
		if (bin == null || reload) {
			try {
				bin = new Properties();
				param.load(new FileInputStream("ini\\bin.properties"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bin;
	}
	public static Properties getMessageConfig(boolean reload) {
		if (msgConfig == null || reload) {
			try {
				msgConfig = new Properties();
				msgConfig.load(new FileInputStream("ini\\ERRORCONSTANTS.properties"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return msgConfig;
	}

	
	public static Properties getBranchNum(boolean reload) {
		if (branchNums == null || reload) {
			try {
				branchNums = new Properties();
				branchNums.load(new FileInputStream("ini\\branchnum.properties"));
				} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return branchNums;
	}
	/**
	 * loads the impsisomessage.properties file.
	 * 
	 * @param reload
	 *            if want to reload the property file
	 * @return properties object
	 */
	public static Properties getIMPSConfig(boolean reload) {
		if (impsISOMessageConfig == null || reload) {
			try {
				impsISOMessageConfig = new Properties();
				impsISOMessageConfig.load(new FileInputStream("ini\\impsisomessage.properties"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return impsISOMessageConfig;
	}
	public static void main(String[] args) {
		
	}
}
