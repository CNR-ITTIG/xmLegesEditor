package it.cnr.ittig.xmleges.editor.services.dom.spostamento;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Node;

/**
 * Servizio per lo spostamento in alto o in basso di partizioni all'interno del
 * documento.
 * 
 * @author <a href="mailto:agnoloni@dsi.unifi.it">Tommaso Agnoloni </a>
 */
public interface Spostamento extends Service {

	/**
	 * @param node
	 * @return
	 */
	public Node canMoveUp(Node node);

	/**
	 * @param node
	 * @return
	 */
	public Node canMoveDown(Node node);

	/**
	 * @param node
	 * @return
	 */
	public boolean canUpgrade(Node node);

	/**
	 * @param node
	 * @return
	 */
	public boolean canDowngrade(Node node);

	/**
	 * @param target
	 * @param newNode
	 * @return
	 */
	public Node moveUp(Node target, Node newNode);

	/**
	 * @param target
	 * @param newNode
	 * @return
	 */
	public Node moveDown(Node target, Node newNode);

	/**
	 * @param node
	 * @return
	 */
	public Node upGrade(Node node);

	/**
	 * @param node
	 * @return
	 */
	public Node downGrade(Node node);
}
