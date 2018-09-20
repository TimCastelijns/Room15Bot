package com.timcastelijns.room15bot.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.timcastelijns.room15bot.bot.Bot
import com.timcastelijns.room15bot.bot.eventhandlers.AccessLevelChangedEventHandler
import com.timcastelijns.room15bot.bot.eventhandlers.MessageEventHandler
import com.timcastelijns.room15bot.bot.monitors.ReminderMonitor
import com.timcastelijns.room15bot.bot.usecases.*
import com.timcastelijns.room15bot.data.db.Database
import com.timcastelijns.room15bot.data.db.ReminderDao
import com.timcastelijns.room15bot.data.db.StarredMessageDao
import com.timcastelijns.room15bot.data.repositories.ConfigRepository
import com.timcastelijns.room15bot.data.repositories.StarredMessageRepository
import com.timcastelijns.room15bot.data.repositories.UserRepository
import com.timcastelijns.room15bot.data.repositories.UserStatsRepository
import com.timcastelijns.room15bot.network.StarService
import com.timcastelijns.room15bot.network.UserService
import com.timcastelijns.room15bot.network.UserStatsService
import com.timcastelijns.room15bot.util.MessageFormatter
import com.timcastelijns.room15bot.util.UserNameValidator
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

private val module: Module = applicationContext {

    bean { Database(get()) }

    factory { Bot(get(), get(), get(), get(), get()) }

    bean { AccessLevelChangedEventHandler(get(), get()) }
    bean { MessageEventHandler(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

    factory { GetBuildConfigUseCase(get()) }
    factory { GetUserStatsUseCase(get()) }
    factory { SyncStarsDataUseCase(get(), get()) }
    factory { GetStarsDataUseCase(get()) }
    factory { SetReminderUseCase(get()) }
    factory { CfUseCase() }
    factory { AdamUseCase() }
    factory { MaukerUseCase() }
    factory { AcceptUserUseCase() }
    factory { RejectUserUseCase() }
    factory { UpdateUseCase(Runtime.getRuntime()) }

    bean { ConfigRepository() }
    bean { UserRepository(get()) }
    bean { StarredMessageRepository(get()) }
    bean { UserStatsRepository(get()) }

    bean { ReminderMonitor(get()) }

    bean { StarredMessageDao() }
    bean { ReminderDao() }

    factory { UserNameValidator() }
    factory { MessageFormatter() }

    bean { provideChatRetrofit().create(StarService::class.java) as StarService }
    bean { provideChatRetrofit().create(UserService::class.java) as UserService }
    bean { provideMainRetrofit().create(UserStatsService::class.java) as UserStatsService }

}

fun provideChatRetrofit(): Retrofit =
        Retrofit.Builder()
                .baseUrl("https://chat.stackoverflow.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()

fun provideMainRetrofit(): Retrofit =
        Retrofit.Builder()
                .baseUrl("https://stackoverflow.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()

val modules = listOf(module)
