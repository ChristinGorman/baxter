var Utils = function () {
	"use strict"

	var methods = {};

	methods.id = function(id) {
		return methods[id];
	}
	
	methods.sendToServer = function(servlet, data, returnValueListener) {
	    methods.servlet = servlet;
	    methods.data = data;
	    methods.returnValueListener = returnValueListener;
		var future = {};
		future.then = function(callback) {
			future.cb = callback;
		}
		return future;
	}
	
	//http://jsbin.com/ozeyag/19
	methods.validateEmail = function (email) { 
	    var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	    
	    return re.test(email);
	} 
	
	methods.toTitleCase = function(string) {
		return string.charAt(0).toUpperCase() + string.toLowerCase().substring(1)
	}

	methods.edit = function(name) {
	    console.log('edit ' + name);
   		Utils.id(name + 'Display').className ='invisible';
   		var select = Utils.id(name);
   		select.className = 'visible';
   		select.size = 3;
   		select.focus();
   	}

   	methods.doneEditing = function(name) {
   		var select = Utils.id(name);
   		var label = Utils.id(name + 'Display');
   		select.className = 'invisible';
   		var values = [];
   		for (var i = 0; i < select.selectedOptions.length; i++) {
   			values.push(select.selectedOptions[i].innerHTML);
   		}
   		label.value = values.join();
   		label.className = 'visible';
   	}

    methods.uploadFiles = function(fileItem, progressItem, servletName, previewArea) {
       methods.fileItem = fileItem;
       methods.progressItem = progressItem;
       methods.servletName = servletName;
       methods.previewArea = previewArea;
    }

	return methods;

}();