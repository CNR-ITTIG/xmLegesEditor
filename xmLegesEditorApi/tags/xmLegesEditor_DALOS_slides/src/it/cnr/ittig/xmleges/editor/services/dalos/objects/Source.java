package it.cnr.ittig.xmleges.editor.services.dalos.objects;

public class Source implements Comparable {
	
	String content;
	
	String link;
	
	String partitionId;	
	
	public Source(String id) {
		
		content = "";
		link = "";
		partitionId = id;
	}
	
	public String getId() {
		
		return partitionId;
	}

	public void setContent(String str) {
		
		content = str;
	}
	
	public String getContent() {
		
		return content;
	}

	public void setLink(String str) {
		
		link = str;
	}
	
	public String getLink() {
		
		return link;
	}

	public String toString() {

		return partitionId;
	}
	
	public boolean equals(Object obj) {

		if(obj instanceof Source) {
			if(partitionId.equalsIgnoreCase(((Source) obj).getId())
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
		String str = getId() + getContent();
		String ostr = o.getId() + o.getContent();
		if(ostr.compareToIgnoreCase(str) < 0) {
			return -1;
		} else {
			return 1;
		}
		
	}
}

