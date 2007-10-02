package it.cnr.ittig.xmleges.editor.services.panes.dalos;

import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;

import java.util.EventObject;

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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public class SynsetSelectionEvent extends EventObject {

	/** Synset attivo. */
	Synset activeSynset;

	
	/**
	 * Crea una nuova istanza di <code>SelectionChangedEvent</code> per
	 * l'evento di nuovo nodo attivo.
	 * 
	 * @param source sorgente dell'evento
	 * @param activeNode nuovo nodo attivo
	 */
	public SynsetSelectionEvent(Object source, Synset activeSynset) {
		super(source);
		this.activeSynset = activeSynset;
	}

	
	/**
	 * Restituisce il nodo attivo se <code>isActiveNodeChanged() == true</code>,
	 * altrimenti <code>null</code>.
	 * 
	 * @return nodo attivo o <code>null</code>
	 */
	public Synset getActiveSynset() {
		return this.activeSynset;
	}


	public String toString() {
		return "source  " + source + "activeSynset " + this.activeSynset;
	}

}
