import signal, sys, time
import Ice
from ReaderVlc import ReaderVlc 

Ice.loadSlice('Stream.ice')
import App


class StreamI(App.Stream):

	media = ReaderVlc()

	def addMusic(self, music, current):
		self.media.addBroadcast(music)
		return "addMusic"

	def removeMusic(self, current):
		self.media.stopVlc()
		return "removeMusic"

	def playMusic(self, current):
		self.media.playVlc()
		return "playMusic"

	def pauseMusic(self, current):
		self.media.pauseVlc()
		return "pauseMusic"


with Ice.initialize(sys.argv, "config.server") as communicator:

	signal.signal(signal.SIGINT, lambda signum, frame: communicator.shutdown())

	if len(sys.argv) > 1:
		print(sys.argv[0] + ": too many arguments")
		sys.exit(1)

	adapter = communicator.createObjectAdapterWithEndpoints("AppAdapter", "tcp -h 192.168.43.194 -p 12000") 
	adapter.add(StreamI(), Ice.stringToIdentity("ServerStream"))
	print("ServerStream:127.0.0.1:12000")
	adapter.activate()
	communicator.waitForShutdown()