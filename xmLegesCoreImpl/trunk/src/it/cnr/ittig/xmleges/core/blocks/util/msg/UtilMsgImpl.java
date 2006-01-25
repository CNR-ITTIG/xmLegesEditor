package it.cnr.ittig.xmleges.core.blocks.util.msg;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.util.msg.Splash;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * <h1>Implementazione del servizio <code>it.cnr.ittig.xmleges.editor.services.util.msg.UtilMsg</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.i18n.I18n:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>util.msg.error: titolo di default per i messaggi di errore;</li>
 * <li>util.msg.info:titolo di default per i messaggi di informazione;</li>
 * <li>util.msg.question:titolo di default per i messaggi di yes/no;</li>
 * <li>util.msg.warning:titolo di default per i messaggi di warning;</li>
 * </ul>
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
public class UtilMsgImpl implements UtilMsg, Loggable, Serviceable {
	Logger logger;

	I18n i18n;

	static Component defaultOwner = null;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		i18n = (I18n) serviceManager.lookup(I18n.class);
	}

	// /////////////////////////////////////////////////////// UtilMsg Interface
	public void setDefaultOwner(Component owner) {
		defaultOwner = owner;
	}

	public void msgError(String msg) {
		msgError(msg, "util.msg.error");
	}

	public void msgError(String msg, String title) {
		msgError(defaultOwner, msg, title);
	}

	public void msgError(Component c, String msg) {
		msgError(c, msg, "util.msg.error");
	}

	public void msgError(Component c, String msg, String title) {
		JOptionPane.showMessageDialog(c, i18n.getTextFor(msg), i18n.getTextFor(title), JOptionPane.ERROR_MESSAGE);
	}

	public void msgInfo(String msg) {
		msgInfo(msg, "util.msg.info");
	}

	public void msgInfo(String msg, String title) {
		msgInfo(defaultOwner, msg, title);
	}

	public void msgInfo(Component c, String msg) {
		msgInfo(c, msg, "util.msg.info");
	}

	public void msgInfo(Component c, String msg, String title) {
		JOptionPane.showMessageDialog(c, i18n.getTextFor(msg), i18n.getTextFor(title), JOptionPane.INFORMATION_MESSAGE);
	}

	public boolean msgYesNo(String msg) {
		return msgYesNo(msg, "util.msg.question");
	}

	public boolean msgYesNo(String msg, String title) {
		return msgYesNo(defaultOwner, msg, title);
	}

	public boolean msgYesNo(Component c, String msg) {
		return msgYesNo(c, msg, "util.msg.question");
	}

	public boolean msgYesNo(Component c, String msg, String title) {
		return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(c, i18n.getTextFor(msg), i18n.getTextFor(title), JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
	}

	public int msgYesNoCancel(String msg) {
		return msgYesNoCancel(msg, "util.msg.question");
	}

	public int msgYesNoCancel(String msg, String title) {
		return msgYesNoCancel(defaultOwner, msg, title);
	}

	public int msgYesNoCancel(Component c, String msg) {
		return msgYesNoCancel(c, msg, "util.msg.question");
	}

	public int msgYesNoCancel(Component c, String msg, String title) {
		switch (JOptionPane.showConfirmDialog(c, i18n.getTextFor(msg), i18n.getTextFor(title), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)) {
		case JOptionPane.NO_OPTION:
			return 0;
		case JOptionPane.YES_OPTION:
			return 1;
		default:
			return 2;
		}
	}

	public boolean msgWarning(String msg) {
		return msgWarning(msg, "util.msg.warning");
	}

	public boolean msgWarning(String msg, String title) {
		return msgWarning(defaultOwner, msg, title);
	}

	public boolean msgWarning(Component c, String msg) {
		return msgWarning(c, msg, "util.msg.warning");
	}

	public boolean msgWarning(Component c, String msg, String title) {
		return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(c, i18n.getTextFor(msg), i18n.getTextFor(title), JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);
	}

	public Splash getSplash() {
		return new SplashImpl(i18n, defaultOwner);
	}

}
