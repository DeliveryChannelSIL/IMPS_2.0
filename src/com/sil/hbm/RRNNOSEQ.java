package com.sil.hbm;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity(name = "sequenceIdentifier")
public class RRNNOSEQ {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="RRNNOSEQ")
	@SequenceGenerator(name="RRNNOSEQ", sequenceName="RRNNOSEQ", allocationSize =1)
	private Integer id;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}


