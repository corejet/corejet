<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output omit-xml-declaration="yes" />
	<xsl:template match="//requirementscatalogue">
		<html>
			<head>
				<script type="text/javascript" src="js/jquery-min-1.5.2.js"></script>
				<script type="text/javascript" src="js/jit-yc.js"></script>
				<link rel="stylesheet" href="css/base.css" />
				<link rel="stylesheet" href="css/Treemap.css" />
				<link rel="stylesheet" href="css/viz.css" />				

				<!-- =================== SCOPE_MAP ================ -->
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
				<!-- =================== END - SCOPE_MAP ================ -->
				
				<!-- =================== RUNNING_MAP ================ -->

				<!-- Data -->				        
				<script type="text/javascript" >    
			       	 var json_running = {
				            'id':       'root',
				            'name':     '<xsl:value-of select="@project"/>',
				            'data':     {},
				            'children': [
				                            <xsl:apply-templates select="epic" mode="map_running"/>
				                        ]
				                   };
				</script>				
				<!-- =================== END - RUNNING_MAP ================ -->
				
				<!-- =================== TIMING_MAP ================ -->

				<!-- Data -->				        
				<script type="text/javascript" >    
			       	 var json_timing = {
				            'id':       'root',
				            'name':     '<xsl:value-of select="@project"/>',
				            'data':     {},
				            'children': [
				                            <xsl:apply-templates select="epic" mode="map_timing"/>
				                        ]
				                   };
				</script>				
				<!-- =================== END - TIMING_MAP ================ -->
				<script type="text/javascript" src="js/viz.js"></script>
				<script type="text/javascript" src="js/viz_running.js"></script>
				<script type="text/javascript" src="js/viz_timing.js"></script>
				
				<!-- =================== SCOPE_DETAIL ================ -->
				
				<script type="text/javascript" src="js/jquery.treeTable.min.js"/>
				<script type="text/javascript" >
					
					jQuery(function($) {
						$("#requirements").treeTable({clickableNodeNames: true});
						$("#failures").treeTable({clickableNodeNames: true});
						$("#defects").treeTable({clickableNodeNames: true});
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
			<!-- Key -->
			<table style="float: right; width: 20%; margin-top:0em; margin-right:3em; font-size:0.9em">
			<tbody><tr><td style="width: 5%; background-color: green;"></td><td style="width: 10%;">Passing</td><td style="width: 5%; background-color: #94CA94;"></td><td style="width: 10%;">Empty</td></tr>
			<tr><td style="width: 5%; background-color: red;"></td><td style="width: 10%;">Failing</td><td style="width: 5%; background-color: grey;"></td><td style="width: 10%;">Not written</td></tr>
			<tr><td style="width: 5%; background-color: purple;"></td><td style="width: 10%;">Defect</td><td style="width: 5%; background-color: white;"></td><td style="width: 10%;">Not tested</td></tr>
			<tr><td style="width: 5%; background-color: orange;"></td><td style="width: 10%;">Mismatch</td><td style="width: 5%; background-color: lightblue;"></td><td style="width: 10%;">Not Built</td></tr>
			</tbody></table>
				<h1>
					Corejet Report - Project
					<span id="project">
						<xsl:value-of select="@project" />
					</span>
				</h1>
				<h2>
					Failures:
					<span id="failure-count"><xsl:value-of select="count(//scenario[@testStatus='fail'])"/>/<xsl:value-of select="count(//scenario[@testStatus='pass'])+count(//scenario[@testStatus='fail'])"/></span>
				</h2>
				<p>
					Tested as of
					<span id="testTime">
						<xsl:value-of select="@extractTime" />
					</span>
				</p>
				<br/>

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
    <li><a href="#tab2"><em>Running tests</em></a></li> 
    <li><a href="#tab3"><em>Test timings</em></a></li> 
    <li><a href="#tab4"><em>Scope detail</em></a></li> 
    <li><a href="#tab5"><em>Failures</em></a></li> 
    <li><a href="#tab6"><em>Defects</em></a></li> 
    <li><a href="#tab7"><em>Problems</em></a></li> 
  </ul> 
  <div class="yui-content"> 
    <div id="tab1">	<div id="infovis"></div></div> 
    <div id="tab2">	<div id="infovis_running"></div></div>
    <div id="tab3">	<div id="infovis_timing"></div></div> 
    <div id="tab4">
    	<br/>
		<table id="requirements" style="table-layout: fixed">
            <thead>
                <tr>
                    <th id="heading-id" width="20%">Id</th>
                    <th id="heading-item" width="50%">Description</th>
                    <th id="heading-size" width="10%">Size</th>
                    <th id="heading-status" width="10%">Status</th>
                    <th id="heading-progress" width="10%">Progress</th>
                </tr>
            </thead>
            <tbody>
            	<xsl:apply-templates select="epic" mode="detail"/>
            </tbody>
            <tfoot>
            	<tr>
       	         	<xsl:variable name="num_passing_total"><xsl:value-of select="count(epic/story/scenario[@testStatus='pass'])"/></xsl:variable>
		            <xsl:variable name="num_pending_total"><xsl:value-of select="count(epic/story/scenario[@testStatus='pending'])"/></xsl:variable>
		            <xsl:variable name="total_scenarios_total"><xsl:value-of select="count(epic/story/scenario)"/></xsl:variable>
                    <td></td>
                    <td><strong>Total</strong></td>
                    <td><strong><xsl:value-of select="sum(epic/story/@points)" /></strong></td>
                    <td><strong><xsl:if test="$num_passing_total=$total_scenarios_total">Complete</xsl:if><xsl:if test="$total_scenarios_total>$num_passing_total">Incomplete</xsl:if></strong></td>
                    <td><strong><xsl:number value="($num_passing_total div $total_scenarios_total)*100"/>%</strong></td>
                </tr>
            </tfoot>
        </table>
	</div> 
	<div id="tab5">
		<br/>
		<table id="failures" style="table-layout: fixed">
            <thead>
                <tr>
                    <th id="heading-id" width="20%">Id</th>
                    <th id="heading-item" width="20%">Description</th>
                    <th id="heading-size" width="10%">Duration</th>
                    <th id="heading-size" width="50%">Failure</th>
                </tr>
            </thead>
            <tbody>
            	<xsl:apply-templates select="epic" mode="failures"/>
            </tbody>
            <tfoot>
            	<tr>
                    <td></td>
                    <td></td>
                    <td><strong>Total</strong></td>
                    <td><strong><xsl:value-of select="count(epic/story/scenario[@testStatus='fail'])"/>/<xsl:value-of select="count(epic/story/scenario[@testStatus='fail']) + count(epic/story/scenario[@testStatus='pass'])"/></strong></td>
                </tr>
            </tfoot>
        </table>
    </div> 
    <div id="tab6">
    	<br/>
		<table id="defects">
            <thead>
                <tr>
                    <th id="heading-id">Id</th>
                    <th id="heading-item">Description</th>
                    <th id="heading-progress">Defect</th>
                </tr>
            </thead>
            <tbody>
            	<xsl:apply-templates select="epic" mode="defects"/>
            </tbody>
            <tfoot>
            	<tr>
                    <td></td>
                    <td><strong>Total</strong></td>
                    <td><strong><xsl:value-of select="count(epic/story/scenario[@testStatus='defect'])"/>/<xsl:value-of select="count(epic/story/scenario)"/></strong></td>
                </tr>
            </tfoot>
        </table>
    </div> 
     <div id="tab7">
    	<br/>
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
  
    var bookmarkedTabViewState = YAHOO.util.History.getBookmarkedState("tabview");
    var initialTabViewState = bookmarkedTabViewState || "tab0";
 
    var tabView;
 
   
    YAHOO.util.History.register("tabview", initialTabViewState, function (state) {
        tabView.set("activeIndex", state.substr(3));
    });
 
    function handleTabViewActiveTabChange (e) {
        var newState, currentState;
 
        newState = "tab" + this.getTabIndex(e.newValue);
 
        try {
            currentState = YAHOO.util.History.getCurrentState("tabview");
            if (newState != currentState) {
                YAHOO.util.History.navigate("tabview", newState);
            }
        } catch (e) {
            tabView.set("activeIndex", newState.substr(3));
        }
    }
 
    function initTabView () {
        tabView = new YAHOO.widget.TabView("corejet-tabs");
        tabView.addListener("activeTabChange", handleTabViewActiveTabChange);
    }
 
    YAHOO.util.History.onReady(function () {
        var currentState;
 
        initTabView();
        currentState = YAHOO.util.History.getCurrentState("tabview");
        tabView.set("activeIndex", currentState.substr(3));
    });
 
    try {
        YAHOO.util.History.initialize("yui-history-field", "yui-history-iframe");
    } catch (e) {
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
                                       <xsl:variable name="num_mismatch_epic"><xsl:value-of select="count(story/scenario[@testStatus='mismatch'])"/></xsl:variable>
                                       <xsl:variable name="num_superfluous_epic"><xsl:value-of select="count(story/scenario[@testStatus='superfluous'])"/></xsl:variable>
                                       <xsl:variable name="num_fail_epic"><xsl:value-of select="count(story/scenario[@testStatus='fail'])"/></xsl:variable>
                                       <xsl:variable name="num_defect_epic"><xsl:value-of select="count(story/scenario[@testStatus='defect'])"/></xsl:variable>
                                       <xsl:variable name="num_todo_epic"><xsl:value-of select="count(story/scenario[@testStatus='todo'])"/></xsl:variable>
                                       <xsl:variable name="num_na_epic"><xsl:value-of select="count(story/scenario[@testStatus='na'])"/></xsl:variable>
                                       <xsl:variable name="num_empty_epic"><xsl:value-of select="count(story/scenario[@testStatus='empty'])"/></xsl:variable>
                                       <xsl:variable name="total_scenarios_epic"><xsl:value-of select="count(story/scenario)"/></xsl:variable>
                                                      '$color': <xsl:choose>
                                                      				<xsl:when test="$total_scenarios_epic=0">
                                                                        '#94CA94'
                                                                    </xsl:when>
                                                                    <xsl:when test="$num_fail_epic>0">
                                                                        'red'
                                                                    </xsl:when>
                                                                    <xsl:when test="$num_defect_epic>0">
                                                                        'purple'
                                                                    </xsl:when>
                                                                    <xsl:when test="$num_mismatch_epic+$num_superfluous_epic>0">
                                                                        'orange'
                                                                    </xsl:when>
                                                                    <xsl:when test="$num_passing_epic+$num_na_epic+$num_empty_epic=$total_scenarios_epic">
                                                                        'green'
                                                                    </xsl:when>
                                                                    <xsl:when test="$num_pending_epic+$num_passing_epic+$num_empty_epic+$num_na_epic=$total_scenarios_epic">
                                                                        'lightblue'
                                                                    </xsl:when>
                                                                    <xsl:otherwise>
                                                                        'grey'
                                                                    </xsl:otherwise>
                                                                </xsl:choose>
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
                                                           <xsl:variable name="num_mismatch"><xsl:value-of select="count(scenario[@testStatus='mismatch'])"/></xsl:variable>
                                                           <xsl:variable name="num_defect"><xsl:value-of select="count(scenario[@testStatus='defect'])"/></xsl:variable>
                                                           <xsl:variable name="num_superfluous"><xsl:value-of select="count(scenario[@testStatus='superfluous'])"/></xsl:variable>
                                                           <xsl:variable name="num_fail"><xsl:value-of select="count(scenario[@testStatus='fail'])"/></xsl:variable>
                                                           <xsl:variable name="num_todo"><xsl:value-of select="count(scenario[@testStatus='todo'])"/></xsl:variable>
                                                           <xsl:variable name="num_na"><xsl:value-of select="count(scenario[@testStatus='na'])"/></xsl:variable>
                                                           <xsl:variable name="num_empty"><xsl:value-of select="count(scenario[@testStatus='empty'])"/></xsl:variable>
                                                           <xsl:variable name="total_scenarios"><xsl:value-of select="count(scenario)"/></xsl:variable>
                                                     '$color': <xsl:choose>
                                                    				<xsl:when test="$total_scenarios=0">
                                                                        '#94CA94'
                                                                    </xsl:when>
                                                                    <xsl:when test="$num_fail>0">
                                                                        'red'
                                                                    </xsl:when>
                                                                    <xsl:when test="$num_defect>0">
                                                                        'purple'
                                                                    </xsl:when>
                                                                    <xsl:when test="$num_mismatch+$num_superfluous>0">
                                                                        'orange'
                                                                    </xsl:when>
                                                                    <xsl:when test="$num_empty+$num_passing+$num_na=$total_scenarios">
                                                                        'green'
                                                                    </xsl:when>
                                                                    <xsl:when test="$num_empty+$num_pending+$num_passing+$num_na=$total_scenarios">
                                                                        'lightblue'
                                                                    </xsl:when>                                                                    
                                                                    <xsl:otherwise>
                                                                        'grey'
                                                                    </xsl:otherwise>
                                                                </xsl:choose>
                                                   },
                                           'children': [
                                                           <xsl:apply-templates select="scenario" mode="map"/>
                                                       ]
                                        },
   </xsl:template>
  
   <xsl:template match="scenario" mode="map">
                                                         {
                                                           'id':   '<xsl:value-of select="../@id"/>-<xsl:value-of select="position()" />',
                                                           'name': '',
                                                           'data': {
                                                                     'title': '<xsl:value-of select="@name"/>',
                                                                     '$color': 
                                                                               <xsl:choose>
                                                                                	<xsl:when test="@testStatus='empty'">
                                                                                       '#94CA94'
                                                                                   </xsl:when>
                                                                                   <xsl:when test="@testStatus='pass'">
                                                                                       'green'
                                                                                   </xsl:when>
                                                                                   <xsl:when test="@testStatus='superfluous'">
                                                                                       'orange'
                                                                                   </xsl:when>
                                                                                   <xsl:when test="@testStatus='mismatch'">
                                                                                       'orange'
                                                                                   </xsl:when>
                                                                                   <xsl:when test="@testStatus='todo'">
                                                                                       'grey'
                                                                                   </xsl:when>
                                                                                   <xsl:when test="@testStatus='pending'">
                                                                                       'lightblue'
                                                                                   </xsl:when>
                                                                                   <xsl:when test="@testStatus='na'">
                                                                                       'white'
                                                                                   </xsl:when>
                                                                                   <xsl:when test="@testStatus='defect'">
                                                                                       'purple'
                                                                                   </xsl:when>
                                                                                   <xsl:otherwise>
                                                                                       'red'
                                                                                   </xsl:otherwise>
                                                                               </xsl:choose>,
                                                                     '$area': (<xsl:value-of select="../@points"/> / <xsl:value-of select="count(../scenario)"/>) * 10,
                                                       		         '$text': '<h2><xsl:value-of select="@name"/></h2><xsl:for-each select="given"><xsl:sort select="position()" data-type="number" order="ascending"/><p>Given <xsl:value-of select="."/></p></xsl:for-each><xsl:for-each select="when"><xsl:sort select="position()" data-type="number" order="ascending"/><p>When <xsl:value-of select="."/></p></xsl:for-each><xsl:for-each select="then"><xsl:sort select="position()" data-type="number" order="ascending"/><p>Then <xsl:value-of select="."/></p></xsl:for-each><xsl:for-each select="failure"><br/>	<p><strong>Failed at step <xsl:value-of select="@step"/></strong></p></xsl:for-each>',
                                                                   },
                                                           'children': [],
                                                         },
   </xsl:template>
   
   <xsl:template match="epic" mode="map_running">
                    <xsl:variable name="num_passing_epic"><xsl:value-of select="count(story/scenario[@testStatus='pass'])"/></xsl:variable>
                    <xsl:variable name="num_fail_epic"><xsl:value-of select="count(story/scenario[@testStatus='fail'])"/></xsl:variable>
                    <xsl:variable name="total_running_scenarios_epic"><xsl:value-of select="count(story/scenario[@testStatus='pass'])+count(story/scenario[@testStatus='fail'])"/></xsl:variable>
       				<xsl:if test="$total_running_scenarios_epic>0">
                        {   
                           'id':    'epic-running-<xsl:value-of select="@id"/>',
                           'name':  '<xsl:value-of select="@title"/>',
                           'data': {
                                       '$area': <xsl:value-of select="sum(child::story/@points)"/> * 10,
                                                      '$color': <xsl:choose>
                                                                    <xsl:when test="$num_fail_epic>0">
                                                                        'red'
                                                                    </xsl:when>
                                                                    <xsl:otherwise>
                                                                        'green'
                                                                    </xsl:otherwise>
                                                                </xsl:choose>
                                   },
                           'children': [
                                           <xsl:apply-templates select="story" mode="map_running"/>
                                       ]
                        },
                  </xsl:if>
   </xsl:template>
   
  <xsl:template match="story" mode="map_running">
                                        <xsl:variable name="num_passing"><xsl:value-of select="count(scenario[@testStatus='pass'])"/></xsl:variable>
                                        <xsl:variable name="num_fail"><xsl:value-of select="count(scenario[@testStatus='fail'])"/></xsl:variable>
                                        <xsl:variable name="total_running_scenarios"><xsl:value-of select="count(scenario[@testStatus='pass'])+count(scenario[@testStatus='fail'])"/></xsl:variable>
                                      	<xsl:if test="$total_running_scenarios>0">
                                       {
                                           'id': 'story-running-<xsl:value-of select="@id"/>',
                                           'name': '<xsl:value-of select="@id"/>',
                                           'data': {
                                                     'title': '<xsl:value-of select="@title"/>',
                                                     '$area': <xsl:value-of select="@points"/> * 10,
                                                     '$color': <xsl:choose>
                                                                    <xsl:when test="$num_fail>0">
                                                                        'red'
                                                                    </xsl:when>
                                                                    <xsl:otherwise>
                                                                    	'green'
                                                                    </xsl:otherwise>                                                                
                                                                </xsl:choose>
                                                   },
                                           'children': [
                                                           <xsl:apply-templates select="scenario[@testStatus='pass']" mode="map_running"/>
                                                           <xsl:apply-templates select="scenario[@testStatus='fail']" mode="map_running"/>
                                                       ]
                                        },
                                        </xsl:if>
   </xsl:template>
  
   <xsl:template match="scenario" mode="map_running">
                                                         {
                                                           'id':   'running-<xsl:choose><xsl:when test="@testStatus='pass'">pass</xsl:when><xsl:when test="@testStatus='fail'">fail</xsl:when></xsl:choose><xsl:value-of select="../@id"/>-<xsl:value-of select="position()" />',
                                                           'name': '',
                                                           'data': {
                                                                     'title': '<xsl:value-of select="@name"/>',
                                                                     '$color': 
                                                                               <xsl:choose>
                                                                                   <xsl:when test="@testStatus='pass'">
                                                                                       'green'
                                                                                   </xsl:when>
                                                                                   <xsl:otherwise>
                                                                                       'red'
                                                                                   </xsl:otherwise>
                                                                               </xsl:choose>,
                                                                     '$area': (<xsl:value-of select="../@points"/> / <xsl:value-of select="count(../scenario[@testStatus='pass'])+count(../scenario[@testStatus='fail'])"/>)* 10,
                                                       		         '$text': '<h2><xsl:value-of select="@name"/></h2><xsl:for-each select="given"><xsl:sort select="position()" data-type="number" order="ascending"/><p>Given <xsl:value-of select="."/></p></xsl:for-each><xsl:for-each select="when"><xsl:sort select="position()" data-type="number" order="ascending"/><p>When <xsl:value-of select="."/></p></xsl:for-each><xsl:for-each select="then"><xsl:sort select="position()" data-type="number" order="ascending"/><p>Then <xsl:value-of select="."/></p></xsl:for-each><xsl:for-each select="failure"><br/>	<p><strong>Failed at step <xsl:value-of select="@step"/></strong></p></xsl:for-each>',
                                                                   },
                                                           'children': [],
                                                         },
   </xsl:template>
   <xsl:template match="epic" mode="map_timing">
                    <xsl:variable name="num_passing_epic"><xsl:value-of select="count(story/scenario[@testStatus='pass'])"/></xsl:variable>
                    <xsl:variable name="num_fail_epic"><xsl:value-of select="count(story/scenario[@testStatus='fail'])"/></xsl:variable>
                    <xsl:variable name="total_running_scenarios_epic"><xsl:value-of select="count(story/scenario[@testStatus='pass'])+count(story/scenario[@testStatus='fail'])"/></xsl:variable>
       				<xsl:if test="$total_running_scenarios_epic>0">
                        {   
                           'id':    'epic-timing-<xsl:value-of select="@id"/>',
                           'name':  '<xsl:value-of select="@title"/>',
                           'data': {
                                       '$area': (<xsl:value-of select="sum(child::story/scenario/@duration) + 0.0001 "/> * 1000 ),
                                                      '$color': <xsl:choose>
                                                                    <xsl:when test="$num_fail_epic>0">
                                                                        'red'
                                                                    </xsl:when>
                                                                    <xsl:otherwise>
                                                                        'green'
                                                                    </xsl:otherwise>
                                                                </xsl:choose>
                                   },
                           'children': [
                                           <xsl:apply-templates select="story" mode="map_timing"/>
                                       ]
                        },
                  </xsl:if>
   </xsl:template>
   
  <xsl:template match="story" mode="map_timing">
                                        <xsl:variable name="num_passing"><xsl:value-of select="count(scenario[@testStatus='pass'])"/></xsl:variable>
                                        <xsl:variable name="num_fail"><xsl:value-of select="count(scenario[@testStatus='fail'])"/></xsl:variable>
                                        <xsl:variable name="total_running_scenarios"><xsl:value-of select="count(scenario[@testStatus='pass'])+count(scenario[@testStatus='fail'])"/></xsl:variable>
                                      	<xsl:if test="$total_running_scenarios>0">
                                       {
                                           'id': 'story-timing-<xsl:value-of select="@id"/>',
                                           'name': '<xsl:value-of select="@id"/>',
                                           'data': {
                                                     'title': '<xsl:value-of select="@title"/>',
                                                     '$area': <xsl:value-of select="sum(child::scenario/@duration)+0.0001"/> * 1000,
                                                     '$color': <xsl:choose>
                                                                    <xsl:when test="$num_fail>0">
                                                                        'red'
                                                                    </xsl:when>
                                                                    <xsl:otherwise>
                                                                    	'green'
                                                                    </xsl:otherwise>                                                                
                                                                </xsl:choose>
                                                   },
                                           'children': [
                                                           <xsl:apply-templates select="scenario[@testStatus='pass']" mode="map_timing"/>
                                                           <xsl:apply-templates select="scenario[@testStatus='fail']" mode="map_timing"/>
                                                       ]
                                        },
                                        </xsl:if>
   </xsl:template>
  
   <xsl:template match="scenario" mode="map_timing">
                                                         {
                                                           'id':   'timing-<xsl:choose><xsl:when test="@testStatus='pass'">pass</xsl:when><xsl:when test="@testStatus='fail'">fail</xsl:when></xsl:choose><xsl:value-of select="../@id"/>-<xsl:value-of select="position()" />',
                                                           'name': '',
                                                           'data': {
                                                                     'title': '<xsl:value-of select="@name"/>',
                                                                     '$color': 
                                                                               <xsl:choose>
                                                                                   <xsl:when test="@testStatus='pass'">
                                                                                       'green'
                                                                                   </xsl:when>
                                                                                   <xsl:otherwise>
                                                                                       'red'
                                                                                   </xsl:otherwise>
                                                                               </xsl:choose>,
                                                                     '$area': (<xsl:value-of select="@duration"/> + 0.0001)* 1000,
                                                       		         '$text': '<h2><xsl:value-of select="@name"/> - <xsl:value-of select="@duration"/>s</h2><xsl:for-each select="given"><xsl:sort select="position()" data-type="number" order="ascending"/><p>Given <xsl:value-of select="."/> - <xsl:value-of select="@duration"/>s</p></xsl:for-each><xsl:for-each select="when"><xsl:sort select="position()" data-type="number" order="ascending"/><p>When <xsl:value-of select="."/> - <xsl:value-of select="@duration"/>s</p></xsl:for-each><xsl:for-each select="then"><xsl:sort select="position()" data-type="number" order="ascending"/><p>Then <xsl:value-of select="."/> - <xsl:value-of select="@duration"/>s</p></xsl:for-each><xsl:for-each select="failure"><br/>	<p><strong>Failed at step <xsl:value-of select="@step"/></strong></p></xsl:for-each>',
                                                                   },
                                                           'children': [],
                                                         },
   </xsl:template>
   
   <xsl:template match="epic" mode="detail">
         <tr>
         	<xsl:attribute name="class">epic</xsl:attribute>
         	<xsl:attribute name="id">node-epic-<xsl:value-of select="position()" /></xsl:attribute>
         	<td><xsl:value-of select="@id" /></td>
         	<td><xsl:value-of select="@title" /></td>
         	<td><xsl:value-of select="sum(child::story/@points)" /></td>     	
         	<xsl:variable name="num_passing"><xsl:value-of select="count(story/scenario[@testStatus='pass'])"/></xsl:variable>
         	<xsl:variable name="num_na"><xsl:value-of select="count(story/scenario[@testStatus='na'])"/></xsl:variable>
            <xsl:variable name="num_pending"><xsl:value-of select="count(story/scenario[@testStatus='pending'])"/></xsl:variable>
            <xsl:variable name="num_super"><xsl:value-of select="count(story/scenario[@testStatus='superflous'])"/></xsl:variable>
            <xsl:variable name="num_mismatch"><xsl:value-of select="count(story/scenario[@testStatus='mismatch'])"/></xsl:variable>
            <xsl:variable name="total_scenarios"><xsl:value-of select="count(story/scenario)"/></xsl:variable>
         	<td><xsl:if test="$num_passing=$total_scenarios">Complete</xsl:if><xsl:if test="$total_scenarios>($num_passing + $num_na)">Incomplete</xsl:if></td>
         	<td><xsl:number value="(($num_passing + $num_na) div $total_scenarios)*100"/>%</td>
         </tr>
         <xsl:apply-templates select="story" mode="detail"/>
   </xsl:template>
   <xsl:template match="story" mode="detail">
         <tr>
         	<xsl:attribute name="class">story child-of-node-epic-<xsl:value-of select="count(parent::*/preceding-sibling::*) + 1" /></xsl:attribute>
         	<xsl:attribute name="id">node-story-<xsl:value-of select="@id" /></xsl:attribute>
         	<td><xsl:value-of select="@id" /></td>
         	<td><xsl:value-of select="@title" /></td>
         	<td><xsl:value-of select="@points" /></td>
         	<xsl:variable name="num_passing"><xsl:value-of select="count(scenario[@testStatus='pass'])"/></xsl:variable>
         	<xsl:variable name="num_na"><xsl:value-of select="count(scenario[@testStatus='na'])"/></xsl:variable>
            <xsl:variable name="num_pending"><xsl:value-of select="count(scenario[@testStatus='pending'])"/></xsl:variable>
            <xsl:variable name="total_scenarios"><xsl:value-of select="count(scenario)"/></xsl:variable>
         	<td><xsl:if test="$num_passing=$total_scenarios">Complete</xsl:if><xsl:if test="$total_scenarios>($num_passing + $num_na)">Incomplete</xsl:if></td>
         	<td><xsl:number value="(($num_passing + $num_na) div $total_scenarios)*100"/>%</td>
         </tr>
         <xsl:apply-templates select="scenario" mode="detail"/>
   </xsl:template>
   <xsl:template match="scenario" mode="detail">
         <tr>
         	<xsl:attribute name="class">scenario child-of-node-story-<xsl:value-of select="../@id" /></xsl:attribute>
         	<xsl:attribute name="id">node-scenario-<xsl:value-of select="@id" /></xsl:attribute>
         	<td><xsl:value-of select="@name" /></td>
         	<td style="word-wrap:break-word">
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
         	<td><xsl:if test="@testStatus='pass' or @testStatus='na'">10</xsl:if>0%</td>
         </tr>
   </xsl:template>  
   
    <xsl:template match="epic" mode="failures">
         <xsl:variable name="num_failing_epic"><xsl:value-of select="count(story/scenario[@testStatus='fail'])"/></xsl:variable>
         <xsl:if test="$num_failing_epic>0">
	         <tr>
	         	<xsl:attribute name="class">epic</xsl:attribute>
	         	<xsl:attribute name="id">node-epic-failure-<xsl:value-of select="position()" /></xsl:attribute>
	         	<td><xsl:value-of select="@id" /></td>
	         	<td><xsl:value-of select="@title" /></td>
	         	<td></td>
	         	<td><strong><xsl:value-of select="count(story/scenario[@testStatus='fail'])"/>/<xsl:value-of select="count(story/scenario[@testStatus='fail']) + count(story/scenario[@testStatus='pass'])"/></strong></td>    	
	         </tr>
	         <xsl:apply-templates select="story" mode="failures"/>
	    </xsl:if>
   </xsl:template>
   <xsl:template match="story" mode="failures">
         <xsl:variable name="num_failing"><xsl:value-of select="count(scenario[@testStatus='fail'])"/></xsl:variable>
         <xsl:if test="$num_failing>0">
	         <tr>
	         	<xsl:attribute name="class">story child-of-node-epic-failure-<xsl:value-of select="count(parent::*/preceding-sibling::*) + 1" /></xsl:attribute>
	         	<xsl:attribute name="id">node-story-failure-<xsl:value-of select="@id" /></xsl:attribute>
	         	<td><xsl:value-of select="@id" /></td>
	         	<td><xsl:value-of select="@title" /></td>
	         	<td></td>
	         	<td><strong><xsl:value-of select="count(scenario[@testStatus='fail'])"/>/<xsl:value-of select="count(scenario[@testStatus='fail'])+count(scenario[@testStatus='pass'])"/></strong></td>
	         </tr>
	         <xsl:apply-templates select="scenario" mode="failures"/>
         </xsl:if>
   </xsl:template>
   <xsl:template match="scenario" mode="failures">
         <xsl:if test="@testStatus='fail'">
	         <tr>
	         	<xsl:attribute name="class">scenario child-of-node-story-failure-<xsl:value-of select="../@id" /></xsl:attribute>
	         	<xsl:attribute name="id">node-scenario-failure-<xsl:value-of select="@id" /></xsl:attribute>
	         	<td><xsl:value-of select="@name" /></td>
	         	<td>
		         	<xsl:for-each select="given">
		         		<xsl:sort select="position()" data-type="number" order="ascending"/>
		         		<p>Given, <xsl:value-of select="."/> - <xsl:value-of select="@duration"/>s</p>
		         	</xsl:for-each>
		         	<xsl:for-each select="when">
		         		<xsl:sort select="position()" data-type="number" order="ascending"/>
		         		<p>When, <xsl:value-of select="."/> - <xsl:value-of select="@duration"/>s</p>
		         	</xsl:for-each>
		         	<xsl:for-each select="then">
		         		<xsl:sort select="position()" data-type="number" order="ascending"/>
		         		<p>Then, <xsl:value-of select="."/> - <xsl:value-of select="@duration"/>s</p>
		         	</xsl:for-each>
	         	</td>
	         	<td><xsl:value-of select="@duration" />s</td>
	         	<td>
	         		<xsl:for-each select="failure">
		         		<p><strong>Failed at step: <xsl:value-of select="@step"/></strong></p>
		         		<xsl:if test="@link">
			         		<xsl:element name="a">
							  <xsl:attribute name="href">
							    <xsl:value-of select="@link"/>
							  </xsl:attribute>
							  <xsl:attribute name="target">_blank</xsl:attribute>
							  More Information
							</xsl:element>
						</xsl:if>
		         		<p><stong>Caused by: </stong></p>
			         	<p><xsl:value-of select="@cause"/></p>
			         	<p><strong>Stack Trace: </strong></p>
		         		<div style="background-color:white; width:100%; overflow:auto;">
		         				<xsl:for-each select="line"><span><xsl:value-of select="."/><br/></span></xsl:for-each>    
			         	</div>     	
		         	</xsl:for-each>
	         	</td>
	         </tr>
	    </xsl:if>
   </xsl:template> 
   
    <xsl:template match="epic" mode="problem">
     	<xsl:variable name="num_super"><xsl:value-of select="count(story/scenario[@testStatus='superfluous'])"/></xsl:variable>
        <xsl:variable name="num_mismatch_scenario"><xsl:value-of select="count(story/scenario[@testStatus='mismatch'])"/></xsl:variable>
        <xsl:variable name="num_mismatch_story"><xsl:value-of select="count(story[@requirementResolution='mismatch'])"/></xsl:variable>
        <xsl:if test="$num_super+$num_mismatch_scenario+$num_mismatch_story>0">
	         <tr>
	         	<xsl:attribute name="class">epic</xsl:attribute>
	         	<xsl:attribute name="id">node-epic-<xsl:value-of select="position()" /></xsl:attribute>
	         	<td><xsl:value-of select="@id" /></td>
	         	<td><xsl:value-of select="@title" /></td>
	         	<td></td>
	         </tr>
       	 	 <xsl:apply-templates select="story" mode="problem"/>
         </xsl:if>
   </xsl:template>
   <xsl:template match="story" mode="problem">
        <xsl:variable name="num_super"><xsl:value-of select="count(scenario[@testStatus='superfluous'])"/></xsl:variable>
        <xsl:variable name="num_mismatch"><xsl:value-of select="count(scenario[@testStatus='mismatch'])"/></xsl:variable>       
        <xsl:if test="$num_super+$num_mismatch>0 or @requirementResolution='mismatch'">
	         <tr>
	         	<xsl:attribute name="class">story child-of-node-epic-<xsl:value-of select="count(parent::*/preceding-sibling::*) + 1" /></xsl:attribute>
	         	<xsl:attribute name="id">node-story-<xsl:value-of select="@id" /></xsl:attribute>
	         	<td><xsl:value-of select="@id" /></td>
         		<td><xsl:value-of select="@title" /></td>
	         	<td><xsl:if test="@requirementResolution='mismatch'">Mismatch</xsl:if></td>
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
   
    <xsl:template match="epic" mode="defects">
         <xsl:variable name="num_defect_epic"><xsl:value-of select="count(story/scenario[@testStatus='defect'])"/></xsl:variable>
         <xsl:if test="$num_defect_epic>0">
	         <tr>
	         	<xsl:attribute name="class">epic</xsl:attribute>
	         	<xsl:attribute name="id">node-epic-defect-<xsl:value-of select="position()" /></xsl:attribute>
	         	<td><xsl:value-of select="@id" /></td>
	         	<td><xsl:value-of select="@title" /></td>
	         	<td><strong><xsl:value-of select="count(story/scenario[@testStatus='defect'])"/>/<xsl:value-of select="count(story/scenario)"/></strong></td>     	
	         </tr>
	         <xsl:apply-templates select="story" mode="defects"/>
	    </xsl:if>
   </xsl:template>
   <xsl:template match="story" mode="defects">
         <xsl:variable name="num_failing"><xsl:value-of select="count(scenario[@testStatus='defect'])"/></xsl:variable>
         <xsl:if test="$num_failing>0">
	         <tr>
	         	<xsl:attribute name="class">story child-of-node-epic-defect-<xsl:value-of select="count(parent::*/preceding-sibling::*) + 1" /></xsl:attribute>
	         	<xsl:attribute name="id">node-story-defect-<xsl:value-of select="@id" /></xsl:attribute>
	         	<td><xsl:value-of select="@id" /></td>
	         	<td><xsl:value-of select="@title" /></td>
	         	<td><strong><xsl:value-of select="count(scenario[@testStatus='defect'])"/>/<xsl:value-of select="count(scenario)"/></strong></td>
	         </tr>
	         <xsl:apply-templates select="scenario" mode="defects"/>
         </xsl:if>
   </xsl:template>
   <xsl:template match="scenario" mode="defects">
         <xsl:if test="@testStatus='defect'">
	         <tr>
	         	<xsl:attribute name="class">scenario child-of-node-story-defect-<xsl:value-of select="../@id" /></xsl:attribute>
	         	<xsl:attribute name="id">node-scenario-defect-<xsl:value-of select="@id" /></xsl:attribute>
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
	         	<td>
       				<p><strong><xsl:value-of select="@defect"/></strong></p>
	         	</td>
	         </tr>
	    </xsl:if>
   </xsl:template> 
     
</xsl:stylesheet>