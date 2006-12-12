<?xml version="1.0" encoding="UTF-8"?>

<!--
============================================================================================
File di trasformazione per gli elementi di meta.dtd.

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
                xmlns:cnr   = "http://www.cnr.it/provvedimenti/2.1"
                xmlns:mapper= "xalan://it.cnr.ittig.xmleges.core.blocks.panes.xsltmapper.XsltMapperImpl"
                version     = "1.0"
>

<xsl:output method="html" />
<xsl:include href="xsltmapper-1.0.xsl"/>
<xsl:strip-space elements="*" />


<xsl:template match="*[name()='redazionale']" />

<xsl:template match="*[
	name()='descrittori' or name()='lavoripreparatori' or
	name()='proprietario' or name()='disposizioni' or name()='ciclodivita']">
	<center><font size="+2"><b><xsl:value-of select="name()"/></b></font></center>
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
		<xsl:apply-templates />
	</xsl:element>
	<hr/>
</xsl:template>

<xsl:template match="*[name()='descrittori']">
	<center><font size="+2"><b><xsl:value-of select="name()"/></b></font></center>
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
		<xsl:apply-templates select="*[name()='pubblicazione']" />
		<xsl:apply-templates select="*[name()='altrepubblicazioni']" />
		<xsl:for-each select="*[name()='alias']">
			<xsl:variable name="num">
				<xsl:value-of select="position()" />
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="$num=1">
					<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
	   					<xsl:attribute name="style">
	            			margin: 30 15 15 25;
		            		color: red;
					    </xsl:attribute>
					    <xsl:value-of select="name()"/>
					</xsl:element>	    
				</xsl:when>
			</xsl:choose>
			<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
				<font color="blue"><xsl:value-of select="@value"/></font>
			</xsl:element>
		</xsl:for-each>
	</xsl:element>
	<xsl:apply-templates select="*[name()='redazione']" />
	<br/><hr/>
</xsl:template>

<xsl:template match="*[name()='pubblicazione']" >
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
	   	<xsl:attribute name="style">
	            margin: 30 15 15 25;
	            color: red;
	    </xsl:attribute>
	    <xsl:value-of select="name()"/>
	</xsl:element>	    
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		Tipo: <font color="blue"><xsl:value-of select="@tipo"/></font>
		Numero: <font color="blue"><xsl:value-of select="@num"/></font>
		Data: <font color="blue"><xsl:value-of select="concat(substring(@norm,7,2),'/',substring(@norm,5,2),'/',substring(@norm,1,4))"/></font>		
	</xsl:element>
</xsl:template>

<xsl:template match="*[name()='altrepubblicazioni']" >
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
	   	<xsl:attribute name="style">
	            margin: 30 15 15 25;
	            color: red;
	    </xsl:attribute>
	    <xsl:value-of select="name()"/>
	</xsl:element>	    
	<xsl:apply-templates />
</xsl:template>

<xsl:template match="*[name()='altrepubblicazioni']/*" >
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		<font color="blue">
		    <xsl:value-of select="name()"/>
		    <xsl:text>, N. </xsl:text>
		    <xsl:value-of select="@num"/>
		    <xsl:text>, </xsl:text>
		    <xsl:value-of select="@tipo"/>
		    <xsl:text>, </xsl:text>		    
		    <xsl:value-of select="concat(substring(@norm,7,2),'/',substring(@norm,5,2),'/',substring(@norm,1,4))"/> 
		</font>
	</xsl:element>
</xsl:template>

<xsl:template match="*[name()='redazione']" >
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
	   	<xsl:attribute name="style">
	            margin: 30 15 15 25;
	            color: red;
	    </xsl:attribute>
	    <xsl:value-of select="name()"/>
	</xsl:element>	    
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		Data: <font color="blue"><xsl:value-of select="concat(substring(@norm,7,2),'/',substring(@norm,5,2),'/',substring(@norm,1,4))"/></font>		
		Nome: <font color="blue"><xsl:value-of select="@nome"/></font>
		Url: <font color="blue"><xsl:value-of select="@url"/></font>
		Contributo: <font color="blue"><xsl:value-of select="@contributo"/></font>
	</xsl:element>
