<?xml version="1.0" encoding="iso-8859-1"?>

<!--
============================================================================================
File di trasformazione per i metadati analitici di un documento NIR.

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
<xsl:include href="nir-panes-dtd-globali-1.0.xsl"/>
<xsl:include href="nir-panes-dtd-testo-1.0.xsl"/>

<!-- metere elementi gestiti da questo file
<xsl:strip-space elements="*" />
-->


pubblicazione, altrepubblicazioni?, urn+, alias*, 
                         vigenza+, relazioni?,keywords*)

<!-- ================================================================================ -->
<!-- =============================================================== INTESTAZIONE === -->
<!-- ================================================================================ -->
<xsl:template match="titoloDoc"> <!-- il css stranamente non funziona -->
	<center>
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    <font size="+1">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
        </font>
    </xsl:element>
	</center>
</xsl:template>

<xsl:template match="emanante">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="tipoDoc|dataDoc|numDoc">
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<!-- ================================================================================ -->
<!-- =========================================================== FORMULA INIZIALE === -->
<!-- ================================================================================ -->
<xsl:template match="formulainiziale|preambolo">
        <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
        	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
            <xsl:apply-templates />
        </xsl:element>
</xsl:template>

</xsl:transform>
