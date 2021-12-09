package view

import Settings
import model.CollisionManager
import model.Lens
import model.Ray
import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PVector

class ViewPort() : PApplet(){

    override fun settings() {
        size(800, 500)
        smooth()
        Settings.font = loadFont("data/ProcessingSansPro-Regular-48.vlw")
    }

    override fun setup() {
        frameRate(1000F)
        textFont(Settings.font, 20f)         //load Font instead of create improves Performance factor 150
        surface.setLocation(-800,300)
        background(255)

        createButtons()

        pgLens = createGraphics(width, height)
        pgGui = createGraphics(width, height)

        pgLens.beginDraw()
        pgLens.textFont(Settings.font, 15F)
        //optische Achse
        pgLens.line(0F, (height / 2).toFloat(), width.toFloat(), (height / 2).toFloat())
        pgLens.endDraw()

        pgGui.beginDraw()
        pgGui.textFont(Settings.font, 20f);


//        //Liste mit allen Strahlen
//        ray_list = arrayOfNulls<Ray>(0)
//        index_pos = 0


        lens = Lens(width / 2, height / 2, 80F, 200F, 1.3F)
        lens.renderGlas(true, pgLens)
        //line(392,226,455.89844,229.59277);

        Thread.sleep(3500)
    }

    override fun draw() {
        //background(255)
        surface.setTitle(frameRate.toInt().toString()  + " fps")
        render()
    }

    // val BUTTONS = 3    used later for positioning all buttons accordingly
    val GAP = 10
    val BTN_W = 120
    val BTN_H = 50
    private val buttonList = mutableListOf<Button>()
    private val rayList = mutableListOf<Ray>()
    private var pgLens = PGraphics()
    private var pgGui = PGraphics()
    private lateinit var lens : Lens


    init {
        val startX = 150F
        for (i in 0..1 ) {
            this.rayList.add(Ray(startX, 290F, 2f).also { it.calcDirection(310F, 290F) })
            this.rayList.add(Ray(startX, 280F, 2f).also { it.calcDirection(310F, 280F) })
            this.rayList.add(Ray(startX, 270F, 2f).also { it.calcDirection(310F, 270F) })
            this.rayList.add(Ray(startX, 260F, 2f).also { it.calcDirection(310F, 260F) })
            this.rayList.add(Ray(startX, 250F, 2f).also { it.calcDirection(310F, 250F) })
            this.rayList.add(Ray(startX, 240F, 2f).also { it.calcDirection(310F, 240F) })
            this.rayList.add(Ray(startX, 230F, 2f).also { it.calcDirection(310F, 230F) })
            this.rayList.add(Ray(startX, 220F, 2f).also { it.calcDirection(310F, 220F) })
            this.rayList.add(Ray(startX, 210F, 2f).also { it.calcDirection(310F, 210F) })
        }
    }

    fun createButtons() {
        val y: Int = height - BTN_H - GAP
        buttonList.add(object : Button(this@ViewPort,GAP, y, BTN_W, BTN_H, "Straight-\n Mode") {
            override fun action() {
                //straightMode = !straightMode
            }
        })
        buttonList.add(object : Button(this@ViewPort, (width - BTN_W) / 2, y, BTN_W, BTN_H, "place \n Object") {
            override fun action() {}
        })
        buttonList.add(object : Button(this@ViewPort, width - BTN_W - GAP, y, BTN_W, BTN_H, "all 3 rays") {
            override fun action() {}
        })
    }

    fun render(){
        background(255)
        text("X: $mouseX;Y: $mouseY", 50F,25F)
        image(pgLens,0f,0f)

        for (b in buttonList) b.display()

        stroke(0)
        for (ray in rayList) {
            if (!isInViewport(ray.end)){
                continue
            }
            ray.move()
            if (CollisionManager.rayCollidesLens(ray, lens, this.g)) {
                lens.setLot(ray.end, ray.referenceCircle?.midPoint, ray.inLens)
                ray.move()
                //lens.renderLot(ray.end, this.g)
                ray.refract(lens, this)
            }
            ray.render(this)


            //also move all nested Rays
            var currentRay = ray
            while (currentRay.refracted_ray != null) {
                if (!isInViewport(currentRay.refracted_ray!!.end)){
                    currentRay.refracted_ray!!.render(this)
                    currentRay = currentRay.refracted_ray!!
                    continue
                }
                currentRay.refracted_ray!!.move()
                if (CollisionManager.rayCollidesLens(currentRay.refracted_ray!!, lens, this.g)) {
                    lens.setLot(
                        currentRay.refracted_ray!!.end,
                        currentRay.refracted_ray!!.referenceCircle?.midPoint,
                        currentRay.refracted_ray!!.inLens
                    )
                    currentRay.refracted_ray!!.move()
                    //lens.renderLot(ray.end, this.g)
                    currentRay.refracted_ray!!.refract(lens, this)
                }
                currentRay.refracted_ray!!.render(this)
                currentRay = currentRay.refracted_ray!!
            }
            //text(ray.intersectsLensPlane(lens).toString(), ray.start.x, ray.start.y)
        }

    }

    fun isInViewport(pVector: PVector) : Boolean
    {
        if (pVector.x < 0 || pVector.x > width){
            return false
        }
        if (pVector.y < 0 || pVector.y > height){
            return false
        }
        return true
    }
    override fun mousePressed(){
        println(mouseY)
        if (mouseY < height - GAP - BTN_H) {
            this.rayList.add(Ray(mouseX.toFloat(), mouseY.toFloat(), 2f))
        } else {
            for (b in buttonList) if (b.hasClicked()) {
                b.action()
                break
            }
        }
    }

    override fun mouseReleased() {
        if (mouseY < height - GAP - BTN_H) {
//            if (straightMode) {
//                rayList[rayList.size-1] .direction.set(1F, 0F)
//            } else {
                rayList[rayList.size-1].calcDirection(mouseX.toFloat(), mouseY.toFloat())
//            }
            rayList[rayList.size-1].intersectsLensPlane(lens)
            rayList[rayList.size-1].render(this)
        }
    }

}