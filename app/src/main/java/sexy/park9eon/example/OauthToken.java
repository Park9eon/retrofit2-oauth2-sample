package sexy.park9eon.example;

import android.util.Base64;
import com.google.gson.annotations.SerializedName;

public class OauthToken {

    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String TOKEN_TYPE = "token_type";
    public static final String AUTHORIZATION = Base64.encodeToString("@{TODO}".getBytes(), Base64.NO_WRAP);

    @SerializedName(ACCESS_TOKEN)
    private String tokenType;
    @SerializedName(REFRESH_TOKEN)
    private String refreshToken;
    @SerializedName(TOKEN_TYPE)
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String bearer() {
        return String.format("Bearer %s", this.getAccessToken());
    }

    public static String basic() {
        return String.format("Basic %s", AUTHORIZATION);
    }
}
