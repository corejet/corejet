<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output omit-xml-declaration="yes" />
	<xsl:template match="//requirementscatalogue">
		<html>
			<head>
				<script type="text/javascript" src="js/jquery-min-1.5.2.js"></script>

				<!-- =================== SCOPE_MAP ================ -->
				<script type="text/javascript" src="js/jit-yc.js"></script>

				<!-- Colors -->
				<script type="text/javascript" >
				        
				        var custom_color_for = function(num_passing, num_pending, total) {
				            var weighted = ( num_passing + (0.3 * num_pending) ) / total;
				        
				            var red = (255 * weighted) / 100;
				            var green = (255*(100-weighted)) / 100;
				            
				            if (total==0) {
				                red = 0;
				                green = 0;
				            }
				        
				            return 'rgb('+red+','+green+',0)';
				        }
				</script>
				
				<!-- Data -->				        
				<script type="text/javascript" >
				
				        var metadata = {
				            'project':  '<xsl:value-of select="@project"/>',
				            'testTime': '<xsl:value-of select="@extractTime"/>'
				        }	        
			       	 var json = {
				            'id':       'root',
				            'name':     '<xsl:value-of select="@project"/>',
				            'data':     {},
				            'children': [
				                            <xsl:apply-templates select="epic" mode="map"/>
				                        ]
				                   };
				</script>
				<script type="text/javascript" src="js/viz.js"></script>

				<link rel="stylesheet" href="css/viz.css" />
				<link rel="stylesheet" href="css/base.css" />
				<link rel="stylesheet" href="css/Treemap.css" />
				
				<!-- =================== END - SCOPE_MAP ================ -->
				
				<!-- =================== SCOPE_DETAIL ================ -->
				
				<script type="text/javascript" src="js/jquery.treeTable.min.js"/>
				<script type="text/javascript" >
					
					jQuery(function($) {
						$("#requirements").treeTable({clickableNodeNames: true});
						$("#problems").treeTable({clickableNodeNames: true});
					});
				</script>
				
				<!-- =================== END - SCOPE_DETAIL ================ -->


				<style type="text/css">
					/*margin and padding on body element
					can introduce errors in determining
					element position and are not recommended;
					we turn them off as a foundation for YUI
					CSS treatments. */
					body {
					margin:0;
					padding:0;
					}
				</style>

				<link rel="stylesheet" type="text/css" href="css/yui/fonts-min.css" />
				<link rel="stylesheet" type="text/css" href="css/yui/tabview.css" />
				<script type="text/javascript" src="js/yui/yahoo-dom-event.js"></script>
				<script type="text/javascript" src="js/yui/element-min.js"></script>
				<script type="text/javascript" src="js/yui/tabview-min.js"></script>
				<script type="text/javascript" src="js/yui/history-min.js"></script>

				<title>Corejet Visualization</title>
			</head>
			<body class="yui-skin-sam">
			<div id="canvas">
			<image src="img/CoreJet2.png" style="float: right"></image>
				<h1>
					Corejet Report - Project
					<span id="project">
						<xsl:value-of select="@project" />
					</span>
				</h1>
				<p>
					Tested as of
					<span id="testTime">
						<xsl:value-of select="@extractTime" />
					</span>
				</p>

<!-- =================== TABS =============================== --> 
 
<style> 
 
#yui-history-iframe {
  position:absolute;
  top:0; left:0;
  width:1px; height:1px; /* avoid scrollbars */
  visibility:hidden;
}
 

 
</style> 
 
<!-- Static markup required by the browser history utility. Note that the
     iframe is only used on Internet Explorer. If this page is server
     generated (by a PHP script for example), it is a good idea to create
     the IFrame ONLY for Internet Explorer (use server side user agent sniffing) --> 
 
<iframe id="yui-history-iframe" src="assets/blank.html"></iframe> 
<input id="yui-history-field" type="hidden" /> 
 
<!-- Static markup required for the TabView widget. --> 
 
