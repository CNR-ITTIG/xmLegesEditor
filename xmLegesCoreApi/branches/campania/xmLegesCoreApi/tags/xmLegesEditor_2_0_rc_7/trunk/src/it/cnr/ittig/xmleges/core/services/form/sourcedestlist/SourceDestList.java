package it.cnr.ittig.xmleges.core.services.form.sourcedestlist;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.core.services.form.CommonForm;

import java.util.Vector;

/**
 * Servizio per ...
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>, Lorenzo
 *         Sarti
 */

public interface SourceDestList extends Service, CommonForm {

	/**
	 * Imposta gli elementi visualizzati nella lista sorgente
	 * 
	 * @param source
	 */
	public void setSource(Vector source);

	/**
	 * Restituisce gli elementi visualizzati nella lista destinazione
	 * 
	 * @return
	 */
	public Vector getDestination();
}
