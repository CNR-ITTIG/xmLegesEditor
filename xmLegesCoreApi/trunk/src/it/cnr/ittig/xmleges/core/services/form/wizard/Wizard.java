package it.cnr.ittig.xmleges.core.services.form.wizard;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per la gestione standard delle form guidate.
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
public interface Wizard extends Service {

	/**
	 * Aggiunge una pagina alla sequenza delle pagine da visualizzare.
	 * 
	 * @param step pagina da aggiungere
	 */
	public void add(WizardStep step);

	/**
	 * Abilita o disabilita il pulsante per avanzare di una pagina.
	 * 
	 * @param enabled <code>true</code> per abilitare il pulsante
	 */
	public void setForwardEnabled(boolean enabled);

	/**
	 * Abilita o disabilita il pulsante di fine inserimento.
	 * 
	 * @param enabled <code>true</code> per abilitare il pulsante
	 */
	public void setFinishEnabled(boolean enabled);

	/**
	 * Visualizza la Wizard.
	 * 
	 * @return <code>true</code> se &egrave; stato premuto il pulsante di fine
	 *         inserimento
	 */
	public boolean show();
}
