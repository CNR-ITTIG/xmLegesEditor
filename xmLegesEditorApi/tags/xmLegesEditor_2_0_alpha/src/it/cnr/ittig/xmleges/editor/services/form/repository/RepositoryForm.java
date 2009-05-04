package it.cnr.ittig.xmleges.editor.services.form.repository;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per la visualizzazione di una form per settare la configurazione di un repository.
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
 */
public interface RepositoryForm extends Service {
	/**
	 * Apre la form del repository
	 * 
	 */
	public void openForm();

	/**
	 * Restituisce se abilitare il repository
	 * 
	 * @return </code>true</code> se &egrave abilitato il repository
	 */
	public boolean isAttivo();

	/**
	 * Restituisce il nome dell'host.
	 * 
	 * @return nome Host
	 */
	public String getHost();

	/**
	 * Restituisce il login
	 * 
	 * @return nome login
	 */
	public String getLogin();

	/**
	 * Restituisce la password
	 * 
	 * @return nome Password
	 */
	public String getPassword();

}
