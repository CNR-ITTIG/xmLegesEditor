<?xml version="1.0" encoding="UTF-8"?>

<!--
============================================================================================
File di trasformazione per gli elementi di meta.dtd.

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
                xmlns:h     = "http://www.w3.org/HTML/1998/html4"
                xmlns       = "http://www.normeinrete.it/nir/1.0"
                xmlns:mapper= "xalan://it.cnr.ittig.xmleges.core.blocks.panes.xsltmapper.XsltMapperImpl"
                version     = "1.0"
>

<xsl:output method="html" />

<xsl:include href="xsltmapper-1.0.xsl"/>

<!-- mettere gli elementi gestiti da questo file -->
<xsl:strip-space elements="*" />



<xsl:template match="*[
	name()='descrittori' or name()='lavoripreparatori' or
	name()='redazionale' or name()='proprietario' or
	name()='disposizioni'
	]">
	<center><font size="+2"><b><xsl:value-of select="name()"/></b></font></center>
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
		<xsl:apply-templates />
	</xsl:element>
	<hr/>
</xsl:template>

<xsl:template match="node()">
	<b><xsl:value-of select="name()"/></b>
	<xsl:for-each select="@*">
		<br/>
		&#160;
		<font color="blue"><xsl:value-of select="name()"/></font>
		=
		<font color="green">"<xsl:value-of select="."/></font>"
	</xsl:for-each>
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
		<xsl:apply-templates />
	</xsl:element>
</xsl:template>



</xsl:transform>
