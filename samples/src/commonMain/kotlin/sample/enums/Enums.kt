package sample.enums

@Suppress("Unused")
enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

enum class DeviceKind(val userAgent: String, val index: Int) {
    Smartphone("ios", 0),
    Computer("Firefox", 1),
    Tablet("Xiaomi", 2),
    Smartwatches("Samsung browser", 4),
    Chromebook("Chrome", 3)
}