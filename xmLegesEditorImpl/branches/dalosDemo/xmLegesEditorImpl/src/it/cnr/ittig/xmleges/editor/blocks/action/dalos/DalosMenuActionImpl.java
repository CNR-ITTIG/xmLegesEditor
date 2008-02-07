package it.cnr.ittig.xmleges.editor.blocks.action.dalos;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.bars.Bars;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.editor.services.action.dalos.DalosMenuAction;
import it.cnr.ittig.xmleges.editor.services.dalos.kb.KbManager;
import it.cnr.ittig.xmleges.editor.services.dalos.util.LangChangedEvent;
import it.cnr.ittig.xmleges.editor.services.dalos.util.UtilDalos;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Properties;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.allineamento.AllineamentoAction</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>action-manager</li>
 * <li>event-manager</li>
 * <li>selection-manager</li>
 * <li>editor-dom-allineamento</li>
 * <li>editor-dom-tabelle</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>editor.allineamento.oriz.sx</li>
 * <li>editor.allineamento.oriz.dx</li>
 * <li>editor.allineamento.oriz.centro</li>
 * <li>editor.allineamento.oriz.giustificato</li>
 * </ul>
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public class DalosMenuActionImpl implements DalosMenuAction, Loggable, Serviceable, Initializable, EventManagerListener {
	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;
	
	Node activeNode = null;

	ShowViewAction showViewAction;
	
	Frame frame;
	
	Bars bars;
	
	PreferenceManager preferenceManager;
	
	KbManager kbManager;
	
	UtilDalos utilDalos;
	
	UtilMsg utilMsg;
	
	boolean isDalosShown = false;
	

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		frame = (Frame) serviceManager.lookup(Frame.class);
		kbManager = (KbManager) serviceManager.lookup(KbManager.class);
		bars = (Bars) serviceManager.lookup(Bars.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		utilDalos = (UtilDalos) serviceManager.lookup(UtilDalos.class);
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		showViewAction = new ShowViewAction();
		actionManager.registerAction("editor.dalos.showview", showViewAction);
		
		for(int i=0;i<utilDalos.getDalosLang().length;i++){
			actionManager.registerAction("editor.dalos.switchlang."+utilDalos.getDalosLang()[i].toLowerCase(), new SwitchLangAction(utilDalos.getDalosLang()[i].toUpperCase()));
		}
		
	}

	public void manageEvent(EventObject event) {
	}

	

	public class ShowViewAction extends AbstractAction {
		
		public ShowViewAction() {		
		}

		public boolean canDoAction(Node n) {
			return true;
		}

		public void actionPerformed(ActionEvent e) {
			
			// panels properties
			Properties panelsProps = preferenceManager.getPreferenceAsProperties(frame.getClass().getName());   // le preference di Frame
			Properties oldPanelsProps = (Properties)panelsProps.clone();   // le oldProps sono quelle dell'avvio lette dal file delle pref
			boolean defaultValue = false;
			
			Enumeration en=panelsProps.keys();
							
			while(en.hasMoreElements()){
				String el = en.nextElement().toString();
				if(el.indexOf("show")!=-1){
					if(el.indexOf("dalos")!=-1 || el.indexOf("document")!=-1){   // mostrare tutti i pannelli dalos e quello del documento
						panelsProps.setProperty(el,Boolean.toString(true));
					}
					else
						panelsProps.setProperty(el,Boolean.toString(false));		
				}
			}
			
			
			if(panelsProps.size()==0){      // al primo avvio non esiste il file delle preference
				defaultValue = true;
				panelsProps.setProperty("show.editor.panes.documento", "true");
				panelsProps.setProperty("show.editor.panes.dalos.synsettree", "true");
				panelsProps.setProperty("show.editor.panes.dalos.synsetlist", "true");
				panelsProps.setProperty("show.editor.panes.dalos.synsetdetails", "true");
				panelsProps.setProperty("show.editor.panes.dalos.linguisticrelation", "true");
				panelsProps.setProperty("show.editor.panes.dalos.semanticrelation", "true");
				panelsProps.setProperty("show.editor.panes.dalos.source", "true");
			}
			//////////////////////////////////////
			//
			// BARS properties    ???
			//
			
			Properties barsProps = preferenceManager.getPreferenceAsProperties(bars.getClass().getName());   // le preference di Frame
			Properties oldbarsProps = (Properties)barsProps.clone();
			
			en=barsProps.keys();
							
			while(en.hasMoreElements()){
				String el = en.nextElement().toString();
				if(el.indexOf("check.view")!=-1){
					if(el.indexOf("dalos")!=-1 || el.indexOf("document")!=-1){   // mostrare tutti i pannelli dalos e quello del documento
						barsProps.setProperty(el,Boolean.toString(true));
					}
					else
						barsProps.setProperty(el,Boolean.toString(false));		
				}
			}
			
			
			if(barsProps.size()==0){      // al primo avvio non esiste il file delle preference; va gestito a parte
				defaultValue = true;
				barsProps.setProperty("check.view.pane.editor.panes.documento", "true");
				barsProps.setProperty("check.view.pane.editor.panes.dalos.synsettree", "true");
				barsProps.setProperty("check.view.pane.editor.panes.dalos.synsetlist", "true");
				barsProps.setProperty("check.view.pane.editor.panes.dalos.synsetdetails", "true");
				barsProps.setProperty("check.view.pane.editor.panes.dalos.linguisticrelation", "true");
				barsProps.setProperty("check.view.pane.editor.panes.dalos.semanticrelation", "true");
				barsProps.setProperty("check.view.pane.editor.panes.panes.dalos.source", "true");
			}
			//
			//
			/////////////////////////////////////////////////////////
			
			
			
			// RELOAD
			if(!isDalosShown){
			   frame.reloadPerspective(panelsProps,false);
			   isDalosShown = true;
			}else{
				frame.reloadPerspective(oldPanelsProps,defaultValue);
				isDalosShown = false;
			}
				
			System.err.println("--------SHOW DALOS VIEW----------");
		}
	}
	
	
	
	public class SwitchLangAction extends AbstractAction {
		String lang;

		public SwitchLangAction(String lang) {
			this.lang = lang;
		}

		public void actionPerformed(ActionEvent e) {
			
			if(utilMsg.msgWarning("editor.dalos.switchlang.msg")){
				if(kbManager.isLangSupported(lang))
					eventManager.fireEvent(new LangChangedEvent(this, true, lang));
				else 
				   utilMsg.msgError("editor.dalos.switchlang.unsupportedlang");
			}
		}
	}

}
