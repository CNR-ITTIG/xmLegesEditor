package it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento;


public class Infodoc {
	
	String natura="";
	
	String normativa="";
	
	String funzione="";
	
	String fonte="";
	
	public Infodoc(String natura, String normativa, String funzione, String fonte) {
		setNatura(natura);
		setNormativa(normativa);
		setFunzione(funzione);
		setFonte(fonte);
	}
	

	public void setFonte(String fonte) {
		this.fonte = fonte;
	}

	public void setFunzione(String funzione) {
		this.funzione = funzione;
	}

	public void setNatura(String natura) {
		this.natura = natura;
	}

	public void setNormativa(String normativa) {
		this.normativa = normativa;
	}


	public String getFonte() {
		return fonte;
	}


	public String getFunzione() {
		return funzione;
	}


	public String getNatura() {
		return natura;
	}


	public String getNormativa() {
		return normativa;
	}

}
