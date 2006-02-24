<?xml version="1.0" encoding="UTF-8"?>

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
<xsl:strip-space elements="*" />



<!-- ================================================================================ -->
<!-- =============================================================== INTESTAZIONE === -->
<!-- ================================================================================ -->
<xsl:template match="*[name()='intestazione']">
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
		<xsl:apply-templates />
	</xsl:element>
</xsl:template>

<xsl:template match="*[name()='tipoDoc' or name()='dataDoc' or name()='numDoc']">
	<xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
		<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
		<xsl:apply-templates />
	</xsl:element>
</xsl:template>

<xsl:template match="*[name()='titoloDoc']">
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

<xsl:template match="*[name()='emanante']">
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
		<xsl:apply-templates />
	</xsl:element>
</xsl:template>



<!-- ================================================================================ -->
<!-- =========================================================== FORMULA INIZIALE === -->
<!-- ================================================================================ -->
<xsl:template match="*[name()='formulainiziale' or name()='preambolo']">
        <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
        	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
            <xsl:apply-templates />
        </xsl:element>
</xsl:template>



<!-- ================================================================================ -->
<!-- ============================================================= FORMULA FINALE === -->
<!-- ================================================================================ -->
<xsl:template match="*[name()='formulafinale']">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:attribute name="style">margin: 30 5 5 5;</xsl:attribute>
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>



<!-- ================================================================================ -->
<!-- ================================================================ DECORAZIONE === -->
<!-- ================================================================================ -->
<xsl:template match="*[name()='decorazione']">
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="*[name()='rango']">
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
    	<xsl:value-of select="@tipo"/>
    </xsl:element>
</xsl:template>



<!-- ================================================================================ -->
<!-- ================================================================= ARTICOLATO === -->
<!-- ================================================================================ -->
<xsl:template match="*[name()='articolato']">
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:attribute name="style">
    	    text-align: center;
    	    margin-top: 40 0 5 0;
        </xsl:attribute>
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
    	<xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="*[
	name()='libro' or name()='parte' or name()='titolo' or name()='capo' or
	name()='sezione' or name()='paragrafo' or name()='partizione' or name()='articolo'
	]">
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:attribute name="style">
    	    font-size: x-large;
            margin: 30 0 5 0;
	    <xsl:if test="@status='soppresso'">color:red;  text-decoration:line-through;</xsl:if>
	    <xsl:if test="@status='inserito'">color:green</xsl:if>
        </xsl:attribute>
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
    	<xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="*[name()='rubrica']">
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:attribute name="style">
    	    font-size: large;
            font-style: italic;
        </xsl:attribute>
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
    	<xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="*[name()='num']">
	<b>
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
    </b>
</xsl:template>

<xsl:template match="*[name()='comma' or name()='el' or name()='en' or name()='ep']">
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:attribute name="style">
    	    font-size: medium;
            text-align: left;
            margin: 10 10 10 10;
	    <xsl:if test="@status='soppresso'">color:red;  text-decoration:line-through;</xsl:if>
	    <xsl:if test="@status='inserito'">color:green</xsl:if>
        </xsl:attribute>
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
    	<xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="*[name()='corpo' or name()='alinea' or name()='coda']">
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template> 



<!-- ================================================================================ -->
<!-- ================================================================ CONCLUSIONE === -->
<!-- ================================================================================ -->
<xsl:template match="*[name()='conclusione']">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:attribute name="style">margin: 20 30;</xsl:attribute>
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="*[name()='sottoscrizioni']">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:attribute name="style">margin: 15 0 0 0;</xsl:attribute>
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="*[name()='sottoscrivente']">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:attribute name="style">margin: 5 0 0 0;</xsl:attribute>
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="*[name()='visto']">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:attribute name="style">font-style: italic; margin: 20 0</xsl:attribute>
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="*[name()='dataeluogo']">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:attribute name="style">font-style: italic;</xsl:attribute>
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>



<!-- ================================================================================ -->
<!-- ==================================================================== ANNESSI === -->
<!-- ================================================================================ -->
<xsl:template match="*[name()='annessi']">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="*[name()='elencoAnnessi']">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="*[name()='annesso']">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
        <xsl:apply-templates />
    </xsl:element>
	<p/>
	<hr width="80%" align="center"/>
	<p/>
</xsl:template>

<xsl:template match="*[name()='testata']">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:attribute name="style">
    		background: #EEEEFF;
    		margin: 10 0 10 0;
    	</xsl:attribute>
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="*[name()='denAnnesso' or name()='titAnnesso']">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:attribute name="align">right</xsl:attribute>
		<font size="+1"><b>
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
        </b></font>
    </xsl:element>
</xsl:template>

<xsl:template match="*[name()='preAnnesso']">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:attribute name="style">text-align: left; font-size: large; font-weight: normal;</xsl:attribute>
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
        <br/>
    </xsl:element>
</xsl:template>

<xsl:template match="*[name()='rifesterno']">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:attribute name="style">
    		text-align: center;
    		font-size: normal; 
    		font-weight: bold;
    		color:blue;
    		margin: 0 0 0 0;
    	</xsl:attribute>
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
    		riferimento esterno<br/>
    		<xsl:apply-templates />
        <br/>
    </xsl:element>
</xsl:template>



<!-- ================================================================================ -->
<!-- ======================== ELEMENTI INLINE E SPECIALI CON SEMENATICA RILEVANTE === -->
<!-- ================================================================================ -->
<xsl:template match="*[name()='mrif']">
	<u>
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
    </u>
</xsl:template> 

<xsl:template match="*[name()='rif']">
	<font color="blue"><u>
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
    </u></font>
</xsl:template> 

<xsl:template match="*[name()='mod']">
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
	<font bgcolor="#FFDDAA">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
	</font>
    </xsl:element>
</xsl:template> 

<xsl:template match="*[name()='virgolette']">
	<xsl:choose>
		<xsl:when test="@tipo='struttura'">
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
		</xsl:when>
		<xsl:otherwise>
			<i>
			<xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
		    	<font bgcolor="#FFEE99">
		    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
	    		<xsl:apply-templates />
	    		</font>
		    	</xsl:element>
			</i>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="*[
	name()='def' or name()='atto' or name()='soggetto' or name()='ente' or
	name()='data' or name()='luogo' or name()='valute' or name()='importo'
	]">
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
    	<xsl:attribute name="style">color:green;</xsl:attribute>
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template> 



<!-- ================================================================================ -->
<!-- =========================================================== DISEGNI DI LEGGE === -->
<!-- ================================================================================ -->

<xsl:template match="*[name()='legislatura']">
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="*[
		name()='relazione' or name()='confronto' or 
		name()='sinistra' or name()='destra'
		]">
        <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
        	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
            <xsl:apply-templates />
        </xsl:element>
</xsl:template>

</xsl:transform>