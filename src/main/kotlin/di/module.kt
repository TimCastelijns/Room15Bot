package di

import bot.Bot
import bot.usecases.*
import bot.monitors.ReminderMonitor
import data.db.Database
import data.db.ReminderDao
import data.db.StarredMessageDao
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
import util.MessageFormatter
import util.UserNameValidator

private val module: Module = applicationContext {

    factory { Bot(get(), get(), get(), get(), get(), get(), get(), get()) }
    bean { Database(get()) }

    factory { GetUserStatsUseCase(get()) }
    factory { SyncStarsDataUseCase(get(), get()) }
    factory { GetStarsDataUseCase(get()) }
    factory { SetReminderUseCase(get()) }
    factory { AcceptUserUseCase() }
    factory { RejectUserUseCase() }

    bean { ConfigRepository() }
    bean { StarredMessageRepository(get()) }
    bean { UserStatsRepository(get()) }

    bean { ReminderMonitor(get()) }

    bean { StarredMessageDao() }
    bean { ReminderDao() }

    factory { UserNameValidator() }
    factory { MessageFormatter() }

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
