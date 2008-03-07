package it.cnr.ittig.xmleges.editor.blocks.contents;

import it.cnr.ittig.xmleges.core.services.document.DomEdit;
import it.cnr.ittig.xmleges.core.util.date.UtilDate;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import org.w3c.dom.Node;

public class DataDocProblemImpl extends ContentsValidatorProblem {

	int type;

	String text;

	Node dataDoc = null;

	boolean removableByUser = false;

	boolean resolvableProblem = true;

	public DataDocProblemImpl(int type, String text, ContentsValidatorImpl cv) {
		super(cv);
		this.type = type;
		this.text = text;
	}

	public boolean test() {
		dataDoc = UtilDom.findParentByName(cv.changedNode, "dataDoc");
		String norm = UtilDom.findAttribute(dataDoc, "norm");

		if (UtilDate.textualFormatToDate(UtilDom.getText(dataDoc)) == null)
			return false;
		return (norm.trim().equalsIgnoreCase(UtilDate.dateToNorm(UtilDate.textualFormatToDate(UtilDom.getText(dataDoc)))));
	}

	public boolean canRemoveByUser() {
		return this.removableByUser;
	}

	public void setCanRemoveByUser(boolean removableByUser) {
		this.removableByUser = removableByUser;
	}

	public boolean canResolveProblem() {
		return this.resolvableProblem;
	}

	public void setCanResolveProblem(boolean resolvableProblem) {
		this.resolvableProblem = resolvableProblem;
	}

	public Node getNode() {
		return cv.changedNode;
	}

	public String getText() {
		return this.text;
	}

	public int getType() {
		return this.type;
	}

	public boolean resolveProblem() {

		if (cv.changeType == DomEdit.ATTR_NODE_MODIFIED) {
			String norm = UtilDom.findAttribute(dataDoc, "norm");
			UtilDom.setTextNode(dataDoc, UtilDate.dateTotextualFormat(UtilDate.normToDate(norm)));
			return true;
		} else {
			if (UtilDate.textualFormatToDate(UtilDom.getText(dataDoc)) != null) {
				UtilDom.setAttributeValue(dataDoc, "norm", UtilDate.dateToNorm(UtilDate.textualFormatToDate(UtilDom.getText(dataDoc))));
				return true;
			}
		}
		return false;
	}

	public boolean equals(Object obj) {
		try {
			if (((DataDocProblemImpl) obj).getNode() == this.getNode())
				return true;
			return false;
		} catch (Exception e) {
			return false;
		}
	}

}
