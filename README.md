# SnakeGame

The Transmission of UDP messages for the Multiplayer Snake Game
---------------------------------------------------------------

Introduction 
------------

This RFC describes the details of communications for the Multiplayer Snake Game.

Implementation of Game Structure
--------------------------------

Game should be coded with two levels: a server level and a client level. Server should receive all requests to change snake’s directions, compute their positions then broadcast them. Clients should receive those positions and display them with a graphic interface. If players want to change their snake’s direction, they send it to the server.
Communications must be implemented with UDP.
# 
+-------------+ broad Port(=5656) +--------------+
# 
|   Game  |---------------------------->| Client | 
# 
| Handler |<--------------------------- |        | 
+-----------------+ inputPort +------------------+
    |
    | starts the game 
    V
+--------------+ clientPort +--------------+ 
|  Game   |---------------------> | Client |
| Manager |<--------------------- |        |
+---------------+ gamePort +---------------+
Clients must listen on port 5656 at initialization. Other ports are communicated by messages.

# Universal Format of messages ---------------------------
Messages have data types so as to be extensible. Types are differently interpreted if read by the client or the server. The common structure is the following:

0        7 
+--------+--------+ 
|  Data  |  Data  | 
|  Type  | octets | 
+--------+--------+

# Messages sent from Client -----------------------------------

Here is the list of messages sent from client. Only data content is described.

-) The message broadcasted on port 5656 contains the server name coded in ASCII and the game port.
0      7 8   7+ns 8+ns 31+ns 
+-------+--------+------+ 
|  name | Server | game | 
|  Size |  Byte  | Port | 
+-------+--------+------+

-) Type 0 is used when client connects to the server and must communicate its listening port to inputPort.
0        7 8         23 
+---------+-----------+ 
|   Type  | Listening | 
|    0    |    Port   | 
+---------+-----------+

-) Type 2 is used when client wants to change his snake’s direction. It is sent to gamePort. Direction are coded by a byte as followed:
Left ---> 0 
Up ---> 1 
Right ---> 2 
Down ---> 3
0 7 8 15 16 23 24 31 +-------------+------------------+------------------+--------------------+ | Type | Task | Client | New | | 2 | ID | ID | Direction | +-------------+------------------+------------------+--------------------+
Messages sent from Server -----------------------------------
Here is the list of messages sent from Server. Only data content is described.
-) The message broadcasted on port 5656 contains the server name coded in ASCII and the game port.
0 7 8 7+ns 8+ns 31+ns +-------------+------------------+------------------+ | name | Server | game | | Size | Byte | Port | +-------------+------------------+------------------+
-) Type 0 is used when client connects to the server. The server answers by sending him a gamePort via clientPort to listen game communications, and a client ID to identify him.
0 78 2324 32 +-------------+------------------+---------------+ | Type | Game | Client | | 0 | Port | ID | +-------------+-------------------+---------------+
-) Type 1 is used when all players are ready. It broadcasts the time remaining before start.
078 15 +-------------+-------------------------+ | Type | Time | | 1 | Remaining | +-------------+-------------------------+
-) Type 2 is used to broadcast all snakes’ positions to display the game. We communicate the number of snakes then the structure of each one. Snakes are coded so as to decode them starting from the queue. They are represented by giving the queue position (one byte x, one byte y), then the number of times it changes direction, and for each direction the number of points aligned. An extra point, the apple, is given to increment snake’s size if it reaches it.
0 78 1516 i i+7 i+8 i+15 j j+15 +-------------+------------------------+----------+----------------+----------------+--------+------------+ | Type | Number | ... | Snake | Snake | ... | Apple | | 2 | OfSnakes | ... | Data | Data |... |Position| +-------------+-------------------------+----------+----------------+----------------+--------+------------+
One snake is transmitted as followed:
0 78 2324 31 i i+7 i+8 i+15 +-------------+------------------------+-------------------+----------+-------------------+----------------+------+ | Client | Queue | Number | ... | Direction | Length | ...| | ID | Position | Directions | ... | | | ... | +-------------+------------------------+-------------------+----------+-------------------+----------------+------+
