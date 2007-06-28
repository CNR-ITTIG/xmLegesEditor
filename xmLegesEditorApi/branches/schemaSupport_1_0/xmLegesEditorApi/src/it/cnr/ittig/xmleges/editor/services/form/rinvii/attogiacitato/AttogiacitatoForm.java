package it.cnr.ittig.xmleges.editor.services.form.rinvii.attogiacitato;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per ...
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
 * @version 1.0
 * @author <a href="mailto:sarti@dii.unisi.it">Lorenzo Sarti</a>
 */
public interface AttogiacitatoForm extends Service {
	/**
	 * Apre la form Attogiacitato
	 */
	public boolean openForm();

	/**
	 * Restituisce la urn selezionata
	 */
	public String getUrn();
}