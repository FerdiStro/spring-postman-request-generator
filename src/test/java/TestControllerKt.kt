import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

class TestControllerKt {

    @RequestMapping(path = ["/info"], method = [RequestMethod.POST])
    fun annotated(
        a: String,
    ) {
    }

    fun plain() {}
}
