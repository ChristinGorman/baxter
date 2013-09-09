/*
DISCLAIMER!
My javascript code is a result of random trial and error, copy pasting from stackoverflow etc,
without any supervision from experts.
I freely admit, that I do not know what I am doing when it comes to javascript.
*/
var GrownUp = function () {
	"use strict"

	var page = {};
	
	page.addDaycareCentersToSelect = function(daycareCenters) {
		var select = Utils.id('grownup_daycare_id');
		select.length = 0;
		daycareCenters.forEach(function(daycare) {
			var option=document.createElement("option");
			option.text=daycare.daycare_name;
			option.value=daycare.daycare_id;
			select.add(option, null);
		});			
	}
	
	page.addChildrenToSelect = function(children) {
		var select = Utils.id('child_id');
		select.length = 0;
		if (children) {
          children.forEach(function(child) {
			var option=document.createElement("option");
			option.text=child.nickname;
			option.value=child.child_id;
			select.add(option, null);
		  });			
		}
	}

	page.addGroupsToSelect = function(groups) {
	  var select = Utils.id('club_id');
      select.length = 0;
      if (groups) {
        groups.forEach(function(group) {
    	  var option=document.createElement("option");
    	  option.text=group.club_name
    	  option.value=group.club_id;
    	  select.add(option, null);
    	});
      }
    }
	
	page.validateEmail = function() {
		var validated = Utils.validateEmail(Utils.id('email').value);
		Utils.id('submit').disabled = !validated;
		var error = Utils.id('emailError');
		error.value = 'Not a valid email address';
		error.className = validated ? 'invisible' : 'visible errorMessage';
		return validated;
	}
	
	page.validatePasswords = function() {
		var msg = null;
		if (Utils.id('password').value.length <= 4) {
			msg = 'password has to be more than 4 characters long';
		}else if (Utils.id('password').value !== Utils.id('repeatPassword').value) {
			msg = 'passwords didn\'t match';
		}else {
			msg = '';
		}
		var validated = msg === '';
		var error = Utils.id('passwordError');
		error.value = msg;
		error.className = validated ? 'invisible':'visible';
		Utils.id('submit').disabled = !validated;
		return validated;
	}
	
	page.display = function(grownUp) {
		if (!grownUp) return;
		page.displayed = grownUp;
		for (var prop in grownUp)    {
		   if (Utils.id(prop)) {
		     Utils.id(prop).value = grownUp[prop];
		   }
		}
		Data.getChildren(grownUp.grownup_daycare_id, GrownUp.addChildrenToSelect).then(selectGrownUpsChildren);
		Data.getGroups(grownUp.grownup_daycare_id, GrownUp.addGroupsToSelect).then(selectGrownUpsGroups);
	}
	
	var selectGrownUpsChildren = function() {
		Data.getChildrenForGrownUp(GrownUp.displayed.grownup_id, selectChildren);
	}

	var selectGrownUpsGroups = function() {
    	Data.getGroupsForGrownup(GrownUp.displayed.grownup_id, selectGroups);
    }

	var selectChildren = function(children) {
		var select = Utils.id('child_id');
		var names = [];
		if (select) {
			children.forEach(function(child) {
			  names.push(child.nickname);
			  for (var i = 0; i < select.options.length; i++) {
				if (select.options[i].value == child.child_id) {
					select.options[i].selected = true;
				}
			  }
		    });
			Utils.id('child_idDisplay').value = names.join(', ');
	    }
	}

	var selectGroups = function(groups) {
	  var select = Utils.id('club_id');
      var names = [];
      if (select) {
    	groups.forEach(function(group) {
    	names.push(group.club_name);
    	for (var i = 0; i < select.options.length; i++) {
    	  if (select.options[i].value == group.club_id) {
    		select.options[i].selected = true;
    	  }
    	}
      });
      Utils.id('club_idDisplay').value = names.join(', ');
      }
    }
	return page;
}();