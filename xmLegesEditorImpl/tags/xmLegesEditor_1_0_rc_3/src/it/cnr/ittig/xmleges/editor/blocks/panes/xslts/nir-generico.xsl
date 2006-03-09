<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"  xmlns:nir = "http://www.normeinrete.it/nir/1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  xmlns="http://www.w3.org/HTML/1998/html4" xmlns:h="http://www.w3.org/HTML/1998/html4" xmlns:xlink="http://www.w3.org/1999/xlink">
	<xsl:output method="html"  indent="yes"/>
	<!-- ======================================================== -->
	<!--                                                          -->
	<!--  Template principale                                     -->
	<!--                                                          -->
	<!-- ======================================================== -->
	<xsl:template match="/">
		<html>
			<head>
				<title>
					<xsl:value-of select="//nir:emanante"/>  <xsl:value-of select="//nir:numDoc"/>
				</title>
				<meta http-equiv="Content-Type" content="text/html"/>
				<link href="nir-generico-style.css" rel="stylesheet"/>
			</head>
			<body>
            	<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='intestazione']" />
            	<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='formulainiziale']" />
            	<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='articolato']|/*[name()='NIR']/*/*[name()='contenitore']|/*[name()='NIR']/*/*[name()='gerarchia']" />
            	<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='formulafinale']" />
            	<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='conclusione']" />
			</body>
		</html>
	</xsl:template>
	
	<!-- ======================================================== -->
	<!--                                                          -->
	<!--  Template intestazione e relazione                       -->
	<!--                                                          -->
	<!-- ======================================================== -->
	<xsl:template match="nir:intestazione">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="nir:emanante">
		<div class="title">
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	
	
	<!-- ======================================================== -->
	<!--                                                          -->
	<!--  Template articolato                                     -->
	<!--                                                          -->
	<!-- ======================================================== -->
	<xsl:template match="nir:articolato">
		<table border="0" cellpadding="0" cellspacing="10" width="100%">			
					<xsl:apply-templates/>
		</table>
	</xsl:template>
	
	<xsl:template match="nir:intestazione/nir:titoloDoc">
				<div class="title">
					<b>
						<xsl:apply-templates/>
					</b>
				</div>
	</xsl:template>
	
	<xsl:template match="nir:*">
				<div class="{local-name()}">
						<xsl:apply-templates select="nir:num">
						</xsl:apply-templates>
						<xsl:text> </xsl:text>
						<xsl:apply-templates select="nir:rubrica | nir:corpo| nir:alinea">
						</xsl:apply-templates>
				</div>
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="nir:num | nir:rubrica | nir:corpo| nir:alinea" />
	<xsl:template match="nir:articolo">
		<xsl:param name="pos">none</xsl:param>
		<div class="articolo">
			<xsl:apply-templates select="nir:num">
				<xsl:with-param name="pos" select="$pos"/>
			</xsl:apply-templates>
			<xsl:text> </xsl:text>
			<xsl:apply-templates select="nir:rubrica">
				<xsl:with-param name="pos" select="$pos"/>
			</xsl:apply-templates>
		</div>
		<xsl:apply-templates select="nir:comma">
			<xsl:with-param name="pos" select="$pos"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="nir:comma| nir:el | nir:en | nir:ep">
		<xsl:param name="pos">none</xsl:param>
		<div class="{local-name()}">
			<xsl:apply-templates select="nir:num">
				<xsl:with-param name="pos" select="$pos"/>
			</xsl:apply-templates>
			<xsl:text> </xsl:text>
			<xsl:apply-templates select="nir:corpo | nir:alinea">
				<xsl:with-param name="pos" select="$pos"/>
			</xsl:apply-templates>
		</div>
		<xsl:apply-templates select="nir:el | nir:ep | nir:en | nir:coda">
			<xsl:with-param name="pos" select="$pos"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="nir:corpo | nir:alinea">
		<xsl:param name="pos">none</xsl:param>
				<xsl:apply-templates>
					<xsl:with-param name="pos" select="$pos"/>
				</xsl:apply-templates>
	</xsl:template>
	
	<!-- ======================================================== -->
	<!--                                                          -->
	<!--  Template sotto il comma                                 -->
	<!--                                                          -->
	<!-- ======================================================== -->
	<xsl:template match="nir:articolo/nir:num">
		<xsl:param name="pos">none</xsl:param>
		<a name="{../@id}">
			<xsl:apply-templates>
				<xsl:with-param name="pos" select="$pos"/>
			</xsl:apply-templates>
		</a>
	</xsl:template>
	<xsl:template match="nir:num">
		<xsl:param name="pos">none</xsl:param>
		<xsl:apply-templates>
			<xsl:with-param name="pos" select="$pos"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="nir:articolo/nir:rubrica">
		<xsl:param name="pos">none</xsl:param>
		<xsl:text>(</xsl:text>
		<xsl:apply-templates>
			<xsl:with-param name="pos" select="$pos"/>
		</xsl:apply-templates>
		<xsl:text>)</xsl:text>
	</xsl:template>
	<xsl:template match="nir:virgolette">
		<xsl:param name="pos">none</xsl:param>
		<p>
			<xsl:text>"</xsl:text>
			<xsl:apply-templates>
				<xsl:with-param name="pos" select="$pos"/>
			</xsl:apply-templates>
			<xsl:text>"</xsl:text>
		</p>
	</xsl:template>
	<xsl:template match="nir:nome">
		<span title="Nome: {.}">
			<xsl:apply-templates/>
		</span>
	</xsl:template>
	<xsl:template match="nir:rif">
		<a href="http://www.nir.it/cgi-bin/N2Ln?{@xlink:href}" title="Destinazione: {@xlink:href}">
			<xsl:apply-templates/>
		</a>
	</xsl:template>
	<xsl:template match="nir:data">
		<span title="Data: {concat(substring(@norm,7,2),'/',substring(@norm,5,2),'/',substring(@norm,1,4))}">
			<xsl:apply-templates/>
		</span>
	</xsl:template>
	<xsl:template match="nir:ndr">
		<sup class="ndr">
			<xsl:attribute name="title">Nota: <xsl:value-of select="."/></xsl:attribute>
			[<xsl:value-of select="@value"/>]
		</sup>
	</xsl:template>
	<!-- ======================================================== -->
	<!--                                                          -->
	<!--  Template conclusioni                                    -->
	<!--                                                          -->
	<!-- ======================================================== -->
	<xsl:template match="nir:conclusione">
		<div style="margin-top:15px;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	<xsl:template match="nir:dataeluogo">
		<div style="margin-top:5px;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	<xsl:template match="nir:sottoscrizioni">
		<ul style="margin-top:5px;">
			<xsl:apply-templates/>
		</ul>
	</xsl:template>
	<xsl:template match="nir:sottoscrivente">
		<li>
			<xsl:apply-templates/>
		</li>
	</xsl:template>
	<!-- ======================================================== -->
	<!--                                                          -->
	<!--  Template allegati                                       -->
	<!--                                                          -->
	<!-- ======================================================== -->
	<xsl:template match="nir:annessi">
		<hr/>
		<ol style="margin-top:15px;">
			<xsl:apply-templates/>
		</ol>
	</xsl:template>
	<xsl:template match="nir:testata">
		<li class="small">
			<xsl:apply-templates select="nir:denAnnesso| nir:titAnnesso"/>
		</li>
	</xsl:template>
	<xsl:template match="nir:denAnnesso">
		<b>
			<xsl:apply-templates select=".//text()"/>
		</b>
		<xsl:if test="following-sibling::nir:titAnnesso"> - </xsl:if>
	</xsl:template>
	<xsl:template match="nir:titAnnesso">
		<a name="{../../@id}" href="{../../nir:rifesterno/@xlink:href}">
			<xsl:apply-templates select=".//text()"/>
		</a>
	</xsl:template>
	<!-- ======================================================== -->
	<!--                                                          -->
	<!--  Template metadati                                       -->
	<!--                                                          -->
	<!-- ======================================================== -->
	<xsl:template match="nir:meta">
		<hr/>
		<table border="1" cellpadding="2" cellspacing="0" width="75%" style="margin-left: 15px;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>
	<xsl:template match="nir:urn">
		<tr>
			<td class="small">
				<a href="http://www.senato.it/japp/bgt/showdoc/frame.jsp?tipodoc={//nir:approvazione/@tipodoc}&amp;leg={//nir:approvazione/@leg}&amp;id={//nir:approvazione/@internal_id}">
					<xsl:value-of select="."/>
				</a>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="nir:confronto"/>
	
	<!-- ======================================================== -->
	<!--                                                          -->
	<!--  Template generici                                       -->
	<!--                                                          -->
	<!-- ======================================================== -->
	<xsl:template match="@*">
		<xsl:attribute name="{local-name()}"><xsl:value-of select="."/></xsl:attribute>
	</xsl:template>
	<xsl:template match="h:*">
		<xsl:param name="pos">none</xsl:param>
		<xsl:element name="{local-name()}">
			<xsl:apply-templates select="@*"/>
			<xsl:apply-templates>
				<xsl:with-param name="pos" select="$pos"/>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="h:span">
		<xsl:choose>
			<xsl:when test="@inizio!=''">
			    <xsl:variable name="inizio_id">
				   <xsl:value-of select="@inizio"/>
				</xsl:variable>
				<xsl:variable name="fine_id">
				   <xsl:value-of select="@fine"/>
				</xsl:variable>
					<xsl:element name="span"> 
						<xsl:variable name="data_inizio">
				   			<xsl:value-of select="//nir:vigenza[@id=$inizio_id]/@inizio"/>
						</xsl:variable>
						<xsl:variable name="data_fine">
				   			<xsl:value-of select="//nir:vigenza[@id=$fine_id]/@fine"/>
						</xsl:variable>
						<xsl:choose>
							<xsl:when test="$data_fine!=''">
							    <i>
							    	[<xsl:value-of select="concat(substring($data_inizio,7,2),'/',substring($data_inizio,5,2),'/',substring($data_inizio,1,4))"/> - <xsl:value-of select="concat(substring($data_fine,7,2),'/',substring($data_fine,5,2),'/',substring($data_fine,1,4))"/>] 
								</i>
								<font color="#FF0000">
									<xsl:apply-templates/>
								</font>
							</xsl:when>
							<xsl:otherwise>
							    <i>
									[dal <xsl:value-of select="concat(substring($data_inizio,7,2),'/',substring($data_inizio,5,2),'/',substring($data_inizio,1,4))"/>]
						        </i>
								<font color="#006600">
									<xsl:apply-templates/>	
								</font>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:element>
			</xsl:when>
			<xsl:otherwise>
		    	<xsl:element name="span">
		        	<xsl:apply-templates/>
		    	</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	
	<xsl:template match="nir:*">
		<xsl:param name="pos">none</xsl:param>
		<xsl:apply-templates>
			<xsl:with-param name="pos" select="$pos"/>
		</xsl:apply-templates>
	</xsl:template>
</xsl:stylesheet>