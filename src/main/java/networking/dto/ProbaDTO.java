package networking.dto;

import java.io.Serializable;

public class ProbaDTO implements Serializable {
    private String distanta;
    private String stil;
    private String id;

    public ProbaDTO(String distanta, String stil, String id) {
        this.distanta = distanta;
        this.stil = stil;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProbaDTO(String distanta, String stil) {
        this.distanta = distanta;
        this.stil = stil;
    }

    public String getDistanta() {
        return distanta;
    }

    public void setDistanta(String distanta) {
        this.distanta = distanta;
    }

    public String getStil() {
        return stil;
    }

    public void setStil(String stil) {
        this.stil = stil;
    }
}
