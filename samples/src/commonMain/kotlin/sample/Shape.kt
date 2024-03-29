package sample

import MermaidGraph
import sample.another.Oval

data class Position(val x: Float, val y: Float)


@MermaidGraph("Shapes", [Shape::class, Polygon::class, Circle::class, Rectangle::class, Oval::class])
@MermaidGraph("SquareToShape", [Shape::class, Polygon::class, Square::class])
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

interface Polygon : Shape {
    fun howMuchSides(): Int
}

class Square(override val originPosition: Position) : Polygon {
    val sideSize: Float = 1f
    override fun howMuchSides(): Int = TODO() // Ignored (override)
    public fun publicFun() = Unit // Reported
    private fun privateFun() = Unit // Ignored (visibility)
    protected fun protectedFun() = Unit // Ignored (visibility)
    internal fun internalFun() = Unit // Ignored (visibility)
    override fun computeSurface(): Float = TODO() // Ignored (override)
}

data class Circle(override val originPosition: Position, val radius: Float) : Shape
