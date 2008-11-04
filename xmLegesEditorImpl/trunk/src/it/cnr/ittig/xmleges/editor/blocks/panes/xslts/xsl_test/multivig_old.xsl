<xsl:template name="vigenza">
	<xsl:variable name="id">
		<xsl:value-of select="@id" />
	</xsl:variable>
	<xsl:variable name="stato">
		<xsl:value-of select="@status" />
	</xsl:variable>
	<xsl:variable name="inizio_id">
		<xsl:value-of select="@iniziovigore"/>
	</xsl:variable>
	<xsl:variable name="fine_id">
		<xsl:value-of select="@finevigore"/>
	</xsl:variable>
	<xsl:variable name="data_inizio">
		<xsl:value-of select="id($inizio_id)/@data"/>
	</xsl:variable>
	<xsl:variable name="data_fine">
		<xsl:value-of select="id($fine_id)/@data"/>
	</xsl:variable>
	
	<xsl:choose>		
		<!--		 DTD 2.1 		-->
		<!-- ========================================== DATA FINE !='' ====================================== -->
		<xsl:when test="$fine_id!=''">
      		<xsl:choose>
				<xsl:when test="local-name()='span'">
					<font color="red">
					    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
					    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
					        <xsl:apply-templates />
					    </xsl:element>
						<xsl:choose>
							<xsl:when test="/nir:NIR/*/nir:meta/nir:disposizioni/nir:modifichepassive/*/*/dsp:pos[@xlink:href=$id]">
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
							</xsl:when>
							<xsl:otherwise>					
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
							</xsl:otherwise>
						</xsl:choose>
					</font>
				</xsl:when>
				<xsl:otherwise>
				    <div style="color: red; ">
					    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
					    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
					        <xsl:apply-templates />
					    </xsl:element>
						<xsl:choose>
							<xsl:when test="/nir:NIR/*/nir:meta/nir:disposizioni/nir:modifichepassive/*/*/dsp:pos[@xlink:href=$id]">
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
							</xsl:when>
							<xsl:otherwise>
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
							</xsl:otherwise>
						</xsl:choose>
				    </div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<!--		 ========================================== DATA inizio !='' ====================================== -->
		<xsl:when test="$inizio_id!=''">
      		<xsl:choose>
				<xsl:when test="local-name()='span'">
				    <font color="green">
					    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
					    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
					        <xsl:apply-templates />
					    </xsl:element>
						<xsl:choose>
							<xsl:when test="/nir:NIR/*/nir:meta/nir:disposizioni/nir:modifichepassive/*/*/dsp:pos[@xlink:href=$id]">
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
					
							</xsl:when>
							<xsl:otherwise>
								<span>
									<em>
									  <font size="2">
										&#91;In vigore&#160;
										dal <xsl:value-of select="concat(substring($data_inizio,7,2),'/',substring($data_inizio,5,2),'/',substring($data_inizio,1,4))"/>
										&#93;
									  </font>
									</em>
								</span>
							</xsl:otherwise>
						</xsl:choose>
				    </font>
				</xsl:when>
				<xsl:otherwise>
				    <div style="color: green; ">
					    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
					    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
					        <xsl:apply-templates />
					    </xsl:element>


						<xsl:choose>
							<xsl:when test="/nir:NIR/*/nir:meta/nir:disposizioni/nir:modifichepassive/*/*/dsp:pos[@xlink:href=$id]">
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
					
							</xsl:when>
							<xsl:otherwise>
							
										    
								<span>
									<em>
									  <font size="2">
										&#91;In vigore&#160;
										dal <xsl:value-of select="concat(substring($data_inizio,7,2),'/',substring($data_inizio,5,2),'/',substring($data_inizio,1,4))"/>
										&#93;
									  </font>
									</em>
								</span>
					
					
							</xsl:otherwise>
						</xsl:choose>
				
				    </div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:otherwise>
		    <xsl:element name="span" use-attribute-sets="XsltMapperSetClass">
		    	<xsl:apply-templates select="mapper:getTextNodeIfEmpty(.)" />
		        <xsl:apply-templates />
		    </xsl:element>
		</xsl:otherwise>
	</xsl:choose>
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