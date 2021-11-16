
# First go to start Receiver program server by type:
	- get in to receiver folder: "cd receiver"
	- then compile all java file to bytecode file: "javac ./edu/metrostate/*.java"
	- finally type an example command below to run the Receiver program: 

		"java edu.metrostate.Receiver -d 0.34 localhost 58974"

		-d : percentage of datagrams to corrupt, delay, or drop, like 0.34(34%).
		localhost : receiver ip address, like "127.0.0.0" or "localhost"
		58974 : receiver port number.

# Second, go to start Sender program client by type:
	- get in to sender folder: "cd sender"
	- then compile all java file to bytecode file: "javac ./edu/metrostate/*.java"
	- finally type an example command below to run the Sender program: 

		"java edu.metrostate.Sender -s 500 -t 1500 -d 0.23 -f cat.jpg localhost 58974"

		-s : size of packet, like 500
		-t : timeout interval, like 2000 (2 seconds)
		-d : percentage of datagrams to corrupt, delay, or drop, like 0.25(25%).
		-f : file name which to send (the file must locate inside the sender folder)
		localhost : receiver ip address, like "127.0.0.0" or "localhost"
		58974 : receiver port number.