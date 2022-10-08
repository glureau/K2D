package sample.another

import sample.Position
import sample.Shape
import sample.Square

/**
 * Please check [this link](https://en.wikipedia.org/wiki/Oval) for reference
 */
@Suppress("Unused")
data class Oval(
    /**
     * This is the original position use to originate the...
     */
    // please it's just a lorem ipsum on stupied classes...
    /* Don't believe these comments, they are not kdoc, only for developer purpose */
    override val originPosition: Position,
    val distanceBetweenPoints: Float,
    /**
     * In Euclidean geometry, an angle is the figure formed by two rays, called the sides of the angle, sharing a common endpoint, called the vertex of the angle.[1] Angles formed by two rays lie in the plane that contains the rays. Angles are also formed by the intersection of two planes. These are called dihedral angles. Two intersecting curves may also define an angle, which is the angle of the rays lying tangent to the respective curves at their point of intersection.
     *
     * Angle is also used to designate the measure of an angle or of a rotation. This measure is the ratio of the length of a circular arc to its radius. In the case of a geometric angle, the arc is centered at the vertex and delimited by the sides. In the case of a rotation, the arc is centered at the center of the rotation and delimited by any other point and its image by the rotation.
     */
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