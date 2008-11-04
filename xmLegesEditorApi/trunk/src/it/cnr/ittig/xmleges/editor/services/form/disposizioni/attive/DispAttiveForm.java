package it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive;

import it.cnr.ittig.services.manager.Service;


/**
 * Servizio per la visualizzazione della form per delle disposizioni attive
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
 */
public interface DispAttiveForm extends Service {

	//operazioni
	final static int NO_OPERAZIONE = 0;
	final static int INIZIO = 1;
	final static int ABROGAZIONE = 2;
	final static int SOSTITUZIONE = 3;
	final static int INTEGRAZIONE = 4;
	final static int NOVELLANDO = 5;
	final static int NOVELLA = 6;
	final static int FINE = 7;
	
	/**
	 * Apre la form.
	 */
	public void openForm(boolean cancellaCampi);
	
	/**
	 * Richiama le funzioni DOM per la scrittura dei meta della disposizione
	 */
	public void setMeta();
	
	/**
	 * Attivo/disattivo il listener su FormClosed
	 */
	public void setListenerFormClosed(boolean flag);
	
	/**
	 * Restituisce la partizione selezionata
	 */
	public String getPartizione();
	
	/**
	 * Restituisce il delimitatore selezionato
	 */
	public String[] getDelimitatori();
	
	/**
	 * Imposta la prossima operazione da eseguire
	 */
	public void setOperazioneProssima(int operazione);

	/**
	 * Restituisce l'operazione iniziale
	 */
	public int getOperazioneIniziale();

}
