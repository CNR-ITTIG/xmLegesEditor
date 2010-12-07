package it.cnr.ittig.xmleges.editor.services.form.fileExportTAF;

import it.cnr.ittig.services.manager.Service;


/**
 * Servizio per l'attivazione della form "File Export"
 * 
 * @version 1.0
 * @author Tommaso Agnoloni
 */
public interface FileExportTafForm extends Service {

	/**
	 * Apertura della Form per l'esportazione in testo a fronte di documenti multivigenti
	 * 
	 * @return <code>true</code> se la form &egrave; valida
	 */
	public boolean openForm();
		
	
	/**
	 * 
	 * @return
	 */
	public String getData1();
	
	/**
	 * 
	 * @return
	 */
	public String getData2();
	
}
