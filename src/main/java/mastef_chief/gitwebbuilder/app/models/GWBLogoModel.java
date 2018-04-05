package mastef_chief.gitwebbuilder.app.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * GWBLogoModel - Mastef_Chief
 * Created using Tabula 7.0.0
 */
public class GWBLogoModel extends ModelBase {
    public ModelRenderer logoblock;

    public GWBLogoModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.logoblock = new ModelRenderer(this, 0, 0);
        this.logoblock.setRotationPoint(-7.0F, 9.0F, -7.0F);
        this.logoblock.addBox(0.0F, 0.0F, 0.0F, 14, 14, 14, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.logoblock.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
