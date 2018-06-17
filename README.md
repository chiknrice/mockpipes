# Overview

***mockpipes*** is a framework for mocking socket servers.  It provides a facility to define behavior of socket servers in reaction to events.  The framework is designed to promote fluent APIs.

# API

## MockPipes

The API which provides methods to:
 - configure behavior socket server behavior
 - getting all the received messages grouped by connection IDs
 - getting all the sent message grouped by connection IDs 
 - getting all the exceptions thrown while processing messages grouped by connection IDs
 - starting the server
 - stopping/destroying the server
 - resetting server state

The APIs for configuring the behavior of the socket server are fluent APIs.  Each behavior configuration is composed of an event/events and an action or chain of actions.

The server would listen to the events and execute the actions if the event/events matches.

The behavior can be persistent or one-off.  One-off configuration would only match the configured event once, in contrast the persistent behavior would fire the actions associated to it every time the event matches.

The whole configuration is applied to every connection created to the server, so a one off configuration (e.g. send a message once the event 'connection established' is encountered) would be fired for every connection.

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