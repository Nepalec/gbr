package com.gbr.data.mapper

import com.gbr.data.model.GitabaseDescNetwork
import com.gbr.model.gitabase.Gitabase
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.gitabase.GitabaseType
import com.gbr.model.gitabase.GitabaseLang

/**
 * Mapper for converting GitabaseDescNetwork to Gitabase domain model.
 */
object GitabaseDescMapper {
    
    /**
     * Maps GitabaseDescNetwork to Gitabase domain model.
     * 
     * @param gitabaseDescNetwork The network model to convert
     * @return Gitabase domain model
     */
    fun toGitabase(gitabaseDescNetwork: GitabaseDescNetwork): Gitabase {
        return Gitabase(
            id = GitabaseID(
                type = GitabaseType(gitabaseDescNetwork.gbalias),
                lang = GitabaseLang(gitabaseDescNetwork.gblang)
            ),
            title = gitabaseDescNetwork.gbname,
            version = 1, // Default version
            filePath = "${gitabaseDescNetwork.gbalias}_${gitabaseDescNetwork.gblang}",
            lastModified = gitabaseDescNetwork.lastModified
        )
    }
    
    /**
     * Maps a list of GitabaseDescNetwork to a list of Gitabase domain models.
     * 
     * @param gitabaseDescNetworks The list of network models to convert
     * @return List of Gitabase domain models
     */
    fun toGitabaseList(gitabaseDescNetworks: List<GitabaseDescNetwork>): List<Gitabase> {
        return gitabaseDescNetworks.map { toGitabase(it) }
    }
}
