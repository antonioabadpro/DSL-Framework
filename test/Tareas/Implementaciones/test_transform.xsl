<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : test_transform.xsl
    Created on : 11 de noviembre de 2025, 17:58
    Author     : agustinrodriguez
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/persona">
        <usuario>
            <nombre><xsl:value-of select="nombre"/></nombre>
        </usuario>
    </xsl:template>
</xsl:stylesheet>
