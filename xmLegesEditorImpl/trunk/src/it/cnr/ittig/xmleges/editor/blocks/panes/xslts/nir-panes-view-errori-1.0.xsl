<?xml version="1.0" encoding="iso-8859-1"?>

<!--
============================================================================================
File di trasformazione per l'elenco degli errori.

author       : Mirco Taddei <mirco.taddei@gmail.com>
version      : 1.0
copyright (C): 2004 - 2005
               Istituto di Teoria e Tecniche dell'Informazione Giuridica (ITTIG)
               Consiglio Nazionale delle Ricerche - Italy
               http://www.ittig.cnr.it
license      : GNU General Public License (http://www.gnu.org/licenses/gpl.html)
============================================================================================
-->

<xsl:transform  xmlns:xsl   = "http://www.w3.org/1999/XSL/Transform"
                xmlns:xlink = "http://www.w3.org/1999/xlink"
                xmlns       = "http://www.w3.org/HTML/1998/html4"
                xmlns:nir   = "http://www.normeinrete.it/nir/2.2"
                xmlns:mapper= "xalan://it.cnr.ittig.xmleges.core.blocks.panes.xsltmapper.XsltMapperImpl"
                version     = "1.0"
>

<xsl:output method="html" 
            omit-xml-declaration="yes"
            encoding="ISO-8859-15"
            indent="yes"/>
            
<xsl:strip-space elements="*" />

<xsl:include href="xsltmapper-1.0.xsl"/>

 
<xsl:template match="/">
   <html>
        <body>
            <xsl:apply-templates select="//processing-instruction('error')" ></xsl:apply-templates>
        </body>
    </html>
</xsl:template>


</xsl:transform>
