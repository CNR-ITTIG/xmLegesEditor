package it.cnr.ittig.xmleges.core.blocks.help;

import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;

/**
 * Classe per la gestione della finestra di dialogo per la visualizzazione dell'Help.
 * 
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class HelpDialog implements HyperlinkListener { // , ComponentListener {
	I18n i18n;

	Form helpForm;

	JEditorPane text;

	JLabel title;

	JLabel status;

	BackAction backAction = new BackAction();

	ForwardAction forwardAction = new ForwardAction();

	ReloadAction reloadAction = new ReloadAction();

	CloseAction closeAction = new CloseAction();

	Vector hystory = new Vector();

	int hystoryCurr = 0;

	public HelpDialog(I18n i18n, Form helpForm) {
		this.i18n = i18n;
		this.helpForm = helpForm;

		text = (JEditorPane) helpForm.getComponentByName("help.panel.text");
		text.addHyperlinkListener(this);

		AbstractButton btn;
		btn = (AbstractButton) helpForm.getComponentByName("help.panel.back");
		btn.setAction(backAction);
		btn = (AbstractButton) helpForm.getComponentByName("help.panel.forward");
		btn.setAction(forwardAction);
		btn = (AbstractButton) helpForm.getComponentByName("help.panel.reload");
		btn.setAction(reloadAction);
		btn = (AbstractButton) helpForm.getComponentByName("help.panel.close");
		btn.setAction(closeAction);

		title = (JLabel) helpForm.getComponentByName("help.panel.title");
		status = (JLabel) helpForm.getComponentByName("help.panel.status");
		updateActions();
	}

	public URL setDocument(String fileName) throws IOException {
		try {
			URL url = new URL(fileName);
			return setDocument(url);
		} catch (MalformedURLException ex) {
			throw new IOException(ex.toString());
		}
	}

	public URL setDocument(URL url) throws IOException {
		try {
			text.setPage(url);
			HTMLDocument doc = (HTMLDocument) text.getDocument();
			doc.setAsynchronousLoadPriority(-20);
			doc.setPreservesUnknownTags(false);
			Object tit = doc.getProperty(Document.TitleProperty);
			for (int i = 0; i < 20 && tit == null; i++) {
				Thread.sleep(100);
				tit = doc.getProperty(Document.TitleProperty);
			}
			title.setText((tit != null) ? tit.toString() : url.toString());
		} catch (Exception ex) {
			throw new IOException(ex.toString());
		}
		if (hystory.size() == 0)
			hystory.addElement(url);
		updateActions();
		return url;
	}

	public void setMessage(String msg) {
		text.setText(msg);
	}

	public void hyperlinkUpdate(HyperlinkEvent e) {
		String file = e.getDescription();
		HyperlinkEvent.EventType type = e.getEventType();
		if (type == HyperlinkEvent.EventType.ACTIVATED) {
			status.setText(i18n.getTextFor("help.panel.reading") + file);
			try {
				hystoryCurr++;
				URL url = null;
				url = (e.getURL() != null) ? setDocument(e.getURL()) : setDocument(file);
				if (hystoryCurr >= hystory.size() || hystory.elementAt(hystoryCurr) != url) {
					while (hystory.size() - hystoryCurr > 0)
						hystory.removeElementAt(hystory.size() - 1);
					hystory.insertElementAt(url, hystoryCurr);
				}
				updateActions();
				status.setText(" ");
			} catch (IOException ex) {
				status.setText(i18n.getTextFor("help.panel.error"));
				hystoryCurr--;
			}
		}
		if (type == HyperlinkEvent.EventType.ENTERED)
			status.setText(file);
		if (type == HyperlinkEvent.EventType.EXITED)
			status.setText(" ");
	}

	protected void updateActions() {
		backAction.setEnabled(hystoryCurr > 0);
		forwardAction.setEnabled(hystoryCurr < hystory.size() - 1);
	}

	protected void updateDocument(Object o) {
		try {
			if (o instanceof URL)
				setDocument((URL) o);
			else
				setMessage((String) o);
		} catch (IOException ex) {
			status.setText(i18n.getTextFor("help.panel.error"));
		}
	}

	public class BackAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (hystoryCurr > 0)
				updateDocument(hystory.elementAt(--hystoryCurr));
		}
	}

	public class ForwardAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (hystoryCurr + 1 < hystory.size())
				updateDocument(hystory.elementAt(++hystoryCurr));
		}
	}

	public class ReloadAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			updateDocument(hystory.elementAt(hystoryCurr));
		}
	}

	public class CloseAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			helpForm.close();
		}
	}

}
