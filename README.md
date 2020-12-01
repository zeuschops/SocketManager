# SocketManager
A networking library to quickly test and utilize networking resources.

This project was rapidly assembled mainly to focus on dealing with TCP and UDP data quickly and efficient. This is focused on designing a network utility for JDK 10+ since the Library is very close (if not the same) as JDK 11 and JDK 12 has plans to be (last I checked). There is a chance this utility will work for JDK 6+, but it's going to be unofficially supported at this current point in time as the focus changes to JDK 10 or newer or library maintenance purposes.

## What was the purpose of the project?
I like networks, I can't say you don't either since you're using an internet browser or else git clone 'd the repository which transports data over a network. The simplicity of the library can be extremely helpful in the long-run, especially if the code is maintained between iterations. Primarily, the time advantage to just using a "wrapper"-like library is exponentially critical to beginning other projects and stabilizing projects over time.

## Why not just put this in another project?
I would have forced this project into another, but I wanted an easy way to strip out different pieces before working on critical applications. This also opens up the opportunity to mess with Java's Module system in the future, which could prove to be useful.

## Somehow this library still works
I have been away from Java for a little while and things are getting a little fuzzy at best. While away, I forgot how to write Java code and had some issues with JDK 11 and JDK 12. With the release of JDK 13 this code is recently untested and I am headed back to work with JDK 8u231 for University. This project will currently remain ongoing but may end up with a branch for pre-JDK 9 in the (somewhat) near future.

## Explanation
This project mainly serves as generic reference to TCP/UDP/Encryption access for Java 9+. This has worked since JDK 9, but is not regularly updated so some items may need to update if you choose to utilize it.