<div id="corejet-tabs" class="yui-navset yui-navset-top"> 
  <ul class="yui-nav"> 
    <li><a href="#tab1"><em>Scope map</em></a></li> 
    <li><a href="#tab2"><em>Scope detail</em></a></li> 
    <li><a href="#tab3"><em>Problems</em></a></li> 
  </ul> 
  <div class="yui-content"> 
    <div id="tab1">	<div id="infovis"></div></div> 
    <div id="tab2">
    	<br/>
		<table id="requirements">
            <thead>
                <tr>
                    <th id="heading-id">Id</th>
                    <th id="heading-item">Description</th>
                    <th id="heading-size">Size</th>
                    <th id="heading-status">Status</th>
                    <th id="heading-progress">Progress</th>
                </tr>
            </thead>
            <tbody>
            	<xsl:apply-templates select="epic" mode="detail"/>
            </tbody>
        </table>
	</div> 
    <div id="tab3"><br/>
		<table id="problems">
            <thead>
                <tr>
                    <th id="heading-id">Id</th>
                    <th id="heading-item">Description</th>
                    <th id="heading-progress">Issue</th>
                </tr>
            </thead>
            <tbody>
            	<xsl:apply-templates select="epic" mode="problem"/>
            </tbody>
        </table>
    </div> 
  </div> 
</div> 
 
<script> 
 
(function () {
 
    // The initially selected tab will be chosen in the following order:
    //
    // URL fragment identifier (it will be there if the user previously
    // bookmarked the application in a specific state)
    //
    //         or
    //
    // "tab0" (default)
 
    var bookmarkedTabViewState = YAHOO.util.History.getBookmarkedState("tabview");
    var initialTabViewState = bookmarkedTabViewState || "tab0";
 
    var tabView;
 
    // Register our TabView module. Module registration MUST
    // take place before calling YAHOO.util.History.initialize.
    YAHOO.util.History.register("tabview", initialTabViewState, function (state) {
        // This is called after calling YAHOO.util.History.navigate, or after the user
        // has trigerred the back/forward button. We cannot discrminate between
        // these two situations.
 
        // "state" can be "tab0", "tab1" or "tab2".
        // Select the right tab:
        tabView.set("activeIndex", state.substr(3));
    });
 
    function handleTabViewActiveTabChange (e) {
        var newState, currentState;
 
        newState = "tab" + this.getTabIndex(e.newValue);
 
        try {
            currentState = YAHOO.util.History.getCurrentState("tabview");
            // The following test is crucial. Otherwise, we end up circling forever.
            // Indeed, YAHOO.util.History.navigate will call the module onStateChange
            // callback, which will call tabView.set, which will call this handler
            // and it keeps going from here...
            if (newState != currentState) {
                YAHOO.util.History.navigate("tabview", newState);
            }
        } catch (e) {
            tabView.set("activeIndex", newState.substr(3));
        }
    }
 
    function initTabView () {
        // Instantiate the TabView control...
        tabView = new YAHOO.widget.TabView("corejet-tabs");
        tabView.addListener("activeTabChange", handleTabViewActiveTabChange);
    }
 
    // Use the Browser History Manager onReady method to instantiate the TabView widget.
    YAHOO.util.History.onReady(function () {
        var currentState;
 
        initTabView();
 
        // This is the tricky part... The onLoad event is fired when the user
        // comes back to the page using the back button. In this case, the
        // actual tab that needs to be selected corresponds to the last tab
        // selected before leaving the page, and not the initially selected tab.
        // This can be retrieved using getCurrentState:
        currentState = YAHOO.util.History.getCurrentState("tabview");
        tabView.set("activeIndex", currentState.substr(3));
    });
 
    // Initialize the browser history management library.
    try {
        YAHOO.util.History.initialize("yui-history-field", "yui-history-iframe");
    } catch (e) {
        // The only exception that gets thrown here is when the browser is
        // not supported (Opera, or not A-grade) Degrade gracefully.
        initTabView();
    }
 
})();
 
