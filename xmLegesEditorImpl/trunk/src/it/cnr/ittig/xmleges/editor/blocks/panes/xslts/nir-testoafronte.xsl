<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns="http://www.w3.org/HTML/1998/html4" xmlns:nir="http://www.normeinrete.it/nir/2.2/"
	xmlns:dsp="http://www.normeinrete.it/nir/disposizioni/2.2/"
	xmlns:ittig="http://www.ittig.cnr.it/provvedimenti/2.2" xmlns:h="http://www.w3.org/HTML/1998/html4">

	
	<xsl:output method="html" encoding="UTF-8" indent="yes" />

	<xsl:param name="data1" />
	<xsl:param name="data2" />
	<xsl:param name="css" />



	<xsl:param name="doc" />


	<xsl:param name="encoding" />



	<xsl:param name="baseurl" />

	<!-- ======================================================== -->
	<!-- -->
	<!-- Template principale -->
	<!-- -->
	<!-- ======================================================== -->
	
	
	<xsl:template match="text()">&#160;<xsl:value-of select="."/>&#160;</xsl:template>
	
	<xsl:template match="nir:NIR/*">
	<!-- xsl:template match="nir:Legge | nir:DocumentoNIR" -->
		<html>
			<head>		
				<title>
					<xsl:value-of select="//nir:tipoDoc" />
					&#160;
					n.&#160;
					<xsl:value-of select="//nir:numDoc" />
					&#160;del&#160;
					<xsl:value-of select="//nir:dataDoc" />
				</title>
				<meta http-equiv="Content-Type" content="text/html" />
				<link href="nir-testoafronte-style.css" rel="stylesheet"/>
				<!-- link rel="stylesheet" type="text/css" href="css/nir{$css}.css"	media="screen" /-->
			</head>
			<body>
				<table cellpadding="0" cellspacing="5" border="0" width="100%">
					<tr>
						Testo a fronte tra
						<td width="50%" valign="top">
							documento vigente al
							<xsl:value-of select="$data1" />
						</td>
						<td width="50%" valign="top">
							e documento vigente al
							<xsl:value-of select="$data2" />	
						</td>
					</tr>
				</table>
				<table cellpadding="0" cellspacing="5" border="0" width="100%">
					<tr style="margin:top:30px; margin-bottom:30px;">
						
						<td colspan="2">							
							<xsl:apply-templates select="nir:intestazione" />
							<xsl:apply-templates select="nir:formulainiziale" />
						</td>
					</tr>
					<tr valign="top">
						<td width="100%" style="padding-top:15px;">
							<xsl:apply-templates select="nir:articolato | nir:contenitore" />
							<xsl:apply-templates select="nir:conclusione" />
							<xsl:apply-templates select="nir:annessi" />
							<xsl:call-template name="notemultivigente" />
							<!--xsl:apply-templates select="nir:meta"/ -->
						</td>
					</tr>
				</table>
			</body>
		</html>
	</xsl:template>
	<!-- ======================================================== -->
	<!-- -->
	<!-- Template intestazione e relazione -->
	<!-- -->
	<!-- ======================================================== -->
	
	
	<xsl:template match="/*[name()='NIR']/*/*[name()='formulainiziale']">
		<div class="formulainiziale">
			<xsl:apply-templates />
			&#160;
		</div>

		<hr />
	</xsl:template>
	
	<xsl:template match="/*[name()='NIR']/*/*[name()='intestazione']">

		<div class="intestazione">

			<xsl:apply-templates select="*[name()='emanante']" />
			<xsl:apply-templates select="*[name()='tipoDoc']" />
			<xsl:text> </xsl:text>
			<xsl:apply-templates select="*[name()='dataDoc']" />
			<xsl:text>, n. </xsl:text>
			<xsl:apply-templates select="*[name()='numDoc']" />
			<xsl:apply-templates select="*[name()='titoloDoc']" />

			


			<div class="info">
				&#160;
				<xsl:variable name="tipo">
					<xsl:value-of
						select="//*[name()='NIR']/*/*[name()='meta']/*[name()='descrittori']/*[name()='pubblicazione']/@tipo" />
				</xsl:variable>
				<xsl:variable name="num">
					<xsl:value-of
						select="//*[name()='NIR']/*/*[name()='meta']/*[name()='descrittori']/*[name()='pubblicazione']/@num" />
				</xsl:variable>
				<xsl:variable name="data">
					<xsl:value-of
						select="//*[name()='NIR']/*/*[name()='meta']/*[name()='descrittori']/*[name()='pubblicazione']/@norm" />
				</xsl:variable>
				<xsl:text>Pubblicato su </xsl:text>
				<xsl:value-of select="$tipo" />
				<xsl:text>, n.&#160;</xsl:text>
				<xsl:value-of select="$num" />
				<xsl:if test="$data!=''">
					<xsl:text> del </xsl:text>
					<xsl:value-of
						select="concat(substring($data,7,2),'/',substring($data,5,2),'/',substring($data,1,4))" />
				</xsl:if>
			</div>
		</div>
	
	</xsl:template>
	
	<xsl:template match="//*[name()='tipoDoc']">
		<span>
			<xsl:value-of select="." />
		</span>
	</xsl:template>
	<xsl:template match="//*[name()='dataDoc']">
		<span>
			<xsl:value-of select="." />
		</span>
	</xsl:template>
	<xsl:template match="//*[name()='numDoc']">
		<span>
			<xsl:value-of select="." />
		</span>
	</xsl:template>


	<xsl:template match="//*[name()='emanante']">
		<div class="emanante">
			<xsl:apply-templates />
			&#160;
		</div>
	</xsl:template>

	<xsl:template match="//*[name()='titoloDoc']">

				<div class="titoloDoc">
					&#160;
					<div class="titolodoc">
						<h1>
							<xsl:apply-templates />
						</h1>
					</div>
					&#160;
				</div>

	</xsl:template>


	<xsl:template match="//*[name()='preambolo']">
			
			<div class="preambolo">
				<xsl:apply-templates />
			</div>
		

	</xsl:template>
	

	

	<!-- ======================================================== -->
	<!-- -->
	<!-- Template articolato -->
	<!-- -->
	<!-- ======================================================== -->




