package de.rettichlp.therettingtoncompanion.common.utils;

import com.google.gson.JsonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.google.gson.JsonParser.parseString;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.LOGGER;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_NAME;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.notificationService;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static java.awt.Color.WHITE;
import static java.net.URI.create;
import static java.net.http.HttpClient.newHttpClient;
import static java.net.http.HttpRequest.BodyPublishers.ofByteArray;
import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.nio.file.Files.probeContentType;
import static java.nio.file.Files.readAllBytes;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static net.minecraft.client.Screenshot.SCREENSHOT_DIR;
import static net.minecraft.client.Screenshot.takeScreenshot;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.util.Util.getFilenameFormattedDateTime;

public class ScreenshotUtils {

    private static final String IMGUR_API_URL = "https://api.imgur.com/3/image";
    private static final String IMGUR_CLIENT_ID = "f0f10087565b2ee";
    private static final String IMGUR_SCREENSHOT_DIR = "uploaded_to_imgur";

    public static @NonNull CompletableFuture<File> takeImgurScreenshot() {
        CompletableFuture<File> completableFuture = new CompletableFuture<>();
        Minecraft client = Minecraft.getInstance();

        File screenshotsDirectory = client.gameDirectory.toPath().resolve(SCREENSHOT_DIR).resolve(IMGUR_SCREENSHOT_DIR).toFile();
        screenshotsDirectory.mkdirs();

        takeScreenshot(client.getMainRenderTarget(), nativeImage -> {
            File screenshotFile = getFile(screenshotsDirectory);

            Util.ioPool().execute(() -> {
                try {
                    nativeImage.writeToFile(screenshotFile);
                    notificationService.sendNotification(translatable("screenshot.success", screenshotFile.getName()), WHITE, 5000);
                    completableFuture.complete(screenshotFile);
                } catch (Exception e) {
                    LOGGER.warn("Couldn't save screenshot", e);
                    notificationService.sendWarningNotification(translatable("screenshot.failure", e.getMessage()));
                    completableFuture.completeExceptionally(e);
                }
            });
        });

        return completableFuture;
    }

    public static @NonNull CompletableFuture<String> uploadImageToImgur(@NonNull Path pathToImage) {
        return supplyAsync(() -> {
            try (HttpClient client = newHttpClient()) {
                String boundary = "JavaHttpClientBoundary" + randomUUID();

                Map<String, String> fields = Map.of(
                        "type", "file",
                        "title", pathToImage.getFileName().toString(),
                        "description", "Uploaded by " + player.getName().getString() + " using the Minecraft mod '" + MOD_NAME + "'"
                );

                byte[] body = createMultipartBody(pathToImage, fields, boundary);
                HttpRequest request = newBuilder()
                        .uri(create(IMGUR_API_URL))
                        .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                        .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                        .POST(ofByteArray(body))
                        .build();

                HttpResponse<String> response = client.send(request, ofString());
                JsonElement jsonElement = parseString(response.body());
                return jsonElement.getAsJsonObject().get("data").getAsJsonObject().get("link").getAsString();
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static byte @NonNull [] createMultipartBody(Path file, @NonNull Map<String, String> fields, String boundary) throws
                                                                                                                         IOException {
        var byteBuilder = new java.io.ByteArrayOutputStream();
        String lineSeparator = "\r\n";

        // Add text fields
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            byteBuilder.write(("--" + boundary + lineSeparator).getBytes());
            byteBuilder.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + lineSeparator).getBytes());
            byteBuilder.write(lineSeparator.getBytes());
            byteBuilder.write((entry.getValue() + lineSeparator).getBytes());
        }

        // Add file field
        byteBuilder.write(("--" + boundary + lineSeparator).getBytes());
        byteBuilder.write(("Content-Disposition: form-data; name=\"image\"; filename=\"" + file.getFileName() + "\"" + lineSeparator).getBytes());
        byteBuilder.write(("Content-Type: " + probeContentType(file) + lineSeparator).getBytes());
        byteBuilder.write(lineSeparator.getBytes());
        byteBuilder.write(readAllBytes(file));
        byteBuilder.write(lineSeparator.getBytes());

        // End of multipart
        byteBuilder.write(("--" + boundary + "--" + lineSeparator).getBytes());

        return byteBuilder.toByteArray();
    }

    private static File getFile(File picDir) {
        String name = getFilenameFormattedDateTime();
        int count = 1;
        File file;
        while ((file = new File(picDir, name + (count == 1 ? "" : "_" + count) + ".png")).exists()) {
            ++count;
        }
        return file;
    }
}
