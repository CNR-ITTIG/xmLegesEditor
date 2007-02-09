<?xml version="1.0" encoding="UTF-8"?>

<!--
=================
Template minimale
=================
-->

<xsl:transform  xmlns:xsl   = "http://www.w3.org/1999/XSL/Transform"
                xmlns:xlink = "http://www.w3.org/1999/xlink"
                xmlns:h     = "http://www.w3.org/HTML/1998/html4"
                xmlns       = "http://www.normeinrete.it/nir/1.0"
                xmlns:mapper= "xalan://it.cnr.ittig.xmleges.core.blocks.panes.xsltmapper.XsltMapperImpl"
                version     = "1.0"
>

<xsl:output method="html" />
<xsl:include href="xsltmapper-1.0.xsl"/>
<xsl:strip-space elements="*" />

<xsl:template match="/">
   <html>
   		<head>
		    <style type="text/css">
		        body { font-family: Arial; }
		    </style>
        </head>
        <body>
            <xsl:apply-templates />
        </body>
    </html>
</xsl:template>

<xsl:template match="*">
	<xsl:element name="div">
		<xsl:attribute name="style">
			margin: 5 0 5 10;
	    </xsl:attribute>
	    
    	<!--	=== mostro il tag aperto	===	-->
	    <font color="blue">
		    <xsl:text>&lt;</xsl:text>
		    <xsl:value-of select="name()"/>
		    <xsl:text>&gt;</xsl:text>
	    </font>
	    
        	<!--	=== parte essenziale	===	-->
    		<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
	        <xsl:apply-templates />
	        
    	<!--	=== mostro il tag chiuso	===	-->	        
		<font color="blue">
	    	<xsl:text>&lt;/</xsl:text>
		    <xsl:value-of select="name()"/>
		    <xsl:text>&gt;</xsl:text>
		</font>		    
	</xsl:element>	    
</xsl:template> 

</xsl:transform>
