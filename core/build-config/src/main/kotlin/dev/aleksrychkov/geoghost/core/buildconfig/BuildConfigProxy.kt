package dev.aleksrychkov.geoghost.core.buildconfig

// Important: valid only when Application class is loaded by class loader
object BuildConfigProxy {
    private var _debug: Boolean? = null
    var DEBUG: Boolean
        set(value) {
            if (_debug == null) {
                _debug = value
            }
        }
        get() = requireNotNull(_debug)
}
