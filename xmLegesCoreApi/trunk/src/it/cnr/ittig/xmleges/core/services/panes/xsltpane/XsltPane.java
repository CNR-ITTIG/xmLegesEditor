package it.cnr.ittig.xmleges.core.services.panes.xsltpane;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.core.services.frame.Pane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.Hashtable;

/**
 * Servizio per la gestione di un pannello di testo per la modifica di un
 * documento XML. <br>
 * Il pannello presenta il testo in formato HTML tramite un foglio di
 * trasformazione standart XSLT. La modifica apportata al testo presentato
 * &egrave; automaticamente validato e riportato sul DOM del documento
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>,
 */
public interface XsltPane extends Service, Pane {

	/**
	 * Imposta il file di trasformazione in HTML, il file con gli stile per i
	 * tag HTML e i parametri da passare al file di trasformazione.
	 * 
	 * @param xslt file di trasformazione
	 * @param css file con gli stili di visualizzazione
	 * @param param parametri presenti nel file di trasformazione
	 */
	public void set(File xslt, File css, Hashtable param);

	/**
	 * Aggiunge il file con gli stile per i tag HTML agli stili correnti.
	 * 
	 * @param css file con gli stili di visualizzazione
	 */
	public void addStyleSheet(File css);

	/**
	 * Imposta i parametri da passare al file di trasformazione.
	 */
	public void setParameters(Hashtable param);

	/** Posizione del pannello di modifica: NORTH */
	public final static Object NORTH = BorderLayout.NORTH;

	/** Posizione del pannello di modifica: SOUTH */
	public final static Object SOUTH = BorderLayout.SOUTH;

	/** Posizione del pannello di modifica: WEST */
	public final static Object WEST = BorderLayout.WEST;

	/** Posizione del pannello di modifica: EAST */
	public final static Object EAST = BorderLayout.EAST;

	/**
	 * Imposta il pannello di modifica dei parametri del file di trasformazione
	 * nella posizione <code>pos</code>. Il parametro <code>pos</code> pu?
	 * essere uno tra: <code>XsltPane.NORTH</code>,
	 * <code>XsltPane.SOUTH</code>,<code>XsltPane.WEST</code>,
	 * <code>XsltPane.EAST</code>.
	 * 
	 * @param paramPanel pannello di modifica dei parametri
	 * @param pos indica la posizione del pannello
	 */
	public void addParameterPanel(Component paramPanel, Object pos);

	/**
	 * Imposta il nome del pannello.
	 * 
	 * @param name nome del pannello
	 */
	public void setName(String name);

	/**
	 * Imposta l'azione da compiere sulla pressione del tasto invio.
	 * 
	 * @param insertBreakAction azione
	 */
	public void setInsertBreakAction(InsertBreakAction insertBreakAction);

	/**
	 * Imposta l'azione da compiere sulla pressione del tasto backspace o canc
	 * sugli estremi del nodo.
	 * 
	 * @param deleteNextPrevAction azione
	 */
	public void setDeleteNextPrevAction(DeleteNextPrevAction deleteNextPrevAction);

}
