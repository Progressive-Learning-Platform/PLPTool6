/*
jQWidgets v4.5.0 (2017-Jan)
Copyright (c) 2011-2017 jQWidgets.
License: http://jqwidgets.com/license/
*/
!function(a){"use strict";a.jqx.jqxWidget("jqxNavigationBar","",{}),a.extend(a.jqx._jqxNavigationBar.prototype,{defineInstance:function(){var b={width:"auto",height:"auto",expandAnimationDuration:250,collapseAnimationDuration:250,animationType:"slide",toggleMode:"click",showArrow:!0,arrowPosition:"right",disabled:!1,initContent:null,rtl:!1,easing:"easeInOutSine",expandMode:"singleFitHeight",expandedIndexes:[],_expandModes:["singleFitHeight","single","multiple","toggle","none"],aria:{"aria-disabled":{name:"disabled",type:"boolean"}},events:["expandingItem","expandedItem","collapsingItem","collapsedItem"]};return a.extend(!0,this,b),b},createInstance:function(){this._isTouchDevice=a.jqx.mobile.isTouchDevice(),a.jqx.aria(this),this.render()},val:function(a){return 0===arguments.length||"object"==typeof a?this.expandedIndexes:("string"==typeof a?(this.expandedIndexes.push(parseInt(a,10)),this._applyExpandedIndexes()):(a instanceof Array?this.expandedIndexes=a:this.expandedIndexes=[a],this._applyExpandedIndexes()),this.expandedIndexes)},expandAt:function(b){var c=this;if("single"==this.expandMode||"singleFitHeight"==this.expandMode||"toggle"==this.expandMode)for(var d=0;d<c.items.length;d++)d!=b&&c.collapseAt(d);var e=this.items[b];if(e.disabled===!1&&e.expanded===!1&&1==e._expandChecker)switch(e._expandChecker=0,this._raiseEvent("0",{item:b}),e._headerHelper.removeClass(this.toThemeProperty("jqx-fill-state-normal")),e._headerHelper.addClass(this.toThemeProperty("jqx-fill-state-pressed jqx-expander-header-expanded")),e._arrowHelper.removeClass(this.toThemeProperty("jqx-icon-arrow-down jqx-icon-arrow-down-hover jqx-icon-arrow-up-hover jqx-icon-arrow-down-selected jqx-expander-arrow-top")),e._arrowHelper.addClass(this.toThemeProperty("jqx-icon-arrow-up jqx-icon-arrow-up-selected jqx-expander-arrow-bottom jqx-expander-arrow-expanded")),this.heightFlag===!1&&(c.element.style.overflowX="hidden",c.element.style.overflowY="hidden"),this.eCFlag=1,this.animationType){case"slide":var f=e._contentHelper,g=0,h=f.outerHeight();f.slideDown({duration:this.expandAnimationDuration,easing:this.easing,step:function(a,b){b.now=Math.round(a),"height"!==b.prop?g+=b.now:c._collapseContent?(b.now=Math.round(h-c._collapseContent.outerHeight()-g),g=0):b.now=Math.round(a)},complete:function(){e.expanded=!0,a.jqx.aria(e._header,"aria-expanded",!0),a.jqx.aria(e._content,"aria-hidden",!1),c._updateExpandedIndexes(),c._raiseEvent("1",{item:b}),c._checkHeight(),c.heightFlag===!0&&(c.element.style.overflowX="hidden",c.element.style.overflowY="auto"),c.initContent&&e._initialized===!1&&(c.initContent(b),e._initialized=!0),c.eCFlag=0}});break;case"fade":setTimeout(function(){e._contentHelper.fadeIn({duration:this.expandAnimationDuration,complete:function(){e.expanded=!0,a.jqx.aria(e._header,"aria-expanded",!0),a.jqx.aria(e._content,"aria-hidden",!1),c._updateExpandedIndexes(),c._raiseEvent("1",{item:b}),c._checkHeight(),c.heightFlag===!0&&(c.element.style.overflowX="hidden",c.element.style.overflowY="auto"),c.initContent&&e._initialized===!1&&(c.initContent(b),e._initialized=!0),c.eCFlag=0}})},this.collapseAnimationDuration);break;case"none":e._content.style.display="",e.expanded=!0,a.jqx.aria(e._header,"aria-expanded",!0),a.jqx.aria(e._content,"aria-hidden",!1),this._updateExpandedIndexes(),this._raiseEvent("1",{item:b}),this._checkHeight(),this.heightFlag===!0&&(c.element.style.overflowX="hidden",c.element.style.overflowY="auto"),this.initContent&&e._initialized===!1&&(this.initContent(b),e._initialized=!0),this.eCFlag=0}},collapseAt:function(b){var c=this.items[b];if(c.disabled===!1&&c.expanded===!0&&0===c._expandChecker){var d=this;switch(c._expandChecker=1,this._raiseEvent("2",{item:b}),c._headerHelper.removeClass(this.toThemeProperty("jqx-fill-state-pressed jqx-expander-header-expanded")),c._headerHelper.addClass(this.toThemeProperty("jqx-fill-state-normal")),c._arrowHelper.removeClass(this.toThemeProperty("jqx-icon-arrow-up jqx-icon-arrow-up-selected jqx-icon-arrow-down-selected jqx-expander-arrow-bottom jqx-expander-arrow-expanded")),c._arrowHelper.addClass(this.toThemeProperty("jqx-icon-arrow-down jqx-expander-arrow-top")),this.heightFlag===!1&&(d.element.style.overflowX="hidden",d.element.style.overflowY="hidden"),this.eCFlag=1,this._collapseContent=c._contentHelper,this.animationType){case"slide":var e=c._contentHelper;e.slideUp({duration:this.collapseAnimationDuration,step:function(a,b){b.now=Math.round(a)},easing:this.easing,complete:function(){c.expanded=!1,c._content.style.display="none",a.jqx.aria(c._header,"aria-expanded",!1),a.jqx.aria(c._content,"aria-hidden",!0),d._updateExpandedIndexes(),d._raiseEvent("3",{item:b}),d._checkHeight(),d.heightFlag===!0&&(d.element.style.overflowX="hidden",d.element.style.overflowY="auto"),d.eCFlag=0,d._collapseContent=null}});break;case"fade":c._contentHelper.fadeOut({duration:this.collapseAnimationDuration,complete:function(){c.expanded=!1,a.jqx.aria(c._header,"aria-expanded",!1),a.jqx.aria(c._content,"aria-hidden",!0),d._updateExpandedIndexes(),d._raiseEvent("3",{item:b}),d._checkHeight(),d.heightFlag===!0&&(d.element.style.overflowX="hidden",d.element.style.overflowY="auto"),d.eCFlag=0}});break;case"none":c._content.style.display="none",c.expanded=!1,a.jqx.aria(c._header,"aria-expanded",!1),a.jqx.aria(c._content,"aria-hidden",!0),this._updateExpandedIndexes(),this._raiseEvent("3",{item:b}),this._checkHeight(),this.heightFlag===!0&&(d.element.style.overflowX="hidden",d.element.style.overflowY="auto"),this.eCFlag=0}}},setHeaderContentAt:function(a,b){this.items[a]._headerText.innerHTML=b},getHeaderContentAt:function(a){return this.items[a]._headerText.innerHTML},setContentAt:function(a,b){this.items[a]._content.innerHTML=b,this._checkContent(a)},getContentAt:function(a){return this.items[a]._content.innerHTML},showArrowAt:function(a){this.items[a]._arrow.style.display="block"},hideArrowAt:function(a){this.items[a]._arrow.style.display="none"},enable:function(){this.disabled=!1,this._enabledDisabledCheck(),this.refresh(),a.jqx.aria(this,"aria-disabled",!1)},disable:function(){this.disabled=!0,this._enabledDisabledCheck(),this.refresh(),a.jqx.aria(this,"aria-disabled",!0)},enableAt:function(a){this.items[a].disabled=!1,this.refresh()},disableAt:function(a){this.items[a].disabled=!0,this.refresh()},invalidate:function(){this.refresh()},refresh:function(a){if(a!==!0){this._removeHandlers();for(var b=0;b<this.items.length;b++)this.items[b]._arrow.style.display=this.showArrow?"block":"none";this._updateExpandedIndexes(),this._setTheme(),this._setSize(),this._toggle(),this._keyBoard()}},render:function(){this.widgetID=this.element.id;var b=this;this._expandModes.indexOf(this.expandMode)==-1&&(this.expandMode="singleFitHeight"),a.jqx.utilities.resize(this.host,function(){b._setSize()}),b.element.setAttribute("role","tablist"),this.items&&(this._removeHandlers(),a.each(this.items,function(){this._header.className="",this._header.setAttribute("tabindex",null),this._header.style.marginTop="0px",this._headerText.className="",this._header.innerHTML=this._headerText.innerHTML,this._content.setAttribute("tabindex",null)})),this.items=[];var c=b.host.children(),d=c.length,e="Invalid jqxNavigationBar structure. Please add an even number of child div elements that will represent each item's header and content.";try{if(d%2!==0)throw e}catch(a){throw new Error(a)}var f="Invalid jqxNavigationBar structure. Please make sure all the children elements of the navigationbar are divs.";try{for(var g=0;g<d;g++)if("div"!=c[g].tagName.toLowerCase())throw f}catch(a){throw new Error(a)}for(var h=0;h<d;h+=2){var i=c[h];i.innerHTML="<div>"+i.innerHTML+"</div>"}for(var j,k=0,l=0;l<d/2;l++){j=k+1;var m={};m={},m._header=c[k],m._headerHelper=a(c[k]),c[k].setAttribute("role","tab"),m._content=c[j],m._contentHelper=a(c[j]),m._contentHelper.initAnimate&&m._contentHelper.initAnimate(),m.expandedFlag=!1,m.expanded=!1,m.focusedH=!1,m.focusedC=!1,this.items[l]=m,c[j].setAttribute("role","tabpanel"),k+=2}var n=this.expandedIndexes.length;if(!this.items||0!==this.items.length){if("single"==this.expandMode||"singleFitHeight"==this.expandMode||"toggle"==this.expandMode||"none"==this.expandMode)0!==n?this.items[this.expandedIndexes[0]].expanded=!0:0!==n||"single"!=this.expandMode&&"singleFitHeight"!=this.expandMode||(this.items[0].expanded=!0);else if("multiple"==this.expandMode&&0!==n)for(var o=0;o<n;o++)b.items[this.expandedIndexes[o]].expanded=!0;this._enabledDisabledCheck();var p=0;a.each(this.items,function(c){var d=this;d._headerText=a(d._header).children()[0],b.rtl?a(d._headerText).addClass(b.toThemeProperty("jqx-expander-header-content-rtl")):a(d._headerText).addClass(b.toThemeProperty("jqx-expander-header-content")),d._arrow=document.createElement("div"),d._arrowHelper=a(d._arrow),d._header.appendChild(d._arrow),b.showArrow?d._arrow.style.display="block":d._arrow.style.display="none",d.expanded===!0?(d._arrowHelper.addClass(b.toThemeProperty("jqx-icon-arrow-up jqx-icon-arrow-up-selected jqx-expander-arrow-bottom jqx-expander-arrow-expanded")),b.initContent?setTimeout(function(){b.initContent(c),d._initialized=!0},10):d._initialized=!0,d._expandChecker=0,a.jqx.aria(d._header,"aria-expanded",!0),a.jqx.aria(d._content,"aria-hidden",!1)):d.expanded===!1&&(d._arrowHelper.addClass(b.toThemeProperty("jqx-icon-arrow-down jqx-expander-arrow-top")),d._initialized=!1,d._expandChecker=1,d._content.style.display="none",a.jqx.aria(d._header,"aria-expanded",!1),a.jqx.aria(d._content,"aria-hidden",!0)),null===d._header.getAttribute("tabindex")&&(p++,d._header.setAttribute("tabindex",p)),null===d._content.getAttribute("tabindex")&&(p++,d._content.setAttribute("tabindex",p))}),this._setTheme(),this._setSize();for(var q=0;q<b.items.length;q++)b._checkContent(q);this._toggle(),this._keyBoard()}},insert:function(a,b,c){var d=document.createElement("div"),e=document.createElement("div");if(d.innerHTML=b,e.innerHTML=c,a>=0&&a<=this.items.length){var f=this.items[a]._header;this.element.insertBefore(d,f),this.element.insertBefore(e,f)}else this.element.appendChild(d),this.element.appendChild(e);this.render()},add:function(a,b){this.insert(-1,a,b)},update:function(a,b,c){this.setHeaderContentAt(a,b),this.setContentAt(a,c)},remove:function(a){if(isNaN(a)&&(a=this.items.length-1),this.items[a]){this.items[a]._header.remove(),this.items[a]._content.remove(),this.items.splice(a,1);var b=this.expandedIndexes.indexOf(a);b>-1&&this.expandedIndexes.splice(b,1),this.render()}},destroy:function(){this._removeHandlers(),this.host.remove()},focus:function(){try{for(var a=0;a<this.items.length;a++){var b=this.items[a];if(b.disabled===!1)return b._header.focus(),!1}}catch(a){}},_applyExpandedIndexes:function(){for(var a=this,b=this.expandedIndexes.length,c=0;c<b;c++)for(var d=a.expandedIndexes[c],e=0;e<a.items.length;e++){var f=a.items[e];if(e==d){if(f.expandedFlag=!0,f.expanded===!1&&a.expandAt(e),"single"==a.expandMode||"singleFitHeight"==a.expandMode||"toggle"==a.expandMode||"none"==a.expandMode)return!1}else e!=d&&f.expandedFlag===!1&&a.collapseAt(e)}for(var g=0;g<a.items.length;g++)a.items[g].expandedFlag=!1},propertiesChangedHandler:function(a,b,c){c.width&&c.height&&2==Object.keys(c).length&&a._setSize()},propertyChangedHandler:function(a,b,c,d){if(!(a.batchUpdate&&a.batchUpdate.width&&a.batchUpdate.height&&2==Object.keys(a.batchUpdate).length))return"width"==b||"height"==b?void a._setSize():void("disabled"==b?a._enabledDisabledCheck():"expandedIndexes"==b?a._applyExpandedIndexes():a.refresh())},_raiseEvent:function(b,c){var d=this.events[b],e=new a.Event(d);e.owner=this,e.args=c,e.item=e.args.item;var f;try{f=this.host.trigger(e)}catch(a){}return f},resize:function(a,b){this.width=a,this.height=b,this._setSize()},_setSize:function(){var b=this;this.headersHeight=0;var c=this.items&&this.items.length>0?parseInt(this.items[0]._headerHelper.css("padding-left"),10):0,d=this.items&&this.items.length>0?parseInt(this.items[0]._headerHelper.css("padding-right"),10):0,e=2,f=c+d+e;isNaN(f)&&(f=12),"auto"==this.width?b.element.style.width="auto":null!=this.width&&this.width.toString().indexOf("%")!=-1?b.element.style.width=b.width:b.element.style.width=parseInt(this.width,10)+f+"px","number"==typeof b.height?b.element.style.height=b.height+"px":b.element.style.height=b.height;for(var g=0;g<b.items.length;g++){var h=b.items[g],i=b.arrowPosition;if(b.rtl)switch(i){case"left":i="right";break;case"right":i="left"}"right"==i?(h._headerText.style.float="left",h._headerText.style.marginLeft="0px",h._arrow.style.float="right",h._arrow.style.position="relative"):"left"==i&&("auto"==b.width?(h._headerText.style.float="left",h._headerText.style.marginLeft="17px",h._arrow.style.float="left",h._arrow.style.position="absolute"):(h._headerText.style.float="right",h._headerText.style.marginLeft="0px",h._arrow.style.float="left",h._arrow.style.position="relative")),h._header.style.height="auto",h._headerText.style.minHeight=h._arrow.offsetHeight,b.headersHeight+=a(h._header).outerHeight(),h._arrow.style.marginTop=h._headerText.offsetHeight/2-h._arrow.offsetHeight/2+"px"}for(var j=0;j<b.items.length;j++){var k=b.items[j];if("auto"!=b.height)if("single"==b.expandMode||"toggle"==b.expandMode||"multiple"==b.expandMode)b.element.style.overflowX="hidden",b.element.style.overflowY="auto";else if("singleFitHeight"==b.expandMode){var l=parseInt(k._contentHelper.css("padding-top"),10)+parseInt(k._contentHelper.css("padding-bottom"),10);b.height&&b.height.toString().indexOf("%")>=0?k._content.style.height=Math.max(0,b.element.offsetHeight-b.headersHeight-l+2)+"px":k._content.style.height=Math.max(0,b.element.offsetHeight-b.headersHeight-l)+"px"}}b._checkHeight()},_toggle:function(){var b=this;if(this._isTouchDevice===!1)switch(this.toggleMode){case"click":case"dblclick":a.each(this.items,function(a){var c=this;c.disabled===!1&&b.addHandler(c._header,b.toggleMode+".navigationbar"+b.widgetID,function(){b.focusedH=!0,b._animate(a)})});break;case"none":}else{if("none"==this.toggleMode)return;a.each(this.items,function(c){var d=this;d.disabled===!1&&b.addHandler(d._header,a.jqx.mobile.getTouchEventName("touchstart")+"."+b.widgetID,function(){b._animate(c)})})}},_animate:function(a,b){var c=this,d=this.items[a];"none"!=this.expandMode&&1!=this.eCFlag&&(this.items[a].expanded===!0?"multiple"!=this.expandMode&&"toggle"!=this.expandMode||this.collapseAt(a):this.expandAt(a),c._isTouchDevice||(b!==!0?(d._headerHelper.addClass(this.toThemeProperty("jqx-fill-state-hover jqx-expander-header-hover")),d._arrowHelper.addClass(this.toThemeProperty("jqx-expander-arrow-top-hover jqx-expander-arrow-down-hover"))):(d._headerHelper.removeClass(this.toThemeProperty("jqx-fill-state-hover jqx-expander-header-hover")),d._arrowHelper.removeClass(this.toThemeProperty("jqx-expander-arrow-top-hover jqx-expander-arrow-down-hover")))))},_removeHandlers:function(){var a=this;this.removeHandler(this.host,"keydown.navigationbar"+this.widgetID);for(var b=0;b<a.items.length;b++){var c=a.items[b];a.removeHandler(c._header,"click.navigationbar"+a.widgetID),a.removeHandler(c._header,"dblclick.navigationbar"+a.widgetID),a.removeHandler(c._header,"mouseenter.navigationbar"+a.widgetID),a.removeHandler(c._header,"mouseleave.navigationbar"+a.widgetID),a.removeHandler(c._header,"focus.navigationbar"+a.widgetID),a.removeHandler(c._header,"blur.navigationbar"+a.widgetID),a.removeHandler(c._content,"focus.navigationbar"+a.widgetID),a.removeHandler(c._content,"blur.navigationbar"+a.widgetID),a.removeHandler(c._headerText,"focus.navigationbar"+a.widgetID),a.removeHandler(c._arrow,"focus.navigationbar"+a.widgetID)}},_setTheme:function(){var b=this;this.host.addClass(this.toThemeProperty("jqx-reset jqx-widget")),this.rtl===!0&&this.host.addClass(this.toThemeProperty("jqx-rtl")),a.each(this.items,function(a){var c=this,d=c._headerHelper,e=c._arrowHelper,f=c._contentHelper,g="jqx-widget-header jqx-item jqx-expander-header",h="jqx-widget-content jqx-expander-content jqx-expander-content-bottom";c._header.style.position="relative",c._content.style.position="relative",c.disabled===!1?(d.removeClass(b.toThemeProperty("jqx-fill-state-disabled")),f.removeClass(b.toThemeProperty("jqx-fill-state-disabled")),c.expanded===!0?g+=" jqx-fill-state-pressed jqx-expander-header-expanded":(g+=" jqx-fill-state-normal",d.removeClass(b.toThemeProperty("jqx-expander-header-expanded"))),b._isTouchDevice||(b.addHandler(c._header,"mouseenter.navigationbar"+b.widgetID,function(){1==c._expandChecker&&(c.focusedH||(c._header.style.zIndex=5),d.removeClass(b.toThemeProperty("jqx-fill-state-normal jqx-fill-state-pressed")),d.addClass(b.toThemeProperty("jqx-fill-state-hover jqx-expander-header-hover")),e.addClass(b.toThemeProperty("jqx-expander-arrow-top-hover jqx-expander-arrow-down-hover")),c.expanded?e.addClass(b.toThemeProperty("jqx-icon-arrow-up-hover")):e.addClass(b.toThemeProperty("jqx-icon-arrow-down-hover")))}),b.addHandler(c._header,"mouseleave.navigationbar"+b.widgetID,function(){c.focusedH||(c._header.style.zIndex=0),d.removeClass(b.toThemeProperty("jqx-fill-state-hover jqx-expander-header-hover")),e.removeClass(b.toThemeProperty("jqx-expander-arrow-top-hover jqx-expander-arrow-down-hover jqx-icon-arrow-up-hover jqx-icon-arrow-down-hover")),1==c._expandChecker?d.addClass(b.toThemeProperty("jqx-fill-state-normal")):d.addClass(b.toThemeProperty("jqx-fill-state-pressed"))}))):(g+=" jqx-fill-state-disabled",h+=" jqx-fill-state-disabled"),b.host.addClass(b.toThemeProperty("jqx-navigationbar")),d.addClass(b.toThemeProperty(g)),f.addClass(b.toThemeProperty(h)),0!==a&&(c._header.style.marginTop="-1px"),e.addClass(b.toThemeProperty("jqx-expander-arrow"))})},_checkContent:function(a){var b=this.items[a],c=b._content;if(this._cntntEmpty=/^\s*$/.test(this.items[a]._content.innerHTML),this._cntntEmpty===!0)c.style.display="none",c.style.height="0px",b._contentHelper.addClass(this.toThemeProperty("jqx-expander-content-empty"));else{if(b.expanded&&(c.style.display="block"),"singleFitHeight"==this.expandMode){var d=1;0!==a&&(d=2),c.style.height=Math.max(0,this.element.offsetHeight-this.headersHeight+d)+"px"}else c.style.height="auto";b._contentHelper.removeClass(this.toThemeProperty("jqx-expander-content-empty"))}},_checkHeight:function(){var a=this;if("string"!=typeof a.width||a.width.indexOf("%")===-1){var b=0,c=this.items&&this.items.length>0?parseInt(this.items[0]._headerHelper.css("padding-left"),10):0,d=this.items&&this.items.length>0?parseInt(this.items[0]._headerHelper.css("padding-right"),10):0,e=2,f=c+d+e;isNaN(f)&&(f=12);for(var g=17,h=0;h<a.items.length;h++){var i=a.items[h];b+=(i.expanded?i._contentHelper.outerHeight():0)+i._headerHelper.outerHeight()}"auto"!=this.width&&"auto"!=this.height&&"singleFitHeight"!=this.expandMode&&(b>a.element.offsetHeight?(a.element.style.width=parseInt(this.width,10)+f+g+"px",this.heightFlag=!0):(a.element.style.width=parseInt(this.width,10)+f+"px",this.heightFlag=!1))}},_enabledDisabledCheck:function(){for(var a=0;a<this.items.length;a++)this.items[a].disabled=this.disabled},_updateExpandedIndexes:function(){var b=this;this.expandedIndexes=[],a.each(this.items,function(a){var c=this;if(c.expanded===!0&&(b.expandedIndexes.push(a),"single"==b.expandMode||"singleFitHeight"==b.expandMode||"toggle"==b.expandMode||"none"==b.expandMode))return!1})},_keyBoard:function(){var b=this;this._focus(),this.addHandler(this.host,"keydown.navigationbar"+this.widgetID,function(c){var d=!1,e=b.items.length;return a.each(b.items,function(a){var f=this;if((f.focusedH===!0||f.focusedC===!0)&&f.disabled===!1){switch(c.keyCode){case 13:case 32:"none"!=b.toggleMode&&(f.focusedH===!0&&b._animate(a,!0),d=!0);break;case 37:0!==a?b.items[a-1]._header.focus():b.items[e-1]._header.focus(),d=!0;break;case 38:c.ctrlKey===!1?0!==a?b.items[a-1]._header.focus():b.items[e-1]._header.focus():f.focusedC===!0&&f._header.focus(),d=!0;break;case 39:a!=e-1?b.items[a+1]._header.focus():b.items[0]._header.focus(),d=!0;break;case 40:c.ctrlKey===!1?a!=e-1?b.items[a+1]._header.focus():b.items[0]._header.focus():f.expanded===!0&&f._content.focus(),d=!0;break;case 35:a!=e-1&&b.items[e-1]._header.focus(),d=!0;break;case 36:0!==a&&b.items[0]._header.focus(),d=!0}return!1}}),d&&c.preventDefault&&c.preventDefault(),!d})},_focus:function(){var b=this;this.disabled||a.each(this.items,function(){var c=this;b.addHandler(c._header,"focus.navigationbar"+this.widgetID,function(){c.focusedH=!0,a.jqx.aria(c._header,"aria-selected",!0),c._headerHelper.addClass(b.toThemeProperty("jqx-fill-state-focus")),c._header.style.zIndex=10}),b.addHandler(c._header,"blur.navigationbar"+this.widgetID,function(){c.focusedH=!1,a.jqx.aria(c._header,"aria-selected",!1),c._header.className.indexOf("jqx-expander-header-hover")!==-1?c._header.style.zIndex=5:c._header.style.zIndex=0,c._headerHelper.removeClass(b.toThemeProperty("jqx-fill-state-focus"))}),b.addHandler(c._headerText,"focus.navigationbar"+this.widgetID,function(){c._header.focus()}),b.addHandler(c._arrow,"focus.navigationbar"+this.widgetID,function(){c._header.focus()}),b.addHandler(c._content,"focus.navigationbar"+this.widgetID,function(){c.focusedC=!0,c._contentHelper.addClass(b.toThemeProperty("jqx-fill-state-focus"))}),b.addHandler(c._content,"blur.navigationbar"+this.widgetID,function(){c.focusedC=!1,c._contentHelper.removeClass(b.toThemeProperty("jqx-fill-state-focus"))})})}})}(jqxBaseFramework);

