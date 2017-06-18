import l293d

motors = []
motors.append(l293d.DC(22, 16, 18))  # Front-left
motors.append(l293d.DC(15, 11, 15))  # Front-right
motors.append(l293d.DC(36, 31, 32))  # Back-left
motors.append(l293d.DC(33, 29, 37))  # Back-right

motors[0].reversed = True
motors[2].reversed = True


def forwards():
	for m in motors:
	    m.clockwise()


def stop():
	for m in motors:
	    m.stop()


def cleanup():
	stop()
	l293d.cleanup()


def drive_in_direction(direction):
	return None
