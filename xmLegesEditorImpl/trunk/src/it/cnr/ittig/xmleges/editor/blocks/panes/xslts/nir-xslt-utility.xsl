<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet version="1.0" xmlns:nir = "http://www.normeinrete.it/nir/2.2" 
										xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
										xmlns="http://www.w3.org/HTML/1998/html4" 
										xmlns:h="http://www.w3.org/HTML/1998/html4" 
										xmlns:xlink="http://www.w3.org/1999/xlink"
										xmlns:date="http://exslt.org/dates-and-times">
										
	<xsl:output method="html"  indent="yes"/>

	<!-- ======================================================== -->
	<!--  Forma testuale URN						              -->
	<!-- ======================================================== -->

 
	<!-- ======================================================== -->
	<!--  Calcola -1 giorno				              			  -->
	<!-- ======================================================== -->

	<xsl:template name="finevigenza">
		<xsl:param name="datafinevigenza" />
		<xsl:variable name="giornofinevigenza">
			<xsl:value-of select="substring($datafinevigenza,7,2)"/>
		</xsl:variable>
		<xsl:variable name="mesefinevigenza">
			<xsl:value-of select="substring($datafinevigenza,5,2)"/>
		</xsl:variable>
		<xsl:variable name="annofinevigenza">
			<xsl:value-of select="substring($datafinevigenza,1,4)"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$giornofinevigenza='01'">
				<xsl:choose>
					<xsl:when test="$mesefinevigenza='01'">
						<xsl:value-of select="concat('31','/','12','/',format-number(number(number($annofinevigenza)-1), '0000'))"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:variable name="mesevigenza">
							<xsl:value-of select="number(number($mesefinevigenza)-1)"/>
						</xsl:variable>
						<xsl:variable name="giornovigenza">
						    <xsl:choose>
						      <xsl:when test="$mesevigenza=1">31</xsl:when>
						      <xsl:when test="$mesevigenza=2">28</xsl:when>
						      <xsl:when test="$mesevigenza=3">31</xsl:when>
						      <xsl:when test="$mesevigenza=4">30</xsl:when>
						      <xsl:when test="$mesevigenza=5">31</xsl:when>
						      <xsl:when test="$mesevigenza=6">30</xsl:when>
						      <xsl:when test="$mesevigenza=7">31</xsl:when>
						      <xsl:when test="$mesevigenza=8">31</xsl:when>
						      <xsl:when test="$mesevigenza=9">30</xsl:when>
						      <xsl:when test="$mesevigenza=10">31</xsl:when>
						      <xsl:when test="$mesevigenza=11">30</xsl:when>
						      <xsl:when test="$mesevigenza=12">31</xsl:when>
						    </xsl:choose>					
						</xsl:variable>
						<xsl:value-of select="concat($giornovigenza,'/',format-number(number(number($mesefinevigenza)-1),'00'),'/',$annofinevigenza)"/>
					</xsl:otherwise>							
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat(format-number(number(number($giornofinevigenza)-1),'00'),'/',$mesefinevigenza,'/',$annofinevigenza)"/>
			</xsl:otherwise>							
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>