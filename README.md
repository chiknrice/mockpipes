[![Build Status](https://travis-ci.org/chiknrice/mockpipes.svg?branch=master)](https://travis-ci.org/chiknrice/mockpipes) [![Coverage Status](https://coveralls.io/repos/github/chiknrice/mockpipes/badge.svg?branch=master)](https://coveralls.io/github/chiknrice/mockpipes?branch=master) [ ![Download](https://api.bintray.com/packages/chiknrice/maven/mockpipes/images/download.svg) ](https://bintray.com/chiknrice/maven/mockpipes/_latestVersion)

# Overview

***mockpipes*** is a framework for mocking socket servers.  It provides fluent APIs to configure behavior.  A behavior
is a pair of event and action.  The server would listen to the event and execute the associated action if the event matches.

The event can be a connected event, a single (or set of) received message(s), a single (or set of) sent message(s).  The
action can be a single action, a list of the same action, and optionally followed by different type of actions.

Behaviors can be persistent or one-off.  One-off behavior would only match the configured event once, in contrast the
persistent behavior would fire the associated actions every time the event/events matches.

Each connection established with MockPipes creates a single session.  Each session is configured with its local copy of
the behavior defined for the server.  This means that given there are two clients connected to MockPipes (client1 and
client2), and if a one-off behavior (e.g. a string message matching "a" replies with "b") has been configured, then even
if the behavior has already been matched for client1, it would still be matched for client2 the first time client2 sends
message "a". 

Along with the behavior the received messages, sent messages, and exceptions related to the session are stored for the
specific session/connection.  These are accessible from the server using the connectionId.  The connectionId can be
captured by defining a behavior which listens to connected event.

## Architecture

The server is implemented using Apache MINA and does not use an executor filter, this means each connection has its own
IoProcessor thread which also performs encoding and decoding.  However, an ExecutorService is used to perform all event
matching and actions, but the tasks for the same session/connection are processed in sequence (more like synchronized
but not).  This means that there are no synchronization in any code which performs the behavior defined for MockPipes -
which makes the server very fast.  

# API

## MockPipes

The API which provides methods to:
 - configure socket server behavior
 - getting all the received messages grouped by connection IDs
 - getting all the sent message grouped by connection IDs 
 - getting all the exceptions thrown within the server (including triggered RaiseExceptionActions)
 - starting the server
 - stopping/destroying the server
 - resetting server state (which clears behavior and collected messages and exceptions)

## Events

MockPipes can react to three type of events:

 - connection established
 - message received
 - message sent

The server can be configured to react to a message (e.g. after receiving message 'a', perform this action...) or a collection of messages (e.g. after receiving messages 'a' ***and*** 'b', perform this action...)

## Actions

MockPipes can perform the following actions in response to an event or a collection of events:
 - send a message
 - expect a message (with an optional timeout)
 - raise an exception (which would be caught by the server)
 - custom action

Send and expect message actions can be a collection and can be chained but raise exception is a terminating action - nothing can be chained after raising an exception.

Collection of actions has to be the same type (e.g. after receiving message 'a', send messages 'b' ***and*** 'c').  It cannot be a mix of actions (e.g. after receiving message 'a', send message 'b' ***and*** raise and exception).

Chained action is different in that two changed actions can be different types (e.g. after receiving message 'a', send message 'b', ***then*** raise and exception).

Collection of message-related actions can also be chained to another type of action (e.g. after receiving message 'a', send messages 'b' ***and*** 'c', then expect messages 'd' ***and*** 'e')

## Usage

The framework provides a ***Builder*** to create a MockPipes, MockPipesClassRule, or a MockPipesMethodRule.

TODO