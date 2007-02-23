<?xml version="1.0"?>

<xsl:stylesheet 
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:html="http://www.w3.org/1999/xhtml">
  
  <xsl:param name="base"/> <!-- The base URL of the site -->
  <xsl:param name="path"/> <!-- the path of the file below the base -->
  <xsl:param name="news"/> <!-- absolute URL of the news feed file in the workspace -->
  
  <xsl:output
      method="xml"
      version="1.0"
      encoding="utf8"
      doctype-public="-//W3C//DTD XHTML 1.1//EN"
      doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"
      indent="yes"/>
  
  
  <xsl:template match="html:html">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
	<title>jMock - <xsl:value-of select="/html:html/html:head/html:title"/></title>
	<link media="screen" rel="stylesheet" type="text/css" href="jmock.css"/>
	<link media="print" rel="stylesheet" type="text/css" href="print.css"/>
	<xsl:copy-of select="html:html/html:head/*[not(name()='title')]"/>
      </head>
      
      <body>
	<div id="banner">
	  <a href="index.html"><img id="logo" src="logo.png" alt="jMock"/></a>
	</div>
	
	<div id="center">
	  <xsl:choose>
	    <xsl:when test="$path = 'index.html'">
	      <xsl:attribute name="class">Content3Column</xsl:attribute>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:attribute name="class">Content2Column</xsl:attribute>
	    </xsl:otherwise>
	  </xsl:choose>
	  
	  <div id="content">
	    <xsl:apply-templates select="/html:html/html:body/*"/>
	  </div>
	  
          <xsl:if test="//html:a">
    	    <div class="LinkFootnotes">
	      <p class="LinkFootnotesHeader">Links:</p>
	      <xsl:for-each select="//html:a">
	        <p><xsl:number level="any" format="1"/>. <xsl:value-of select="."/>: <xsl:value-of select="@href"/></p>
	      </xsl:for-each>
	    </div>
          </xsl:if>
	</div>
	
	<div class="SidePanel" id="left">
	  <div class="MenuGroup">
	    <h1><a href="download.html">Software</a></h1>
	    <xsl:apply-templates select="document('../data/versions-jmock1.xml')"/>
	    <xsl:apply-templates select="document('../data/versions-jmock2.xml')"/>
	    <p><a href="repository.html">Anonymous CVS Access</a></p>
	    <p><a href="license.html">Project License</a></p>
	    <p><a href="versioning.html">jMock Versioning</a></p>
	  </div>
	  
	  <div class="MenuGroup">
	    <h1>Documentation</h1>
	    <p><a href="getting-started.html">Getting Started</a></p>
	    <p><a href="cookbook.html">Cookbook</a></p>
	    <p><a href="cheat-sheet.html">Cheat Sheet</a></p>
	    <p><a href="docs/javadoc-2.0.0/index.html">JavaDocs</a></p>
	    <p><a href="articles.html">Articles and Papers</a></p>
	    <p><a href="http://www.mockobjects.com">All about Mock Objects</a></p>
	  </div>
	  
	  <div class="MenuGroup">
	    <h1>User Support</h1>
	    <p><a href="mailing-lists.html">Mailing Lists</a></p>
	    <p><a href="http://jira.codehaus.org/secure/BrowseProject.jspa?id=10336">Issue Tracker</a></p>
	    <p><a href="news-rss2.xml">News Feed (RSS 2.0)</a></p>
	  </div>
	  
	  <div class="MenuGroup">
	    <h1><a href="development.html">Development</a></h1>
	    <p><a href="how-to-contribute.html">How to Contribute</a></p>
	    <p><a href="team.html">Development Team</a></p>
	    <p><a href="http://www.codehaus.org">Project hosted by Codehaus</a></p>
	    <p class="More"><a href="development.html">More...</a></p>
	  </div>
	</div>
	
	<xsl:if test="$path = 'index.html'">
	  <div class="SidePanel" id="right">
	    <div class="NewsGroup">
	      <h1>Recent News</h1>
	      <xsl:for-each select="document('../content/news-rss2.xml')/rss/channel/item[position() &lt;= 5]">
		<div class="NewsItem">
		  <p class="NewsTitle"><xsl:value-of select="title"/></p>
		  <p class="NewsDate"><xsl:value-of select="pubDate"/></p>
		  <p class="NewsText"><xsl:copy-of select="html:div/node()"/></p>
		</div>
	      </xsl:for-each>	    
	      <p class="NewsMore"><a href="news-rss2.xml">News feed (RSS 2.0)</a></p>
	    </div>
	  </div>
	</xsl:if>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="html:a">
    <xsl:copy-of select="."/><sup class="LinkFootnoteRef"><xsl:number level="any" format="1"/></sup>
  </xsl:template>
  
  <xsl:template match="versions">
    <h2><xsl:value-of select="branch"/></h2>
    <ul>
      <xsl:for-each select="version">
	<li><a href="download.html"><xsl:value-of select="."/>: <xsl:value-of select="@number"/></a></li>
      </xsl:for-each>
    </ul>
  </xsl:template>
  
</xsl:stylesheet>
