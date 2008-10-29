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
                xmlns       = "http://www.w3.org/HTML/1998/html4"
                xmlns:nir   = "http://www.normeinrete.it/nir/2.2/"
                xmlns:cnr   = "http://www.cnr.it/provvedimenti/2.2"
                xmlns:mapper= "xalan://it.cnr.ittig.xmleges.core.blocks.panes.xsltmapper.XsltMapperImpl"
                version     = "1.0">

<xsl:include href="xsltmapper-1.0.xsl"/>
<xsl:strip-space elements="*" />


<xsl:template name="visualizzaMeta">
	<xsl:apply-templates select="/nir:NIR/*/nir:meta/nir:descrittori"  />
	<xsl:apply-templates select="/nir:NIR/*/nir:meta/nir:inquadramento"   />	
	<xsl:apply-templates select="/nir:NIR/*/nir:meta/nir:ciclodivita"   />	
	<xsl:apply-templates select="/nir:NIR/*/nir:meta/nir:lavoripreparatori"   />	
	<xsl:apply-templates select="/nir:NIR/*/nir:meta/nir:redazionale"   />	
	<xsl:apply-templates select="/nir:NIR/*/nir:meta/nir:proprietario"   />	
	<xsl:apply-templates select="/nir:NIR/*/nir:meta/nir:risoluzione"   />	
	<xsl:apply-templates select="/nir:NIR/*/nir:meta/nir:disposizioni"   />	
</xsl:template>

<xsl:template match="nir:redazionale"   />
<xsl:template match="nir:lavoripreparatori"  />
<xsl:template match="nir:disposizioni"  />

<xsl:template match="nir:descrittori" >
	<center><b><xsl:value-of select="name()"/></b></center>
	
	<xsl:apply-templates select="nir:urn" />
	<xsl:apply-templates select="nir:pubblicazione"  />
	<xsl:apply-templates select="nir:alias"  />	
	<xsl:apply-templates select="nir:redazione"  />
	<xsl:apply-templates select="nir:materie"  />
	<br/><hr/>
	
	<!--xsl:apply-templates select="../nir:proprietario"  /-->
	<xsl:for-each select="../nir:proprietario">
		<xsl:variable name="num">
			<xsl:value-of select="position()" />
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$num=1">
				<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
	   				<center><b><xsl:value-of select="name()"/>/i</b></center>
				</xsl:element>	    
			</xsl:when>
		</xsl:choose>
		<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		    <xsl:text> Soggetto: </xsl:text>
			<font color="blue">		    	
			    <xsl:value-of select="@soggetto"/>
			    <xsl:text>, </xsl:text>
			    <xsl:value-of select="@xlink:href"/>
			</font>
		</xsl:element>
		<xsl:choose>
			<xsl:when test="$num=last()">
				<br/><hr/>    
			</xsl:when>
		</xsl:choose>
	</xsl:for-each>
</xsl:template>

<xsl:template match="nir:materia"   >
    <xsl:value-of select="@valore"/>
    <xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="nir:pubblicazione"   >
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

<xsl:template match="nir:urn">
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
				<font color="blue"><xsl:value-of select="@valore"/></font>
			</xsl:element>
</xsl:template>

<xsl:template match="nir:alias">
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
				<font color="blue"><xsl:value-of select="@valore"/></font>
			</xsl:element>
</xsl:template>

<xsl:template match="nir:materie">
		<xsl:variable name="num">
			<xsl:value-of select="position()" />
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$num=1">
				<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
	   				<br/><center><b><xsl:value-of select="name()"/></b></center>
				</xsl:element>	    
			</xsl:when>
		</xsl:choose>
		<br/><xsl:value-of select="@vocabolario"/>
		<xsl:text>: </xsl:text>
		<font color="blue">
			<xsl:apply-templates select="nir:materia"  />
		</font>	
</xsl:template>


<xsl:template match="nir:risoluzione"   >
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		<center><b><xsl:value-of select="name()"/></b></center>
		<font color="blue">
		    <xsl:text>URL: </xsl:text>
		    <xsl:value-of select="@url"/>
		    <xsl:text>, URN: </xsl:text>
		    <xsl:value-of select="@urn"/>
		</font>
	</xsl:element>
</xsl:template>

<xsl:template match="nir:redazione"   >
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

<xsl:template match="nir:ciclodivita"  >
	<center><b><xsl:value-of select="name()"/></b></center>
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		<xsl:apply-templates  />
	</xsl:element>
	<hr/>
</xsl:template>

