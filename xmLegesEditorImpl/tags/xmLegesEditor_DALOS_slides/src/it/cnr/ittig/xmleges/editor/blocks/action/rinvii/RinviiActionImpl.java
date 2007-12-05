package it.cnr.ittig.xmleges.editor.blocks.action.rinvii;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.action.rinvii.RinviiAction;
import it.cnr.ittig.xmleges.editor.services.dom.rinvii.Rinvii;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.interni.RinviiInterniForm;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.newrinvii.NewRinviiForm;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import java.awt.event.ActionEvent;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.rinvii.RinviiAction</code>.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class RinviiActionImpl implements RinviiAction, EventManagerListener, FormClosedListener, Loggable, Serviceable, Initializable {

	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	SelectionManager selectionManager;

	RifInternoAction rifInternoAction;

	RifEsternoAction rifEsternoAction;

	NirUtilUrn nirUtilUrn;

	NirUtilDom nirUtilDom;

	Node activeNode;

	Node primoNodoAttivo;

	Node nodeRif;

	boolean isOpenForm = false;

	boolean changeInt = false;

	int start;

	int end;

	int primostart;

	int primoend;

	NewRinviiForm newrinvii;

	RinviiInterniForm rinviiInterni;

	Rinvii domrinvii;
	
	UtilMsg utilMsg;

	FormClosedListener listener;

	// /////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		newrinvii = (NewRinviiForm) serviceManager.lookup(NewRinviiForm.class);
		rinviiInterni = (RinviiInterniForm) serviceManager.lookup(RinviiInterniForm.class);
		domrinvii = (Rinvii) serviceManager.lookup(Rinvii.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		nirUtilUrn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		rifInternoAction = new RifInternoAction();
		rifEsternoAction = new RifEsternoAction();
		actionManager.registerAction("editor.rinvii.interno", rifInternoAction);
		actionManager.registerAction("editor.rinvii.esterno", rifEsternoAction);
		eventManager.addListener(this, SelectionChangedEvent.class);
		enableActions();
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof SelectionChangedEvent) {
			activeNode = ((SelectionChangedEvent) event).getActiveNode();
			start = selectionManager.getTextSelectionStart();
			end = selectionManager.getTextSelectionEnd();
			enableActions();
		}
	}

	protected void setModified(Node modified) {
		if (modified != null) {
			selectionManager.setActiveNode(this, modified);
			activeNode = modified;
			rifInternoAction.setEnabled((domrinvii.canInsert(activeNode) || (domrinvii.canChange(activeNode) && isRinvioInt())) && !isOpenForm);
			rifEsternoAction.setEnabled((domrinvii.canInsert(activeNode) || (domrinvii.canChange(activeNode) && isRinvioExt())) && !isOpenForm);
			logger.debug(" set modified " + UtilDom.getPathName(modified));
		} else
			logger.debug(" modified null in set modified ");
	}

	public void formClosed() {
		if (rinviiInterni.isOk()) {

			String[] mrif = rinviiInterni.getMRif();
			if (mrif != null)
				for (int i = 0; i < mrif.length; i++)
					mrif[i] = "#" + mrif[i];

			if (!changeInt) {
				isOpenForm = false;
				if (rinviiInterni.getMRif() != null) { // caso di mrif
					if (rinviiInterni.getMRif().length == 1)
						setModified(domrinvii.insert(primoNodoAttivo, primostart, primoend, mrif[0], rinviiInterni.getTesto()));
					else
						setModified(domrinvii.insert(primoNodoAttivo, primostart, primoend, mrif, rinviiInterni.getDescrizioneMRifInt()));
				} else { // caso di rif singolo
					setModified(domrinvii.insert(primoNodoAttivo, primostart, primoend, "#" + rinviiInterni.getId(), rinviiInterni.getTesto()));
				}
			} else {
				isOpenForm = false;
				changeInt = false;
				if (rinviiInterni.getMRif() != null) {
					if (rinviiInterni.getMRif().length == 1)
						setModified(domrinvii.change(nodeRif, mrif[0], rinviiInterni.getTesto(),msgUpdateText()));
					else
						setModified(domrinvii.change(nodeRif, mrif, rinviiInterni.getDescrizioneMRifInt()));
				} else {
					setModified(domrinvii.change(nodeRif, "#" + rinviiInterni.getId(), rinviiInterni.getTesto(),msgUpdateText()));
				}
			}
		} else {
			isOpenForm = false;
			enableActions();
		}
		rinviiInterni.setAutoUpdate(true);
	}

	protected void enableActions() {
		if (activeNode == null) {
			rifInternoAction.setEnabled(false);
			rifEsternoAction.setEnabled(false);
		} else {
			rifInternoAction.setEnabled((domrinvii.canInsert(activeNode) || (domrinvii.canChange(activeNode) && isRinvioInt())) && !isOpenForm);

			rifEsternoAction.setEnabled((domrinvii.canInsert(activeNode) || (domrinvii.canChange(activeNode) && isRinvioExt())) && !isOpenForm);
		}
	}

	public void doNewRinvioEsterno() {
		rifInternoAction.setEnabled(false);
		rifEsternoAction.setEnabled(false);
		if (newrinvii.openForm("", true, true, true, true)) {
			Node node = selectionManager.getActiveNode();
			int start = selectionManager.getTextSelectionStart();
			int end = selectionManager.getTextSelectionEnd();
			if (newrinvii.getUrn().size() > 1)
				setModified(domrinvii.insert(node, start, end, newrinvii.getUrn(), newrinvii.getDescrizioneMRif()));
			else
				setModified(domrinvii.insert(node, start, end, (Urn) newrinvii.getUrn().get(0)));
		} else
			enableActions();
	}

	public void changeRinvioEsterno() {

		boolean isOk = false;

		Node node = selectionManager.getActiveNode();
		nodeRif = isSingleRif(node);

		if (nodeRif != null)
			isOk = newrinvii.openForm(UtilDom.getAttributeValueAsString(nodeRif, "xlink:href"), true, true, true, true);
		else {
			nodeRif = isMultipleRif(node);
			if (nodeRif != null) {
				String[] urns = getExternalMrif(nodeRif);
				isOk = newrinvii.openForm(urns, true, true, true, true);
			}
		}

		if (isOk) {
			if (newrinvii.getUrn().size() > 1)
				setModified(domrinvii.change(nodeRif, newrinvii.getUrn(), newrinvii.getDescrizioneMRif()));
			else
				setModified(domrinvii.change(nodeRif, (Urn) newrinvii.getUrn().get(0),msgUpdateText()));
		}

	}

	public void changeRinvioInterno() {

		Node node = selectionManager.getActiveNode();
		nodeRif = isSingleRif(node);

		if (nodeRif != null) {
			String id = UtilDom.getAttributeValueAsString(nodeRif, "xlink:href");
			id = id.replaceAll("#", "");
			Node nodeID = node.getOwnerDocument().getElementById(id);
			rinviiInterni.setCallingNode(nodeRif);
			rinviiInterni.openForm(this, true);
			selectionManager.setActiveNode(this, nodeID);
			changeInt = true;
		} else {
			nodeRif = isMultipleRif(node);
			if (nodeRif != null) {
				String[] ids = getInternalMrif(nodeRif);
				Node nodeID = node.getOwnerDocument().getElementById(ids[0]);
				rinviiInterni.setCallingNode(nodeRif);
				rinviiInterni.openForm(this, true);
				selectionManager.setActiveNode(this, nodeID);
				rinviiInterni.setMrif(ids);
				changeInt = true;
			}
		}
	}
	

	public void doNewRinvioInterno() {

		rifInternoAction.setEnabled(false);
		rifEsternoAction.setEnabled(false);

		primoNodoAttivo = activeNode;
		primostart = start;
		primoend = end;
		String textSelected = null;

		if (primostart == primoend) {
			rinviiInterni.setCallingNode(primoNodoAttivo);
			rinviiInterni.openForm(this, true);
		} else {
			textSelected = activeNode.getNodeValue().trim().substring(start, end);
			rinviiInterni.setCallingNode(primoNodoAttivo);
			rinviiInterni.openForm(this, true);
			rinviiInterni.setAutoUpdate(false);
			rinviiInterni.setTesto(textSelected);
		}
		isOpenForm = true;
	}

	private boolean isRinvioInt() {
		Node primorif = null;
		Node mrif = null;
		if ((mrif = isMultipleRif(activeNode)) != null)
			primorif = UtilDom.findDirectChild(mrif, "rif");
		else
			primorif = isSingleRif(activeNode);

		if (primorif != null) {
			String attValue = UtilDom.getAttributeValueAsString(primorif, "xlink:href");
			if (attValue!=null && attValue.startsWith("#"))
				return true;
		}
		return false;
	}

	private boolean isRinvioExt() {
		Node primorif = null;
		Node mrif = null;
		if ((mrif = isMultipleRif(activeNode)) != null)
			primorif = UtilDom.findDirectChild(mrif, "rif");
		else
			primorif = isSingleRif(activeNode);

		if (primorif != null) {
			String attValue = UtilDom.getAttributeValueAsString(primorif, "xlink:href");
			if (attValue!=null && attValue.startsWith("urn"))
				return true;
		}
		return false;
	}

	private Node isSingleRif(Node activeNode) {
		Node mrif = null;
		if (activeNode != null && activeNode.getParentNode() != null) {
			Node primorif = UtilDom.findParentByName(activeNode, "rif");
			if (primorif != null)
				mrif = UtilDom.findParentByName(primorif, "mrif");
			if (mrif != null)
				return null;
			return primorif;
		}
		return null;
	}

	private Node isMultipleRif(Node node) {
		if (node != null && node.getParentNode() != null)
			return UtilDom.findParentByName(node, "mrif");
		return null;
	}

	private String[] getInternalMrif(Node mrif) {
		Vector ids = new Vector();
		String id;
		NodeList nl = mrif.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeName().equalsIgnoreCase("rif")) {
				id = UtilDom.getAttributeValueAsString(nl.item(i), "xlink:href");
				id = id.replaceAll("#", "");
				ids.add(id);
			}
		}
		if (ids.size() > 0) {
			String[] ret = new String[ids.size()];
			ids.copyInto(ret);
			return ret;
		}
		return null;
	}

	private String[] getExternalMrif(Node mrif) {
		Vector urns = new Vector();
		String urn;
		NodeList nl = mrif.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeName().equalsIgnoreCase("rif")) {
				urn = UtilDom.getAttributeValueAsString(nl.item(i), "xlink:href");
				urns.add(urn);
			}
		}
		if (urns.size() > 0) {
			String[] ret = new String[urns.size()];
			urns.copyInto(ret);
			return ret;
		}
		return null;
	}
	
	
	private boolean msgUpdateText(){
		return utilMsg.msgYesNo("editor.rinvii.msg.updatetext");
	}

	public class RifEsternoAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (domrinvii.canInsert(activeNode)) {
				doNewRinvioEsterno();
			} else {
				if (domrinvii.canChange(activeNode)) {
					changeRinvioEsterno();
				}
			}
		}
	}

	public class RifInternoAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (domrinvii.canInsert(activeNode)) {
				doNewRinvioInterno();
			} else {
				if (domrinvii.canChange(activeNode)) {
					changeRinvioInterno();
				}
			}
		}
	}

}
