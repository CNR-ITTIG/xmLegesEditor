<?xml version="1.0" encoding="iso-8859-1" ?>

<!--                                                         -->
<!--     Stylesheet XSLT                                     -->
<!--     Trasforma un documento XML in un documento XSL-FO   -->
<!--                                                         -->
<!--                                                         -->
<!--     Andrea Marchetti - IIT/CNR                          -->
<!--     Release 31/05/2006                                  -->
<!--                                                         -->

<xsl:stylesheet  version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                               xmlns:fo="http://www.w3.org/1999/XSL/Format"
                               xmlns:h="http://www.w3.org/HTML/1998/html4"
                               xmlns:xlink="http://www.w3.org/1999/xlink" 
                               xmlns:nir="http://www.normeinrete.it/nir/2.2/"
                               xmlns:cnr="http://www.cnr.it/provvedimenti/2.1" >
                               
<xsl:output method="xml" indent="yes" encoding="iso-8859-1"/>


<xsl:template match="/">
   <fo:root>
      <xsl:comment>Formato FO di un atto normativo</xsl:comment>
      <fo:layout-master-set>
         <fo:simple-page-master  master-name="page" xsl:use-attribute-sets="pageStyle" >
            <fo:region-body   xsl:use-attribute-sets="bodyStyle"      />
            <fo:region-before xsl:use-attribute-sets="headerStyle"    />
            <fo:region-after  xsl:use-attribute-sets="footerStyle"    />
         </fo:simple-page-master>
      </fo:layout-master-set>
      
      <fo:page-sequence master-reference="page" xsl:use-attribute-sets="characterStyle"
                                    initial-page-number="1" >
         <!--
         <fo:static-content flow-name="xsl-region-before">
            <fo:block font-size="10pt">
               <fo:inline margin-left="1cm"><xsl:call-template name="estremiAtto"/></fo:inline>
               <fo:inline text-align="right"><fo:page-number/></fo:inline>
            </fo:block>
         </fo:static-content>
         -->
         
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

<!-- STYLE INFO -->
<!-- Page Size A4 -->
<xsl:param name="paper-height">29.7cm</xsl:param>
<xsl:param name="paper-width" >21.0cm</xsl:param>

<!-- Margini di stampa -->
<xsl:param name="up"    >2.0cm</xsl:param> <!-- 1.4 -->
<xsl:param name="side1" >2.0cm</xsl:param> <!-- 3.0 -->
<xsl:param name="side2" >4.0cm</xsl:param> <!-- 5.0 -->
<xsl:param name="down"  >2.0cm</xsl:param> <!-- 6.0 -->

<!-- Intestazione -->
<xsl:param name="headTitle">Provvedimenti CNR</xsl:param>


<xsl:attribute-set name="pageStyle">
   <xsl:attribute name="page-width" ><xsl:value-of select="$paper-width" /></xsl:attribute>
   <xsl:attribute name="page-height"><xsl:value-of select="$paper-height"/></xsl:attribute>
   <!-- xsl:attribute name="reference-orientation">0</xsl:attribute-->
   <xsl:attribute name="writing-mode" >lr-tb</xsl:attribute>
   <xsl:attribute name="margin-right" ><xsl:value-of select="$side1" /></xsl:attribute>
   <xsl:attribute name="margin-left"  ><xsl:value-of select="$side1" /></xsl:attribute>
   <xsl:attribute name="margin-bottom"><xsl:value-of select="$down"  /></xsl:attribute>
   <xsl:attribute name="margin-top"   ><xsl:value-of select="$up"    /></xsl:attribute>
</xsl:attribute-set>


<xsl:attribute-set name="headerStyle">
   <xsl:attribute name="extent">0mm</xsl:attribute>
   <xsl:attribute name="border-bottom">0.5pt solid gray</xsl:attribute>
   <xsl:attribute name="padding-bottom">0mm</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="footerStyle">
   <xsl:attribute name="extent">10mm</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="bodyStyle">
   <xsl:attribute name="margin-top">2mm</xsl:attribute>
   <xsl:attribute name="margin-bottom">2mm</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="characterStyle">
   <xsl:attribute name="font-family">Times</xsl:attribute>
   <xsl:attribute name="font-size">12pt</xsl:attribute>
   <xsl:attribute name="line-height">18pt</xsl:attribute>
   <xsl:attribute name="text-align">justify</xsl:attribute>
   <xsl:attribute name="text-indent">2mm</xsl:attribute>
</xsl:attribute-set>
<!-- STYLE INFO -->

<xsl:template name="intestazione">
<fo:block text-align="center" >
   <fo:block font-weight="bold" font-size="18pt" space-after="5mm" font-style="italic">
   Consiglio Nazionale delle Ricerche
   </fo:block>
   <fo:block font-weight="bold" font-size="12pt">
      <xsl:call-template name="estremiAtto" />
   </fo:block>
   <fo:block font-weight="bold" font-size="15pt" space-before="10mm" space-after="5mm">
      <xsl:apply-templates select="//nir:intestazione/nir:titoloDoc"/>
   </fo:block>
</fo:block>
</xsl:template>

