package it.cnr.ittig.xmleges.core.blocks.xmleditor;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.util.msg.Splash;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.xmleditor.XmlEditor;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.xmleditor.XmlEditor</code>.</h1>
 * <h1>Descrizione</h1>
 * Questa implementazione registra come default owner di Form e UtilMsg il componente
 * Frame. <br>
 * Se specificato nella configurazione attiva una splash per informare che l'applicazione
 * &egrave; in fase di caricamento.
 * <h1>Configurazione</h1>
 * La configurazione pu&ograve; avere il tag <code>&lt;splash&gt;</code> per presentare
 * una splash durante il caricamento. Tale tag deve avere l'attributo <code>image</code>
 * o <code>text</code>; pu&ograve; essere specificato inoltre quanto tempo deve essere
 * visualizzata tramite l'attributo <code>timeout</code> (default 5000 millisencodi).
 * <br>
 * Esempio:
 * 
 * <pre>
 *      &lt;splash text=&quot;loading...&quot; image=&quot;editor.png&quot; timeout=&quot;15000&quot; /&gt;
 * </pre>
 * 
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.preference.PreferenceManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.frame.Frame:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.form.Form:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.util.msg.UtilMsg:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * Nessuno.
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
public class XmlEditorImpl implements XmlEditor, Loggable, Serviceable, Configurable, Initializable, Startable {
	Logger logger;

	PreferenceManager preferenceManager;

	Frame frame;

	Form form;

	UtilMsg utilMsg;

	boolean hasSplash = false;

	String icon;

	String text;

	int timeout = 5000;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		this.preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
		this.frame = (Frame) serviceManager.lookup(Frame.class);
		this.form = (Form) serviceManager.lookup(Form.class);
		this.utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		try {
			Configuration c = configuration.getChild("splash");
			try {
				icon = c.getAttribute("image");
				hasSplash = true;
			} catch (ConfigurationException ex) {
			}
			try {
				text = c.getAttribute("text");
				hasSplash = true;
			} catch (ConfigurationException ex) {
			}
			timeout = c.getAttributeAsInteger("timeout", timeout);
		} catch (Exception ex) {
		}
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		utilMsg.setDefaultOwner(frame.getComponent());
		if (hasSplash) {
			Splash splash = utilMsg.getSplash();
			splash.setTimeout(timeout);
			splash.setImage(icon);
			splash.setText(text);
			splash.show();
		}
		form.setDefaultOwner(frame.getComponent());
	}

	// ///////////////////////////////////////////////////// Startable Interface
	public void start() throws java.lang.Exception {
		this.frame.show();
	}

	public void stop() throws java.lang.Exception {
	}

}
