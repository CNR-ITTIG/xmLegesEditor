package it.cnr.ittig.xmleges.editor.services.dalos.action;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;

/**
 * Servizio per l'attivazione della gestione delle vigenze.
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
public interface SynsetMarkupAction extends Service {

	/**
	 * Gestione della vigenza del nodo selezionato
	 *
	 */
	public void doSynsetMarkup(Synset synset, String variant);

	
	

}
