package it.cnr.ittig.xmleges.editor.blocks.form.browser;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.editor.services.form.browser.BrowserForm;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.JPanel;

import org.jdesktop.jdic.browser.WebBrowser;
import org.jdesktop.jdic.browser.WebBrowserEvent;
import org.jdesktop.jdic.browser.WebBrowserListener;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.browser.BrowserForm</code>.</h1>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 */
public class BrowserFormImpl implements BrowserForm, Loggable, Serviceable, Initializable, EventManagerListener {

	Logger logger;

	Form form;

	JPanel panel;
	
	final WebBrowser webBrowser = new WebBrowser();
	
	EventManager eventManager;
	
	Vector downloadCompletedUrl = new Vector();
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
				
		form.setMainComponent(this.getClass().getResourceAsStream("Browser.jfrm"));
				
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new Dimension(600, 400));
		panel.add(webBrowser, BorderLayout.CENTER);
		

        webBrowser.addWebBrowserListener(
                new WebBrowserListener() {
                            
                public void downloadStarted(WebBrowserEvent event) {;}
                public void downloadCompleted(WebBrowserEvent event) {
                	
                	if (!downloadCompletedUrl.isEmpty()) {
                		URL currentUrl = webBrowser.getURL();
                    	if (currentUrl != null) {
                    		if (downloadCompletedUrl.contains(currentUrl.toString())) {
                        		//aspetto che venga effettuato il redirect della pagina
                        		while (webBrowser.getContent().indexOf("almeno un termine")==-1) {}                        	
                    			eventManager.fireEvent(new BrowserEvent(webBrowser.getContent()));
                        	}	
                    	}	
                	}
                }
                public void downloadProgress(WebBrowserEvent event) {;}
                public void downloadError(WebBrowserEvent event) {;}
                public void documentCompleted(WebBrowserEvent event) {;}
                public void titleChange(WebBrowserEvent event) {;}  
                public void statusTextChange(WebBrowserEvent event) {;}        
            });
		form.replaceComponent("editor.form.browser.jdic",panel);		
	}

	// /////////////////////////////////////////////////////// BrowserForm Interface
	public Component getAsComponent() {
		return form.getAsComponent();
	}

	public void setUrl(URL url) {
		webBrowser.setURL(url);
	}

	public void setUrl(String url) {
		try {
			setUrl(new URL(url));
		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
			//TODO Mettere in futuro il return per gestire l'errore.
			//return true;
		}
		//return false;
	}

	public URL getUrl() {
		return null;
	}

	public String getHtml() {
		return null;
	}

	public void setUrlListener(String url) {
		downloadCompletedUrl.add(new String(url));
	}

	public void setSize(int width, int height) {
		//TODO implementare il metodo
	}
		
	public void manageEvent(EventObject event) {}
	 
}