</xsl:template>

<xsl:template match="*[name()='eventi']" >
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
	   	<xsl:attribute name="style">
	            margin: 30 15 15 25;
	            color: red;
	    </xsl:attribute>
	    <xsl:value-of select="name()"/>
	</xsl:element>	    
	<xsl:for-each select="*[name()='evento']">
		<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
			<xsl:variable name="fonte"><xsl:value-of select="@fonte"/></xsl:variable>
			<font color="blue">
		    	<xsl:value-of select="concat(substring(@data,7,2),'/',substring(@data,5,2),'/',substring(@data,1,4))"/> 
			    <xsl:text>, </xsl:text>
			    <xsl:value-of select="@tipo"/>
			    <xsl:text>, </xsl:text>		  
			    <xsl:for-each select="//*[name()='relazioni']/*[@id=$fonte]">
				    <xsl:value-of select="name()"/>
			        <xsl:text>, </xsl:text>
				    <xsl:value-of select="@xlink:href"/>			        
				    <xsl:if test="@effetto!=''">
				    	<xsl:text>, </xsl:text>
						<xsl:value-of select="@effetto"/>
					</xsl:if>
					<xsl:if test="@tipo!=''">
					    <xsl:text>, </xsl:text>
						<xsl:value-of select="@tipo"/>
					</xsl:if>
				</xsl:for-each>			      
			</font>
		</xsl:element>
	</xsl:for-each>
</xsl:template>

<xsl:template match="*[name()='relazioni']" >
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
	   	<xsl:attribute name="style">
	            margin: 30 15 15 25;
	            color: red;
	    </xsl:attribute>
	    Altre <xsl:value-of select="name()"/>
	</xsl:element>	    
	<xsl:for-each select="*">
		<xsl:variable name="id"><xsl:value-of select="@id"/></xsl:variable>
		<xsl:choose>
			<xsl:when test="../../*[name()='eventi']/*[name()='evento' and @fonte=$id]">
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
					<font color="blue">
					    <xsl:value-of select="name()"/>
				        <xsl:text>, </xsl:text>
			    		<xsl:value-of select="@xlink:href"/>			        
			    		<xsl:if test="@effetto!=''">
	   						<xsl:text>, </xsl:text>
							<xsl:value-of select="@effetto"/>
						</xsl:if>
						<xsl:if test="@tipo!=''">
						    <xsl:text>, </xsl:text>
							<xsl:value-of select="@tipo"/>
						</xsl:if>
					</font>
				</xsl:element>							
			</xsl:otherwise>
		</xsl:choose>
	</xsl:for-each>			      
</xsl:template>						





<xsl:template match="*[name()='cnr:meta']">
	<center>
	  <font size="+2">
		<b>(CNR) meta</b>
	  </font>
	</center>
	
	 
	<!--
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
		<xsl:apply-templates />
	</xsl:element>
	-->
	
	<!-- modificato causa SAXException (Can't have more than one root on a DOM!) -->
	
	<xsl:element name="div">
		<xsl:apply-templates/>
	</xsl:element>
	
	
	<hr/>
</xsl:template>



<xsl:template match="node()">

    <xsl:variable name ="nodename">
      <xsl:value-of select="name()"/>
    </xsl:variable>
    
    <!-- gestione delle etichette con prefisso cnr -->
    <xsl:choose>
      <xsl:when test ="substring($nodename,1,3)='cnr'">
        <b><xsl:value-of select="substring($nodename,5)"/></b> 
      </xsl:when>
      <xsl:otherwise>
        <b><xsl:value-of select="name()"/></b>
      </xsl:otherwise> 
    </xsl:choose>
    <!--             fine etichette cnr            -->
    
	
	<!-- modificato causa SAXException (Can't have more than one root on a DOM!) non dovrebbe servire se i dati stanno negli attributi-->
	<xsl:element name="div"> <!-- use-attribute-sets="XsltMapperSetClass"--> 
		<!-- xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" /-->
		<xsl:apply-templates />
	</xsl:element>
	
</xsl:template>



</xsl:transform>
