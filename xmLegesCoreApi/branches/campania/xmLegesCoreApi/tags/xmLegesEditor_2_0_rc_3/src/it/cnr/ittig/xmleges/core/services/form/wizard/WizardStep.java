package it.cnr.ittig.xmleges.core.services.form.wizard;

import java.awt.Component;

/**
 * Interfaccia per definire le pagine che deve essere visualizzate tramite
 * <code>it.cnr.ittig.xmleges.editor.services.form.wizard.Wizard</code>.
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
 * @see it.cnr.ittig.xmleges.core.services.form.wizard.Wizard
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface WizardStep {

	/**
	 * Restituisce la pagina come <code>java.awt.Component</code> per essere
	 * visualizzato da
	 * <code>it.cnr.ittig.xmleges.editor.services.form.wizard.Wizard</code>.
	 * 
	 * @return componente grafico per visualizzare la pagina
	 */
	public Component getComponent();
}
