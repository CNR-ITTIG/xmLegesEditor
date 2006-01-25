package it.cnr.ittig.xmleges.core.services.selection;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Node;

/**
 * Servizio per la gestione delle selezioni dei nodi del documento e del testo
 * all'interno di un nodo di tipo testo.
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface SelectionManager extends Service {

	/**
	 * Imposta il nodo attivo.
	 * 
	 * @param source sorgente che imposta il nodo attivo
	 * @param node nodo attivo
	 */
	public void setActiveNode(Object source, Node node);

	/**
	 * Restituisce il nodo attivo.
	 * 
	 * @return nodo attivo
	 */
	public Node getActiveNode();

	/**
	 * Imposta i nodi selezionati
	 * 
	 * @param source sorgente che imposta i nodi selezionati
	 * @param nodes nodi selezionati
	 */
	public void setSelectedNodes(Object source, Node[] nodes);

	/**
	 * Restituisce i nodi selezionati
	 * 
	 * @return nodi selezionati
	 */
	public Node[] getSelectedNodes();

	/**
	 * Imposta l'inizio e la fine della selezione del testo all'interno dei un
	 * nodo di tipo testo. L'inizio e la fine sono offset rispetto al testo del
	 * nodo (0 indica il primo carattere del nodo testo). <br>
	 * Se <code>start</code> &egrave uguale a <code>end</code> la selezione
	 * indica la posizione del cursore.
	 * 
	 * @param source sorgente che imposta il testo selezionato
	 * @param node nodo sul quale &egrave; stata effettuata la selezione
	 * @param start inizio della selezione
	 * @param end fine della selezione
	 */
	public void setSelectedText(Object source, Node node, int start, int end);

	/**
	 * Rimuove la selezione del testo.
	 * 
	 * @param source sorgente che rimuove il testo selezionato
	 */
	public void removeTextSelection(Object source);

	/**
	 * Informa se &egrave; presente una selezione del testo di un nodo.
	 * 
	 * @return <code>true</code> se &egrave; presente
	 */
	public boolean isTextSelected();

	/**
	 * Restituisce l'inizio della selezione del testo.
	 * 
	 * @return inizio della selezione o -1 se non presente
	 */
	public int getTextSelectionStart();

	/**
	 * Restituisce la fine della selezione del testo.
	 * 
	 * @return fine della selezione o -1 se non presente
	 */
	public int getTextSelectionEnd();

}
