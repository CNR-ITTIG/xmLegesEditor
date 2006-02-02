package it.cnr.ittig.xmleges.core.blocks.bars;

import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.bars.StatusBar;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Implementazione della barra di stato. <br>
 * Il file di configurazione deve avere i seguenti nodi:
 * 
 * <pre>
 *   &lt;slot name=&quot;name&quot; relsize=&quot;0.x&quot; icon=&quot;iconame.png&quot; action=&quot;actioname&quot; delay=&quot;1500&quot;/&gt;
 * </pre>
 * 
 * L'attributo <i>relsize </i> &egrave; obbligatorio, <i>icon </i>, <i>delay </i> e
 * <i>action </i> sono opzionali. La somma di tutti i <i>relsize </i> deve essere uguale a
 * 1.<br>
 * Esempio:
 * 
 * <pre>
 *   &lt;status&gt;
 *     &lt;slot name=&quot;info&quot; relsize=&quot;0.7&quot;/&gt;
 *     &lt;slot name=&quot;pos&quot; relsize=&quot;0.2&quot; icon=&quot;zoom.png&quot; action=&quot;zoom&quot;/&gt;
 *     &lt;slot name=&quot;zoom&quot; relsize=&quot;0.1&quot; icon=&quot;delay.png&quot; delay=&quot;1500&quot;/&gt;
 *   &lt;/status&gt;
 * </pre>
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
public class StatusBarImpl implements StatusBar, MouseListener {
	Logger logger;

	ActionManager actionManager;

	I18n i18n;

	Timer timer = new Timer();

	GridBagLayout layout = new GridBagLayout();

	JPanel panel = new JPanel(layout);

	JLabel[] labels;

	Color[] defaultBack;

	Color[] defaultFore;

	Hashtable nameToPos;

	long[] delays;

	LabelTimerTask[] tasks;

	public StatusBarImpl(Logger logger, I18n i18n) {
		this.logger = logger;
		this.i18n = i18n;
	}

	public boolean create(Configuration conf, ActionManager actionManager) {
		this.actionManager = actionManager;
		boolean hasAllAction = true;
		Configuration[] c = conf.getChildren();
		logger.info("Status bars: " + c.length);
		labels = new JLabel[c.length];
		defaultBack = new Color[c.length];
		defaultFore = new Color[c.length];
		nameToPos = new Hashtable();
		delays = new long[c.length];
		tasks = new StatusBarImpl.LabelTimerTask[c.length];
		for (int i = 0; i < c.length; i++) {
			labels[i] = new JLabel(" ");
			labels[i].setBorder(BorderFactory.createEtchedBorder());
			defaultBack[i] = labels[i].getBackground();
			defaultFore[i] = labels[i].getForeground();
			String action = null;
			String name = null;
			float size = c[i].getAttributeAsFloat("relsize", 1 / c.length);
			name = c[i].getAttribute("name", "slot-" + i);
			labels[i].setIcon(i18n.getIconFor(c[i].getAttribute("icon", "")));
			try {
				action = c[i].getAttribute("action");
				if (!actionManager.hasAction(action))
					hasAllAction = false;
			} catch (ConfigurationException ex) {
			}
			delays[i] = c[i].getAttributeAsLong("delay", -1);
			GridBagConstraints cons = new GridBagConstraints();
			cons.gridx = i;
			cons.gridy = 0;
			cons.fill = GridBagConstraints.HORIZONTAL;
			cons.weightx = size;
			labels[i].setName(action);
			labels[i].addMouseListener(this);
			panel.add(labels[i], cons);
			nameToPos.put(name, new Integer(i));
			logger.debug("Status create:" + name);
			panel.invalidate();
		}
		return hasAllAction;
	}

	// ///////////////////////////////////////////////////// Statusbar Interface
	public Component getComponent() {
		return panel;
	}

	public void setText(String text) {
		setText(text, 0);
	}

	public void setText(String text, int n) {
		try {
			if (i18n.hasNotEmptyKey(text + ".text"))
				labels[n].setText(i18n.getTextFor(text + ".text"));
			else
				labels[n].setText(text);
			labels[n].setIcon(i18n.getIconFor(text + ".icon"));
			if (i18n.hasNotEmptyKey(text + ".tooltip"))
				labels[n].setToolTipText(i18n.getTextFor(text + ".icon"));
			else
				labels[n].setToolTipText(null);
			if (tasks[n] != null)
				tasks[n].cancel();
			tasks[n] = new LabelTimerTask(n);
			timer.schedule(tasks[n], delays[n]);
		} catch (Exception ex) {
			logger.warn("Status label not defined: " + n);
		}
	}

	public void setText(String text, String name) {
		Integer n = (Integer) nameToPos.get(name);
		if (n != null)
			setText(text, n.intValue());
	}

	public void setBackground(Color color, int n) {
		labels[n].setBackground(color);
	}

	public void setBackground(Color color, String name) {
		Integer n = (Integer) nameToPos.get(name);
		if (n != null)
			setBackground(color, n.intValue());

	}

	public void setDefaultBackground(int n) {
		setBackground(defaultBack[n], n);
	}

	public void setDefaultBackground(String name) {
		Integer n = (Integer) nameToPos.get(name);
		if (n != null)
			setDefaultBackground(n.intValue());
	}

	public void setForeground(Color color, int n) {
		labels[n].setForeground(color);
	}

	public void setForeground(Color color, String name) {
		Integer n = (Integer) nameToPos.get(name);
		if (n != null)
			setForeground(color, n.intValue());
	}

	public void setDefaultForeground(int n) {
		setForeground(defaultFore[n], n);
	}

	public void setDefaultForeground(String name) {
		Integer n = (Integer) nameToPos.get(name);
		if (n != null)
			setDefaultForeground(n.intValue());
	}

	// ///////////////////////////////////////////////// MouseListener Interface
	public void mouseClicked(MouseEvent e) {
		String actionName = ((JLabel) e.getSource()).getName();
		try {
			if (actionManager.getAction(actionName).isEnabled())
				actionManager.fireAction(actionName, e);
		} catch (Exception ex) {
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * Timer per cancellare il messaggio sullo slot n-esimo.
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
	 * 
	 */
	protected class LabelTimerTask extends TimerTask {
		int n;

		public LabelTimerTask(int n) {
			this.n = n;
		}

		public void run() {
			labels[n].setText(" ");
		}
	}

}
