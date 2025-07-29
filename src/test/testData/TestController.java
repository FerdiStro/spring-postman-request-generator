import org.springframework.web.bind.annotation.RequestMapping;

public class TestController {
    @RequestMapping
    public void annotated() {}

    public void plain() {}
}
