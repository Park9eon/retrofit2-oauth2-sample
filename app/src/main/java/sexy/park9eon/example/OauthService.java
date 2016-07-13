package sexy.park9eon.example;

import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface OauthService {

    @POST("oauth/token")
    Observable<OauthToken> getAccessToken(@Query("grant_type") String grantType, @Query("username") String username, @Query("password") String password);
}
