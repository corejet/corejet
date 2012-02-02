var labelType, useGradients, nativeTextSupport, animate;

$(document)
		.ready(
				function() {

					(function() {
						var ua = navigator.userAgent, iStuff = ua
								.match(/iPhone/i)
								|| ua.match(/iPad/i), typeOfCanvas = typeof HTMLCanvasElement, nativeCanvasSupport = (typeOfCanvas == 'object' || typeOfCanvas == 'function'), textSupport = nativeCanvasSupport
								&& (typeof document.createElement('canvas')
										.getContext('2d').fillText == 'function');
						// I'm setting this based on the fact that ExCanvas
						// provides text support for IE
						// and that as of today iPhone/iPad current text support
						// is lame
						labelType = (!nativeCanvasSupport || (textSupport && !iStuff)) ? 'Native'
								: 'HTML';
						nativeTextSupport = labelType == 'Native';
						useGradients = nativeCanvasSupport;
						animate = !(iStuff || !nativeCanvasSupport);
					})();

					var Log = {
						elem : false,
						write : function(text) {
							if (!this.elem)
								this.elem = document.getElementById('log');
							this.elem.innerHTML = text;
							this.elem.style.left = (500 - this.elem.offsetWidth / 2)
									+ 'px';
						}
					};
					
					$('#project').text( metadata.project );
					$('#testTime').text( metadata.testTime );

					var tm = new $jit.TM.Squarified(
							{
								// where to inject the visualization
								injectInto : 'infovis',
								// parent box title heights
								titleHeight : 30,
								// enable animations
								animate : animate,
								// box offsets
								offset : 2,
								levelsToShow : 3,
								// Attach left and right click events
								Events : {
									enable : true,
									onClick : function(node) {
										if (node)
											tm.enter(node);
									},
									onRightClick : function() {
										tm.out();
									}
								},
								duration : 1000,
								// Enable tips
								Tips : {
									enable : true,
									// add positioning offsets
									offsetX : 20,
									offsetY : 20,
									// implement the onShow method to
									// add content to the tooltip when a node
									// is hovered
									onShow : function(tip, node, isLeaf,
											domElement) {
										var boxHtml = "<div class=\"tip-title\">"
												+ node.name
												+ "</div><div class=\"tip-text\">";
										var data = node.data;
										if (data.url) {
											boxHtml += "<a href=\""
													+ data.url
													+ "\" target=\"_blank\" />node.id</a>";
										}
										
										if (data.title==undefined) {
											data.title = "";
										}
										
										tip.innerHTML = "<div class=\"tip-title\">"
											    + node.name
											    + ": "
												+ data.title
												+ "</div><div class=\"tip-text\">";
									}
								},
								// Add the name of the node in the correponding
								// label
								// This method is called once, on label
								// creation.
								onCreateLabel : function(domElement, node) {
									domElement.innerHTML = "<span style=\"position:absolute; margin:5px\">"+node.name+"</span>";
									var style = domElement.style;
									style.display = '';
									style.border = '1px solid transparent';
									domElement.onmouseover = function() {
										style.border = '1px solid #9fd4ff';
									};
									domElement.onmouseout = function() {
										style.border = '1px solid transparent';
									};
									domElement.onmousedown = function(e){
										if(e.which == 2 || e.which == 3 ) {
											if (!node.anySubnode()){
												domElement.innerHTML= "";
											};
										} else {	if (!node.anySubnode()){

												var fontColor = white;
												if (node.getData('color') == fontColor){
													fontColor = 'black';
												};
												domElement.innerHTML= "<div style=\"position:absolute; margin:10px; overflow:auto; font-size:1.2em; color:"+fontColor+"; \">"+node.getData('text')+"</div>";
											};

										};	
									}
								}
							});
					tm.loadJSON(json);
					tm.refresh();

				});
