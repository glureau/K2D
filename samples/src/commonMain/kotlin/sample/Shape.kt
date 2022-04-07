package sample

data class Position(val x: Float, val y: Float)
interface Shape {
    val originPosition: Position
    fun computeSurface(): Float {
        return 0f
    }
}

data class Circle(override val originPosition: Position, val radius: Float) : Shape
interface Rectangle : Shape {
    val width: Float
    val height: Float
    fun rotate(angle: Float)
}

interface Polygon : Shape {
    fun howMushSides(): Int
}

class Square(override val originPosition: Position) : Polygon {
    val sideSize: Float = 1f
    override fun howMushSides(): Int = TODO() // Ignored (override)
    public fun publicFun() = Unit // Reported
    private fun privateFun() = Unit // Ignored (visibility)
    protected fun protectedFun() = Unit // Ignored (visibility)
    internal fun internalFun() = Unit // Ignored (visibility)
    override fun computeSurface(): Float = TODO() // Ignored (override)
}
