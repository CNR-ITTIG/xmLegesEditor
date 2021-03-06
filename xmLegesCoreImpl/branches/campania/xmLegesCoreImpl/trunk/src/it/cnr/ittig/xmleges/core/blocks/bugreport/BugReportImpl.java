package it.cnr.ittig.xmleges.core.blocks.bugreport;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.bugreport.BugReport;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;

import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.bugreport.BugReport</code>.</h1>
 * <h1>Descrizione</h1>
 * Questa classe implementa un buffer circolare incrementato dai vari logger. Il contenuto
 * del buffer si pu� inviare tramite mail ad un indirizzo che si pu� configurare.

 * Si pu� configurare tramite file il numero di linee del buffer e l'indirizzo mail da
 * utilizzare per l'invio del contenuto. 

 * <h1>Configurazione</h1>
 * La configurazione pu&ograve; avere i seguenti tag (tutti opzionali):
 * <ul>
 * <li><code>&lt;mailto&gt;</code>: indica l'indirizzo mail per ricevere il report; </li>
 * <li><code>&lt;maxlines&gt;</code>: indica il numero di linee del buffer.</li>
 * </ul>
 * 
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.core.services.form.Form:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.preference.PreferenceManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.util.msg.utilMsg:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li><code>form.text</code>: titolo di default della form;</li>
 * <li><code>form.text</code>: titolo di default della form;</li>
 * <li><code>form.icon</code>: icona di default della form;</li>
 * <li><code>help.contents.form.bugreport</code>: pulsante per attivare l'help;</li>
 * <li><code>generic.close</code>: pulsante di chiusura della form;</li>
 * <li><code>bugreport.mail.from</code>: indirizzo mail mittente;</li>		
 * <li><code>bugreport.mail.to</code>: indirizzo mail destinatario;</li>
 * <li><code>bugreport.mail.smtp</code>: indirizzo del server smtp;</li>		
 * <li><code>bugreport.mail.subject</code>: oggetto della mail;</li>
 * <li><code>bugreport.mail.body</code>: corpo della mail;</li>
 * <li><code>bugreport.clear</code>: pulsante di azzeramento contenuto del buffer;</li>
 * <li><code>bugreport.send</code>: pulsante di invio del contenuto del buffer;</li>
 * <li><code>bugreport.msg.from</code>: messaggio di errore sul campo from;</li>
 * <li><code>bugreport.msg.send</code>: messaggio di errore sul campo send;</li>
 * <li><code>bugreport.msg.smtp</code>: messaggio di errore sul server smtp;</li>
 * <li><code>bugreport.msg.subject</code>: messaggio di errore sull'oggetto mail;</li>
 * <li><code>bugreport.msg.body</code>: messaggio di errore sul corpo della mail;</li>
 * <li><code>bugreport.msg.log</code>: messaggio di errore sul buffer;</li>
 * <li><code>bugreport.msg.sendok</code>: messaggio di invio mail con successo;</li>
 * </ul> 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @see it.cnr.ittig.xmleges.core.services.preference.PreferenceManager
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class BugReportImpl implements BugReport, /* ActionListener,*/ Loggable, Configurable, Serviceable, Startable {
	Logger logger;

	Form form;

	PreferenceManager preferenceManager;

	UtilMsg utilMsg;

	DefaultListModel listModel = new DefaultListModel();

	String mailTo;

	JTextField mailFrom;

	JTextField mailSmtp;

	JTextField mailSubject;

	JTextArea mailBody;

	DefaultComboBoxModel comboBoxModel;;

	JComboBox logModules;

	JComboBox logLevels;

	Hashtable backupLog = new Hashtable();

	final static String allString = "ALL";

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		mailTo = configuration.getChild("mailto").getValue();
		BugReportAppender.maxLines = configuration.getChild("maxlines").getValueAsInteger(10000);		
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(getClass().getResourceAsStream("BugReport.jfrm"));
		form.setName("bugreport");
		
		form.setHelpKey("help.contents.form.bugreport");
		
		form.setSize(550, 550);
		form.setCustomButtons(new String[] { "generic.close" });
		mailFrom = (JTextField) form.getComponentByName("bugreport.mail.from");
		((JTextField) form.getComponentByName("bugreport.mail.to")).setText(mailTo);
		mailSmtp = (JTextField) form.getComponentByName("bugreport.mail.smtp");
		mailSubject = (JTextField) form.getComponentByName("bugreport.mail.subject");
		mailBody = (JTextArea) form.getComponentByName("bugreport.mail.body");
		form.addCutCopyPastePopupMenu(mailBody);
		((AbstractButton) form.getComponentByName("bugreport.clear")).setAction(new ClearAction());
		((AbstractButton) form.getComponentByName("bugreport.send")).setAction(new SendAction());
		JList list = (JList) form.getComponentByName("bugreport.list");
		list.setModel(BugReportAppender.listModel);

		Properties p = preferenceManager.getPreferenceAsProperties(getClass().getName());
		mailFrom.setText(p.getProperty("mail.from", ""));
		mailSmtp.setText(p.getProperty("mail.smtp", ""));
	}

	// ///////////////////////////////////////////////////// Startable Interface
	public void start() throws Exception {
	}

	public void stop() throws Exception {
		if (form.hasMainComponent()) {
			Properties p = preferenceManager.getPreferenceAsProperties(getClass().getName());
			p.setProperty("mail.from", mailFrom.getText());
			p.setProperty("mail.smtp", mailSmtp.getText());
			preferenceManager.setPreference(getClass().getName(), p);
		}
	}

	// ///////////////////////////////////////////////////// BugReport Interface
	public void openForm() {
		if (!form.hasMainComponent())
			try {
				initialize();
			} catch (Exception ex) {
				logger.error(ex.toString(), ex);
			}
					
		form.showDialog((FormClosedListener) null);
	}

	public void clearBugReport() {
		BugReportAppender.listModel.clear();
	}
	
	/**
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
	 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>License: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General
	 * Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	protected class ClearAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			BugReportAppender.listModel.clear();
			logger.info("log cleared");
		}
	}

	/**
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
	 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>License: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General
	 * Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	protected class SendAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			String from = mailFrom.getText().trim();
			String smtp = mailSmtp.getText().trim();
			String subject = mailSubject.getText().trim();
			String body = mailBody.getText().trim();
			String log = "";
			if (from.length() == 0) {
				utilMsg.msgError(form.getAsComponent(), "bugreport.msg.from");
				return;
			}
			if (smtp.length() == 0) {
				utilMsg.msgError(form.getAsComponent(), "bugreport.msg.smtp");
				return;
			}
			if (subject.length() == 0) {
				utilMsg.msgError(form.getAsComponent(), "bugreport.msg.subject");
				return;
			}
			if (body.length() == 0) {
				utilMsg.msgError(form.getAsComponent(), "bugreport.msg.body");
				return;
			}
			DefaultListModel l = BugReportAppender.listModel;
			StringBuffer sb = new StringBuffer(10000);
			for (int i = 0; i < l.size(); i++)
				sb.append(l.get(i));
			log = sb.toString();
			if (log.length() == 0) {
				utilMsg.msgError(form.getAsComponent(), "bugreport.msg.log");
				return;
			}

			if (utilMsg.msgYesNo(form.getAsComponent(), "bugreport.msg.send"))
				try {
					Properties p = new Properties();
					p.put("mail.smtp.host", smtp);
					Session session = Session.getInstance(p);

					MimeMessage message = new MimeMessage(session);
					message.setFrom(new InternetAddress(from));
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));
					message.setSubject(subject);

					Multipart multipart = new MimeMultipart();

					BodyPart messageBodyPart = new MimeBodyPart();
					messageBodyPart.setText(body);
					messageBodyPart.setDescription("Bug description");
					messageBodyPart.setDisposition(Part.INLINE);
					multipart.addBodyPart(messageBodyPart);

					messageBodyPart = new MimeBodyPart();
					messageBodyPart.setText(log);
					messageBodyPart.setDescription("Log");
					messageBodyPart.setDisposition(Part.ATTACHMENT);
					multipart.addBodyPart(messageBodyPart);

					message.setContent(multipart);

					Transport.send(message);

					utilMsg.msgInfo("bugreport.msg.sendok");
				} catch (Exception ex) {
					utilMsg.msgError(ex.toString());
				}
		}
	}
}
