package it.cnr.ittig.xmleges.core.blocks.exec;

/**
 * Listener per ricevere eventi forniti da ThreadedStreamReader.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 * @version 1.0
 */
public interface ThreadedStreamReaderListener {

	/**
	 * Metodo invocato su un evento di esecuzione.
	 * 
	 * @param evt evento
	 */
	public void threadedStreamReaderEvent(ThreadedStreamReaderEvent evt);
}
