package com.sil.hbm;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity(name = "sequenceIdentifier")
public class RECONSEQ {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="RECON_SEQUENCE_IMPS")
	@SequenceGenerator(name="RECON_SEQUENCE_IMPS", sequenceName="RECON_SEQUENCE_IMPS", allocationSize =1)
	private Integer id;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}


