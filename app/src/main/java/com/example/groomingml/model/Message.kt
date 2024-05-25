package com.example.groomingml.model

import com.example.groomingml.utils.GroomingStage

data class Message(val id: Int, val text: String, val classification: GroomingStage?) {
    override fun toString(): String {
        //return "Message(text='$text', classification='$classification', id='$id')"
        return "'$text', classification='$classification'"
    }
}