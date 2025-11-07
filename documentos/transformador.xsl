<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <bebida>
            <xsl:for-each select="/cafe_order/drinks/drink">
                    <nombre><xsl:value-of select="name"/></nombre>
                    <tipo><xsl:value-of select="type"/></tipo>
            </xsl:for-each>
        </bebida>
    </xsl:template>
</xsl:stylesheet>