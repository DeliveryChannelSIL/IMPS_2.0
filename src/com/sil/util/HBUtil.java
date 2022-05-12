package com.sil.util;

import java.io.File;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.hibernate5.encryptor.HibernatePBEEncryptorRegistry;

public class HBUtil {
	private static SessionFactory sessionFactory;
	public static String xmlPath=null;
	static
	{
		xmlPath=String.valueOf("ini/hibernate.cfg.xml");
		System.out.println(xmlPath);
	}
	public static SessionFactory getSessionFactory() {
		if (sessionFactory != null)
			return sessionFactory;
		else {
			
			Configuration configuration = new Configuration().configure(new File(xmlPath));
			StandardPBEStringEncryptor strongEncryptor = new StandardPBEStringEncryptor();
			strongEncryptor.setAlgorithm("PBEWithMD5AndDES");
			strongEncryptor.setPassword(configuration.getProperty("connection.encryptor_key"));
			HibernatePBEEncryptorRegistry registry = HibernatePBEEncryptorRegistry.getInstance();
			registry.registerPBEStringEncryptor("configurationHibernateEncryptor", strongEncryptor);
			
			String pass = strongEncryptor.decrypt(configuration.getProperty("hibernate.connection.password"));
			configuration.setProperty("hibernate.connection.password", pass);
			
			sessionFactory = configuration.buildSessionFactory();
			
			/*final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure(new File(xmlPath))
					.build();
			sessionFactory = new Configuration().configure(new File(xmlPath)).buildSessionFactory();*/
			///sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
			return sessionFactory;
		}
	}

	public static void main(String[] args) {
//		System.out.println(new File(System.getProperty("user.dir")).getAbsolutePath());
		System.out.println(HBUtil.getSessionFactory().openSession().isConnected());
	}
}
