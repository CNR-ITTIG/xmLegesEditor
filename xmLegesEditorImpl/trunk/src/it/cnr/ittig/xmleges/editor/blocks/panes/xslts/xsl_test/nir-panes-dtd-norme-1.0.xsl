<?xml version="1.0" encoding="UTF-8"?>
 
<!--
============================================================================================
File di trasformazione per gli elementi di testo.dtd.

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
                xmlns:dsp   = "http://www.normeinrete.it/nir/disposizioni/2.2/"
                xmlns:ittig = "http://www.ittig.cnr.it/provvedimenti/2.2"
                xmlns:mapper= "xalan://it.cnr.ittig.xmleges.core.blocks.panes.xsltmapper.XsltMapperImpl"
                version     = "1.0">


<xsl:include href="xsltmapper-1.0.xsl"/>

<!-- mettere elementi gestiti da questo file -->
<xsl:strip-space elements="*" />	
	
<!-- ================================================================================ -->
<!-- =============================================================== INTESTAZIONE === -->
<!-- ================================================================================ -->
<xsl:template match="nir:intestazione">
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
		<xsl:apply-templates />
	</xsl:element>
</xsl:template>

<xsl:template match="nir:tipoDoc | nir:dataDoc | nir:numDoc">
	<xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
		<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
		<xsl:apply-templates />
	</xsl:element>
</xsl:template>

		
<xsl:template match="nir:titoloDoc">
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">				
        <xsl:call-template name="vigenza"/>	
	</xsl:element>
</xsl:template>

<xsl:template match="nir:emanante">
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
		<xsl:apply-templates />
	</xsl:element>
</xsl:template>



<!-- ================================================================================ -->
<!-- =========================================================== FORMULA INIZIALE === -->
<!-- ================================================================================ -->
<xsl:template match="nir:formulainiziale">
        <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
        	<xsl:call-template name="vigenza"/>	
        </xsl:element>
</xsl:template>

<xsl:template match="nir:preambolo">
    <xsl:element name="p" use-attribute-sets="XsltMapperSetClass">
        <xsl:call-template name="vigenza"/>	
    </xsl:element>
</xsl:template>



<!-- ================================================================================ -->
<!-- ============================================================= FORMULA FINALE === -->
<!-- ================================================================================ -->
<xsl:template match="nir:formulafinale">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:call-template name="vigenza"/>
    </xsl:element>
</xsl:template>



<!-- ================================================================================ -->
<!-- ================================================================ DECORAZIONE === -->
<!-- ================================================================================ -->
<xsl:template match="nir:decorazione">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="nir:rango">
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
    	<xsl:value-of select="@tipo"/>
    </xsl:element>
</xsl:template>



<!-- ================================================================================ -->
<!-- ================================================================= ARTICOLATO === -->
<!-- ================================================================================ -->
<xsl:template match="nir:articolato">
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:call-template name="vigenza"/>
    </xsl:element>
</xsl:template>

<xsl:template match="nir:libro | nir:parte | nir:titolo | nir:capo |
	nir:sezione | nir:paragrafo | nir:partizione | nir:articolo">
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
        <xsl:call-template name="vigenza"/>	
    </xsl:element>
</xsl:template>

<xsl:template match="nir:rubrica">
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
        <xsl:call-template name="vigenza"/>	
    </xsl:element>
</xsl:template>

<xsl:template match="nir:num">
	<b>
	    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
	    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
	        <xsl:apply-templates />
	    </xsl:element>
    </b>
</xsl:template>

<xsl:template match="nir:comma | nir:el | nir:en | nir:ep">
	<xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
        <xsl:call-template name="vigenza"/>	
    </xsl:element>
</xsl:template>

<xsl:template match="nir:corpo | nir:alinea | nir:coda">
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
        <xsl:call-template name="vigenza"/>	
    </xsl:element>
</xsl:template> 



<!-- ================================================================================ -->
<!-- ================================================================ CONCLUSIONE === -->
<!-- ================================================================================ -->
<xsl:template match="nir:conclusione">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
        <xsl:call-template name="vigenza"/>
    </xsl:element>
</xsl:template>
	
	<!--	RIMOSSI DALLA DTD 2.2 - Ma servono ancora per i DDL -->
