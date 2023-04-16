package kg.healthlink.myapplication.di

import kg.healthlink.myapplication.ui.news.NewsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModule = module { viewModel { NewsViewModel(get()) } }