Swarm
=====

This is a _fork_ of Swarm. Don't expect any of this to work. You may find the Swarm source code at http://github.com/sanity/Swarm. For the project's homepage
please visit GoogleCode_.

.. _GoogleCode: http://code.google.com/p/swarm-dpl


About this fork:

Jini and Javaspaces: I'm working on using Jini and JavaSpaces infrastructure to move the Swarm continuations around. 
An early smoke-test worked, and I'm in the middle of setting up clean up the start/stop scripts for the 
Jini/JavaSpaces servers you need to have running.

The scripts are written for, and tested on, a UNIX-based system (tested on Ubuntu, actually).
bin/jini-start.sh

will start the three main Jini servers we need: Reggie (lookup service, e.g. a registry of services), Outrigger
(the JavaSpaces implementation) and Mahalo (distributed transaction manager).

bin/jini-kill.sh

will stop the servers, using PIDs recorded by the start scripts.

Per-server logs, and the PIDs, are written to /tmp.

The Swarm code hasn't been updated to use either JavaSpaces or Jini. Now I have the servers running, I'll work on 
moving my demo code in.

Note on security: it's possible to tighten the screws on Jini servers and clients, but I haven't done so yet. 
