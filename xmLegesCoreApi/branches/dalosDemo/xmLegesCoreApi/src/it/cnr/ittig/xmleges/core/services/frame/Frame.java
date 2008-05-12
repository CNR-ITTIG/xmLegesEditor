package it.cnr.ittig.xmleges.core.services.frame;

import it.cnr.ittig.services.manager.Service;

import java.awt.Component;
import java.util.Properties;

/**
 * Servizio per la gestione della finestra principale dell'applicazione. <br>
 * Ogni pannello di modifica che intende essere visualizzato deve implementare
 * l'interfaccia <code>Pane</code> e registrarsi sul frame attraverso il
 * metodo <code>addPane</code>. Le aree sul quale il pannello pu&ograve;
 * essere visualizzato sono espresse tramite i valori:
 * <ul>
 * <li><code>Frame.TOP_LEFT</code>;</li>
 * <li><code>Frame.TOP_CENTER</code>;</li>
 * <li><code>Frame.BOTTOM_LEFT</code>;</li>
 * <li><code>Frame.BOTTOM_CENTER</code>.</li>
 * </ul>
 * che rappresentano le seguenti posizioni:
 * 
 * <pre>
 *         
 *          +-------------+---------------+
 *          :  TOP_LEFT   :  TOP_CENTER   :
 *          +-------------+---------------+
 *          : BOTTOM_LEFT : BOTTOM_CENTER :
 *          +-------------+---------------+
 *          
 * </pre>
 * 
 * <br>
 * Il componente che implementa questo servizio deve preoccuparsi della gestione
 * dell'attivazione e disattivazione dei pannelli ed emettere l'opportuno evento (
 * <code>PaneFocusGainedEvent</code>,<code>PaneFocusLostEvent</code>,
 * <code>PaneActivatedEvent</code> e <code>PaneDeactivatedEvent</code>)
 * tramite il componente <code>EventManager</code>.
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
 * @see it.cnr.ittig.xmleges.core.services.frame.Pane
 * @see it.cnr.ittig.xmleges.core.services.frame.PaneFocusGainedEvent
 * @see it.cnr.ittig.xmleges.core.services.frame.PaneFocusLostEvent
 * @see it.cnr.ittig.xmleges.core.services.frame.PaneActivatedEvent
 * @see it.cnr.ittig.xmleges.core.services.frame.PaneDeactivatedEvent
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 */
public interface Frame extends Service {

	/** Indica la posizione del pannello all'interno del frame: alto sinistra. */
	public final static String TOP_LEFT = "top-left";

	/** Indica la posizione del pannello all'interno del frame: alto centrale. */
	public final static String TOP_CENTER = "top-center";

	/** Indica la posizione del pannello all'interno del frame: basso sinistra. */
	public final static String BOTTOM_LEFT = "bottom-left";

	/** Indica la posizione del pannello all'interno del frame: basso centrale. */
	public final static String BOTTOM_CENTER = "bottom-center";

	/**
	 * Visualizza il frame.
	 */
	public void show();

	/**
	 * Restituisce il frame come componente.
	 * 
	 * @return frame
	 */
	public Component getComponent();

	/**
	 * Aggiorna il frame.
	 */
	public void refresh();

	/**
	 * Ricarica tutti i pannelli.
	 */
	public void reloadAllPanes();

	/**
	 * Aggiunge un pannello al frame. Il parametro <code>pos</code> deve
	 * essere:
	 * <ul>
	 * <li><code>Frame.TOP_LEFT</code>;</li>
	 * <li><code>Frame.TOP_CENTER</code>;</li>
	 * <li><code>Frame.BOTTOM_LEFT</code>;</li>
	 * <li><code>Frame.BOTTOM_CENTER</code>.</li>
	 * </ul>
	 * 
	 * @param pane pannello di modifica da aggiungere
	 * @param scrollable <code>true</code> se il pannello deve essere gestito
	 *        con JScrollPane
	 */
	public void addPane(Pane pane, boolean scrollable);

	/**
	 * Restituisce il pannello di modifica attivo.
	 * 
	 * @return pannello attivo
	 */
	public Pane getActivePane();

	/**
	 * Estrae il pannello <code>pane</code> dal frame.
	 * 
	 * @param pane pannello che deve essere estratto
	 */
	public void extractPane(Pane pane);

	/**
	 * Ripristina il pannello <code>pane</code> nella posizione originale.
	 * 
	 * @param pane pannello che deve essere ripristinato
	 */
	public void restorePane(Pane pane);

	/**
	 * Blocca o riattiva l'interazione del frame principale. Con
	 * <code>setInteraction(false)</code> l'interezione &egrave; bloccata fino
	 * a <code>setInteraction(true)</code> o allo scadere di un determinato
	 * timeout dipendente dall'implementazione.
	 * 
	 * @param interaction <code>true</code> per attivare, <code>false</code>
	 *        per bloccare
	 */
	public void setInteraction(boolean interaction);

	/**
	 * Aggiorna il <code>FocusListener</code> per il pannello
	 * <code>pane</code>. Questo metodo dovrebbe essere invocato quando
	 * vengono aggiunti componenti dinamicamente al pane dopo averlo aggiunto al
	 * frame, soprattutto quando il pane contiene dei
	 * <code>javaw.swing.JTabbedPane</code>.
	 * 
	 * @param pane pannello sul quale aggiornare il <code>FocusListener</code>
	 */
	public void updateFocusListener(Pane pane);

	/**
	 * Evidenzia il titolo del pannello <code>pane</code> se non &egrave;
	 * attivo.
	 * 
	 * @param pane pannello da evidenziare
	 * @param highlight <code>true</code> per evidenziare il pannello
	 *        <code>pane</code>
	 */
	public void highlightPane(Pane pane, boolean highlight);
	
	/**
	 * 
	 * @param pane
	 * @param show
	 */
	public void setSelectedPane(Pane pane);
	
	/**
	 * 
	 * @param pane
	 * @return
	 */
	public boolean isSelectedPane(Pane pane);
	
	/**
	 * 
	 * @param prop
	 */
	public void reloadPerspective(Properties prop, boolean defaultValue);
	
}
