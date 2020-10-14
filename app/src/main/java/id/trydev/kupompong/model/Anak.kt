package id.trydev.kupompong.model

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class Anak (

    var id: String? = null,
    var fullName: String? = null,
    var birthPlace: String? = null,
    var dateOfBirth: String? = null,
    var gender: String? = null,
    var old: Int? = null,
    var address: String? = null,
    var education: String? = null,
    var motorik: String? = null,
    var language: String? = null,
    var diagnose: String? = null,
    var gejala: String? = null,
    var history: String? = null,
    var medicalTreatment: String? = null,
    var imgPath: String? = null,
    var photoUrl: String? = null,
    @ServerTimestamp
    var createdAt: Date? = null,
    @ServerTimestamp
    var updatedAt: Date? = null

): Serializable