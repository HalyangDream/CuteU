package com.cute.uibase.event

import com.cute.logic.http.response.list.Filter

sealed class VideoFilterEvent {

    data class FeelingFilterEvent(val filter: Filter) : VideoFilterEvent()
    data class LanguageFilterEvent(val filter: Filter) : VideoFilterEvent()
    data class CountryFilterEvent(val filter: Filter) : VideoFilterEvent()
    data class RegionFilterEvent(val filter: Filter) : VideoFilterEvent()
}