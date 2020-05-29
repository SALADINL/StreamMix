#!/usr/bin/env python
#
# Copyright (c) ZeroC, Inc. All rights reserved.
#

import signal
import sys
import time
import Ice

Ice.loadSlice('Flux.ice')
import Stream

class FluxI(Stream.Flux):
    def play(self, current):
        print("Play !")

    def pause(self, current):
        print("Pause !")

    def soundUp(self, val, current):
        print("SoundUp by \d",val)
    
    def soundDown(self, val, current):
        print("SoundDown by \d",val)

    def shutdown(self, current):
        current.adapter.getCommunicator().shutdown()


with Ice.initialize(sys.argv) as communicator:

    signal.signal(signal.SIGINT, lambda signum, frame: communicator.shutdown())

    adapter = communicator.createObjectAdapterWithEndpoints("StreamAdapter", "tcp -h 127.0.0.1 -p 10000")
    adapter.add(FluxI(), Ice.stringToIdentity("ServerMedia"))
    adapter.activate()
    communicator.waitForShutdown()
