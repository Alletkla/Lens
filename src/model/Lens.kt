package model

import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PVector

/**
 * Left means "to the light source" right means further from the light source
 */
class Lens(xPos: Int, yPos: Int, var d: Float, r: Float, n: Float) : PApplet() {
    var midPoint: PVector
    lateinit var circleLeft : Circle
    lateinit var circleRight : Circle
    lateinit var lensSystem: RefSystem
    var nLens = 2.5.toFloat()
    var nOutside = 1.0.toFloat()

    var x = 0f
    var y1 = 0f
    var y2 = 0f
    var midToMid = 0f
    var refractionIndex = 0f
    var lot: PVector? = null

    //all to the Crospoints of both Circles
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
        lensSystem = RefSystem(circleLeft.midPoint, circleRight.midPoint, midPoint)
        midToMid = circleRight.midPoint.copy().sub(circleLeft.midPoint).mag()
        //x is the length from middlePoint of Circle 1 to the cross point lot to Lens Axis
        x = (pow(circleRight.r, 2F) + pow(midToMid, 2F) - pow(circleLeft.r, 2F)) / (2 * midToMid)
        y1 = sqrt(pow(circleRight.r, 2F) - pow(x, 2F))
        y2 = -y1
        xPoint1 = PVector(x, y1)
        xPoint2 = PVector(x, y2)
        angleToXPoint = 90 - degrees(asin(x / circleRight.r))
        return true
    }

    fun renderAccessories(renderContext: PGraphics){
        //KreisMittelpunkt der Bogen nach rechts Schlägt
        renderContext.fill(0)
        renderContext.circle(circleRight.midPoint.x, circleRight.midPoint.y, 5f)

        //KreisMittelpunkt der Bogen nach links schlägt
        renderContext.fill(0)
        renderContext.circle(circleLeft.midPoint.x, circleLeft.midPoint.y, 5f)
    }

    fun renderHelps(renderContext: PGraphics) {
        renderFocalLength(renderContext)
        lensSystem.render(renderContext)
        renderContext.endDraw()
    }

    fun renderFocalLength(pgLens: PGraphics) {
        val x: Float = midPoint.x + lensSystem.e1.x * (1 / refractionIndex)
        val y: Float = midPoint.y + lensSystem.e1.y * (1 / refractionIndex)
        pgLens.fill(0)
        pgLens.beginDraw()
        pgLens.circle(x, y, 5f)
        renderLabel("Brennweite", x, y, pgLens)
        pgLens.endDraw()
    }

    fun renderLabel(label: String?, posX: Float, posY: Float, renderContext: PGraphics) {
        val preTextAlign: Int = renderContext.textAlign
        renderContext.textAlign(CENTER, TOP)
        renderContext.text(label, posX, posY + 5)
        renderContext.textAlign(preTextAlign)
    }

    fun renderGlas(render_helps: Boolean, renderContext: PGraphics): Boolean {
        if (render_helps) {
            renderHelps(renderContext)
        }
        renderContext.beginDraw()
        //Linsenglas
        renderContext.noFill()
        renderContext.arc(
            midPoint.x - circleRight.r + d / 2,
            midPoint.y,
            circleRight.r * 2,
            circleRight.r * 2,
            radians(-angleToXPoint),
            radians(angleToXPoint)
        )
        renderContext.arc(
            midPoint.x + circleLeft.r - d / 2,
            midPoint.y,
            circleLeft.r * 2,
            circleLeft.r * 2,
            radians(180 - angleToXPoint),
            radians(180 + angleToXPoint)
        )

        renderContext.fill(255)
        //Kreise an Schnittpunkten der Linsenkreise
        renderContext.circle(circleRight.midPoint.x + x, circleRight.midPoint.y + y1, 8f)
        renderContext.circle(circleRight.midPoint.x + x, circleRight.midPoint.y + y2, 8f)

        renderContext.endDraw()
        return true
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
    }

    fun renderLot(PointOnRadius: PVector, renderContext: PGraphics) {
        renderContext.line(PointOnRadius.x, PointOnRadius.y, PointOnRadius.x + 50 * lot!!.x, PointOnRadius.y + 50 * lot!!.y)
    }

    fun setLot(PointOnRadius: PVector?, MidPoint: PVector?, innen: Boolean) {
        lot = PVector()
        if (innen) {
            lot!!.set(MidPoint)
            lot!!.sub(PointOnRadius).normalize()
        } else {
            lot!!.set(PointOnRadius)
            lot!!.sub(MidPoint).normalize()
        }
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