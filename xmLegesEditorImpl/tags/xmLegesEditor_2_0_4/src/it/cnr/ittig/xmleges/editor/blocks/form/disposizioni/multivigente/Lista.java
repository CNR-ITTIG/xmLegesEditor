package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.multivigente;

public class Lista {

	String data_evento;
	String urn_evento;
	String urn_attivo;

	public Lista(String data_evento, String urn_evento, String urn_attivo) {

		this.data_evento = data_evento;
		this.urn_evento = urn_evento;
		this.urn_attivo = urn_attivo;
	}

	public String getData_evento() {
		return data_evento;
	}

	public String getUrn_evento() {
		return urn_evento;
	}

	public String getUrn_attivo() {
		return urn_attivo;
	}
}