package it.cnr.ittig.xmleges.editor.services.form.disposizioni.multivigente;

import org.w3c.dom.Node;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;

/**
 * <code> * Servizio per la visualizzazione della form per il posizionamento
 * manuale durante la creazione del testo multivigente</code>.
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
public interface PosizionamentoManualeForm extends Service {

	
	/**
	 * Apre la form.
	 */
	public void openForm(FormClosedListener listener, Node inserire, String nomeDomNodo);
	
	public void azzeraBordi();
	public void aggiungiBordo(String ord, String tipo);
	public boolean isChange();
	public Node getNodoSelezionato();
	public int getInizioSelezione();
	public int getFineSelezione();
}