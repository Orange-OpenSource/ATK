<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="text()[normalize-space(.)][../*]">
        <text><xsl:value-of select="."/></text>
    </xsl:template>

    <xsl:template match="li[text() and not(font) and not(code) and not(em) and not(b)                             and not(a) and not(img) and not(br) and not(let) and not(foreach) and not(if) and not(report)]">
        <li><text><xsl:value-of select="text()"/></text></li>
    </xsl:template>
    <xsl:template match="font[text() and not(font) and not(code) and not(em) and not(b) and not(a)                            and not(img) and not(br) and not(let) and not(foreach) and not(if) and not(report)]">
        <font>
			<xsl:if test="@color">
				<xsl:attribute name="color">
					<xsl:value-of select="@color"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@face">
				<xsl:attribute name="face">
					<xsl:value-of select="@face"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@size">
				<xsl:attribute name="size">
					<xsl:value-of select="@size"/>
				</xsl:attribute>
			</xsl:if>
			<text><xsl:value-of select="text()"/></text>
		</font>
    </xsl:template>
    <xsl:template match="p[text() and not(font) and not(code) and not(em) and not(b) and not(a)                            and not(img) and not(br) and not(let) and not(foreach) and not(if) and not(report)]">
        <p><text><xsl:value-of select="text()"/></text></p>
    </xsl:template>
    <xsl:template match="code[text() and not(font) and not(code) and not(em) and not(b) and not(a)                            and not(img) and not(br) and not(let) and not(foreach) and not(if) and not(report)]">
        <code><text><xsl:value-of select="text()"/></text></code>
    </xsl:template>
    <xsl:template match="h1[text() and not(font) and not(code) and not(em) and not(b) and not(a)                            and not(img) and not(br) and not(let) and not(foreach) and not(if) and not(report)]">
        <h1><text><xsl:value-of select="text()"/></text></h1>
    </xsl:template>
    <xsl:template match="h2[text() and not(font) and not(code) and not(em) and not(b) and not(a)                            and not(img) and not(br) and not(let) and not(foreach) and not(if) and not(report)]">
        <h2><text><xsl:value-of select="text()"/></text></h2>
    </xsl:template>
    <xsl:template match="h3[text() and not(font) and not(code) and not(em) and not(b) and not(a)                            and not(img) and not(br) and not(let) and not(foreach) and not(if) and not(report)]">
        <h3><text><xsl:value-of select="text()"/></text></h3>
    </xsl:template>
    <xsl:template match="h4[text() and not(font) and not(code) and not(em) and not(b) and not(a)                            and not(img) and not(br) and not(let) and not(foreach) and not(if) and not(report)]">
        <h4><text><xsl:value-of select="text()"/></text></h4>
    </xsl:template>
    <xsl:template match="h5[text() and not(font) and not(code) and not(em) and not(b) and not(a)                            and not(img) and not(br) and not(let) and not(foreach) and not(if) and not(report)]">
        <h5><text><xsl:value-of select="text()"/></text></h5>
    </xsl:template>
    <xsl:template match="h6[text() and not(font) and not(code) and not(em) and not(b) and not(a)                            and not(img) and not(br) and not(let) and not(foreach) and not(if) and not(report)]">
        <h6><text><xsl:value-of select="text()"/></text></h6>
    </xsl:template>
    <xsl:template match="em[text() and not(b) and not(a) and not(img) and not(br) and not(let)    and not(foreach) and not(if) and not(report)]">
        <em><text><xsl:value-of select="text()"/></text></em>
    </xsl:template>
    <xsl:template match="b[text() and not(em) and not(a) and not(img) and not(br) and not(let)    and not(foreach) and not(if) and not(report) and not(code)]">
        <b><text><xsl:value-of select="text()"/></text></b>
    </xsl:template>
    <xsl:template match="a[text() and not(em) and not(b) and not(img) and not(br) and not(let)    and not(foreach) and not(if) and not(report)]">
        <a>
			<xsl:if test="@href">
				<xsl:attribute name="href">
					<xsl:value-of select="@href"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@name">
				<xsl:attribute name="name">
					<xsl:value-of select="@name"/>
				</xsl:attribute>
			</xsl:if>
			<text><xsl:value-of select="text()"/></text>
		</a>
    </xsl:template>
    <xsl:template match="td[text() and not(font) and not(code) and not(em) and not(b) and not(a)                            and not(img) and not(br) and not(p) and not(h1) and not(h2) and not(h3)                            and not(h4) and not(h5) and not(h6) and not(ul) and not(ol) and not(table)               and not(center) and not(let) and not(foreach) and not(if) and not(report)]">
        <td>
			<xsl:if test="@height">
				<xsl:attribute name="height">
					<xsl:value-of select="@height"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@width">
				<xsl:attribute name="width">
					<xsl:value-of select="@width"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@align">
				<xsl:attribute name="align">
					<xsl:value-of select="@align"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@valign">
				<xsl:attribute name="valign">
					<xsl:value-of select="@valign"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@rowspan">
				<xsl:attribute name="rowspan">
					<xsl:value-of select="@rowspan"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@colspan">
				<xsl:attribute name="colspan">
					<xsl:value-of select="@colspan"/>
				</xsl:attribute>
			</xsl:if>
			<text><xsl:value-of select="text()"/></text>
		</td>
    </xsl:template>
    <xsl:template match="report[text() and not(font) and not(code) and not(em) and not(b) and not(a)                            and not(img) and not(br) and not(p) and not(h1) and not(h2) and not(h3)                            and not(h4) and not(h5) and not(h6) and not(ul) and not(ol) and not(table)                            and not(center) and not(htmlValue) and not(let) and not(foreach) and not(if)                         and not(report) and not(li)]">
        <report><text><xsl:value-of select="text()"/></text></report>
    </xsl:template>
		<xsl:template match="center[text() and not(font) and not(code) and not(em) and not(b) and not(a)                            and not(img) and not(br) and not(p) and not(h1) and not(h2) and not(h3)                            and not(h4) and not(h5) and not(h6) and not(ul) and not(ol) and not(table)                          and not(let) and not(foreach) and not(if) and not(report)]">
        <report><text><xsl:value-of select="text()"/></text></report>
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>