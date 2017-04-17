# SnakeGame

The Transmission of UDP messages for the Multiplayer Snake Game
---------------------------------------------------------------

Introduction 
------------

This RFC describes the details of communications for the Multiplayer Snake Game.

Implementation of Game Structure
--------------------------------

Game should be coded with two levels: a server level and a client level. Server should receive all requests to change snake’s directions, compute their positions then broadcast them. Clients should receive those positions and display them with a graphic interface. If players want to change their snake’s direction, they send it to the server.
<br/>Communications must be implemented with UDP.
<br/>+-------------+ broad Port(=5656) +--------------+ 
<br/>|___Game__|---------------------------->|_Client_| 
<br/>|_Handler_|<--------------------------- |________| 
<br/>+-----------------+ inputPort +------------------+
<br/> ___|
<br/> ___| starts the game 
<br/> ___V
<br/>+--------------+ clientPort +--------------+ 
<br/>|__Game___|---------------------> |_Client_|
<br/>|_Manager_|<--------------------- |________|
<br/>+---------------+ gamePort +---------------+
<br/>Clients must listen on port 5656 at initialization. Other ports are communicated by messages.

Universal Format of messages 
----------------------------
Messages have data types so as to be extensible. Types are differently interpreted if read by the client or the server. The common structure is the following:

<br/>0        7 
<br/>+--------+--------+ 
<br/>|  Data  |  Data  | 
<br/>|  Type  | octets | 
<br/>+--------+--------+

Messages sent from Client 
-------------------------

Here is the list of messages sent from client. Only data content is described.

<br/><br/>-) Type 0 is used when client connects to the server and must communicate its listening port to inputPort.
<br/>0        7 8         23 
<br/>+---------+-----------+ 
<br/>|   Type  | Listening | 
<br/>|    0    |    Port   | 
<br/>+---------+-----------+

<br/><br/>-) Type 2 is used when client wants to change his snake’s direction. It is sent to gamePort. Direction are coded by a byte as followed:
<br/>Left ---> 0 
<br/>Up ---> 1 
<br/>Right ---> 2 
<br/>Down ---> 3

<br/><br/>0     7 8   15 16    23 24        31 
<br/>+------+------+--------+-----------+ 
<br/>| Type | Task | Client |    New    | 
<br/>|   2  |  ID  |   ID   | Direction | 
<br/>+------+------+--------+-----------+ 

Messages sent from Server 
-------------------------

Here is the list of messages sent from Server. Only data content is described.

<br/>-) The message broadcasted on port 5656 contains the server name coded in ASCII and the game port.
<br/>0      7 8   7+ns 8+ns 31+ns 
<br/>+-------+--------+------+ 
<br/>|  name | Server | game | 
<br/>|  Size |  Byte  | Port | 
<br/>+-------+--------+------+

>>-) Type 0 is used when client connects to the server. The server answers by sending him a gamePort via clientPort to listen game communications, and a client ID to identify him.
>0     7 8   23 24     32 
>+------+------+--------+ 
>| Type | Game | Client | 
>|   0  | Port |   ID   | 
>+------+------+--------+ 

>>-) Type 1 is used when all players are ready. It broadcasts the time remaining before start.
>0     7 8         15 
>+------+-----------+ 
>| Type |    Time   | 
>|   1  | Remaining | 
>+------+-----------+ 

>>-) Type 2 is used to broadcast all snakes’ positions to display the game. We communicate the number of snakes then the structure of each one. Snakes are coded so as to decode them starting from the queue. They are represented by giving the queue position (one byte x, one byte y), then the number of times it changes direction, and for each direction the number of points aligned. An extra point, the apple, is given to increment snake’s size if it reaches it.

>>0     7 8       15 16    i   i+7 i+8  i+15     j    j+15 
>+------+----------+-----+-------+-------+-----+--------+ 
>| Type |  Number  | ... | Snake | Snake | ... |  Apple | 
>|   2  | OfSnakes | ... |  Data |  Data | ... |Position| 
>+------+----------+-----+-------+-------+-----+--------+ 

>>One snake is transmitted as followed:
>0      7 8       23 24         31       i       i+7 i+8  i+15 
>+--------+----------+------------+-----+-----------+--------+-----+ 
>| Client |   Queue  |    Number  | ... | Direction | Length | ... | 
>|   ID   | Position | Directions | ... |           |        | ... | 
>+--------+----------+------------+-----+-----------+--------+-----+ 
