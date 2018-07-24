import socket
import sys
import json
import traceback
from threading import Thread

CONNECTED = False
def client_send_thread(soc, user):
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
            CONNECTED = False
            soc.close()
            break
    print("Session closed")


def client_receive_thread(connection, max_buffer_size):
    while CONNECTED :
        client_input = connection.recv(max_buffer_size)
        client_input_size = sys.getsizeof(client_input)
        print(client_input)
        if client_input_size > max_buffer_size:
            print("The input size is greater than expected {}".format(client_input_size))

        decoded_input = json.loads(client_input)  # decode and strip end of line
        result = [input_json['user'] , input_json['message']]
        proc_client_inp = result[0] + ": " + result[1] + "\n"
        print(proc_client_inp)




def main():
    soc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    host = sys.argv[2]
    port = int (sys.argv[1])
    user = sys.argv[3]
    max_buffer_size = 5120

    try:
        soc.connect((host, port))
    except:
        print("Connection error")
        sys.exit()
    try:
        CONNECTED = True
        Thread(target=client_send_thread, args=(soc, user)).start()
    except:
        print("client_send_thread did not start")
        traceback.print_exc()
    try:
        Thread(target=client_receive_thread, args=(soc, max_buffer_size)).start()
    except:
        print("client_receive_thread did not start")
        traceback.print_exc()

if __name__ == "__main__":
	main()
