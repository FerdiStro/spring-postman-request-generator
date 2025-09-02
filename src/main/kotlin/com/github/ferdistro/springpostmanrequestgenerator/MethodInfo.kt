package com.github.ferdistro.springpostmanrequestgenerator

import com.intellij.openapi.project.Project

data class MethodInfo(
    val name: String,
    val project: Project,
    val parameters: List<ParameterInfo>,
    val annotations: List<AnnotationInfo>,
)

