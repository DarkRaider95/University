import os
import socket
import string
import platform
import traceback

if (platform.system()=="Windows"):
	import winman as man
else:
	import linman as man


def main():
	ssock=setsocket()
	
	while 1:
		serverforever(ssock)
	
def setsocket():
	ssock=socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	ssock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
	ssock.bind(('0.0.0.0', 7777))
	ssock.listen(1)

	return ssock

def serverforever(ssock):
	try:
		(csock, address) = ssock.accept()
	except KeyboardInterrupt:
		print ("\nServer closed")
		ssock.close()
		exit(0)
	req=csock.recv(8192)
	if(req!=""):
		parsereq(req, csock)
	else:
		csock.shutdown(socket.SHUT_RDWR)
	csock.close()
	

def parsereq(req, csock):
	print ("REQUEST:\n"+req)
	
	rows=req.split('\r\n')

	print ("ROWS:")
	print (rows)
	words=rows[0].split()

	print ("WORDS:")
	print (words)

	man.execop(words[0],words[1],csock)	

if __name__ == '__main__':
    main()  