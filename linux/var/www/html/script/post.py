#!/usr/bin/env python

import subprocess
import web
import time

urls = (
	'/', 'respond'
)

class respond:
	def GET(self):
		return "Hello, world."

	def POST(self):
		timestr = time.strftime("%Y%m%d-%H%M%S")
		xml = web.data()
		with open("/var/www/html/script/"+ timestr + ".xml", "w") as f:
			f.write(xml)
		with open("/var/www/html/gps.xml", "a") as f:
			f.write(xml)
		return


application = web.application(urls, globals()).wsgifunc()
