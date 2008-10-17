package it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive;


import org.w3c.dom.Node;

import it.cnr.ittig.services.manager.Service;


/**
 * Servizio per la visualizzazione della form per la scelta dell'atto modificato
 * della disposizione attiva
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
public interface RiferimentoForm extends Service {
	
	/**
	 * Apre la form.
	 */
	public boolean openForm();
	
	/**
	 * Inizializza i valori della form
	 */
	public void initForm(Node nodoCorrente); 

	/**
	 * Restituisce la decorrenza selezionata
	 * 
	 */
	public String getRiferimento();
	
	/**
	 * Restituisce la partizione dell'unico RIF nel MOD;
	 * Stringa vuota se non ho partizioni o ho più riferimenti.
	 * 
	 */
	public String getPartizionePrimoAtto();
	
	/**
	 * Restituisce i bordi, inseriti in una eventuale nota, di tipo:
	 * <!-- frammento: partizione,numero,ordinale-part2,num2,ord2-... -->
	 * NextSibling() al rif individuato da getRiferimento().
	 * 
	 */
	public String[] getBordi();
	public String[] getBordiDaNota(Node riferimento);
}