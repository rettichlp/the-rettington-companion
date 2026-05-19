package de.rettichlp.therettingtoncompanion.common.services;

import de.rettichlp.therettingtoncompanion.common.gui.widgets.base.AbstractWidget;
import de.rettichlp.therettingtoncompanion.common.gui.widgets.base.Widget;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.entity.EntityLike;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.awt.Color;
import java.util.LinkedHashSet;
import java.util.Objects;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.StreamSupport.stream;
import static net.minecraft.client.font.TextRenderer.TextLayerType.SEE_THROUGH;
import static net.minecraft.client.render.RenderLayers.debugQuads;
import static net.minecraft.client.render.RenderLayers.lines;
import static net.minecraft.util.math.RotationAxis.POSITIVE_Y;
import static org.atteo.classindex.ClassIndex.getAnnotated;

public class RenderService {

    public static final int TEXT_BOX_PADDING = 3;

    @Getter
    private LinkedHashSet<AbstractWidget<?>> widgets = new LinkedHashSet<>();

    public boolean isDebugEnabled() {
        return false;
    }

    public void drawOutline(@NotNull MatrixStack matrices,
                            @NotNull VertexConsumerProvider vertexConsumers,
                            @NotNull EntityLike entity,
                            double expandBoundingBox) {
        Box box = entity.getBoundingBox().expand(expandBoundingBox);
        drawOutline(matrices, vertexConsumers, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, new Color(255, 255, 0, 150));
    }

    public void drawOutline(@NotNull MatrixStack matrices,
                            @NotNull VertexConsumerProvider vertexConsumers,
                            double x1,
                            double y1,
                            double z1,
                            double x2,
                            double y2,
                            double z2,
                            Color color) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        double camX = camera.getCameraPos().x;
        double camY = camera.getCameraPos().y;
        double camZ = camera.getCameraPos().z;

        float minX = (float) (x1 - camX);
        float minY = (float) (y1 - camY);
        float minZ = (float) (z1 - camZ);
        float maxX = (float) (x2 - camX);
        float maxY = (float) (y2 - camY);
        float maxZ = (float) (z2 - camZ);

        VertexConsumer consumer = vertexConsumers.getBuffer(lines());
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        drawLine(consumer, matrix, minX, minY, minZ, maxX, minY, minZ, color);
        drawLine(consumer, matrix, maxX, minY, minZ, maxX, minY, maxZ, color);
        drawLine(consumer, matrix, maxX, minY, maxZ, minX, minY, maxZ, color);
        drawLine(consumer, matrix, minX, minY, maxZ, minX, minY, minZ, color);

        drawLine(consumer, matrix, minX, maxY, minZ, maxX, maxY, minZ, color);
        drawLine(consumer, matrix, maxX, maxY, minZ, maxX, maxY, maxZ, color);
        drawLine(consumer, matrix, maxX, maxY, maxZ, minX, maxY, maxZ, color);
        drawLine(consumer, matrix, minX, maxY, maxZ, minX, maxY, minZ, color);

