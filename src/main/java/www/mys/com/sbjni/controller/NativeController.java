package www.mys.com.sbjni.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import www.mys.com.sbjni.utils.NativeUtils;

@RestController
public class NativeController {

    @RequestMapping("/test")
    public String test() {
        return NativeUtils.getJniStr();
    }

}