<xsl:template match="nir:eventi"   >
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
	   	<xsl:attribute name="style">
	            margin: 30 15 15 25;
	            color: red;
	    </xsl:attribute>
	    <xsl:value-of select="name()"/>
	</xsl:element>	
	<xsl:apply-templates select="nir:evento" />    
</xsl:template>



<xsl:template match="nir:relazioni"   >
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
	   	<xsl:attribute name="style">
	            margin: 30 15 15 25;
	            color: red;
	    </xsl:attribute>
	    Relazioni <!--xsl:value-of select="name()"/-->
	</xsl:element>	    
	<xsl:apply-templates select="nir:originale" mode="other"/>
	<xsl:apply-templates select="nir:attiva" mode="other"/>
	<xsl:apply-templates select="nir:passiva" mode="other"/>
	<xsl:apply-templates select="nir:giurisprudenza" mode="other"/>
	<xsl:apply-templates select="nir:haallegato" mode="other"/>
	<xsl:apply-templates select="nir:allegatodi" mode="other"/>	      
</xsl:template>						

<xsl:template match="nir:evento"   >
		<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
			<xsl:variable name="fonte"><xsl:value-of select="@fonte"/></xsl:variable>
			<font color="blue">
		    	<xsl:value-of select="concat(substring(@data,7,2),'/',substring(@data,5,2),'/',substring(@data,1,4))"/> 
			    <xsl:text>, </xsl:text>
			    <xsl:value-of select="@tipo"/>
			    <xsl:text>, </xsl:text>		
			    <xsl:apply-templates select="//nir:relazioni/*[@id=$fonte]"  />  
			    	      
			</font>
		</xsl:element>
</xsl:template>	

<xsl:template match="nir:originale | nir:attiva | nir:passiva | nir:giurisprudenza | nir:haallegato | nir:allegatodi" > 
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
		<br/>
</xsl:template>

<xsl:template match="nir:originale | nir:attiva | nir:passiva | nir:giurisprudenza | nir:haallegato | nir:allegatodi" mode="other">
	<xsl:variable name="id"><xsl:value-of select="@id"/></xsl:variable>
	<xsl:if test="../../nir:eventi/nir:evento and @fonte=$id">
	</xsl:if>
	<font color="green"> 
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
		<br/>
</xsl:template>
				
						
<xsl:template match="nir:inquadramento"  >
	
	<center><b><xsl:value-of select="name()"/></b></center>
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">	    
		    <xsl:apply-templates select="nir:infodoc" />
		    <xsl:apply-templates select="nir:infomancanti" />
		    <xsl:apply-templates select="nir:oggetto" />
		    <xsl:apply-templates select="nir:proponenti" />
	</xsl:element>
	<br/><hr/>
</xsl:template>


<xsl:template match="nir:infodoc"   > 
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
			<xsl:attribute name="style">
           			margin: 30 15 15 25;
            		color: red;
			</xsl:attribute>
			<xsl:value-of select="name()"/>
			<br/>
		Fonte: <font color="blue"><xsl:value-of select="@fonte"/></font><br/>
		Funzione: <font color="blue"><xsl:value-of select="@funzione"/></font><br/>
		Natura: <font color="blue"><xsl:value-of select="@natura"/></font><br/>
		Normativa: <font color="blue"><xsl:value-of select="@normativa"/></font><br/>
		Registrazione: <font color="blue"><xsl:value-of select="@registrazione"/></font><br/>
	</xsl:element>
</xsl:template>


<!--xsl:template match="nir:infomancanti"   >
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		non fatto
	</xsl:element>
</xsl:template-->

<xsl:template match="nir:oggetto | nir:proponenti | nir:infomancanti"   >
			
	<xsl:for-each select="*">
		<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		<xsl:attribute name="style">
           			margin: 30 15 15 25;
            		color: red;
			</xsl:attribute>
			<xsl:value-of select="name()"/>: <font color="blue"><xsl:value-of select="@valore"/></font>
			<br/>
		</xsl:element>	    
	</xsl:for-each>
</xsl:template>



<xsl:template match="cnr:meta"  >
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
	   	<xsl:attribute name="style">
	            margin: 30 15 15 25;
	            color: red;
	    </xsl:attribute>
	    (CNR) meta
	</xsl:element>	    
	<xsl:for-each select="*">
		<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		    <xsl:value-of select="substring(name(),5)"/>
		    <xsl:text>: </xsl:text>
			<font color="blue"><xsl:value-of select="@valore"/></font>
		</xsl:element>		
	</xsl:for-each>
</xsl:template>

</xsl:transform>
