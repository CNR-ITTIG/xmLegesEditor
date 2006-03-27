package it.cnr.ittig.xmleges.core.services.util.rulesmanager;

import it.cnr.ittig.services.manager.Service;

import java.util.Collection;

import javax.swing.JMenu;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Servizio per ...
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
 * @version 1.1
 * @author <a href="mailto:mollicone@ittig.cnr.it">Maurizio Mollicone </a>
 */
public interface UtilRulesManager extends Service {

	/**
	 * Costruisce un men&egrave; con la lista dei nodi inseribili nell'ordine
	 * come: fratello successivo, figlio, fratello precedente del nodo
	 * <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @return menu con la lista dei nodi inseribili
	 */
	public JMenu createMenuInsert(Node node);

	/**
	 * Costruisce un men&egrave; con la lista dei nodi inseribili come fratello
	 * precedente del nodo <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @return menu con la lista dei nodi inseribili
	 */
	public JMenu createMenuInsertBefore(Node node);

	/**
	 * Costruisce un men&egrave; con la lista dei nodi inseribili come fratello
	 * successivo del nodo <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @return menu con la lista dei nodi inseribili
	 */
	public JMenu createMenuInsertAfter(Node node);

	/**
	 * Costruisce un men&egrave; con la lista dei nodi inseribili fome figli del
	 * nodo <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @return menu con la lista dei nodi inseribili
	 */
	public JMenu createMenuInsertNode(Node node);

	/**
	 * @param node nodo di riferimento
	 * @param start inizio della selezione
	 * @param end fine della selezione
	 * @return menu con la lista dei nodi inseribili
	 */
	public JMenu createMenuInsert(Node node, int start, int end);

	
	/**
	 * 
	 * @param parent
	 * @param new_Node
	 * @return
	 */
	public boolean orderedInsertChild(Node parent, Node new_Node);
	
	
	
	/**
	 * @param parent
	 * @param child_node
	 * @param new_Node
	 * @return
	 */
	public boolean queryCanReplaceWith(Node parent, Node child_node, Node new_Node);

	/**
	 * @param node
	 * @param start
	 * @param end
	 * @return
	 */
	public Collection queryInsertableNodeInText(Node node, int start, int end);

	/**
	 * @param node
	 * @param start
	 * @param end
	 * @param newNode
	 * @param replaceSelected
	 * @return
	 */
	public boolean insertNodeInText(Node node, int start, int end, Node newNode, boolean replaceSelected);

	/**
	 * @param node
	 * @param start
	 * @param end
	 * @param tagName
	 * @return
	 */
	public Node encloseTextInTag(Node node, int start, int end, String tagName);

	/**
	 * @param node
	 * @param start
	 * @param end
	 * @param tagName
	 * @param NSprefix
	 * @return
	 */
	public Node encloseTextInTag(Node node, int start, int end, String tagName, String NSprefix);

	/**
	 * @param text
	 * @param tagName
	 * @return
	 */
	public Node encloseTextInTag(String text, String tagName);

	/**
	 * @param text
	 * @param tagName
	 * @param NSprefix
	 * @return
	 */
	public Node encloseTextInTag(String text, String tagName, String NSprefix);

	/**
	 * Fornisce un template del nodo minimale corrispondente all'elemento
	 * <code>elem_name</code>
	 * 
	 * @param elem_name nome dell'elemento
	 * @return il nodo di default per l'elemento passato
	 */
	public Node getNodeTemplate(String elem_name);

	/**
	 * Fornisce un template del nodo minimale corrispondente all'elemento
	 * <code>elem_name</code> nel documento <code>doc</code>
	 * 
	 * @param doc il documento DOM associato
	 * @param elem_name nome dell'elemento
	 * @return il nodo di default per l'elemento passato
	 */
	public Node getNodeTemplate(Document doc, String elem_name);

	/**
	 * Indica se &egrave; possibile inserire un nodo con nome
	 * <code>nodeName</code> come fratello precedente del nodo
	 * <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @param nodeName nome del nodo da inserire
	 * @return <code>true</code> se &egrave; possibile inserire il nodo
	 */
	public boolean canInsertBefore(Node node, String nodeName);

	/**
	 * Indica se &egrave; possibile inserire un nodo con nome
	 * <code>nodeName</code> come fratello successivo del nodo
	 * <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @param nodeName nome del nodo da inserire
	 * @return <code>true</code> se &egrave; possibile inserire il nodo
	 */
	public boolean canInsertAfter(Node node, String nodeName);

	/**
	 * Inserisce un sottoalbero alla posizione <code>pos</code> del nodo testo
	 * <code>node</code>.<br>
	 * Se il parametro <code>destructure</code> &egrave; <code>true</code>
	 * l'inserimento tenter&agrave; di appiattire i livelli del sottoalbero fino
	 * a quando &egrave; possibile inserirlo correttamente. In questo caso
	 * alcune parti della struttura di partenza possono essere perse.
	 * 
	 * @param node nodo testo nel quale inserire il sottoalbero
	 * @param pos posizione di inserimento
	 * @param destructure <code>true</code> se deve essere eliminata la
	 *        struttura
	 * @return <code>true</code> se l'inserimento ha avuto successo
	 */
	public boolean insertSubTreeInText(Node node, int pos, boolean destructure);

	/**
	 * Indica se la dtd del documento aperto &egrave; una dtd base (light)
	 * 
	 * @return <code>true</code> se &egrave; una dtd base (light)
	 */
	public boolean isDtdBase();

	/**
	 * Indica se la dtd del documento aperto &egrave; una dtd per disegni di
	 * legge
	 * 
	 * @return <code>true</code> se &egrave; una dtd per disegni di legge
	 */
	public boolean isDtdDL();

}
