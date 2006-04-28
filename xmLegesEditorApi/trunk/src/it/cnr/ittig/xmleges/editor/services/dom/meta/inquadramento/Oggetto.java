package it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento;

public class Oggetto {

	String finalita="";
	
	String destinatario="";
	
	String territorio="";
	
	String attivita="";
	
	public Oggetto(String finalita, String destinatario, String territorio, String attivita) {
		setFinalita(finalita);
		setDestinatario(destinatario);
		setTerritorio(territorio);
		setAttivita(attivita);
	}
	public String getAttivita() {
		return attivita;
	}
	public void setAttivita(String attivita) {
		this.attivita = attivita;
	}
	public String getDestinatario() {
		return destinatario;
	}
	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}
	public String getFinalita() {
		return finalita;
	}
	public void setFinalita(String finalita) {
		this.finalita = finalita;
	}
	public String getTerritorio() {
		return territorio;
	}
	public void setTerritorio(String territorio) {
		this.territorio = territorio;
	}
}
