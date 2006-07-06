<?xml version="1.0" encoding="iso-8859-1" ?>

<!--                                                         -->
<!--     Stylesheet XSLT                                     -->
<!--     Trasforma un documento XML in un documento HTML     -->
<!--                                                         -->
<!--                                                         -->
<!--     Andrea Marchetti - IIT/CNR                          -->
<!--     Release 31/05/2006                                  -->
<!--                                                         -->

<xsl:stylesheet  version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                               xmlns:nir="http://www.normeinrete.it/nir/2.1"
                               xmlns:h  ='http://www.w3.org/HTML/1998/html4' 
                               xmlns:dsp="http://www.normeinrete.it/nir/disposizioni/2.1" 
                               xmlns:xlink="http://www.w3.org/1999/xlink">

<xsl:output method="html" encoding="iso-8859-1"/>


<xsl:template match="/">
   <html>
      <head>
	      <title><xsl:value-of select = "//nir:intestazione/nir:titoloDoc"/></title>
	      <!-- link   rel="stylesheet"  type="text/css" href="../stylesheet/cnrLight.css" /-->
	      <style type="text/css" >
	      <xsl:comment>
@namespace h url(http://www.w3.org/HTML/1998/html4);


/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * ELEMENTO ROOT                                                                                                       *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

body { font-family: "Verdana", "Tahoma", "Times New Roman",  "Book Antiqua";
		font-size: 1.1em; line-height:1.6em;
       background-color: #aaaaaa; color: #494949; }

#page { margin-right:15%; margin-left:15%; margin-top:5%; margin-bottom:5%;
        border-right: 3pt ridge gray; border-bottom: 3pt ridge gray;
        border-top: 0.5pt solid black;  border-left: 0.5pt solid black;
        background-color: white;
        padding-top: 80px;  padding-right: 60px; padding-left: 60px; padding-bottom:  80px;}



/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * METADATI                                                                                                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
.meta, .inlinemeta { display: none; }/* I metadati non sono visualizzati */




/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * INTESTAZIONE                                                                                                        *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
.intestazione { text-align: center; }
.titoloDoc    { margin: 2%; font-weight: bold ;  }
.emanante     { text-transform: capitalize; font-family:Book Antiqua; font-style:oblique; font-size: 16pt; margin: 2%}

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Table Of Content                                                                                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
.indice     { margin: 0%; margin-left: 0.2em; display: block;   color: gray; font-size: 8pt;          }
.indiceLabel{                                        display: block;   text-align: center; font-weight:bold;  font-size: 10pt;       }
.libroToc   { margin-top: 4.0em; margin-left: 0.2em; display: block; color: black           }
.parteToc   { margin-top: 3.0em; margin-left: 0.4em; display: block; color: black           }
.titoloToc  { margin-top: 2.0em; margin-left: 0.6em; display: block; color: black           }
.capoToc    { margin-top: 1.0em; margin-left: 1.0em; display: block; color: black           }
.sezioneToc { margin-top: 0.5em; margin-left: 1.5em; display: block; color: gray           }
.articoloToc{ margin-top: 0.2em; margin-left: 2.5em; display: block; color: gray           }


/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * FORMULA INIZIALE, FINALE e CONCLUSIONE                                                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
.formulainiziale        {  margin: 10 pt;    display: block;  text-align: center;                     }
.preambolo              {  margin-top: 1em;  display: block;  text-align: justify; text-indent: 1em; font-size: 9pt; line-height:12pt; }
.formulafinale          {  margin-top: 50pt;  margin-left: 10pt; margin-right: 10pt;    display: block;}
.conclusione            {  display: block;                                                            }
.dataeluogo             {  margin-top: 3em;  display: block;  padding-left: 20pt;                     }
.sottoscrizioni         {  display: block;                                                            }
.sottoscrivente         {  margin-top: 1em;  display: block; text-align: center; padding-left: 20%;   }
.visto                  {  margin-top: 2em;  display: block;  text-align: left; font-size: 9pt;       }


/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * ARTICOLATO                                                                                                          *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
.articolato  {  margin: 10pt;   display: block;  font-weight: bold;  text-align: center;    }
.libro       {  margin-top: 3em;   display: block;  font-size: 16pt     }
.parte       {  margin-top: 2.5em; display: block;  font-size: 15pt     }
.titolo      {  margin-top: 2em;   display: block;  font-size: 14pt;    }
.capo        {  margin-top: 2em;   display: block;  font-size: 13pt;    }
.sezione     {  margin-top: 2em;   display: block;  font-size: 12pt;    }
.articolo    {  margin-top: 1.5em; display: block;  font-size: 11pt;    }
.comma       {  margin-top: 6pt;   display: block;  font-size: 10pt;     font-weight: normal;   text-align: justify; }
.el          {  margin-top: 5pt;   display: block;  font-size:  9pt;     margin-left: 1em;   }
.en          {  margin-top: 3pt;   display: block;  font-size:  8pt;     margin-left: 1em;  }



/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * TESTO                                                                                                               *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
h\:p,  h:p, .p, blocco, .blocco,    p        {  margin-top: 0.5em; display: block; }

.comma .corpo    p,
h\:p,  .corpo            {  display: inline; }
.rubrica        {  display: block; font-size: small; font-style: italic; font-weight: bold; }
.comma .num   {  display: inline;   }
.el    .num   {  display: inline;   font-style: italic;}
.alinea       {  display: inline; }
.num          {  margin-right: 3pt; color: Black; font-weight: bold;}


/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * MODIFICHE                                                                                                           *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
mod, .mod                {  display: inline; }
/* virgolette, .virgolette  {  display: inline; margin-top: 1pt; margin-left: 1em; padding: 0.5em;  background: #EEEEEE;} */
virgolette, .virgolette  {  display: block; margin-top: 1pt; margin-left: 1em; padding: 0.5em; border-style: groove; border-width: solid; background: #EEEEEE;}
mod articolo, mod comma,
mod el, mod en,
.mod .articolo, .mod .comma,
.mod .el, .mod .en       {  font-size: 9pt;  font-weight: normal;  font-family: Courier;}

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * ELEMENTI INTERNI                                                                                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
ente, .ente{  display: inline;}
ndr, .ndr  {  font-size: x-small;  color: blue; }
def, .def  {  color: red;  font-weight: bold; }



/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * ALLEGATI                                                                                                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
annessi, .annessi       {  display: block;  margin-top: 5em;  margin-bottom: 2em;  }
denAnnesso, .denAnnesso {  display: block;  text-align: right;  margin-bottom: 1em; }


.l1 {  display: block;                      margin-left: 1.0em;  }
.l2 {  display: block; margin-bottom: 2em;  margin-left: 1.0em;  }
.l3 {  display: block; margin-bottom: 2em;  margin-left: 2.5em;}
.l4 {  display: block; margin-bottom: 2em;  margin-left: 3.0em;}
.l5 {  display: block; margin-bottom: 2em;  margin-left: 3.5em;}
.l6 {  display: block; margin-bottom: 2em;  margin-left: 4.0em;}
.l7 {  display: block; margin-bottom: 2em;  margin-left: 4.5em;}


/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * ELEMENTI HTML                                                                                                       *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
h\:br,    br    {  display: block; }
h\:table, table {  display: block;  border: medium dotted;  margin-top: 1em; }
h\:tr,    tr    {  display: block;  border-top: medium dashed;  border-bottom: thin dotted; }
h\:td,    td    {  display: table-cell; }
h\:li,    li    {  display: list-item;  margin-top: 5pt; }


/* Links */

h\:a:visited, a:visited       {  color: green; text-decoration: none;}
h\:a:link,    a:link          {  color: brown; text-decoration: none;}
h\:a:hover,   a:hover         {  background: yellow; }

rif, .rif  {  display: inline; text-decoration: underline;}
rif:hover  {  background: yellow;}
mrif,.mrif {  display: inline; text-decoration: underline; }
.rifEsterno {  color: blue; }
.rifInterno {  color: green; }

	      </xsl:comment>
	      </style>
	      <xsl:comment>
	         <xsl:apply-templates select="//meta"/>
	      </xsl:comment>
      </head>
      <body>
      	<div id="page">
	    	<xsl:call-template name="intestazione"/>
	    	<xsl:call-template name="articolato"  />
    	</div>
      </body>
   </html>
</xsl:template>



<!--*************************************************************-->
<!--****** Intestazione  ****************************************-->
<!--*************************************************************-->
<xsl:template name="intestazione">
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
</xsl:template>

<xsl:template match="nir:intestazione">
</xsl:template>





<!--*************************************************************-->
<!--****** Norma  ****************************************-->
<!--*************************************************************-->
<!-- Norma -->
<xsl:template name="articolato">
   <div class="norma">
      <xsl:apply-templates select="*" />
   </div>
</xsl:template>

<!-- Le Metainformazioni sono ignorate -->
<xsl:template match="nir:meta">
</xsl:template>

<xsl:template match="nir:soggetto">
	<span class="soggetto"><xsl:value-of select="." /></span>
</xsl:template>

<xsl:template match="h:p|nir:p">
   <xsl:if test="@id"><a name="{@id}"></a></xsl:if>
   <p><xsl:value-of select="." /></p>
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
            <xsl:text>http://norma.test.cineca.it/cgi-bin/N2Ls?urn=</xsl:text>
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



</xsl:stylesheet>
