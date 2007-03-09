<?xml version="1.0" encoding="iso-8859-1"?>

<!--
============================================================================================
File di trasformazione per gli elementi di testo.dtd.

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

<!-- mettere elementi gestiti da questo file -->
<xsl:strip-space elements="h:*" />



<!-- ================================================================================ -->
<!-- ============================================================= ELEMENTI VUOTI === -->
<!-- ================================================================================ -->

<xsl:template match="h:br">
	<font color="blue" weight="bold">¶</font><br/>
</xsl:template>

<xsl:template match="h:hr">
	<hr/>
</xsl:template>

<xsl:template match="h:img">
    <!--    <xsl:element name="img" use-attribute-sets="XsltMapperSetClass" />  -->

    	&#160;
 		 <font color="blue">
   		 Immagine = "<xsl:value-of select="@src"/>"
   		 </font>
    	&#160;
        
</xsl:template>

<xsl:template match="h:input">
    <xsl:element name="input" use-attribute-sets="XsltMapperSetClass" />
</xsl:template>


<!-- ================================================================================ -->
<!-- ============================================================ ELEMENTI INLINE === -->
<!-- ================================================================================ -->

<xsl:template match="h:b">
    <xsl:element name="b" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="h:i">
    <xsl:element name="i" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="h:u">
    <xsl:element name="u" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="h:sub">
    <xsl:element name="sub" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="h:sup">
    <xsl:element name="sup" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="h:span">
         <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
                <xsl:call-template name="vigenza"/>	
<!--		    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />-->
<!--		        <xsl:apply-templates />-->
	    </xsl:element>
</xsl:template>	


<xsl:template match="h:a">
	<font color="blue"><u>
    	<xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
    		<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        	<xsl:apply-templates />
    	</xsl:element>
    </u></font>
</xsl:template>

<!-- ================================================================================ -->
<!-- ===================================================== ELEMENTI BLOCCO E FORM === -->
<!-- ================================================================================ -->

<xsl:template match="h:p">
    <xsl:element name="p" use-attribute-sets="XsltMapperSetClass">
	    <font color="blue" weight="bold">§</font>

    	<!--	xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates /	-->

        <xsl:call-template name="vigenza"/>	

    </xsl:element>
</xsl:template>

<xsl:template match="h:div">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="h:form">
    <xsl:element name="form" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>


<!-- ================================================================================ -->
<!-- ====================================================================== LISTE === -->
<!-- ================================================================================ -->

<xsl:template match="h:ul">
    <xsl:element name="ul" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="h:ol">
    <xsl:element name="ol" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="h:li">
    <xsl:element name="li" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="h:dl">
    <xsl:element name="dl" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="h:dt">
    <xsl:element name="dt" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="h:dd">
    <xsl:element name="dd" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>




<!-- ================================================================================ -->
<!-- ==================================================================== TABELLE === -->
<!-- ================================================================================ -->

<xsl:template match="h:table">
    <div align="center">
    <xsl:element name="table" use-attribute-sets="XsltMapperSetClass">
        <xsl:attribute name="width">95%</xsl:attribute>
<!--
 	    <xsl:if test="not(@border)">
            <xsl:attribute name="border">0</xsl:attribute>        
        </xsl:if>
 -->
 
        <xsl:attribute name="cellpadding">2</xsl:attribute>
        <xsl:attribute name="align">center</xsl:attribute>
        <xsl:apply-templates select="./h:caption" />
        <xsl:apply-templates select="./h:thead" />
        <xsl:apply-templates select="./h:tbody" />
        <xsl:apply-templates select="./h:tfoot" />
    </xsl:element>
    <br/>
    </div>
</xsl:template>

<xsl:template match="h:caption">
    <i>
    <xsl:element name="caption" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
	    <xsl:apply-templates />
    </xsl:element>
    </i>
</xsl:template>

<xsl:template match="h:thead">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="h:tfoot">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="h:tbody">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="h:colgroup">
    <xsl:element name="colgroup" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="h:col">
    <xsl:element name="col" use-attribute-sets="XsltMapperSetClass">    
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="h:tr">
    <xsl:element name="tr" use-attribute-sets="XsltMapperSetClass">
    	<xsl:if test="name(..) = 'h:tbody'">
        	<xsl:attribute name="bgcolor">gray</xsl:attribute>
    	</xsl:if>
    	<xsl:if test="name(..) = 'h:thead' or name(..) = 'h:tfoot'">
        	<xsl:attribute name="bgcolor">blue</xsl:attribute>
    	</xsl:if>
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="h:th">
    <xsl:element name="th" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="h:td">
    <xsl:element name="td" use-attribute-sets="XsltMapperSetClass">
	<xsl:attribute name="style">margin: 1</xsl:attribute>
	<xsl:attribute name="bgcolor">white</xsl:attribute>
	<xsl:if test="@align">
		<xsl:attribute name="align"><xsl:value-of select="@align"/></xsl:attribute>
	</xsl:if>
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

</xsl:transform>
