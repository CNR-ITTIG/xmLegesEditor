package it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Node;

/**
 * Servizio per la visualizzazione della form delle disposizioni attive
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
	 * Ritorna il tipo di disposizione
	 * 
	 */
	public int getTipoDisposizione();
	
	/**
	 * Setta la prossima operazione da compiere
	 * 
	 */
	public void setOperazioneProssima();

	/**
	 * Setta la posizione della disposizione
	 * 
	 */
	public void setPosdisposizione(Node node);
	
	/**
	 * Setta l'id del novellando
	 * 
	 */
	public void setNovellando(String id);

	/**
	 * Setta l'id della novella
	 * 
	 */
	public void setNovella(String id);

}
