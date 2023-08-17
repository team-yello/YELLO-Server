package com.yello.server.global;

import com.yello.server.global.common.util.ConstantUtil;
import com.yello.server.global.common.util.RestUtil;
import io.swagger.v3.oas.annotations.Hidden;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequiredArgsConstructor
public class HealthCheckController {

    @GetMapping("/")
    public String healthCheck() {
        return "Yell:o world!";
    }

    @GetMapping("/check1")
    public String check1() {
        return RestUtil.getSubscribeCheck(
            "omfefngneagfjbbiilkfklma.AO-J1OzpoLwvaghTYTf3fmdWnGshzvmO7tWgLS1csfUH4t9jALlrkS4mdO0xnUaKLiFg8GDwB65WjY_P3Kgl7KMs0o5gP7aRPA",
            "ya29.a0AfB_byCNJouXhS9xk2xBRG6GOFecUVBpVI0-7ro1pziHD7LhmGGinDm4nOnHbuKyjN_fJnlPRsSv7AzchF4dy4QxF0s9_KUQtKBaaagrpcoscBd6WaTgKxc28V6gvbzHK_NkR_0hVu2kXlRqdBUgU-l4u9JKkSnZYmZqXHvb0gaCgYKAaMSARISFQHsvYlsIAie_CKHJcpEqFfWmG6DjA0177"
        ).toString();
    }

    @GetMapping("/check2")
    public String check2() {
        return RestUtil.getTicketCheck(
            ConstantUtil.GOOGLE_FIVE_TICKET_ID,
            "iokechedcfehajgklkmhooil.AO-J1Owu16cgCtqfJeVim7uG6WDeJ7hkmmeq7fbzU05PdwzZx9lMK473z1BJOGvrVynngJHhGTQDqLCPFCze9LvpDV0TmT21PQ",
            "ya29.a0AfB_byCNJouXhS9xk2xBRG6GOFecUVBpVI0-7ro1pziHD7LhmGGinDm4nOnHbuKyjN_fJnlPRsSv7AzchF4dy4QxF0s9_KUQtKBaaagrpcoscBd6WaTgKxc28V6gvbzHK_NkR_0hVu2kXlRqdBUgU-l4u9JKkSnZYmZqXHvb0gaCgYKAaMSARISFQHsvYlsIAie_CKHJcpEqFfWmG6DjA0177"
        ).toString();
    }

    @GetMapping("/check3")
    public String check3() throws IOException {
        return RestUtil.postGoogleTokenReissue(
            "1//0eyLATSiDsTNjCgYIARAAGA4SNwF-L9Ir1pNWYBG1lS2i0Bl6kapR9_NQzhwuXd1NpYRZBuFKVMHbXlqHZyo1kybr_sPw0ijeO20"
        ).toString();
    }
}
