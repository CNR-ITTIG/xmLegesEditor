package it.cnr.ittig.xmleges.core.blocks.util.msg;

import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.util.msg.Splash;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Image;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Implementazione della splash. Se &egrave; impostata l'immagine tramite il nome allora
 * si cerca prima nell'I18n, altrimenti &egrave; considerato come nome di file.
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class SplashImpl implements Splash {
	I18n i18n;

	Component defaultOwner;

	long timeout = -1;

	SplashWindow splash;

	Icon icon;

	String text;

	public SplashImpl(I18n i18n, Component defaultOwner) {
		super();
		this.i18n = i18n;
		this.defaultOwner = defaultOwner;
	}

	public void show() {
		try {
			splash = new SplashWindow(icon, text, (Frame) defaultOwner, timeout);
		} catch (ClassCastException ex) {
			splash = new SplashWindow(icon, text, null, timeout);
		}
	}

	public void hide() {
		splash.setVisible(false);
		splash.dispose();
	}

	public void setImage(String imageName) {
		Image image = null;
		if (i18n.hasNotEmptyKey(imageName))
			image = i18n.getImageFor(imageName);
		if (image != null)
			setImage(image);
		else
			setImage(new File(imageName));
	}

	public void setImage(File imageFile) {
		setImage(new ImageIcon(imageFile.getAbsolutePath()));
	}

	public void setImage(Image image) {
		setImage(new ImageIcon(image));
	}

	protected void setImage(ImageIcon icon) {
		this.icon = icon;
	}

	public void setText(String text) {
		this.text = i18n.getTextFor(text);
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

}
