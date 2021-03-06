<?xml version="1.0" encoding="UTF-8"?>

<!--
============================================================================================
Regola per trasformare correttamente i nodi testo del documento per essere interpretati 
da XsltPane.
Questo file dovrebbe essere incluso in ogni foglio di trasformazione che desidera modificare
i nodi testo del documento.

author       : Mirco Taddei <mirco.taddei@gmail.com>
version      : 1.0
copyright (C): 2003 - 2004
               Istituto di Teoria e Tecniche dell'Informazione Giuridica (ITTIG)
               Consiglio Nazionale delle Ricerche - Italy
               http://www.ittig.cnr.it
license      : GNU General Public License http://www.gnu.org/licenses/gpl.html
============================================================================================
-->

<xsl:transform  xmlns:xsl   = "http://www.w3.org/1999/XSL/Transform"
                xmlns:xlink = "http://www.w3.org/1999/xlink"
                xmlns:h     = "http://www.w3.org/HTML/1998/html4"
                xmlns:mapper= "xalan://it.cnr.ittig.xmleges.core.blocks.panes.xsltmapper.XsltMapperImpl"
                version     = "1.0"
>

<xsl:output method="html"/>
<xsl:strip-space elements="*" />

<!-- ====================================================================== -->
<!-- ======================================================= TEXT NODE ==== -->
<!-- ====================================================================== -->
<!-- Spaces before and after real element text are needed to uniquely identify
     the span element from cursor position. Without the preceding space there
     is no way to tell textpane to write on the element following caret (even 
     when the preceding element is not modifiable). Without the ending space 
     it's difficult to write at the end of the text. -->
<xsl:template match="text()"><xsl:element name="span" use-attribute-sets="XsltMapperSetClassNoStyle">&#160;<xsl:value-of select="." />&#160;</xsl:element></xsl:template>


<!-- ====================================================================== -->
<!-- =========================== COMMENT E PROCESSING INSTRUCTION NODE ==== -->
<!-- ====================================================================== -->
<xsl:template match="processing-instruction()">
	<font color="red">
	<xsl:element name="span" use-attribute-sets="XsltMapperSetClass">&#160;<xsl:if test="string-length(.) = 0"><xsl:value-of select="mapper:getTextStringIfEmpty(.)" /></xsl:if><xsl:if test="string-length(.) != 0"><xsl:value-of select="." /></xsl:if>&#160;</xsl:element>
	</font>
</xsl:template>

<xsl:template match="comment()">
	<font color="green">
	<xsl:element name="span" use-attribute-sets="XsltMapperSetClass">&#160;<xsl:if test="string-length(.) = 0"><xsl:value-of select="mapper:getTextStringIfEmpty(.)" /></xsl:if><xsl:if test="string-length(.) != 0"><xsl:value-of select="." /></xsl:if>&#160;</xsl:element>
	</font>
</xsl:template>


<!-- ====================================================================== -->
<!-- translate(name(.),':','_')"/>=========================================== XSLT MAPPER SET CLASS ==== -->
<!-- ====================================================================== -->
<xsl:attribute-set name="XsltMapperSetClass">

    <xsl:attribute name="class">
        <xsl:value-of select="translate(name(),':','_')"/>
    </xsl:attribute>

    <xsl:attribute name="id">
        <xsl:value-of select="mapper:getUniqueId(.)"/>
    </xsl:attribute>
	<xsl:attribute name="style">
   	    <xsl:choose>
    		<xsl:when test="@status='soppresso'">color:red;  text-decoration:line-through;</xsl:when>
    		<xsl:when test="@status='inserito'">color:green;</xsl:when>
    		<xsl:when test="@style!=''"><xsl:value-of select="@style"/></xsl:when>
    		<xsl:when test="@h:style!=''"><xsl:value-of select="@h:style"/></xsl:when>
    		<xsl:otherwise/>
    	</xsl:choose>
    </xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="XsltMapperSetClassNoStyle">
    <xsl:attribute name="id">
        <xsl:value-of select="mapper:getUniqueId(.)"/>
    </xsl:attribute>
</xsl:attribute-set>

</xsl:transform>
