package it.cnr.ittig.xmleges.core.services.action.help;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per richiamare la gestione dell'help.
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 * @version 1.0
 */
public interface HelpAction extends Service {

	/**
	 * Richiama il pannello sulle informazioni dell'applicazione.
	 */
	public void doAbout();

	/**
	 * Richiama l'help sull'indice.
	 */
	public void doContents();

}
