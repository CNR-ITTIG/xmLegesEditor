package it.cnr.ittig.xmleges.editor.services.form.rinvii.newrinvii;

import it.cnr.ittig.services.manager.Service;

import java.util.Vector;

/**
 * Servizio per l'inserimento e la generazione della URN.
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
 * @author <a href="mailto:sarti@dii.unisi.it">Lorenzo Sarti </a>
 */
public interface NewRinviiForm extends Service {

	/**
	 * Apre la form NewRinviiForm
	 * 
	 * @param urn urn con la quale popolare eventualmente i controlli
	 * @param allowMRif impostato a true consente l'inserimento di riferimenti
	 *        multipli
	 * @param allowPartizioni impostato a true consente il riferimento a
	 *        partizioni specifiche di un provvedimento
	 * @param allowAnnessi impostato a true consente l'inserimento di annessi
	 * @param allowAttigiacitati impostato a true consente l'apertura della form
	 *        degli atti gia' citati
	 * @return <code>true</code> se &egrave; stato premuto ok
	 */
	public boolean openForm(String urn, boolean allowMRif, boolean allowPartizioni, boolean allowAnnessi, boolean allowAttigiacitati);

	public boolean openForm(String[] urnString, boolean allowMRif, boolean allowPartizioni, boolean allowAnnessi, boolean allowAttigiacitati);

	/**
	 * @return restituisce un vettore di oggetti urn
	 */
	public Vector getUrn();

	/**
	 * @return restituisce un vettore i cui elementi sono le descrizioni
	 *         testuali dei nodi componenti un mrif
	 */
	public Vector getDescrizioneMRif();

}
