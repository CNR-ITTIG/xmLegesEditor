package it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane;

import javax.swing.text.Element;

/**
 * Interfaccia per la definizione degli elementi di XsltDocument.
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
 */
public interface XsltElement extends Element {

	/**
	 * Restituisce l'id dell'elemento.
	 * 
	 * @return id dell'elemento
	 */
	public String getId();

	/**
	 * Effettua l'aggiornamento del nodo DOM associato all'elemento impostando il testo
	 * <code>text</code>.
	 * 
	 * @param text testo da impostare sul DOM
	 * @return <code>true</code> se l'operazione &egrave; terminata con successo
	 */
	public boolean updateDom(String text);

	/**
	 * Aggiorna l'elemento recuperando il contenuto da XsltMapper.
	 * 
	 */
	public void updateFromXslt();
}
