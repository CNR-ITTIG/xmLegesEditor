package it.cnr.ittig.xmleges.editor.services.form.urn;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.core.services.form.CommonForm;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

/**
 * Servizio per presentare un oggetto grafico per la visualizzazione e la
 * modifica di una urn.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface UrnForm extends Service, CommonForm {

	public void setPartizioni(boolean partizioni);

	public boolean isPartizioni();

	public void setAnnessi(boolean annessi);

	public boolean isAnnessi();

	public void setAttiGiaCitati(boolean citati);

	public boolean isAttiGiaCitati();

	/**
	 * Imposta la urn da modificare.
	 * 
	 * @param urn urn da modificare.
	 */
	public void setUrn(Urn urn);

	/**
	 * Restituisce la urn modificata dall'oggetto.
	 * 
	 * @return urn modificata
	 */
	public Urn getUrn();

	/**
	 * Imposta la visualizzazione del pulsante di apertura della form di
	 * modifica della URN.
	 * 
	 * @param visible <code>true</code> per visualizzare il pulsante di
	 *        apertura della form
	 */
	public void setOpenFormVisible(boolean visible);

	public void openForm();

	/**
	 * Apre la form per la modifica della URN.
	 */
	public void openForm(boolean allowPartizioni, boolean allowAnnessi, boolean allowAttigiacitati);

}
