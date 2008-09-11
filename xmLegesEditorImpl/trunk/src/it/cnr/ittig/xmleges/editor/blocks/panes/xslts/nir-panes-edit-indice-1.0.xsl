<?xml version="1.0" encoding="UTF-8"?>

<!--
============================================================================================

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
                version     = "1.0"
>

<xsl:output method="html" 
            omit-xml-declaration="yes"
            encoding="ISO-8859-15"
            indent="yes"/>
            
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
	<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='articolato']/*" />
	</body>
	</html>
</xsl:template>

<xsl:template match="*[name()='libro' or name()='parte' or name()='titolo' or name()='capo' or name()='sezione' or name()='paragrafo' or name()='partizione' or name()='articolo']">
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
    	<xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="*[name()='num']">
	<xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
   		<xsl:apply-templates  />
    </xsl:element>
</xsl:template>

<xsl:template match="*[name()='rubrica']">
    <font color="blue">
		<xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
	    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
    		<xsl:apply-templates  />
	    </xsl:element>
    </font>
</xsl:template>

<xsl:template match="*"/>

</xsl:transform>
