package it.cnr.ittig.xmleges.core.blocks.action.tool.contents;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.tool.contents.ContentsValidatorAction;
import it.cnr.ittig.xmleges.core.services.bars.Bars;
import it.cnr.ittig.xmleges.core.services.bars.StatusBar;
import it.cnr.ittig.xmleges.core.services.contents.ContentsValidator;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.tool.contents.ContentsValidatorAction</code>.
 * </h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.form.Form:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
 * 
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
public class ContentsValidatorActionImpl extends AbstractAction implements ContentsValidatorAction, Loggable, Serviceable, Initializable, Startable {

	Logger logger;

	ActionManager actionManager;

	ContentsValidator validator;

	UtilMsg utilMsg;

	Bars bars;

	StatusBar statusBar;

	PreferenceManager preferenceManager;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		validator = (ContentsValidator) serviceManager.lookup(ContentsValidator.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		statusBar = ((Bars) serviceManager.lookup(Bars.class)).getStatusBar();
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("tool.contents", this);
	}

	// ///////////////////////////////////////////////////// Startable Interface
	public void start() throws java.lang.Exception {
		String enable = preferenceManager.getPreferenceAsString(getClass().getName());
		doEnable(Boolean.valueOf(enable).booleanValue());
	}

	public void stop() throws java.lang.Exception {
		preferenceManager.setPreference(getClass().getName(), "" + validator.isEnabled());
	}

	// /////////////////////////////////////// ContentsValidatorAction Interface
	public void doEnable(boolean enable) {
		validator.setEnabled(enable);
		statusBar.setText("tool.contents.status." + (enable ? "enable" : "disable"), "contents");
	}

	// ////////////////////////////////////////////////// AbstractAction Extends
	public void actionPerformed(ActionEvent e) {
		boolean rin = validator.isEnabled();
		String str = "tool.contents.msg." + (rin ? "disable" : "enable");
		boolean ris = utilMsg.msgYesNo(str);
		doEnable((rin && !ris) || (!rin && ris));
	}

}
