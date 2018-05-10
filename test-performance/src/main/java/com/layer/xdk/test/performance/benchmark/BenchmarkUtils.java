package com.layer.xdk.test.performance.benchmark;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.test.InstrumentationRegistry;

import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerException;
import com.layer.sdk.listeners.LayerAuthenticationListener;
import com.layer.xdk.test.performance.BuildConfig;
import com.layer.xdk.ui.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class BenchmarkUtils {
    public static LayerClient createLayerClientForBenchmark() {
        LayerClient.Options options = new LayerClient.Options()
                .historicSyncPolicy(LayerClient.Options.HistoricSyncPolicy.FROM_LAST_MESSAGE)
                .setTelemetryEnabled(false)
                .customEndpoint(null,
                        "https://certs.stage1.lyr8.net/certificates",
                        "https://sync.stage1.lyr8.net",
                        "https://sync.stage1.lyr8.net/");

        LayerClient layerClient = LayerClient.newInstance(InstrumentationRegistry.getTargetContext(),
                BuildConfig.TEST_BENCHMARK_APP_ID, options);
        LayerClient.setLoggingEnabled(InstrumentationRegistry.getTargetContext(), true);
        layerClient.registerAuthenticationListener(new BenchmarkAuthenticationListener());
        return layerClient;
    }

    private static class BenchmarkAuthenticationListener implements LayerAuthenticationListener {
        @Override
        public void onAuthenticated(LayerClient layerClient, String s) {

        }

        @Override
        public void onDeauthenticated(LayerClient layerClient) {

        }

        @SuppressLint("StaticFieldLeak")
        @Override
        public void onAuthenticationChallenge(final LayerClient layerClient, final String nonce) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    respondToChallenge(layerClient, nonce);
                    return null;
                }
            }.execute();
        }

        @Override
        public void onAuthenticationError(LayerClient layerClient, LayerException e) {

        }

        private void respondToChallenge(LayerClient layerClient, String nonce) {
            try {
                // Post request
                HttpURLConnection connection = (HttpURLConnection) new URL(BuildConfig.TEST_BENCHMARK_IDENTITY_PROVIDER_URL).openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("X_LAYER_APP_ID", BuildConfig.TEST_BENCHMARK_APP_ID);
                connection.setRequestProperty("X_AUTH_EMAIL", BuildConfig.TEST_BENCHMARK_EMAIL);

                // Credentials
                JSONObject rootObject = new JSONObject();
                JSONObject userObject = new JSONObject();
                rootObject.put("user", userObject);
                userObject.put("email", BuildConfig.TEST_BENCHMARK_EMAIL);
                userObject.put("password", BuildConfig.TEST_BENCHMARK_PASSWORD);
                rootObject.put("nonce", nonce);

                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                OutputStream os = connection.getOutputStream();
                os.write(rootObject.toString().getBytes("UTF-8"));
                os.close();

                // Handle failure
                int statusCode = connection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK && statusCode != HttpURLConnection.HTTP_CREATED) {
                    String error = String.format(Locale.getDefault(), "Got status %d when requesting authentication for '%s' with nonce '%s' from '%s'",
                            statusCode, BuildConfig.TEST_BENCHMARK_EMAIL, nonce, BuildConfig.TEST_BENCHMARK_IDENTITY_PROVIDER_URL);
                    if (Log.isLoggable(Log.ERROR)) Log.e(error);
                    return;
                }

                // Parse response
                InputStream in = new BufferedInputStream(connection.getInputStream());
                String result = streamToString(in);
                in.close();
                connection.disconnect();
                JSONObject json = new JSONObject(result);
                if (json.has("error")) {
                    String error = json.getString("error");
                    if (Log.isLoggable(Log.ERROR)) Log.e(error);
                    return;
                }

                // Answer authentication challenge.
                String identityToken = json.optString("layer_identity_token", null);
                if (Log.isLoggable(Log.VERBOSE)) Log.v("Got identity token: " + identityToken);
                layerClient.answerAuthenticationChallenge(identityToken);
            } catch (Exception e) {
                String error = "Error when authenticating with provider: " + e.getMessage();
                if (Log.isLoggable(Log.ERROR)) Log.e(error, e);
            }
        }

        private String streamToString(InputStream stream) throws IOException {
            int n = 0;
            char[] buffer = new char[1024 * 4];
            InputStreamReader reader = new InputStreamReader(stream, "UTF8");
            StringWriter writer = new StringWriter();
            while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
            return writer.toString();
        }
    }
}