        drawLine(consumer, matrix, minX, minY, minZ, minX, maxY, minZ, color);
        drawLine(consumer, matrix, maxX, minY, minZ, maxX, maxY, minZ, color);
        drawLine(consumer, matrix, maxX, minY, maxZ, maxX, maxY, maxZ, color);
        drawLine(consumer, matrix, minX, minY, maxZ, minX, maxY, maxZ, color);
    }

    public void drawArea(@NotNull MatrixStack matrices,
                         @NotNull VertexConsumerProvider vertexConsumers,
                         @NotNull Direction direction,
                         float x,
                         float y,
                         float z,
                         @NotNull Color color) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        double camX = camera.getCameraPos().x;
        double camY = camera.getCameraPos().y;
        double camZ = camera.getCameraPos().z;

        float modifiedX = (float) (x - camX);
        float modifiedY = (float) (y - camY);
        float modifiedZ = (float) (z - camZ);

        VertexConsumer consumer = vertexConsumers.getBuffer(debugQuads());
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        Color alphaColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 50);

        switch (direction) {
            case UP -> {
                consumer.vertex(matrix, modifiedX, modifiedY + 1, modifiedZ).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
                consumer.vertex(matrix, modifiedX + 1, modifiedY + 1, modifiedZ).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
                consumer.vertex(matrix, modifiedX + 1, modifiedY + 1, modifiedZ + 1).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
                consumer.vertex(matrix, modifiedX, modifiedY + 1, modifiedZ + 1).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
            }
            case DOWN -> {
                consumer.vertex(matrix, modifiedX, modifiedY, modifiedZ).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
                consumer.vertex(matrix, modifiedX + 1, modifiedY, modifiedZ).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
                consumer.vertex(matrix, modifiedX + 1, modifiedY, modifiedZ + 1).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
                consumer.vertex(matrix, modifiedX, modifiedY, modifiedZ + 1).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
            }
            case NORTH -> {
                consumer.vertex(matrix, modifiedX, modifiedY, modifiedZ).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
                consumer.vertex(matrix, modifiedX + 1, modifiedY, modifiedZ).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
                consumer.vertex(matrix, modifiedX + 1, modifiedY + 1, modifiedZ).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
                consumer.vertex(matrix, modifiedX, modifiedY + 1, modifiedZ).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
            }
            case EAST -> {
                consumer.vertex(matrix, modifiedX + 1, modifiedY, modifiedZ).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
                consumer.vertex(matrix, modifiedX + 1, modifiedY + 1, modifiedZ).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
                consumer.vertex(matrix, modifiedX + 1, modifiedY + 1, modifiedZ + 1).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
                consumer.vertex(matrix, modifiedX + 1, modifiedY, modifiedZ + 1).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
            }
            case SOUTH -> {
                consumer.vertex(matrix, modifiedX, modifiedY, modifiedZ + 1).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
                consumer.vertex(matrix, modifiedX + 1, modifiedY, modifiedZ + 1).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
                consumer.vertex(matrix, modifiedX + 1, modifiedY + 1, modifiedZ + 1).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
                consumer.vertex(matrix, modifiedX, modifiedY + 1, modifiedZ + 1).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
            }
            case WEST -> {
                consumer.vertex(matrix, modifiedX, modifiedY, modifiedZ).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
                consumer.vertex(matrix, modifiedX, modifiedY + 1, modifiedZ).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
                consumer.vertex(matrix, modifiedX, modifiedY + 1, modifiedZ + 1).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
                consumer.vertex(matrix, modifiedX, modifiedY, modifiedZ + 1).color(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), alphaColor.getAlpha()).normal(0, 1, 0);
            }
        }
    }

    public void drawLine(@NotNull VertexConsumer consumer,
                         Matrix4f matrix,
                         float x1,
                         float y1,
                         float z1,
                         float x2,
                         float y2,
                         float z2) {
        Color color = new Color(255, 255, 0, 150);
        drawLine(consumer, matrix, x1, y1, z1, x2, y2, z2, color);
    }

    public void drawLine(@NotNull VertexConsumer consumer,
                         Matrix4f matrix,
                         float x1,
                         float y1,
                         float z1,
                         float x2,
                         float y2,
                         float z2,
                         @NotNull Color color) {
        consumer.vertex(matrix, x1, y1, z1).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).normal(0, 1, 0);
        consumer.vertex(matrix, x2, y2, z2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).normal(0, 1, 0);
    }

    public void renderTextAboveEntity(@NotNull MatrixStack matrices,
                                      VertexConsumerProvider vertexConsumers,
                                      @NotNull Entity entity,
                                      Text text) {
        renderTextAboveEntity(matrices, vertexConsumers, entity, text, 0.025F);
    }

    public void renderTextAboveEntity(@NotNull MatrixStack matrices,
                                      VertexConsumerProvider vertexConsumers,
                                      @NotNull Entity entity,
                                      Text text,
                                      float scale) {
        renderTextAt(matrices, vertexConsumers, entity.getX(), entity.getY() + 1.35, entity.getZ(), text, scale);
    }

    public void renderTextAt(@NotNull MatrixStack matrices,
                             VertexConsumerProvider vertexConsumers,
                             double x,
                             double y,
                             double z,
                             Text text,
                             float scale) {
        // save the current matrix state
        matrices.push();

        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        double camX = camera.getCameraPos().x;
        double camY = camera.getCameraPos().y;
        double camZ = camera.getCameraPos().z;

        matrices.translate(x - camX, y - camY, z - camZ);

        // make the text face the camera
        matrices.multiply(camera.getRotation());
        matrices.multiply(POSITIVE_Y.rotationDegrees(180.0F));

        // scale the text down so it's not too big
        matrices.scale(-scale, -scale, scale);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        // calculate the width of the text to center it
        float textWidth = -textRenderer.getWidth(text) / 2.0F;

        // render the text
        textRenderer.draw(text, textWidth, 0.0F, 0xFFFFFFFF, false, matrices.peek().getPositionMatrix(), vertexConsumers, SEE_THROUGH, 0x55000000, 0xF000F0);

        // restore the previous matrix state
        matrices.pop();
    }

    public Color getSecondaryColor(@NotNull Color color) {
        return new Color(color.getRed() / 2, color.getGreen() / 2, color.getBlue() / 2, 100);
    }

    public void initializeWidgets() {
        this.widgets = stream(getAnnotated(Widget.class).spliterator(), false)
                .map(widgetClass -> {
                    try {
                        return (AbstractWidget<?>) widgetClass.getConstructor().newInstance();
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .peek(AbstractWidget::init)
                .sorted(comparing(AbstractWidget::getRegistryName))
                .collect(toCollection(LinkedHashSet::new));
    }
}
