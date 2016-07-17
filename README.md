Connection wrapper: Wraps TCP connection into different tunnels
=====================================

Connection wrapper creates a bidirectional virtual data connection tunnelled in plain TCP connection and go through some proxies or SSL servers.
This can be useful for users behind restrictive firewalls or when you need to connect from old client(telnet/netcat) which doesn't support Socks/SSL protocols.
If your WWW access is allowed through a HTTPS proxy(method CONNECT), it's possible to use connection wrapper and telnet to connect to a computer outside the firewall/proxy. 

Requires JDK 1.8 or higher.
