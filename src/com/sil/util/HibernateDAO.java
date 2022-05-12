package com.sil.util;

import org.eclipse.persistence.sessions.factories.SessionFactory;
import org.hibernate.Session;
import com.sil.hbm.D009021;
import com.sil.hbm.D009021Id;


/**
 * Data access object (DAO) for domain model
 * @author MyEclipse Persistence Tools
 */
public class HibernateDAO extends SessionFactory implements IBaseHibernateDAO {
	
	public HibernateDAO(String sessionsXMLPath, String sessionName) {
		super(sessionsXMLPath, sessionName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Session getSession(boolean bdmlTxn) {
		// TODO Auto-generated method stub
		Session session=getSession(false);
		D009021Id id=new D009021Id();
		id.setLbrCode(3);
		id.setPrdCd("SB");
		D009021 d009021= session.get(D009021.class,id);
		System.out.println(d009021);
		return null;
	}
	public static void main(String[] args) {
		new HibernateDAO(null,null).getSession(false);
	}
}