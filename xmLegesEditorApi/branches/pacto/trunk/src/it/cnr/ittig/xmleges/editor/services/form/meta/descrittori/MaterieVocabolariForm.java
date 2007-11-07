package it.cnr.ittig.xmleges.editor.services.form.meta.descrittori;

import it.cnr.ittig.services.manager.Service;
import it.ipiu.digest.parse.Vocabolario;

/**
 * Servizio per la gestione della form per i metadati descrittori delle materie del documento.
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public interface MaterieVocabolariForm extends Service {

	/**
	 * Apre la form per l'inserimento delle materie associate ad un documento.
	 * 
	 * @return <code>true</code> se la form &egrave; valida
	 */
	public boolean openForm();
	
	/**
	 * Restituisce i vocabolari usati nel documento.
	 * 
	 * @return vocabolari 
	 */
	public Vocabolario[] getVocabolari();

	/**
	 * Imposta sulla form i vocabolari associati al documento
	 * 
	 * @param vocabolari
	 */
	public void setVocabolari(Vocabolario[] vocabolari);

	
	


}
