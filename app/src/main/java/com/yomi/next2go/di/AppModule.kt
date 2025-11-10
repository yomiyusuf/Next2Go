package com.yomi.next2go.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yomi.next2go.core.common.time.Clock
import com.yomi.next2go.core.common.time.SystemClock
import com.yomi.next2go.core.data.repository.RaceRepositoryImpl
import com.yomi.next2go.core.domain.mapper.RaceDisplayModelMapper
import com.yomi.next2go.core.domain.repository.RaceRepository
import com.yomi.next2go.core.domain.timer.CountdownTimer
import com.yomi.next2go.core.domain.timer.CountdownTimerImpl
import com.yomi.next2go.core.domain.usecase.GetNextRacesUseCase
import com.yomi.next2go.core.network.api.RacingApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.neds.com.au/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun provideRacingApiService(retrofit: Retrofit): RacingApiService =
        retrofit.create(RacingApiService::class.java)

    @Provides
    @Singleton
    fun provideClock(): Clock = SystemClock()

    @Provides
    @Singleton
    fun provideRaceRepository(
        apiService: RacingApiService,
    ): RaceRepository = RaceRepositoryImpl(apiService)

    @Provides
    @Singleton
    fun provideGetNextRacesUseCase(
        repository: RaceRepository,
        clock: Clock,
    ): GetNextRacesUseCase = GetNextRacesUseCase(repository, clock)

    @Provides
    @Singleton
    fun provideRaceDisplayModelMapper(
        clock: Clock,
    ): RaceDisplayModelMapper = RaceDisplayModelMapper(clock)

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob())

    @Provides
    fun provideCountdownTimer(
        scope: CoroutineScope,
    ): CountdownTimer = CountdownTimerImpl(scope)
}
