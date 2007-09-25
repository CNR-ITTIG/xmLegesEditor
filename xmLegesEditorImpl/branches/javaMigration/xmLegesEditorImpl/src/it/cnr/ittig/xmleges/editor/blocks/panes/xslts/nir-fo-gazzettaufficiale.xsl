<?xml version="1.0" encoding="iso-8859-1"?>
<!--                                                         -->
<!--     Stylesheet XSLT                                     -->
<!--     Trasforma un documento XML in un documento XSL-FO   -->
<!--     DTD Light                                           -->
<!--                                                         -->
<!--     Andrea Marchetti - IAT/CNR                          -->
<!--     Release 31/01/2001                                  -->
<!--                                                         -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:h="http://www.w3.org/HTML/1998/html4" xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:dsp="http://www.normeinrete.it/nir/disposizioni/2.2" xmlns:nir="http://www.normeinrete.it/nir/2.2">

	<xsl:output method="xml" encoding="ISO-8859-1"/>


	<xsl:template match="/">
		<fo:root>
			<xsl:comment>Formato FO di un atto normativo</xsl:comment>
			<fo:layout-master-set>
				<!-- page size A4 
              orientation portrait -->

				<!-- FIRST PAGE                                               -->
				<fo:simple-page-master master-name="firstPage" xsl:use-attribute-sets="oddPageStyle">
					<fo:region-body xsl:use-attribute-sets="bodyStyle"/>
					<fo:region-before xsl:use-attribute-sets="headerStyle"/>
					<fo:region-after xsl:use-attribute-sets="footerStyle"/>
				</fo:simple-page-master>
				<!--                                                          -->

				<!-- EVEN PAGE                                                -->
				<fo:simple-page-master master-name="evenPage" xsl:use-attribute-sets="evenPageStyle">
					<fo:region-body xsl:use-attribute-sets="bodyStyle"/>
					<fo:region-before xsl:use-attribute-sets="headerStyle"/>
					<fo:region-after xsl:use-attribute-sets="footerStyle"/>
				</fo:simple-page-master>
				<!--                                                          -->

				<!-- ODD PAGE                                                 -->
				<fo:simple-page-master master-name="oddPage" xsl:use-attribute-sets="oddPageStyle">
					<fo:region-body xsl:use-attribute-sets="bodyStyle"/>
					<fo:region-before xsl:use-attribute-sets="headerStyle"/>
					<fo:region-after xsl:use-attribute-sets="footerStyle"/>
				</fo:simple-page-master>
				<!--                                                          -->

				<!--                                                          -->
				<fo:page-sequence-master master-name="attoSequence">
					<fo:repeatable-page-master-alternatives>
						<fo:conditional-page-master-reference master-reference="firstPage" page-position="first"/>
						<fo:conditional-page-master-reference master-reference="oddPage" odd-or-even="odd"/>
						<fo:conditional-page-master-reference master-reference="evenPage" odd-or-even="even"/>
					</fo:repeatable-page-master-alternatives>
				</fo:page-sequence-master>
				<!--                                                          -->
			</fo:layout-master-set>

	         <fo:page-sequence master-reference="attoSequence"
                        initial-page-number="1" 
                        font-family="Times" font-size="9pt"
                        text-align="justify" text-indent="2mm" >
                        
         <fo:static-content flow-name="xsl-region-before">
         <!--
            <fo:block font-weight="bold">
               <fo:inline ><xsl:value-of select="$headTitle" /></fo:inline>
               <fo:inline margin-left="1cm"><xsl:call-template name="estremiAtto"/></fo:inline>
               <fo:inline margin-left="2cm"><fo:page-number/></fo:inline>
            </fo:block>
         -->
         
            <fo:list-block provisional-distance-between-starts="4.5in"
                           provisional-label-separation="0pt"
                           >
              <fo:list-item>
                 <fo:list-item-label end-indent="label-end()">
                   <fo:block text-align="start" 
                             font-weight="bold">
                     <xsl:value-of select="$headTitle"/>
                   </fo:block>
                 </fo:list-item-label>
                 <fo:list-item-body start-indent="body-start()">
                    <!-- fo:block -->
                      <fo:block text-align="center" font-weight="bold">
                         <xsl:call-template name="estremiAtto"/>
                      </fo:block>
                      <fo:block text-align="end" font-weight="bold">
                         <fo:page-number/>
                      </fo:block>
                    <!-- /fo:block -->
                 </fo:list-item-body>
      
              </fo:list-item>
            </fo:list-block>
         </fo:static-content>
         
         <fo:flow flow-name="xsl-region-body">
            <xsl:call-template name="intestazione" />
            <xsl:call-template name="norma" />
         </fo:flow>
      </fo:page-sequence>	
			<!--
         <xsl:call-template name="intestazione"/>
         <xsl:call-template name="norma"/>
      -->
		</fo:root>
	</xsl:template>

	<!-- xsl:include href="styleFO.xsl" /-->
	<!-- Page Size A4 -->
	<xsl:param name="paper-height">29.7cm</xsl:param>
	<xsl:param name="paper-width">21.0cm</xsl:param>

	<!-- Margini di stampa -->
	<xsl:param name="up">2.0cm</xsl:param>
	<!-- 1.4 -->
	<xsl:param name="side1">2.0cm</xsl:param>
	<!-- 3.0 -->
	<xsl:param name="side2">4.0cm</xsl:param>
	<!-- 5.0 -->
	<xsl:param name="down">2.0cm</xsl:param>
	<!-- 6.0 -->

	<!-- Intestazione -->
	<xsl:param name="headTitle">Progetto Norme In Rete (NIR)</xsl:param>

	<xsl:attribute-set name="oddPageStyle">
		<xsl:attribute name="page-width">
			<xsl:value-of select="$paper-width"/>
		</xsl:attribute>
		<xsl:attribute name="page-height">
			<xsl:value-of select="$paper-height"/>
		</xsl:attribute>
		<!--<xsl:attribute name="reference-orientation">0</xsl:attribute> -->
		<xsl:attribute name="writing-mode">lr-tb</xsl:attribute>
		<xsl:attribute name="margin-right">
			<xsl:value-of select="$side1"/>
		</xsl:attribute>
		<xsl:attribute name="margin-left">
			<xsl:value-of select="$side2"/>
		</xsl:attribute>
		<xsl:attribute name="margin-bottom">
			<xsl:value-of select="$down"/>
		</xsl:attribute>
		<xsl:attribute name="margin-top">
			<xsl:value-of select="$up"/>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="evenPageStyle">
		<xsl:attribute name="page-width">
			<xsl:value-of select="$paper-width"/>
		</xsl:attribute>
		<xsl:attribute name="page-height">
			<xsl:value-of select="$paper-height"/>
		</xsl:attribute>
		<!--<xsl:attribute name="reference-orientation">0</xsl:attribute>-->
		<xsl:attribute name="writing-mode">lr-tb</xsl:attribute>
		<xsl:attribute name="margin-right">
			<xsl:value-of select="$side2"/>
		</xsl:attribute>
		<xsl:attribute name="margin-left">
			<xsl:value-of select="$side1"/>
		</xsl:attribute>
		<xsl:attribute name="margin-bottom">
			<xsl:value-of select="$down"/>
		</xsl:attribute>
		<xsl:attribute name="margin-top">
			<xsl:value-of select="$up"/>
		</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="headerStyle">
		<xsl:attribute name="extent">10mm</xsl:attribute>
		<xsl:attribute name="border-bottom">0mm solid gray</xsl:attribute>
		<xsl:attribute name="padding-bottom">0mm</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="footerStyle">
		<xsl:attribute name="extent">10mm</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="bodyStyle">
		<xsl:attribute name="column-count">2</xsl:attribute>
		<xsl:attribute name="column-gap">5mm</xsl:attribute>
		<xsl:attribute name="margin-top">20mm</xsl:attribute>
		<xsl:attribute name="margin-bottom">20mm</xsl:attribute>
	</xsl:attribute-set>


	<xsl:template name="intestazione">
		<fo:block>
			<xsl:call-template name="estremiAtto"/>
		</fo:block>
		<fo:block font-weight="bold" space-before="5mm" space-after="5mm">
			<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='intestazione']/*[name()='titoloDoc']"/>
		</fo:block>
	</xsl:template>

	<xsl:template name="estremiAtto">
		<xsl:value-of select="/*[name()='NIR']/*/*[name()='intestazione']/*[name()='tipoDoc']"/>
		<xsl:text> del </xsl:text>
		<xsl:value-of select="/*[name()='NIR']/*/*[name()='intestazione']/*[name()='dataDoc']"/>
		<xsl:text>, n. </xsl:text>
		<xsl:value-of select="/*[name()='NIR']/*/*[name()='intestazione']/*[name()='numDoc']"/>
	</xsl:template>

	<!-- Norma -->
	<xsl:template name="norma">
		<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='formulainiziale']"/>
		<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='articolato']"/>
		<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='formulafinale']"/>
		<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='conclusione']"/>
		<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='annessi']"/>
		<xsl:apply-templates select="/*[name()='NIR']/*/*[name()='meta']/*[name()='redazionale']"/>
	</xsl:template>

	<!-- ======================================================== -->
	<!--                                                          -->
	<!--  template riferimenti incompleti                         -->
	<!--                                                          -->
	<!-- ======================================================== -->
	<xsl:template match="processing-instruction('rif')">
		<xsl:value-of select="substring-before(substring-after(.,'&gt;'),'&lt;')" />&#160;
	</xsl:template>
	

	<xsl:template match="*[name()='formulainiziale']">
		<fo:block text-align="center" margin-top="5mm">
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>
	<xsl:template match="*[name()='preambolo']">
		<fo:block text-align="justify" space-before="5mm">
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>
	<xsl:template match="*[name()='articolato']">
		<fo:block text-align="justify" space-before="5mm">
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>
	<xsl:template match="*[name()='formulafinale']">
		<fo:block text-align="justify" space-before="5mm">
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>
	<xsl:template match="*[name()='conclusione']">
		<fo:block text-align="left" space-before="5mm">
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>
	<xsl:template match="*[name()='dataeluogo']">
		<fo:block space-before="2mm">
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>
	<xsl:template match="*[name()='sottoscrizioni']">
		<fo:block space-before="2mm">
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>
	<xsl:template match="*[name()='sottoscrivente']">
		<fo:block margin-left="4em" text-indent="0mm" space-before="1mm">
			<!-- fo:block margin-left="50%" text-indent="0mm" margin-top="1mm"-->
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>
	<xsl:template match="*[name()='visto']">
		<fo:block margin-bottom="2mm" font-size="7pt" text-indent="0mm" space-before="2mm">
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>

	<xsl:template match="*[name()='redazionale']">
		<fo:block text-align="justify" text-indent="3mm" margin-top="3mm">
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>
	<xsl:template match="*[name()='h:p']">
		<fo:block text-align="justify" text-indent="3mm">
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>


	<!-- RIGUARDARE T. -->
	<!--xsl:template name="/*[name()='annessi']"-->
	<!--/xsl:template-->
	

	<!-- Tutti gli elementi -->
	<xsl:template match="*[name()='libro' or name()='parte' or name()='capo' or name()='titolo' or name()='sezione' or name()='articolo']">
		<fo:block margin-top="2mm" text-align="center">
			<fo:block  space-before="3mm" space-after="3mm" margin-bottom="0mm">
				<xsl:value-of select="*[name()='num']"/>
			</fo:block>
			<fo:block font-style="italic" space-after="3mm">
				<xsl:if test="*[name()='rubrica']"> 
					<xsl:apply-templates select="*[name()='rubrica']"/>
				</xsl:if>
			</fo:block>
			<xsl:apply-templates select="*[name()='libro' or name()='parte' or name()='capo' or name()='titolo' or name()='sezione' or name()='articolo' or name()='comma']"/>
		</fo:block>
	</xsl:template>
	
<!-- Tutti gli elementi -->
<!-- <xsl:template match="/*[name()='libro' or name()='parte' or name()='capo' or name()='titolo' or name()='sezione' or name()='articolo']" >
   <fo:block margin-top="2mm" >
      <fo:inline font-style="italic"><xsl:value-of select="num"/><xsl:if test="/*[name()='rubrica']"> - <xsl:apply-templates select="*[name()='rubrica']"/></xsl:if></fo:inline>
      <xsl:apply-templates select="libro|parte|capo|titolo|sezione|articolo|comma"/>
   </fo:block>
</xsl:template> -->


	<!-- Trattamento particolare di alcuni elementi che richiedono una qualche trasformazione -->

	<xsl:template match="*[name()='comma']">
		<fo:block margin-top="1mm" text-align="justify" text-indent="3mm">
			<xsl:if test="*[name()='num']!=''">
				<xsl:value-of select="*[name()='num']"/>
			</xsl:if>

			<xsl:apply-templates select="*[name()='corpo' or name()='alinea' or name()='el']"/>
		</fo:block>
	</xsl:template>
	
	<xsl:template match="*[name()='rubrica']">
		<fo:block margin-top="0mm" padding-top="0mm" text-align="center">
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>
	<xsl:template match="*[name()='el' or name()='en']">
		<fo:block margin-top="1mm" text-align="justify" text-indent="6mm">
			<xsl:if test="*[name()='num']!=''">
				<xsl:value-of select="*[name()='num']"/>
				<xsl:choose>
					<xsl:when test="substring(./*,1,1)!=' '">
						<xsl:text>&#160;</xsl:text>
					</xsl:when>
					<xsl:otherwise/>
				</xsl:choose>
			</xsl:if>
			<xsl:apply-templates select="*[name()='corpo' or name()='alinea' or name()='el']"/>
		</fo:block>
	</xsl:template>
	
	<!-- Elementi inline -->
	<xsl:template match="h:i">
		<fo:inline font-style="italic">
			<xsl:apply-templates/>
		</fo:inline>
	</xsl:template>


	<xsl:template match="h:b">
		<fo:inline font-weight="bold">
			<xsl:apply-templates/>
		</fo:inline>
	</xsl:template>
	
	<xsl:template match="h:u">
		<fo:inline text-decoration="underline">
			<xsl:apply-templates/>
		</fo:inline>
	</xsl:template>

	<!--xsl:template match="*[name()='virgolette']">
			<xsl:apply-templates />
	</xsl:template-->

	<!-- Elementi block -->
	<xsl:template match="h:p|h:div|h:br">
		<fo:block text-indent="3mm">
			<xsl:value-of select="."/>
		</fo:block>
	</xsl:template>


	<!-- Note a pi di pagina -->
	<xsl:template match="*[name()='ndr']">
		<fo:footnote>
			<fo:inline baseline-shift="super" font-size="6pt">(<xsl:value-of select="@value"/>)</fo:inline>
			<fo:footnote-body>
				<!-- fo:block>
            <fo:table border="0">
               <fo:table-body>
                  <fo:table-row>
                     <fo:table-cell width="8mm"><fo:block>(<xsl:value-of select = "@value"/>)</fo:block></fo:table-cell>
                     <fo:table-cell><xsl:apply-templates select = "id(@num)"/></fo:table-cell>
                  </fo:table-row>
               </fo:table-body>
            </fo:table>
         </fo:block-->
				<fo:list-block  provisional-distance-between-starts="20pt" provisional-label-separation="5pt">
					<fo:list-item>
						<fo:list-item-label end-indent="label-end()">
							<fo:block font-size="0.83em" line-height="0.9em">(<xsl:value-of select="@value"/>)
								<!-- xsl:number level="any" count="fn" format="1)"/--></fo:block>
						</fo:list-item-label>
						<fo:list-item-body start-indent="body-start()">
							<fo:block font-size="0.83em" line-height="0.9em">
								<xsl:apply-templates select="id(@num)"/>
							</fo:block>
						</fo:list-item-body>
					</fo:list-item>
				</fo:list-block>
			</fo:footnote-body>
		</fo:footnote>
	</xsl:template>
</xsl:stylesheet>

