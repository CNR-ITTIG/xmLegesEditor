package it.cnr.ittig.xmleges.editor.services.form.disposizioni.passive;

import org.w3c.dom.Node;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;

/**
 * Servizio per la visualizzazione della form per delle disposizioni passive
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
public interface DispPassiveForm extends Service {

	/**
	 * Apre la form.
	 */
	public void openForm(boolean cancellaCampi);

	/**
	 * Setta la prossima operazione da compiere
	 * 
	 */
	public void setOperazioneProssima();

	/**
	 * Setta la posizione della disposizione
	 * 
	 */
	public void setPosdisposizione(Node node);
	
	/**
	 * Setta l'id del novellando
	 * 
	 */
	public void setNovellando(String id, String iniziovigore, String finevigore, String status, boolean nuovo);

	/**
	 * Setta l'id della novella
	 * 
	 */
	public void setNovella(String id);
	
	/**
	 * Setta la parte di testo da inserire prima della nota automatica di vigenza
	 * 
	 */
	public void setPrenota(String nota);

	/**
	 * Setta la parte di testo da inserire dopo della nota automatica di vigenza
	 * 
	 */
	public void setPostnota(String nota);
	
	/**
	 * Costruisce la vigenza del nodo node a secondo il tipo di disposizione dsp
	 * 
	 */
	public VigenzaEntity makeVigenza(Node node, String dsp);
}
