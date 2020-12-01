//
//  SocketServer.hpp
//  SocketManager-cpp
//
//  Created by Austin on 11/18/20.
//

#ifndef SocketServer_hpp
#define SocketServer_hpp

#include <stdio.h>
#include <sys/socket.h>
#include <string>

std::string getData();
void run();
void sendData(std::string data);
void start();
bool stop();

#endif /* SocketServer_hpp */
