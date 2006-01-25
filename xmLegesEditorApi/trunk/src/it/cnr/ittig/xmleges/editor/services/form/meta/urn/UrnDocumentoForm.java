package it.cnr.ittig.xmleges.editor.services.form.meta.urn;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

/**
 * Servizio per la gestione delle urn del documento NIR.
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public interface UrnDocumentoForm extends Service {

	/**
	 * @param urn
	 * @return
	 */
	public boolean openForm(Urn[] urn);

	/**
	 * Apre la form per l'inserimento delle urn di un documento NIR.
	 * 
	 * @return <code>true</code> se la form &egrave; valida
	 */
	public boolean openForm();

	/**
	 * Restituisce le urn del documento.
	 * 
	 * @return urn del documento
	 */
	public Urn[] getUrn();

	/**
	 * Imposta le urn del documento
	 * 
	 * @param urn urn del documento
	 */
	public void setUrn(Urn[] urn);

}
