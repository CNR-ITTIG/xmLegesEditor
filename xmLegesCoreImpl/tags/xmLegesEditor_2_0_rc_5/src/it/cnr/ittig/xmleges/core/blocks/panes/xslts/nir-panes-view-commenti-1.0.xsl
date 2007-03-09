<?xml version="1.0" encoding="iso-8859-1"?>

<!--
============================================================================================
File di trasformazione per l'elenco dei commenti.   (NON è la stessa NIR)
============================================================================================
-->

<xsl:transform  xmlns:xsl   = "http://www.w3.org/1999/XSL/Transform"
                xmlns:xlink = "http://www.w3.org/1999/xlink"
                xmlns:h     = "http://www.w3.org/HTML/1998/html4"
                xmlns       = "http://www.normeinrete.it/nir/1.0"
                xmlns:mapper= "xalan://it.cnr.ittig.xmleges.core.blocks.panes.xsltmapper.XsltMapperImpl"
                version     = "1.0"
>

<xsl:output method="html" />
<xsl:include href="xsltmapper-1.0.xsl"/>
<xsl:strip-space elements="*" />

<xsl:template match="/">
   <html>
        <body>
            <xsl:apply-templates select="//comment()" />
        </body>
    </html>
</xsl:template>

</xsl:transform>
