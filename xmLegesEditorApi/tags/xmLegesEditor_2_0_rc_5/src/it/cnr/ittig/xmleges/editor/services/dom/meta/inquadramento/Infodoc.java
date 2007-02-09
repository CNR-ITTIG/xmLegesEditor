package it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento;
/**
 * Classe per la gestione delle informazioni del documento.
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
