package com.trapezoidlimited.groundforce.di

import com.trapezoidlimited.groundforce.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton


@InstallIn(ApplicationComponent::class)
@Module
class NetworkModule {


    @Singleton
    @Provides
    fun provideApiService():ApiService{
        return ApiService.create()
    }

}