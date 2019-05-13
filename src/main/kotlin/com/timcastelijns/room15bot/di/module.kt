package com.timcastelijns.room15bot.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.timcastelijns.room15bot.bot.Bot
import com.timcastelijns.room15bot.bot.eventhandlers.AccessLevelChangedEventHandler
import com.timcastelijns.room15bot.bot.eventhandlers.MessageEventHandler
import com.timcastelijns.room15bot.bot.monitors.ReminderMonitor
import com.timcastelijns.room15bot.bot.usecases.*
import com.timcastelijns.room15bot.data.db.*
import com.timcastelijns.room15bot.data.repositories.ConfigRepository
import com.timcastelijns.room15bot.data.repositories.StarredMessageRepository
import com.timcastelijns.room15bot.data.repositories.UserRepository
import com.timcastelijns.room15bot.data.repositories.UserStatsRepository
import com.timcastelijns.room15bot.network.StarService
import com.timcastelijns.room15bot.network.UserService
import com.timcastelijns.room15bot.network.UserStatsService
import com.timcastelijns.room15bot.util.MessageFormatter
import com.timcastelijns.room15bot.util.UserNameValidator
import org.koin.dsl.module
import org.koin.experimental.builder.factory
import org.koin.experimental.builder.single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

private val module = module {

    single<Bot>()

    single<AccessLevelChangedEventHandler>()
    single<MessageEventHandler>()

    factory<GetBuildConfigUseCase>()
    factory<GetUserStatsUseCase>()
    factory<SyncStarsDataUseCase>()
    factory<GetTopMessageUseCase>()
    factory<GetStarsDataUseCase>()
    factory<SetReminderUseCase>()
    factory<GetProfileUseCase>()
    factory<UpdateProfileUseCase>()
    factory<NorsemenReferenceUseCase>()
    factory<AdamUseCase>()
    factory<MaukerUseCase>()
    factory<AhmadUseCase>()
    factory<DaveUseCase>()
    factory<AcceptUserUseCase>()
    factory<RejectUserUseCase>()

    factory { UpdateUseCase(Runtime.getRuntime()) }

    single<ConfigRepository>()
    single<UserRepository>()
    single<StarredMessageRepository>()
    single<UserStatsRepository>()

    single<ReminderMonitor>()

    single<UserDao>()
    single<UserProfileDao>()
    single<StarredMessageDao>()
    single<ReminderDao>()

    factory<UserNameValidator>()
    factory<MessageFormatter>()

    single { provideChatRetrofit().create(StarService::class.java) as StarService }
    single { provideChatRetrofit().create(UserService::class.java) as UserService }
    single { provideMainRetrofit().create(UserStatsService::class.java) as UserStatsService }

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

val room15botModules = listOf(module)
