<?xml version="1.0" standalone="yes"?>
<?xm-well_formed path = ""?>
<xsl:stylesheet  version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                               xmlns:fo= "http://www.w3.org/1999/XSL/Format"
                               xmlns:h  ='http://www.w3.org/HTML/1998/html4' 
                               xmlns:xlink="http://www.w3.org/1999/xlink" 
                               xmlns:nir="http://www.normeinrete.it/nir/1.0">

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
   <xsl:attribute name="border-bottom">0pt solid gray</xsl:attribute>
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
   <!-- xsl:attribute name="font-family">Book Antiqua</xsl:attribute-->
   <xsl:attribute name="font-size">12pt</xsl:attribute>
   <xsl:attribute name="line-height">18pt</xsl:attribute>
   <xsl:attribute name="text-align">justify</xsl:attribute>
   <xsl:attribute name="text-indent">2mm</xsl:attribute>
</xsl:attribute-set>

</xsl:stylesheet>