package com.gbr.scrtext.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.common.strings.StringProvider
import com.gbr.data.repository.TextsRepository
import com.gbr.model.book.BookDetail
import com.gbr.model.book.BookPreview
import com.gbr.model.book.TextDetailItem
import com.gbr.model.gitabase.GitabaseID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextViewModel @Inject constructor(
    private val textsRepository: TextsRepository,
    private val stringProvider: StringProvider,
    private val colorProvider: TextHtmlColorProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(TextUiState())
    val uiState: StateFlow<TextUiState> = _uiState.asStateFlow()

    fun initialize(
        gitabaseId: GitabaseID,
        bookPreview: BookPreview,
        chapterNumber: Int?,
        textNumber: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, gitabaseId = gitabaseId)

            try {
                // Load BookDetail to determine book structure
                val detailResult = textsRepository.getBookDetail(gitabaseId, bookPreview, extractImages = false)
                detailResult.onSuccess { detail ->
                    // Get total text count
                    val countResult = textsRepository.getBookTextsCount(gitabaseId, bookPreview)
                    countResult.onSuccess { totalCount ->
                        // Find initial text index
                        val indexResult = textsRepository.findTextIndexByTextNumber(
                            gitabaseId,
                            bookPreview,
                            chapterNumber,
                            textNumber
                        )
                        indexResult.onSuccess { initialIndex ->
                            // Load initial adjacent texts (current ± 2)
                            val startIndex = (initialIndex - 2).coerceAtLeast(0)
                            val endIndex = (initialIndex + 2).coerceAtMost(totalCount - 1)

                            val textsResult = textsRepository.getTextsByIndexRange(
                                gitabaseId,
                                bookPreview,
                                startIndex,
                                endIndex
                            )
                            textsResult.onSuccess { texts ->
                                val loadedTextsMap = texts.associateBy { it.preview.id }
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    totalTextCount = totalCount,
                                    loadedTexts = loadedTextsMap,
                                    bookPreview = bookPreview,
                                    bookDetail = detail,
                                    currentTextIndex = initialIndex,
                                    initialTextIndex = initialIndex
                                )
                            }.onFailure { error ->
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    error = error.message ?: "Failed to load texts"
                                )
                            }
                        }.onFailure { error ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = error.message ?: "Failed to find initial text"
                            )
                        }
                    }.onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to get text count"
                        )
                    }
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load book detail"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun loadTextsForIndex(index: Int, gitabaseId: GitabaseID? = null) {
        val currentState = _uiState.value
        if (currentState.loadedTexts.containsKey(index)) {
            // Already loaded
            return
        }

        viewModelScope.launch {
            val bookPreview = currentState.bookPreview ?: return@launch
            val effectiveGitabaseId = gitabaseId ?: currentState.gitabaseId ?: return@launch
            val totalCount = currentState.totalTextCount

            try {
                // Determine range to load (index ± 2, clamped to valid range)
                val startIndex = (index - 2).coerceAtLeast(0)
                val endIndex = (index + 2).coerceAtMost(totalCount - 1)

                // Load texts in range
                val textsResult = textsRepository.getTextsByIndexRange(
                    effectiveGitabaseId,
                    bookPreview,
                    startIndex,
                    endIndex
                )
                textsResult.onSuccess { texts ->
                    val updatedMap = currentState.loadedTexts.toMutableMap()
                    texts.forEach { text ->
                        updatedMap[text.preview.id] = text
                    }
                    _uiState.value = currentState.copy(loadedTexts = updatedMap)
                }
            } catch (e: Exception) {
                // Silently fail - text will show loading indicator
            }
        }
    }

    fun onPageChanged(newIndex: Int) {
        _uiState.value = _uiState.value.copy(currentTextIndex = newIndex)
        loadTextsForIndex(newIndex)
    }

    fun getTextHtml(text: TextDetailItem): String {
        val colors = colorProvider
        val isDarkMode = colors.isDarkMode()
        val sNM = if (isDarkMode) "_d" else ""

        // Color strings
        val strColorSanskrit = colors.getSanskritColor()
        val strColorTranslit = colors.getTranslitColor()
        val strColorWBW = colors.getWordByWordColor()
        val strColorV = colors.getTranslationColor()
        val strColorP = colors.getCommentColor()
        val strColorBG = colors.getBackgroundColor()
        val sLinkColor = colors.getLinkColor()
        val sHLColor2 = colors.getHighlightColorQuestion()
        val sHLColor3 = colors.getHighlightColorNote()
        val sHLColor4 = colors.getHighlightColorHighlight()
        val sSrchColor = colors.getSearchColor()
        val sSrchColor_HL = colors.getSearchHighlightColor()

        // Constants for div IDs
        val DIV_SANSKRIT = "divSanskrit"
        val DIV_TRANSLIT = "divTranslit"
        val DIV_WBW = "divWBW"
        val DIV_VERSE = "divVerse"
        val DIV_PURPORT = "divPurport"

        // Check if sanskrit is available
        val bSanskritAvailable = text.sanskrit.isNotEmpty() || text.translit.isNotEmpty() || text.wordByword.isNotEmpty()

        // Build CSS styles
        val cssStyles = """
            <style>
            #divBottomPaginationFooter {
                height:10px;
            }
            .body_styles {
                padding-left: 4px;
                padding-right: 10px;
                padding-bottom: 50px;
                background-color: $strColorBG;
            }
            .androidFix {
                overflow:hidden !important;
                overflow-y:hidden !important;
                overflow-x:hidden !important;
            }
            blockquote {
                border-left: 5px solid $strColorV;
                font-size: 90%;
                line-height:1.2;
                padding-left:10px;
                left:20px;
                margin: 10px auto;
                width:auto;
                max-width:90%;
            }
            .extra_action {
                text-decoration:underline;
                text-decoration-style: dotted;
                margin-top:20px;
                color: $strColorV;
                font-size: 70%;
                font-weight: normal;
            }
            img {
                display: inline;
                height: auto;
                max-width: 90%;
                margin: 0;
                border-radius: 4%;
            }
            .hl2 {
                background: $sHLColor2;
            }
            .hl3 {
                background: $sHLColor3;
            }
            .hl4 {
                background: $sHLColor4;
            }
            .srch_res {
                color: $sSrchColor;
            }
            .srch_res_HL {
                color: $sSrchColor;
                background: $sSrchColor_HL;
            }
            .verseSanskrit {
                margin-bottom:10px;
                color:$strColorSanskrit;
                text-align:center;
                font-size: 100%;
            }
            .verseTranslit {
                margin-bottom:10px;
                color: $strColorTranslit;
                text-align:center;
                font-size: 110%;
            }
            .verseTranslWBW i { color: $strColorTranslit;}
            .verseTranslWBW {
                color:$strColorWBW;
                margin-bottom:20px;
            }
            .verseTransl2 {
                color: $strColorV;
                text-align: left;
                font-size: 100%;
                margin-bottom: 10px;
                font-weight: bold;
            }
            .$DIV_PURPORT {
                color: $strColorP;
                font-size: 100%;
                text-align: left;
                font-weight: normal;
            }
            a:link {
                color: $sLinkColor;
            }
            p {
                line-height: 120%;
                margin-bottom: 10px;
                padding: 0px;
            }
            .wbw_img {
                opacity: 0.2;
                display: block;
                margin-left: auto;
                margin-right: auto;
            }
            </style>
        """.trimIndent()

        // Build JavaScript
        val javascript = """
            <script language="javascript">
            var sanskrVisible = true;
            function toggleSanskrit() {
                var devanag = document.getElementById('$DIV_SANSKRIT');
                var translit = document.getElementById('$DIV_TRANSLIT');
                var wbw = document.getElementById('$DIV_WBW');
                if(devanag) changeVis(devanag, sanskrVisible);
                if(translit) changeVis(translit, sanskrVisible);
                if(wbw) changeVis(wbw, sanskrVisible);
                sanskrVisible = !sanskrVisible;
            }
            function getScrollPercentage() {
                var scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
                var scrollHeight = document.documentElement.scrollHeight || document.body.scrollHeight;
                var clientHeight = document.documentElement.clientHeight || window.innerHeight;
                return (scrollTop / (scrollHeight - clientHeight)) * 100;
            }
            window.onscroll = function() {
                // Scroll tracking can be added here if needed
            };
            function changeVis(div, visibility) {
                if(typeof(div) == 'undefined' || div == null) return;
                try {
                    var disp; var vis;
                    if(!visibility) {disp='none'; vis='hidden';}
                    else {disp='block';vis='visible';}
                    div.style.display = disp;
                    div.style.visibility = vis;
                } catch(e) { }
            }
            var hideWBW = false;
            function wbw_clicked(nm, dens, bSave) {
                var wbwDiv = document.getElementById('$DIV_WBW');
                if(wbwDiv) {
                    changeVis(wbwDiv, hideWBW);
                    hideWBW = !hideWBW;
                }
            }
            function getScroll() {
                return window.pageYOffset;
            }
            function webviewScrollToY(y) {
                window.scroll(0, y);
            }
            function webviewScrollPage(step) {
                var doc = document.documentElement;
                var top = (window.pageYOffset || doc.scrollTop) - (doc.clientTop || 0);
                var pageH = window.innerHeight;
                var totalH = document.body.scrollHeight;
                if(step<0 && top==0) return;
                if(step>0 && top+2*step*pageH > totalH) {
                    var diff = parseInt(top+2.1*step*pageH-totalH, 10);
                    var footer = document.getElementById('divBottomPaginationFooter');
                    var footerH = footer ? footer.clientHeight : 0;
                    if(footerH < pageH*0.7 && footer) footer.style.height = diff + 'px';
                }
                window.scrollBy(0, step*pageH);
            }
            function onLoad() {
                toggleSanskrit();
            }
            </script>
        """.trimIndent()

        // Build HTML header
        val header = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, target-densityDpi=device-dpi">
                $cssStyles
                $javascript
            </head>
            <body class='body_styles' onload='onLoad();'>
        """.trimIndent()

        // Build content sections
        val content = buildString {
            if (bSanskritAvailable) {
                if (text.sanskrit.isNotEmpty()) {
                    append("<div class='verseSanskrit' id='$DIV_SANSKRIT'>${escapeHtml(text.sanskrit)}</div>")
                }
                if (text.translit.isNotEmpty()) {
                    append("<div class='verseTranslit' id='$DIV_TRANSLIT'>${escapeHtml(text.translit)}</div>")
                }
                if (text.wordByword.isNotEmpty()) {
                    val sHideOrShow = "show"
                    append("<img onclick=\"wbw_clicked('$sNM', 'mdpi', true)\" id='wbw_img' class='wbw_img' src='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=='/>")
                    append("<div class='verseTranslWBW' id='$DIV_WBW'>${escapeHtml(text.wordByword)}</div>")
                }
            }

            // Translation/verse section
            if (text.translation.isNotEmpty()) {
                append("<div class='verseTransl2' id='$DIV_VERSE'>${escapeHtml(text.translation)}</div>")
            }

            // Comment/purport section
            if (text.comment.isNotEmpty()) {
                append("<div class='$DIV_PURPORT' id='$DIV_PURPORT'>${escapeHtml(text.comment)}</div>")
            }
        }

        // Build footer
        val footer = "<div id='divBottomPaginationFooter'></div></body></html>"

        return header + content + footer
    }

    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }
}

data class TextUiState(
    val isLoading: Boolean = true,
    val totalTextCount: Int = 0,
    val loadedTexts: Map<Int, TextDetailItem> = emptyMap(),
    val bookPreview: BookPreview? = null,
    val bookDetail: BookDetail? = null,
    val currentTextIndex: Int = 0,
    val initialTextIndex: Int? = null,
    val error: String? = null,
    val gitabaseId: GitabaseID? = null
)