<xsl:template match="nir:sottoscrizioni">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>
<xsl:template match="nir:sottoscrivente">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
        <xsl:call-template name="vigenza"/>
    </xsl:element>
</xsl:template>


<xsl:template match="nir:firma">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">		  
        <xsl:call-template name="vigenza"/>
    </xsl:element>
</xsl:template>


<xsl:template match="nir:dataeluogo">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>



<!-- ================================================================================ -->
<!-- ==================================================================== ANNESSI === -->
<!-- ================================================================================ -->
<xsl:template match="nir:annessi">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="nir:elencoAnnessi">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="nir:annesso">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
        <xsl:call-template name="vigenza"/>	
    </xsl:element>
	<p/>
	<hr width="80%" align="center"/>
	<p/>
</xsl:template>

<xsl:template match="nir:testata">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
     	<xsl:apply-templates />
    </xsl:element>
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:attribute name="style">
    		background: #FFEEEE;
    		margin: 10 0 10 0;
    		font-size: small;
    	</xsl:attribute>
        <xsl:apply-templates select="../*/nir:meta/*"  />
    </xsl:element>    
</xsl:template>

<xsl:template match="nir:denAnnesso | nir:titAnnesso">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:attribute name="align">right</xsl:attribute>
		<font size="+1"><b>
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
        </b></font>
    </xsl:element>
</xsl:template>

<xsl:template match="nir:preAnnesso">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
        <br/>
    </xsl:element>
</xsl:template>

<xsl:template match="nir:rifesterno">
    <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
    		riferimento esterno<br/>
    		<xsl:apply-templates />
        <br/>
    </xsl:element>
</xsl:template>

<xsl:template match="nir:meta"/>


<!-- ================================================================================ -->
<!-- ======================== ELEMENTI INLINE E SPECIALI CON SEMENATICA RILEVANTE === -->
<!-- ================================================================================ -->
<xsl:template match="nir:mrif">
	<u>
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
    </u>
</xsl:template> 

<xsl:template match="nir:rif | nir:irif">
	<font color="blue"><u>
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
    </u></font>
</xsl:template> 


<xsl:template match="nir:mod">
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
	<font bgcolor="#FFDDAA">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
	</font>
    </xsl:element>
</xsl:template> 

<xsl:template match="nir:mmod">
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
	<font bgcolor="#FFDD88">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
	</font>
    </xsl:element>
</xsl:template> 

<xsl:template match="nir:virgolette">
	<xsl:choose>
		<xsl:when test="@tipo='struttura'">
		   <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
		    		<xsl:call-template name="vigenza"/>	
	       </xsl:element>
		</xsl:when>
		<xsl:otherwise>
		   <i>
			<xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
		    	<font bgcolor="#FFEE99">
   		        	<xsl:call-template name="vigenza"/>	
	    		</font>
		    </xsl:element>
		   </i>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="nir:def | nir:atto | nir:soggetto | nir:ente |
	nir:data | nir:luogo | nir:valute | nir:importo">
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template> 



<!-- ================================================================================ -->
<!-- =========================================================== DISEGNI DI LEGGE === -->
<!-- ================================================================================ -->

<xsl:template match="nir:legislatura">
    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="
		nir:relazione | nir:confronto | 
		nir:sinistra | nir:destra">
        <xsl:element name="div" use-attribute-sets="XsltMapperSetClass">
        	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
            <xsl:apply-templates />
        </xsl:element>
</xsl:template>

<!-- ================================================================================ -->
<!-- =========================================================== COMUNI           === -->
<!-- ================================================================================ -->

<!-- riferimenti incompleti -->

<xsl:template match="processing-instruction('rif')">
	<xsl:variable name="content">
		<xsl:value-of select="substring-before(substring-after(.,'&gt;'),'&lt;')"/>
	</xsl:variable>
	<font color="red">
		<u><xsl:element name="span" use-attribute-sets="XsltMapperSetClass">&#160;<xsl:if test="string-length($content) = 0"><xsl:value-of select="mapper:getTextStringIfEmpty(.)"/></xsl:if><xsl:if test="string-length(.) != 0"><xsl:value-of select="$content"/></xsl:if>&#160;</xsl:element></u>
	</font>
