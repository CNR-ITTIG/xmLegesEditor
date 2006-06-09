package it.cnr.ittig.xmleges.editor.services.form.evento;

import java.util.Vector;

import javax.swing.JTextField;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.core.services.form.CommonForm;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;

/**
 * Servizio per presentare un oggetto grafico per la visualizzazione e la
 * modifica di un evento del ciclodivita.
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public interface EventoForm extends Service, CommonForm {

	
	/**
	 * Imposta l'evento  da modificare.
	 * 
	 * @param evento evento da modificare.
	 */
	public void setEvento(Evento evento);

	/**
	 * Restituisce l'evento modificato dall'oggetto.
	 * 
	 * @return evento modificato
	 */
	public Evento getEvento();

	

	/**
	 * Apre la form
	 *
	 */
	public boolean openForm();
	
	
	
	public void setTextField(JTextField textField);
	
	

	

}
