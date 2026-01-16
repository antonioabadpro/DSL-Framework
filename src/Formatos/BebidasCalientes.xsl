<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        <sql>
            Select  * 
            from "BebidasCalientes" 
            where name='<xsl:value-of select="//name"/>'
        </sql>
    </xsl:template>

</xsl:stylesheet>