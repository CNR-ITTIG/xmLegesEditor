package it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento;

public class InfoMancanti {

	String mTitolodoc="";
	
	String mTipodoc="";
	
	String mDatadoc="";
	
	String mNumdoc="";
	
	String mEmanante="";
	
	public InfoMancanti(String titolodoc, String tipodoc, String datadoc, String numdoc, String emanante) {
		setMTitolodoc(tipodoc);
		setMTipodoc(titolodoc);
		setMDatadoc(datadoc);
		setMNumdoc(numdoc);
		setMEmanante(emanante);
	}

	public String getMDatadoc() {
		return mDatadoc;
	}

	public void setMDatadoc(String datadoc) {
		mDatadoc = datadoc;
	}

	public String getMEmanante() {
		return mEmanante;
	}

	public void setMEmanante(String emanante) {
		mEmanante = emanante;
	}

	public String getMNumdoc() {
		return mNumdoc;
	}

	public void setMNumdoc(String numdoc) {
		mNumdoc = numdoc;
	}

	public String getMTipodoc() {
		return mTipodoc;
	}

	public void setMTipodoc(String tipodoc) {
		mTipodoc = tipodoc;
	}

	public String getMTitolodoc() {
		return mTitolodoc;
	}

	public void setMTitolodoc(String titolodoc) {
		mTitolodoc = titolodoc;
	}
	
}
