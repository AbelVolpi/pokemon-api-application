package com.abelvolpi.pokemonapi.data.models

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubType(
    @Json(name = "name") val typeName: String = ""
) : Parcelable
