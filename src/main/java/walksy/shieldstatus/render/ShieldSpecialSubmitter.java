package walksy.shieldstatus.render;

import com.mojang.blaze3d.vertex.PoseStack;
import main.walksy.lib.core.config.local.options.type.WalksyLibColor;
import net.minecraft.client.model.object.equipment.ShieldModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.texture.UvMapping;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import walksy.shieldstatus.ShieldStatus;

import java.util.Objects;


public class ShieldSpecialSubmitter {

    private final ShieldModel model;
    private final SpriteGetter sprites;
    private final ShieldSpecialConfigState state;

    public ShieldSpecialSubmitter(final ShieldModel model, final SpriteGetter sprites) {
        this.model = model;
        this.sprites = sprites;
        this.state = new ShieldSpecialConfigState();
    }

    public void submit(final ItemDisplayContext context, final DataComponentMap components, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final boolean hasFoil) {
        ShieldStatus.checkDisplayContext(context);
        this.state.extractConfigState();
        final WalksyLibColor color = this.state.tintedColor;
        final Identifier sheet = this.state.shieldSheet;
        final BannerPatternLayers patterns = components != null ? components.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY) : BannerPatternLayers.EMPTY;
        final DyeColor baseColor = components != null ? components.get(DataComponents.BASE_COLOR) : null;
        final RenderType renderType = hasFoil ? RenderTypes.itemTranslucentGlint(sheet) : RenderTypes.entityTranslucent(sheet);
        submitNodeCollector.submitModelPart(this.model.handle(), poseStack, renderType, lightCoords, overlayCoords, IDENTITY_UV, color.getRGB());
        submitNodeCollector.submitModelPart(this.model.plate(), poseStack, renderType, lightCoords, overlayCoords, IDENTITY_UV, color.getRGB());
        if (!patterns.layers().isEmpty() || baseColor != null) {
            this.submitPatterns(poseStack, submitNodeCollector.order(1), lightCoords, overlayCoords, Objects.requireNonNullElse(baseColor, DyeColor.WHITE), patterns);
        }
    }

    private static final UvMapping IDENTITY_UV = new UvMapping() {
        @Override
        public float getU(float u) { return u; }

        @Override
        public float getV(float v) { return v; }
    };

    private void submitPatterns(final PoseStack poseStack, final OrderedSubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final DyeColor baseColor, final BannerPatternLayers patterns) {
        this.submitPatternLayer(Sheets.SHIELD_PATTERN_BASE, submitNodeCollector, poseStack, lightCoords, overlayCoords, baseColor);
        for (int maskIndex = 0; maskIndex < 16 && maskIndex < patterns.layers().size(); ++maskIndex) {
            final BannerPatternLayers.Layer layer = patterns.layers().get(maskIndex);
            this.submitPatternLayer(Sheets.getShieldSprite(layer.pattern()), submitNodeCollector, poseStack, lightCoords, overlayCoords, layer.color());
        }
    }

    private void submitPatternLayer(final SpriteId sprite, final OrderedSubmitNodeCollector submitNodeCollector, final PoseStack poseStack, final int lightCoords, final int overlayCoords, final DyeColor color) {
        submitNodeCollector.submitModelPart(this.model.plate(), poseStack, sprite.renderType(RenderTypes::entityTranslucent), lightCoords, overlayCoords, this.sprites.get(sprite), color.getTextureDiffuseColor());
    }

}