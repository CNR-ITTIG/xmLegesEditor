<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet version="1.0" xmlns:nir = "http://www.normeinrete.it/nir/2.2" 
										xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
										xmlns="http://www.w3.org/HTML/1998/html4" 
										xmlns:h="http://www.w3.org/HTML/1998/html4" 
										xmlns:xlink="http://www.w3.org/1999/xlink"
										xmlns:date="http://exslt.org/dates-and-times">
										
	<xsl:output method="html"  indent="yes"/>


	<xsl:template match="/">
		<estrazione>
			<xsl:for-each select="//*[name()='mod']">
				<br/>&lt;mod id="<xsl:value-of select="@id"/>"&gt;<br/>
					<xsl:apply-templates>
						<xsl:with-param name="usaId" select="@id"/>
					</xsl:apply-templates>
				<br/>&lt;/mod&gt;
			</xsl:for-each>
		</estrazione>	
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
		<xsl:text> RIF </xsl:text><xsl:value-of select="$usaId" /><xsl:text>-rif</xsl:text><xsl:value-of select="position()" /><xsl:text> </xsl:text>
	</xsl:template>
	
</xsl:stylesheet>
