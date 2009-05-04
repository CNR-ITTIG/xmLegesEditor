<?xml version="1.0" encoding="UTF-8"?>
 
<!--
============================================================================================
File di trasformazione per la modifica del documento principale di un documento AKOMANTOSO

author       : Tommaso Agnoloni <agnoloni@ittig.cnr.it>
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
                xmlns:an    = "http://www.akomantoso.org/1.0"
                xmlns:mapper= "xalan://it.cnr.ittig.xmleges.core.blocks.panes.xsltmapper.XsltMapperImpl"
                version     = "1.0"
>

<xsl:output method="html" indent="yes"/>
<!--xsl:include href="nir-panes-dtd-norme-1.0.xsl"/-->
<!--xsl:include href="nir-panes-dtd-testo-1.0.xsl"/-->
<!--xsl:include href="nir-panes-dtd-globali-1.0.xsl"/-->
<xsl:include href="xsltmapper-1.0.xsl"/>
<xsl:strip-space elements="*" />

<xsl:param name="base"/>

<xsl:template match="/">
   <html>
   		<head>
		    <style type="text/css">
		        body { font-family: Arial; }
		    </style>
        </head>
        <base href="{$base}" />
        <body>
        	<xsl:apply-templates />	
        
        
        <!-- NIR
            <xsl:apply-templates select="/*[name()='NIR']/*/*[name()='meta']/*[name()='confronto']" />
            <xsl:apply-templates select="/*[name()='NIR']/*/*[name()='intestazione']" />
            <xsl:apply-templates select="/*[name()='NIR']/*/*[name()='formulainiziale']" />
            <xsl:apply-templates select="/*[name()='NIR']/*/*[name()='relazione']"/>
            <xsl:apply-templates select="/*[name()='NIR']/*/*[name()='articolato']|/*[name()='NIR']/*/*[name()='contenitore']|/*[name()='NIR']/*/*[name()='gerarchia']" />
            <xsl:apply-templates select="/*[name()='NIR']/*/*[name()='formulafinale']" />
            <xs:apply-templates select="/*[name()='NIR']/*/*[name()='conclusione']" />           
        --> 
        
        </body>
    </html>
</xsl:template>



<!-- ================================================================================ -->
<!-- =============================================================== INTESTAZIONE === -->
<!-- ================================================================================ -->
<xsl:template match="an:preface">
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
		<xsl:apply-templates />
	</xsl:element>
</xsl:template>


<xsl:template match="an:ActType | an:ActDate | an:ActNumber">
	<xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
		<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
		<xsl:apply-templates />
	</xsl:element>
</xsl:template>


<xsl:template match="an:ActTitle">
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		<xsl:attribute name="style">
			text-align: center;
			font-size: x-large;
			font-style: bold;
			margin: 15 0 15 0;
		</xsl:attribute>
		<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
		<xsl:apply-templates />	
	</xsl:element>
</xsl:template>




<xsl:template match="an:body">
	<xsl:element name="div">
    	<xsl:attribute name="style">text-align: center;margin-top: 40 0 5 0;</xsl:attribute>
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)"/>
    	<xsl:apply-templates />
    </xsl:element>
</xsl:template>


<xsl:template match="an:num">
	<b>
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)"/>
        <xsl:apply-templates/>
    </b>
</xsl:template>


<xsl:template match="an:heading | an:title ">
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
	    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
	    	<xsl:apply-templates />
    </xsl:element>
</xsl:template>


<xsl:template match="an:section | an:part | an:paragraph | an:chapter | an:book | an:tome | an:article">
	<xsl:element name="div" >
    	<xsl:attribute name="style">
    		font-size: x-large;
    		margin: 30 0 5 0;
        </xsl:attribute>
	  	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
    	<xsl:apply-templates />
    </xsl:element>
</xsl:template>


<xsl:template match="an:clause | an:subsection | an:subpart | an:subparagraph | an:subchapter | an:subtitle | an:subclause">
	<xsl:element name="div">
    	<xsl:attribute name="style">
    	    font-size: medium;
            text-align: left;
            margin: 10 10 10 10;
        </xsl:attribute>
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
	   	<xsl:apply-templates />
    </xsl:element>
</xsl:template>


<xsl:template match="an:item">
	<xsl:attribute name="style">
    	    font-size: medium;
            text-align: left;
            margin: 10 10 10 10;
    </xsl:attribute>
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
    	<xsl:apply-templates />
    </xsl:element>
</xsl:template>


<xsl:template match="an:content">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
    	<xsl:apply-templates />
</xsl:template> 


<xsl:template match="an:mod">
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
	<font bgcolor="#FFDDAA">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
	</font>
    </xsl:element>
</xsl:template> 


<xsl:template match="an:quotedText">
			<i>
			<xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
		    	<font bgcolor="#FFEE99">
		    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
	    		<xsl:apply-templates />
	    		</font>
		    	</xsl:element>
			</i>
</xsl:template>


<xsl:template match="an:quotedStructure">
			<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		    	<xsl:attribute name="style">
		            margin: 5 25 5 25;
		            font-style: italic;
		        </xsl:attribute>
		    	<font bgcolor="#FFEE99">
		    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
	    		<xsl:apply-templates />
	    		</font>
	    	</xsl:element>
</xsl:template>

<xsl:template match="an:ref">
	<font color="blue"><u>
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
    </u></font>
</xsl:template> 


<!-- ================================================================================ -->
<!-- =============================================================== TESTO        === -->
<!-- ================================================================================ -->


<!-- qui sarebbe corretto mettere un p al posto dello span ma ci sono gia' troppi ritorni a capo (eol) -->
<xsl:template match="an:p">
    <xsl:element name="p" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template> 

<xsl:template match="an:eol">
    <xsl:element name="br">
        <xsl:apply-templates />
    </xsl:element>
</xsl:template> 



</xsl:transform>
