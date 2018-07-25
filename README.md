# Room15Bot

This is a bot solely for [room 15](https://chat.stackoverflow.com/rooms/15/android) on Stack Overflow chat.

It currently depends on [ChatExchange](https://github.com/TimCastelijns/ChatExchange) for listening to chat events.


## Running

A `config.properties` file must be located in the root of the project with values for at least the following properties:

    # Bot
    botemail=
    botpassword=

    # Database
    dbuser=
    dbpassword=
    dburl=
    dbdriver=

### Database

[Exposed](https://github.com/JetBrains/Exposed) is used as database framework. You are not tied to a specific database type, however it must be one that Exposed supports.

Make sure that

- you are connecting to a valid database (via the config in the file above).
- if you want to use a database/jdbc driver that is not mariadb, update the driver dependency in `build.gradle`.

The bot will create the required tables and populate them (if applicable) for you.

A test database will be provided in the future to ease this process if you just want to test stuff.
