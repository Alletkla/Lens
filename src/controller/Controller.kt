package controller

import model.manager.CollisionManager
import model.manager.ObjectManager
import model.objectRenderer.RayRenderer

object Controller {

    val lens = ObjectManager.getLensList()[0]

    fun processNextFrame(){
        for (ray in ObjectManager.getRayList()) {
            if (ray.process) ray.move()
        }
    }
}