package it.cnr.ittig.xmleges.editor.services.form.vigenza;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;

import org.w3c.dom.Node;

/**
 * Form per la selezione di una vigenza da applicare a un testo.
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
 * @author <a href="francesca.uccheddu@gmail.com">Francesca Uccheddu</a>
 */
public interface VigenzaForm extends Service {

	
	
	/**
	 * Apre il form per la selezione/modifica della vigenza (vigore ed efficacia)
	 * @param attivo Nodo selezionato a cui applicare la vigenza 
	 * @return <code>true</code> se &egrave; stato premuto ok.
	 */

	public boolean openForm(Node attivo);
	
	/**
	 * Imposta la DTD del documento (base, completo...)
	 * 
	 * @param tipoDTD DTD del documento
	 */
	public void setTipoDTD(String tipoDTD);

	/**
	 * 
	 * @return
	 */
	public Evento getInizioVigore();
		
	/**
	 * 
	 * @return
	 */
	public Evento getFineVigore();
	
	/**
	 * 
	 * @return
	 */
	public String getStatus();


	/**
	 * 
	 * @return
	 */
	public void setInizioVigore(Evento iniziovigore);
		
	/**
	 * 
	 * @return
	 */
	public void setFineVigore(Evento finevigore);
	
	/**
	 * 
	 * @return
	 */
	public void setStatus(String status);
	
	/**
	 * 
	 * @return
	 */
	public VigenzaEntity getVigenza();
	
	/**
	 * 
	 * @param vigenza
	 */
	public void setVigenza(VigenzaEntity vigenza);
	
	/**
	 * 
	 * @param testo
	 */
	public void setTestoselezionato(String testo);
	
//	public void setCiclidivita(Relazione[] relazioni, Evento[] eventi);
	
	
	
}
