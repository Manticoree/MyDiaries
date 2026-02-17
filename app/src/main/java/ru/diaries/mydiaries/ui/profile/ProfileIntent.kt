package ru.diaries.mydiaries.ui.profile

sealed class ProfileIntent {
    data object LoadData : ProfileIntent()
    data object StartEditingName : ProfileIntent()
    data object CancelEditingName : ProfileIntent()
    data class UpdateName(val name: String) : ProfileIntent()
    data object SaveName : ProfileIntent()
    data object ShowProfile : ProfileIntent()
}
