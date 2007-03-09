package it.cnr.ittig.xmleges.editor.services.form.xmleges.linker;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Node;

/**
 * Servizio per la visualizzazione della form per accedere al Parser dei
 * Riferimenti.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface XmLegesLinkerForm extends Service {

	/**
	 * Apre la form per l'analisi del testo <code>text</code>.
	 * 
	 * @param text testo da analizzare
	 * @return <code>true</code> la form &egrave stata chiusa con successo
	 */
	public boolean openForm(String text);

	/**
	 * Apre la form per l'analisi del nodo <code>node</code>.
	 * 
	 * @param node nodo da analizzare
	 * @return <code>true</code> la form &egrave stata chiusa con successo
	 */
	public boolean openForm(Node node);

	/**
	 * Apre la form per l'analisi dei nodi <code>nodes</code>.
	 * 
	 * @param nodes nodi da analizzare
	 * @return <code>true</code> la form &egrave stata chiusa con successo
	 */
	public boolean openForm(Node[] nodes);

	/**
	 * Apre la form per forzare l'analisi dell'intero documento.
	 * 
	 * @param all <code>true</code> per analizzare l'intero documento
	 * @return <code>true</code> la form &egrave stata chiusa con successo
	 */
	public void setParseAll(boolean all);

	/**
	 * Indica se l'analisi &egrave; stata effettuata su tutto il documento.
	 * 
	 * @return <code>true</code> analisi su tutto il documento
	 */
	public boolean isParseAll();

	/**
	 * Restituisce il risultato per il metodo <code>openForm(String)</code>.
	 * 
	 * @return risultato dell'analisi
	 */
	public String getResultForText();

	/**
	 * Restituisce il risultato per il metodo <code>openForm(Node)</code>.
	 * 
	 * @return risultato dell'analisi
	 */
	public String getResultForNode();

	/**
	 * Restituisce il risultato per il metodo <code>openForm(Node[])</code>.
	 * 
	 * @return risultato dell'analisi
	 */
	public String[] getResultForNodes();

	/**
	 * Restituisce il risultato dell'analisi per l'intero documento (
	 * <code>isParseAll() == true)</code>.
	 * 
	 * @return risultato dell'analisi
	 */
	public String getResultForAll();
}
