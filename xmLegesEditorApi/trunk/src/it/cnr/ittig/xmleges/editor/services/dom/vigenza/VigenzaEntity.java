package it.cnr.ittig.xmleges.editor.services.dom.vigenza;

import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;

/**
 * Classe per la descrizione delle vigenze.
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
 * @version 1.0
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public class VigenzaEntity {

	
	/** Evento iniziovigore */
	Evento eInizioVigore;
	
	/** Evento finevigore */
	Evento eFineVigore;

//	/** Evento inizioEfficacia */
//	Evento eInizioEfficacia;
//	
//	/** Evento fineEfficacia */
//	Evento eFineEfficacia;
	
	/** Status della vigenza*/
	String status;
	
	String sel_text;

	public VigenzaEntity(Evento inizioVigore,
			Evento fineVigore,
			/*Evento inizioEfficacia,
			Evento fineEfficacia,*/
			String status, String text) 
	{
		setEInizioVigore(inizioVigore);
		setEFineVigore(fineVigore);
//		setEInizioEfficacia(inizioEfficacia);
//		setEFineEfficacia(fineEfficacia);
		setStatus(status);
		setSel_text(text);
	}
	
	
	public String toString() {
		String retVal = "";
		
		retVal += sel_text;
		retVal += eInizioVigore.getId()+", ";
		retVal += eFineVigore.getId()+", ";
//		retVal += eInizioEfficacia.getId()+", ";
//		retVal += eInizioEfficacia.getId()+", ";
		retVal += status;
		
		return retVal;
		
	}
	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


//	public Evento getEFineEfficacia() {
//		return eFineEfficacia;
//	}
//
//
//	public void setEFineEfficacia(Evento fineEfficacia) {
//		eFineEfficacia = fineEfficacia;
//	}
//
//
//	public Evento getEInizioEfficacia() {
//		return eInizioEfficacia;
//	}
//
//
//	public void setEInizioEfficacia(Evento inizioEfficacia) {
//		eInizioEfficacia = inizioEfficacia;
//	}


	public String getSel_text() {
		return sel_text;
	}


	public void setSel_text(String sel_text) {
		this.sel_text = sel_text;
	}

	public Evento getEInizioVigore() {
	return eInizioVigore;
}


public void setEInizioVigore(Evento inizioVigore) {
	eInizioVigore = inizioVigore;
}
	public Evento getEFineVigore() {
		return eFineVigore;
	}


	public void setEFineVigore(Evento fineVigore) {
		eFineVigore = fineVigore;
	}


	

}
