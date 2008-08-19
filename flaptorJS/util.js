var util;

if (!util) {
	
	util = {
	
		//for debugging
	    checkLibrary : function () {
	        alert("flaptor javascript - util present");
	    },
	    
//////////////////////////// ADDING FUNCTIONALITY //////////////////////////////

	    includeScript : function (scriptSrc) {
		    var script = document.createElement("script");
		    script.type="text/javascript";
		    script.src=scriptSrc;
		    document.getElementsByTagName("head")[0].appendChild(script);
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
		
//////////////////////////// SHOWING, VIEWING //////////////////////////////
	    
	    setVisible: function (d, showOrNot) {
	        if (showOrNot)
	            document.getElementById(d).style.display = "block";
	        else 
	            document.getElementById(d).style.display = "none";
	    },
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
	

//////////////////////////// TIMEZONE  //////////////////////////////
		
	    getTimezone: function() {
	        return -(new Date()).getTimezoneOffset()/60;
	    },
	

		
//////////////////////////// CONFIRMATION  //////////////////////////////
		confirmUrl: function(text, url) {
	        if(confirm(text)) {
	            location.href = url;
	        }
	    },
	
//////////////////////////// VALIDATING & DATA //////////////////////////////

		isEmpty: function(value){
		    return (value.trim().length == 0);
		},
		isInt: function (value){
		    if (!this.isNumber(value))return false;
		    return value.search(/\./) ==-1; 
		},
		isNumber: function(value){
		    if (this.isEmpty(value)) return false;
		    return isFinite(value);
		},
	
		getText: function (id) {
		    return document.getElementById(id).firstChild.nodeValue;
		},
		setText: function (id, text) {
		    return document.getElementById(id).firstChild.nodeValue= text;
		},
	
		getValue: function (id) {
		    return document.getElementById(id).value;
		},

//////////////////////////// URL & LOCATION //////////////////////////////
		
		getParam: function (param, defaultValue){
		    matchParam = new RegExp(param + "=[^&]*");
		    matched = matchParam.exec(location.search);
		    if (matched != null) {
		    	return String(matched).slice(5)
		    } else {
		    	return defaultValue;
		    }
		},
		setParam: function (param, value){
		    if (this.getParam(param, null) != null) {
		        matchParam = new RegExp(param + "=[^&]*");
		        location.search = location.search.replace(matchParam, param+"="+value);
		    } else {
		        location.search += "&" + param + "=" + value;
		    }
		},

//////////////////////////// PAGINATION //////////////////////////////
		changePage: function (add, defaultValue){
		    pageNum = parseInt(this.getParam("page", 0));
		    this.setParam("page", pageNum + add);
		},
	};

	String.prototype.trim = function() {
	    return this.replace(/^\s+|\s+$/g,"");
	};
	String.prototype.ltrim = function() {
	    return this.replace(/^\s+/,"");
	};
	String.prototype.rtrim = function() {
	    return this.replace(/\s+$/,"");
	};
}