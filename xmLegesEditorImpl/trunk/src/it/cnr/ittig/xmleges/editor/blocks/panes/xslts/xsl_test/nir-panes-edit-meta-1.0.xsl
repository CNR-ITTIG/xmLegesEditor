<?xml version="1.0" encoding="UTF-8"?>

<!--
============================================================================================
File di trasformazione per la modifica delle metainformazioni.

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
                xmlns:nir   = "http://www.normeinrete.it/nir/2.2/"
                xmlns:mapper= "xalan://it.cnr.ittig.xmleges.core.blocks.panes.xsltmapper.XsltMapperImpl"
                version     = "1.0">

<xsl:output method="html" 
            omit-xml-declaration="yes"
            encoding="ISO-8859-15"
            indent="no"/>
            
<xsl:include href="nir-panes-dtd-meta-1.0.xsl"/>
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
	
	<xsl:apply-templates select="/nir:NIR/*/nir:meta/nir:descrittori"  />
	<xsl:apply-templates select="/nir:NIR/*/nir:meta/nir:inquadramento"   />	
	<xsl:apply-templates select="/nir:NIR/*/nir:meta/nir:ciclodivita"   />	
	<xsl:apply-templates select="/nir:NIR/*/nir:meta/nir:lavoripreparatori"   />	
	<xsl:apply-templates select="/nir:NIR/*/nir:meta/nir:redazionale"   />	
	<xsl:apply-templates select="/nir:NIR/*/nir:meta/nir:proprietario"   />	
	<xsl:apply-templates select="/nir:NIR/*/nir:meta/nir:risoluzione"   />	
	<xsl:apply-templates select="/nir:NIR/*/nir:meta/nir:disposizioni"   />	
	
	</body>
	</html>
</xsl:template>

</xsl:transform>