<xsl:template name="estremiAtto">
   <xsl:value-of select="//nir:intestazione/nir:tipoDoc"/>
   <xsl:if test="//nir:intestazione/nir:numDoc">
	   <xsl:text> n.</xsl:text>
	   <xsl:value-of select="//nir:intestazione/nir:numDoc"/>
	   <xsl:text> del </xsl:text>
	   <xsl:value-of select="//nir:intestazione/nir:dataDoc"/>
   </xsl:if>
</xsl:template>

<!-- Norma -->
<xsl:template name="norma">
   <xsl:apply-templates select="//nir:formulainiziale" />
   <xsl:apply-templates select="//nir:articolato|//nir:contenitore" />
   <xsl:apply-templates select="//nir:formulafinale" />
   <xsl:apply-templates select="//nir:conclusione" />
   <xsl:apply-templates select="//nir:annessi" />
</xsl:template>

	<!-- ======================================================== -->
	<!--                                                          -->
	<!--  template riferimenti incompleti                         -->
	<!--                                                          -->
	<!-- ======================================================== -->
	<xsl:template match="processing-instruction('rif')">
		<xsl:value-of select="substring-before(substring-after(.,'&gt;'),'&lt;')" />&#160;
	</xsl:template>
	
	
<xsl:template match="nir:formulainiziale">
   <fo:block text-align="center" space-before="5mm">
      <xsl:apply-templates/>
   </fo:block>
</xsl:template>
<xsl:template match="nir:preambolo">
   <fo:block text-align="justify" space-before="5mm" space-after="5mm" font-size="11pt">
      <xsl:apply-templates/>
   </fo:block>
</xsl:template>
<xsl:template match="nir:articolato">
   <fo:block>
      <xsl:apply-templates/>
   </fo:block>
</xsl:template>
<xsl:template match="nir:formulafinale">
   <fo:block space-before="5mm">
      <xsl:apply-templates/>
   </fo:block>
</xsl:template>
<xsl:template match="nir:conclusione">
   <fo:block text-align="left"  space-before="5mm">
      <xsl:apply-templates/>
   </fo:block>
</xsl:template>
<xsl:template match="nir:dataeluogo">
   <fo:block>
      <xsl:apply-templates/>
   </fo:block>
</xsl:template>

<!--	xsl:template match="nir:sottoscrizioni">
   <fo:block space-before="2mm">
      <xsl:apply-templates/>
   </fo:block>
</xsl:template	-->

<!-- xsl:template match="nir:sottoscrivente">
   <fo:block margin-left="4em" text-indent="0mm" space-before="1mm">
   
      <xsl:apply-templates/>
   </fo:block>
</xsl:template -->

<!--	xsl:template match="nir:visto">
   <fo:block font-size="smaller" text-indent="0mm" space-before="2mm">
      <xsl:apply-templates/>
   </fo:block>
</xsl:template	-->

<xsl:template match="nir:firma">
   <fo:block font-size="smaller" text-indent="0mm" space-before="2mm">
      <xsl:apply-templates/>
   </fo:block>
</xsl:template>

<xsl:template name="annessi">
</xsl:template>

<!-- Tutti gli elementi -->
<xsl:template match="nir:libro|nir:parte|nir:capo|nir:titolo|nir:sezione|nir:articolo" >
   <fo:block space-before="2mm" >
      <fo:block font-style="italic" text-align="center"><xsl:value-of select="nir:num"/><xsl:if test="nir:rubrica"> - <xsl:apply-templates select="nir:rubrica"/></xsl:if></fo:block>
      <xsl:apply-templates select="nir:libro|nir:parte|nir:capo|nir:titolo|nir:sezione|nir:articolo|nir:comma"/>
   </fo:block>
</xsl:template>

<!-- Trattamento particolare di alcuni elementi che richiedono una qualche trasformazione -->

<xsl:template match="nir:comma|nir:el|nir:en" >
   <fo:block space-before="1mm">
      <xsl:if test="nir:num!=''"><xsl:value-of select = "nir:num"/> </xsl:if>
      <xsl:apply-templates select="nir:corpo|nir:alinea|nir:el" />
   </fo:block>
</xsl:template>

<!-- Elementi inline -->
<xsl:template match="h:i" >
   <fo:inline font-style="italic"><xsl:apply-templates /></fo:inline>
</xsl:template>

<!-- Elementi block -->
<xsl:template match="h:p|h:div" >
   <fo:block space-before="2mm" ><xsl:value-of select="."/></fo:block>
</xsl:template>


<!-- Note a pi� di pagina -->
<xsl:template match="nir:ndr" >
   <fo:footnote>
      <fo:inline font-size="6pt" baseline-shift="super" >(<xsl:value-of select = "@nir:value"/>)</fo:inline>
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
         <fo:list-block provisional-distance-between-starts="20pt" provisional-label-separation="5pt">
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block  font-size="0.83em" line-height="0.9em">
                     (<xsl:value-of select = "@nir:value"/>)
                     <!-- xsl:number level="any" count="fn" format="1)"/-->
                  </fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  <fo:block  font-size="0.83em" line-height="0.9em">
                     <xsl:apply-templates select = "id(@nir:num)"/>
                  </fo:block>
               </fo:list-item-body>
            </fo:list-item>
         </fo:list-block>        
      </fo:footnote-body>
   </fo:footnote>
</xsl:template>



</xsl:stylesheet>
