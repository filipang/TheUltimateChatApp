import socket
import sys
import json
import traceback
from threading import Thread

connections = []

def main():
    start_server()


def start_server():
    host = "0.0.0.0"
    port = int(sys.argv[1])         # arbitrary non-privileged port

    soc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    soc.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)   # SO_REUSEADDR flag tells the kernel to reuse a local socket in TIME_WAIT state, without waiting for its natural timeout to expire
    print("Socket created")

    try:
        soc.bind((host, port))
    except:
        print("Bind failed. Error : " + str(sys.exc_info()))
        sys.exit()

    soc.listen(5)       # queue up to 5 requests
    print("Socket now listening")
    # infinite loop- do not reset for every requests
    while True:
        connection, address = soc.accept()
        connections.append((address[0], connection))
        ip, port = str(address[0]), str(address[1])
        print("Connected with " + ip + ":" + port)

        try:
            Thread(target=client_thread, args=(connection, ip, port)).start()
        except:
            print("Thread did not start.")
            traceback.print_exc()

    soc.close()


def client_thread(connection, ip, port, max_buffer_size = 5120):
    is_active = True
    while is_active:
        client_input = receive_input(connection, max_buffer_size)

        if "--QUIT--" in client_input:
            print("Client is requesting to quit")
            connection.close()
            print("Connection " + ip + ":" + port + " closed")
            is_active = False
        else:
            print("SEND TO EVERYONE ELSE")
            for con in connections:
                add, conx = con;
                print("Sending message to " + add + " client if it is not " + ip + "...")
                if(conx != connection):
                    conx.sendall(client_input)

def receive_input(connection, max_buffer_size):
    client_input = connection.recv(max_buffer_size)
    client_input_size = sys.getsizeof(client_input)
    print(client_input)
    if client_input_size > max_buffer_size:
        print("The input size is greater than expected {}".format(client_input_size))

    decoded_input = json.loads(client_input)  # decode and strip end of line
    result = process_input(decoded_input)
    return result[0] + ": " + result[1] + "\n"


def process_input(input_json):
    print("Processing the input received from client")
    return [input_json['user'] , input_json['message']]

if __name__ == "__main__":
	main()
