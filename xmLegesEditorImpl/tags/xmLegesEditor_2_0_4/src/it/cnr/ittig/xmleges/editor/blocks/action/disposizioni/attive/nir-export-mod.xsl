<?xml version="1.0" encoding="iso-8859-15"?> 
<xsl:stylesheet version="1.0" xmlns:nir = "http://www.normeinrete.it/nir/2.2" 
										xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
										xmlns="http://www.w3.org/HTML/1998/html4" 
										xmlns:h="http://www.w3.org/HTML/1998/html4" 
										xmlns:xlink="http://www.w3.org/1999/xlink"
										xmlns:date="http://exslt.org/dates-and-times">
										
	<xsl:output method="text" encoding="iso-8859-15" indent="yes"/>

	<xsl:param name="azione"/>		<!-- azione=tutti o nuovi o vecchi -->

	<xsl:template match="/">
			<xsl:for-each select="//*[name()='mod']">
				<!-- test se esite il metadato -->
				<xsl:variable name="cercoId">
					<xsl:value-of select="concat('#',@id)"/>
				</xsl:variable>	
				<xsl:choose>
					<xsl:when test="/*[name()='NIR']/*/*[name()='meta']/*[name()='disposizioni']/*[name()='modificheattive']/*/*[name()='dsp:pos'][@xlink:href=$cercoId]">
						<!-- mod con metadato -->
						<xsl:if test="$azione!='nuovi'">
							<!-- lo prendo -->
							<xsl:call-template name="modifica"/>							
						</xsl:if>		
					</xsl:when>
					<xsl:otherwise>
						<!-- mod senza metadato -->
						<xsl:if test="$azione!='vecchi'">
							<!-- lo prendo -->
							<xsl:call-template name="modifica"/>							
						</xsl:if>	
					</xsl:otherwise>							
				</xsl:choose>			
				<xsl:text> .
</xsl:text><!-- non portare sopra. c'è un newline che serve -->
			</xsl:for-each>
	</xsl:template> 
	
	<xsl:template name="modifica" >		
		<!--	xsl:text>&lt;mod id=&quot;</xsl:text>						Nessun tag x Ilc
		<xsl:value-of select="@id"/><xsl:text>&quot;&gt;</xsl:text	-->
		<xsl:apply-templates>
			<xsl:with-param name="usaId" select="@id"/>
		</xsl:apply-templates>
		<!--	xsl:text>&lt;/mod&gt;</xsl:text	-->
	</xsl:template>
	
	<xsl:template match="//*[name()='virgolette']">
		<xsl:text> VIR </xsl:text><xsl:value-of select="@id"/><xsl:text> </xsl:text>
	</xsl:template>
	
	<xsl:template match="//*[name()='mrif']">
		<xsl:param name="usaId"/>
		<xsl:apply-templates select="*[name()='rif']">
			<xsl:with-param name="usaId" select="$usaId"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="//*[name()='rif']">
		<xsl:param name="usaId"/>
		<xsl:text> RIF </xsl:text><xsl:value-of select="$usaId" /><xsl:text>-rif</xsl:text><xsl:value-of select="position()" />
		
		<xsl:if test="contains(@xlink:href,'#')">
			<xsl:text>#</xsl:text><xsl:value-of select="substring-after(@xlink:href,'#')"/> 
		</xsl:if>			

		<xsl:text> </xsl:text>
	</xsl:template>
	
</xsl:stylesheet>
