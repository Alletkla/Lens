package view

import Settings
import controller.Controller
import model.Lens
import model.manager.ObjectManager
import model.objectRenderer.LensRenderer
import model.objectRenderer.RayRenderer
import model.rayCreator.RayCreator3Rays
import model.rayCreator.RayCreatorAngle
import model.rayCreator.RayCreatorStraight
import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PVector
import java.awt.Color

class ViewPort : PApplet() {

    override fun settings() {
        size(1000, 500)
        smooth()
        Settings.font = loadFont("data/ProcessingSansPro-Regular-48.vlw")
    }

    override fun setup() {
        RenderContextProvider.renderContext = this.g


        frameRate(750f)
        textFont(Settings.font, 20f)         //load Font instead of create improves Performance factor 150
        surface.setLocation(-1050, 300)
        background(255)

        createButtons()

        pgLens = createGraphics(width, height)
        LensRenderer.renderContext = pgLens
        ObjectManager.addLens(Lens(width / 2, height / 2, 80F, 200F, 1.5F))
        ObjectManager.getLensList().forEach {LensRenderer.render(it)}

        LensRenderer.renderContext = this.g

        pgGui = createGraphics(width, height)
        pgGui.beginDraw()
        pgGui.textFont(Settings.font, 20f);
        pgGui.endDraw()

        val start = PVector(70f, 200f)
        val offset = PVector(0f, 0f)
        val offsetHelper = PVector(0f,20f)
        val direction = PVector(1f,0f)
//        ObjectManager.addRay(Ray(start, direction.copy(), 2f))
//        for (i in 0..0) {
//            ObjectManager.addRay(Ray(start.copy().add(offset.add(offsetHelper)), direction.copy(), 2f))
//        }

        ObjectManager.setMode(RayCreator3Rays(ObjectManager.getLensList()[0]))
        ObjectManager.rayCreate(start.copy().add(offset.add(offsetHelper)), Color.CYAN)
        ObjectManager.rayCreate(start.copy().add(offset.add(offsetHelper)), Color.RED)


        Thread.sleep(3500)
    }

    override fun draw() {
        surface.setTitle(frameRate.toInt().toString() + " fps")

        background(255)
        text("X: $mouseX;Y: $mouseY", 50F, 25F)
        image(pgLens, 0f, 0f)

        Controller.processNextFrame()

        for (b in ObjectManager.getButtonList()) {
            b.display()
        }

        for (ray in ObjectManager.getRayList()) {
            RayRenderer.render(ray)
            //text(ray.intersectsLensPlane(lens).toString(), ray.start.x, ray.start.y)
        }

    }

    // val BUTTONS = 3    used later for positioning all buttons accordingly
    val GAP = 10
    val BTN_W = 120
    val BTN_H = 50
    private var pgLens = PGraphics()
    private var pgGui = PGraphics()



    init {
    }

    fun createButtons() {
        val y: Int = height - BTN_H - GAP
        ObjectManager.addButton("straight", object : Button(this@ViewPort, GAP, y, BTN_W, BTN_H, "Straight-\n Mode") {
            override fun action() {
                if (buttonOn) ObjectManager.setMode(RayCreatorStraight()) else ObjectManager.setMode(RayCreatorAngle())
            }
        })
        ObjectManager.addButton("place", object : Button(this@ViewPort, (width - BTN_W) / 2, y, BTN_W, BTN_H, "place \n Object") {
            override fun action() {}
        })
        ObjectManager.addButton("3rays", object : Button(this@ViewPort, width - BTN_W - GAP, y, BTN_W, BTN_H, "all 3 rays") {
            override fun action() {
                ObjectManager.getButtonByID("straight")?.buttonOn = false
                if (buttonOn) ObjectManager.setMode(RayCreator3Rays(ObjectManager.getLensList()[0])) else ObjectManager.setMode(RayCreatorAngle())
            }
        })
    }

    fun getRenderContext() : PGraphics{
        return this.g
    }

    fun isInViewport(pVector: PVector): Boolean {
        if (pVector.x < 0 || pVector.x > width) {
            return false
        }
        if (pVector.y < 0 || pVector.y > height) {
            return false
        }
        return true
    }

    override fun mousePressed() {
        println(mouseY)

        ObjectManager.rayInit(PVector(mouseX.toFloat(), mouseY.toFloat()))
    }

    override fun mouseReleased() {

        for (b in ObjectManager.getButtonList()) {
            if (b.hasClicked()) {
                b.action()
                return
            }
        }

        ObjectManager.rayCreate(PVector(mouseX.toFloat(), mouseY.toFloat()))
    }

}