package di

import bot.Bot
import data.commands.*
import data.db.Database
import data.repositories.ConfigRepository
import data.repositories.StarredMessageRepository
import data.repositories.UserStatsRepository
import network.StarService
import network.UserStatsService
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import util.UserNameValidator

private val module: Module = applicationContext {

    factory { Bot(get(), get(), get(), get()) }
    bean { Database(get()) }

    factory { GetUserStatsCommand(get()) }
    factory { SyncStarsDataCommand(get()) }
    factory { GetStarsOverviewCommand(get()) }
    factory { GetStarsDataCommand() }
    factory { SetReminderCommand() }

    bean { ConfigRepository() }
    bean { StarredMessageRepository(get()) }
    bean { UserStatsRepository(get()) }

    factory { UserNameValidator() }

    bean { provideChatRetrofit().create(StarService::class.java) as StarService }
    bean { provideMainRetrofit().create(UserStatsService::class.java) as UserStatsService }

}

fun provideChatRetrofit(): Retrofit =
        Retrofit.Builder()
                .baseUrl("https://chat.stackoverflow.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

fun provideMainRetrofit(): Retrofit =
        Retrofit.Builder()
                .baseUrl("https://stackoverflow.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

val modules = listOf(module)
