<?xml version="1.0" encoding="UTF-8"?>

<xsl:transform  xmlns:xsl   = "http://www.w3.org/1999/XSL/Transform"
                xmlns:xlink = "http://www.w3.org/1999/xlink"
                xmlns:h     = "http://www.w3.org/HTML/1998/html4"
                xmlns:util= "xalan://it.cnr.ittig.xmleges.core.blocks.panes.xsltutil.XsltUtilImpl"
                version     = "1.0"
>

<xsl:output method="html"/>
<xsl:strip-space elements="*" />


<xsl:attribute-set name="XsltMapperVerificaUrn">
    <xsl:attribute name="color">
        <!--	xsl:value-of select="util:getTestUrn(@xlink:href)"/	-->
        <xsl:value-of select="util:getTestUrn(.)"/>
    </xsl:attribute>
</xsl:attribute-set>

<!--	non usato	
<xsl:attribute-set name="XsltMapperBaseURL">
    <xsl:attribute name="href">
        <xsl:value-of select="util:getBaseURL(.)"/>
    </xsl:attribute>
</xsl:attribute-set	-->


</xsl:transform>