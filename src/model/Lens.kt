package model

import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PVector

/**
 * Left means "to the light source" right means further from the light source
 */
class Lens(xPos: Int, yPos: Int, var d: Float, r: Float, n: Float) : PApplet() {
    var midPoint: PVector
    lateinit var lensSystem: RefSystem
    var nLens = 2.5.toFloat()
    var nOutside = 1.0.toFloat()
    var refractionIndex = 0f
    val focalLengthPoint1 : PVector by lazy{calcFocalLengthPoint(0)}
    val focalLengthPoint2 : PVector by lazy{calcFocalLengthPoint(1)}

    lateinit var circleLeft : Circle
    lateinit var circleRight : Circle
    var midToMid = 0f

    //all to the cross points of both Circles
    var angleToXPoint : Float = 0F
    lateinit var xPoint1: PVector
    lateinit var xPoint2: PVector

    /**
     * see https://de.wikipedia.org/wiki/Linsenschleiferformel
     * the signs are equal if the middle points are of the same side of the lens (konvex-konkav)
     *
     * @param r1 The radius of the curvature with sign closer to the light source
     * @param r2 The radius of the curvature with sign farther from the light source
     */
    fun calcRefractionIndex(r1 : Float, r2 : Float) : Float {
        return (nLens - nOutside) / nOutside * (1 / r1 + 1 / r2) - pow(
            nLens - nOutside,
            2F
        ) * d / (nLens * r1 * r2)
    }

    /**
     * see https://mathworld.wolfram.com/Circle-CircleIntersection.html
     */
    private fun calcCrossPoints(): Boolean {
        //check if circles will cut each other on the first half
        if (circleRight.r + circleLeft.r <= d) {
            return false
        }
        midToMid = circleRight.midPoint.copy().sub(circleLeft.midPoint).mag()
        //x is the length from middlePoint of Circle 1 to the cross point lot to Lens Axis
        val x = (pow(circleRight.r, 2F) + pow(midToMid, 2F) - pow(circleLeft.r, 2F)) / (2 * midToMid)
        val y1 = sqrt(pow(circleRight.r, 2F) - pow(x, 2F))
        val y2 = -y1
        xPoint1 = PVector(x, y1)
        xPoint2 = PVector(x, y2)
        angleToXPoint = 90 - degrees(asin(x / circleRight.r))
        return true
    }

    private fun calcFocalLengthPoint(index : Int) : PVector{
        return if (index == 0){
            val x: Float = midPoint.x + lensSystem.e1.x * (1 / refractionIndex)
            val y: Float = midPoint.y + lensSystem.e1.y * (1 / refractionIndex)
            PVector(x,y)
        }else{
            val x: Float = midPoint.x - lensSystem.e1.x * (1 / refractionIndex)
            val y: Float = midPoint.y - lensSystem.e1.y * (1 / refractionIndex)
            PVector(x,y)
        }
    }

    private fun setCircles(d: Float, r: Float) {
        val midPointCircle1 = PVector()
        val midPointCircle2 = PVector()

        //Kreis 1 ist der nach rechts den Bogen schlägt
        midPointCircle1.x = midPoint.x - r + d / 2
        midPointCircle1.y = midPoint.y

        this.circleRight = Circle(midPointCircle1, r)

        //Kreis 2 ist der nach links den Bogen schlägt
        midPointCircle2.x = midPoint.x + r - d / 2
        midPointCircle2.y = midPoint.y

        this.circleLeft = Circle(midPointCircle2, r)

        lensSystem = RefSystem(circleRight.midPoint, circleLeft.midPoint, midPoint)
    }

    init {
        nLens = n
        midPoint = PVector()
        midPoint.x = xPos.toFloat()
        midPoint.y = yPos.toFloat()

        refractionIndex = calcRefractionIndex(r, r)
        setCircles(d, r)
        if (!calcCrossPoints()) throw Exception("keine Schnittpunkte für die Linsenkreise gefunden")
    }
}