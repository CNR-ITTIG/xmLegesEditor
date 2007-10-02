package it.cnr.ittig.xmleges.editor.services.dalos.objects;

import it.cnr.ittig.xmleges.core.services.i18n.I18n;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

public class SynsetTree extends SingleTree {
	
	public SynsetTree(DefaultTreeModel model, I18n i18n) {
		
		super(model);
				
     	//Personalizza CellRenderer:
     	DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
     	renderer.setLeafIcon(i18n.getIconFor("editor.panes.dalos.concept.icon"));
     	renderer.setOpenIcon(i18n.getIconFor("editor.panes.dalos.treeopen.icon"));
     	renderer.setClosedIcon(i18n.getIconFor("editor.panes.dalos.treeclosed.icon"));
     	
     	setCellRenderer(renderer);
		
	}
	
}
