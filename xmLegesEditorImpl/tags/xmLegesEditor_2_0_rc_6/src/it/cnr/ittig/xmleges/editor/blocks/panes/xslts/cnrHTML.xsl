<?xml version="1.0" encoding="iso-8859-1" ?>

<!--     cnrHTML.xsl                                         -->
<!--     Stylesheet XSLT progettato per convertire i         -->
<!--     provvedimenti CNR in formato HTML                   -->
<!--                                                         -->
<!--                                                         -->
<!--     Andrea Marchetti - IIT/CNR                          -->
<!--     Release 02/05/2007                                  -->
<!--                                                         -->

<xsl:stylesheet  version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                               xmlns:h  ='http://www.w3.org/HTML/1998/html4' 
                               xmlns:xlink="http://www.w3.org/1999/xlink"
                               xmlns:nir="http://www.normeinrete.it/nir/2.2/"
                               xmlns:dsp="http://www.normeinrete.it/nir/disposizioni/2.2/"
                               >

<xsl:output method="xml" encoding="iso-8859-1" />

<xsl:preserve-space elements="nir:intestazione"/>

	<xsl:param name="encoding"/>
	<xsl:param name="baseurl"/>
	
<xsl:template match="/">
   <html>
      <head>
	      <title><xsl:value-of select = "//nir:intestazione/nir:titoloDoc"/></title>
	      
	      <meta http-equiv="Content-Type" content="text/html; charset= {$encoding}"/>
	      
	      <link   rel="stylesheet"  type="text/css" href="cnrLight.css" ></link>
	      <link   rel="alternate stylesheet"  type="text/css" href="cnr.css" ></link>
	      
	      <xsl:comment>
	         <xsl:apply-templates select="//meta"/>
	      </xsl:comment>
      </head>
      
	  <base href="{$baseurl}" />
      
      <body>
		<xsl:apply-templates />
      </body>
   </html>
</xsl:template>

<!--*************************************************************-->
<!--****** Meta          ****************************************-->
<!--*************************************************************-->
<!-- Le Metainformazioni sono ignorate -->
<xsl:template match="nir:meta">
</xsl:template>


<!--*************************************************************-->
<!--****** Intestazione  ****************************************-->
<!--*************************************************************-->
<!--xsl:template match="nir:intestazione">
   <div class="intestazione">
	   <div class="emanante"><xsl:value-of select = "//nir:intestazione/nir:emanante"/></div>
	   <xsl:value-of select = "//nir:intestazione/nir:tipoDoc"/>
	   <xsl:if test="//nir:intestazione/nir:numDoc != ''">
	      <xsl:text> n. </xsl:text><xsl:value-of select = "//nir:intestazione/nir:numDoc"/>
	   </xsl:if>
	   <xsl:if test="//nir:intestazione/nir:dataDoc != ''">
	      <xsl:text> in data </xsl:text><xsl:value-of select = "//nir:intestazione/nir:dataDoc"/>
	   </xsl:if>
	   <div class="titoloDoc"><xsl:value-of select = "//nir:intestazione/nir:titoloDoc"/></div>
   </div>
</xsl:template-->






<!--*************************************************************-->
<!--****** Norma         ****************************************-->
<!--*************************************************************-->


<xsl:template match="nir:soggetto">
	<span class="soggetto"><xsl:value-of select="." /></span>
</xsl:template>

<xsl:template match="h:p|nir:p">
   <xsl:if test="@id"><a name="{@id}"></a></xsl:if>
   <p><xsl:apply-templates /></p>
</xsl:template>

<!-- Tutti gli elementi -->
<xsl:template match="*" >
   <xsl:if test="@id"><a name="{@id}"></a></xsl:if>
   <div class="{name(.)}"><xsl:apply-templates /></div>
</xsl:template>

<!-- Elementi interni -->
<xsl:template match="nir:mrif|nir:ente|nir:ndr|nir:def" >
   <span class="{name(.)}"><xsl:apply-templates /></span>
</xsl:template>


<!-- Trattamento particolare di alcuni elementi che richiedono una qualche trasformazione -->
<xsl:template match="nir:rubrica" >
   <div class="rubrica"><xsl:value-of select = "."/></div>
</xsl:template>



<!-- Riferimenti -->
<xsl:template match="nir:rif">
   <!-- Riferimenti interni -->
   <xsl:if test="starts-with(@xlink:href, '#') and not(ancestor::nir:virgolette)">
      <!-- Per utilizzare la funzione id() occorre inserire la dichiarazione della DTD per definire quali elementi hanno un attributo id -->
      <!-- eliminazione di eventuali virgolette &#34; = " -->
      <xsl:variable name="c1"><xsl:copy-of  select="translate(normalize-space(id(substring-after(@xlink:href,'#'))),'&#34;','-')" /></xsl:variable>
      <!--<xsl:variable name="c1"><xsl:copy-of  select="translate(normalize-space(  *[@id=substring-after(@xlink:href,'#')]  ),'&#34;','-')" /></xsl:variable>-->
      <xsl:variable name="c2">
         <xsl:value-of select="substring($c1,1,300)"/>
         <xsl:if test="string-length($c1) > 300" ><xsl:text> ... </xsl:text></xsl:if>
      </xsl:variable>
      <a href="{@xlink:href}" onmouseout="Hide()" onmouseover='Show("{$c2}")'>
         <span class="rifInterno"><xsl:value-of select="."/></span>
      </a>
   </xsl:if>

   <!-- Riferimenti a testi normativi esterni -->
   <xsl:if test="starts-with(@xlink:href, 'urn:nir')">
      <a target="nirWindow">
         <xsl:attribute name="href">
            <xsl:text>http://www.nir.it/cgi-bin/N2Ln?</xsl:text>
            <xsl:if test="contains(@xlink:href,';')">
               <xsl:value-of select="substring-before(substring-after(@xlink:href,'urn:nir:'),';')"/>
               <xsl:text>%3b</xsl:text>
               <xsl:value-of select="substring-after(@xlink:href,';')"/>
            </xsl:if>
            <xsl:if test="not(contains(@xlink:href,';'))">
               <xsl:value-of select="substring-after(@xlink:href,'urn:nir:')"/>
            </xsl:if>
         </xsl:attribute>
         <span class="rifEsterno"><xsl:value-of select="."/></span>
      </a>
      <!-- <u><xsl:value-of select="."/></u> -->
   </xsl:if>

   <!-- Altri riferimenti -->
   <xsl:if test="@xlink:href=''">
      <u><xsl:value-of select="."/></u>
   </xsl:if>
   <xsl:if test="not(@xlink:href)">
      <i><xsl:value-of select="."/></i>
   </xsl:if>
</xsl:template>

<!--*************************************************************-->
<!--****** HTML          ****************************************-->
<!--*************************************************************-->
<xsl:template match="h:*">
	<xsl:element name="{local-name()}">
		<xsl:copy-of select="@*" />
		<xsl:apply-templates />
	</xsl:element>
</xsl:template>

<xsl:template match="h:br">
	<br/>
</xsl:template>

</xsl:stylesheet>