package com.github.ferdistro.springpostmanrequestgenerator

import com.github.ferdistro.springpostmanrequestgenerator.services.PermanentCache
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiTypeElement
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import java.awt.event.MouseEvent
import javax.swing.Icon

class JsonGeneratorLineMarkerProvider : LineMarkerProvider {
    val icon: Icon = IconLoader.getIcon("/META-INF/icon.svg", JsonGeneratorLineMarkerProvider::class.java)

    val url: String = "{{PROTOCOL}}{{SERVER}}"
    val urlPath: String = "{{APP_CONTEXT}}}"


    @Nullable
    override fun getLineMarkerInfo(@NotNull element: PsiElement): LineMarkerInfo<*>? {
        if (element is PsiMethod) {
            if (!element.hasAnnotation("org.springframework.web.bind.annotation.RequestMapping")) {
                return null
            }
            val modifiers = element.modifierList

            val anchor = modifiers.nextSibling ?: element

            val finalAnchor = if (anchor is PsiTypeElement) {
                anchor
            } else {
                element.nameIdentifier ?: element
            }

            return LineMarkerInfo(
                finalAnchor,
                finalAnchor.textRange,
                icon,
                { "Generate JSON" },
                { mouseEvent: MouseEvent?, elt -> generateJson(element) },
                GutterIconRenderer.Alignment.RIGHT,
                { "Generate JSON" })
        }
        return null
    }


    private fun generateJson(method: PsiMethod) {
        val stringBuilder: StringBuilder = StringBuilder()

        val item = Item(
            name = "", request = Request(
                method = "", header = emptyList(), url = URL(
                    raw = "", host = emptyList(), path = emptyList(), query = emptyList()
                )
            ), response = emptyList()
        )

        val request = item.request

        for (param in method.parameterList.parameters) {

            stringBuilder.append("Parameter: ${param.name}; Type: ${param.type.presentableText}")

            request.url.query = request.url.query + QueryItem(key = param.name, value = "")

            val annotations = param.modifierList?.annotations ?: emptyArray()
            for (ann in annotations) {
                val annQualifiedName = ann.qualifiedName ?: "Unbekannt"
                stringBuilder.append("  Annotation: $annQualifiedName")

                val attributes = ann.parameterList.attributes
                for (attr in attributes) {
                    val name = attr.name ?: "value"
                    val value = attr.value?.text ?: "null"
                    stringBuilder.append("    Attribute: $name = $value")
                }
            }
        }


        val annotation = method.modifierList.findAnnotation("org.springframework.web.bind.annotation.RequestMapping")


        if (annotation != null) {
            val attributes = annotation.parameterList.attributes
            for (attr in attributes) {
                val name = attr.name ?: "value"
                val value = attr.value?.text ?: "null"


                if (name == "value") {
                    item.name = value.split("/").last().replace("\"", "")
                    request.url = URL(
                        raw = (this.url + "/" + this.urlPath + value).replace("\"", ""),
                        host = listOf(this.url),
                        path = (listOf(this.urlPath) + value.replace("\"", "").split("/").drop(1)),
                        query = request.url.query
                    )
                }

                if (name == "method") {
                    request.method = value.split(".")[1].replace("\"", "")
                }


                stringBuilder.append("\"$name\" : \"$value\",")
            }
        }


        val project = method.project
        val service = project.getService(PermanentCache::class.java)


        service.addRequest(item)

    }
}

