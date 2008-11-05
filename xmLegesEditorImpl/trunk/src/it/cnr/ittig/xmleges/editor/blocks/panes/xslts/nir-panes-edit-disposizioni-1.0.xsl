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
	        <xsl:choose>
				<xsl:when test="//*[name()='mod']">
		        	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
	   					<xsl:attribute name="style">
	   						text-align: center;
	            			margin: 30 15 15 25;
		            		color: blue;
					    </xsl:attribute>
					    <xsl:text> Disposizioni attive </xsl:text>
					</xsl:element>
					<br/>
					<xsl:for-each select="//*[name()='mod']">
						<xsl:apply-templates select="./.." />
					</xsl:for-each>			
				</xsl:when>	
				<xsl:otherwise>
					<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
	   					<xsl:attribute name="style">
	   						text-align: center;
	            			margin: 30 15 15 25;
		            		color: blue;
					    </xsl:attribute>
					    <xsl:text> Non ci sono disposizioni attive </xsl:text>
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>   
			<br/><br/><br/><hr/><br/><br/>

			<!--	mettere anche le passive	-->

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
			<div><font color="Green">Non sono disponibili informazioni nei metadati</font></div><br/>
		</xsl:otherwise>
	</xsl:choose>		
	<xsl:call-template name="modclassico" />
	<br/><br/><hr width="30%" align="center"/>
</xsl:template> 

<xsl:template match="*[name()='dsp:abrogazione' or name()='dsp:sostituzione' or name()='dsp:integrazione']">
	<div>
	  <font color="Red">disposizione: </font>
	  <font color="Black">tipo= </font>
	  <font color="Blue"><xsl:value-of select="local-name()"/></font>
	  <font color="Black"> ; identificativo= </font>
	  <font color="Blue"><xsl:value-of select="*[name()='dsp:pos']/@xlink:href"/></font>
	  <br/>
	  <font color="Red">decorrenza: </font>
	  <xsl:variable name="idDecorrenzaConPound">
	  	<xsl:value-of select="*[name()='dsp:termine']/@da"/>
	  </xsl:variable>
	  <xsl:variable name="idDecorrenza">
	  	<xsl:value-of select="substring($idDecorrenzaConPound,2)"/>
	  </xsl:variable>
	  <xsl:variable name="dataDecorrenza">
	  	<xsl:value-of select="id($idDecorrenza)/@data"/>
	  </xsl:variable>
	  <font color="Blue"><xsl:value-of select="concat(substring($dataDecorrenza,7,2),'/',substring($dataDecorrenza,5,2),'/',substring($dataDecorrenza,1,4))"/></font>
	  <br/>
	  <font color="Red">norma: </font>
	  <font color="Blue"><xsl:value-of select="*[name()='dsp:norma']/@xlink:href"/></font>
	  <xsl:variable name="posNorma">
	  	<xsl:value-of select="*[name()='dsp:norma']/*[name()='dsp:pos']/@xlink:href"/>
	  </xsl:variable>
	  <xsl:if test="*[name()='dsp:norma']/@xlink:href!=$posNorma">
			<font color="Black"> ; partizione: </font>
			<font color="Blue"><xsl:value-of select="substring-after($posNorma,'#')" /></font>
	  </xsl:if>
	  <br/>
	  <xsl:if test="*[name()='dsp:norma']/*[name()='dsp:subarg']">
			<xsl:apply-templates select="*[name()='dsp:norma']/*[name()='dsp:subarg']/*[name()='ittig:bordo']" mode="dispo"/><br/>
	  </xsl:if>
	  <xsl:if test="*[name()='dsp:posizione']">
			<font color="Red">posizione: </font>
			<xsl:apply-templates select="*[name()='dsp:posizione']/*[name()='dsp:pos']"  mode="dispo"/>
	  </xsl:if>
	  <xsl:if test="*[name()='dsp:novellando']">
	  		<font color="Red">novellando: </font>
			<font color="Black">tipo= </font>
		  	<font color="Blue"><xsl:value-of select="*[name()='dsp:novellando']/*[name()='dsp:subarg']/*[name()='ittig:tipo']/@valore"/></font>
  			<br/>
			<xsl:apply-templates select="*[name()='dsp:novellando']"  mode="dispo"/>
	  </xsl:if>	  
	  <xsl:if test="*[name()='dsp:novella']">
		    <font color="Red">novella: </font>
			<font color="Black">tipo= </font>
			<font color="Blue"><xsl:value-of select="*[name()='dsp:novella']/*[name()='dsp:subarg']/*[name()='ittig:tipo']/@valore"/></font>
			<font color="Black"> ; contenuto= </font>
			<xsl:variable name="idNovellaConPound">
			  	<xsl:value-of select="*[name()='dsp:novella']/*[name()='dsp:pos']/@xlink:href"/>
			</xsl:variable>
			<xsl:variable name="idNovella">
	  			<xsl:value-of select="substring($idNovellaConPound,2)"/>
	  		</xsl:variable>
			<xsl:variable name="testoNovella">
				<xsl:value-of select="id($idNovella)"/>
			</xsl:variable>
			<font color="Blue">
				<xsl:value-of select="$idNovella"/>
				<xsl:text> ( </xsl:text>
				<xsl:choose>
					<xsl:when test="string-length($testoNovella)>30">
						<xsl:value-of select="substring($testoNovella,1,30)"/><xsl:text> ...</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$testoNovella"/>
					</xsl:otherwise>
				</xsl:choose>	
				<xsl:text> )</xsl:text>
			</font>
	  </xsl:if>
	  <br/>
	</div>
	<xsl:call-template name="modclassico" />
