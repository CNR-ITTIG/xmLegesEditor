package it.cnr.ittig.xmleges.editor.blocks.form.rinvii.interni;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.interni.RinviiInterniForm;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.rinvii.interni.RinviiInterni</code>.
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class RinviiInterniFormImpl implements RinviiInterniForm, EventManagerListener, Loggable, ActionListener, Serviceable, Initializable {
	Logger logger;

	Form form;

	NirUtilDom nirUtilDom;

	NirUtilUrn nirUtilUrn;

	EventManager eventManager;

	UtilUI utilui;

	UtilMsg utilmsg;

	JTextField id;

	JTextField testo;

	JButton add;

	JButton del;

	JList mrif;

	DefaultListModel lmmrif;

	Node activeNode;

	Node callingNode;

	boolean valid = false;

	boolean autoUpdate = true;

	Vector mRifDescription = new Vector();

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		nirUtilUrn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
		utilui = (UtilUI) serviceManager.lookup(UtilUI.class);
		utilmsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		eventManager.addListener(this, SelectionChangedEvent.class);
		form.setMainComponent(this.getClass().getResourceAsStream("RinviiInterni.jfrm"));
		form.setName("editor.form.rinvii.interni");
		
		form.setHelpKey("help.contents.form.rinviiinterni");
		
		id = (JTextField) form.getComponentByName("editor.form.rinvii.interni.id");
		testo = (JTextField) form.getComponentByName("editor.form.rinvii.interni.testo");
		add = (JButton) form.getComponentByName("editor.form.rinvii.interni.add");
		del = (JButton) form.getComponentByName("editor.form.rinvii.interni.del");
		mrif = (JList) form.getComponentByName("editor.form.rinvii.interni.mrif");
		form.addCutCopyPastePopupMenu(testo);
		form.setSize(350, 300);
		add.addActionListener(this);
		del.addActionListener(this);
		utilui.applyI18n(add);
		utilui.applyI18n(del);
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		SelectionChangedEvent e = (SelectionChangedEvent) event;
		if (e.isActiveNodeChanged() && form.isDialogVisible()) {
			logger.debug("activeNodeChanged in form");
			updateContent(e.getActiveNode());
		}
	}

	public class citazione {
		String id;

		public citazione(String id) {
			this.id = id;
		}

		public String getId() {
			return this.id;
		}

		public String toString() {
			return (nirUtilUrn.getFormaTestualeById(id) + "  (" + id + ")");
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(add)) {
			try {
				citazione cit = new citazione(id.getText());
				if (checkInsertable(cit)) {
					lmmrif.addElement(new citazione(id.getText()));
					getDescrizioneMrifInt();
					String txt = getFormaTestualeMRifInt(getMRif(), getDescrizioneMRifInt());
					if (autoUpdate)
						this.testo.setText(txt);
				} else {
					utilmsg.msgError("editor.form.rinvii.interni.msg.error.text");
				}

			} catch (Exception exc) {
				logger.error(exc.getMessage(), exc);
			}
		}
		if (e.getSource().equals(del)) {
			try {
				lmmrif.removeElementAt(mrif.getSelectedIndex());
				getDescrizioneMrifInt();
				String txt = getFormaTestualeMRifInt(getMRif(), getDescrizioneMRifInt());
				if (autoUpdate)
					this.testo.setText(txt);
			} catch (Exception exc) {
				//logger.error(exc.getMessage(), exc);
			}
		}
	}

	private boolean checkInsertable(citazione c) {
		if (lmmrif.size() == 0)
			return true;
		if (isDuplicatedId(c.getId()))
			return false;
//		citazione base = (citazione) lmmrif.get(0);
		return true;
	}

	private void updateContent(Node activeNode) {
		if (activeNode != null) {
			Node container = nirUtilDom.getNirContainer(activeNode);
			if (container == null)
				container = getAnnessoContainer(activeNode);
			logger.debug("container " + UtilDom.getPathName(container));
			if (container != null) {
				logger.debug("container not null");
				logger.debug(UtilDom.getAttributeValueAsString(container, "id"));
				valid = true;
				id.setText(UtilDom.getAttributeValueAsString(container, "id"));
				if (autoUpdate) {
					String callingId = null;
					String referredId = UtilDom.getAttributeValueAsString(container, "id");
					if (nirUtilDom.getContainer(callingNode) != null)
						callingId = UtilDom.getAttributeValueAsString(nirUtilDom.getContainer(callingNode), "id");
					testo.setText(nirUtilUrn.getFormaTestualeById(callingId, referredId));
				}
			}
		}
	}

	private Node getAnnessoContainer(Node node) {
		if (node != null) {
			Node container = node;
			while (container != null && !container.getNodeName().equalsIgnoreCase("annesso"))
				container = container.getParentNode();
			return container;
		}
		return (null);
	}

	private boolean isDuplicatedId(String id) {
		for (int i = 0; i < lmmrif.getSize(); i++) {
			if (((citazione) lmmrif.get(i)).getId().equalsIgnoreCase(id))
				return true;
		}
		return false;
	}

	private void getDescrizioneMrifInt() {
		Vector Partizioni = new Vector();

		// Prende tutti gli id inseriti: l'ultimo e tutti quelli inseriti nella
		// lista
		// if(!isDuplicatedId(this.id.getText()))
		// Partizioni.add(this.id.getText());
		for (int i = 0; i < lmmrif.getSize(); i++) {
			Partizioni.add(((citazione) lmmrif.get(i)).getId());
		}

		String[] sottopart1;
		String[] sottopart2;
		mRifDescription = new Vector();

		String callingId = null;
		if (nirUtilDom.getContainer(callingNode) != null)
			callingId = UtilDom.getAttributeValueAsString(nirUtilDom.getContainer(callingNode), "id");

		// Crea la descrizione dell'mrif
		for (int i = 0; i < Partizioni.size(); i++) {
			if (Partizioni.get(i) != null) {
				mRifDescription.add(nirUtilUrn.getFormaTestualeById(callingId, Partizioni.get(i).toString()));
				for (int j = i + 1; j < Partizioni.size(); j++) {
					if (Partizioni.get(i).toString().length() == Partizioni.get(j).toString().length()) {// Forse
																											// le
																											// due
																											// citazioni
																											// fanno
																											// riferimento
																											// alla
																											// stessa
																											// sottopartizione
						sottopart1 = Partizioni.get(i).toString().split("-");
						sottopart2 = Partizioni.get(j).toString().split("-");
						for (int k = 0; k < sottopart1.length; k++)
							if (sottopart1[k].compareTo(sottopart2[k]) == 0 && k < sottopart1.length - 1) { // stessa radice "es prt-2"
							  if(sottopart1[k + 1].length()>=3 && sottopart2[k + 1].length()>=3 ){   // salta quelli che non sono prefissi di partizioni (es t1,t2,t3)
								if (sottopart1[k + 1].substring(0, 3).compareTo(sottopart2[k + 1].substring(0, 3)) == 0) {
									mRifDescription.add(",");
									mRifDescription.add(sottopart2[k + 1].substring(3, sottopart2[k + 1].length()));
									Partizioni.set(j, null);
									break;
								}
							 }
							}
					}
				}
			}
		}

	}

	private String getFormaTestualeMRifInt(String[] id, String[] descrizioneMRif) {

		int Descrizioneindex = 0;

		String testoMRif = "";

		for (int i = 0; i < id.length; i++) {
			testoMRif += descrizioneMRif[Descrizioneindex];
			Descrizioneindex++;
			if ((Descrizioneindex < descrizioneMRif.length) && (descrizioneMRif[Descrizioneindex].compareTo(",") == 0)) {
				testoMRif += descrizioneMRif[Descrizioneindex];
				Descrizioneindex++;
			} else if (Descrizioneindex < descrizioneMRif.length) {
				testoMRif += " e ";
			}
		}
		return testoMRif;
	}

	public String[] getDescrizioneMRifInt() {
		getDescrizioneMrifInt();
		if (mRifDescription.size() > 0) {
			String[] descrizioneMRif = new String[mRifDescription.size()];
			mRifDescription.copyInto(descrizioneMRif);
			return descrizioneMRif;
		}
		return null;
	}

	// ///////////////////////////////////////////////// RinviiInterni Interface
	public void openForm(FormClosedListener listener, boolean allowMRif) {

		mrif.setEnabled(allowMRif);
		add.setEnabled(allowMRif);
		del.setEnabled(allowMRif);

		lmmrif = new DefaultListModel();
		mrif.setModel(lmmrif);
		mrif.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		updateContent(this.callingNode);
		form.showDialog(listener);
	}

	public boolean isOk() {
		return form.isOk();
	}

	public String getId() {
		return id.getText();
	}

	public String[] getMRif() {
		Vector ret = new Vector();

		// escludo il primo
		// if(!isDuplicatedId(id.getText()))
		// ret.add(id.getText());
		for (int i = 0; i < lmmrif.getSize(); i++) {
			ret.add(((citazione) lmmrif.get(i)).getId());
		}

		if (ret.size() > 0) {
			String[] ids = new String[ret.size()];
			ret.copyInto(ids);
			return ids;
		}
		return null;
	}

	public String getTesto() {
		return testo.getText();
	}

	public void setTesto(String testo) {
		this.testo.setText(testo);
	}

	public void setMrif(String[] ids) {
		lmmrif.clear();
		for (int i = 0; i < ids.length; i++)
			lmmrif.addElement(new citazione(ids[i]));
	}

	public void setCallingNode(Node node) {
		this.callingNode = node;
	}

	public void setAutoUpdate(boolean auto) {
		this.autoUpdate = auto;
	}

}
