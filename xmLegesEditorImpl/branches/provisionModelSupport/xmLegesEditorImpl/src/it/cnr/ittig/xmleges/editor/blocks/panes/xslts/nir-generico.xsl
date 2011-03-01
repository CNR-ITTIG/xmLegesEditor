<?xml version="1.0" encoding="UTF-8"?>
<!--  DA JTEST: xsl pubblicazione raccolta normativa CAMPANIA -->
<xsl:stylesheet version="1.0"
	xmlns:nir="http://www.normeinrete.it/nir/2.2"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:h="http://www.w3.org/HTML/1998/html4"
	xmlns:xlink="http://www.w3.org/1999/xlink">

	<xsl:output method="html" indent="yes" omit-xml-declaration="yes" />
	<!-- ======================================================== -->
	<!-- -->
	<!-- Template principale -->
	<!-- -->
	<!-- ======================================================== -->

	<xsl:param name="datafine"></xsl:param>
	<xsl:param name="css" />
	<xsl:param name="encoding" />
	<xsl:param name="baseurl" />
	<xsl:template match="/">
		<html lang="it" dir="ltr">
			<head>
				<title>
					<xsl:value-of
						select="/*[name()='NIR']/*/*/*[name()='tipoDoc']" />
					&#160; n.&#160;
					<xsl:value-of
						select="/*[name()='NIR']/*/*/*[name()='numDoc']" />
					&#160;del&#160;
					<xsl:value-of
						select="/*[name()='NIR']/*/*/*[name()='dataDoc']" />
				</title>

				<meta http-equiv="Content-Type"
					content="text/html; charset={$encoding}" />
				<link rel="stylesheet" type="text/css"
					href="nir-generico-style.css" media="screen" />

			</head>
			<body>



				<div id="contenitore">
					&#160;

					<xsl:choose>
						<xsl:when test="$datafine!=''">
							<div class="vigore">
								&#160;
								<!--p style="font-weight:bold;" -->
								<xsl:text>Atto in vigore al</xsl:text>
								<xsl:value-of
									select="concat(substring($datafine,7,2),'/',substring($datafine,5,2),'/',substring($datafine,1,4))" />
								<!--/p -->
								&#160;
							</div>
						</xsl:when>
					</xsl:choose>

					<!-- xsl:choose> <xsl:when test="//*[name()='NIR']/@tipo!='originale'" -->
					<div id="timeline">
						&#160;
						<xsl:call-template name="TimeLine" />
					</div>
					<!-- /xsl:when> </xsl:choose -->
					<xsl:apply-templates select="/*[name()='NIR']/*" />
					<div class="allineasx">
						&#160;
						<xsl:call-template name="notemultivigente" />
					</div>

					<xsl:if
						test="/*/*/*[name()='meta']/*[name()='redazionale']/*[name()='nota']">
						<div class="allineasx">
							&#160;
							<br />
							<h2>Note della redazione</h2>
							<xsl:apply-templates
								select="/*/*/*[name()='meta']/*[name()='redazionale']/*[name()='nota']" />
						</div>
					</xsl:if>
					<xsl:if
						test="/*/*/*[name()='annessi']/*[name()='annesso']/*/*[name()='meta']/*[name()='redazionale']/*[name()='nota']">
						<div class="allineasx">
							&#160;
							<br />
							<h2>Note in allegati</h2>
							<xsl:apply-templates
								select="/*/*/*[name()='annessi']/*[name()='annesso']/*/*[name()='meta']/*[name()='redazionale']/*[name()='nota']" />

						</div>
					</xsl:if>
				</div>
				<div class="hrpiepagina">Copyright - ITTIG</div>


			</body>
		</html>
	</xsl:template>

	<xsl:template match="*[name()='annesso']" mode="indice">
		<div>
			&#160;
			<a href="#{@id}">
				<xsl:value-of
					select="*[name()='testata']/*[name()='denAnnesso']" />
			</a>
			&#160;
		</div>
	</xsl:template>


	<xsl:template
		match="*[name()='libro' or name()='parte' or name()='titolo' or name()='capo' or name()='sezione' or name()='paragrafo' or name()='partizione' or name()='articolo']"
		mode="indice">
		<xsl:choose>
			<xsl:when test="$datafine!=''">
				<xsl:variable name="inizio_id">
					<xsl:value-of select="@iniziovigore" />
				</xsl:variable>
				<xsl:variable name="fine_id">
					<xsl:value-of select="@finevigore" />
				</xsl:variable>
				<xsl:variable name="data_inizio">
					<xsl:value-of
						select="//*[name()='evento'][@id=$inizio_id]/@data" />
					<!--xsl:value-of select="id($inizio_id)/@data"/ -->
				</xsl:variable>
				<xsl:variable name="data_fine">
					<xsl:value-of
						select="//*[name()='evento'][@id=$fine_id]/@data" />
					<!--xsl:value-of select="id($fine_id)/@data"/ -->
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="$data_fine!=''">
						<xsl:if
							test="$data_inizio&lt;number(number($datafine)+1) and $data_fine&gt;$datafine">
							<xsl:call-template name="stampaindice" />
						</xsl:if>
					</xsl:when>
					<xsl:when test="$data_inizio!=''">
						<xsl:if
							test="$data_inizio&lt;number(number($datafine)+1)">
							<xsl:call-template name="stampaindice" />
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="stampaindice" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="stampaindice" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="stampaindice">
		<div>
			&#160;
			<a href="#{@id}">
				<xsl:value-of select="*[name()='num']/text()" />

				<xsl:if test="*[name()='rubrica']!=''">

					<xsl:if test="*[name()='num']!=''">
						<xsl:text>-</xsl:text>
					</xsl:if>
					<xsl:value-of select="*[name()='rubrica']" />
				</xsl:if>
			</a>
			<xsl:if
				test="*[name()='libro' or name()='parte' or name()='titolo' or name()='capo' or name()='sezione' or name()='paragrafo' or name()='partizione' or name()='articolo']">
				<div class="itemindice">
					&#160;
					<xsl:apply-templates
						select="*[name()='libro' or name()='parte' or name()='titolo' or name()='capo' or name()='sezione' or name()='paragrafo' or name()='partizione' or name()='articolo']"
						mode="indice" />
					&#160;
				</div>
			</xsl:if>


		</div>
	</xsl:template>



	<xsl:template match="text()">
		&#160;
		<xsl:value-of select="." />
		&#160;
	</xsl:template>

	<xsl:template match="/*[name()='NIR']/*">

		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates
							select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text>( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div>
					<xsl:choose>
						<xsl:when test="$datafine!=''">
							<xsl:variable name="fine_id">
								<xsl:value-of select="@finevigore" />
							</xsl:variable>
							<xsl:variable name="inizio_id">
								<xsl:value-of select="@iniziovigore" />
							</xsl:variable>
							<xsl:variable name="data_fine">
								<xsl:value-of
									select="//*[name()='evento'][@id=$fine_id]/@data" />
								<!--xsl:value-of select="id($fine_id)/@data"/ -->
							</xsl:variable>
							<xsl:choose>
								<xsl:when
									test="$data_fine&lt;number(number($datafine)+1)">
									<!--eccezione intera legge abrogata (o resa inapplicabile da corte 
										costituzionale -->
									<xsl:choose>
										<xsl:when
											test="//*[name()='evento'][@id=$fine_id]/@tipo='inapplicabilita'">
											<div class="art_abrogato">
												LEGGE INEFFICACE
											</div>
										</xsl:when>
										<xsl:otherwise>
											<div class="art_abrogato">
												LEGGE ABROGATA
											</div>
										</xsl:otherwise>
									</xsl:choose>

									<div class="abrogato">
										&#160; [
										<xsl:apply-templates
											select="*[name()='intestazione']" />
										]
										<a href="#n{@id}"
											name="t{@id}">
											<sup>
												[
												<xsl:value-of
													select="position()" />
												]
											</sup>
										</a>
									</div>
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="vigenza" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="multivigenza" />
						</xsl:otherwise>
					</xsl:choose>
				</div>

			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template
		match="/*[name()='NIR']/*/*[name()='formulainiziale']">
		<!--a name="{@id}">&#160;</a -->
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates
							select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text>( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="formulainiziale">
					<xsl:choose>
						<xsl:when test="$datafine!=''">
							<xsl:call-template name="vigenza" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="multivigenza" />
						</xsl:otherwise>
					</xsl:choose>
				</div>

			</xsl:otherwise>
		</xsl:choose>

		<hr />
	</xsl:template>

	<xsl:template
		match="/*/*/*[name()='meta']/*[name()='descrittori']/*[name()='materie']">
		<xsl:for-each select="*[name()='materia']">
			<xsl:value-of select="@valore" />
			<xsl:text>,</xsl:text>
		</xsl:for-each>
	</xsl:template>

	<!-- ======================================================== -->
	<!-- -->
	<!-- Template intestazione e relazione -->
	<!-- -->
	<!-- ======================================================== -->

	<xsl:template match="/*[name()='NIR']/*/*[name()='intestazione']">

		<div class="intestazione">

			<xsl:apply-templates select="*[name()='emanante']" />
			<xsl:apply-templates select="*[name()='tipoDoc']" />
			<xsl:text></xsl:text>
			<xsl:apply-templates select="*[name()='dataDoc']" />
			<xsl:text>, n.</xsl:text>
			<xsl:apply-templates select="*[name()='numDoc']" />
			<xsl:apply-templates select="*[name()='titoloDoc']" />



			<div class="meta">
				MATERIE:
				<xsl:apply-templates
					select="/*/*/*[name()='meta']/*[name()='descrittori']/*[name()='materie']" />
			</div>
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
				<xsl:text>Pubblicato su</xsl:text>
				<xsl:value-of select="$tipo" />
				<xsl:text>, n.&#160;</xsl:text>
				<xsl:value-of select="$num" />
				<xsl:if test="$data!=''">
					<xsl:text>del</xsl:text>
					<xsl:value-of
						select="concat(substring($data,7,2),'/',substring($data,5,2),'/',substring($data,1,4))" />
				</xsl:if>
			</div>
		</div>

		<xsl:if test="/*[name()='NIR']/*/*[name()='articolato']/*">
<!-- indice solo per i documenti con articolato -->


			<!-- non far vedere l'indice se e' abrogato per intero RIVEDERE fatto 
				velocemente -->
			<xsl:choose>
				<xsl:when test="$datafine!=''">
					<xsl:variable name="fine_id">
						<xsl:value-of
							select="/*[name()='NIR']/*/@finevigore" />
					</xsl:variable>
					<xsl:variable name="inizio_id">
						<xsl:value-of
							select="/*[name()='NIR']/*/@iniziovigore" />
					</xsl:variable>
					<xsl:variable name="data_fine">
						<xsl:value-of
							select="//*[name()='evento'][@id=$fine_id]/@data" />
						<!--xsl:value-of select="id($fine_id)/@data"/ -->
					</xsl:variable>
					<xsl:choose>
						<xsl:when
							test="$data_fine&lt;number(number($datafine)+1)">
						</xsl:when>
						<xsl:otherwise>
							<div id="indice">
								<h2>Indice</h2>
								<xsl:apply-templates
									select="/*[name()='NIR']/*/*[name()='articolato']/*"
									mode="indice" />
								<xsl:apply-templates
									select="/*[name()='NIR']/*/*[name()='annessi']/*"
									mode="indice" />
							</div>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<div id="indice">
						<h2>Indice</h2>
						<xsl:apply-templates
							select="/*[name()='NIR']/*/*[name()='articolato']/*"
							mode="indice" />
						<xsl:apply-templates
							select="/*[name()='NIR']/*/*[name()='annessi']/*" mode="indice" />
					</div>
				</xsl:otherwise>
			</xsl:choose>

		</xsl:if>


		<hr />
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

		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates
							select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text>( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="titoloDoc">
					&#160;
					<xsl:choose>
						<xsl:when test="$datafine!=''">
							<xsl:call-template name="vigenza" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="multivigenza" />
						</xsl:otherwise>
					</xsl:choose>
					&#160;
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>


	<xsl:template match="//*[name()='preambolo']">

		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates
							select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text>( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="preambolo">
					<xsl:choose>
						<xsl:when test="$datafine!=''">
							<xsl:call-template name="vigenza" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="multivigenza" />
						</xsl:otherwise>
					</xsl:choose>
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>




	<!-- ======================================================== -->
	<!-- -->
	<!-- Template articolato -->
	<!-- -->
	<!-- ======================================================== -->

	<xsl:template
		match="//*[name()='articolato'] | //*[name()='contenitore']">

		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates
							select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text>( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div style="width:100%;">
					&#160;
					<xsl:choose>
						<xsl:when test="$datafine!=''">
							<xsl:call-template name="vigenza" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="multivigenza" />
						</xsl:otherwise>
					</xsl:choose>
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!-- ========================== LIBRO============================== -->
	<xsl:template match="//*[name()='libro']">
		<a name="{@id}">&#160;</a>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates
							select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text>( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="libro">
					&#160;
					<xsl:choose>
						<xsl:when test="$datafine!=''">
							<xsl:call-template name="vigenza" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="multivigenza" />
						</xsl:otherwise>
					</xsl:choose>
					&#160;
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!-- ========================== PARTE============================== -->
	<xsl:template match="//*[name()='parte']">
		<a name="{@id}">&#160;</a>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates
							select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text>( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="parte">
					&#160;
					<xsl:choose>
						<xsl:when test="$datafine!=''">
							<xsl:call-template name="vigenza" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="multivigenza" />
						</xsl:otherwise>
					</xsl:choose>
					&#160;
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!-- ========================== TITOLO============================== -->
	<xsl:template match="//*[name()='titolo']">
		<a name="{@id}">&#160;</a>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates
							select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text>( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="titolo">
					&#160;
					<xsl:choose>
						<xsl:when test="$datafine!=''">
							<xsl:call-template name="vigenza" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="multivigenza" />
						</xsl:otherwise>
					</xsl:choose>
					&#160;
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!-- ========================== SEZIONE============================== -->
	<xsl:template match="//*[name()='sezione']">
		<a name="{@id}">&#160;</a>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates
							select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text>( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="sezione">
					&#160;
					<xsl:choose>
						<xsl:when test="$datafine!=''">
							<xsl:call-template name="vigenza" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="multivigenza" />
						</xsl:otherwise>
					</xsl:choose>
					&#160;
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!-- ========================== CAPO============================== -->
	<xsl:template match="//*[name()='capo']">
		<hr />
		<a name="{@id}">&#160;</a>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates
							select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text>( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="capo">
					<xsl:choose>
						<xsl:when test="$datafine!=''">
							<xsl:call-template name="vigenza" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="multivigenza" />
						</xsl:otherwise>
					</xsl:choose>
					&#160;
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!-- ========================== RUBRICA ============================== -->

	<xsl:template match="//*[name()='rubrica']">
		<p class="rubrica">
			<xsl:choose>
				<xsl:when test="$datafine!=''">
					<xsl:call-template name="vigenza" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="multivigenza" />
				</xsl:otherwise>
			</xsl:choose>
		</p>
	</xsl:template>

	<xsl:template match="//*[name()='decorazione']">
		<p class="decorazione">
			<xsl:apply-templates />
		</p>
	</xsl:template>



	<!-- =========================ARTICOLO=============================== -->

	<xsl:template match="//*[name()='articolo']">
		<a name="{@id}">&#160;</a>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates
							select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text>( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="articolo">
					&#160;
					<xsl:choose>
						<xsl:when test="$datafine!=''">
							<xsl:call-template name="vigenza" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="multivigenza" />
						</xsl:otherwise>
					</xsl:choose>
					&#160;
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!-- =========================COMMA e sotto comma=============================== -->

	<xsl:template match="//*[name()='comma']">
		<a name="{@id}">&#160;</a>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<p class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates
							select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text>( Omissis )</xsl:text>
				</p>
			</xsl:when>
			<xsl:otherwise>

				<div class="comma">
					&#160;
					<xsl:choose>
						<xsl:when test="$datafine!=''">
							<xsl:call-template name="vigenza" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="multivigenza" />
						</xsl:otherwise>
					</xsl:choose>
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template match="//*[name()='num']">
		<xsl:choose>
			<xsl:when
				test="parent::node()[name()='el'] or parent::node()[name()='en'] or parent::node()[name()='ep']">
				<em>
					<xsl:apply-templates />
					&#160;
				</em>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
				&#160;
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<xsl:template match="//*[name()='coda']">

		<p class="coda">
			<xsl:choose>
				<xsl:when test="$datafine!=''">
					<xsl:call-template name="vigenza" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="multivigenza" />
				</xsl:otherwise>
			</xsl:choose>
		</p>


	</xsl:template>



	<!-- =========================EL , EN , EP=============================== -->
	<xsl:template
		match="//*[name()='el'] | //*[name()='en'] | //*[name()='ep']">
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<a name="{@id}">&#160;</a>
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates
							select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text>( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="{local-name()}">
					&#160;
					<xsl:choose>
						<xsl:when test="$datafine!=''">
							<a name="{@id}">&#160;</a>
							<xsl:call-template name="vigenza" />
						</xsl:when>
						<xsl:otherwise>
							<a name="{@id}">&#160;</a>
							<xsl:call-template name="multivigenza" />
						</xsl:otherwise>
					</xsl:choose>
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>



	<xsl:template match="//*[name()='corpo']">

		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates
							select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text>( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<!--div class="corpo"> <xsl:choose> <xsl:when test="$datafine!=''"> <xsl:call-template 
					name="vigenza"/> </xsl:when> <xsl:otherwise> <xsl:call-template name="multivigenza"/> 
					</xsl:otherwise> </xsl:choose> </div -->
				<xsl:apply-templates />

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template match="//*[name()='alinea']">

		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates
							select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text>( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<!--div class="alinea"> <xsl:choose> <xsl:when test="$datafine!=''"> 
					<xsl:call-template name="vigenza"/> </xsl:when> <xsl:otherwise> <xsl:call-template 
					name="multivigenza"/> </xsl:otherwise> </xsl:choose> </div -->
				<xsl:apply-templates />

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!-- ======================================================== -->
	<!-- -->
	<!-- Template sotto il comma -->
	<!-- -->
	<!-- ======================================================== -->


	<xsl:template match="//*[name()='mmod']">
		<a name="{@id}">&#160;</a>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates
							select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text>( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="mmod">
					<xsl:choose>
						<xsl:when test="$datafine!=''">
							<xsl:call-template name="vigenza" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="multivigenza" />
						</xsl:otherwise>
					</xsl:choose>
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template match="//*[name()='mod']">
		<a name="{@id}">&#160;</a>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates
							select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text>( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="mod">
					<xsl:choose>
						<xsl:when test="$datafine!=''">
							<xsl:call-template name="vigenza" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="multivigenza" />
						</xsl:otherwise>
					</xsl:choose>
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template match="//*[name()='virgolette']">
		<a name="{@id}">&#160;</a>
		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates
							select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text>( Omissis )</xsl:text>
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
		<div style="display:inline" title="Nome: {.}">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="//*[name()='rifesterno']">
		<a href="http://www.normattiva.it/uri-res/N2Ls?{@xlink:href}"
			title="URN = {@xlink:href}">
			<xsl:value-of select="@xlink:href" />
		</a>
	</xsl:template>

	<xsl:template match="//*[name()='rif']">
		<xsl:variable name="url">
			<xsl:value-of select="@xlink:href" />
		</xsl:variable>
		<xsl:variable name="post">
			<xsl:value-of select="./parent::*" />
		</xsl:variable>
		<xsl:variable name="stringa">
			<xsl:value-of
				select="substring(substring-after($post,.),1,1)" />
		</xsl:variable>

		<xsl:variable name="riftilde">
			<xsl:choose>
				<xsl:when test="contains(@xlink:href, '#')">
					<xsl:value-of
						select="substring-before(@xlink:href, '#')" />
					~
					<xsl:value-of
						select="substring-after(@xlink:href, '#')" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@xlink:href" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>



		<xsl:choose>
			<xsl:when test="contains($url,'urn:')">
				<xsl:choose>
					<xsl:when
						test="$stringa='&#59;' or $stringa='&#46;' or $stringa='&#58;' or $stringa='&#44;'  or $stringa='&#45;'">
						<a
							href="http://www.normattiva.it/uri-res/N2Ls?{$riftilde}"
							title="URN = {$riftilde}">
							<xsl:apply-templates />
						</a>
					</xsl:when>
					<xsl:otherwise>
						<a
							href="http://www.normattiva.it/uri-res/N2Ls?{$riftilde}"
							title="URN = {$riftilde}">
							<xsl:apply-templates />
						</a>
						&#160;
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when
						test="$stringa='&#59;' or $stringa='&#46;' or $stringa='&#58;' or $stringa='&#44;'  or $stringa='&#45;'">
						<a href="{@xlink:href}"
							title="Destinazione: {@xlink:href}">
							<xsl:apply-templates />
						</a>
					</xsl:when>
					<xsl:otherwise>
						<a href="{@xlink:href}"
							title="Destinazione: {@xlink:href}">
							<xsl:apply-templates />
						</a>
						&#160;
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>


	<xsl:template match="//*[name()='data']">
		<div style="display:inline"
			title="Data: {concat(substring(@norm,7,2),'/',substring(@norm,5,2),'/',substring(@norm,1,4))}">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	<xsl:template match="//*[name()='ndr']">
		<xsl:variable name="numnogratella">
			<xsl:value-of
				select="substring(@num,2,string-length(@num))" />
		</xsl:variable>


		<sup class="ndr">
			<a name="{concat('ndr',$numnogratella)}" href="{@num}"
				title="Destinazione: {@num}">
				<xsl:attribute name="title">
					Nota:
					<xsl:value-of select="." />
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

		<xsl:choose>
			<xsl:when test="@status = 'omissis'">
				<div class="omissis">
					<xsl:apply-templates select="*[name()='num']" />
					<xsl:if test="*[name()='rubrica']">
						-
						<xsl:apply-templates
							select="*[name()='rubrica']/text()" />
					</xsl:if>
					<xsl:text>( Omissis )</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>

				<div class="formulafinale">
					<hr />
					<xsl:choose>
						<xsl:when test="$datafine!=''">
							<xsl:call-template name="vigenza" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="multivigenza" />
						</xsl:otherwise>
					</xsl:choose>
				</div>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template match="//*[name()='conclusione']">
		<div class="conclusione">
			&#160;
			<xsl:apply-templates />
		</div>
	</xsl:template>
	<xsl:template match="//*[name()='dataeluogo']">
		<p class="dataeluogo">
			<xsl:apply-templates />
		</p>
	</xsl:template>

	<xsl:template match="//*[name()='firma']">
		<xsl:choose>
			<xsl:when test="@tipo='visto'">
				<p class="visto">
					<xsl:choose>
						<xsl:when test="$datafine!=''">
							<xsl:call-template name="vigenza" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="multivigenza" />
						</xsl:otherwise>
					</xsl:choose>
				</p>
			</xsl:when>
			<xsl:otherwise>
				<p class="firma">
					<xsl:choose>
						<xsl:when test="$datafine!=''">
							<xsl:call-template name="vigenza" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="multivigenza" />
						</xsl:otherwise>
					</xsl:choose>
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
		<br />
		ALLEGATI:
		<xsl:apply-templates />
	</xsl:template>
	<xsl:template match="//*[name()='annesso']">
		<a name="{@id}">&#160;</a>
		<xsl:choose>
			<xsl:when test="$datafine!=''">
				<xsl:call-template name="vigenza" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="multivigenza" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="//*[name()='testata']">
		<hr />
		<xsl:apply-templates
			select="*[name()='denAnnesso'] | *[name()='titAnnesso']" />
		<br />
	</xsl:template>
	<xsl:template match="*[name()='denAnnesso']">
		<xsl:text>-</xsl:text>
		<b>
			<xsl:apply-templates select=".//text()" />
			&#160;
		</b>
		<xsl:text>-</xsl:text>
	</xsl:template>
	<xsl:template match="*[name()='titAnnesso']">
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
		<xsl:variable name="idnota">
			<xsl:value-of select="@id" />
		</xsl:variable>
		<xsl:variable name="idnotagratella">
			#
			<xsl:value-of select="@id" />
		</xsl:variable>


		<xsl:for-each
			select="//*[name()='ndr'][@num=$idnotagratella]">
			<a class="nota" name="{$idnota}"
				href="{concat('#ndr',$idnota)}">
				<xsl:value-of select="." />
			</a>
		</xsl:for-each>

		<span class="nota">
			<xsl:text>&#160;-&#160;</xsl:text>
		</span>
		<xsl:apply-templates />


	</xsl:template>


	<xsl:template match="//*[name()='confronto']" />

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
					<xsl:value-of
						select="concat('file://',$baseurl,$nome)" />
				</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="{name()}">
					<xsl:value-of select="." />
				</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="h:*">
		<xsl:element name="{local-name()}">
			<xsl:apply-templates select="@*" />
			<xsl:apply-templates />

		</xsl:element>
	</xsl:template>

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
		<xsl:value-of
			select="substring-before(substring-after(.,'&gt;'),'&lt;')" />
		&#160;
	</xsl:template>
	<!-- ======================================================== -->
	<!-- -->
	<!-- template span -->
	<!-- -->
	<!-- ======================================================== -->
	<xsl:template match="//*[name()='h:span']">
		&#160;
		<div style="display:inline">
			&#160;
			<xsl:choose>
				<xsl:when test="$datafine!=''">
					<xsl:call-template name="vigenza" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="multivigenza" />
				</xsl:otherwise>
			</xsl:choose>
		</div>
		&#160;
	</xsl:template>


	<!-- ======================================================== -->
	<!-- -->
	<!-- template p -->
	<!-- -->
	<!-- ======================================================== -->
	<xsl:template match="//*[name()='h:p']">
		<p>
			<xsl:choose>
				<xsl:when test="$datafine!=''">
					<xsl:call-template name="vigenza" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="multivigenza" />
				</xsl:otherwise>
			</xsl:choose>
		</p>
	</xsl:template>

	<!-- ======================================================== -->
	<!-- -->
	<!-- template gestione MULTIvigenze -->
	<!-- -->
	<!-- ======================================================== -->

	<xsl:template name="multivigenza">
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

			<xsl:value-of
				select="//*[name()='evento'][@id=$inizio_id]/@data" />
			<!--xsl:for-each select="key('dateventi',$inizio_id)"> <xsl:value-of select="@data"/> 
				</xsl:for-each -->
			<!--xsl:value-of select="key('dateventi',$inizio_id)" / -->
			<!--xsl:value-of select="id($inizio_id)/@data"/ -->
		</xsl:variable>
		<xsl:variable name="data_fine">

			<xsl:value-of
				select="//*[name()='evento'][@id=$fine_id]/@data" />
			<!--xsl:for-each select="key('dateventi',$fine_id)"> <xsl:value-of select="@data"/> 
				</xsl:for-each -->
			<!--xsl:value-of select="key('dateventi',$fine_id)" / -->
			<!--xsl:value-of select="id($fine_id)/@data"/ -->
		</xsl:variable>
		<xsl:variable name="giornoprima">
			<xsl:call-template name="finevigenza">
				<xsl:with-param name="datafinevigenza"
					select="$data_fine" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="tooltip">
			<xsl:choose>
				<xsl:when test="$data_fine!=''">
					In vigore&#160;
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
					&#160;al
					<xsl:value-of select="$giornoprima" />
					<xsl:choose>
						<xsl:when test="$stato!=''">
							(
							<xsl:value-of select="$stato" />
							)
						</xsl:when>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$data_inizio!=''">
					In vigore&#160;dal
					<xsl:value-of
						select="concat(substring($data_inizio,7,2),'/',substring($data_inizio,5,2),'/',substring($data_inizio,1,4))" />
				</xsl:when>
			</xsl:choose>
		</xsl:variable>


		<xsl:choose>

			<!-- ========================================== DATA FINE !='' ====================================== -->
			<xsl:when test="$data_fine!=''">
				<xsl:choose>
					<xsl:when test="$data_fine&lt;$datafine">
						<!--===================== n{@id}, t{@id}:'n' e 't' differenziano il 
							testo dalle note ===================== <xsl:value-of select="@id"/>: il valore 
							accanto a 'VigNota', l'id della partizione -->

						<xsl:variable name="id">
							<xsl:value-of select="@id" />
						</xsl:variable>
						<xsl:for-each
							select="//*[name()!='urn']/@iniziovigore">
							<xsl:if test="../@id=$id">
								<a href="#n{$id}" name="t{$id}">
									<sup>
										[
										<xsl:value-of
											select="position()" />
										]
									</sup>
								</a>
							</xsl:if>
						</xsl:for-each>


					</xsl:when>
					<xsl:when
						test="$data_inizio&gt;number(number($datafine)-1)">
						<div class="inserito">
							<xsl:attribute name="title">
								<xsl:copy-of select="$tooltip" />
							</xsl:attribute>
							[
							<xsl:apply-templates />
							]
							<xsl:variable name="id">
								<xsl:value-of select="@id" />
							</xsl:variable>
							<xsl:for-each
								select="//*[name()!='urn']/@iniziovigore">
								<xsl:if test="../@id=$id">
									<a href="#n{$id}" name="t{$id}">
										<sup>
											[
											<xsl:value-of
												select="position()" />
											]
										</sup>
									</a>
								</xsl:if>
							</xsl:for-each>
							&#160;
						</div>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="$stato!=''">
								<div class="abrogato">
									<xsl:attribute name="title">
										<xsl:copy-of select="$tooltip" />
									</xsl:attribute>
									[
									<xsl:apply-templates />
									]
									<xsl:variable name="id">
										<xsl:value-of select="@id" />
									</xsl:variable>
									<xsl:for-each
										select="//*[name()!='urn']/@iniziovigore">
										<xsl:if test="../@id=$id">
											<a href="#n{$id}"
												name="t{$id}">
												<sup>
													[
													<xsl:value-of
														select="position()" />
													]
												</sup>
											</a>
										</xsl:if>
									</xsl:for-each>
									&#160;
								</div>
							</xsl:when>
							<xsl:otherwise>
								<div class="abrogato">
									<xsl:attribute name="title">
										<xsl:copy-of select="$tooltip" />
									</xsl:attribute>
									[
									<xsl:apply-templates />
									]
									<xsl:variable name="id">
										<xsl:value-of select="@id" />
									</xsl:variable>
									<xsl:for-each
										select="//*[name()!='urn']/@iniziovigore">
										<xsl:if test="../@id=$id">
											<a href="#n{$id}"
												name="t{$id}">
												<sup>
													[
													<xsl:value-of
														select="position()" />
													]
												</sup>
											</a>
										</xsl:if>
									</xsl:for-each>
									&#160;
								</div>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<!-- ========================================== DATA inizio !='' ====================================== -->
			<xsl:when test="$data_inizio!=''">
				<xsl:choose>
					<xsl:when
						test="$data_inizio&gt;$datafine or $data_fine&lt;$datafine">
						<!--===================== n{@id}, t{@id}:'n' e 't' differenziano il 
							testo dalle note ===================== <xsl:value-of select="@id"/>: il valore 
							accanto a 'VigNota', l'id della partizione -->
						<xsl:variable name="id">
							<xsl:value-of select="@id" />
						</xsl:variable>
						<xsl:for-each
							select="//*[name()!='urn']/@iniziovigore">
							<xsl:if test="../@id=$id">
								<a href="#n{$id}" name="t{$id}">
									<sup>
										[
										<xsl:value-of
											select="position()" />
										]
									</sup>
								</a>
							</xsl:if>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<div class="inserito">
							<xsl:attribute name="title">
								<xsl:copy-of select="$tooltip" />
							</xsl:attribute>
							<xsl:apply-templates />
							<xsl:variable name="id">
								<xsl:value-of select="@id" />
							</xsl:variable>
							<xsl:for-each
								select="//*[name()!='urn']/@iniziovigore">
								<xsl:if test="../@id=$id">
									<a href="#n{$id}" name="t{$id}">
										<sup>
											[
											<xsl:value-of
												select="position()" />
											]
										</sup>
									</a>
								</xsl:if>
							</xsl:for-each>
							&#160;
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>


			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
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
				<xsl:when
					test="/*[name()='NIR']/@tipo!='multivigente'">
					<xsl:value-of
						select="/*[name()='NIR']/*/*[name()='meta']/*[name()='descrittori']/*[name()='urn']/@valore" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:for-each
						select="/*[name()='NIR']/*/*[name()='meta']/*[name()='descrittori']/*[name()='urn']">
						<xsl:choose>
							<xsl:when test="$datafine!=''">
								<xsl:variable name="inizio_id">
									<xsl:value-of
										select="@iniziovigore" />
								</xsl:variable>
								<xsl:variable name="fine_id">
									<xsl:value-of select="@finevigore" />
								</xsl:variable>
								<xsl:variable name="data_inizio">
									<xsl:value-of
										select="//*[name()='evento'][@id=$inizio_id]/@data" />
									<!--xsl:value-of select="id($inizio_id)/@data"/ -->
								</xsl:variable>
								<xsl:variable name="data_fine">
									<xsl:value-of
										select="//*[name()='evento'][@id=$fine_id]/@data" />
									<!--xsl:value-of select="id($fine_id)/@data"/ -->
								</xsl:variable>
								<xsl:choose>
									<xsl:when test="$data_fine!=''">
										<!-- aggiungo un caso: posso avere un intervallo che parte e termina 
											lo stesso giorno <xsl:if test="$data_inizio&lt;number(number($datafine)+1) 
											and $data_fine&gt;$datafine"> <xsl:value-of select="@valore" /> <br/> </xsl:if -->
										<xsl:choose>
											<xsl:when
												test="$data_inizio&lt;number(number($datafine)+1) and $data_fine&gt;$datafine">
												<xsl:value-of
													select="@valore" />
												<br />
											</xsl:when>
											<xsl:otherwise>
												<xsl:if
													test="$data_inizio=$datafine and $data_fine=$data_inizio=number(number($datafine)+1)">
													<xsl:value-of
														select="@valore" />
													<br />
												</xsl:if>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:when>
									<xsl:otherwise>
										<xsl:if
											test="$data_inizio&lt;number(number($datafine)+1)">
											<xsl:value-of
												select="@valore" />
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
	<!-- template gestione vigenze -->
	<!-- -->
	<!-- ======================================================== -->

	<xsl:template name="vigenza">
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

		<!--NON LO USO + PER IL TEST <xsl:variable name="data_entratainvigore"> 
			<xsl:value-of select="//*[name()='evento'][@fonte='ro1']/@data"/> </xsl:variable> -->

		<xsl:variable name="data_inizio">
			<xsl:value-of
				select="//*[name()='evento'][@id=$inizio_id]/@data" />
			<!--xsl:value-of select="id($inizio_id)/@data"/ -->
		</xsl:variable>
		<xsl:variable name="data_fine">
			<xsl:value-of
				select="//*[name()='evento'][@id=$fine_id]/@data" />
			<!--xsl:value-of select="id($fine_id)/@data"/ -->
		</xsl:variable>
		<xsl:variable name="giornoprima">
			<xsl:call-template name="finevigenza">
				<xsl:with-param name="datafinevigenza"
					select="$data_fine" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="tooltip">
			<xsl:choose>
				<xsl:when test="$data_fine!=''">
					In vigore&#160;
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
					&#160;al
					<xsl:value-of select="$giornoprima" />
					<xsl:choose>
						<xsl:when test="$stato!=''">
							(
							<xsl:value-of select="$stato" />
							)
						</xsl:when>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$data_inizio!=''">
					In vigore&#160;dal
					<xsl:value-of
						select="concat(substring($data_inizio,7,2),'/',substring($data_inizio,5,2),'/',substring($data_inizio,1,4))" />
				</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<xsl:choose>

			<!-- ========================================== DATA FINE !='' ====================================== -->
			<xsl:when test="$data_fine!=''">
				<xsl:variable name="ittignota">
					<xsl:value-of
						select="/*[name()='NIR']/*/*[name()='meta']/*[name()='disposizioni']/*[name()='modifichepassive']/*/*/*[name()='dsp:pos'][@xlink:href=$idnota]/../../*[name()='dsp:norma']/*[name()='dsp:subarg']/*[name()='ittig:notavigenza']/@id" />
				</xsl:variable>
				<xsl:choose>
					<xsl:when
						test="$data_fine&lt;number(number($datafine)+1)">

						<xsl:choose>
							<xsl:when
								test="following-sibling::node()[1][@iniziovigore=$fine_id]">
							</xsl:when>


							<!-- <xsl:when test="preceding-sibling::node()[1][@iniziovigore=$fine_id]"> 
								</xsl:when> -->
							<xsl:otherwise>


								<!-- ************ <span class="abrogato"> <xsl:attribute name="title"> 
									<xsl:copy-of select="$tooltip" /> </xsl:attribute> [ ... ] <a href="#n{@id}" 
									name="t{@id}"> <sup>{<xsl:value-of select="substring($ittignota,4,number(string-length($ittignota)))"/>}</sup> 
									</a> </span> -->
								<div class="abrogato">
									<xsl:attribute name="title">
										<xsl:copy-of select="$tooltip" />
									</xsl:attribute>
									<xsl:choose>
										<xsl:when
											test="*[name()='num']">
											[
											<xsl:apply-templates
												select="*[name()='num']" />
											]
										</xsl:when>
										<xsl:otherwise>
											[ ... ]
										</xsl:otherwise>
									</xsl:choose>
									<xsl:variable name="id">
										<xsl:value-of select="@id" />
									</xsl:variable>
									<xsl:for-each
										select="//*[name()!='urn']/@iniziovigore">
										<xsl:if test="../@id=$id">
											<a href="#n{$id}"
												name="t{$id}">
												<sup>
													[
													<xsl:value-of
														select="position()" />
													]
												</sup>
											</a>
										</xsl:if>
									</xsl:for-each>
									&#160;
								</div>





							</xsl:otherwise>
						</xsl:choose>

					</xsl:when>
					<xsl:when
						test="$data_inizio&lt;number(number($datafine)+1) and $data_fine&gt;$datafine">
						<xsl:choose>
							<xsl:when
								test="$data_inizio and $inizio_id!='t1'">
								<div class="inserito">
									<xsl:attribute name="title">
										<xsl:copy-of select="$tooltip" />
									</xsl:attribute>
									<xsl:apply-templates />
									<xsl:variable name="id">
										<xsl:value-of select="@id" />
									</xsl:variable>
									<xsl:for-each
										select="//*[name()!='urn']/@iniziovigore">
										<xsl:if test="../@id=$id">
											<a href="#n{$id}"
												name="t{$id}">
												<sup>
													[
													<xsl:value-of
														select="position()" />
													]
												</sup>
											</a>
										</xsl:if>
									</xsl:for-each>
									&#160;
								</div>
							</xsl:when>
							<xsl:otherwise>
								<xsl:apply-templates />
								<xsl:variable name="id">
									<xsl:value-of select="@id" />
								</xsl:variable>
								<xsl:for-each
									select="//*[name()!='urn']/@iniziovigore">
									<xsl:if test="../@id=$id">
										<a href="#n{$id}"
											name="t{$id}">
											<sup>
												[
												<xsl:value-of
													select="position()" />
												]
											</sup>
										</a>
									</xsl:if>
								</xsl:for-each>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
			<!-- ========================================== DATA inizio !='' ====================================== -->
			<xsl:when test="$data_inizio!=''">
				<xsl:choose>
					<xsl:when test="$data_inizio&gt;$datafine">
						<!--===================== n{@id}, t{@id}:'n' e 't' differenziano il 
							testo dalle note ===================== <xsl:value-of select="@id"/>: il valore 
							accanto a 'VigNota', l'id della partizione -->
					</xsl:when>
					<xsl:otherwise>
						<div class="inserito">
							<xsl:attribute name="title">
								<xsl:copy-of select="$tooltip" />
							</xsl:attribute>
							<xsl:apply-templates />


							<xsl:variable name="id">
								<xsl:value-of select="@id" />
							</xsl:variable>
							<xsl:for-each
								select="//*[name()!='urn']/@iniziovigore">
								<xsl:if test="../@id=$id">
									<a href="#n{$id}" name="t{$id}">
										<sup>
											[
											<xsl:value-of
												select="position()" />
											]
										</sup>
									</a>
								</xsl:if>
							</xsl:for-each>
							&#160;
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>

			<xsl:otherwise>
				<xsl:apply-templates />
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
				<xsl:when test="$saltare"></xsl:when>
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
							<xsl:with-param name="datafinevigenza"
								select="$data_fine" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:variable name="fonte">
						<xsl:choose>
							<xsl:when test="$fine_id!=''">
								<xsl:value-of
									select="id($fine_id)/@fonte" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of
									select="id($inizio_id)/@fonte" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:variable name="urn">
						<xsl:value-of select="id($fonte)/@xlink:href" />
					</xsl:variable>

					<xsl:variable name="urn_tilde">

						<xsl:choose>
							<xsl:when test="contains($urn, '#')">
								<xsl:value-of
									select="substring-before($urn, '#')" />
								~
								<xsl:value-of
									select="substring-after($urn, '#')" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$urn" />
							</xsl:otherwise>
						</xsl:choose>

					</xsl:variable>

					<xsl:choose>
						<xsl:when test="$numeronota=1">
							<br />
							<h2>Note sulla vigenza</h2>
						</xsl:when>
					</xsl:choose>
					<div class="paragrafo">
						<xsl:choose>
							<xsl:when
								test="/*[name()='NIR']/*/*[name()='meta']/*[name()='disposizioni']/*[name()='modifichepassive']/*/*/*[name()='dsp:pos'][@xlink:href=$id]/../../*[name()='dsp:norma']">
								<!--ho le informazione nei metadati-->
								<xsl:for-each
									select="/*[name()='NIR']/*/*[name()='meta']/*[name()='disposizioni']/*[name()='modifichepassive']/*/*/*[name()='dsp:pos'][@xlink:href=$id]/../../*[name()='dsp:norma']">
									<xsl:variable name="implicita">
										<xsl:value-of
											select="../@implicita" />
									</xsl:variable>
									<xsl:variable name="urn_meta">
										<xsl:value-of
											select="../*[name()='dsp:norma']/*[name()='dsp:pos']/@xlink:href" />
									</xsl:variable>
									<xsl:variable
										name="urn_meta_tilde">

										<xsl:choose>
											<xsl:when
												test="contains($urn_meta, '#')">
												<xsl:value-of
													select="substring-before($urn_meta, '#')" />
												~
												<xsl:value-of
													select="substring-after($urn_meta, '#')" />
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of
													select="$urn_meta" />
											</xsl:otherwise>
										</xsl:choose>

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
										<a name="n{$idNoPound}"
											href="#t{$idNoPound}">
											[
											<xsl:value-of
												select="$numeronota" />
											]
										</a>
									</xsl:if>

									<xsl:text>-</xsl:text>
									<xsl:choose>
										<xsl:when test="$novellando">
											<xsl:choose>
												<xsl:when
													test="$novella">
													<!--sostituzione-->
													Sostituzione
												</xsl:when>
												<xsl:otherwise>
													<!--abrogazione o annullamento della cassazione-->
													<xsl:choose>
														<!--
															<xsl:when test="id(id(substring($novellando,2,number(string-length($novellando))))/@finevigore)/@tipo='inefficacia'">
														-->
														<xsl:when
															test="//*[name()='evento'][@id=//*[@id=substring($novellando,2,number(string-length($novellando)))]/@finevigore]/@tipo='inapplicabilita'">
															Reso
															inefficace
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
												<xsl:when
													test="$novella">
													<!--integrazione-->
													Integrazione
												</xsl:when>
											</xsl:choose>
										</xsl:otherwise>
									</xsl:choose>
									<xsl:if test="$implicita!='no'">
										<xsl:text>implicita</xsl:text>
									</xsl:if>

									<xsl:if test="$novellando">
										<xsl:if test="$novella">
											<!--sostituzione-->
											<xsl:if
												test="$novella=$id">
												(testo inserito)
											</xsl:if>
											<xsl:if
												test="$novellando=$id">
												(testo eliminato)
											</xsl:if>
										</xsl:if>
									</xsl:if>
									<xsl:text>da:</xsl:text>
									<a
										href="http://www.normattiva.it/uri-res/N2Ls?{$urn_meta_tilde}"
										title="URN = {$urn_meta_tilde}">
										<xsl:value-of
											select="$autonota" />
									</a>
									<xsl:text>.</xsl:text>
								</xsl:for-each>
							</xsl:when>
							<xsl:otherwise>
								<!--NON ho le informazione nei metadati -->
								<a name="n{$idNoPound}"
									href="#t{$idNoPound}">
									[
									<xsl:value-of select="$numeronota" />
									]
								</a>
								<xsl:text>- Modificato da:</xsl:text>
								<a
									href="http://www.normattiva.it/uri-res/N2Ls?{$urn_tilde}"
									title="URN = {$urn_tilde}">
									<xsl:value-of select="$urn_tilde" />
								</a>
								<xsl:text>.</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
						<xsl:choose>
							<!-- ================= data_fine!='' =========-->
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

								<!--xsl:choose>
									<xsl:when test="$stato!=''">
									(<xsl:value-of select="$stato"/>)
									</xsl:when>
									</xsl:choose-->

							</xsl:when>
							<!-- ================= data_inizio!='' =========-->
							<xsl:when test="$data_inizio!=''">
								&#160;In vigoredal
								<xsl:value-of
									select="concat(substring($data_inizio,7,2),'/',substring($data_inizio,5,2),'/',substring($data_inizio,1,4))" />
							</xsl:when>
						</xsl:choose>

					</div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>




	<!-- ======================================================== -->
	<!-- -->
	<!-- template TimeLine -->
	<!-- -->
	<!-- ======================================================== -->

	<xsl:template name="TimeLine">
		<div
			style="font-weight: bold; font-style: normal; font-size: medium; text-align: right">
			<xsl:call-template name="urnvigente" />
		</div>
		<!-- OMESSA LA TIMELINE DI JTEST CON I RIFERIMENTI DEI DOC MODIFICANTI   -->
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
							<xsl:value-of
								select="number(number($mesefinevigenza)-1)" />
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

</xsl:stylesheet>
