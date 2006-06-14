<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"  xmlns:nir = "http://www.normeinrete.it/nir/2.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  xmlns="http://www.w3.org/HTML/1998/html4" xmlns:h="http://www.w3.org/HTML/1998/html4" xmlns:xlink="http://www.w3.org/1999/xlink">
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
					<xsl:value-of select="/*[name()='NIR']/*/*/*[name()='tipoDoc']"/>&#160;
					n. &#160;<xsl:value-of select="/*[name()='NIR']/*/*/*[name()='numDoc']"/>	
					&#160; del &#160; <xsl:value-of select="/*[name()='NIR']/*/*/*[name()='dataDoc']"/>
				</title>
				<meta http-equiv="Content-Type" content="text/html"/>
				<link href="nir-generico-style.css" rel="stylesheet"/>			
				
			</head>
			<body>
				<div class="intestazione">
				<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='intestazione']" />
				</div>
				<hr/>
				<div class="formulainiziale">
		       	<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='formulainiziale']" />
		       	</div>
            	<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='articolato']/* | /*[name()='NIR']/*/*[name()='contenitore']|/*[name()='NIR']/*/*[name()='gerarchia']" />
			  	<div class="formulafinale">
	            	<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='formulafinale']" />
   		       	</div>
 			       	<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='conclusione']" /> 
            	<div class="meta">
            		<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='meta']" />
            	</div>
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
	<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="nir:emanante">
		<div class="title">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="//*[name()='titoloDoc']">
				<div class="title">
					<xsl:apply-templates/>
				</div>
	</xsl:template>	
	
	<xsl:template match="//*[name()='preambolo']">
				<div class="preambolo">
					<xsl:apply-templates/>
				</div>
	</xsl:template>	
	<!-- ======================================================== -->
	<!--                                                          -->
	<!--  Template articolato                                     -->
	<!--                                                          -->
	<!-- ======================================================== -->
	
	<xsl:template match="//*[name()='articolato']">
		<table border="0" cellpadding="0" cellspacing="10" width="100%">			
					<xsl:apply-templates/>
		</table>
	</xsl:template>
	<!-- ========================== 	CAPO	============================== -->
	<xsl:template match="//*[name()='capo']">
		<hr />
		<a name="{@id}"/>		
		<p class="capo" name="{@id}">
			<xsl:call-template name="vigenza"/>
		</p>
	</xsl:template>
	
	<!-- ========================== 	RUBRICA	 	============================== -->
	
	<xsl:template match="//*[name()='rubrica']">
		<p class="rubrica">
			<xsl:apply-templates />
		</p>
	</xsl:template>	

	
<!--	<xsl:template match="nir:*">
				<div class="{local-name()}">
						<xsl:apply-templates select="nir:num">
						</xsl:apply-templates>
						<xsl:text>&#160;</xsl:text>
						<xsl:apply-templates select="nir:rubrica | nir:corpo| nir:alinea">
						</xsl:apply-templates>
				</div>
		<xsl:apply-templates />
	</xsl:template> -->
	
	
	<!-- =========================	ARTICOLO	=============================== -->
	
	<xsl:template match="//*[name()='articolo']">
		<a name="{@id}"/>
		<div class="articolo" name="{@id}">
			<xsl:call-template name="vigenza"/>
		</div>
	</xsl:template>
	
