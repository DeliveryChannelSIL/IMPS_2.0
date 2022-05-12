package com.sil.hbm;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.hibernate.Session;

import com.sil.util.HBUtil;

@Entity(name = "sequenceIdentifier")
public class SequenceIdentifier {
	public	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(SequenceIdentifier.class);
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="test")
	@SequenceGenerator(name="test", sequenceName="test", allocationSize =1)
	private Integer id;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	
	public static void main(String[] args) {
		SequenceIdentifier seq = new SequenceIdentifier();
		Session session = HBUtil.getSessionFactory().openSession();
		session.save(seq);
		session.close();
		logger.error("Int : "+seq.getId());		
		HBUtil.getSessionFactory().close();
		
	}
}


