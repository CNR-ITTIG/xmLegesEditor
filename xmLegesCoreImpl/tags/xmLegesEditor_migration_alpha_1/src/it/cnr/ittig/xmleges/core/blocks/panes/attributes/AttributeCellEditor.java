package it.cnr.ittig.xmleges.core.blocks.panes.attributes;

import it.cnr.ittig.xmleges.core.services.panes.attributes.AttributeEditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.w3c.dom.Node;

/**
 * Classe per visualizzare correttamente l'editor delle celle della tabella e presentare
 * il pulsante per l'apertura della form se supportata dall'editor incapsulato.
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
public class AttributeCellEditor extends JPanel implements ActionListener, FocusListener {
	AttributesCellEditor attributesCellEditor;

	/** Editor della cella. */
	AttributeEditor editor;

	/** Pulsante per l'apertura della form. */
	JButton btnForm;

	/** Pulsante per terminare l'editing della cella. */
	// JButton btnOk;
	/** Pulsante per annullare l'editing della cella. */
	// JButton btnCancel;
	/** Nodo di cui visualizzare la tabella con gli attributi. */
	Node node;

	/** Attributo in fase di modifica. */
	Node attrib;

	Component editorComponent = null;

	/**
	 * Costruttore di <code>AttributeCellEditor</code>.
	 */
	public AttributeCellEditor(AttributesCellEditor attributesCellEditor) {
		setLayout(new BorderLayout(0, 0));
		btnForm = new JButton("...");
		btnForm.setMargin(new Insets(0, 0, 0, 0));
		btnForm.addActionListener(this);
		// btnOk = new JButton("Ok");
		// btnOk.setMargin(new Insets(0, 0, 0, 0));
		// btnOk.addActionListener(this);
		this.attributesCellEditor = attributesCellEditor;
	}

	/**
	 * Imposta l'editor attivato per la modifica corrente dell'attributo
	 * <code>attrib</code> del nodo <code>node</code>.
	 * 
	 * @param editor modificatore specializzato dell'attributo
	 * @param node nodo dell'albero DOM che ha l'attributo <code>attrib</code>
	 * @param attrib nodo attributo
	 */
	public void setEditor(AttributeEditor editor, Node node, Node attrib) {
		removeAll();
		this.editor = editor;
		this.node = node;
		this.attrib = attrib;
		// JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		// btnPanel.setBorder(null);
		boolean hasForm = editor.hasForm(node, attrib);
		if (hasForm)
			add(btnForm, BorderLayout.EAST);
		// btnPanel.add(btnForm);
		// else
		// remove(btnForm);
		// btnPanel.add(btnOk);
		// add(btnPanel, BorderLayout.EAST);
		editorComponent = editor.getEditor(node, attrib);
		if (!hasForm)
			editorComponent.addFocusListener(this);
		add(editorComponent, BorderLayout.CENTER);
	}

	/**
	 * Restituisce il valore modificato che si intende memorizzare nell'attributo.
	 * 
	 * @return valore modificato per l'attributo
	 */
	public String getValueChanged() {
		return editor.getValueChanged(node, attrib);
	}

	// ////////////////////////////////////////////// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (btnForm.equals(e.getSource())) {
			editor.showEditorForm();
		}
		// else if (btnOk.equals(e.getSource()))
		// attributesCellEditor.stopCellEditing();
	}

	// /////////////////////////////////////////////// FocusListener Interface
	public void focusGained(FocusEvent e) {
	}

	public void focusLost(FocusEvent e) {
		editorComponent.removeFocusListener(this);
		// attributesCellEditor.cancelCellEditing();
		attributesCellEditor.stopCellEditing();

	}

}
