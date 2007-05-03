package it.cnr.ittig.xmleges.editor.blocks.panes.riferimenti;


import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.bars.Bars;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.FindIterator;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.frame.PaneException;
import it.cnr.ittig.xmleges.editor.services.panes.riferimenti.RiferimentiPane;
import it.cnr.ittig.xmleges.editor.services.panes.xslts.NirXslts;
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.XsltPane;
import it.cnr.ittig.xmleges.core.services.panes.xsltutil.XsltUtil;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.EventObject;


import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JProgressBar;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**						
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.panes.riferimenti.RiferimentiPane</code>.</h1>
 * <h1>Descrizione</h1>
 * Servizio per la visualizzazione del pannello dei riferimenti.
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
 */

public class RiferimentiPaneImpl implements RiferimentiPane, EventManagerListener, Loggable, Serviceable, Initializable, Startable {

	HttpClient client;
	
	XsltUtil xsltutil;
	
	Logger logger;
		
	Frame frame;

	EventManager eventManager;

	UtilUI utilUI;

	Bars bars;

	JPanel panel = new JPanel(new BorderLayout());
	
	JScrollPane scrollPane = new JScrollPane();

	StartAction startAction = new StartAction();
	
	StopAction stopAction = new StopAction();

	JPopupMenu popupMenu;

	XsltPane xsltPane;
	
	NirXslts xslts;
	
	JProgressBar progress = new JProgressBar();
	
	JToolBar bar = new JToolBar();
	
	DocumentManager documentManager;
	
	Thread thread;
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}
	
	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		frame = (Frame) serviceManager.lookup(Frame.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		utilUI = (UtilUI) serviceManager.lookup(UtilUI.class);
		bars = (Bars) serviceManager.lookup(Bars.class);
		xsltPane = (XsltPane) serviceManager.lookup(XsltPane.class);
		xslts = (NirXslts) serviceManager.lookup(NirXslts.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		xsltutil = (XsltUtil) serviceManager.lookup(XsltUtil.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		popupMenu = bars.getPopup(false);
		
		bar.add(utilUI.applyI18n("panes.riferimenti.start", startAction));
		bar.add(utilUI.applyI18n("panes.riferimenti.stop", stopAction));
		bar.getComponent(0).setEnabled(true);
		bar.getComponent(1).setEnabled(false);
		bar.add(progress);
		panel.add(bar, BorderLayout.NORTH);
		
		//TODO prendere dalla configurazione
		xsltPane.setName("editor.panes.riferimenti");
		xsltPane.set(xslts.getXslt("riferimenti"), null, null);
		frame.addPane(xsltPane, false);
		
			
		scrollPane.setViewportView(xsltPane.getPaneAsComponent());
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(scrollPane);

		eventManager.addListener(this, DocumentClosedEvent.class);
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof DocumentClosedEvent) {
			bar.getComponent(0).setEnabled(true);
			bar.getComponent(1).setEnabled(false);
			if(thread.isAlive())
				thread.stop();
			progress.setValue(0);
		}
			
	}
	
	// ///////////////////////////////////////////////////// Startable Interface
	public void start() throws Exception {
		frame.addPane(this, false);
	}

	public void stop() throws Exception {
	}

	// ///////////////////////////////////////////////////// RiferimentiPane Interface
	public String getName() {
		return "editor.panes.riferimenti";
	}

	public Component getPaneAsComponent() {
		return panel;
	}
	
	// ///////////////////////////////////////////////////////// Toolbar Actions
	class StartAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {

			bar.getComponent(0).setEnabled(false);
			bar.getComponent(1).setEnabled(true);
			
			
			
			new Thread() {
				public void run() {
					logger.debug("Eseguo test urn");
					thread = Thread.currentThread();
					thread.setName("Test URN online");

					
					client = new HttpClient(new MultiThreadedHttpConnectionManager());
					client.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
					
					Document dom = documentManager.getDocumentAsDom();
			
					NodeList nl = dom.getElementsByTagName("rif");
					progress.setMaximum(nl.getLength());
					for (int i = 0; i < nl.getLength(); i++) {
						Node node = nl.item(i);
						String urn = UtilDom.getAttributeValueAsString(node, "xlink:href");
						testURN(urn);
						progress.setValue(i+1);
					}
					
					progress.setValue(0);
					bar.getComponent(0).setEnabled(true);
					bar.getComponent(1).setEnabled(false);
					xsltPane.reload();
				}
			}.start(); 
			
		}
	}
	
	protected class StopAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if(thread.isAlive())
				thread.stop();
			progress.setValue(0);
			bar.getComponent(0).setEnabled(true);
			bar.getComponent(1).setEnabled(false);
			xsltPane.reload();
		}
	}

	private void testURN(String urn) {
		GetMethod get = new GetMethod("http://www.nir.it/cgi-bin/N2Ln?"+urn);
		get.setFollowRedirects(true);

		try {
			int iGetResultCode = client.executeMethod(get);
			final String strGetResponseBody = get.getResponseBodyAsString();

			String colore = "";
			if (strGetResponseBody != null) {
				if (strGetResponseBody.indexOf("non contiene")==-1) {
					logger.debug("Urn TROVATA");
					colore = "green";
				}
				else {
					logger.debug("Urn Non TROVATA");
					colore = "red";
				}
				xsltutil.setUrn(urn, colore);
				
				
				//TODO NON va aggiornato solo se attivo.... va aggiornato se visibile
//				if (frame.getActivePane().getName().equals("editor.panes.riferimenti"))
//					frame.getActivePane().reload();
				
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			get.releaseConnection();
		}
	}
	
	public boolean canCut() {
		return false;
	}

	public void cut() throws PaneException {
	}

	public boolean canCopy() {
		return false;
	}

	public void copy() throws PaneException {
	}

	public boolean canPaste() {
		return false;
	}

	public void paste() throws PaneException {
	}

	public boolean canPasteAsText() {
		return false;
	}

	public void pasteAsText() throws PaneException {
	}

	public boolean canDelete() {
		return false;
	}

	public void delete() throws PaneException {
	}

	public boolean canPrint() {
		return false;
	}

	public Component getComponentToPrint() {
		return null;
	}

	public boolean canFind() {
		return false;
	}

	public FindIterator getFindIterator() {
		return null;
	}

	public void reload() {
	}
	
	
}
