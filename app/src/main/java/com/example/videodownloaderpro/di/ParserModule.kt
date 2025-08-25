package com.example.videodownloaderpro.di

import com.example.videodownloaderpro.data.parser.YouTubeParser
import com.example.videodownloaderpro.domain.parser.VideoParser
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class ParserModule {

    @Binds
    @IntoSet
    abstract fun bindYouTubeParser(parser: YouTubeParser): VideoParser

    // When we add more parsers (e.g., InstagramParser), we just add a new binding here.
    // @Binds
    // @IntoSet
    // abstract fun bindInstagramParser(parser: InstagramParser): VideoParser
}
