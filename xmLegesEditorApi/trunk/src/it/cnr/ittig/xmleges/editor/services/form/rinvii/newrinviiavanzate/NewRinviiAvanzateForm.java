/*
 * Created on Apr 28, 2005 TODO To change the template for this generated file
 * go to Window - Preferences - Java - Code Style - Code Templates
 */
package it.cnr.ittig.xmleges.editor.services.form.rinvii.newrinviiavanzate;

import it.cnr.ittig.services.manager.Service;

import java.util.Vector;

/**
 * Servizio per ...
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
public interface NewRinviiAvanzateForm extends Service {

	/**
	 * Apre la form NewRinviiAvanzateForm
	 * 
	 * @param urn vettore delle urn con il quale popolare eventualmente i
	 *        controlli
	 * @param allowAnnessi impostato a true consente l'inserimento di annessi
	 * @return
	 */
	public boolean openForm(Vector urn, boolean allowAnnessi);

	public Vector getUrn();

	public Vector getAllegati();

	public Vector getComunicati();

	public String getVersione();
}
