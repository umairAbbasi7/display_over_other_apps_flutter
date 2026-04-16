package com.umairabbasi.display_over_other_apps_flutter

import io.flutter.plugin.common.EventChannel

object OverlayEventStream {
    private var eventSink: EventChannel.EventSink? = null

    fun setSink(sink: EventChannel.EventSink?) {
        eventSink = sink
    }

    fun send(event: String) {
        eventSink?.success(event)
    }
}
