# Project2 Stop and Wait

    This project provides an all-inclusive implementation experience of major topics in Computer Networks, including protocol design and implementation.
    The User Datagram Protocol (UDP) provides point-to-point, unreliable datagram service between a pair of hosts. It does not provide any reliability or sequencing guarantees – i.e. packets may arrive late, may not arrive at all, or arrive corrupted. 
    Your project is to implement a reliable and sequenced message transport protocol on top of this unreliable UDP (stop and wait). Your protocol will ensure reliable, end-to-end delivery of messages in the face of packet loss, and will preserve message ordering in the face of arbitrary latencies due to multiple paths taken by packets.
    In order to simulate a lossy network on a single computer, you will implement a proxy that randomly drops, delays, or corrupts packets.
    Whereas TCP allows fully bidirectional communication, your implementation will be asymmetric. Each endpoint will play the role of a "sender" and a "receiver". Data packets will only flow from the sender to the receiver, while ACKs will only flow in the "reverse" direction from the receiver back to the sender. 

    implement the sender and receiver components of a transport layer. The sender reads a stream of data (from a file), breaks it into fixed-sized packets suitable for UDP transport, prepends a control header to the data, and sends each packet to the receiver. The receiver reads these packets and writes the corresponding data, in order, to a reliable stream (a file). 
    A high-level overview of the system:
    Input File  Sender  data datagrams travel over UDP  Receiver  Output File 
	                     ← ACK datagrams travel over UDP  
    The Receiver should exactly reproduce the Sender's input file's content in its output file, regardless of a lossy, congested, or corrupting network layer. 
- will ensure reliable transport by having the Receiver acknowledge packets received from the Sender; the Sender will detect missing acknowledgements and resend the dropped or corrupted datagrams after a timeout period. The default timeout period is 2000ms, but you may change this with the -t command-line option. 
- The Receiver will only use positive ACKs for any datagrams that it receives. 
- Acknowledgement datagrams do not need to be acknowledged by the Sender.
- Your Receiver should ensure that data are written in the correct order. 
- For stop-and-wait, set the window size at both the sender and receiver to 1. 
- File data will be binary. 
- Because we are doing this between two processes on a single machine, latency is near 0, and no network faults can occur. As a result, you should introduce errors to test the correctness of your implementation. The Sender and Receiver programs must both accept options to force packets to be lost or corrupted at both processes. 

### User Interface:
    
        java edu.metrostate.Sender -s 100 -t 30000 -d 0.25 receiver_ip_addr receiver_port
        
        java edu.metrostate.Receiver -d 0.5 receiver_ip_addr receiver_port