#!/usr/bin/env python

import sys, gtk, dbus

class KopeteMessage:

    def setMessage(self, message):
        # Initiate a connection to the Session Bus
        bus = dbus.SessionBus()

        # Associate Kopete's D-Bus interface with Python objects
        obj = bus.get_object("org.kde.kopete", "/Kopete")
        kopete = dbus.Interface(obj, "org.kde.Kopete")
    
        kopete.setStatusMessage(message)
    

if len(sys.argv) > 1:
	kopeteMessage = KopeteMessage()
	kopeteMessage.setMessage(sys.argv[1])


                           
  
