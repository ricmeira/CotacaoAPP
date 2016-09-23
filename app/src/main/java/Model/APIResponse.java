package Model;

import java.util.List;

/**
 * Created by ricardo on 9/22/2016.
 */

public class APIResponse {
    private String status;
    private Currency valores;

    public Currency getValores() {
        return valores;
    }

    public void setValores(Currency valores) {
        this.valores = valores;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
