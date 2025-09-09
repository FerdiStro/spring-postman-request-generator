package com.github.ferdistro.springpostmanrequestgenerator.settings

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlRootElement
import kotlin.concurrent.thread


@Service
@State(
    name = "RequestGeneratorSettings", storages = [Storage("spring-postman-request-generator.xml")]
)
class RequestGeneratorSettings : PersistentStateComponent<RequestGeneratorSettings.State> {

    @XmlAccessorType(XmlAccessType.FIELD)
    data class ContextSetting(
        @XmlElement var name: String = "",
        @XmlElement var value: String = "",
        @XmlElement var useEnv: Boolean = false,
        @XmlElement var extra: Boolean = false
    )

    @XmlRootElement(name = "State")
    @XmlAccessorType(XmlAccessType.FIELD)
    data class State(
        @XmlElement var apiActive: Boolean = false,
        @XmlElement var protocol: ContextSetting = ContextSetting("protocol", "http://"),
        @XmlElement var serverUrl: ContextSetting = ContextSetting("serverUrl", "localhost:8080"),
        @XmlElement var context: ContextSetting = ContextSetting("context", "/api")
    )

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        XmlSerializerUtil.copyBean(state, myState)
    }

    companion object {
        fun getInstance(): RequestGeneratorSettings =
            ApplicationManager.getApplication().getService(RequestGeneratorSettings::class.java)

        private val CREDENTIAL_KEY = "com.github.ferdistro.springpostmanrequestgenerator.apiToken"

        fun saveApiToken(token: String) {
            val credentialAttributes = CredentialAttributes(CREDENTIAL_KEY)
            val credentials = Credentials("user", token)
            thread {
                PasswordSafe.instance.set(credentialAttributes, credentials)


            }
        }

        fun loadApiToken(): String? {
            val credentialAttributes = CredentialAttributes(CREDENTIAL_KEY)
            return PasswordSafe.instance.get(credentialAttributes)?.password?.toString()
        }
    }


}