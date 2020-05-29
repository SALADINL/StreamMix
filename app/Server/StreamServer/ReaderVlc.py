import signal, sys, time, threading, traceback, Ice

import vlc

class ReaderVlc:

	def __init__(self):		
		self.instance = vlc.Instance("")
		
		self.list_media_name = ["Pikatchu.mp3", "waka-waka.mp3"]
		self.media_name = "pika.mp3"
		self.sout = '#transcode{acodec=mp3,ab=128,channels=2,samplerate=44100}:http{dst=:3200/'+ str(self.media_name)+'}';


	def addBroadcast(self, music):
		self.media_name = music
		self.sout = '#transcode{acodec=mp3,ab=128,channels=2,samplerate=44100}:http{dst=:3200/'+ str(self.media_name)+'}';
		try:
			self.instance.vlm_add_broadcast("0", self.media_name, self.sout, 0, None, True,True)
		except NameError:
			print ('NameError: % (%s vs Libvlc %s)' % (sys.exc_info()[1],vlc.__version__, vlc.libvlc_get_version()))
			sys.exit(1)

	def playVlc(self):
		self.instance.vlm_play_media("0")

	def stopVlc(self):
		self.instance.vlm_stop_media("0")
		self.instance.vlm_release()

	def pauseVlc(self):
		self.instance.vlm_pause_media("0")