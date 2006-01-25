package it.cnr.ittig.xmleges.core.services.exec.monitor;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.core.services.exec.Exec;

import java.awt.Component;

/**
 * Servizio per la visualizzazione dei log emessi da una istanza di
 * <code>it.cnr.ittig.xmleges.editor.services.exec.Exec</code>.
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
 * @see it.cnr.ittig.xmleges.core.services.exec.Exec
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface ExecMonitor extends Service {

	/**
	 * Imposta il servizio di esecuzione di un comando esterno alla Java Virtual
	 * Machine <code>exec</code> che deve essere monitorato.
	 * 
	 * @param exec servizio che deve essere monitorato
	 */
	public void setExec(Exec exec);

	/**
	 * Restituisce l'<code>ExecMonitor</code> come
	 * <code>java.awt.Component</code>.
	 * 
	 * @return oggetto grafico di <code>ExecMonitor</code>
	 */
	public Component getAsComponent();

	/**
	 * Svuota il monitor.
	 */
	public void clear();

	/**
	 * Chiude la form di dialogo del monitor.
	 */
	public void close();
}
