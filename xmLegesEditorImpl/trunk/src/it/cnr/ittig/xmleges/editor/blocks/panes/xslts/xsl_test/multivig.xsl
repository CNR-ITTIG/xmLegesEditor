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
				<xsl:value-of select="/nir:NIR/*/nir:meta/nir:disposizioni/nir:modifichepassive/*/*/dsp:pos[@xlink:href=$id]/../../dsp:norma/ittig:notavigenza/@id "/>
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
