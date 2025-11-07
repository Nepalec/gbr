package com.gbr.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.gbr.common.network.Feature
import com.gbr.data.repository.TextsRepository
import javax.inject.Inject
import javax.inject.Singleton

interface FullscreenContent : Feature

@Singleton
class FullscreenContentImpl @Inject constructor(
    private val textsRepository: TextsRepository
) : FullscreenContent {
    override fun registerGraph(
        navHostController: NavHostController,
        navGraphBuilder: NavGraphBuilder
    ) {
        navGraphBuilder.BookDetail(navHostController, textsRepository)
        navGraphBuilder.ChapterDetail(navHostController, textsRepository)
    }
}
