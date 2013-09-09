/*
DISCLAIMER!
My javascript code is a result of random trial and error, copy pasting from stackoverflow etc,
without any supervision from experts.
I freely admit, that I do not know what I am doing when it comes to javascript.
*/
var Utils = function () {
	"use strict"

	var methods = {};
	
	methods.id = function(id) {
		return document.getElementById(id);
	}
	
	methods.sendToServer = function(servlet, data, returnValueListener) {
		var future = {};
		future.then = function(callback) {
			future.cb = callback;
		}
		var xmlhttp = new XMLHttpRequest();
		xmlhttp.onreadystatechange = function() {
		    if (xmlhttp.readyState == 4 && xmlhttp.status == 200 && xmlhttp.responseText && returnValueListener) {
			    var json = JSON.parse(xmlhttp.responseText);
				if (json) {
					returnValueListener(json);
					if (future.cb) {
					  future.cb();
					}
				}
				if (Utils.id('errorMessage')) {
				  Utils.id('errorMessage').innerHTML = '';
				}
			} else {
			   if (xmlhttp.responseText.indexOf('Not logged in') >= 0) {
			     window.location='index.html';
			   } else if (Utils.id('errorMessage')) {
			     var startOfError = xmlhttp.responseText.indexOf('Reason:') + 7;
			     var javaException = xmlhttp.responseText.substring(startOfError);
			     javaException = javaException.substring(javaException.indexOf(':') + 1);
			     Utils.id('errorMessage').innerHTML = javaException.substring(0, javaException.indexOf('\n'));
			   }
			}
		};
		xmlhttp.open('POST', servlet, true);
		xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		console.log(data);
		xmlhttp.send(data);
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
      if (previewArea) {
        previewFiles(fileItem, previewArea)
      }
      for (var i = 0; i < fileItem.files.length; i++) {
        var xhr = new XMLHttpRequest();
        xhr.open("POST", servletName, true);
        if (progressItem) {
          xhr.onload = function() {progressItem.value = 0;};
          xhr.upload.onprogress = function(event) {
            if (event.lengthComputable) {
              var complete = (event.loaded / event.total * 100 | 0);
              progressItem.value = complete;
            } else {
              console.log('length not computable ' + progressItem.value);
              progressItem.value = progressItem.value + 1;
            }
          };
        }
        var file = fileItem.files[i];
        console.log('upload ' + file);
        var formData = new FormData();
        formData.append('file', file);
        xhr.send(formData);
      }
    }

    var previewFiles= function(fileItem, previewArea){
      for (var i = 0; i < fileItem.files.length; i++) {
        var file = fileItem.files[i];
        var reader = new FileReader();
        reader.onload = (function(theFile) {
          var span = document.createElement('span');
          span.innerHTML = ['<img style="height:75px;" src="', theFile.target.result, '" title="event photo"/>'].join('');
          previewArea.appendChild(span);
        });
        reader.readAsDataURL(file);
      }
    }

	return methods;

}();