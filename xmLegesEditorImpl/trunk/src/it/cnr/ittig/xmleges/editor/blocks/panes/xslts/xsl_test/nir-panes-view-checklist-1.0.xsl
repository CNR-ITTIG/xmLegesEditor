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
                xmlns:nir   = "http://www.normeinrete.it/nir/2.2/"
 			    xmlns:mapper= "xalan://it.cnr.ittig.xmleges.core.blocks.panes.xsltmapper.XsltMapperImpl"
                version     = "1.0"
>

<xsl:output method="html" />

<xsl:include href="xsltmapper-1.0.xsl"/>

<xsl:strip-space elements="*" />

<xsl:template match="nir:NIR">
	<html>
		<head>

        </head>
		<body>
			<xsl:text> [ Documento principale ] </xsl:text><br/>
			<xsl:call-template name="intestazione" />
			<xsl:call-template name="descrittori" />
			<xsl:for-each select="//nir:annesso">
				<br/><br/><xsl:text> [ Annesso: </xsl:text><xsl:value-of select="./nir:testata/nir:denAnnesso"/><xsl:text> ] </xsl:text><br/>			
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
		<xsl:when test="./*/nir:intestazione/nir:emanante">
			<xsl:apply-templates select="./*/nir:intestazione/nir:emanante" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="elementoAssente">
				<xsl:with-param name="elemento">Emanante</xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
		<xsl:when test="./*/nir:intestazione/nir:tipoDoc">
			<xsl:apply-templates select="./*/nir:intestazione/nir:tipoDoc" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="elementoAssente">
				<xsl:with-param name="elemento">Tipo documento</xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
		<xsl:when test="./*/nir:intestazione/nir:dataDoc">
			<xsl:apply-templates select="./*/nir:intestazione/nir:dataDoc" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="elementoAssente">
				<xsl:with-param name="elemento">Data documento</xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
		<xsl:when test="./*/nir:intestazione/nir:numDoc">
			<xsl:apply-templates select="./*/nir:intestazione/nir:numDoc" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="elementoAssente">
				<xsl:with-param name="elemento">Numero documento</xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
		<xsl:when test="./*/nir:intestazione/nir:titoloDoc">
			<xsl:apply-templates select="./*/nir:intestazione/nir:titoloDoc" />
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
		<xsl:when test="./*/nir:meta/nir:descrittori/nir:pubblicazione">
			<xsl:apply-templates select="./*/nir:meta/nir:descrittori/nir:pubblicazione" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="elementoAssente">
				<xsl:with-param name="elemento">Pubblicazione</xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
		<xsl:when test="./*/nir:meta/nir:descrittori/nir:entratainvigore">
			<xsl:apply-templates select="./*/nir:meta/nir:descrittori/nir:entratainvigore" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="elementoAssente">
				<xsl:with-param name="elemento">Entrata in vigore</xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
		<xsl:when test="./*/nir:meta/nir:descrittori/nir:urn">
			<xsl:apply-templates select="./*/nir:meta/nir:descrittori/nir:urn"/>
		</xsl:when>
		<!--	xsl:when test="/nir:NIR/*/nir:meta/nir:descrittori/nir:urn/@iniziovigore='t1'">
			<xsl:apply-templates select="/nir:NIR/*/nir:meta/nir:descrittori/nir:urn" />
		</xsl:when	-->
		<xsl:otherwise>
			<xsl:call-template name="elementoAssente">
				<xsl:with-param name="elemento">Urn</xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="nir:pubblicazione" >
	<br/><font color="blue"><xsl:text> - </xsl:text><xsl:value-of select="local-name(.)"/><xsl:text>: </xsl:text></font>
	 <xsl:element name="span"> <!-- use-attribute-sets="XsltMapperSetClass"-->   
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:value-of select="."/>
        <xsl:text> ( Data: </xsl:text><xsl:value-of select="@norm"/>
        <xsl:text> Numero: </xsl:text><xsl:value-of select="@num"/>
        <xsl:text> Tipo: </xsl:text><xsl:value-of select="@tipo"/>
        <xsl:text> ) </xsl:text>
	</xsl:element>		
</xsl:template>

<xsl:template match="nir:entratainvigore" >
	<br/><font color="blue"><xsl:text> - </xsl:text><xsl:value-of select="local-name(.)"/><xsl:text>: </xsl:text></font>
	<xsl:element name="span"> <!-- use-attribute-sets="XsltMapperSetClass"-->
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:value-of select="."/>
        <xsl:text> ( Data: </xsl:text><xsl:value-of select="@norm"/>
        <xsl:text> ) </xsl:text>
	</xsl:element>		
</xsl:template>

<xsl:template match="nir:urn" >
	<xsl:if test="not(@iniziovigore) or @iniziovigore='t1'">
		<br/><font color="blue"><xsl:text> - </xsl:text><xsl:value-of select="local-name(.)"/><xsl:text>: </xsl:text></font>
		<xsl:element name="span" > <!-- use-attribute-sets="XsltMapperSetClass"-->
    		<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
       	 <xsl:value-of select="."/><xsl:value-of select="@valore"/>
		</xsl:element>	
	</xsl:if>		
</xsl:template>

<xsl:template match="*" >
	<br/><font color="blue"><xsl:text> - </xsl:text><xsl:value-of select="local-name(.)"/><xsl:text>: </xsl:text></font>
	<xsl:element name="span" > <!-- use-attribute-sets="XsltMapperSetClass"-->
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:value-of select="."/>
	</xsl:element>		
</xsl:template>

<xsl:template name="elementoAssente">
	<xsl:param name="elemento"/>
	<br/><font color="blue"><xsl:text> - </xsl:text><xsl:value-of select="$elemento"/><xsl:text>: </xsl:text></font> <font color="red">Assente</font>
</xsl:template>


</xsl:transform>