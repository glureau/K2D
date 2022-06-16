package sample.another

import sample.Position
import sample.Shape
import sample.Square

@Suppress("Unused")
data class Oval(
    override val originPosition: Position,
    val distanceBetweenPoints: Float,
    val angle: Float,
    val radius: Float,
) : Shape {
    val centerOne: Position = TODO()
    val centerTwo: Position = TODO()

    inner class InnerOval {
        // Not supported yet
        val square: Square = TODO()
    }

    companion object Builder {
        // Not supported yet
        fun build(): Oval = TODO()
    }
}

@Suppress("Unused")
class TheTrap {
    val foo: Int = 0
}