<?xml version="1.0" encoding="UTF-8"?>

<!--
============================================================================================
File di trasformazione per la modifica del documento principale di un documento NIR.

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
<xsl:include href="nir-panes-dtd-norme-1.0.xsl"/>
<xsl:include href="nir-panes-dtd-testo-1.0.xsl"/>
<xsl:include href="nir-panes-dtd-globali-1.0.xsl"/>
<xsl:include href="xsltmapper-1.0.xsl"/>
<xsl:strip-space elements="*" />

<!-- aggiunto relazione dei DL definito nell'xsl dell'intestazione -->
<!-- aggiunto il tag confronto dei DL in cima al documento -->

<xsl:template match="/">
   <html>
   		<head>
		    <style type="text/css">
		        body { font-family: Arial; }
		    </style>
        </head>
        <body>
            <xsl:apply-templates select="/*[name()='NIR']/*/*[name()='meta']/*[name()='confronto']" />
            <xsl:apply-templates select="/*[name()='NIR']/*/*[name()='intestazione']" />
            <xsl:apply-templates select="/*[name()='NIR']/*/*[name()='formulainiziale']" />
            <xsl:apply-templates select="/*[name()='NIR']/*/*[name()='relazione']"/>
            <xsl:apply-templates select="/*[name()='NIR']/*/*[name()='articolato']|/*[name()='NIR']/*/*[name()='contenitore']|/*[name()='NIR']/*/*[name()='gerarchia']" />
            <xsl:apply-templates select="/*[name()='NIR']/*/*[name()='formulafinale']" />
            <xsl:apply-templates select="/*[name()='NIR']/*/*[name()='conclusione']" />
        </body>
    </html>
</xsl:template>

<xsl:template match="*[name()='rif']">
	<font color="blue"><u>
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
    </u></font>
	<xsl:call-template name="makeA" />
</xsl:template> 

<!-- riferimenti incompleti -->
<xsl:template match="processing-instruction('rif')">
	  <font color="FF3300"><u>
	  <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">&#160;
		<xsl:if test="string-length(.) = 0">
			<xsl:value-of select="mapper:getTextStringIfEmpty(.)" />
		</xsl:if>
		<xsl:if test="string-length(.) != 0">    
			<xsl:value-of select="substring-before(substring-after(.,'&gt;'),'&lt;')" />
		</xsl:if>
	 </xsl:element>
	 </u></font>
</xsl:template>

<xsl:template name="makeA">
	<xsl:if test="substring(@xlink:href, 1, 1)='#'">
		<xsl:element name="a">
			<xsl:attribute name="href"><xsl:value-of select="@xlink:href" /></xsl:attribute>
			[*]
		</xsl:element>
	</xsl:if>
</xsl:template>

</xsl:transform>
