#!/usr/bin/env python

import sys, gtk, dbus

class PidginMessage:

    def setMessage(self, message):
        # Initiate a connection to the Session Bus
        bus = dbus.SessionBus()

        # Associate Pidgin's D-Bus interface with Python objects
        obj = bus.get_object("im.pidgin.purple.PurpleService", "/im/pidgin/purple/PurpleObject")
        purple = dbus.Interface(obj, "im.pidgin.purple.PurpleInterface")
    
        # Get current status type (Available/Away/etc.)
        current = purple.PurpleSavedstatusGetType(purple.PurpleSavedstatusGetCurrent())
        # Create new transient status and activate it
        status = purple.PurpleSavedstatusNew("", current)
        purple.PurpleSavedstatusSetMessage(status, message)
        purple.PurpleSavedstatusActivate(status)            
        

if len(sys.argv) > 1:
	pidginMessage = PidginMessage()
	pidginMessage.setMessage(sys.argv[1])


                           
  