<!--	<xsl:template match="nir:comma | nir:el | nir:en | nir:ep">
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
	</xsl:template> -->
	<!-- =========================	COMMA e sotto comma	=============================== -->
	<xsl:template match="//*[name()='comma']">
		<a name="{@id}"/>
		<p class="comma" name="{@id}">
			<xsl:call-template name="vigenza" />
		</p>
	</xsl:template>

	<xsl:template match="//*[name()='num']">
		<xsl:choose>
			<xsl:when test="parent::node()[name()='el'] or parent::node()[name()='en'] or parent::node()[name()='ep']">
				<em><xsl:apply-templates />&#160;</em>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />&#160;
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- =========================	EL , EN , EP	=============================== -->
	<xsl:template match="//*[name()='el'] | //*[name()='en'] | //*[name()='ep']">
	<a name="{@id}"/>		
	<p class="{local-name()}" name="{@id}">
		<xsl:call-template name="vigenza"/>	
	</p>			

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
	<xsl:template match="//*[name()='nome']">
		<span title="Nome: {.}">
			<xsl:apply-templates/>
		</span>
	</xsl:template>
	<xsl:template match="//*[name()='rif']">
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
			<xsl:when test="contains($url,'urn:nir:')">
				<xsl:choose>
					<xsl:when test="$strina='&#59;' or $strina='&#46;' or $strina='&#58;' or $strina='&#44;'  or $strina='&#45;'">		 
						<a href="http://www.nir.it/cgi-bin/N2Ln?{@xlink:href}" title="Destinazione: http://www.nir.it/cgi-bin/N2Ln?{@xlink:href}">
							<xsl:apply-templates/>
						</a>
					</xsl:when>
					<xsl:otherwise>					 
						<a href="http://www.nir.it/cgi-bin/N2Ln?{@xlink:href}" title="Destinazione: http://www.nir.it/cgi-bin/N2Ln?{@xlink:href}">						
							<xsl:apply-templates/>						
						</a>&#160;
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$strina='&#59;' or $strina='&#46;' or $strina='&#58;' or $strina='&#44;'  or $strina='&#45;'">		 
						<a href="{@xlink:href}" title="Destinazione: {@xlink:href}">
							<xsl:apply-templates/>
						</a>
					</xsl:when>
					<xsl:otherwise>					 
						<a href="{@xlink:href}" title="Destinazione: {@xlink:href}">						
						<xsl:apply-templates/>						
						</a>&#160;
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>
	<xsl:template match="//*[name()='data']">
		<span title="Data: {concat(substring(@norm,7,2),'/',substring(@norm,5,2),'/',substring(@norm,1,4))}">
			<xsl:apply-templates/>
		</span>
	</xsl:template>
	<xsl:template match="//*[name()='ndr']">
		<a href="#{@num}" title="Destinazione: {@num}">
			<sup class="ndr">
				<xsl:attribute name="title">Nota: <xsl:value-of select="."/></xsl:attribute>
				[<xsl:value-of select="@value"/>]
			</sup>
		</a>
	</xsl:template>
	
	<!-- ======================================================== -->
	<!--                                                          -->
	<!--  Template conclusioni e formula finale                   -->
	<!--                                                          -->
	<!-- ======================================================== -->
	<xsl:template match="//*[name()='formulafinale']">
		<div class="formulafinale">
			<hr />
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	
	<xsl:template match="//*[name()='conclusione']">
		<div class="conclusione">
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	<xsl:template match="//*[name()='dataeluogo']">
		<p class="dataeluogo">
			<xsl:apply-templates/>
		</p>
	</xsl:template>
	<xsl:template match="//*[name()='sottoscrizioni']">
		<ul class="sottoscrizioni">
			<xsl:apply-templates/>
		</ul>
	</xsl:template>
	<xsl:template match="//*[name()='sottoscrivente']">
		<li>
			<xsl:apply-templates/>
		</li>
	</xsl:template>
	<xsl:template match="//*[name()='visto']">
		<p class="visto">
			<xsl:apply-templates/>
		</p>
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
	<xsl:template match="//*[name()='meta']">
		<hr/>
		<table border="1" cellpadding="2" cellspacing="0" width="75%" style="margin-left: 15px;">
			<xsl:apply-templates/>
		</table>
	</xsl:template>
