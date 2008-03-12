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
                xmlns:nir   = "http://www.normeinrete.it/nir/2.2"
                xmlns:cnr   = "http://www.cnr.it/provvedimenti/2.1"
                xmlns:mapper= "xalan://it.cnr.ittig.xmleges.core.blocks.panes.xsltmapper.XsltMapperImpl"
                version     = "1.0"
>

<xsl:output method="html" />
<xsl:include href="xsltmapper-1.0.xsl"/>
<xsl:strip-space elements="*" />


<xsl:template name="visualizzaMeta">
	<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='meta']/*[name()='descrittori']" mode="oneroot" />
	<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='meta']/*[name()='inquadramento']" mode="oneroot"  />	
	<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='meta']/*[name()='ciclodivita']" mode="oneroot"  />	
	<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='meta']/*[name()='lavoripreparatori']" mode="oneroot"  />	
	<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='meta']/*[name()='redazionale']" mode="oneroot"  />	
	<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='meta']/*[name()='proprietario']" mode="oneroot"  />	
	<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='meta']/*[name()='risoluzione']" mode="oneroot"  />	
	<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='meta']/*[name()='disposizioni']" mode="oneroot"  />	
</xsl:template>

<xsl:template match="*[name()='redazionale']"  mode="oneroot" />
<xsl:template match="*[name()='lavoripreparatori']" mode="oneroot" />
<xsl:template match="*[name()='disposizioni']" mode="oneroot" />

<xsl:template match="*[name()='descrittori']" mode="oneroot">
	<center><b><xsl:value-of select="name()"/></b></center>
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		<xsl:for-each select="*[name()='urn']">
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
		</xsl:for-each>
		<xsl:apply-templates select="*[name()='pubblicazione']" mode="oneroot" />
		
<!--	RIMOSSO DALLA DTD 2.2		
		<xsl:apply-templates select="*[name()='altrepubblicazioni']" />
-->
		
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
				<font color="blue"><xsl:value-of select="@valore"/></font>
			</xsl:element>
		</xsl:for-each>
	</xsl:element>
	<xsl:apply-templates select="*[name()='redazione']" mode="oneroot" />


	<xsl:for-each select="*[name()='materie']">
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
			<xsl:apply-templates select="*[name()='materia']" mode="oneroot" />
		</font>	
	</xsl:for-each>

	<br/><hr/>
	
	<xsl:for-each select="../*[name()='proprietario']">
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

<xsl:template match="*[name()='materia']"  mode="oneroot" >
    <xsl:value-of select="@valore"/>
    <xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="*[name()='pubblicazione']"  mode="oneroot" >
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

<!--	Rimosso dalla Dtd 2.2
	<xsl:template match="*[name()='altrepubblicazioni']" >
		<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
	   		<xsl:attribute name="style">
	        	    margin: 30 15 15 25;
	            	color: red;
		    </xsl:attribute>
		    <xsl:value-of select="name()"/>
		</xsl:element>	    
		<xsl:apply-templates mode="oneroot" />
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
fine rimosso dalla dtd 2.2-->

<xsl:template match="*[name()='risoluzione']"  mode="oneroot" >
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

<xsl:template match="*[name()='redazione']"  mode="oneroot" >
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

<xsl:template match="*[name()='ciclodivita']" mode="oneroot" >
	<center><b><xsl:value-of select="name()"/></b></center>
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		<xsl:apply-templates mode="oneroot" />
	</xsl:element>
	<hr/>
</xsl:template>

<xsl:template match="*[name()='eventi']"  mode="oneroot" >
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

<xsl:template match="*[name()='relazioni']"  mode="oneroot" >
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

<xsl:template match="*[name()='inquadramento']" mode="oneroot" >
	<center><b><xsl:value-of select="name()"/></b></center>
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		<xsl:for-each select="*">
			<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
				<xsl:attribute name="style">
           			margin: 30 15 15 25;
            		color: red;
			    </xsl:attribute>
			    <xsl:value-of select="name()"/>
			    <br/>
			</xsl:element>	    
		    <xsl:apply-templates select="."/>
		</xsl:for-each>
	</xsl:element>
	<br/><hr/>
</xsl:template>

<xsl:template match="*[name()='infodoc']"  mode="oneroot" >
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		Fonte: <font color="blue"><xsl:value-of select="@fonte"/></font><br/>
		Funzione: <font color="blue"><xsl:value-of select="@funzione"/></font><br/>
		Natura: <font color="blue"><xsl:value-of select="@natura"/></font><br/>
		Normativa: <font color="blue"><xsl:value-of select="@normativa"/></font><br/>
		Registrazione: <font color="blue"><xsl:value-of select="@registrazione"/></font><br/>
	</xsl:element>
</xsl:template>


<xsl:template match="*[name()='infomancanticccccc']"  mode="oneroot" >
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		non fatto
	</xsl:element>
</xsl:template>

<xsl:template match="*[name()='oggetto'] | *[name()='proponenti'] | *[name()='infomancanti']"  mode="oneroot" >
	<xsl:for-each select="*">
		<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
			<xsl:value-of select="name()"/>: <font color="blue"><xsl:value-of select="@valore"/></font>
		</xsl:element>	    
	</xsl:for-each>
</xsl:template>



<xsl:template match="*[name()='cnr:meta']" mode="oneroot" >
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
