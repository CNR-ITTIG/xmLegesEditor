package it.cnr.ittig.xmleges.core.blocks.bars;

import java.awt.Component;

import javax.swing.AbstractButton;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Gestione dei menu di popup.
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
public class PopupMenu extends JPopupMenu implements ChangeListener {

	boolean activeOnly;

	public PopupMenu(boolean activeOnly) {
		super();
		this.activeOnly = activeOnly;
	}

	public Component add(Component comp) {
		if (comp instanceof AbstractButton) {
			AbstractButton btn = (AbstractButton) comp;
			btn.addChangeListener(this);
			update(btn);
		}
		return super.add(comp);
	}

	// ////////////////////////////////// ChangeListener Interface
	public void stateChanged(ChangeEvent e) {
		Object src = e.getSource();
		if (src instanceof AbstractButton)
			update((AbstractButton) src);
	}

	protected void update(AbstractButton btn) {
		if (activeOnly)
			btn.setVisible(btn.isEnabled());
	}
}
