package it.cnr.ittig.xmleges.editor.services.dom.vigenza;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Node;

/**
 * Servizio per l'assegnazione di un intervallo di vigenza ad una porzione di
 * testo
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public interface Vigenza extends Service {
	
	

	/**
	 * Funzione per l'abilitazione dell'azione di assegnazione della vigenza
	 * 
	 * @param node nodo sul cui testo si vuole applicare la vigenza
	 * @param start inizio selezione del testo
	 * @param end fine selezione del testo
	 * @return </code>true</code> se l'azione puo' essere abilitata
	 */
	public boolean canSetVigenza(Node node);
	
	
	/**
	 * Funzione Dom per l'assegnazione di un intervallo di vigenza ad una
	 * porzione di testo
	 * 
	 * @param node nodo sul cui testo si applica la vigenza
	 * @param start inizio selezione del testo
	 * @param end fine selezione del testo
	 * @param dataInizio data di fine della vigenza
	 * @param dataFine data di inizio della vigenza
	 * @return </code>true</code> se la vigenza e' stata inserita
	 *         correttamente
	 */
	public Node setVigenza(Node node, String selectedText, int start, int end, VigenzaEntity vigenza);
	
	/**
	 * Funzione Dom per la lettura da documento della vigenza del nodo selezionato
	 * @param node: nodo selezionato
	 * @param start: inizio selezione
	 * @param end: fine selezione
	 * @return
	 */
	public VigenzaEntity getVigenza(Node node, int start, int end);
	/**
	 * Funzione per la lettura del testo marcato
	 * @return
	 */
	public String getSelectedText();
	
	/**
	 * Funzione per verificare se il documento ha almeno una vigenza
	 * @return 
	 */
	public boolean isVigente();
	/**
	 * Funzione che cambia il tipo di documento in funzione delle vigenze presenti
	 *
	 */
	public void setTipoDocVigenza();
	
	/**
	 * Funzione che aggiorna le vigenze che fanno riferimento 
	 * agli eventi cancellati dal ciclo di vita
	 * @param vig: vigenza da aggiornare
	 */
	public void updateVigenzaOnDoc(VigenzaEntity vig);

	
	
	


}
