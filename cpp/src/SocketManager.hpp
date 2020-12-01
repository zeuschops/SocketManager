//
//  SocketManager.hpp
//  SocketManager-cpp
//
//  Created by Austin on 11/17/20.
//

#ifndef SocketManager_hpp
#define SocketManager_hpp

#include <stdio.h>
#include <string>

std::string getData();
void run();
void sendData(std::string data);
void start();
bool stop();

#endif /* SocketManager_hpp */
