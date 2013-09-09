/*
DISCLAIMER!
My javascript code is a result of random trial and error, copy pasting from stackoverflow etc,
without any supervision from experts.
I freely admit, that I do not know what I am doing when it comes to javascript.
*/
var Child = function () {
	"use strict"

	var page = {};

    page.addDaycareCentersToSelect = function(daycareCenters) {
		var select = Utils.id('daycare');
		select.length = 0;
		daycareCenters.forEach(function(daycare) {
			var option=document.createElement("option");
			option.text=daycare.daycare_name;
			option.value=daycare.daycare_id;
			select.add(option, null);
		});
	}

	page.display = function(child) {
	  if (!child) return;
	  page.child = child;
	  for (var prop in child){
        if (Utils.id(prop)) {
          Utils.id(prop).value = child[prop];
        }
      }
	}
	
	return page;

}();