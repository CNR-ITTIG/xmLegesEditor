package it.cnr.ittig.xmleges.editor.services.form.disposizioni.passive;

import org.w3c.dom.Node;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;

/**
 * Servizio per la visualizzazione della form per delle disposizioni passive
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
public interface DispPassiveForm extends Service {

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
	 * Setta la posizione della disposizione
	 * 
	 */
	public void setPosdisposizione(Node node);
	
	/**
	 * Setta l'id del novellando
	 * 
	 */
	public void setNovellando(String id, String iniziovigore, String finevigore, String status, boolean nuovo);

	/**
	 * Setta l'id della novella
	 * 
	 */
	public void setNovella(String id);

	/**
	 * Setta l'id della novella
	 * 
	 */
	public void setStatus(String status);

	/**
	 * Costruisce la vigenza del nodo node a secondo il tipo di disposizione dsp
	 * 
	 */
	public VigenzaEntity makeVigenza(Node node, String dsp, String status);
	
	/**
	 * Richiama le funzioni DOM per la scrittura dei meta della disposizione
	 * 
	 */
	public void setMeta();
}
