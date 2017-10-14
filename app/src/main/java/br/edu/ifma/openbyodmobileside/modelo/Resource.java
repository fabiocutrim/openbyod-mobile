package br.edu.ifma.openbyodmobileside.modelo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ARTLAN-00 on 20/08/2017.
 */

public class Resource {
    @SerializedName("resource")
    private String resource;
    @SerializedName("token")
    private String token;

    public Resource(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
