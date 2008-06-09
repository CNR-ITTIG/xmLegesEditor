package it.cnr.ittig.xmleges.editor.services.dalos.objects;

import java.util.Collection;
import java.util.HashSet;

public class Source implements Comparable {
	
	private String content;
	
	private String link;
	
	private String partitionId;
	private String documentId;
	
	private Collection linkedSynsets;
	
	public Collection getLinkedSynsets() {
		return linkedSynsets;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public Source() {
		
		content = "";
		link = null;
		partitionId = null;
		linkedSynsets = new HashSet();
	}		
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPartitionId() {
		return partitionId;
	}

	public void setPartitionId(String partitionId) {
		this.partitionId = partitionId;
	}

	public String toString() {

		return partitionId;
	}
	
	public boolean equals(Object obj) {

		if(obj instanceof Source) {
			if(partitionId.equalsIgnoreCase(((Source) obj).getPartitionId())
					&& content.equalsIgnoreCase(((Source) obj).getContent()) ) {
				return true;
			}
		}
		return false;
	}
	
	public int hashCode() {
		return 1;
	}

	public int compareTo(Object obj) {
		
		if(equals(obj)) {
			return 0;
		}
		
		if(!(obj instanceof Source)) {
			return 1;
		}
		
		Source o = (Source) obj;
		String str = getPartitionId() + getContent();
		String ostr = o.getPartitionId() + o.getContent();
		if(ostr.compareToIgnoreCase(str) < 0) {
			return -1;
		} else {
			return 1;
		}
		
	}
}

