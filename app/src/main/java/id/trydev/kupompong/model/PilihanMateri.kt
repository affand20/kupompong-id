package id.trydev.kupompong.model

import java.io.Serializable

data class PilihanMateri (

    var id: String? = null,
    var caption: String? = null,
    var imgUrl: String? = null,
    var imgPath: String? = null,
    var audioUrl: String? = null,
    var audioPath: String? = null

): Serializable