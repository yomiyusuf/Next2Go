package com.yomi.next2go.core.domain.mvi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

abstract class BaseMviViewModel<State : UiState, Intent : UiIntent, SideEffect : UiSideEffect>(
    initialState: State,
) : ViewModel(), MviViewModel<State, Intent, SideEffect> {

    private val _uiState = MutableStateFlow(initialState)
    override val uiState: StateFlow<State> = _uiState.asStateFlow()

    private val _sideEffect = Channel<SideEffect>(Channel.BUFFERED)
    override val sideEffect = _sideEffect.receiveAsFlow()

    protected val currentState: State
        get() = _uiState.value

    protected fun updateState(transform: (State) -> State) {
        _uiState.value = transform(currentState)
    }

    protected suspend fun emitSideEffect(effect: SideEffect) {
        _sideEffect.send(effect)
    }

    abstract override fun handleIntent(intent: Intent)
}
