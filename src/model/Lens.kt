package model

import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PVector


class Lens(xPos: Int, yPos: Int, var d: Float, var r1: Float, n: Float) : PApplet() {
    var MidPoint: PVector
    var MidPointCircle1: PVector? = null
    var MidPointCircle2: PVector? = null
    var XPoint1: PVector? = null
    var XPoint2: PVector? = null
    var LensSystem: RefSystem? = null
    var nLens = 2.5.toFloat()
    var nOutside = 1.0.toFloat()
    var r2: Float                   //Radius of the lens half
    var angleToXPoint = 0f
    var x = 0f
    var y1 = 0f
    var y2 = 0f
    var MidToMid = 0f
    var Brechwert = 0f
    var lot: PVector? = null

    /**
     * see https://de.wikipedia.org/wiki/Linsenschleiferformel
     */
    fun calcRefractionIndex() {
        Brechwert = (nLens - nOutside) / nOutside * (1 / r1 + 1 / r2) - pow(
            nLens - nOutside,
            2F
        ) * d / (nLens * r1 * r2)
    }

    /**
     * see https://mathworld.wolfram.com/Circle-CircleIntersection.html
     */
    fun calcCrossPoints(): Boolean {
        //check if circles will cut each other on the first half
        if (r1 + r2 <= d) {
            return false
        }
        LensSystem = RefSystem(MidPointCircle2, MidPointCircle1, MidPoint)
        MidToMid = MidPointCircle1!!.copy().sub(MidPointCircle2).mag()
        //x is the length from middlePoint of Circle 1 to the cross point lot to Lens Axis
        x = (pow(r1, 2F) + pow(MidToMid, 2F) - pow(r2, 2F)) / (2 * MidToMid)
        y1 = sqrt(pow(r1, 2F) - pow(x, 2F))
        y2 = -y1
        XPoint1 = PVector(x, y1)
        XPoint2 = PVector(x, y2)
        angleToXPoint = 90 - degrees(asin(x / r1))
        return true
    }

    fun renderHelps(renderContext: PGraphics) {
        renderFocalLength(renderContext)
        LensSystem?.render(renderContext)
        renderContext.endDraw()
    }

    fun renderFocalLength(pgLens: PGraphics) {
        val x: Float = MidPoint.x + LensSystem!!.e1.x * (1 / Brechwert)
        val y: Float = MidPoint.y + LensSystem!!.e1.y * (1 / Brechwert)
        pgLens.fill(0)
        pgLens.beginDraw()
        pgLens.circle(x, y, 5f)
        renderLabel("Brennweite", x, y, pgLens)
        pgLens.endDraw()
    }

    fun renderLabel(label: String?, posX: Float, posY: Float, renderContext: PGraphics) {
        val pretextalign: Int = renderContext.textAlign
        renderContext.textAlign(CENTER, TOP)
        renderContext.text(label, posX, posY + 5)
        renderContext.textAlign(pretextalign)
    }

    fun renderGlas(render_helps: Boolean, renderContext: PGraphics): Boolean {
        if (!calcCrossPoints()) {
            return false
        }
        if (render_helps) {
            renderHelps(renderContext)
        }
        renderContext.beginDraw()
        //Linsenglas
        renderContext.noFill()
        renderContext.arc(
            MidPoint.x - r1 + d / 2,
            MidPoint.y,
            r1 * 2,
            r1 * 2,
            radians(-angleToXPoint),
            radians(angleToXPoint)
        )
        renderContext.arc(
            MidPoint.x + r2 - d / 2,
            MidPoint.y,
            r2 * 2,
            r2 * 2,
            radians(180 - angleToXPoint),
            radians(180 + angleToXPoint)
        )

        //Kreis der Bogen nach rechts Schlägt
        renderContext.fill(color(255, 0, 0))
        renderContext.circle(MidPointCircle1!!.x, MidPointCircle1!!.y, 5f)

        //Kreis der Bogen nach links schlägt
        renderContext.fill(255)
        renderContext.circle(MidPointCircle2!!.x, MidPointCircle2!!.y, 5f)

        //Kreise an Schnittpunkten der Linsenkreise
        renderContext.circle(MidPointCircle1!!.x + x, MidPointCircle1!!.y + y1, 10f)
        renderContext.circle(MidPointCircle1!!.x + x, MidPointCircle1!!.y + y2, 10f)

        renderContext.endDraw()
        return true
    }

    fun setCircles(d_temp: Float, r1_temp: Float) {
        d = d_temp
        r1 = r1_temp
        r2 = r1
        MidPointCircle1 = PVector()
        MidPointCircle2 = PVector()

        //Kreis 1 ist der nach rechts den Bogen schlägt
        MidPointCircle1!!.x = MidPoint.x - r1 + d / 2
        MidPointCircle1!!.y = MidPoint.y

        //Kreis 2 ist der nach links den Bogen schlägt
        MidPointCircle2!!.x = MidPoint.x + r1 - d / 2
        MidPointCircle2!!.y = MidPoint.y
    }

    fun renderLot(PointOnRadius: PVector) {
        line(PointOnRadius.x, PointOnRadius.y, PointOnRadius.x + 50 * lot!!.x, PointOnRadius.y + 50 * lot!!.y)
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
        r2 = r1
        nLens = n
        MidPoint = PVector()
        MidPoint.x = xPos.toFloat()
        MidPoint.y = yPos.toFloat()
        calcRefractionIndex()
        setCircles(d, r1)
    }
}