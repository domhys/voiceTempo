package com.infullmobile.voicetempo

data class SendLogRequest(
        var issue: KeyRequest,
        var author: NameRequest,
        var comment: String,
        var dateStarted: String,
        var timeSpentSeconds: Long
)