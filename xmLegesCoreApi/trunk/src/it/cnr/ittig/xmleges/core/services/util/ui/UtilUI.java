package it.cnr.ittig.xmleges.core.services.util.ui;

import it.cnr.ittig.services.manager.Service;

import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.w3c.dom.Node;

/**
 * Servizio per avere oggetto utili per l'interfaccia grafica. Le
 * propriet&agrave; dei componenti sono recuperate tramite il servizio I18n
 * usando come prefisso il nome del componente stesso (impostato con il metodo
 * <code>setName()</code>).<br>
 * Le propriet&agrave; gestite, se supportate dal componente, sono:
 * <ul>
 * <li>text</li>
 * <li>mnemonic</li>
 * <li>icon</li>
 * <li>tooltip</li>
 * <li>accelerator</li>
 * </ul>
 * Ad esempio l'azione con nome <code>file.open</code> pu&ograve; essere
 * configurata come segue:
 * 
 * <pre>
 *     file.open.text=Open
 *     file.open.mnemonic=O
 *     file.open.icon=images/fileopen.png
 *     file.open.tooltip=Open document
 *     file.open.accelerator=Control X
 * </pre>
 * 
 * oppure il pulsante tale che <code>button.getName()="form.button.ok"</code>
 * pu&ograve; essere configurato come segue:
 * 
 * <pre>
 *     form.button.ok.text=Ok
 *     form.button.ok.mnemonic=O
 *     form.button.ok.icon=images/ok.png
 * </pre>
 * 
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
public interface UtilUI extends Service {

	/**
	 * Crea un generico menu vuoto.
	 * 
	 * @return menu
	 * @param name nome del menu
	 */
	public JMenu createMenu(String name);

	/**
	 * Crea un generico elemento di menu.
	 * 
	 * @return elemento di menu inizializzato
	 * @param action nome dell'azione su evento
	 */
	public JMenuItem createMenuItem(String action);

	/**
	 * Crea un generico elemento di menu impostando l'oggetto che gestisce
	 * l'evento.
	 * 
	 * @param action nome dell'azione su evento
	 * @param al listener per gli eventi
	 * @return elemento di menu inizializzato
	 */
	public JCheckBoxMenuItem createCheckBoxMenuItem(String action, ActionListener al);

	/**
	 * Crea un elemento di menu spuntabile.
	 * 
	 * @return elemento di menu inizializzato
	 * @param action nome dell'azione su evento
	 */
	public JCheckBoxMenuItem createCheckBoxMenuItem(String action);

	/**
	 * Crea un elemento di menu spuntabile impostando l'oggetto che gestisce
	 * l'evento.
	 * 
	 * @param action nome dell'azione su evento
	 * @param al listener per gli eventi
	 * @return elemento di menu inizializzato
	 */
	public JMenuItem createMenuItem(String action, ActionListener al);

	/**
	 * Crea un generico pulsante.
	 * 
	 * @param action nome dell'azione su evento
	 * @return il pulsante inizializzato
	 */
	public JButton createButton(String action);

	/**
	 * Crea un generico pulsante recuperando impostando l'oggetto che gestisce
	 * l'evento.
	 * 
	 * @param action nome dell'azione su evento
	 * @param al listener per gli eventi
	 * @return il pulsante inizializzato
	 */
	public JButton createButton(String action, ActionListener al);

	/**
	 * Crea un generico pulsante per le toolbar (con margini 0 e rollover
	 * abilitato).
	 * 
	 * @return il pulsante inizializzato
	 * @param action nome dell'azione su evento
	 */
	public JButton createButtonForToolbar(String action);

	/**
	 * Crea un generico pulsante per le toolbar (con margini 0 e rollover
	 * abilitato) impostando l'oggetto che gestisce l'evento.
	 * 
	 * @param action nome dell'azione su evento
	 * @param al listener per gli eventi
	 * @return il pulsante inizializzato
	 */
	public JButton createButtonForToolbar(String action, ActionListener al);

	/**
	 * Crea un generico pulsante on/off.
	 * 
	 * @param action nome dell'azione su evento
	 * @return il pulsante inizializzato
	 */
	public JToggleButton createToggleButton(String action);

	/**
	 * Crea un generico pulsante on/off impostando l'oggetto che gestisce
	 * l'evento.
	 * 
	 * @param action nome dell'azione su evento
	 * @param al listener per gli eventi
	 * @return il pulsante inizializzato
	 */
	public JToggleButton createToggleButton(String action, ActionListener al);

	/**
	 * Imposta, se non sono gi&agrave; presenti, le propriet&agrave;:
	 * <ul>
	 * <li>text</li>
	 * <li>mnemonic</li>
	 * <li>icon</li>
	 * <li>tooltip</li>
	 * <li>accelerator</li>
	 * </ul>
	 * 
	 * @param actionName nome dell'azione
	 * @param action azione
	 * @return azione configurata
	 */
	public Action applyI18n(String actionName, Action action);

	/**
	 * Imposta, se non sono gi&agrave; presenti, le propriet&agrave;:
	 * <ul>
	 * <li>text</li>
	 * <li>mnemonic</li>
	 * <li>icon</li>
	 * <li>tooltip</li>
	 * <li>accelerator</li>
	 * </ul>
	 * I componenti con le relative propriet&agrave supportate sono:
	 * 
	 * <pre>
	 *     JComponent    : tooltip
	 *     JLabel        : tooltip, text, icon
	 *     AbstractButton: tooltip, text, icon, mnemonic
	 * </pre>
	 * 
	 * @param comp componente
	 * @return componente configurato
	 */
	public JComponent applyI18n(JComponent comp);

	/**
	 * Costruisce un <code>javax.swing.tree.DefaultTreeModel</code> che
	 * contiene il nodo DOM <code>node</code> e tutto il suo sottoalbero. <br>
	 * Ogni nodo del <code>DefaultTreeModel</code> ha come
	 * <code>userObject</code> il nodo dell'albero opportuno.
	 * 
	 * @param node nodo DOM
	 * @return <code>DefaultTreeModel</code> che rappresenta il sottoalbero di
	 *         <code>node</code>
	 */
	public DefaultTreeModel createDefaultTreeModel(Node node);

	/**
	 * Costruisce un <code>javax.swing.tree.DefaultMutableTreeNode</code> dove
	 * lo <code>userObject</code> &egrave; il nodo stesso. <br>
	 * Se <code>deep</code> &egrave; <code>true</code> allora il viene
	 * costruito tutto il sottoalbero di <code>node</code>.
	 * 
	 * @param node node DOM
	 * @param deep <code>true</code> se deve essere costruito tutto il
	 *        sottoalbero
	 * @return <code>DefaultMutableTreeNode</code> contenente il nodo
	 *         <code>node</code>
	 */
	public DefaultMutableTreeNode createDefaultMutableTreeNode(Node node, boolean deep);

}
