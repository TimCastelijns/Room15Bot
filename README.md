[![CircleCI](https://circleci.com/gh/TimCastelijns/Room15Bot/tree/master.svg?style=shield)](https://circleci.com/gh/TimCastelijns/Room15Bot/tree/master)

# Room15Bot

This is a bot solely for [room 15](https://chat.stackoverflow.com/rooms/15/android) on Stack Overflow chat.

It currently depends on [ChatExchange](https://github.com/TimCastelijns/ChatExchange) for listening to chat events.


## Usage

Commands are prefixed with `!`. Commands are only recognized when they are at the start of a message. Commands are case insensitive, however their arguments might not be.

Available commands are listed below. Some require an elevated access level. 

### Commands for all users

Name|Format|Example|Description
---|---|---|---
Status|!status|!status|Display a status message
Stats me|!stats|!stats|Shows your stats
Stats user|!stats \<user id\>|!stats 1|Shows a user's stats
Stars any|!stars|!stars|Shows the most starred messages of the room
Stars week|!stars week||Shows the most starred messages of the room of the past week
Stars month|!stars month||Shows the most starred messages of the room of the past month
Stars user|!stars \<user name\>|!stars john|Shows a user's most starred messages
Remind me|!remindme \<future date expression\>|!remindme 2h|Sets a reminder. When it expires, the bot will ping you
||!remindme in 5 mins|
Adam|!adam|!adam|Finds lost things
Mauker|!mauker|!mauker|Mocks Mauker
Ahmad|!ahmad|!ahmad|Asks Ahmad about anything
Benz|!🚗|!🚗|All|Checks your car
Dave|!dave|!dave|Creates realistic simulation of interacting with Dave

### Commands for room owners

Name|Format|Example|Description
---|---|---|---
Accept user|!accept \<user name\>|!accept john|Posts a welcome message for the user and grants him write access
|!accept|!accept|Accepts the most recent user to request access
Reject user|!reject \<user name\>|!reject john|Posts a rejection message for the user and clears his access
|!reject|!reject|Rejects the most recent user to request access

### Command for bot owners

Name|Format|Example|Description
---|---|---|---
Stop bot|!leave|!leave|Stops the bot
Sync stars data|!syncstars|!syncstars|Synchronizes all starred messages data

Access levels do not overlap. I.e. a bot owner is not necessarily a room owner.

For more detailed information on how each command is processed, what input it will take and what aliases it has, you can check its UseCase [here](https://github.com/TimCastelijns/Room15Bot/tree/master/src/main/kotlin/com/timcastelijns/room15bot/bot/usecases). For more detailed information on what constitutes a command you can check its pattern in the [CommandParser](https://github.com/TimCastelijns/Room15Bot/blob/master/src/main/kotlin/com/timcastelijns/room15bot/util/CommandParser.kt).


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

When running tests locally, this file must also include the following properties:

    dburltest=

### Database

[Exposed](https://github.com/JetBrains/Exposed) is used as database framework. You are not tied to a specific database type, however it must be one that Exposed supports.

Make sure that

- you are connecting to a valid database (via the config in the file above). Note that tests use a different database. You should create both.
- if you want to use a database/jdbc driver that is not mariadb, update the driver dependency in `build.gradle`.

The bot will create the required tables and populate them (if applicable) for you.

A test database will be provided in the future to ease this process if you just want to test stuff.
