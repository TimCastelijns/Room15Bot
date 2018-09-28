[![CircleCI](https://circleci.com/gh/TimCastelijns/Room15Bot/tree/master.svg?style=shield)](https://circleci.com/gh/TimCastelijns/Room15Bot/tree/master)

# Room15Bot

This is a bot solely for [room 15](https://chat.stackoverflow.com/rooms/15/android) on Stack Overflow chat.

It currently depends on [ChatExchange](https://github.com/TimCastelijns/ChatExchange) for listening to chat events.


## Usage

Commands are prefixed with `!`. Commands are only recognized when they are at the start of a message. Commands are case insensitive, however their arguments might not be.

Available commands are listed below. Some require an elevated access level. 

|Name|Format|Example|Access|Description
|-|-|-|-|-
|Status|!status|!status|All|Display a status message
|Stats me|!stats|!stats|All|Shows your stats
|Stats user|!stats \<user id\>|!stats 1|All|Shows a user's stats
|Stars any|!stars|!stars|All|Shows the most starred messages of the room
|Stars user|!stars \<user name\>|!stars john|All|Shows a user's most starred messages
|Remind me|!remindme \<future date expression\>|!remindme 2h|All|Sets a reminder. When it expires, the bot will ping you
|||!remindme in 5 mins|
|CF|!cf\<index(optional)\>|!cf|All|Does a good impression of CF
|||!cf[0]|
|Adam|!adam|!adam|All|Finds lost things
|Mauker|!mauker|!mauker|All|Mocks Mauker
|Benz|!🚗|!🚗|All|Checks your car
|Accept user|!accept \<user name\>|!accept john|Room owner|Posts a welcome message for the user and grants him write access
||!accept|!accept|Room owner|Accepts the most recent user to request access
|Reject user|!reject \<user name\>|!reject john|Room owner|Posts a rejection message for the user and clears his access
||!reject|!reject|Room owner|Rejectsthe most recent user to request access
|Stop bot|!ahmad|!ahmad|Bot owner|Stops the bot
|Sync stars data|!syncstars|!syncstars|Bot owner|Synchronizes all starred messages data

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

### Database

[Exposed](https://github.com/JetBrains/Exposed) is used as database framework. You are not tied to a specific database type, however it must be one that Exposed supports.

Make sure that

- you are connecting to a valid database (via the config in the file above).
- if you want to use a database/jdbc driver that is not mariadb, update the driver dependency in `build.gradle`.

The bot will create the required tables and populate them (if applicable) for you.

A test database will be provided in the future to ease this process if you just want to test stuff.
