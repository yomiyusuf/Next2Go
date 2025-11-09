package com.yomi.next2go.core.domain.mvi

import kotlinx.coroutines.flow.Flow

interface UiState

interface UiIntent

interface UiSideEffect

interface MviViewModel<State : UiState, Intent : UiIntent, SideEffect : UiSideEffect> {
    val uiState: Flow<State>
    val sideEffect: Flow<SideEffect>
    fun handleIntent(intent: Intent)
}