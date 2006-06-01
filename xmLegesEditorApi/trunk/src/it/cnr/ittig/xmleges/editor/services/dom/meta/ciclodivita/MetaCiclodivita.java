package it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;

/**
 * Servizio per l'inserimento del ciclodivita del documento.
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
public interface MetaCiclodivita extends Service {

	
	/**
	 * Restituisce gli eventi associati al documento.
	 * 
	 * @return eventi del documento
	 */
	public Evento[] getEventi();

	/**
	 * Imposta gli eventi sul ciclodivita del documento.
	 * 
	 * @param eventi eventi
	 */
	public void setEventi(Evento[] eventi);

	/**
	 * Restituisce le relazioni del documento con altri documenti
	 * 
	 * @return relazioni con altri documenti
	 */
	public Relazione[] getRelazioni();

	/**
	 * Imposta le relazioni del documento con altri documenti
	 * 
	 * @param relazioni relazioni con altri documenti
	 */
	public void setRelazioni(Relazione[] relazioni);
	
	/**
	 * 
	 * @param eventi: vettore contenente le relazioni degli eventi
	 * @param relazioniUlteriori: vettore contenente le relazioni ulteriori 
	 * @return: un unico vettore con la fusione dei due
	 */
	public Relazione[] mergeRelazioni(Evento[] eventi, Relazione[] relazioniUlteriori);
	
	/**
	 * 
	 * @param eventi
	 * @param relazioni
	 * @return
	 */
	public Relazione[] getRelazioniUlteriori(Evento[] eventi, Relazione[] relazioni);
	/**
	 * 
	 * @return: restituisce tutti gli id degli eventi coinvolti nelle vigenze del documento
	 */
	public String[] getEventiOnVigenza();
	/**
	 * 
	 * @return: restituisce l'elenco delle vigenze del documento
	 */
	public VigenzaEntity[] getVigenze();
}
