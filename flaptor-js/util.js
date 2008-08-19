var util;

if (!util) {
util = {
    //for debugging
    checkLibrary : function () {
        alert("ok");
    },
    includeScript : function (scriptSrc) {
	    var script = document.createElement("script");
	    script.type="text/javascript";
	    script.src=scriptSrc;
	    document.getElementsByTagName("head")[0].appendChild(script);
    },

    getTimezone: function() {
        return -(new Date()).getTimezoneOffset()/60;
    },

    show: function (d, showOrNot) {
        if (showOrNot)
            document.getElementById(d).style.display = "block";
        else 
            document.getElementById(d).style.display = "none";
    }
    show: function (d) {
        document.getElementById(d).style.display = "block";
    },
	hide: function (d) {
	    document.getElementById(d).style.display = "none";
	},
	toggle: function (d) {
	    if(d.length < 1) { return; }
	    if(document.getElementById(d).style.display == "none") { document.getElementById(d).style.display = "block"; }
	    else { document.getElementById(d).style.display = "none"; }
	},

    confirmUrl: function(text, url) {
        if(confirm(text)) {
            location.href = url;
        }
    },

	isEmpty: function(value){
	    return (value.trim().length == 0);
	},
	
	isInt: function (value){
	    if (!isNumber(value))return false;
	    return value.search(/\./) ==-1; 
	},
	 isNumber: function(value){
	    if (isEmpty(value)) return false;
	    return isFinite(value);
	},
	getText: function (id) {
	    return document.getElementById(id).firstChild.nodeValue;
	},
	setText: function (id, text) {
	    return document.getElementById(id).firstChild.nodeValue= text;
	},

	addToOnresize: function (myFunction) {
	    var oldResize = window.onresize;
	    window.onresize=function(){
	        if (oldResize != null) oldResize();
	        myFunction();
	    }
	},
	addToOnload: function (myFunction) {
	    var oldOnload = window.onload;
	    window.onload=function(){
	        if (oldOnload != null) oldOnload();
	        myFunction();
	    }
	},
	addTimeplot: function (timeplotDiv, lineInfos, plotData, enableValues) {
	    var valueGeometry = new Timeplot.DefaultValueGeometry({
	              gridColor: "#000000",
	              axisLabelsPlacement: "left",
	              min: 0
	          });
	    var timeGeometry = new Timeplot.DefaultTimeGeometry({
	              gridColor: new Timeplot.Color("#000000"),
	              axisLabelsPlacement: "top",
	              gridSpacing: 7,
	              gridStep: 2
	          })
	    var eventSource = new Timeplot.DefaultEventSource();
	    var plotInfo = [];
	    for (i = 0; i < lineInfos.length; i++) {
	        plotInfo[i] = Timeplot.createPlotInfo({
	            id: "plot"+lineInfos[i].id,
	            lineColor: lineInfos[i].color,
	            lineWidth: 2,
	            dataSource: new Timeplot.ColumnSource(eventSource, lineInfos[i].id * 2),
	            valueGeometry: valueGeometry,
	            timeGeometry: timeGeometry,
	            showValues: enableValues
	        });
	    }
	    timeplotDiv.style.backgroundColor= "white";
	    var ret = Timeplot.create(timeplotDiv, plotInfo);
	    eventSource.loadText(plotData, ",", "/");
	    var resizeTimerId = null;
	    flaptor.addToOnresize(function(){
	        if (resizeTimerId == null) {
	            resizeTimerId = window.setTimeout(function() {resizeTimerId = null; ret.repaint(); }, 100);
	        }
	    });
	    return ret;
	}
}

String.prototype.trim = function() {
    return this.replace(/^\s+|\s+$/g,"");
}
String.prototype.ltrim = function() {
    return this.replace(/^\s+/,"");
}
String.prototype.rtrim = function() {
    return this.replace(/\s+$/,"");
}
util.includeScript("http://static.simile.mit.edu/timeplot/api/1.0/timeplot-api.js");            
}
