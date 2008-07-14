package it.cnr.ittig.xmleges.editor.blocks.dom.rinumerazione;

import org.w3c.dom.Node;

import it.cnr.ittig.xmleges.core.services.panes.problems.Problem;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

public class IdProblemImpl implements Problem {

	Node node=null;
	int type=-1;
	String attrName="";
	String attrValue="";
	
	public IdProblemImpl(Node node, int type, String attrName, String attrValue){
		
		this.node=node;
		this.type=type;
		this.attrName=attrName;
		this.attrValue=attrValue;
		
	}
	//dovrebbe esserci un controllo sul tipo di IDProblem xes. nel caso di ndr va bene true.
	public boolean canRemoveByUser() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean canResolveProblem() {
		// TODO Auto-generated method stub
		return true;
	}

	public Node getNode() {
		// TODO Auto-generated method stub
		return this.node;
	}

	public String getText() {
		// TODO Auto-generated method stub
		return "Removed "+attrValue+". errore in "+attrName+" di "+node.getNodeName()+" ("+(UtilDom.getAttributeValueAsString(node, "id")!=null?UtilDom.getAttributeValueAsString(node, "id"):"")+")";
	}

	public int getType() {
		// TODO Auto-generated method stub
		return this.type;
	}

	//elimina completamente il nodo riferente. va bene nel caso di ndr con nota nulla.
	public boolean resolveProblem() {
		
		Node parent = (this.node).getParentNode();								
		parent.removeChild(this.node);
		UtilDom.mergeTextNodes(parent);
		return true;
	}

}
