TODO
====

Status Sunday Oct 18
need to listen for incoming events
- notify is possible, seems like a hassle
- need otherwise to start three loops, one for each case - task op, error, result
  - start result and error listener only when op is sent
  - when result or error is received, remove all others for the task
  - on timeout, remove all others for the task

- **node identifier** Need concept for identifying a "node" which is some server that can host continuations. The
identifier lets use identify where we need to move, e.g. where some data is currently located so we can
move the continuation there, or which the "home" node is, that is, the node where the continuation originated

- **home node**: The "home" node for a continuation is the node where it was first launched, and where it should
"return" when it has finished its operation, or when it has errored out or timed out

- **task, result, error**: my current idea is as follows
  - a *task* is some process we want to run which may access data on more than one node
  - a task is represented as a *delimited continuation*, which means in practical terms as a method in Scala
  - a task may have a timeout associated with it
    - if a task is not complete within the allotted time, it is considered failed
    - ultimately, the home node must clean up tasks which time out
  - if everything goes well, a task will produce a *result*, the type of which is task-specific
  - if there is any error in handling the task, the task will produce an *error*
    - error could result from network problem (can't move between nodes) or internal exception (e.g. NPE)
    - assume errors mean the task is not recoverable
  - *task*, *result* and *error* are all classes
- in other words
  - a node which receives *requests* and creates *tasks* which are implemented as *delimited continuations* (or more
  simply, a continuation)
  - a task begins to run as soon as the request posts it
  - a task which completes must return a *result* if it is successful
  - a task which encounters a non-recoverable failure must return an *error*
  - a node receiving a request may impose a time limit for a task to complete
    - a task which has not completed before its timeout must return a *timeout*
  - a node may decide to block on receiving a request, until the alloted time for the request's task has elapsed,
  or earlier
  - a task may execute asynchronously from a request
- in terms of a space
  - assume requests are HTTP requests for now
  - incoming requests to the node are handled by a RequestProcessor
    - can be modeled as an agent, but the HTTP handler must then block until it receives a message from the RP
    - can handle/track more than one request at a time
    - for HTTP requests, the RP tracks the HTTP request and response objects for the request
  - a request to a node results in a Task being created
    - a task has a UUID associated with it
    - a task has a timeout (in milliseconds) associated with it
  - a TaskProcessor (agent?) receives the task and begins executing it, asynchronously from the request
  - if the task's continuation reaches a point where it needs to execute on a remote node, the task is written to the
  space as a TaskEntry
    - the lease for task should be at least as long as the timeoout, that is, the node should not have to re-write
    the task to the space before the timeout elapses
    - the entry must identify the target node where it needs to operate next
    - if the task cannot be written to the space, the TaskProcessor sends an Error message back to the RequestProcessor
    - the RP must monitor the space for Result and Error entries, and for task timeout (no result or error within the
    execution window)
      - on error or result, the RP must message the caller with the result of the operation, and remove any entries
      with the task's UUID from the space
  - nodes in the system monitor the space for TaskEntries targeted at them using notify() and read()
    - will take() the entry when it arrives
    - on an exception during take() the node will write an Error into the space with the task's UUID



 