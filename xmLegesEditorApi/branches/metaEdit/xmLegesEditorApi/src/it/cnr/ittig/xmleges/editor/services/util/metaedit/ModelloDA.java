package it.cnr.ittig.xmleges.editor.services.util.metaedit;

import it.cnr.ittig.services.manager.Service;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Servizio per la fornitura di utilit&agrave specifiche Nir.
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */

public interface ModelloDA extends Service {
	/**
	 * 
	 * @return
	 */
	public Document getModelloDA();
	
	/**
	 * 
	 * @param disposizione
	 * @return
	 */
	public Node[] getClassificationPathFor(String disposizione);
	
	/**
	 * 
	 * @param disposizione
	 * @return
	 */
	public Node getClassificationFor(String disposizione);
	
	/**
	 * 
	 * @return
	 */
	public Vector getDisposizioniList();
	
	/**
	 * 
	 * @return
	 */
	public Vector getArgomentiList();
	
	/**
	 * 
	 * @param disposizione
	 * @return
	 */
	public Vector getArgomentiListFor(String disposizione);
	
	/**
	 * 
	 * @param label
	 * @return
	 */
	public boolean isDisposizione(String label);
	
	
	/**
	 * 
	 * @param label
	 * @return
	 */
	public boolean isArgomento(String label);
	
}
