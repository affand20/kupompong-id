package id.trydev.kupompong.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class HasilTerapi (

    var id: String? = null,
    var idAnak: String? = null,
    var namaAnak: String? = null,
    var photoUrl: String? = null,
    var umurAnak: Int? = null,
    var genderAnak: String? = null,
    var topik: String? = null,
    var levelOfPrompt: String? = null,
    var levelOfMastery: String? = null,
    var responAnak: String? = null,
    var tindakLanjut: String? = null,
    @ServerTimestamp
    var dateTerapi: Date? = null

): Serializable