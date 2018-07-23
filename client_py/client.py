import socket
import sys
import json

def main():
    soc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    host = sys.argv[2]
    port = int (sys.argv[1])
    user = sys.argv[3]
    try:
        soc.connect((host, port))
    except:
        print("Connection error")
        sys.exit()
    
    while True :
        print("Enter --QUIT-- to exit")
        message = raw_input(" -> ")
        
        print("'{}'".format(message))
        my_dict={}
        my_dict["message"] = message
        my_dict["user"] = user
        print json.dumps(my_dict, indent=4)
        soc.sendall(json.dumps(my_dict))
        if message == "--QUIT--" :
            break
    print("Session closed")

    """
    while message != 'quit':
        soc.sendall(message.encode("utf8"))
        if soc.recv(5120).decode("utf8") == "-":
            pass        # null operation

        message = input(" -> ")

    soc.send(b'--quit--')
"""
if __name__ == "__main__":
	main()
