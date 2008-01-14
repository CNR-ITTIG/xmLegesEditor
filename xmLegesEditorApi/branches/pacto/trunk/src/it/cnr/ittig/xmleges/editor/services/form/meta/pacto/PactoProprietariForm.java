package it.cnr.ittig.xmleges.editor.services.form.meta.pacto;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per la gestione della form per i metadati pacto del documento .
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 */
public interface PactoProprietariForm extends Service {

	/**
	 * Apre la form per l'inserimento dei metadati pacto associati ad un documento
	 * 
	 * @return <code>true</code> se la form &egrave; valida
	 */
	public boolean openForm();
	
	/**
	 * Restituisce i metadati proprietari del cnr.
	 * 
	 * @return metadati proprietari
	 */
	public String[] getProprietari();

	/**
	 * Imposta sulla form i metadati cnr associati al documento
	 * 
	 * @param metadati
	 */
	public void setProprietari(String[] metadati);




}