<xsl:template match="nir:articolato">
	<table border="0" cellpadding="0" cellspacing="10" width="100%">
			
		<xsl:apply-templates mode="parallelo"/>
				
	</table>
		
</xsl:template>






	<xsl:template match="nir:*" mode="parallelo">
		
			<!-- applying templates for:<xsl:value-of select="local-name(.)"></xsl:value-of>: -->
			<tr>
				<td width="50%" valign="top">
					<div class="{local-name()}">
						
						<xsl:call-template name="vigenza">
							<xsl:with-param name="pos">left</xsl:with-param>
						</xsl:call-template>
						
					</div>
				</td>
				<td width="50%" valign="top">
					<div class="{local-name()}">
					
						<xsl:call-template name="vigenza">
							<xsl:with-param name="pos">right</xsl:with-param>
						</xsl:call-template>
						
					</div>
				</td>

			</tr>
			
		
					
	</xsl:template>
	
	


	<xsl:template match="nir:*" mode="sostituito">
		<xsl:param name="pos">none</xsl:param>
		<xsl:apply-templates>
			<xsl:with-param name="pos" select="$pos" />
		</xsl:apply-templates>
	</xsl:template>

	<!--xsl:template match="nir:num | nir:rubrica | nir:corpo| nir:alinea" mode="parallelo"/ -->









	<!-- ======================================================== -->
	<!-- -->
	<!-- DA QUI Template articolato -->
	<!-- -->
	<!-- ======================================================== -->

	<!-- xsl:template match="//*[name()='articolato'] | //*[name()='contenitore']"> 
		<xsl:choose> <xsl:when test="@status = 'omissis'"> <div class="omissis"> 
		<xsl:apply-templates select="*[name()='num']" /> <xsl:if test="*[name()='rubrica']"> 
		- <xsl:apply-templates select="*[name()='rubrica']/text()" /> </xsl:if> <xsl:text> 
		( Omissis )</xsl:text> </div> </xsl:when> <xsl:otherwise> <div style="width:100%;"> 
		&#160; <xsl:choose> <xsl:when test="$datafine!=''"> <xsl:call-template name="vigenza" 
		/> </xsl:when> <xsl:otherwise> <xsl:call-template name="multivigenza" /> 
		</xsl:otherwise> </xsl:choose> </div> </xsl:otherwise> </xsl:choose> </xsl:template -->

	<!-- ========================== LIBRO============================== -->
	<xsl:template match="nir:libro">
		<xsl:param name="pos">
			none
		</xsl:param>
		<a name="{@id}">&#160;</a>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text> ( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="libro">
					&#160;
					<xsl:call-template name="vigenza">
						<xsl:with-param name="pos" select="$pos" />
					</xsl:call-template>

					&#160;
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!-- ========================== PARTE============================== -->
	<xsl:template match="nir:parte">
		<xsl:param name="pos">
			none
		</xsl:param>
		<a name="{@id}">&#160;</a>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text> ( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="parte">
					&#160;
					<xsl:call-template name="vigenza">
						<xsl:with-param name="pos" select="$pos" />
					</xsl:call-template>
					&#160;
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!-- ========================== TITOLO============================== -->
	<xsl:template match="nir:titolo">
		<xsl:param name="pos">
			none
		</xsl:param>
		<a name="{@id}">&#160;</a>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text> ( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="titolo">
					&#160;
					<xsl:call-template name="vigenza">
						<xsl:with-param name="pos" select="$pos" />
					</xsl:call-template>
					&#160;
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!-- ========================== SEZIONE============================== -->
	<xsl:template match="nir:sezione">
		<xsl:param name="pos">
			none
		</xsl:param>
		<a name="{@id}">&#160;</a>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text> ( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="sezione">
					&#160;
					<xsl:call-template name="vigenza">
						<xsl:with-param name="pos" select="$pos" />
					</xsl:call-template>
					&#160;
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!-- ========================== CAPO============================== -->
	<xsl:template match="nir:capo">
		<xsl:param name="pos">none</xsl:param>
		<hr />
		<a name="{@id}">&#160;</a>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text> ( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<div class="capo">
					<xsl:call-template name="vigenza">
						<xsl:with-param name="pos" select="$pos" />
					</xsl:call-template>
					&#160;
				</div>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!-- ========================== RUBRICA ============================== -->

	<xsl:template match="nir:rubrica">
		<xsl:param name="pos">
			none
		</xsl:param>
		<p class="rubrica">
			<xsl:call-template name="vigenza">
				<xsl:with-param name="pos" select="$pos" />
			</xsl:call-template>
		</p>
	</xsl:template>

	<xsl:template match="//*[name()='decorazione']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<p class="decorazione">
			<xsl:apply-templates />
		</p>
	</xsl:template>



	<!-- =========================ARTICOLO=============================== -->

	<xsl:template match="nir:articolo">
		<xsl:param name="pos">
			none
		</xsl:param>
		
		
		<a name="{@id}">&#160;</a>
		
		
		
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text> ( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="articolo">
					&#160;
					<xsl:call-template name="vigenza">
						<xsl:with-param name="pos" select="$pos" />
					</xsl:call-template>
					&#160;
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!-- =========================COMMA e sotto comma=============================== -->

	<xsl:template match="nir:comma">
		<xsl:param name="pos">
			none
		</xsl:param>
		<a name="{@id}">&#160;</a>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<p class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text> ( Omissis )</xsl:text>
				</p>
			</xsl:when>
			<xsl:otherwise>

				<div class="comma">
					&#160;
					<xsl:call-template name="vigenza">
						<xsl:with-param name="pos" select="$pos" />
					</xsl:call-template>
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template match="nir:num">
		<xsl:param name="pos">
			none
		</xsl:param>
		<xsl:choose>
			<xsl:when
				test="parent::node()[name()='el'] or parent::node()[name()='en'] or parent::node()[name()='ep']">
				<em>
					<xsl:apply-templates>
						<xsl:with-param name="pos" select="$pos" />
					</xsl:apply-templates>
					&#160;
				</em>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates>
					<xsl:with-param name="pos" select="$pos" />
				</xsl:apply-templates>
				&#160;
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<xsl:template match="//*[name()='coda']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<p class="coda">
			<xsl:call-template name="vigenza">
				<xsl:with-param name="pos" select="$pos" />
			</xsl:call-template>
		</p>


	</xsl:template>



	<!-- =========================EL , EN , EP=============================== -->
	<xsl:template match="//*[name()='el'] | //*[name()='en'] | //*[name()='ep']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<a name="{@id}">&#160;</a>
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text> ( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="{local-name()}">
					&#160;
					<a name="{@id}">&#160;</a>
					<xsl:call-template name="vigenza">
						<xsl:with-param name="pos" select="$pos" />
					</xsl:call-template>
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>



	<xsl:template match="//*[name()='corpo']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text> ( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates>
					<xsl:with-param name="pos" select="$pos" />
				</xsl:apply-templates>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template match="//*[name()='alinea']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text> ( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates>
					<xsl:with-param name="pos" select="$pos" />
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!-- ======================================================== -->
	<!-- -->
	<!-- Template sotto il comma -->
	<!-- -->
	<!-- ======================================================== -->


	<xsl:template match="//*[name()='mmod']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<a name="{@id}">&#160;</a>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text> ( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="mmod">
					<xsl:call-template name="vigenza">
						<xsl:with-param name="pos" select="$pos" />
					</xsl:call-template>
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template match="//*[name()='mod']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<a name="{@id}">&#160;</a>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text> ( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="mod">
					<xsl:call-template name="vigenza">
						<xsl:with-param name="pos" select="$pos" />
					</xsl:call-template>
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template match="//*[name()='virgolette']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<a name="{@id}">&#160;</a>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text> ( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<xsl:choose>
					<xsl:when test="@tipo='struttura'">
						<div class="virgolette">
							<xsl:apply-templates />
						</div>
						<!--table width="100%" cellpadding="15"> <tr> <td class="virgolette"> 
							<xsl:apply-templates/> </td> </tr> </table -->
					</xsl:when>
					<xsl:otherwise>
						<div class="virgolette">
							<xsl:apply-templates />
						</div>
					</xsl:otherwise>
				</xsl:choose>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template match="//*[name()='nome']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<div style="display:inline" title="Nome: {.}">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="//*[name()='rifesterno']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<a href="http://www.normattiva.it/uri-res/N2Ls?{@xlink:href}" title="URN = {@xlink:href}">
			<xsl:value-of select="@xlink:href" />
		</a>
	</xsl:template>

	<xsl:template match="//*[name()='rif']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<xsl:variable name="url">
			<xsl:value-of select="@xlink:href" />
		</xsl:variable>
		<xsl:variable name="post">
			<xsl:value-of select="./parent::*" />
		</xsl:variable>
		<xsl:variable name="strina">
			<xsl:value-of select="substring(substring-after($post,.),1,1)" />
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="contains($url,'urn:')">
				<xsl:choose>
					<xsl:when
						test="$strina='&#59;' or $strina='&#46;' or $strina='&#58;' or $strina='&#44;'  or $strina='&#45;'">
						<a href="http://www.normattiva.it/uri-res/N2Ls?{@xlink:href}"
							title="URN = {@xlink:href}">
							<xsl:apply-templates />
						</a>
					</xsl:when>
					<xsl:otherwise>
						<a href="http://www.normattiva.it/uri-res/N2Ls?{@xlink:href}"
							title="URN = {@xlink:href}">
							<xsl:apply-templates />
						</a>
						&#160;
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when
						test="$strina='&#59;' or $strina='&#46;' or $strina='&#58;' or $strina='&#44;'  or $strina='&#45;'">
						<a href="{@xlink:href}" title="Destinazione: {@xlink:href}">
							<xsl:apply-templates />
						</a>
					</xsl:when>
					<xsl:otherwise>
						<a href="{@xlink:href}" title="Destinazione: {@xlink:href}">
							<xsl:apply-templates />
						</a>
						&#160;
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>
	<xsl:template match="//*[name()='data']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<div style="display:inline"
			title="Data: {concat(substring(@norm,7,2),'/',substring(@norm,5,2),'/',substring(@norm,1,4))}">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	<xsl:template match="//*[name()='ndr']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<xsl:variable name="numnogratella">
			<xsl:value-of select="substring(@num,2,string-length(@num))" />
		</xsl:variable>


		<sup class="ndr">
			<a name="{concat('ndr',$numnogratella)}" href="{@num}"
				title="Destinazione: {@num}">
				<xsl:attribute name="title">Nota: <xsl:value-of
					select="." />
</xsl:attribute>
				<xsl:value-of select="." />
			</a>
		</sup>

	</xsl:template>

	<!-- ======================================================== -->
	<!-- -->
	<!-- Template conclusioni e formula finale -->
	<!-- -->
	<!-- ======================================================== -->
	<xsl:template match="//*[name()='formulafinale']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text> ( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="formulafinale">
					<hr />
					<xsl:call-template name="vigenza">
						<xsl:with-param name="pos" select="$pos" />
					</xsl:call-template>
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template match="//*[name()='conclusione']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<div class="conclusione">
			&#160;
			<xsl:apply-templates />
		</div>
	</xsl:template>
	<xsl:template match="//*[name()='dataeluogo']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<p class="dataeluogo">
			<xsl:apply-templates />
		</p>
	</xsl:template>

	<xsl:template match="//*[name()='firma']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<xsl:choose>
			<xsl:when test="@tipo='visto'">
				<p class="visto">
					<xsl:call-template name="vigenza">
						<xsl:with-param name="pos" select="$pos" />
					</xsl:call-template>
				</p>
			</xsl:when>
			<xsl:otherwise>
				<p class="firma">
					<xsl:call-template name="vigenza">
						<xsl:with-param name="pos" select="$pos" />
					</xsl:call-template>
				</p>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- ======================================================== -->
	<!-- -->
	<!-- Template allegati -->
	<!-- -->
	<!-- ======================================================== -->
	<xsl:template match="//*[name()='annessi']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<br />
		ALLEGATI:
		<xsl:apply-templates />
	</xsl:template>
	<xsl:template match="//*[name()='annesso']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<a name="{@id}">&#160;</a>
		<xsl:call-template name="vigenza">
			<xsl:with-param name="pos" select="$pos" />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="//*[name()='testata']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<hr />
		<xsl:apply-templates select="*[name()='denAnnesso'] | *[name()='titAnnesso']" />
		<br />
	</xsl:template>
	<xsl:template match="*[name()='denAnnesso']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<xsl:text> - </xsl:text>
		<b>
			<xsl:apply-templates select=".//text()" />
			&#160;
		</b>
		<xsl:text> - </xsl:text>
	</xsl:template>
	<xsl:template match="*[name()='titAnnesso']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<xsl:apply-templates select=".//text()" />
	</xsl:template>
	<!-- ======================================================== -->
	<!-- -->
	<!-- Template metadati -->
	<!-- -->
	<!-- ======================================================== -->
	<xsl:template match="//*[name()='meta']" />
	<!--xsl:template match="//*[name()='urn']"> <tr> <td class="small"> <a href="http://www.senato.it/japp/bgt/showdoc/frame.jsp?tipodoc={//approvazione/@tipodoc}&amp;leg={//approvazione/@leg}&amp;id={//approvazione/@internal_id}"> 
		<xsl:value-of select="."/> </a> </td> </tr> </xsl:template> <xsl:template 
		match="//*[name()='redazionale']"> <div class="redazionale"> <xsl:apply-templates 
		/> </div> </xsl:template -->
	<xsl:template match="//*[name()='nota']">
		<xsl:param name="pos">
			none
		</xsl:param>
		<xsl:variable name="idnota">
			<xsl:value-of select="@id" />
		</xsl:variable>
		<xsl:variable name="idnotagratella">
			#
			<xsl:value-of select="@id" />
		</xsl:variable>


		<xsl:for-each select="//*[name()='ndr'][@num=$idnotagratella]">
			<a class="nota" name="{$idnota}" href="{concat('#ndr',$idnota)}">
				<xsl:value-of select="." />
			</a>
		</xsl:for-each>

		<span class="nota">
			<xsl:text>&#160;-&#160;</xsl:text>
		</span>
		<xsl:apply-templates />


	</xsl:template>




	<!-- ======================================================== -->
	<!-- -->
	<!-- Template generici -->
	<!-- -->
	<!-- ======================================================== -->
	<xsl:template match="h:table">
		<xsl:element name="{local-name()}">
			<xsl:apply-templates select="@*" />
			<xsl:if test="not(@width)">
				<xsl:attribute name="width">100%</xsl:attribute>
			</xsl:if>
			<xsl:if test="not(@border)">
				<xsl:attribute name="border">1</xsl:attribute>
			</xsl:if>
			<xsl:if test="not(@cellpadding)">
				<xsl:attribute name="cellpadding">2</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="@*">
		<xsl:attribute name="{local-name()}">
<xsl:value-of select="." />
</xsl:attribute>
	</xsl:template>

	<xsl:template match="@*" mode="object">
		<xsl:choose>
			<xsl:when test="name()='src'">
				<xsl:variable name="nome">
					<xsl:value-of select="." />
				</xsl:variable>
				<xsl:attribute name="src">
<xsl:value-of select="concat('file://',$baseurl,$nome)" />
</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="{name()}">
<xsl:value-of select="." />
</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--xsl:template match="h:*">
		<xsl:element name="{local-name()}">
			<xsl:apply-templates select="@*" />
			<xsl:apply-templates />

		</xsl:element>
	</xsl:template-->
	
	
	<!--xsl:template match="h:*">
		<xsl:param name="pos">none</xsl:param>
		<xsl:element name="{local-name()}">
			<xsl:apply-templates select="@*"/>
			<xsl:apply-templates>
				<xsl:with-param name="pos" select="$pos"/>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template-->
	

	<xsl:template match="h:img">
		<xsl:element name="{local-name()}">
			<xsl:apply-templates select="@*" mode="object" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	<!-- ======================================================== -->
	<!-- -->
	<!-- template riferimenti incompleti -->
	<!-- -->
	<!-- ======================================================== -->
	<xsl:template match="processing-instruction('rif')">
		<xsl:value-of select="substring-before(substring-after(.,'&gt;'),'&lt;')" />
		&#160;
	</xsl:template>
	<!-- ======================================================== -->
	<!-- -->
	<!-- template span -->
	<!-- -->
	<!-- ======================================================== -->
	<xsl:template match="h:span">
		<xsl:param name="pos">
			none
		</xsl:param>
		&#160;
		<div style="display:inline">
			&#160;
			<xsl:call-template name="vigenza">
				<xsl:with-param name="pos" select="$pos" />
			</xsl:call-template>
		</div>
		&#160;
	</xsl:template>


	<!-- ======================================================== -->
	<!-- -->
	<!-- template p -->
	<!-- -->
	<!-- ======================================================== -->
	<xsl:template match="h:p">
		<xsl:param name="pos">
			none
		</xsl:param>
		<p>
			<xsl:call-template name="vigenza">
				<xsl:with-param name="pos" select="$pos" />
			</xsl:call-template>
		</p>
	</xsl:template>


	<!-- ======================================================== -->
	<!-- -->
	<!-- template gestione urnvigente -->
	<!-- -->
	<!-- ======================================================== -->

	<xsl:template name="urnvigente">
		<div class="urnvigente">
			&#160;
			<xsl:choose>
				<xsl:when test="/*[name()='NIR']/@tipo!='multivigente'">
					<xsl:value-of
						select="/*[name()='NIR']/*/*[name()='meta']/*[name()='descrittori']/*[name()='urn']/@valore" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:for-each
						select="/*[name()='NIR']/*/*[name()='meta']/*[name()='descrittori']/*[name()='urn']">
						<xsl:choose>
							<xsl:when test="$data1!=''">
								<xsl:variable name="inizio_id">
									<xsl:value-of select="@iniziovigore" />
								</xsl:variable>
								<xsl:variable name="fine_id">
									<xsl:value-of select="@finevigore" />
								</xsl:variable>
								<xsl:variable name="data_inizio">
									<xsl:value-of select="//*[name()='evento'][@id=$inizio_id]/@data" />
									<!--xsl:value-of select="id($inizio_id)/@data"/ -->
								</xsl:variable>
								<xsl:variable name="data_fine">
									<xsl:value-of select="//*[name()='evento'][@id=$fine_id]/@data" />
									<!--xsl:value-of select="id($fine_id)/@data"/ -->
								</xsl:variable>
								<xsl:choose>
									<xsl:when test="$data_fine!=''">
										<!-- aggiungo un caso: posso avere un intervallo che parte e termina 
											lo stesso giorno <xsl:if test="$data_inizio&lt;number(number($data1)+1) and 
											$data_fine&gt;$data1"> <xsl:value-of select="@valore" /> <br/> </xsl:if -->
										<xsl:choose>
											<xsl:when
												test="$data_inizio&lt;number(number($data1)+1) and $data_fine&gt;$data1">
												<xsl:value-of select="@valore" />
												<br />
											</xsl:when>
											<xsl:otherwise>
												<xsl:if
													test="$data_inizio=$data1 and $data_fine=$data_inizio=number(number($data1)+1)">
													<xsl:value-of select="@valore" />
													<br />
												</xsl:if>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:when>
									<xsl:otherwise>
										<xsl:if test="$data_inizio&lt;number(number($data1)+1)">
											<xsl:value-of select="@valore" />
											<br />
										</xsl:if>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							<xsl:otherwise>
								<xsl:if test="@iniziovigore='t1'">
									<xsl:value-of select="@valore" />
									<br />
								</xsl:if>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>


	<!-- ======================================================== -->
	<!-- -->
	<!-- template gestione vigenza LEFT -->
	<!-- -->
	<!-- ======================================================== -->


	<xsl:template name="vigenza">
		<xsl:param name="pos">
			none
		</xsl:param>

		<xsl:choose>
			<xsl:when test="$pos='left'">
				<xsl:call-template name="vigenzaLeft">
					<xsl:with-param name="pos">left</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="vigenzaRight">
					<xsl:with-param name="pos">right</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>



	<xsl:template name="vigenzaLeft">
		<xsl:param name="pos">
			none
		</xsl:param>
		<xsl:variable name="idnota">
			<xsl:value-of select="@id" />
		</xsl:variable>
		<xsl:variable name="stato">
			<xsl:value-of select="@status" />
		</xsl:variable>
		<xsl:variable name="inizio_id">
			<xsl:value-of select="@iniziovigore" />
		</xsl:variable>
		<xsl:variable name="fine_id">
			<xsl:value-of select="@finevigore" />
		</xsl:variable>


		<xsl:variable name="data_inizio">
			<xsl:value-of select="//*[name()='evento'][@id=$inizio_id]/@data" />
		</xsl:variable>
		<xsl:variable name="data_fine">
			<xsl:value-of select="//*[name()='evento'][@id=$fine_id]/@data" />
		</xsl:variable>
		<xsl:variable name="giornoprima">
			<xsl:call-template name="finevigenza">
				<xsl:with-param name="datafinevigenza" select="$data_fine" />
			</xsl:call-template>
		</xsl:variable>


		<xsl:choose>

			<!-- ========================================== DATA FINE !='' ====================================== -->
			<xsl:when test="$data_fine!=''">
				<xsl:variable name="ittignota">
					<xsl:value-of
						select="/*[name()='NIR']/*/*[name()='meta']/*[name()='disposizioni']/*[name()='modifichepassive']/*/*/*[name()='dsp:pos'][@xlink:href=$idnota]/../../*[name()='dsp:norma']/*[name()='dsp:subarg']/*[name()='ittig:notavigenza']/@id" />
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="$data_fine&lt;number(number($data1)+1)">

						<xsl:choose>
							<xsl:when test="following-sibling::node()[1][@iniziovigore=$fine_id]" />

							<xsl:otherwise>
								<div class="abrogato">
									<xsl:choose>
										<xsl:when test="*[name()='num']">
											[
											<xsl:apply-templates select="*[name()='num']">
												<xsl:with-param name="pos" select="$pos" />
											</xsl:apply-templates>
											]
										</xsl:when>
										<xsl:otherwise>
											[ ... ]
										</xsl:otherwise>
									</xsl:choose>
									<xsl:variable name="id">
										<xsl:value-of select="@id" />
									</xsl:variable>
									<xsl:call-template name="scriviNota">
										<xsl:with-param name="id" select="$id"/>
									</xsl:call-template>
								</div>
							</xsl:otherwise>
						</xsl:choose>

					</xsl:when>
					<xsl:when
						test="$data_inizio&lt;number(number($data1)+1) and $data_fine&gt;$data1">
						<xsl:choose>
							<xsl:when test="$data_inizio and $inizio_id!='t1'">
								<div class="inserito">
									<xsl:apply-templates>
										<xsl:with-param name="pos" select="$pos" />
									</xsl:apply-templates>
									<xsl:variable name="id">
										<xsl:value-of select="@id" />
									</xsl:variable>
									<xsl:call-template name="scriviNota">
										<xsl:with-param name="id" select="$id"/>
									</xsl:call-template>
								</div>
							</xsl:when>
							<xsl:otherwise>
								<xsl:apply-templates>
									<xsl:with-param name="pos" select="$pos" />
								</xsl:apply-templates>
								<xsl:variable name="id">
									<xsl:value-of select="@id" />
								</xsl:variable>
									<xsl:call-template name="scriviNota">
										<xsl:with-param name="id" select="$id"/>
									</xsl:call-template>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
			<!-- ========================================== DATA inizio !='' ====================================== -->
			<xsl:when test="$data_inizio!=''">
				<xsl:choose>
					<xsl:when test="$data_inizio&gt;$data1">
						<!--===================== n{@id}, t{@id}:'n' e 't' differenziano il 
							testo dalle note ===================== <xsl:value-of select="@id"/>: il valore 
							accanto a 'VigNota', l'id della partizione -->
					</xsl:when>
					<xsl:otherwise>
						<div class="inserito">

							<xsl:apply-templates>
								<xsl:with-param name="pos" select="$pos" />
							</xsl:apply-templates>


							<xsl:variable name="id">
								<xsl:value-of select="@id" />
							</xsl:variable>
							<xsl:call-template name="scriviNota">
								<xsl:with-param name="id" select="$id"/>
							</xsl:call-template>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>

			<xsl:otherwise>
				<xsl:apply-templates>
					<xsl:with-param name="pos" select="$pos" />
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>





	<!-- ======================================================== -->
	<!-- -->
	<!-- template gestione vigenza RIGHT -->
	<!-- -->
	<!-- ======================================================== -->

	<xsl:template name="vigenzaRight">
		<xsl:param name="pos">
			none
		</xsl:param>
		<xsl:variable name="idnota">
			<xsl:value-of select="@id" />
		</xsl:variable>
		<xsl:variable name="stato">
			<xsl:value-of select="@status" />
		</xsl:variable>
		<xsl:variable name="inizio_id">
			<xsl:value-of select="@iniziovigore" />
		</xsl:variable>
		<xsl:variable name="fine_id">
			<xsl:value-of select="@finevigore" />
		</xsl:variable>

		<xsl:variable name="iv"> <!-- data inizio vigore -->
			<xsl:value-of select="//*[name()='evento'][@id=$inizio_id]/@data" />
		</xsl:variable>
		<xsl:variable name="fv"> <!-- data fine vigore -->
			<xsl:value-of select="//*[name()='evento'][@id=$fine_id]/@data" />
		</xsl:variable>
		<xsl:variable name="giornoprima">
			<xsl:call-template name="finevigenza">
				<xsl:with-param name="datafinevigenza" select="$fv" />
			</xsl:call-template>
		</xsl:variable>


	
        <xsl:variable name="id">
			<xsl:value-of select="@id" />
		</xsl:variable>
        

		<xsl:choose>
			
			<xsl:when test="$iv">
				<xsl:choose>
					<xsl:when test="$iv&gt;number(number($data1)+1)">  <!-- data1 &lt; iniziovig -->
						<xsl:choose>
							<xsl:when test="$iv&gt;number(number($data2)+1)">
								<!-- C: -->
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when test="not($fv) or $fv='' ">
									    <!-- B: -->
										<xsl:choose>
										
											<xsl:when test="preceding-sibling::node()[@finevigore=$inizio_id]">
												
											</xsl:when>
											<xsl:otherwise>
												<div class="inseritoRight">
													[INSERITO] 
													
													<xsl:call-template name="scriviNota">
														<xsl:with-param name="id" select="$id"/>
													</xsl:call-template>
													<br/>
													
													<xsl:apply-templates>
														<xsl:with-param name="pos" select="$pos" />
													</xsl:apply-templates> <!-- riempire col testo -->
												</div>
											</xsl:otherwise>
										</xsl:choose>
										
									</xsl:when>
									<xsl:otherwise>
										<xsl:choose>
											<xsl:when test="$fv&gt;number(number($data2)+1)">
												 <xsl:choose>
										
													<xsl:when test="preceding-sibling::node()[@finevigore=$inizio_id]">
														
													</xsl:when>
													<xsl:otherwise>
														<div class="inseritoRight">
															[INSERITO] 
															<xsl:call-template name="scriviNota">
																<xsl:with-param name="id" select="$id"/>
															</xsl:call-template>
															<br/>
															
															
															<xsl:apply-templates>
																<xsl:with-param name="pos" select="$pos" />
															</xsl:apply-templates> <!-- riempire col testo -->
														</div>
													</xsl:otherwise>
												</xsl:choose>
											</xsl:when>
											<xsl:otherwise>
												<!-- A -->
													<xsl:call-template name="scriviNota">
														<xsl:with-param name="id" select="$id"/>
													</xsl:call-template>
													
											</xsl:otherwise>

										</xsl:choose>
										
									</xsl:otherwise>

								</xsl:choose>
							</xsl:otherwise>

						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="(not($fv) or $fv='') or $fv&gt;number(number($data1)+1)">
								<xsl:choose>
									<xsl:when test="(not($fv) or $fv='') or $fv&gt;number(number($data2)+1)">
										<!-- E: --> 
										
										<xsl:apply-templates>
											<xsl:with-param name="pos" select="$pos" />
										</xsl:apply-templates> <!-- riempire col testo -->
									</xsl:when>
									<xsl:otherwise>
										<xsl:choose>
											
											<xsl:when test="following-sibling::node()[@iniziovigore=$fine_id]">
												<!-- sostituzione -->
												
												<div class="sostituitoRight">	
													[SOSTITUITO] 									
													<xsl:call-template name="scriviNota">
														<xsl:with-param name="id" select="$id"/>
													</xsl:call-template>
													<xsl:apply-templates select="following-sibling::node()[@iniziovigore=$fine_id][1]" mode="sostituito">
														<xsl:with-param name="pos" select="$pos" />
													</xsl:apply-templates> 
													
													
																
													
												</div>
													
											</xsl:when>
											<xsl:otherwise>
												<div class="abrogatoRight">
													<xsl:apply-templates select="nir:num">
														<xsl:with-param name="pos" select="$pos" />
													</xsl:apply-templates>
													<xsl:apply-templates select="nir:rubrica | nir:corpo| nir:alinea">
														<xsl:with-param name="pos" select="$pos" />
													</xsl:apply-templates>
													
													<xsl:text> </xsl:text>
													&#160;[...]<!-- D: -->
													<xsl:call-template name="scriviNota">
														<xsl:with-param name="id" select="$id"/>
													</xsl:call-template>
												</div>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:otherwise>

								</xsl:choose>
							</xsl:when>
							<xsl:otherwise>
								<!-- F: -->
							</xsl:otherwise>

						</xsl:choose>
					</xsl:otherwise>


				</xsl:choose>



			</xsl:when>
			<xsl:otherwise>
				<!-- identici: -->
				<div class="identicoRight">
					<xsl:apply-templates>
						<xsl:with-param name="pos" select="$pos" />
					</xsl:apply-templates> <!-- riempire col testo -->
				</div>
			</xsl:otherwise>

		</xsl:choose>
	</xsl:template>


	<!-- ======================================================== -->
	<!-- -->
	<!-- template note testo MultiVigente (fondo pagina) -->
	<!-- -->
	<!-- ======================================================== -->

	<xsl:template name="notemultivigente">
		<xsl:for-each select="//*[name()!='urn']/@iniziovigore">
			<xsl:variable name="saltare">
				<xsl:value-of select="../@valore" />
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="$saltare" />

				<xsl:otherwise>
					<xsl:variable name="idNoPound">
						<xsl:value-of select="../@id" />
					</xsl:variable>
					<xsl:variable name="id">
						<xsl:value-of select="concat('#',$idNoPound)" />
					</xsl:variable>
					<xsl:variable name="numeronota">
						<xsl:value-of select="position()" />
					</xsl:variable>

					<xsl:variable name="stato">
						<xsl:value-of select="../@status" />
					</xsl:variable>
					<xsl:variable name="inizio_id">
						<xsl:value-of select="../@iniziovigore" />
					</xsl:variable>
					<xsl:variable name="fine_id">
						<xsl:value-of select="../@finevigore" />
					</xsl:variable>
					<xsl:variable name="data_inizio">
						<xsl:value-of select="id($inizio_id)/@data" />
					</xsl:variable>
					<xsl:variable name="data_fine">
						<xsl:value-of select="id($fine_id)/@data" />
					</xsl:variable>
					<xsl:variable name="giornoprima">
						<xsl:call-template name="finevigenza">
							<xsl:with-param name="datafinevigenza" select="$data_fine" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:variable name="fonte">
						<xsl:choose>
							<xsl:when test="$fine_id!=''">
								<xsl:value-of select="id($fine_id)/@fonte" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="id($inizio_id)/@fonte" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:variable name="urn">
						<xsl:value-of select="id($fonte)/@xlink:href" />
					</xsl:variable>

					<xsl:choose>
						<xsl:when test="$numeronota=1">
							<br />
							<h2>Note sulla vigenza</h2>
						</xsl:when>
					</xsl:choose>
					<p>
						<xsl:choose>
							<xsl:when
								test="/*[name()='NIR']/*/*[name()='meta']/*[name()='disposizioni']/*[name()='modifichepassive']/*/*/*[name()='dsp:pos'][@xlink:href=$id]/../../*[name()='dsp:norma']">
								<!--ho le informazione nei metadati -->
								<xsl:for-each
									select="/*[name()='NIR']/*/*[name()='meta']/*[name()='disposizioni']/*[name()='modifichepassive']/*/*/*[name()='dsp:pos'][@xlink:href=$id]/../../*[name()='dsp:norma']">
									<xsl:variable name="implicita">
										<xsl:value-of select="../@implicita" />
									</xsl:variable>
									<xsl:variable name="urn_meta">
										<xsl:value-of
											select="../*[name()='dsp:norma']/*[name()='dsp:pos']/@xlink:href" />
									</xsl:variable>
									<xsl:variable name="autonota">
										<xsl:value-of
											select="../*[name()='dsp:norma']/*[name()='dsp:subarg']/*[name()='ittig:notavigenza']/@auto" />
									</xsl:variable>
									<xsl:variable name="novella">
										<xsl:value-of
											select="../*[name()='dsp:novella']/*[name()='dsp:pos']/@xlink:href" />
									</xsl:variable>
									<xsl:variable name="novellando">
										<xsl:value-of
											select="../*[name()='dsp:novellando']/*[name()='dsp:pos']/@xlink:href" />
									</xsl:variable>

									<xsl:if test="position()=1">
										<a name="n{$idNoPound}" href="#t{$idNoPound}">
											[
											<xsl:value-of select="$numeronota" />
											]
										</a>
									</xsl:if>

									<xsl:text> - </xsl:text>
									<xsl:choose>
										<xsl:when test="$novellando">
											<xsl:choose>
												<xsl:when test="$novella">
													<!--sostituzione -->
													Sostituzione
												</xsl:when>
												<xsl:otherwise>
													<!--abrogazione o annullamento della cassazione -->
													<xsl:choose>
														<!-- <xsl:when test="id(id(substring($novellando,2,number(string-length($novellando))))/@finevigore)/@tipo='inefficacia'"> -->
														<xsl:when
															test="//*[name()='evento'][@id=//*[@id=substring($novellando,2,number(string-length($novellando)))]/@finevigore]/@tipo='inapplicabilita'">
															Reso inefficace
														</xsl:when>
														<xsl:otherwise>
															Abrogazione
														</xsl:otherwise>
													</xsl:choose>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:when>
										<xsl:otherwise>
											<xsl:choose>
												<xsl:when test="$novella">
													<!--integrazione -->
													Integrazione
												</xsl:when>
											</xsl:choose>
										</xsl:otherwise>
									</xsl:choose>
									<xsl:if test="$implicita!='no'">
										<xsl:text> implicita</xsl:text>
									</xsl:if>

									<xsl:if test="$novellando">
										<xsl:if test="$novella">
											<!--sostituzione -->
											<xsl:if test="$novella=$id">
												(testo inserito)
											</xsl:if>
											<xsl:if test="$novellando=$id">
												(testo eliminato)
											</xsl:if>
										</xsl:if>
									</xsl:if>
									<xsl:text> da: </xsl:text>
									<a href="http://www.normattiva.it/uri-res/N2Ls?{$urn_meta}"
										title="URN = {$urn_meta}">
										<xsl:value-of select="$autonota" />
									</a>
									<xsl:text>. </xsl:text>
								</xsl:for-each>
							</xsl:when>
							<xsl:otherwise>
								<!--NON ho le informazione nei metadati -->
								<a name="n{$idNoPound}" href="#t{$idNoPound}">
									[
									<xsl:value-of select="$numeronota" />
									]
								</a>
								<xsl:text> - Modificato da: </xsl:text>
								<a href="http://www.normattiva.it/uri-res/N2Ls?{$urn}">
									<xsl:value-of select="$urn" />
								</a>
								<xsl:text>. </xsl:text>
							</xsl:otherwise>
						</xsl:choose>
						<xsl:choose>
							<!-- ================= data_fine!='' ========= -->
							<xsl:when test="$data_fine!=''">
								In vigore
								<xsl:choose>
									<xsl:when test="$data_inizio!=''">
										dal
										<xsl:value-of
											select="concat(substring($data_inizio,7,2),'/',substring($data_inizio,5,2),'/',substring($data_inizio,1,4))" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>fino</xsl:text>
									</xsl:otherwise>
								</xsl:choose>
								al
								<xsl:value-of select="$giornoprima" />

								<!--xsl:choose> <xsl:when test="$stato!=''"> (<xsl:value-of select="$stato"/>) 
									</xsl:when> </xsl:choose -->

							</xsl:when>
							<!-- ================= data_inizio!='' ========= -->
							<xsl:when test="$data_inizio!=''">
								&#160;In vigoredal
								<xsl:value-of
									select="concat(substring($data_inizio,7,2),'/',substring($data_inizio,5,2),'/',substring($data_inizio,1,4))" />
							</xsl:when>
						</xsl:choose>

					</p>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	

	<!-- ======================================================== -->
	<!-- REPLACE -->
	<!-- ======================================================== -->


	<xsl:template name="replace-string">
		<xsl:param name="text" />
		<xsl:param name="replace" />
		<xsl:param name="with" />
		<xsl:choose>
			<xsl:when test="contains($text,$replace)">
				<xsl:value-of select="substring-before($text,$replace)" />
				<xsl:value-of select="$with" />
				<xsl:call-template name="replace-string">
					<xsl:with-param name="text"
						select="substring-after($text,$replace)" />
					<xsl:with-param name="replace" select="$replace" />
					<xsl:with-param name="with" select="$with" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- ======================================================== -->
	<!-- Calcola -1 giorno -->
	<!-- ======================================================== -->

	<xsl:template name="finevigenza">
		<xsl:param name="datafinevigenza" />
		<xsl:variable name="giornofinevigenza">
			<xsl:value-of select="substring($datafinevigenza,7,2)" />
		</xsl:variable>
		<xsl:variable name="mesefinevigenza">
			<xsl:value-of select="substring($datafinevigenza,5,2)" />
		</xsl:variable>
		<xsl:variable name="annofinevigenza">
			<xsl:value-of select="substring($datafinevigenza,1,4)" />
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$giornofinevigenza='01'">
				<xsl:choose>
					<xsl:when test="$mesefinevigenza='01'">
						<xsl:value-of
							select="concat('31','/','12','/',format-number(number(number($annofinevigenza)-1), '0000'))" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:variable name="mesevigenza">
							<xsl:value-of select="number(number($mesefinevigenza)-1)" />
						</xsl:variable>
						<xsl:variable name="giornovigenza">
							<xsl:choose>
								<xsl:when test="$mesevigenza=1">
									31
								</xsl:when>
								<xsl:when test="$mesevigenza=2">
									28
								</xsl:when>
								<xsl:when test="$mesevigenza=3">
									31
								</xsl:when>
								<xsl:when test="$mesevigenza=4">
									30
								</xsl:when>
								<xsl:when test="$mesevigenza=5">
									31
								</xsl:when>
								<xsl:when test="$mesevigenza=6">
									30
								</xsl:when>
								<xsl:when test="$mesevigenza=7">
									31
								</xsl:when>
								<xsl:when test="$mesevigenza=8">
									31
								</xsl:when>
								<xsl:when test="$mesevigenza=9">
									30
								</xsl:when>
								<xsl:when test="$mesevigenza=10">
									31
								</xsl:when>
								<xsl:when test="$mesevigenza=11">
									30
								</xsl:when>
								<xsl:when test="$mesevigenza=12">
									31
								</xsl:when>
							</xsl:choose>
						</xsl:variable>
						<xsl:value-of
							select="concat($giornovigenza,'/',format-number(number(number($mesefinevigenza)-1),'00'),'/',$annofinevigenza)" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of
					select="concat(format-number(number(number($giornofinevigenza)-1),'00'),'/',$mesefinevigenza,'/',$annofinevigenza)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>








<xsl:template name="scriviNota">
	<!-- xsl:param name="id" />
		<xsl:for-each select="//*[name()!='urn']/@iniziovigore">
			<xsl:if test="../@id=$id">
				<a href="#n{$id}" name="t{$id}">
					<sup>
						[
						<xsl:value-of select="position()" />
						]
					</sup>
				</a>
			</xsl:if>
		</xsl:for-each>
		&#160; -->
</xsl:template>



























</xsl:stylesheet>
