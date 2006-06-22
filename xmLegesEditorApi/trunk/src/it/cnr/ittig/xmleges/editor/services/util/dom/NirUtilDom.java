package it.cnr.ittig.xmleges.editor.services.util.dom;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Servizio per la fornitura di utilit&agrave specifiche Nir.
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */

public interface NirUtilDom extends Service {

	/**
	 * Restituisce il nodo Nir del documento <code>doc</code>
	 * 
	 * @param doc
	 */
	public Node getNIRElement(Document doc);

	/**
	 * Restituisce il nodo tipodocumento del documento <code>doc</code>
	 * 
	 * @param doc
	 */
	public Node getTipoAtto(Document doc);

	/**
	 * Controlla se un nodo e' un container (libro, parte, titolo, capo,
	 * articolo, comma)
	 * 
	 * @param node Il nodo da controllare
	 * @return true se il nodo e' considerato un container
	 */
	public boolean isContainer(Node node);

	/**
	 * Restituisce il nodo della partizione Nir contenitore del nodo
	 * <code>node</code>.<br>
	 * 
	 * @param node nodo di cui trovare il contenitore Nir
	 */
	public Node getNirContainer(Node node);

	/**
	 * Restituisce il nodo di tipo container immediatamente superiore al nodo
	 * <code>node</code>.<br>
	 * 
	 * @param node nodo di cui trovare il parent container
	 * @return
	 */
	public Node getParentContainer(Node node);

	/**
	 * Restituisce il nodo di tipo container immediatamente superiore o uguale
	 * al nodo <code>node</code>.<br>
	 * 
	 * @param node nodo di cui trovare il container
	 * @return
	 */
	public Node getContainer(Node node);

	/**
	 * Restituisce il nodo di tipo h:p immediatamente superiore al nodo
	 * <code>node</code>.<br>
	 * 
	 * @param node nodo di cui trovare il container h:p
	 * @return
	 */
	public Node getHpContainer(Node node);

	/**
	 * Ritorna il pathname del nodo DOM
	 */
	public String getPathName(Node node);

	/**
	 * se il nodo attivo ha un nodo inline lo restituisce, altrimenti lo crea
	 * 
	 * @param doc
	 * @param activeNode
	 * @return
	 */
	public Node checkAndCreateInlineMeta(Document doc, Node activeNode);

	/**
	 * se in meta c'e' il nodo
	 * <code>nome<code> lo restituisce, altrimenti lo crea 
	 * @param doc
	 * @param nome
	 * @return
	 */
	public Node checkAndCreateMeta(Document doc, String nome);

	/**
	 * se in meta c'e' il nodo
	 * <code>nome<code> lo restituisce, altrimenti lo crea 
	 * @param node
	 * @param nome
	 * @return
	 */
	public Node checkAndCreateMeta(Document doc, Node node, String nome);

}
