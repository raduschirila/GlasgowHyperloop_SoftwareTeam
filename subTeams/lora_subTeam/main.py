import socket
import time
import random

target_host = "localhost"
target_port = 5000

cli_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
cli_sock.connect((target_host, target_port))

i = 1

times = 0
speed = 0
acceleration = 10

while True:
    message = int.to_bytes(times, 4, "big") + int.to_bytes(speed, 4, "big") + int.to_bytes(acceleration, 4, "big", signed=True)

    print(b"Sending: " + message)
    cli_sock.send(message)

    times += random.randrange(10, 100)
    speed += acceleration
    if times > 200 and acceleration > -10:
        acceleration -= 1
    if speed <= 0:
        break

    time.sleep(2)
