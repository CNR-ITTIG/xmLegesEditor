<?xml version="1.0" encoding="iso-8859-1"?>

<!--
============================================================================================
File di trasformazione per la modifica dell'articolato di un documento NIR.

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
 			    xmlns:mapper= "xalan://it.cnr.ittig.xmleges.core.blocks.panes.xsltmapper.XsltMapperImpl"
                version     = "1.0"
>

<xsl:output method="html" 
            omit-xml-declaration="yes"
            encoding="ISO-8859-15"
            indent="yes"/>

<xsl:include href="xsltmapper-1.0.xsl"/>

<xsl:strip-space elements="*" />

<xsl:template match="/">
	<html>
		<head>
		    <style type="text/css">
		        body { font-family: Arial; }
		    </style>
        </head>
		<body>
			<xsl:for-each select="//@iniziovigore">	
				<xsl:apply-templates select=".." />
			</xsl:for-each>
		</body>
	</html>
</xsl:template>

<xsl:template match="//*">
   		<xsl:variable name="idnota">
			<xsl:value-of select="@id" />
		</xsl:variable>
		<xsl:variable name="ittignota">
				<xsl:value-of select="/*[name()='NIR']/*/*[name()='meta']/*[name()='disposizioni']/*[name()='modifichepassive']/*/*/*[name()='dsp:pos'][@xlink:href=$idnota]/../../*[name()='dsp:norma']/*[name()='ittig:notavigenza']/@id"/>
		</xsl:variable>	
		<xsl:variable name="autonota">
				<xsl:value-of select="/*[name()='NIR']/*/*[name()='meta']/*[name()='disposizioni']/*[name()='modifichepassive']/*/*/*[name()='dsp:pos'][@xlink:href=$idnota]/../../*[name()='dsp:norma']/*[name()='ittig:notavigenza']/@auto"/>
		</xsl:variable>	
		<xsl:variable name="novella">
				<xsl:value-of select="/*[name()='NIR']/*/*[name()='meta']/*[name()='disposizioni']/*[name()='modifichepassive']/*/*/*[name()='dsp:pos'][@xlink:href=$idnota]/../../*[name()='dsp:novella']/*[name()='dsp:pos']/@xlink:href"/>
		</xsl:variable>	
		<xsl:variable name="novellando">
			<xsl:value-of select="/*[name()='NIR']/*/*[name()='meta']/*[name()='disposizioni']/*[name()='modifichepassive']/*/*/*[name()='dsp:pos'][@xlink:href=$idnota]/../../*[name()='dsp:novellando']/*[name()='dsp:pos']/@xlink:href"/>
		</xsl:variable>	

					<xsl:variable name="inizio_id">
						<xsl:value-of select="@iniziovigore"/>
					</xsl:variable>
					<xsl:variable name="fine_id">
						<xsl:value-of select="@finevigore"/>
					</xsl:variable>		
					<xsl:variable name="data_inizio">
						<xsl:value-of select="id($inizio_id)/@data"/>
					</xsl:variable>
					<xsl:variable name="data_fine">
						<xsl:value-of select="id($fine_id)/@data"/>
					</xsl:variable>

		<xsl:if test="$ittignota">
			<div>
	    		<xsl:attribute name="style">
	            	margin: 10 5 10 5;
		        </xsl:attribute>
	        
		        <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
					<font color="blue">
					[<xsl:value-of select="substring($ittignota,4,number(string-length($ittignota)))"/>
					<xsl:if test="$novellando">
						<xsl:if test="$novella">
							<!--	sostituzione	-->
							<xsl:if test="$novella=$idnota">i</xsl:if>
		   					<xsl:if test="$novellando=$idnota">e</xsl:if>
	   					</xsl:if>
   					</xsl:if>]
   					</font>
				</xsl:element>					
				<xsl:text> </xsl:text>
											
					<xsl:choose>
							<xsl:when test="$novellando">
							<xsl:choose>
								<xsl:when test="$novella">
									<!--	sostituzione	-->
									Sostituzione
		   						</xsl:when>
		   						<xsl:otherwise>
   									<!--	abrogazione		-->
									Abrogazione
								</xsl:otherwise>			
							</xsl:choose>
   						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when test="$novella">
									<!--	integrazione	-->
									Integrazione
   								</xsl:when>
							</xsl:choose>
						</xsl:otherwise>			   				
					</xsl:choose>			
					<xsl:if test="$novellando">
						<xsl:if test="$novella">
							<!--	sostituzione	-->
							<xsl:if test="$novella=$idnota"> (testo inserito)</xsl:if>
		   					<xsl:if test="$novellando=$idnota"> (testo eliminato)</xsl:if>
	   					</xsl:if>
   					</xsl:if>
					<xsl:text> da: </xsl:text>
					<xsl:value-of select="$autonota"/> 	
					<xsl:text>. </xsl:text>				
					<xsl:choose>
					<!-- ================= data_fine!='' =========-->
						<xsl:when test="$data_fine!=''">
								In vigore
						 		<xsl:choose>
									<xsl:when test="$data_inizio!=''">
										dal <xsl:value-of select="concat(substring($data_inizio,7,2),'/',substring($data_inizio,5,2),'/',substring($data_inizio,1,4))"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>fino</xsl:text>
									</xsl:otherwise>
								</xsl:choose>	
								al <xsl:value-of select="concat(substring($data_fine,7,2),'/',substring($data_fine,5,2),'/',substring($data_fine,1,4))"/>
								
								<!--	xsl:choose>			
									<xsl:when test="$stato!=''">
										(<xsl:value-of select="$stato"/>)
									</xsl:when>
								</xsl:choose	-->		
								
						</xsl:when>
						<!-- ================= data_inizio!='' =========-->
						<xsl:when test="$data_inizio!=''">
								&#160;In vigore	dal <xsl:value-of select="concat(substring($data_inizio,7,2),'/',substring($data_inizio,5,2),'/',substring($data_inizio,1,4))"/>
						</xsl:when>
					</xsl:choose>
				
				
	    	</div>
		</xsl:if>
</xsl:template>

</xsl:transform>