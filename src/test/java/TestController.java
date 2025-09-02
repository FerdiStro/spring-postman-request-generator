import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class TestController {

    @RequestMapping(path = {"/info"}, method = {RequestMethod.POST})
    public void annotated() {
    }

    public void plain() {
    }
}