</script> 
<!-- =================== TABS =============================== -->
			</div>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="epic" mode="map">
                        {   
                           'id':    'epic-<xsl:value-of select="@id"/>',
                           'name':  '<xsl:value-of select="@title"/>',
                           'data': {
                                       '$area': <xsl:value-of select="sum(child::story/@points)"/> * 10,
                                       <xsl:variable name="num_passing_epic"><xsl:value-of select="count(story/scenario[@testStatus='pass'])"/></xsl:variable>
                                       <xsl:variable name="num_pending_epic"><xsl:value-of select="count(story/scenario[@testStatus='pending'])"/></xsl:variable>
                                       <xsl:variable name="total_scenarios_epic"><xsl:value-of select="count(story/scenario)"/></xsl:variable>
                                       '$color': custom_color_for(<xsl:number value="$num_passing_epic"/>, <xsl:number value="$num_pending_epic"/>, <xsl:number value="$total_scenarios_epic"/>)
                                       
                                   },
                           'children': [
                                           <xsl:apply-templates select="story" mode="map"/>
                                       ]
                        },
   </xsl:template>
   
  <xsl:template match="story" mode="map">
                                       {
                                           'id': 'story-<xsl:value-of select="@id"/>',
                                           'name': '<xsl:value-of select="@id"/>',
                                           'data': {
                                                     'title': '<xsl:value-of select="@title"/>',
                                                     '$area': <xsl:value-of select="@points"/> * 10,
                                                           <xsl:variable name="num_passing"><xsl:value-of select="count(scenario[@testStatus='pass'])"/></xsl:variable>
                                                           <xsl:variable name="num_pending"><xsl:value-of select="count(scenario[@testStatus='pending'])"/></xsl:variable>
                                                           <xsl:variable name="total_scenarios"><xsl:value-of select="count(scenario)"/></xsl:variable>
                                                     '$color': custom_color_for(<xsl:number value="$num_passing"/>, <xsl:number value="$num_pending"/>, <xsl:number value="$total_scenarios"/>)
                                                   },
                                           'children': [
                                                           <xsl:apply-templates select="scenario" mode="map"/>
                                                       ]
                                        },
   </xsl:template>
  
   <xsl:template match="scenario" mode="map">
                                                         {
                                                           'id':   '<xsl:value-of select="../@id"/> :: <xsl:value-of select="@name"/>',
                                                           'name': '<xsl:value-of select="@name"/>',
                                                           'data': {
                                                                     'title': '',
                                                                     '$color': 
                                                                               <xsl:choose>
                                                                                   <xsl:when test="@testStatus='pass'">
                                                                                       'green'
                                                                                   </xsl:when>
                                                                                   <xsl:when test="@testStatus='pending'">
                                                                                       'grey'
                                                                                   </xsl:when>
                                                                                   <xsl:otherwise>
                                                                                       'red'
                                                                                   </xsl:otherwise>
                                                                               </xsl:choose>,
                                                                     '$area': (<xsl:value-of select="../@points"/> / <xsl:value-of select="count(../scenario)"/>) * 10,
                                                                   },
                                                           'children': [],
                                                         },
   </xsl:template>
   
   <xsl:template match="epic" mode="detail">
         <tr>
         	<xsl:attribute name="class">epic</xsl:attribute>
         	<xsl:attribute name="id">node-epic-<xsl:value-of select="@id" /></xsl:attribute>
         	<td><xsl:value-of select="@id" /></td>
         	<td><xsl:value-of select="@title" /></td>
         	<td><xsl:value-of select="sum(child::story/@points)" /></td>     	
         	<xsl:variable name="num_passing"><xsl:value-of select="count(story/scenario[@testStatus='pass'])"/></xsl:variable>
            <xsl:variable name="num_pending"><xsl:value-of select="count(story/scenario[@testStatus='pending'])"/></xsl:variable>
            <xsl:variable name="num_super"><xsl:value-of select="count(story/scenario[@testStatus='superflous'])"/></xsl:variable>
            <xsl:variable name="num_mismatch"><xsl:value-of select="count(story/scenario[@testStatus='mismatch'])"/></xsl:variable>
            <xsl:variable name="total_scenarios"><xsl:value-of select="count(story/scenario)"/></xsl:variable>
         	<td><xsl:if test="$num_passing=$total_scenarios">Complete</xsl:if><xsl:if test="$total_scenarios>$num_passing">Incomplete</xsl:if></td>
         	<td><xsl:number value="($num_passing div $total_scenarios)*100"/>%</td>
         </tr>
         <xsl:apply-templates select="story" mode="detail"/>
   </xsl:template>
   <xsl:template match="story" mode="detail">
         <tr>
         	<xsl:attribute name="class">story child-of-node-epic-<xsl:value-of select="../@id" /></xsl:attribute>
         	<xsl:attribute name="id">node-story-<xsl:value-of select="@id" /></xsl:attribute>
         	<td><xsl:value-of select="@id" /></td>
         	<td><xsl:value-of select="@title" /></td>
         	<td><xsl:value-of select="@points" /></td>
         	<xsl:variable name="num_passing"><xsl:value-of select="count(scenario[@testStatus='pass'])"/></xsl:variable>
            <xsl:variable name="num_pending"><xsl:value-of select="count(scenario[@testStatus='pending'])"/></xsl:variable>
            <xsl:variable name="total_scenarios"><xsl:value-of select="count(scenario)"/></xsl:variable>
         	<td><xsl:if test="$num_passing=$total_scenarios">Complete</xsl:if><xsl:if test="$total_scenarios>$num_passing">Incomplete</xsl:if></td>
         	<td><xsl:number value="($num_passing div $total_scenarios)*100"/>%</td>
         </tr>
         <xsl:apply-templates select="scenario" mode="detail"/>
   </xsl:template>
   <xsl:template match="scenario" mode="detail">
         <tr>
         	<xsl:attribute name="class">scenario child-of-node-story-<xsl:value-of select="../@id" /></xsl:attribute>
         	<xsl:attribute name="id">node-scenario-<xsl:value-of select="@id" /></xsl:attribute>
         	<td><xsl:value-of select="@name" /></td>
         	<td>
         	<xsl:for-each select="given">
         		<xsl:sort select="position()" data-type="number" order="ascending"/>
         		<p>Given, <xsl:value-of select="."/></p>
         	</xsl:for-each>
         	<xsl:for-each select="when">
         		<xsl:sort select="position()" data-type="number" order="ascending"/>
         		<p>When, <xsl:value-of select="."/></p>
         	</xsl:for-each>
         	<xsl:for-each select="then">
         		<xsl:sort select="position()" data-type="number" order="ascending"/>
         		<p>Then, <xsl:value-of select="."/></p>
         	</xsl:for-each>
         	</td>
         	<td></td>
         	<td><xsl:value-of select="@testStatus" /></td>
         	<td><xsl:if test="@testStatus='pass'">10</xsl:if>0%</td>
         </tr>
   </xsl:template>   
   
    <xsl:template match="epic" mode="problem">
     	<xsl:variable name="num_super"><xsl:value-of select="count(story/scenario[@testStatus='superfluous'])"/></xsl:variable>
        <xsl:variable name="num_mismatch_scenario"><xsl:value-of select="count(story/scenario[@testStatus='mismatch'])"/></xsl:variable>
        <xsl:variable name="num_mismatch_story"><xsl:value-of select="count(story[@requirementResolution='mismatch'])"/></xsl:variable>
        <xsl:if test="$num_super+$num_mismatch_scenario+$num_mismatch_story>0">
	         <tr>
	         	<xsl:attribute name="class">epic</xsl:attribute>
	         	<xsl:attribute name="id">node-epic-<xsl:value-of select="@id" /></xsl:attribute>
	         	<td><xsl:value-of select="@id" /></td>
	         	<td><xsl:value-of select="@title" /></td>
	         	<td>Error</td>
	         </tr>
       	 	 <xsl:apply-templates select="story" mode="problem"/>
         </xsl:if>
   </xsl:template>
   <xsl:template match="story" mode="problem">
        <xsl:variable name="num_super"><xsl:value-of select="count(scenario[@testStatus='superfluous'])"/></xsl:variable>
        <xsl:variable name="num_mismatch"><xsl:value-of select="count(scenario[@testStatus='mismatch'])"/></xsl:variable>       
        <xsl:if test="$num_super+$num_mismatch>0 or @requirementResolution='mismatch'">
	         <tr>
	         	<xsl:attribute name="class">story child-of-node-epic-<xsl:value-of select="../@id" /></xsl:attribute>
	         	<xsl:attribute name="id">node-story-<xsl:value-of select="@id" /></xsl:attribute>
	         	<td><xsl:value-of select="@id" /></td>
         		<td><xsl:value-of select="@title" /></td>
	         	<td>Error<xsl:if test="@requirementResolution='mismatch'"> - Mismatch</xsl:if></td>
	         </tr>
	         <xsl:apply-templates select="scenario" mode="problem"/>
	    </xsl:if>
   </xsl:template>
   <xsl:template match="scenario" mode="problem">
         <xsl:if test="@testStatus = 'superfluous' or @testStatus = 'mismatch'">
	         <tr>
	         	<xsl:attribute name="class">scenario child-of-node-story-<xsl:value-of select="../@id" /></xsl:attribute>
	         	<xsl:attribute name="id">node-scenario-<xsl:value-of select="@id" /></xsl:attribute>
	         	<td><xsl:value-of select="@name" /></td>
	         	<td>
	         	<xsl:for-each select="given">
	         		<xsl:sort select="position()" data-type="number" order="ascending"/>
	         		<p>Given, <xsl:value-of select="."/></p>
	         	</xsl:for-each>
	         	<xsl:for-each select="when">
	         		<xsl:sort select="position()" data-type="number" order="ascending"/>
	         		<p>When, <xsl:value-of select="."/></p>
	         	</xsl:for-each>
	         	<xsl:for-each select="then">
	         		<xsl:sort select="position()" data-type="number" order="ascending"/>
	         		<p>Then, <xsl:value-of select="."/></p>
	         	</xsl:for-each>
	         	</td>
	         	<td><xsl:value-of select="@testStatus" /></td>
	         </tr>
         </xsl:if>
   </xsl:template>   
</xsl:stylesheet>