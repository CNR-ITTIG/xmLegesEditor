<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:nir="http://www.normeinrete.it/disegnilegge/1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" exclude-result-prefixes="h nir xlink" xmlns="http://www.w3.org/HTML/1998/html4" xmlns:h="http://www.w3.org/HTML/1998/html4" xmlns:xlink="http://www.w3.org/1999/xlink">
	<xsl:output method="html" encoding="UTF-8" indent="yes"/>
	<!-- ======================================================== -->
	<!--                                                          -->
	<!--  Template principale                                     -->
	<!--                                                          -->
	<!-- ======================================================== -->
	<xsl:template match="nir:DisegnoLegge | nir:DocumentoNIR">
		<html>
			<head>
				<title>
					<xsl:value-of select="//nir:emanante"/> - Disegno di Legge n. <xsl:value-of select="//nir:numDoc"/>
				</title>
				<meta http-equiv="Content-Type" content="text/html"/>
				<link href="nir-disegnilegge-style.css" rel="stylesheet"/>
			</head>
			<body>
				<table cellpadding="0" cellspacing="5" border="0" width="100%">
					<tr style="margin:top:30px; margin-bottom:30px;">
						<td colspan="2">
							<xsl:apply-templates select="nir:intestazione"/>
						</td>
					</tr>
					<tr valign="top">
						<!--xsl:if test="nir:articolato | nir:annessi">
							<td width="20%" style="border: thin solid #bbbbbb; background-color: #ffffcc;">
								<div class="toc">Sommario</div>
								<xsl:apply-templates select="nir:articolato|nir:annessi" mode="Link"/>
							</td>
						</xsl:if-->
						<td width="100%" style="padding-top:15px;">
							<xsl:apply-templates select="nir:relazione"/>
							<xsl:apply-templates select="nir:articolato | nir:contenitore"/>
							<xsl:apply-templates select="nir:conclusione"/>
							<xsl:apply-templates select="nir:annessi"/>
							<!--xsl:apply-templates select="nir:meta"/-->
						</td>
					</tr>
				</table>
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
	<xsl:template match="nir:legislatura"/>
	<xsl:template match="nir:numDoc">
		<div class="title">Disegno di legge n. <xsl:value-of select="."/> (<xsl:value-of select="//nir:legislatura"/>)</div>
	</xsl:template>
	<xsl:template match="nir:intestazione/h:div ">
		<div class="intestazione">
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	<xsl:template match="nir:relazione">
		<xsl:apply-templates/>
		<hr/>
	</xsl:template>
	<xsl:template match="nir:relazione/h:div">
		<div style="margin-top:5px;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	<xsl:template match="nir:intestazione/nir:titoloDoc | nir:intestazione/nir:tipoDoc">
		<div class="intestazione">
			<i>
				<xsl:apply-templates/>
			</i>
		</div>
	</xsl:template>
	<!-- ======================================================== -->
	<!--                                                          -->
	<!--  Modo Link (barra a sinistra)                            -->
	<!--                                                          -->
	<!-- ======================================================== -->
	<!--xsl:template match="nir:articolo" mode="Link">
		<div class="toc-articolo">
			<xsl:apply-templates select="nir:num | nir:rubrica" mode="Link"/>
		</div>
		<xsl:if test="count(nir:comma) > 5">
			<div class="toc-comma">
				<xsl:apply-templates select="nir:comma" mode="Link"/>
			</div>
		</xsl:if>
	</xsl:template>
	<xsl:template match="nir:articolato" mode="Link">
		<xsl:apply-templates mode="Link"/>
	</xsl:template>
	<xsl:template match="nir:comma" mode="Link">
		<xsl:apply-templates select="nir:num" mode="Link"/>
		<xsl:if test="position() &lt; last()">, </xsl:if>
	</xsl:template>
	<xsl:template match="nir:num" mode="Link">
		<a href="#{../@id}">
			<xsl:value-of select="."/>
		</a>
		<xsl:if test="following-sibling::nir:rubrica"> - </xsl:if>
	</xsl:template>
	<xsl:template match="nir:comma/nir:num" mode="Link">
		<a href="#{../@id}">c <xsl:apply-templates select="node()[not(@status='soppresso')]"/>
		</a>
	</xsl:template>
	<xsl:template match="nir:rubrica" mode="Link">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="nir:annessi" mode="Link">
		<div class="toc-{name()}">
			<a href="#{nir:annesso[1]/@id}">Annessi</a>
		</div>
	</xsl:template-->
	<!-- ======================================================== -->
	<!--                                                          -->
	<!--  Template articolato                                     -->
	<!--                                                          -->
	<!-- ======================================================== -->
	<xsl:template match="nir:articolato">
		<table border="0" cellpadding="0" cellspacing="10" width="100%">
			<xsl:choose>
				<xsl:when test="//@tipo='modificato'">
					<xsl:apply-templates select="//nir:titoloDoc" mode="parallelo"/>
					<xsl:apply-templates mode="parallelo"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates/>
				</xsl:otherwise>
			</xsl:choose>
		</table>
	</xsl:template>
	<xsl:template match="nir:intestazione/nir:titoloDoc" mode="parallelo">
		<tr>
			<td width="50%" valign="top">
				<div class="intestazione">
					<b>
						<xsl:apply-templates select="//nir:tipoDoc"/>
					</b>
				</div>
				<div class="intestazione">
					<b>
						<xsl:apply-templates>
							<xsl:with-param name="pos">left</xsl:with-param>
						</xsl:apply-templates>
					</b>
				</div>
				<div class="intestazione">
					<xsl:apply-templates select="//nir:sinistra"/>
				</div>
			</td>
			<td width="50%" valign="top">
				<div class="intestazione">
					<b>
						<xsl:apply-templates select="//nir:tipoDoc"/>
					</b>
				</div>
				<div class="intestazione">
					<b>
						<xsl:apply-templates>
							<xsl:with-param name="pos">right</xsl:with-param>
						</xsl:apply-templates>
					</b>
				</div>
				<div class="intestazione">
					<xsl:apply-templates select="//nir:destra"/>
				</div>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="nir:*" mode="parallelo">
		<tr>
			<td width="50%" valign="top">
				<div class="{local-name()}">
					<xsl:if test="not(@status='inserito')">
						<xsl:apply-templates select="nir:num">
							<xsl:with-param name="pos">left</xsl:with-param>
						</xsl:apply-templates>
						<xsl:text> </xsl:text>
						<xsl:apply-templates select="nir:rubrica | nir:corpo| nir:alinea">
							<xsl:with-param name="pos">left</xsl:with-param>
						</xsl:apply-templates>
					</xsl:if>
				</div>
			</td>
			<td width="50%" valign="top">
				<div class="{local-name()}">
					<xsl:if test="@status='inserito'">
						<xsl:attribute name="style">font-weight: bold;</xsl:attribute>
					</xsl:if>
					<xsl:if test="not(@status='soppresso')">
						<xsl:apply-templates select="nir:num">
							<xsl:with-param name="pos">right</xsl:with-param>
						</xsl:apply-templates>
						<xsl:text> </xsl:text>
					</xsl:if>
					<xsl:apply-templates select="nir:rubrica | nir:corpo| nir:alinea">
						<xsl:with-param name="pos">right</xsl:with-param>
					</xsl:apply-templates>
				</div>
			</td>
		</tr>
		<xsl:apply-templates mode="parallelo"/>
	</xsl:template>
	<xsl:template match="nir:num | nir:rubrica | nir:corpo| nir:alinea" mode="parallelo"/>
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
		<xsl:choose>
			<xsl:when test="($pos='left' and ../@status='inserito')"/>
			<xsl:when test="($pos='right' and ../@status='soppresso')">
				<i>Soppresso</i>
			</xsl:when>
			<xsl:when test="$pos='right' and not(.//@status) and not(../@status='inserito')">
				<i>Identico</i>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates>
					<xsl:with-param name="pos" select="$pos"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
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
	<xsl:template match="nir:redazione">
		<tr>
			<td class="small">Documento creato il <xsl:value-of select="concat(substring(@norm,7,2),'/',substring(@norm,5,2),'/',substring(@norm,1,4))"/> da <a href="{@url}">
					<xsl:value-of select="@nome"/>
				</a>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="nir:approvazione">
		<tr>
			<td class="small">
				<xsl:value-of select="//nir:tipoDoc"/> approvato il <xsl:value-of select="concat(substring(@norm,7,2),'/',substring(@norm,5,2),'/',substring(@norm,1,4))"/>.</td>
		</tr>
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
	<!--  Template versioning (status="soppresso" o "inserito")   -->
	<!--                                                          -->
	<!-- ======================================================== -->
	<xsl:template match="h:span[@status='soppresso']">
		<xsl:param name="pos">none</xsl:param>
		<xsl:if test="$pos='left'">
			<xsl:apply-templates/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="h:span[@status='inserito']">
		<xsl:param name="pos">none</xsl:param>
		<xsl:choose>
			<xsl:when test="$pos='right'">
				<b>
					<xsl:apply-templates/>
				</b>
			</xsl:when>
			<xsl:when test="$pos='none'">
				<xsl:apply-templates/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
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
	<xsl:template match="nir:*">
		<xsl:param name="pos">none</xsl:param>
		<xsl:apply-templates>
			<xsl:with-param name="pos" select="$pos"/>
		</xsl:apply-templates>
	</xsl:template>
</xsl:stylesheet>
