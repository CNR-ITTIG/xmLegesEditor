package it.cnr.ittig.xmleges.editor.services.form.fileexport;

import it.cnr.ittig.services.manager.Service;


/**
 * Servizio per l'attivazione della form "File Export"
 * 
 * @version 1.0
 * @author Tommaso Agnoloni
 */
public interface FileExportForm extends Service {

	/**
	 * Apertura della Form per l'esportazione di documenti multivigenti
	 * 
	 * @return <code>true</code> se la form &egrave; valida
	 */
	public boolean openForm();
	
	/**
	 * 
	 * @return
	 */
	public boolean isMonoVigente();
	
	
	/**
	 * 
	 * @return
	 */
	public boolean isMultiVigente();
	
	
	/**
	 * 
	 * @return
	 */
	public String getDataVigenza();
	
}
