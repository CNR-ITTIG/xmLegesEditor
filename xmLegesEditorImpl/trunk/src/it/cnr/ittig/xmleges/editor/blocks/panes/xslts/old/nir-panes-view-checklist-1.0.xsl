<?xml version="1.0" encoding="iso-8859-1"?>

<!--
============================================================================================
Elementi sotto verifica: (documento principale e annessi)
	-	emanante
	-	tipodoc
	-	datadoc
	-	numerodoc
	-	pubblicazione
	-	urn del documento
	-	vigenza del documento
============================================================================================
-->

<xsl:transform  xmlns:xsl   = "http://www.w3.org/1999/XSL/Transform"
                xmlns:xlink = "http://www.w3.org/1999/xlink"
                xmlns:h     = "http://www.w3.org/HTML/1998/html4"
                xmlns       = "http://www.normeinrete.it/nir/2.2"
 			    xmlns:mapper= "xalan://it.cnr.ittig.xmleges.core.blocks.panes.xsltmapper.XsltMapperImpl"
                version     = "1.0"
>

<xsl:output method="html" />

<xsl:include href="xsltmapper-1.0.xsl"/>

<xsl:strip-space elements="*" />

<xsl:template match="*[name()='NIR']">
	<html>
		<head>
		    <style type="text/css">
		        body { font-family: Arial; }
		    </style>
        </head>
		<body>
			<xsl:text> [ Documento principale ] </xsl:text><br/>
			<xsl:call-template name="intestazione" />
			<xsl:call-template name="descrittori" />
			<xsl:for-each select="//*[name()='annesso']">
				<br/><br/><xsl:text> [ Annesso: </xsl:text><xsl:value-of select="./*[name()='testata']/*[name()='denAnnesso']"/><xsl:text> ] </xsl:text><br/>			
				<xsl:call-template name="intestazione" />
				<xsl:call-template name="descrittori" />
			</xsl:for-each>
		</body>
	</html>
</xsl:template>

<xsl:template name="check">

</xsl:template>

<xsl:template name="intestazione">
	<xsl:choose>
		<xsl:when test="./*/*[name()='intestazione']/*[name()='emanante']">
			<xsl:apply-templates select="./*/*[name()='intestazione']/*[name()='emanante']" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="elementoAssente">
				<xsl:with-param name="elemento">Emanante</xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
		<xsl:when test="./*/*[name()='intestazione']/*[name()='tipoDoc']">
			<xsl:apply-templates select="./*/*[name()='intestazione']/*[name()='tipoDoc']" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="elementoAssente">
				<xsl:with-param name="elemento">Tipo documento</xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
		<xsl:when test="./*/*[name()='intestazione']/*[name()='dataDoc']">
			<xsl:apply-templates select="./*/*[name()='intestazione']/*[name()='dataDoc']" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="elementoAssente">
				<xsl:with-param name="elemento">Data documento</xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
		<xsl:when test="./*/*[name()='intestazione']/*[name()='numDoc']">
			<xsl:apply-templates select="./*/*[name()='intestazione']/*[name()='numDoc']" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="elementoAssente">
				<xsl:with-param name="elemento">Numero documento</xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
		<xsl:when test="./*/*[name()='intestazione']/*[name()='titoloDoc']">
			<xsl:apply-templates select="./*/*[name()='intestazione']/*[name()='titoloDoc']" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="elementoAssente">
				<xsl:with-param name="elemento">Titolo documento</xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>	
</xsl:template>


<xsl:template name="descrittori">
	<xsl:choose>
		<xsl:when test="./*/*[name()='meta']/*[name()='descrittori']/*[name()='pubblicazione']">
			<xsl:apply-templates select="./*/*[name()='meta']/*[name()='descrittori']/*[name()='pubblicazione']" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="elementoAssente">
				<xsl:with-param name="elemento">Pubblicazione</xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
		<xsl:when test="./*/*[name()='meta']/*[name()='descrittori']/*[name()='entratainvigore']">
			<xsl:apply-templates select="./*/*[name()='meta']/*[name()='descrittori']/*[name()='entratainvigore']" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="elementoAssente">
				<xsl:with-param name="elemento">Entrata in vigore</xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
		<xsl:when test="./*/*[name()='meta']/*[name()='descrittori']/*[name()='urn']">
			<xsl:apply-templates select="./*/*[name()='meta']/*[name()='descrittori']/*[name()='urn']"/>
		</xsl:when>
		<!--	xsl:when test="/*[name()='NIR']/*/*[name()='meta']/*[name()='descrittori']/*[name()='urn']/@iniziovigore='t1'">
			<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='meta']/*[name()='descrittori']/*[name()='urn']" />
		</xsl:when	-->
		<xsl:otherwise>
			<xsl:call-template name="elementoAssente">
				<xsl:with-param name="elemento">Urn</xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="*[name()='pubblicazione']" >
	<br/><font color="blue"><xsl:text> - </xsl:text><xsl:value-of select="local-name(.)"/><xsl:text>: </xsl:text></font>
	<xsl:element name="span">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:value-of select="."/>
        <xsl:text> ( Data: </xsl:text><xsl:value-of select="@norm"/>
        <xsl:text> Numero: </xsl:text><xsl:value-of select="@num"/>
        <xsl:text> Tipo: </xsl:text><xsl:value-of select="@tipo"/>
        <xsl:text> ) </xsl:text>
	</xsl:element>		
</xsl:template>

<xsl:template match="*[name()='entratainvigore']" >
	<br/><font color="blue"><xsl:text> - </xsl:text><xsl:value-of select="local-name(.)"/><xsl:text>: </xsl:text></font>
	<xsl:element name="span">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:value-of select="."/>
        <xsl:text> ( Data: </xsl:text><xsl:value-of select="@norm"/>
        <xsl:text> ) </xsl:text>
	</xsl:element>		
</xsl:template>

<xsl:template match="*[name()='urn']" >
	<xsl:if test="not(@iniziovigore) or @iniziovigore='t1'">
		<br/><font color="blue"><xsl:text> - </xsl:text><xsl:value-of select="local-name(.)"/><xsl:text>: </xsl:text></font>
		<xsl:element name="span">
    		<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
       	 <xsl:value-of select="."/><xsl:value-of select="@valore"/>
		</xsl:element>	
	</xsl:if>		
</xsl:template>

<xsl:template match="*" >
	<br/><font color="blue"><xsl:text> - </xsl:text><xsl:value-of select="local-name(.)"/><xsl:text>: </xsl:text></font>
	<xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:value-of select="."/>
	</xsl:element>		
</xsl:template>

<xsl:template name="elementoAssente">
	<xsl:param name="elemento"/>
	<br/><font color="blue"><xsl:text> - </xsl:text><xsl:value-of select="$elemento"/><xsl:text>: </xsl:text></font> <font color="red">Assente</font>
</xsl:template>


</xsl:transform>