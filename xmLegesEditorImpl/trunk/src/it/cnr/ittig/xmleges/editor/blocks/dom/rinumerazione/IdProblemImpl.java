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
	public boolean canRemoveByUser() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canResolveProblem() {
		// TODO Auto-generated method stub
		return false;
	}

	public Node getNode() {
		// TODO Auto-generated method stub
		return this.node;
	}

	public String getText() {
		// TODO Auto-generated method stub
		return "Removed "+attrValue+". errore in "+attrName+" di "+node.getNodeName()+" ("+UtilDom.getAttributeValueAsString(node, "id")!=null?UtilDom.getAttributeValueAsString(node, "id"):""+")";
	}

	public int getType() {
		// TODO Auto-generated method stub
		return this.type;
	}

	public boolean resolveProblem() {
		// TODO Auto-generated method stub
		return false;
	}

}
