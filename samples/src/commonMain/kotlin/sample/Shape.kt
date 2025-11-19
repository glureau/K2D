package sample

import com.glureau.k2d.K2DHide

data class Position(val x: Float, val y: Float)


interface Shape {
    val originPosition: Position
    fun computeSurface(): Float {
        return 0f
    }
}

interface Rectangle : Shape {
    val width: Float
    val height: Float
    fun rotate(angle: Float)
}

/**
 * My documentation contains a list:
 * - with one element
 * - and another one
 * - and a last one!
 */
interface Polygon : Shape {
    fun howMuchSides(): Int
}

class Square(override val originPosition: Position) : Polygon {
    val sideSize: Float = 1f

    @K2DHide
    val hiddenValue: Float = 2f

    // A computed value has no backing fields
    val computedValue: Float get() = sideSize

    override fun howMuchSides(): Int = TODO() // Ignored (override)
    public fun publicFun() = Unit // Reported
    private fun privateFun() = Unit // Ignored (visibility)
    protected fun protectedFun() = Unit // Ignored (visibility)
    internal fun internalFun() = Unit // Ignored (visibility)
    override fun computeSurface(): Float = TODO() // Ignored (override)
}

data class Circle(override val originPosition: Position, val radius: Float) : Shape {
    companion object {
        const val TAU: Double = kotlin.math.PI * 2
    }
}

@K2DHide
data class InternalWeirdShape(override val originPosition: Position, val radius: Float) : Shape
