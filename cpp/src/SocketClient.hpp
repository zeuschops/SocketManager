//
//  SocketClient.hpp
//  SocketManager-cpp
//
//  Created by Austin on 11/18/20.
//

#ifndef SocketClient_hpp
#define SocketClient_hpp

#include <stdio.h>
#include <sys/socket.h>
#include <string>

bool connect();
bool disconnect();
std::string getData();
bool isConnected();
bool sendData(std::string data);

#endif /* SocketClient_hpp */
