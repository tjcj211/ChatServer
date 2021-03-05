The names of everyone on the team. The names should also appear in the commenting for proper credit to be given to the work done.
    Timothy Carta, Giovanni Greco, Ryan Hayes, Griffin King, Aden Mariyappa


Instructions on how to compile and run both the server and client code.
    Navigate to the folder where ChatServer.java is held using a terminal
    exexute "javac ChatServer.java"
    exexute "java ChatServer"

    Navigate to the folder where ChatClient.java is held using a terminal
    execute "javac ChatClient.java"
    execute "java ChatClient"


A breakdown of the work that each member of the team did, what parts were contributed by that individual.
    Timothy Carta
        - Created server using InventoryServer as a base
        - Made client connect to server
        - Made information pass between client and server
        - Added Room selection option
        - Made it so that when one cleint sends a message, all other clients recieve
        - Entering and exiting is broadcast to clients
        - Comments throughout ChatServer.java
        - Client can change rooms
        - Added logging. I am not sure if logging generally will remove the old log, so that is how I implemented it
        - Server disconnects client when they exit

    Giovanni Greco
        - Attempted to make client connect to server
        - Attempted to make client send and recieve messages and display the messages in the two text areas
        
    Ryan Hayes
        - User enters the chatroom with a specified name (ENTER)
        - User entering the chatroom notifies the server which notifies the other clients (ACK ENTER, ENTERING)
        - User can specify which room they want to join and the server acknowledges the request (JOIN, ACK JOIN)
        - User can transmit messages to the server which tells the server to transmit the message to other clients (TRANSMIT, NEWMESSAGE)
        - Added server-side room support by adding the additional flags to the necessary methods
        - Added the checks to see which room the user was in so they received the correct messages
        - Bug fixes

    Griffin King
        -

    Aden Mariyappa
        -


A description of how the tasks were divided out among the members.
    We created a group chat adn split the team into two subteams. The team that would work on the client and the team that would work on the server.
    People were allowed to pick which requirements they wanted to try to implement.
