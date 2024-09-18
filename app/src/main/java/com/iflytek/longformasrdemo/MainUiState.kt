package com.iflytek.longformasrdemo

data class MainUiState(
    val asrText: String
) {
    companion object {

        fun default(): MainUiState {
            return MainUiState(
                asrText = ""
            )
        }
    }
}