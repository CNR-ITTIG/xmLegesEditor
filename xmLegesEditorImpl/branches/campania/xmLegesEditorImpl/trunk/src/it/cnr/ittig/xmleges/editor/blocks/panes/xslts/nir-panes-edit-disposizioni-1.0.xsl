<?xml version="1.0" encoding="UTF-8"?>
 
<xsl:transform  xmlns:xsl   = "http://www.w3.org/1999/XSL/Transform"
                xmlns:xlink = "http://www.w3.org/1999/xlink"
                xmlns:h     = "http://www.w3.org/HTML/1998/html4"
                xmlns       = "http://www.normeinrete.it/nir/2.2"
                xmlns:mapper= "xalan://it.cnr.ittig.xmleges.core.blocks.panes.xsltmapper.XsltMapperImpl"
                version     = "1.0"
>

<xsl:output method="html" />
<xsl:include href="nir-panes-edit-documento-1.0.xsl"/>
<xsl:strip-space elements="*" />

<xsl:template match="/">
   <html>
   		<head>
		    <style type="text/css">
		        body { font-family: Arial; }
		    </style>
        </head>
        <base href="{$base}" />
        <body>
			<xsl:for-each select="//*[name()='mod']">
				<xsl:apply-templates select="./.." />
			</xsl:for-each>
        </body>
    </html>
</xsl:template>

<xsl:template match="*[name()='mod']">
	<br/>
	<xsl:variable name="id">#<xsl:value-of select="@id"/></xsl:variable>
	<xsl:choose>
		<xsl:when test="/*[name()='NIR']/*/*[name()='meta']/*[name()='disposizioni']/*[name()='modificheattive']/*/*[name()='dsp:pos'][@xlink:href=$id]">
		<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='meta']/*[name()='disposizioni']/*[name()='modificheattive']/*/*[name()='dsp:pos'][@xlink:href=$id]/.."/>
	</xsl:when>
		<xsl:otherwise>
			<div><font color="Green">Non ancora riconosciuta</font></div><br/>
		</xsl:otherwise>
	</xsl:choose>		
	<xsl:call-template name="modclassico" />
</xsl:template> 

<xsl:template match="*[name()='dsp:abrogazione' or name()='dsp:sostituzione' or name()='dsp:integrazione']">
	<div>
	  <font color="Green">
		Riconosciuta come disposizione di <xsl:value-of select="local-name()"/><xsl:text>.</xsl:text><br/>
		La norma &#232; <xsl:value-of select="*[name()='dsp:norma']/@xlink:href"/><xsl:text>.</xsl:text><br/>
		<xsl:if test="*[name()='dsp:norma']/*[name()='dsp:subarg']">
			Bordo: <xsl:apply-templates select="*[name()='dsp:norma']/*[name()='dsp:subarg']/*[name()='ittig:bordo']" mode="dispo"/><br/>
		</xsl:if>
		<xsl:if test="*[name()='dsp:novella']">
			La virgoletta <xsl:value-of select="*[name()='dsp:novella']/*[name()='dsp:pos']/@xlink:href"/><xsl:text> &#232; la novella, di tipo </xsl:text><xsl:value-of select="*[name()='dsp:novella']/*[name()='dsp:subarg']/*[name()='ittig:tipo']/@valore"/><xsl:text>.</xsl:text><br/>
		</xsl:if>
		<xsl:if test="*[name()='dsp:posizione']">
			La posizione &#232; <xsl:value-of select="*[name()='dsp:posizione']/*[name()='dsp:pos']/*[name()='ittig:dove']/@valore"/><xsl:text> </xsl:text><xsl:value-of select="*[name()='dsp:posizione']/*[name()='dsp:pos']/@xlink:href"/><xsl:text>.</xsl:text><br/>
		</xsl:if>
		<xsl:if test="*[name()='dsp:novellando']">
			Il novellando di tipo <xsl:value-of select="*[name()='dsp:novellando']/*[name()='dsp:subarg']/*[name()='ittig:tipo']/@valore"/><xsl:text> &#232; </xsl:text>
			<xsl:apply-templates select="*[name()='dsp:novellando']"  mode="dispo"/><br/>
		</xsl:if>
	  </font>
	</div>
	<xsl:call-template name="modclassico" />
</xsl:template> 

<xsl:template match="*[name()='dsp:novellando']" mode="dispo">
	<xsl:choose>
		<xsl:when test="*[name()='dsp:pos']/*[name()='ittig:ruolo']">
			<xsl:value-of select="*[name()='dsp:pos']/*[name()='ittig:ruolo']/@valore"/><xsl:text> </xsl:text>
			<xsl:value-of select="*[name()='dsp:pos']/@xlink:href"/><xsl:text>, </xsl:text>
		</xsl:when>
		<xsl:otherwise>
			il contenuto<xsl:text>, </xsl:text>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template> 


<xsl:template match="*[name()='ittig:bordo']" mode="dispo">
	<xsl:value-of select="@tipo"/>
	<xsl:text> </xsl:text>
	<xsl:value-of select="@num"/>
	<xsl:if test="@ordinale='si'">
		<xsl:text> (ordinale)</xsl:text>
	</xsl:if>
	<xsl:text>, </xsl:text>
	<xsl:apply-templates select="*[name()='ittig:bordo']" mode="dispo"/>
</xsl:template> 

<xsl:template name="mostrainfo">
	<font bgcolor="Green">
		Disposizione di  <xsl:value-of select="local-name()"/>
	</font>
</xsl:template> 

<xsl:template name="modclassico">
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
		<font bgcolor="#FFDDAA">
    		<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        	<xsl:apply-templates />
		</font>
    </xsl:element>
</xsl:template> 

</xsl:transform>
