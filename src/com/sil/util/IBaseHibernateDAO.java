package com.sil.util;

import org.hibernate.Session;
/**
 * Data access interface for domain model
 * @author MyEclipse Persistence Tools
 */
public interface IBaseHibernateDAO {
	public Session getSession(boolean bdmlTxn);
}