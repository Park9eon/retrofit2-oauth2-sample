package sexy.park9eon.example;

import com.google.gson.GsonBuilder;
import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceBuilder {

    // Server url을 작성해주세요.
    public static final String API_BASE_URL = "@{SERVER_URL}";

    public static OauthToken oauthToken; // 로딩이나 어플리케이션 단에서 설정해준다.

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL) // Server Url설정
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 서비스단에 자유롭다.
                    .addConverterFactory(GsonConverterFactory.create()); // Json to Object

    // oauth token을 가져오는 가벼운 Service를 만든다.
    public static <S> S basicService(Class<S> serviceClass) {
        return builder.client(
                new OkHttpClient.Builder().addInterceptor(chain -> chain.proceed(requestBuild(chain.request(), OauthToken.basic()).build())).build()).build().create(serviceClass);
    }

    // oauth access token을 이용한 service를 만든다.
    public static <S> S createService(Class<S> serviceClass) {
        return builder.client(
                // why x set : o add ??????
            new OkHttpClient.Builder().addInterceptor(chain -> {
                Request original = chain.request();
                Response response = chain.proceed(requestBuild(original, oauthToken.bearer()).build());
                if (response.code() == 401) { // 토큰이 만료되었다.
                    // 이렇게 안하면 함수형처럼 response를 설정할 수 없어서 로우하게 만듬.
                    Request newRequest = requestBuild(
                            new Request.Builder()
                                    .url(String.format("%s/oauth/token?grant_type=refresh_token&refresh_token=%s", API_BASE_URL, oauthToken.getRefreshToken()))
                                    .method("POST", RequestBody.create(MediaType.parse("application/json"), new byte[0]))
                                    .build(), oauthToken.bearer()).build(); // create simple requestBuilder

                    Response newResponse = chain.proceed(newRequest); // 동기, 순차적 작동.
                    // 성공했을 경우에만 토큰을 새로 저장한다.
                    if (newResponse.code() == 200) {
                        oauthToken = new GsonBuilder().create().fromJson(newResponse.body().string(), OauthToken.class);
                        response = chain.proceed(requestBuild(original, oauthToken.bearer()).build());
                    }

                    // else {} // 아니면 그냥 흘려보낸다.
                }

                return response;
            }).build()
        ).build()
                .create(serviceClass);
    }

    // Header를 넣어준다. / header insert in retrofit request builder
    private static Request.Builder requestBuild(Request request, String auth) {
        return request.newBuilder()
                .header("Accept", "application/json")
                .header("Authorization", auth)
                .method(request.method(), request.body());
    }
}
