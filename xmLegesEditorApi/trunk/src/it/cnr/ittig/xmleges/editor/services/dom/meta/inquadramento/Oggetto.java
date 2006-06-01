package it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento;
/**
 * Classe per la gestione dell'oggetto del documento.
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * 
 */
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