<!--	<xsl:template match="//*[name()='urn']">
		<tr>
			<td class="small">
				<a href="http://www.senato.it/japp/bgt/showdoc/frame.jsp?tipodoc={//nir:approvazione/@tipodoc}&amp;leg={//nir:approvazione/@leg}&amp;id={//nir:approvazione/@internal_id}">
					<xsl:value-of select="."/>
				</a>
			</td>
		</tr>
	</xsl:template>-->
	<xsl:template match="//*[name()='redazionale']">
		<div class="redazionale">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	<xsl:template match="//*[name()='nota']">
		<a class="nota" name="{@id}" >
		<xsl:apply-templates />
		</a>
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
	<!-- ======================================================== -->
	<!--                                                          -->
	<!--  template riferimenti incompleti                         -->
	<!--                                                          -->
	<!-- ======================================================== -->
	<xsl:template match="processing-instruction('rif')">
		<xsl:value-of select="substring-before(substring-after(.,'&gt;'),'&lt;')" />&#160;
	</xsl:template>
	<!-- ======================================================== -->
	<!--                                                          -->
	<!--  template span (vigenze)	                              -->
	<!--                                                          -->
	<!-- ======================================================== -->
	<xsl:template match="//*[name()='h:span']">
		<span>
		<xsl:call-template name="vigenza"/>		
		</span>
	</xsl:template>	
<xsl:template name="vigenza" >
		<xsl:variable name="stato">
		<xsl:value-of select="@status" />
		</xsl:variable>
		<xsl:variable name="inizio_id">
			<xsl:value-of select="@iniziovigore"/>
		</xsl:variable>
		<xsl:variable name="fine_id">
			<xsl:value-of select="@finevigore"/>
		</xsl:variable>		
		<xsl:variable name="data_inizio">
			<xsl:value-of select="//*[name()='evento'][@id=$inizio_id]/@data"/>
		</xsl:variable>
		<xsl:variable name="data_fine">
			<xsl:value-of select="//*[name()='evento'][@id=$fine_id]/@data"/>
		</xsl:variable>
		<xsl:variable name="nome">
			<xsl:value-of select="translate(substring(.,1,10),' ','')"/>
		</xsl:variable>	
		<xsl:choose>
		<!-- ========================================== DATA FINE !='' ====================================== -->
			<xsl:when test="$data_fine!=''">
				<xsl:choose>			
					<xsl:when test="$stato!=''">
						<span style="color:#f00;" title="{$stato}"><xsl:apply-templates /> 
						</span>
					</xsl:when>
					<xsl:otherwise>
						<span style="color:#f00;" title="abrogato"><xsl:apply-templates />
						</span>
					</xsl:otherwise>
				</xsl:choose>
					<span style="color:#000;font-size:95%;font-style:italic;">
						&#91;Ndr:&#160;In vigore&#160;
					 		<xsl:choose>
							<xsl:when test="$data_inizio!=''">
								dal <xsl:value-of select="concat(substring($data_inizio,7,2),'/',substring($data_inizio,5,2),'/',substring($data_inizio,1,4))"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>fino</xsl:text>
							</xsl:otherwise>
						</xsl:choose>	
						&#160;al <xsl:value-of select="concat(substring($data_fine,7,2),'/',substring($data_fine,5,2),'/',substring($data_fine,1,4))"/>
						<xsl:choose>			
							<xsl:when test="$stato!=''">
								(<xsl:value-of select="$stato"/>)
							</xsl:when>
						</xsl:choose>		
						<xsl:text>&#93;&#160;</xsl:text>
					</span>	
			</xsl:when>
			<!-- ========================================== DATA inizio !='' ====================================== -->
			<xsl:when test="$data_inizio!=''">
				<xsl:choose>			
					<xsl:when test="$stato!=''">				
						<span style="color:#060;" title="{$stato}">
							<xsl:apply-templates />
						</span>
					</xsl:when>
					<xsl:otherwise>
						<span style="color:#060;" title="vigente">
							<xsl:apply-templates />
						</span>
					</xsl:otherwise>
				</xsl:choose>
				<span style="color:#000;font-size:95%;font-style:italic;">
					&#91;Ndr:&#160;In vigore&#160;
					dal <xsl:value-of select="concat(substring($data_inizio,7,2),'/',substring($data_inizio,5,2),'/',substring($data_inizio,1,4))"/>
					&#93;
				</span>	
			</xsl:when>
			<xsl:otherwise>
			<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
</xsl:template>
</xsl:stylesheet>
