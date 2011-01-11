package it.cnr.ittig.xmleges.core.blocks.panes.xslteditor;

import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ParamPanel extends JPanel {

	ParamModel paramModel = new ParamModel();

	JTable table = new JTable(paramModel);

	JPanel btnPanel = new JPanel();

	public ParamPanel(UtilUI utilUi) {
		super(new BorderLayout());
		add(new JScrollPane(table), BorderLayout.CENTER);
		btnPanel.add(new JButton(utilUi.applyI18n("panes.xslteditor.param.add", new AddAction())));
		btnPanel.add(new JButton(utilUi.applyI18n("panes.xslteditor.param.del", new DelAction())));
		add(btnPanel, BorderLayout.SOUTH);
	}

	public Hashtable getParams() {
		return paramModel.getParamsAsHashtable();
	}

	public class AddAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			paramModel.addParam();
		}
	}

	public class DelAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			paramModel.removeParam(table.getSelectedRow());
		}
	}
}
