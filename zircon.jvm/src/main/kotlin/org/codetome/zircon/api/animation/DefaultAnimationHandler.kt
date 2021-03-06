package org.codetome.zircon.api.animation

import org.codetome.zircon.api.animation.AnimationState.*
import org.codetome.zircon.api.graphics.Layer
import org.codetome.zircon.api.screen.Screen
import org.codetome.zircon.internal.animation.DefaultAnimationResult
import org.codetome.zircon.api.util.Identifier
import java.io.Closeable
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.function.Consumer

/**
 * Note that this class is in **BETA**!
 * It's API is subject to change!
 */
class DefaultAnimationHandler(private val screen: Screen) : AnimationHandler, Closeable {

    // TODO: this needs to be refactored to support multiplatform code
    private val pool = Executors.newFixedThreadPool(1)

    private var running = true
    private lateinit var jobResult: Future<*>
    private lateinit var job: AnimationJob

    init {
        restartAnimatorJob()
    }

    override fun addAnimation(animation: Animation): AnimationResult {
        if (running) {
            if (jobResult.isDone) {
                restartAnimatorJob()
            }
            return job.addAnimation(animation)
        } else {
            throw IllegalStateException("This AnimationHandler is not running anymore!")
        }
    }

    override fun close() {
        this.running = false
        job.close()
        pool.shutdownNow()
    }

    private fun restartAnimatorJob() {
        job = AnimationJob(screen)
        jobResult = pool.submit(job)
    }

    internal class AnimationJob(private val screen: Screen) : Runnable, Closeable {

        private val animations = ConcurrentHashMap<Identifier, Animation>()
        private val results = ConcurrentHashMap<Identifier, DefaultAnimationResult>()
        private val nextUpdatesForAnimations = HashMap<Identifier, Long>()
        private var running = true

        override fun run() {
            try {

                while (running) {
                    val currTime = System.nanoTime()
                    val currentAnimationKeys = animations.keys
                    currentAnimationKeys.forEach { key ->
                        val animation = animations[key]!!
                        val updateTime = nextUpdatesForAnimations.getOrDefault(key, currTime)
                        if (updateTime <= currTime) {
                            val currentFrame = animation.getCurrentFrame()
                            currentFrame.getLayers().forEach(Consumer<Layer> { screen.removeLayer(it) })
                            animation.fetchNextFrame().map { frame ->
                                frame.getLayers().forEach { layer ->
                                    layer.moveTo(frame.getPosition())
                                    screen.pushLayer(layer)
                                }
                                screen.refresh()
                                Optional.empty<Any>()
                            }
                            if (animation.hasNextFrame()) {
                                nextUpdatesForAnimations[key] = currTime + msToNs(animation.getTick())
                            } else {
                                animations.remove(key)?.let {
                                    results[key]?.setState(FINISHED)
                                }
                                animation.getCurrentFrame().getLayers().forEach {
                                    screen.removeLayer(it)
                                }
                                screen.refresh()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun addAnimation(animation: Animation): AnimationResult {
            val result = DefaultAnimationResult(
                    if (animation.isLoopedIndefinitely()) INFINITE else IN_PROGRESS)
            results[animation.getId()] = result
            animations[animation.getId()] = animation
            return result
        }

        override fun close() {
            running = false
        }

        private fun msToNs(ms: Long): Long {
            return ms * 1000 * 1000
        }
    }
}
