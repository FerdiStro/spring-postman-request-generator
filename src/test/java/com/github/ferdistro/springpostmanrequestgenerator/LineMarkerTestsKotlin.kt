package com.github.ferdistro.springpostmanrequestgenerator

import com.github.ferdistro.springpostmanrequestgenerator.line.KotlinLineMarkerProvider
import com.github.ferdistro.springpostmanrequestgenerator.services.PostmanRequestGenerator
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.psi.PsiElement
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.kotlin.psi.KtFile
import java.awt.event.MouseEvent

@TestDataPath($$"$CONTENT_ROOT/src/test/java")
class LineMarkerTestsKotlin : BasePlatformTestCase() {

    override fun getTestDataPath() = "src/test/java/"

    val generator = PostmanRequestGenerator()
    val provider = KotlinLineMarkerProvider(generator)

    fun testFile() {
        myFixture.configureByFiles(
            "TestControllerKt.kt",
            "org/springframework/web/bind/annotation/RequestMapping.java"
        )

        val file = myFixture.file

        check(file is KtFile)
        file.classes.forEach { claz ->
            println("--------------------------")
            println("claz ${claz.name}:")
            claz.methods.forEach { method ->
                println("------")
                println(method.name)
                println(method.annotations.map { it.resolveAnnotationType()?.qualifiedName })
            }
        }
        println(file)
    }


    fun testMouseEvent() {
        myFixture.configureByFiles(
            "TestControllerKt.kt",
            "org/springframework/web/bind/annotation/RequestMapping.java"
        )

        val file = myFixture.file
        check(file is KtFile)
        val classes = file.classes
        val methods = classes.map { c -> c.methods.filter { m -> m.isConstructor.not() }.toList() }.flatten()

        val annotatedMethod = methods.single { method ->
            method.annotations.any { it.resolveAnnotationType()?.qualifiedName == "org.springframework.web.bind.annotation.RequestMapping" }
        }

        val markerInfo = provider.getLineMarkerInfo(annotatedMethod)
        assertNotNull("MarkerInfo should not be null", markerInfo)

        val element = markerInfo?.element ?: error("markerInfo.element is null")

        val fakeEvent = MouseEvent(
            myFixture.editor.contentComponent,
            MouseEvent.MOUSE_CLICKED,
            System.currentTimeMillis(),
            0,
            0,
            0,
            1,
            false
        )

        val handler = markerInfo.navigationHandler as GutterIconNavigationHandler<PsiElement>
        handler.navigate(fakeEvent, element)
    }


    fun testLineMarkerInfo() {
        myFixture.configureByFiles(
            "TestControllerKt.kt",
            "org/springframework/web/bind/annotation/RequestMapping.java"
        )

        val file = myFixture.file
        check(file is KtFile)
        val classes = file.classes
        val methods = classes.map { c -> c.methods.filter { m -> m.isConstructor.not() }.toList() }.flatten()

        val annotatedMethod = methods.single { method ->
            method.annotations.any { it.resolveAnnotationType()?.qualifiedName == "org.springframework.web.bind.annotation.RequestMapping" }
        }

        val markerInfo = provider.getLineMarkerInfo(annotatedMethod)
        assertNotNull("Line Marker should be created", markerInfo)
        assertEquals("Generate postman request", markerInfo?.lineMarkerTooltip)

        val normalMethods = methods.filter { method ->
            method.annotations.none { it.resolveAnnotationType()?.qualifiedName == "org.springframework.web.bind.annotation.RequestMapping" }
        }
        check(normalMethods.size == 1)
        val normalMethod = normalMethods.single()

        val normalMarkerInfo = provider.getLineMarkerInfo(normalMethod)
        assertNull("LineMarker shouldn't be created", normalMarkerInfo)
    }
}
