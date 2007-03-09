<?xml version="1.0" encoding="iso-8859-1"?>

<!--
============================================================================================
File di trasformazione per la modifica dell'articolato di un documento NIR.

author       : Maurizio Mollicone
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
			<xsl:for-each select="//*[name()='meta']">
				<xsl:apply-templates select="./*[name()='redazionale']/*[name()='nota']" />
			</xsl:for-each>
		</body>
	</html>
</xsl:template>

<xsl:template match="*[name()='denAnnesso']" >
		<hr/>
		<center>
			<b>
				<xsl:value-of select="."/>
			</b>
		</center>		
</xsl:template>

<xsl:template match="*[name()='nota']" >
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
		<xsl:apply-templates select="../../../../*[name()='testata']/*[name()='denAnnesso']" />
		<xsl:variable name="idnota"><xsl:value-of select="@id"/></xsl:variable>
		<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
	    	<xsl:attribute name="style">
	            margin: 10 5 10 5;
	        </xsl:attribute>
	        
			<font color="blue">
			<xsl:for-each select="//*[name()='ndr' and @num=$idnota]">
			  <b>
				<xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
					<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
					<xsl:apply-templates />
				</xsl:element>
				</b>
			  </xsl:for-each>
			</font>
						
			<xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
				<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
				<xsl:apply-templates />
			</xsl:element>
				
	    </xsl:element>
	</xsl:element>
</xsl:template>



</xsl:transform>