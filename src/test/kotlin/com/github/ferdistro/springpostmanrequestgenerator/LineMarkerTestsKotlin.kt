package com.github.ferdistro.springpostmanrequestgenerator

import com.github.ferdistro.springpostmanrequestgenerator.services.PostmanRequestGenerator
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.awt.event.MouseEvent

@TestDataPath($$"$CONTENT_ROOT/src/test/testData")
class LineMarkerTestsKotlin : BasePlatformTestCase() {

    override fun getTestDataPath() = "src/test/testData/"

    val generator = PostmanRequestGenerator()
    val provider = JsonGeneratorLineMarkerProvider(generator)

    fun testMouseEvent() {
        myFixture.configureByFiles(
            "TestControllerKt.kt",
            "org/springframework/web/bind/annotation/RequestMapping.java"
        )

        val file = myFixture.file
        val annotatedMethod = PsiTreeUtil.findChildrenOfType(file, PsiMethod::class.java)
            .first()

        assertNotNull("annotatedMethod should be found", annotatedMethod)

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
//        handler.navigate(fakeEvent, element)
    }


    fun testLineMarkerInfo() {
        myFixture.configureByFiles(
            "TestControllerKt.kt",
            "org/springframework/web/bind/annotation/RequestMapping.java"
        )

        val file = myFixture.file
        val methods = PsiTreeUtil.findChildrenOfType(file, PsiMethod::class.java).toList()

        val annotatedMethod = methods.first {
            it.hasAnnotation("org.springframework.web.bind.annotation.RequestMapping")
        }

        val markerInfo = provider.getLineMarkerInfo(annotatedMethod)
        assertNotNull("Line Marker shout be created", markerInfo)
        assertEquals("Generate JSON", markerInfo?.lineMarkerTooltip)

        val normalMethod = methods.first {
            !it.hasAnnotation("org.springframework.web.bind.annotation.RequestMapping")
        }

        val normalMarkerInfo = provider.getLineMarkerInfo(normalMethod)
        assertNull("LineMarker shouldn't be created", normalMarkerInfo)
    }
}