</xsl:template>
		
		
										
<xsl:template name="vigenza">
	<xsl:choose>		
		<xsl:when test="@iniziovigore!=''">
      		<xsl:call-template name="multivigente"/>
		</xsl:when>
		<xsl:otherwise>
		    <xsl:call-template name="plain"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>




	<xsl:template name="multivigente">
			
		<xsl:variable name="id">
			<xsl:value-of select="@id" />
		</xsl:variable>
		<xsl:variable name="inizio_id">
			<xsl:value-of select="@iniziovigore"/>
		</xsl:variable>
		<xsl:variable name="fine_id">
			<xsl:value-of select="@finevigore"/>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="$fine_id!=''">    		 <!-- MULTIVIGENTE RED -->
	      		<xsl:choose>
					<xsl:when test="local-name()='span'">
						<font color="red">
							<xsl:call-template name="multivigente_red">
								<xsl:with-param name="id" select="$id"/>
								<xsl:with-param name="inizio_id" select="$inizio_id"/>
								<xsl:with-param name="fine_id" select="$fine_id"/>
							</xsl:call-template>
						</font>
					</xsl:when>
				    <xsl:otherwise>
				    	<div style="color: red; ">
				    	    <xsl:call-template name="multivigente_red">
								<xsl:with-param name="id" select="$id"/>
								<xsl:with-param name="inizio_id" select="$inizio_id"/>
								<xsl:with-param name="fine_id" select="$fine_id"/>
							</xsl:call-template>
				    	</div>
				    </xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$inizio_id!=''">		  <!-- MULTIVIGENTE GREEN -->
				<xsl:choose>
					<xsl:when test="local-name()='span'">
						<font color="green">
							<xsl:call-template name="multivigente_green">
								<xsl:with-param name="id" select="$id"/>
								<xsl:with-param name="inizio_id" select="$inizio_id"/>
							</xsl:call-template>
						</font>
					</xsl:when>
				    <xsl:otherwise>
				    	<div style="color: green; ">
				    		<xsl:call-template name="multivigente_green">
								<xsl:with-param name="id" select="$id"/>
								<xsl:with-param name="inizio_id" select="$inizio_id"/>
							</xsl:call-template>
				    	</div>
				    </xsl:otherwise>
				</xsl:choose>
			</xsl:when>
		</xsl:choose>
		
	</xsl:template>



	<xsl:template name="multivigente_red">
		
		<xsl:param name="id"/>
		<xsl:param name="inizio_id"/>
		<xsl:param name="fine_id"/>
		
		
		
		<xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
			<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
		    <xsl:apply-templates />
		</xsl:element>
		
		<xsl:choose>
			<xsl:when test="/nir:NIR/*/nir:meta/nir:disposizioni/nir:modifichepassive/*/*/dsp:pos[@xlink:href=$id]">
					<xsl:call-template name="notavigenza">
								<xsl:with-param name="id" select="$id"/>
					</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
					<xsl:call-template name="invigore_dal_al">
								<xsl:with-param name="inizio_id" select="$inizio_id"/>
								<xsl:with-param name="fine_id" select="$fine_id"/>
					</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<xsl:template name="multivigente_green">
	
		<xsl:param name="id"/>
		<xsl:param name="inizio_id"/>
		
		<xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
			<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
		    <xsl:apply-templates />
		</xsl:element>
		
		<xsl:choose>
			<xsl:when test="/nir:NIR/*/nir:meta/nir:disposizioni/nir:modifichepassive/*/*/dsp:pos[@xlink:href=$id]">
					<xsl:call-template name="notavigenza">
								<xsl:with-param name="id" select="$id"/>
					</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
					<xsl:call-template name="invigore_dal">
								<xsl:with-param name="inizio_id" select="$inizio_id"/>
					</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>



	<xsl:template name="plain">
		<xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
	    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
	        <xsl:apply-templates />
	    </xsl:element>
	</xsl:template>



	<xsl:template name="notavigenza">
	
		<xsl:param name="id"/>
		
		<!-- dsp:norma/dsp:subarg/-->
		<xsl:variable name="ittignota">
					<xsl:value-of select="/nir:NIR/*/nir:meta/nir:disposizioni/nir:modifichepassive/*/*/dsp:pos[@xlink:href=$id]/../../dsp:norma/dsp:subarg/ittig:notavigenza/@id "/>
		</xsl:variable>
		<xsl:variable name="novella">
					<xsl:value-of select="/nir:NIR/*/nir:meta/nir:disposizioni/nir:modifichepassive/*/*/dsp:pos[@xlink:href=$id]/../../dsp:novella/dsp:pos/@xlink:href"/>
		</xsl:variable>	
		<xsl:variable name="novellando">
					<xsl:value-of select="/nir:NIR/*/nir:meta/nir:disposizioni/nir:modifichepassive/*/*/dsp:pos[@xlink:href=$id]/../../dsp:novellando/dsp:pos/@xlink:href"/>
		</xsl:variable>	
		
		<sup>
			[<xsl:value-of select="substring($ittignota,4,number(string-length($ittignota)))"/>
						<xsl:if test="$novellando">
							<xsl:if test="$novella">
								<!--	sostituzione	-->
								<xsl:if test="$novella=$id">i</xsl:if>
			   					<xsl:if test="$novellando=$id">e</xsl:if>
		   					</xsl:if>
	   					</xsl:if>]
		</sup>
	</xsl:template>


	<xsl:template name="invigore_dal_al">
	
		<xsl:param name="inizio_id"/>
		<xsl:param name="fine_id"/>
		
		<xsl:variable name="stato">
			<xsl:value-of select="@status" />
		</xsl:variable>
		<xsl:variable name="data_inizio">
			<xsl:value-of select="id($inizio_id)/@data"/>
		</xsl:variable>
		<xsl:variable name="data_fine">
			<xsl:value-of select="id($fine_id)/@data"/>
		</xsl:variable>
	
		<span>
			<em>
				<font size="2">
					&#91;In vigore&#160;
					 	<xsl:choose>
							<xsl:when test="$data_inizio!=''">
								dal <xsl:value-of select="concat(substring($data_inizio,7,2),'/',substring($data_inizio,5,2),'/',substring($data_inizio,1,4))"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>fino</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
						&#160;al <xsl:value-of select="concat(substring($data_fine,7,2),'/',substring($data_fine,5,2),'/',substring($data_fine,1,4))"/>
						<xsl:choose>
							<xsl:when test="$stato!=''">
								(<xsl:value-of select="$stato"/>)
							</xsl:when>
						</xsl:choose>
						<xsl:text>&#93;&#160;</xsl:text>
				</font>
			</em>
		</span>												
	</xsl:template>




	<xsl:template name="invigore_dal">
	
		<xsl:param name="inizio_id"/>
	
		<xsl:variable name="data_inizio">
			<xsl:value-of select="id($inizio_id)/@data"/>
		</xsl:variable>
	
		<span>
			<em>
			  <font size="2">
				&#91;In vigore&#160;
				dal <xsl:value-of select="concat(substring($data_inizio,7,2),'/',substring($data_inizio,5,2),'/',substring($data_inizio,1,4))"/>
				&#93;
			  </font>
			</em>
		</span>
	</xsl:template>

</xsl:transform>



<!-- DTD-DL -->
		<!--xsl:when test="$stato='soppresso'">
      		<xsl:choose>
				<xsl:when test="local-name()='span'">
				    <font color="red"><s>
					    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
					    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
					        <xsl:apply-templates />
					    </xsl:element>
				    </s></font>
				</xsl:when>
				<xsl:otherwise>
				    <div style="color: red; "><s>
					    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
					    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
					        <xsl:apply-templates />
					    </xsl:element>
				    </s></div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:when test="$stato='inserito'">
      		<xsl:choose>
				<xsl:when test="local-name()='span'">
				    <font color="green">
					    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
					    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
					        <xsl:apply-templates />
		    			</xsl:element>
				    </font>
				</xsl:when>
				<xsl:otherwise>
				    <div style="color: green; ">
					    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
					    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
					        <xsl:apply-templates />
		    			</xsl:element>
				    </div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when-->
		