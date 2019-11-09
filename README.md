# SocketManager
A networking library to quickly test and utilize networking resources.

This project was rapidly assembled mainly to focus on dealing with TCP and UDP data quickly and efficient. This is focused on designing a network utility for JDK 10+ since the Library is very close (if not the same) as JDK 11 and JDK 12 has plans to be (last I checked). There is a chance this utility will work for JDK 6+, but it's going to be unofficially supported at this current point in time as the focus changes to JDK 10 or newer or library maintenance purposes.

## What was the purpose of the project?
I like networks, I can't say you don't either since you're using an internet browser or else git clone 'd the repository which transports data over a network. The simplicity of the library can be extremely helpful in the long-run, especially if the code is maintained between iterations. Primarily, the time advantage to just using a "wrapper"-like library is exponentially critical to beginning other projects and stabilizing projects over time.

## Why not just put this in another project?
I would have forced this project into another, but I wanted an easy way to strip out different pieces before working on critical applications. This also opens up the opportunity to mess with Java's Module system in the future, which could prove to be useful.

## Issues with JDK 11
Although JDK 9 was the beginning of the long haul for a large number of development teams, apparently no IDE development team was prepared for JDK 11's rewrite rendering a handful of IDEs and build systems essentially useless until they update their base repositories to better conform to the new standards. As a result of this, I have spent the last few commits to the branch removing Travis-CI, Eclipse Integrations, and Gradle Integrations. Unfortunately, I can't attest that this will work on everyone else's systems until Gradle and Eclipse are both updated to perform where needed until sometime likely after JDK 12's release in March 2019. I do have some plans to continue this project whenever possible but there are few things I wish to add to the repo and not having easily available tests will make things more difficult until other developers get accustomed to JDK's future.

## Significant issues with JDK 12 -- EOL Statement
Moving on to JDK 12, there seems to be significant issues with TCP/IP Sockets during implementation. While the code within the project is sound and stable, code written around the library appears to become fairly unstable while doing some outside tests. It's likely coming to an EOL for the project as a Java-based project. I also just haven't been active of GitHub as of late due to other interests, but may become active again once college starts back up.
