package com.raheygaay.app.di

import com.raheygaay.app.BuildConfig
import com.raheygaay.app.data.mock.MockHomeDataSource
import com.raheygaay.app.data.mock.MockMapDataSource
import com.raheygaay.app.data.mock.MockOtherProfileDataSource
import com.raheygaay.app.data.mock.MockProfileDataSource
import com.raheygaay.app.data.mock.MockSupportDataSource
import com.raheygaay.app.data.remote.RemoteHomeDataSource
import com.raheygaay.app.data.remote.RemoteMapDataSource
import com.raheygaay.app.data.remote.RemoteOtherProfileDataSource
import com.raheygaay.app.data.remote.RemoteProfileDataSource
import com.raheygaay.app.data.remote.RemoteSupportDataSource
import com.raheygaay.app.data.repository.HomeRepository
import com.raheygaay.app.data.repository.HomeRepositoryImpl
import com.raheygaay.app.data.repository.MapRepository
import com.raheygaay.app.data.repository.MapRepositoryImpl
import com.raheygaay.app.data.repository.OtherProfileRepository
import com.raheygaay.app.data.repository.OtherProfileRepositoryImpl
import com.raheygaay.app.data.repository.ProfileRepository
import com.raheygaay.app.data.repository.ProfileRepositoryImpl
import com.raheygaay.app.data.repository.SupportRepository
import com.raheygaay.app.data.repository.SupportRepositoryImpl
import com.raheygaay.app.data.source.DataSourceConfig
import com.raheygaay.app.data.source.HomeDataSource
import com.raheygaay.app.data.source.MapDataSource
import com.raheygaay.app.data.source.OtherProfileDataSource
import com.raheygaay.app.data.source.ProfileDataSource
import com.raheygaay.app.data.source.SupportDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDataSourceConfig(): DataSourceConfig = DataSourceConfig(useRemote = BuildConfig.USE_REMOTE)

    @Provides
    @Singleton
    @Named("mockHome")
    fun provideMockHomeDataSource(): HomeDataSource = MockHomeDataSource()

    @Provides
    @Singleton
    @Named("remoteHome")
    fun provideRemoteHomeDataSource(remote: RemoteHomeDataSource): HomeDataSource = remote

    @Provides
    @Singleton
    @Named("mockProfile")
    fun provideMockProfileDataSource(): ProfileDataSource = MockProfileDataSource()

    @Provides
    @Singleton
    @Named("remoteProfile")
    fun provideRemoteProfileDataSource(remote: RemoteProfileDataSource): ProfileDataSource = remote

    @Provides
    @Singleton
    @Named("mockMap")
    fun provideMockMapDataSource(): MapDataSource = MockMapDataSource()

    @Provides
    @Singleton
    @Named("remoteMap")
    fun provideRemoteMapDataSource(remote: RemoteMapDataSource): MapDataSource = remote

    @Provides
    @Singleton
    @Named("mockSupport")
    fun provideMockSupportDataSource(): SupportDataSource = MockSupportDataSource()

    @Provides
    @Singleton
    @Named("remoteSupport")
    fun provideRemoteSupportDataSource(remote: RemoteSupportDataSource): SupportDataSource = remote

    @Provides
    @Singleton
    @Named("mockOtherProfile")
    fun provideMockOtherProfileDataSource(): OtherProfileDataSource = MockOtherProfileDataSource()

    @Provides
    @Singleton
    @Named("remoteOtherProfile")
    fun provideRemoteOtherProfileDataSource(remote: RemoteOtherProfileDataSource): OtherProfileDataSource = remote

    @Provides
    @Singleton
    fun provideHomeRepository(
        @Named("mockHome") mockDataSource: HomeDataSource,
        @Named("remoteHome") remoteDataSource: HomeDataSource,
        config: DataSourceConfig
    ): HomeRepository = HomeRepositoryImpl(mockDataSource, remoteDataSource, config)

    @Provides
    @Singleton
    fun provideProfileRepository(
        @Named("mockProfile") mockDataSource: ProfileDataSource,
        @Named("remoteProfile") remoteDataSource: ProfileDataSource,
        config: DataSourceConfig
    ): ProfileRepository = ProfileRepositoryImpl(mockDataSource, remoteDataSource, config)

    @Provides
    @Singleton
    fun provideMapRepository(
        @Named("mockMap") mockDataSource: MapDataSource,
        @Named("remoteMap") remoteDataSource: MapDataSource,
        config: DataSourceConfig
    ): MapRepository = MapRepositoryImpl(mockDataSource, remoteDataSource, config)

    @Provides
    @Singleton
    fun provideSupportRepository(
        @Named("mockSupport") mockDataSource: SupportDataSource,
        @Named("remoteSupport") remoteDataSource: SupportDataSource,
        config: DataSourceConfig
    ): SupportRepository = SupportRepositoryImpl(mockDataSource, remoteDataSource, config)

    @Provides
    @Singleton
    fun provideOtherProfileRepository(
        @Named("mockOtherProfile") mockDataSource: OtherProfileDataSource,
        @Named("remoteOtherProfile") remoteDataSource: OtherProfileDataSource,
        config: DataSourceConfig
    ): OtherProfileRepository = OtherProfileRepositoryImpl(mockDataSource, remoteDataSource, config)
}
