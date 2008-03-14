package it.cnr.ittig.xmleges.editor.services.form.filenew;

import it.cnr.ittig.services.manager.Service;

import java.util.Properties;

/**
 * Servizio per l'attivazione della form "File nuovo"
 * 
 * @version 1.0
 * @author Lorenzo Sarti
 */
public interface FileNewForm extends Service {

	/**
	 * Apertura della Wizard per il file nuovo
	 */
	public void openForm();

	/**
	 * Apertura della Wizard per il file nuovo con scelta obbligata della DTD
	 */
	public void openForm(String requiredDtd);

	/**
	 * Restituisce il nome del template file
	 */
	public String getSelectedTemplate();

	/**
	 * Restituisce il nome del provvedimento
	 */
	public String getSelectedProvvedimento();

	/**
	 * Restituisce il nome della DTD
	 */
	public Properties getSelectedDTD();

	/**
	 * Restituisce <code>true</code> se &egrave; stato premuto OK per chiudere la form, <code>false</code> se &egrave;
	 * stato premuto CANCEL
	 */
	public boolean isOKClicked();
}
