<?xml version="1.0" encoding="iso-8859-1"?>

<!--
============================================================================================
File di trasformazione per la modifica degli annessi di un documento NIR.

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
                xmlns:h     = "http://www.w3.org/HTML/1998/html4"
                xmlns       = "http://www.normeinrete.it/nir/1.0"
                xmlns:mapper= "xalan://it.cnr.ittig.xmleges.core.blocks.panes.xsltmapper.XsltMapperImpl"
                version     = "1.0"
>

<xsl:output method="html" />
<xsl:include href="nir-panes-dtd-norme-1.0.xsl"/>
<xsl:include href="nir-panes-dtd-testo-1.0.xsl"/>
<xsl:include href="nir-panes-dtd-globali-1.0.xsl"/>
<xsl:include href="xsltmapper-1.0.xsl"/>
<xsl:strip-space elements="*" />

<xsl:template match="/">
   <html>
		<head>
			<style type="text/css">
				body { font-family: Arial; }
			</style>
		</head>
        <body>
            <xsl:apply-templates select="/*[name()='NIR']/*/*[name()='annessi']" />
        </body>
    </html>
</xsl:template>

</xsl:transform>
