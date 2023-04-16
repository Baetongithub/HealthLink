package kg.healthlink.myapplication.di

import kg.healthlink.myapplication.ui.news.NewsRepository
import org.koin.dsl.module

val repoModule = module { single { NewsRepository(get()) } }