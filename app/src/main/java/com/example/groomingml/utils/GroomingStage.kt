package com.example.groomingml.utils

enum class GroomingStage(val valor: Int, val description: String) {
    FRIENDSHIP(0, "Friendship"),
    RELATIONSHIP(1, "Relationship"),
    SEX(2, "Sex"),
    APPROACH(3, "Approach");

    companion object {
        fun getGroomingStage(valor: Int): GroomingStage? {
            return values().find { it.valor == valor }
        }
    }
}