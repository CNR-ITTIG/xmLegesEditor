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
<xsl:include href="xsltutil-1.0.xsl"/>

<xsl:template match="/">
	<html>
		<head>
        </head>
		<body>
			<xsl:apply-templates select="//nir:annesso|//nir:mrif|//nir:rif[name(..)!='mrif']|//nir:irif[name(..)!='mrif']" />
		</body>
	</html>
</xsl:template>

<xsl:template match="nir:annesso" >
	 <xsl:if test="descendant:: nir:rif | nir:mrif">
		<br/><hr/>
		<center>
			<b>
				<xsl:value-of select="./nir:testata/nir:denAnnesso"/>
			</b>
		</center>		
	</xsl:if>		
</xsl:template>

<xsl:template match="nir:mrif">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        - <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="nir:irif">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        - <xsl:apply-templates />
<!--        [fino a ??]				-->
    </xsl:element>
</xsl:template>

<xsl:template match="nir:rif">
	<xsl:choose>
		<xsl:when test="name(..) != 'mrif'">
			<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
				<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
				<xsl:choose>
					<xsl:when test="substring(@xlink:href, 1, 1)!='#'">
						<xsl:element name="font" use-attribute-sets="XsltMapperVerificaUrn">
							- <xsl:apply-templates /> 
						</xsl:element>
					</xsl:when>
					<xsl:otherwise>
						- <xsl:apply-templates /> 
					</xsl:otherwise>	
				</xsl:choose>					
				<xsl:call-template name="makeA" />
		    </xsl:element>
		
		</xsl:when>
		<xsl:when test="name(..) = 'mrif'">
			<xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
				<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
				<xsl:apply-templates /> 
			</xsl:element>
			<xsl:call-template name="makeA" />
		</xsl:when>
	</xsl:choose>
</xsl:template>

<xsl:template name="makeA">


	<xsl:variable name="riftilde">
			<xsl:choose>
			   <xsl:when test="contains(@xlink:href, '#')">
			      <xsl:value-of select="substring-before(@xlink:href, '#')"/>~<xsl:value-of select="substring-after(@xlink:href, '#')"/>
			   </xsl:when>
		   <xsl:otherwise>
				<xsl:value-of select="@xlink:href"/>
		   </xsl:otherwise>
		   </xsl:choose>
	</xsl:variable>


	<xsl:element name="a">
		<xsl:choose>
			<xsl:when test="substring(@xlink:href, 1, 1)='#'">
				<xsl:attribute name="href"><xsl:value-of select="@xlink:href"/></xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="href">http://www.normattiva.it/uri-res/N2Ls?<xsl:value-of select="$riftilde"/></xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
		[<xsl:apply-templates select="@xlink:href"/>]
	</xsl:element>
</xsl:template>


</xsl:transform>