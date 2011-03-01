<?xml version="1.0" encoding="iso-8859-1"?>

<!--
============================================================================================
File di trasformazione per la modifica dell'articolato di un documento NIR.

author       : Mirco Taddei <mirco.taddei@gmail.com>
version      : 1.0
copyright (C): 2004 - 2005
               Istituto di Teoria e Tecniche dell'Informazione Giuridica (ITTIG)
               Consiglio Nazionale delle Ricerche - Italy
               http://www.ittig.cnr.it
license      : GNU General Public License (http://www.gnu.org/licenses/gpl.html)
============================================================================================
-->

<xsl:transform  xmlns:xsl   = "http://www.w3.org/1999/XSL/Transform"
                xmlns:xlink = "http://www.w3.org/1999/xlink"
                xmlns       = "http://www.w3.org/HTML/1998/html4"
                xmlns:nir   = "http://www.normeinrete.it/nir/2.2/"
 			    xmlns:mapper= "xalan://it.cnr.ittig.xmleges.core.blocks.panes.xsltmapper.XsltMapperImpl"
                version     = "1.0">

<xsl:output method="html" 
            omit-xml-declaration="yes"
            encoding="ISO-8859-15"
            indent="no"/>
            
<xsl:strip-space elements="*" />

<xsl:include href="xsltmapper-1.0.xsl"/>

<xsl:template match="/">
   <html>
   		<head>
   		<!--
		    <style type="text/css">
		        body { font-family: Arial; }
		    </style>
		-->
        </head>
        <body>
            <xsl:apply-templates select="//processing-instruction('rif')" />
        </body>
    </html>
</xsl:template>


<xsl:template match="processing-instruction('rif')">
	<pre color="FF3300"><xsl:element name="span" use-attribute-sets="XsltMapperSetClass"><xsl:attribute name="color">"FF3300"</xsl:attribute>&#160;<xsl:if test="string-length(.) = 0"><xsl:value-of select="mapper:getTextStringIfEmpty(.)" /></xsl:if><xsl:if test="string-length(.) != 0"> - <xsl:value-of select="substring-before(substring-after(.,'&gt;'),'&lt;')" /><xsl:call-template name="makeUrn" /></xsl:if></xsl:element>
	</pre>
</xsl:template>


<xsl:template name="makeUrn">
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass"><xsl:attribute name="color">"FF3300"</xsl:attribute>&#160;<xsl:if test="string-length(.) = 0"><xsl:value-of select="mapper:getTextStringIfEmpty(.)" /></xsl:if><xsl:if test="string-length(.) != 0">[<xsl:value-of select="substring-before(substring-after(.,'&quot;'),'&quot;')" />]</xsl:if>
    </xsl:element>
</xsl:template>


 </xsl:transform>
    
    
    <!--xsl:template match="processing-instruction('rif')">
	  <font color="FF3300">
	  <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
		<xsl:if test="string-length(.) = 0">
			<xsl:value-of select="mapper:getTextStringIfEmpty(.)" />
		</xsl:if>
		<xsl:if test="string-length(.) != 0">    
			- <xsl:value-of select="substring-before(substring-after(.,'&gt;'),'&lt;')" />
		</xsl:if>
	 </xsl:element>
	 </font>
</xsl:template-->

<!--xsl:template match="processing-instruction('rif')">
	  <font color="FF3300">
	  <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		<xsl:if test="string-length(.) = 0">
			<xsl:value-of select="mapper:getTextStringIfEmpty(.)" />
		</xsl:if>
		<xsl:if test="string-length(.) != 0">    
			- <xsl:value-of select="substring-before(substring-after(.,'&gt;'),'&lt;')" />
			<xsl:call-template name="makeA" />
		</xsl:if>	
	 </xsl:element>
	 </font>
</xsl:template-->