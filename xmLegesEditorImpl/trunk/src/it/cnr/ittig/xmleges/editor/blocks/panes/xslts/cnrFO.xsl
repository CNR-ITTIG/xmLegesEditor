<?xml version="1.0" encoding="iso-8859-1" ?>

<!--                                                         -->
<!--     Stylesheet XSLT                                     -->
<!--     Trasforma un documento XML in un documento XSL-FO   -->
<!--                                                         -->
<!--     Andrea Marchetti - IIT/CNR                          -->
<!--     Release 12/03/2007                                  -->
<!--                                                         -->

<xsl:stylesheet  version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                               xmlns:fo= "http://www.w3.org/1999/XSL/Format"
                               xmlns:h  ="http://www.w3.org/HTML/1998/html4"
                               xmlns:xlink="http://www.w3.org/1999/xlink" 
                               xmlns:nir="http://www.normeinrete.it/nir/2.2/"
                               xmlns:cnr="http://www.cnr.it/provvedimenti/2.2/" >
                               
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

<xsl:include href="styleFO.xsl" />

<xsl:template name="intestazione">
<fo:block text-align="center" >
   <fo:block font-weight="bold" font-size="18pt" space-after="5mm" font-style="italic">
   Consiglio Nazionale delle Ricerche
   </fo:block>
   <fo:block font-weight="bold" font-size="12pt" >
      <xsl:call-template name="estremiAtto" />
   </fo:block>
   <fo:block font-weight="bold" font-size="15pt" space-before="10mm" space-after="5mm">
      <xsl:apply-templates select = "//nir:intestazione/nir:titoloDoc"/>
   </fo:block>
   <xsl:if test="//nir:intestazione/nir:titoloDoc/following-sibling::text()">
	   <fo:block font-size="10pt" space-before="2mm" space-after="2mm">
	      <xsl:value-of select = "//nir:intestazione/nir:titoloDoc/following-sibling::text()"/>
	   </fo:block>
   </xsl:if>
</fo:block>
</xsl:template>

<xsl:template name="estremiAtto">
   <xsl:value-of select = "//nir:intestazione/nir:tipoDoc"/>
   <xsl:if test="//nir:intestazione/nir:numDoc">
	   <xsl:text> n. </xsl:text>
	   <xsl:value-of select = "//nir:intestazione/nir:numDoc"/>
	   <xsl:text> in data </xsl:text>
	   <xsl:value-of select = "//nir:intestazione/nir:dataDoc"/>
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
<xsl:template match="nir:sottoscrizioni">
   <fo:block space-before="2mm">
      <xsl:apply-templates/>
   </fo:block>
</xsl:template>
<xsl:template match="nir:sottoscrivente">
   <fo:block margin-left="4em" text-indent="0mm" space-before="1mm">
   <!-- fo:block margin-left="50%" text-indent="0mm" space-before="1mm"-->
      <xsl:apply-templates/>
   </fo:block>
</xsl:template>
<xsl:template match="nir:visto">
   <fo:block font-size="smaller" text-indent="0mm" space-before="2mm">
      <xsl:apply-templates/>
   </fo:block>
</xsl:template>
<xsl:template name="annessi">
</xsl:template>

<!-- Tutti gli elementi -->
<xsl:template match="nir:libro|nir:parte|nir:capo|nir:titolo|nir:sezione|nir:articolo" >
   <fo:block space-before="5mm" space-after="3mm" >
      <fo:block font-style="italic" text-align="center"><xsl:value-of select="nir:num"/></fo:block>
      <fo:block font-style="italic" text-align="center"><xsl:if test="nir:rubrica"><xsl:apply-templates select="nir:rubrica"/></xsl:if></fo:block>
      <xsl:apply-templates select="nir:libro|nir:parte|nir:capo|nir:titolo|nir:sezione|nir:articolo|nir:comma"/>
   </fo:block>
</xsl:template>

<!-- Trattamento particolare di alcuni elementi che richiedono una qualche trasformazione -->

<xsl:template match="nir:comma" >
   <fo:block space-before="5mm" text-indent="5mm">
      <xsl:if test="nir:num!=''">
        <fo:inline><xsl:value-of select = "nir:num"/><xsl:text> </xsl:text></fo:inline>
      </xsl:if>
      <xsl:apply-templates select="nir:corpo|nir:alinea|nir:el|nir:en" />
   </fo:block>
</xsl:template>

<xsl:template match="nir:el" >
   <fo:block space-before="1mm" margin-left="5mm">
      <xsl:if test="nir:num!=''">
        <fo:inline><xsl:value-of select = "nir:num"/><xsl:text> </xsl:text></fo:inline>
      </xsl:if>
      <xsl:apply-templates select="nir:corpo|nir:alinea|nir:el|nir:en|nir:el" />
   </fo:block>
</xsl:template>

<xsl:template match="nir:en" >
   <fo:block space-before="1mm" margin-left="10mm">
      <xsl:if test="nir:num!=''">
        <fo:inline><xsl:value-of select = "nir:num"/><xsl:text> </xsl:text></fo:inline>
      </xsl:if>
      <xsl:apply-templates select="nir:corpo|nir:alinea|nir:el|nir:en" />
   </fo:block>
</xsl:template>

<!-- Elementi HTML -->
<!--
<xsl:template match="h:i" >
   <fo:inline font-style="italic"><xsl:apply-templates /></fo:inline>
</xsl:template>

<xsl:template match="h:p|h:div" >
   <fo:block space-before="2mm" ><xsl:apply-templates /></fo:block>
</xsl:template>

<xsl:template match="h:br" >
   <fo:block/>
</xsl:template>
-->
<xsl:include href="xhtml2fo.xsl" />

<!-- Note a pié di pagina -->
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