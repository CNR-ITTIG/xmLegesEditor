package it.cnr.ittig.xmleges.core.blocks.exec.monitor;

import it.cnr.ittig.xmleges.core.services.exec.ExecEvent;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;

/**
 * Classe per la visualizzazione degli eventi di Exec su una
 * <code>javax.swing.JList</code>.
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class ExecEventRenderer extends DefaultListCellRenderer {

	Icon debugIcon;

	Icon infoIcon;

	Icon warnIcon;

	Icon errorIcon;

	Icon genericIcon;

	public ExecEventRenderer(I18n i18n) {
		super();
		debugIcon = i18n.getIconFor("exec.monitor.debug.icon");
		infoIcon = i18n.getIconFor("exec.monitor.info.icon");
		warnIcon = i18n.getIconFor("exec.monitor.warn.icon");
		errorIcon = i18n.getIconFor("exec.monitor.error.icon");
		genericIcon = i18n.getIconFor("exec.monitor.generic.icon");
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		ExecEvent e = (ExecEvent) value;

		// /////////////////////////////////////////////////////////////// ICON
		if (e.isDebug())
			setIcon(debugIcon);
		else if (e.isInfo())
			setIcon(infoIcon);
		else if (e.isWarn())
			setIcon(warnIcon);
		else if (e.isError())
			setIcon(errorIcon);
		else if (e.isGeneric())
			setIcon(genericIcon);

		// /////////////////////////////////////////////////////////////// TEXT
		if (e.isDebug())
			setText(e.getDebugMsg());
		else if (e.isInfo())
			setText(e.getInfoMsg());
		else if (e.isWarn())
			setText(e.getWarnMsg());
		else if (e.isError())
			setText(e.getErrorMsg());
		else if (e.isGeneric())
			setText(e.getGenericMsg());

		return this;
	}
}