</xsl:template> 


<xsl:template match="*[name()='dsp:posizione']/*[name()='dsp:pos']" mode="dispo">
	<font color="Black"> dove= </font>
	<font color="Blue"><xsl:value-of select="*[name()='ittig:dove']/@valore"/></font>
	<font color="Black"> ; parole= </font>
	<xsl:variable name="idPosizioneConPound">
		<xsl:value-of select="@xlink:href"/>
	</xsl:variable>
	<xsl:variable name="idPosizione">
		<xsl:value-of select="substring($idPosizioneConPound,2)"/>
	</xsl:variable>
	<xsl:variable name="testoPosizione">
		<xsl:value-of select="id($idPosizione)"/>
	</xsl:variable>
	<font color="Blue">
		<xsl:value-of select="idPosizione"/>
		<xsl:text> ( </xsl:text>
		<xsl:choose>
			<xsl:when test="string-length($testoPosizione)>30">
				<xsl:value-of select="substring($testoPosizione,1,30)"/><xsl:text> ...</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$testoPosizione"/>
			</xsl:otherwise>
		</xsl:choose>	
		<xsl:text> )</xsl:text>
	</font>
	<br/>
</xsl:template> 

<xsl:template match="*[name()='dsp:novellando']" mode="dispo">
	<xsl:variable name="idNovellandoConPound">
		<xsl:value-of select="*[name()='dsp:pos']/@xlink:href"/>
	</xsl:variable>
	<xsl:variable name="idNovellando">
		<xsl:value-of select="substring($idNovellandoConPound,2)"/>
	</xsl:variable>
	<xsl:variable name="testoNovellando">
		<xsl:value-of select="id($idNovellando)"/>
	</xsl:variable>
	<xsl:if test="*[name()='dsp:pos']/*[name()='ittig:ruolo']">
		<font color="Black"> - parole= </font>
		<font color="Blue">
			<xsl:value-of select="$idNovellando"/>
			<xsl:text> ( </xsl:text>
			<xsl:choose>
				<xsl:when test="string-length($testoNovellando)>30">
					<xsl:value-of select="substring($testoNovellando,1,30)"/><xsl:text> ...</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$testoNovellando"/>
				</xsl:otherwise>
			</xsl:choose>	
			<xsl:text> )</xsl:text>
		</font>
		<br/>
		<xsl:apply-templates select="*[name()='dsp:pos']/*[name()='ittig:ruolo']"  mode="dispo"/>
	</xsl:if>
</xsl:template> 

<xsl:template match="*[name()='ittig:ruolo']" mode="dispo">
	<font color="Black"> - dove: </font>
	<font color="Blue"><xsl:value-of select="@valore"/></font>
	<font color="Black"> ; parole: </font>	
	<xsl:variable name="idRuoloConPound">
		<xsl:value-of select="../@xlink:href"/>
	</xsl:variable>
	<xsl:variable name="idRuolo">
		<xsl:value-of select="substring($idRuoloConPound,2)"/>
	</xsl:variable>
	<xsl:variable name="testoRuolo">
		<xsl:value-of select="id($idRuolo)"/>
	</xsl:variable>
	<font color="Blue">
		<xsl:value-of select="$idRuolo"/>
		<xsl:text> ( </xsl:text>
		<xsl:choose>
			<xsl:when test="string-length($testoRuolo)>30">
				<xsl:value-of select="substring($testoRuolo,1,30)"/><xsl:text> ...</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$testoRuolo"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text> ) </xsl:text>
	</font>
	<br/>
</xsl:template> 

<xsl:template match="*[name()='ittig:bordo']" mode="dispo">
	<xsl:variable name="tipoBordo">
		<xsl:value-of select="@tipo"/>
	</xsl:variable>
	<font color="Black"> - porzione: tipo= </font>
	<font color="Blue"><xsl:value-of select="$tipoBordo"/></font>
	<!--	xsl:choose>
		<xsl:when test="$tipoBordo='alinea' | $tipoBordo='coda' | $tipoBordo='rubrica'">
			<font color="Black"> ;</font>
		</xsl:when>
		<xsl:otherwise	-->
			<font color="Black"> ; identificativo: </font>
			<font color="Blue"><xsl:value-of select="@num"/></font>
			<xsl:if test="@ordinale='si'">
				<xsl:text> (ordinale)</xsl:text>
			</xsl:if>
		<!--	/xsl:otherwise>
	</xsl:choose	-->
	<br/>
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