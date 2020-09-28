package id.trydev.kupompong.model

import java.io.Serializable

data class Anak (

    var id: String? = null,
    var fullName: String? = null,
    var birthPlace: String? = null,
    var dateOfBirth: String? = null,
    var address: String? = null,
    var education: String? = null,
    var motorik: String? = null,
    var language: String? = null,
    var diagnose: String? = null,
    var gejala: String? = null,
    var history: String? = null,
    var medicalTreatment: String? = null,
    var photoUrl: String? = null

): Serializable