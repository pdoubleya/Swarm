Swarm
=====

This is a _fork_ of Swarm. Don't expect any of this to work. You may find the Swarm source code at
http://github.com/sanity/Swarm. For the project's homepage please visit GoogleCode_.

.. _GoogleCode: http://code.google.com/p/swarm-dpl


About this fork:

Jini and JavaSpaces: I'm working on using Jini and JavaSpaces infrastructure to move the Swarm
continuations around. These experiments are against Jini 2.1. Note that JavaSpaces is delivered
as a part of the Jini project, although conceptually JS just relies on Jini for things like lookup.

Quick Overview
--------------
My current approach is to move continuations between servers asynchronously. When a server wants
to move a continuation somewhere, it identifies the target server using a logical identifier
(currently a string). The Swarm object looks first for a JavaSpace instance on the network,
then looks for the target server's unique identifier (UUID) in the space. It then posts the
serialized continuation in the space using the target server's name and UUID as a sort of key.
On the receiving end, the target server is polling for new entries in the space marked with
its identifier. It then downloads any it finds and executes them.

In this design, the sender could operate even if the receiver was offline; the receiver, on
starting up, would find waiting entries, pull them down, and execute them.

A JavaSpace in this case is a server (there is a reference implementation as part of the Jini SDK,
called Outrigger) which is a sort of electronic bulletin board that is network accessible. In the
demo, the space is transient and does not persist entries, though that can be configured as well.
There are space implementations that can replicate as well, and you can have more than one space
running at a time, to handle failover, etc.

Servers that want to find and retrieve entries from a space can work in several different modes

- poll with no timeout
- poll with timeout
- blocking poll
- asynchronous notification

Currently, I'm using a poll with timeout, which gives me some debugging information (e.g. something is happening).


Starting the Jini/JavaSpaces Services
-------------------------------------

The start and stop scripts are written for, and tested on, a UNIX-based system (tested on Ubuntu, actually).
To launch the services, use::

  bin/jini-start.sh

will start the three main Jini servers we need: Reggie (lookup service, e.g. a registry of services), Outrigger
(the JavaSpaces implementation) and Mahalo (distributed transaction manager). To stop the services::

  bin/jini-stop.sh

will stop the servers, using PIDs recorded by the start scripts.

Per-server logs, and the PIDs, are written to ``/tmp``.

Note on security: it's possible to tighten the screws on Jini servers and clients, but I haven't done so yet. 


Running the Demos
-----------------
Once the servers are started, you can launch the demos as usual. I'm using Maven for this; the
``run.sh`` script hasn't been updated to include the correct classpath.

First, compile cleanly::

  mvn -o clean compile

Launch a generic listener::

  mvn -o -q exec:java -Dexec.mainClass="swarm.demos.Listen"

where **-o** means Maven won't look for JAR updates online, and **-q** means Maven should just shut up. Note that in the original demos, the listener and the sender demos required a port parameter; this is no longer used.

Launch a sender, one of::

  mvn -o -q exec:java -Dexec.mainClass="swarm.demos.ForceRemoteRef" -Dexec.args="start"

or::

  mvn -o -q exec:java -Dexec.mainClass="swarm.demos.ExplicitMoveTo1" -Dexec.args="start"


NOTE: I haven't yet added any graceful cleanup when the demos are done, nor have I coded the Swarm processes to react gracefully when the Jini services are shut down. You should stop/start the servers between each demo run until I have fixed that.


Future
------
There is a lot to look into.

- **synchronous sends**: the original Swarm demos worked on the basis of a synchronous calls between servers. In the Jini world, that would be a Jini interface. It's easy to set up given the infrastructure, but it raises a problem when the sender needs to send and the receiver isn't available. It also forces the receiver to receive synchronously but process asynchronously. Anyway, I want to play around with the spaces a bit, will get back to the synchronous case shortly.

- **pick your target**: a sender needs to know where to go next, e.g. what the target server is. In the demos, we don't have any infrastructure to map data to a server, so the demos have the target addresses hard-coded. Coming up with a logical scheme to identify servers would be a next step.

- **specifying intention**: what I want to play with is that a sender has an intention, e.g. wants to work on some data, and should act on the intention. In a classical spaces design, they would post an entry saying "I need to work on data X" and a server who had data X available would pull the entry down and execute it.

