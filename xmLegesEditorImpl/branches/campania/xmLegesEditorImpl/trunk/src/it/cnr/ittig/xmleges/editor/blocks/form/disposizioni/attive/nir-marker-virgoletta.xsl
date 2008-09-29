<?xml version="1.0" encoding="iso-8859-15"?> 
<xsl:stylesheet version="1.0" xmlns:nir = "http://www.normeinrete.it/nir/2.2" 
										xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
										xmlns="http://www.w3.org/HTML/1998/html4" 
										xmlns:h="http://www.w3.org/HTML/1998/html4" 
										xmlns:xlink="http://www.w3.org/1999/xlink"
										xmlns:date="http://exslt.org/dates-and-times">
										
	<xsl:output method="html" indent="yes"/>

	<xsl:param name="idvir"/>		<!-- id virgoletta -->

	<xsl:template match="/">
			<xsl:apply-templates select="//*[name()='virgolette']"/>								
	</xsl:template> 

	<xsl:template match="//*[name()='virgolette']">
		<xsl:if test="@id=$idvir">
			<xsl:value-of select="."/>
			<xsl:apply-templates/>
		</xsl:if>			
	</xsl:template>	

	<xsl:template match="@*"/>
	
	<xsl:template match="*">
		<xsl:value-of select="."/>
		<xsl:apply-templates/>
	</xsl:template>	

	
</xsl:stylesheet>
