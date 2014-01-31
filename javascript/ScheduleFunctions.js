/*
DISCLAIMER!
My javascript code is a result of random trial and error,
without any supervision from experts.
I freely admit, that I do not know what I am doing when it comes to javascript.
*/
var MINUTES = 60000;

var ScheduleFunctions = function () {
  var methods = {};

  methods.setOpacityFor = function(child, chosenSchedule) {
    if (!chosenSchedule || chosenSchedule === '') {
      return 1;
    }

    var schedule = methods.findSchedule(child, chosenSchedule);
    if (!schedule) {
      return 0;
    }

    var howLongSinceSpotted = (new Date().getTime() - schedule.last_event) / MINUTES;
    var opacity = Math.min(1, howLongSinceSpotted / schedule.interval);
    return opacity;
  }

  methods.findSchedule = function(child, chosenSchedule) {
    if (!child.schedules) return null;
    for (var i = 0; i < child.schedules.length; i++) {
      if (child.schedules[i].schedule_name == chosenSchedule) {
        return child.schedules[i];
      }
    }
    return null;
  }

  return methods;
}();

