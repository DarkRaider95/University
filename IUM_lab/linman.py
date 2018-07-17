import os
import string
import mimetypes

def execop(op, path, csock):

	if op == "GET":
		if os.path.isdir(path):
			try:
				filelist = search(path)
			except:
				response=htmlwriter("Access Denied click <span onclick='window.history.back()'>here</span> to go back")
			else:	
				response = htmlwriter(filelist)
		
		elif os.path.isfile(path):
			response=download(path)
			if response==None:
				response=htmlwriter("Can't download file Permission Denied click <span onclick='window.history.back()'>here</span> to go back")
		
		elif os.path.isfile(path[1:]):
			response=download(path[1:])
			if response==None:
				response=htmlwriter("Can't download file Permission Denied click <span onclick='window.history.back()'>here</span> to go back")
		
		else:
			response = htmlwriter("The path searched is not a directory or a file click <a href='/'>here</a> to return to root directory")
	else:
		response = htmlwriter("Http command not implemented or error")

	sendresp(response, csock)

def search(path):

	print path
	filelist=[]
	pathfilelist=[]

	if path!='/':
		filelist.append(path+'..')
		pathfilelist.append([path+'..','d'])

	print pathfilelist
	filelist.extend(os.listdir(path))
	n=1

	while n < len(filelist):
		filelist[n] = path+filelist[n]
		print filelist[n]
		if(os.path.isdir(filelist[n])):
			pathfilelist.append([filelist[n],'d'])
		else:
			pathfilelist.append([filelist[n],'f'])
		n+=1

	return pathfilelist

def htmlwriter(Object):

	head = """HTTP/1.1 200 OK\r\nContent-type:text/html;\r\n\r\n<!DOCTYPE html>
			  <html>
			  	<head>
			  		<link href="/resources/fileman.css" type="text/css" rel="stylesheet" />
			  		<link href="/resources/favicon.png"  rel="shortcut icon"/>
			  		<title>File Manager</title>
				</head>
			  	<body>
			  		<h1>FILE MANAGER</h1>
			  		<div id="container">"""
	html=''

	if(type(Object) == list):
		print ("\n\n\n\n\n\nFILELIST::::::")
		print (Object)

		for item in Object:
			print (item)
			if item[1] == 'd':
				html+="<div class='directory'><a href='"+item[0]+"/'><img src='/resources/directory.png' alt='img-directory'><br>"+os.path.basename(item[0])+"</a></div>\n"
			else:
				html+="<div class='directory'><a href='"+item[0]+"'><img src='/resources/file.png' alt='img-file'><br>"+os.path.basename(item[0])+"</a></div>\n"
	else:
		html = "<p>"+Object+"</p>"

	tail="""		</div>
				</body>
			</html>"""
	return head+html+tail

def download(path):

	stat=os.stat(path)
	print (mimetypes.guess_type(path)[0])
	mime=mimetypes.guess_type(path)[0]
	print (mime)
	response=[]

	if mime == None:
		response.append('HTTP/1.1 200 OK\r\nContent-type:None;\r\nContent-Length:'+str(stat[6])+'\r\n\r\n')
	else:
		response.append('HTTP/1.1 200 OK\r\nContent-type:'+mime+';\r\nContent-Length:'+str(stat[6])+'\r\nfilename:'+os.path.basename(path)+'\r\n\r\n')

	try:
		infile = open(path,'rb')
	except:
		response=None
	else:
		response.append(infile)

	return response

def sendresp(response,csock):
	
	if(type(response)==str):
		try:
			csock.sendall(response)
		except:
			print (traceback.print_exc())
			print ("impossible to send")

	elif(type(response)==list):
		csock.sendall(response[0])
		data = response[1].read(8192)

		while data:
			try:
				csock.sendall(data)
				data = response[1].read(8192)
			except:
				print (traceback.print_exc())
				print ("impossible to send")
				break
		response[1].close()