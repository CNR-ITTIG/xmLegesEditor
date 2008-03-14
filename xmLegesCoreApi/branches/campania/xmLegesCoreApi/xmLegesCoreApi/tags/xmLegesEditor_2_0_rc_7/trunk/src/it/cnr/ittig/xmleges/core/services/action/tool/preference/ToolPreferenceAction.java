package it.cnr.ittig.xmleges.core.services.action.tool.preference;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per modificare le preferenze dell'applicazione.
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 * @version 1.0
 */
public interface ToolPreferenceAction extends Service {

	/**
	 * Modifica le preferenze dell'applicazione.
	 */
	public void doEditPreference();
}
