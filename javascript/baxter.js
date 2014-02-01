/*
DISCLAIMER!
My javascript code is a result of random trial and error, copy pasting from stackoverflow etc,
without any supervision from experts.
I freely admit, that I do not know what I am doing when it comes to javascript.
*/
var Baxter = function Baxter($scope) {
	"use strict"

    $scope.events = [];
    $scope.children = [];
    $scope.scheduleNames = [];
    $scope.clubs = [];
    $scope.chosenChild = null;
    $scope.chosenSchedule = null;

    $scope.addScheduleNames = function(scheduleNames) {
      scheduleNames.forEach(function(name){$scope.scheduleNames.push(name)});
    }

    $scope.addClubs = function(clubs) {
      clubs.forEach(function (club){
        $scope.clubs.push(club)
        var option=document.createElement("option");
        option.text=club.club_name;
        option.value=club.club_id;
        Utils.id('ec_club_id').add(option, null);
      });
    }

    $scope.addUpdates = function(stuff) {
      stuff.forEach(function(thing) {
        if (thing.schedule_child_id) {
          var found = $scope.findChild(thing.schedule_child_id);
          ScheduleFunctions.findSchedule(found, thing.schedule_name).last_event = thing.last_event;
        } else if (thing.event_id) {
          if (!$scope.findEvent(thing.event_id))
            Utils.sendToServer('timeline', 'action=getEvent&event_id=' + thing.event_id, $scope.addNewEvent);
        } else if (thing.child_id) {
          $scope.updateChild(thing);
        }
      });

	  $scope.children.forEach(function(child) {
        var childNode = Utils.id('child' + child.child_id);
        if (childNode) {
          childNode.style.opacity = ScheduleFunctions.getOpacityFor(child, $scope.chosenSchedule);
        }
      });
      $scope.$apply();
      $scope.poll();
    }

    $scope.updateChild = function(child) {
      var found = $scope.findChild(child.child_id);
      if (!found) {
        $scope.children.push(child);
        found = child;
        var option=document.createElement("option");
        option.text=child.nickname;
        option.value=child.child_id;
        Utils.id('ec_child_id').add(option, null);
      }
      if (child.schedules) found.schedules = child.schedules;
      if (child.groups) found.groups = child.groups.join();
      if (child.color) found.color = child.color;
      if (child.DOB) found.DOB = child.DOB;
      if (child.nickname) found.nickname = child.nickname;
      if (child.child_first_name) found.child_first_name = child.child_first_name;
      if (child.child_middle_name) found.child_middle_name = child.child_middle_name;
      if (child.child_last_name) found.child_last_name = child.child_last_name;
    }

    $scope.findEvent = function(event_id) {
      for ( var i = 0; i < $scope.events.length; i++) {
        if ($scope.events[i].event_id == event_id) {
          return $scope.events[i];
        }
      }
      return null;
    }

	$scope.findChild = function(child_id) {
		for ( var i = 0; i < $scope.children.length; i++) {
			if ($scope.children[i].child_id == child_id) {
				return $scope.children[i];
			} 
		}
		return null;
	}

	$scope.hideChildren = function() {
	  $scope.children.forEach(function(child) {
	    var childDiv = Utils.id('child' + child.child_id);
	    childDiv.style.opacity = ScheduleFunctions.getOpacityFor(child, $scope.chosenSchedule);
	  });
	  setTimeout($scope.hideChildren, 500);
	}

	$scope.spotted = function(child) {
		$scope.choose(null);
		Utils.sendToServer('overview', 'action=scheduleUpdate&child_id='+ child.child_id + '&schedule_name=' + $scope.chosenSchedule, $scope.addUpdates);
	};

	$scope.choose = function(child) {
		$scope.chosenChild = child;
		var editPanel = Utils.id('editPanel');
		if (!child) {
		  editPanel.className='invisible';
		} else {
		  var childDiv = Utils.id('child' + child.child_id);
		  childDiv.appendChild(editPanel);
		  editPanel.className='visible';
		}
	};

	$scope.poll = function() {
		Utils.sendToServer('overview', 'action=poll', $scope.addUpdates);
	}

	$scope.chooseSchedule = function(scheduleName){
	   $scope.chosenSchedule = scheduleName;
	   $scope.children.forEach(function(child) {
	     var childElement = Utils.id('child' + child.child_id);
	     var newOpacity = ScheduleFunctions.getOpacityFor(child, scheduleName);
	     childElement.style.opacity = newOpacity;
	   });
	}

    $scope.childIds = function() {
      return $scope.idsFromSelect('ec_child_id');
    }

    $scope.clubIds = function() {
      return $scope.idsFromSelect('ec_club_id');
    }

    $scope.idsFromSelect = function(selectId) {
      var select = Utils.id(selectId);
      var ids = [];
      for (var i = 0; i < select.options.length; i++) {
        if (select.options[i].selected){
          ids.push(select.options[i].value);
        }
      }
      return ids.join();
    }

    $scope.addNewEvent = function(newEvent) {
        if (!$scope.findEvent(newEvent.event_id)) {
          $scope.events.unshift(newEvent);
          $scope.$apply();
        }
    }

    $scope.addEvents = function(events) {
      events.forEach(function (event) {
        if (!$scope.findEvent(event.event_id))
          $scope.events.push(event);
      });
      $scope.$apply();
    }

    $scope.createNewEvent = function() {
      var eventName = Utils.id('event_name').value;
      Utils.sendToServer('timeline', 'action=insert&event_name=' + eventName + '&child_id=' + $scope.childIds() + '&club_id=' + $scope.clubIds(), $scope.addNewEvent);
      Utils.id('event_name').value = null;
      $scope.clearSelection('ec_child_id');
      $scope.clearSelection('ec_club_id');
      Utils.id('imagePreview').innerHTML = '';
    }

    $scope.clearSelection = function(selectId) {
      var select = Utils.id(selectId);
      for (var i = 0; i < select.options.length; i++) {
        select.options[i].selected = false;
      }
    }

    $scope.getTimeForEvent = function(event) {
      return new Date(event.event_time);
    }

    $scope.deleteEvent = function(event_id) {
      Utils.sendToServer('timeline', 'action=deleteEvent&event_id=' + event_id, function() {
        var event = $scope.findEvent(event_id);
        $scope.events.splice($scope.events.indexOf(event), 1);
        $scope.$apply();
      });
    }

    $scope.addLoggedInInfo = function(registeredUser) {
      $scope.grownup_id = registeredUser.grownup_id;
      $scope.loggedIn = registeredUser.grownup_first_name + ' ' + registeredUser.grownup_last_name;
      $scope.daycareName = registeredUser.daycare_name;
      $scope.clubs = registeredUser.clubs ? registeredUser.clubs.join():[];
    }

    $scope.eventsForFilteredChildren = function(event) {
        if (!event.children || !$scope.search)return true;
        var searchString = $scope.search.toLowerCase();
        for (var i= 0; i < event.children.length; i++) {
           if (event.children[i].nickname.toLowerCase().indexOf(searchString) >= 0) {
             return event;
           }
        };
        return null;
    }


	Utils.sendToServer('overview', 'action=children', $scope.addUpdates);
	Utils.sendToServer('overview', 'action=clubs', $scope.addClubs);
	Utils.sendToServer('overview', 'action=scheduleNames', $scope.addScheduleNames);
	Utils.sendToServer('timeline', 'action=getEvents', $scope.addEvents);
	Data.getLoggedInInfo($scope.addLoggedInInfo);
	$scope.poll();
	$scope.hideChildren();
};