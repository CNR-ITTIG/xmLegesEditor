package it.cnr.ittig.xmleges.core.services.util.msg;

import java.awt.Image;
import java.io.File;

/**
 * Splash per visualizzare dei messaggi durante l'esecuzione di processi che
 * richiedono molto tempo.
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface Splash {

	/**
	 * Visualizza la splash.
	 */
	public void show();

	/**
	 * Nasconde la splash.
	 */
	public void hide();

	/**
	 * Imposta nella splash l'immagine di nome <code>imageName</code>.
	 * 
	 * @param imageFileName nome del file
	 */
	public void setImage(String imageName);

	/**
	 * Imposta nella splash l'immagine recuperandola dal file
	 * <code>imageFile</code>.
	 * 
	 * @param imageFile file con l'immagine
	 */
	public void setImage(File imageFile);

	/**
	 * Imposta nella splash l'immagine con <code>image</code>.
	 * 
	 * @param image immagine
	 */
	public void setImage(Image image);

	/**
	 * Imposta nella splash il testo <code>text</code>.
	 * 
	 * @param text testo da visualizzare
	 */
	public void setText(String text);

	/**
	 * Imposta dopo quanto tempo la splash deve essere chiusa.
	 * 
	 * @param timeout tempo di attesa prima di essere chiusa (im millisecondi)
	 */
	public void setTimeout(long timeout);
}
