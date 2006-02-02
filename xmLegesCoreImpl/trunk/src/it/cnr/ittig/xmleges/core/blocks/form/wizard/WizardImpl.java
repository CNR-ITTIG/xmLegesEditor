package it.cnr.ittig.xmleges.core.blocks.form.wizard;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormException;
import it.cnr.ittig.xmleges.core.services.form.wizard.Wizard;
import it.cnr.ittig.xmleges.core.services.form.wizard.WizardStep;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;

/**
 * <h1>Implementazione del servizio <code>it.cnr.ittig.xmleges.editor.services.form.wizard.Wizard</code>.</h1>
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
 * @see
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class WizardImpl implements Wizard, Loggable, Serviceable, Initializable {
	Logger logger;

	Form form;

	UtilUI utilUI;

	BackAction backAction = new BackAction();

	ForwardAction forwardAction = new ForwardAction();

	FinishAction finishAction = new FinishAction();

	boolean finished = false;

	Vector steps = new Vector();

	int curStep = 0;

	int lastStep = 0;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		utilUI = (UtilUI) serviceManager.lookup(UtilUI.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(getClass().getResourceAsStream("Wizard.jfrm"));
		AbstractButton btn;
		btn = (AbstractButton) form.getComponentByName("form.wizard.back");
		btn.setAction(utilUI.applyI18n("form.wizard.back", backAction));
		btn = (AbstractButton) form.getComponentByName("form.wizard.forward");
		btn.setAction(utilUI.applyI18n("form.wizard.forward", forwardAction));
		btn = (AbstractButton) form.getComponentByName("form.wizard.finish");
		btn.setAction(utilUI.applyI18n("form.wizard.finish", finishAction));
		form.setCustomButtons(new String[] { "form.button.cancel" });
	}

	// //////////////////////////////////////////////////////// Wizard Interface
	public void add(WizardStep page) {
		steps.add(page);
	}

	public void setForwardEnabled(boolean enabled) {
		forwardAction.setEnabled(enabled);
	}

	public void setFinishEnabled(boolean enabled) {
		finishAction.setEnabled(enabled);
	}

	public boolean show() {
		form.showDialog();
		return finished;
	}

	// //////////////////////////////////////////////////////// Button's Actions
	public class BackAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			curStep--;
			updatePage();
		}
	}

	public class ForwardAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			curStep++;
			if (curStep > lastStep)
				lastStep++;
			updatePage();
		}
	}

	public class FinishAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			finished = true;
			form.close();
		}
	}

	protected void updatePage() {
		WizardStep page = (WizardStep) steps.get(curStep);
		Component comp = page.getComponent();
		comp.setName("form.wizard.step");
		try {
			form.replaceComponent("form.wizard.step", comp);
		} catch (FormException ex) {
			logger.error(ex.toString(), ex);
		}
		enableActions();
	}

	protected void enableActions() {
		if (curStep > 0)
			backAction.setEnabled(true);
		if (curStep < lastStep)
			forwardAction.setEnabled(true);
		else if (lastStep == steps.size())
			forwardAction.setEnabled(false);
	}

}
