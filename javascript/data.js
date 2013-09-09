/*
DISCLAIMER!
My javascript code is a result of random trial and error, copy pasting from stackoverflow etc,
without any supervision from experts.
I freely admit, that I do not know what I am doing when it comes to javascript.
*/
var Data = function () {
	"use strict"

	var methods = {};
	methods.getDaycareCenters = function(callback) {		
		return Utils.sendToServer('data', 'query=getAllDaycareCenters', callback);
	}
	
	methods.getChildren = function(daycareCenterId, callback) {
		return Utils.sendToServer('data', 'query=childrenInDaycare&daycare_id=' + daycareCenterId, callback);
	}	
	
	methods.getChildrenForGrownUp = function(grownUpId, callback) {
		return Utils.sendToServer('data', 'query=childrenForGrownup&grownup_id=' + grownUpId, callback);
	}

	methods.getGrownUp = function(grownUpId, callback) {
		return Utils.sendToServer('data', 'query=getGrownup&grownup_id=' + grownUpId, callback);
	}

	methods.getChild = function(childId, callback) {
    	return Utils.sendToServer('data', 'query=getChild&child_id=' + childId, callback);
    }

    methods.getLoggedInInfo = function(callback) {
        return Utils.sendToServer('data', 'query=getLoggedIn', callback);
    }

    methods.getGroups = function(daycareId, callback) {
        return Utils.sendToServer('data', 'query=getAllGroups&daycare_id=' + daycareId, callback);
    }

    methods.getGroupsForGrownup = function(grownupId, callback) {
        return Utils.sendToServer('data', 'query=getGroupsForGrownup&grownup_id=' + grownupId, callback);
    }


	return methods;

}();