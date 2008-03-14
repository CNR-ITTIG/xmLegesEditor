package it.cnr.ittig.xmleges.core.services.printer;

import it.cnr.ittig.services.manager.Service;

import java.awt.Component;

/**
 * Servizio per la gestione della stampa di un componente grafico.
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface Printer extends Service {

	/**
	 * Stampa il compenente <code>comp</code>.
	 * 
	 * @param comp componente da stampare
	 */
	public void print(Component comp);

}
