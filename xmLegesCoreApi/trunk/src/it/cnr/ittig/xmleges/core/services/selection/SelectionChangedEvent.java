package it.cnr.ittig.xmleges.core.services.selection;

import java.util.EventObject;

import org.w3c.dom.Node;

/**
 * Classe evento per notificare la variazione delle selezioni.
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
 * @see it.cnr.ittig.xmleges.core.services.selection.SelectionManager
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class SelectionChangedEvent extends EventObject {

	/** Nodo attivo. */
	Node activeNode;

	/** Nodi selezionati */
	Node[] selectedNodes;

	/** Inizio selezione testo */
	int textSelectionStart;

	/** Fine selezione testo */
	int textSelectionEnd;

	/** Indica se l'evento &egrave; di variazione di nodo attivo */
	boolean activeNodeEvent = false;

	/** Indica se l'evento contiene una selezione di testo */
	boolean textSelectedEvent = false;

	/**
	 * Crea una nuova istanza di <code>SelectionChangedEvent</code> per
	 * l'evento di nuovo nodo attivo.
	 * 
	 * @param source sorgente dell'evento
	 * @param activeNode nuovo nodo attivo
	 */
	public SelectionChangedEvent(Object source, Node activeNode) {
		super(source);
		this.activeNodeEvent = true;
		this.activeNode = activeNode;
		this.selectedNodes = new Node[0];
		this.textSelectionStart = -1;
		this.textSelectionEnd = -1;
	}

	/**
	 * Crea una nuova istanza di <code>SelectionChangedEvent</code> per
	 * l'evento di nuova selezione di nodi.
	 * 
	 * @param source sorgente dell'evento
	 * @param nodes nodi selezionati
	 */
	public SelectionChangedEvent(Object source, Node[] nodes) {
		super(source);
		this.activeNode = null;
		this.selectedNodes = nodes;
		this.textSelectionStart = -1;
		this.textSelectionEnd = -1;
	}

	/**
	 * Crea una nuova istanza di <code>SelectionChangedEvent</code> per
	 * l'evento di nuova selezione di testo.
	 * 
	 * @param source sorgente dell'evento
	 * @param nodes nodi selezionati
	 */
	public SelectionChangedEvent(Object source, Node activeNode, int start, int end, boolean activeNodeChanged) {
		super(source);
		this.textSelectedEvent = true;
		this.activeNodeEvent = activeNodeChanged;
		this.activeNode = activeNode;
		this.selectedNodes = new Node[0];
		this.textSelectionStart = start;
		this.textSelectionEnd = end;
	}

	/**
	 * Indica se l'evento rappresenta un nuovo nodo attivo.
	 * 
	 * @return <code>true</code> se nuovo nodo attivo
	 */
	public boolean isActiveNodeChanged() {
		return this.activeNodeEvent;
	}

	/**
	 * Indica se l'evento rappresenta una nuova selezione di nodi.
	 * 
	 * @return <code>true</code> se nuova selezione di nodi
	 */
	public boolean isSelectedNodesChanged() {
		return this.selectedNodes != null && this.selectedNodes.length > 0;
	}

	/**
	 * Indica se l'evento rappresenta una nuova selezione di testo.
	 * 
	 * @return <code>true</code> se nuova selezione di testo
	 */
	public boolean isTextSelectedChanged() {
		return this.textSelectedEvent;
	}

	/**
	 * Restituisce il nodo attivo se <code>isActiveNodeChanged() == true</code>,
	 * altrimenti <code>null</code>.
	 * 
	 * @return nodo attivo o <code>null</code>
	 */
	public Node getActiveNode() {
		return this.activeNode;
	}

	/**
	 * Restituisce i nodi selezionati se
	 * <code>isNodesSelectedChanged() == true</code>, altrimenti un array di
	 * dimensione 0.
	 * 
	 * @return nodi selezionati o array di dimensione 0
	 */
	public Node[] getSelectedNodes() {
		return this.selectedNodes;
	}

	/**
	 * Indica se &egrave; presente una selezione di testo. Il testo selezionato
	 * &egrave; riferito al nodo attivo recuperabile tramite il metodo
	 * <code>getActiveNode()</code>.<br>
	 * La selezione pu&ograve; collassare in selezione vuota (
	 * <code>getTextSelectionStart() == getTextSelectionEnd()</code>), in
	 * questo caso indica la posizione del cursore.
	 * 
	 * @return <code>true</code> se &egrave; presente una selezione
	 */
	public boolean isTextSelected() {
		return this.textSelectionStart != -1 && this.textSelectionEnd != -1;
	}

	/**
	 * Restituisce l'indice iniziale della selezione o -1 se non &egrave;
	 * presente la selezione.
	 * 
	 * @return indice iniziale della selezione
	 */
	public int getTextSelectionStart() {
		return this.textSelectionStart;
	}

	/**
	 * Restituisce l'indice finale della selezione o -1 se non &egrave; presente
	 * la selezione.
	 * 
	 * @return indice finale della selezione
	 */
	public int getTextSelectionEnd() {
		return this.textSelectionEnd;
	}

	public String toString() {
		return "source  " + source + "activEnode " + this.activeNode + " activeNodeEvent: " + this.activeNodeEvent + " textSelectedEvent " + textSelectedEvent;
	}

}
