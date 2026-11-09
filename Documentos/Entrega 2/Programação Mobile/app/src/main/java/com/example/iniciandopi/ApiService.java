package com.example.iniciandopi;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/v1/token?grant_type=password")
    Call<LoginResponse> fazerLogin(
            @Header("apikey") String apiKey,
            @Header("Content-Type") String contentType,
            @Body LoginRequest loginRequest
    );

    @GET("rest/v1/courses")
    Call<List<CourseModel>> obterCursos(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token
    );

    @GET("rest/v1/certificates")
    Call<List<CertificateModel>> obterCertificados(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("user_id") String userId
    );

    @POST("rest/v1/test_results")
    Call<Void> salvarResultadoTeste(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Body TestResultRequest testResult
    );

    @POST("functions/v1/ai-assistant")
    Call<ChatResponse> enviarMensagemChat(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Body ChatRequest chatRequest
    );
}
