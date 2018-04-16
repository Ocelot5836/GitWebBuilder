package mastef_chief.gitwebbuilder.app.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * GWBLogoModel - Mastef_Chief
 * Created using Tabula 7.0.0
 */
public class GWBLogoModel extends ModelBase {
    public ModelRenderer baseblock;
    public ModelRenderer Bc1;
    public ModelRenderer Bd1;
    public ModelRenderer Ba1;
    public ModelRenderer Bb1;
    public ModelRenderer Ba2;
    public ModelRenderer Ba3;
    public ModelRenderer Ba4;
    public ModelRenderer Ba5;
    public ModelRenderer Ba6;
    public ModelRenderer Bb2;
    public ModelRenderer Bb3;
    public ModelRenderer Bb4;
    public ModelRenderer Bb5;
    public ModelRenderer Bb6;
    public ModelRenderer Bc2;
    public ModelRenderer Bc3;
    public ModelRenderer Bc4;
    public ModelRenderer Bc5;
    public ModelRenderer Bc6;
    public ModelRenderer Bd2;
    public ModelRenderer Bd3;
    public ModelRenderer Bd4;
    public ModelRenderer Bd5;
    public ModelRenderer Bd6;

    public GWBLogoModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.Bb4 = new ModelRenderer(this, 0, 0);
        this.Bb4.mirror = true;
        this.Bb4.setRotationPoint(1.0F, 6.0F, 0.0F);
        this.Bb4.addBox(0.0F, 0.0F, 0.0F, 5, 1, 1, 0.0F);
        this.baseblock = new ModelRenderer(this, 0, 0);
        this.baseblock.setRotationPoint(-6.0F, 12.0F, -6.0F);
        this.baseblock.addBox(0.0F, 0.0F, 0.0F, 12, 12, 12, 0.0F);
        this.Ba6 = new ModelRenderer(this, 0, 0);
        this.Ba6.mirror = true;
        this.Ba6.setRotationPoint(5.0F, 4.0F, 0.0F);
        this.Ba6.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
        this.Ba2 = new ModelRenderer(this, 0, 0);
        this.Ba2.mirror = true;
        this.Ba2.setRotationPoint(1.0F, 0.0F, 0.0F);
        this.Ba2.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.Bb3 = new ModelRenderer(this, 0, 0);
        this.Bb3.mirror = true;
        this.Bb3.setRotationPoint(1.0F, 3.0F, 0.0F);
        this.Bb3.addBox(0.0F, 0.0F, 0.0F, 5, 1, 1, 0.0F);
        this.Bb5 = new ModelRenderer(this, 0, 0);
        this.Bb5.mirror = true;
        this.Bb5.setRotationPoint(4.0F, 1.0F, 0.0F);
        this.Bb5.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
        this.Bc5 = new ModelRenderer(this, 0, 0);
        this.Bc5.mirror = true;
        this.Bc5.setRotationPoint(4.0F, 1.0F, 0.0F);
        this.Bc5.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
        this.Bd1 = new ModelRenderer(this, 0, 0);
        this.Bd1.mirror = true;
        this.Bd1.setRotationPoint(-7.0F, 16.0F, 1.0F);
        this.Bd1.addBox(0.0F, 0.0F, 0.0F, 1, 7, 1, 0.0F);
        this.setRotateAngle(Bd1, 0.0F, 1.5707963267948966F, 0.0F);
        this.Bd6 = new ModelRenderer(this, 0, 0);
        this.Bd6.mirror = true;
        this.Bd6.setRotationPoint(5.0F, 4.0F, 0.0F);
        this.Bd6.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
        this.Bc4 = new ModelRenderer(this, 0, 0);
        this.Bc4.mirror = true;
        this.Bc4.setRotationPoint(1.0F, 6.0F, 0.0F);
        this.Bc4.addBox(0.0F, 0.0F, 0.0F, 5, 1, 1, 0.0F);
        this.Bd3 = new ModelRenderer(this, 0, 0);
        this.Bd3.mirror = true;
        this.Bd3.setRotationPoint(1.0F, 3.0F, 0.0F);
        this.Bd3.addBox(0.0F, 0.0F, 0.0F, 5, 1, 1, 0.0F);
        this.Ba4 = new ModelRenderer(this, 0, 0);
        this.Ba4.mirror = true;
        this.Ba4.setRotationPoint(1.0F, 6.0F, 0.0F);
        this.Ba4.addBox(0.0F, 0.0F, 0.0F, 5, 1, 1, 0.0F);
        this.Bd4 = new ModelRenderer(this, 0, 0);
        this.Bd4.mirror = true;
        this.Bd4.setRotationPoint(1.0F, 6.0F, 0.0F);
        this.Bd4.addBox(0.0F, 0.0F, 0.0F, 5, 1, 1, 0.0F);
        this.Bb2 = new ModelRenderer(this, 0, 0);
        this.Bb2.mirror = true;
        this.Bb2.setRotationPoint(1.0F, 0.0F, 0.0F);
        this.Bb2.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.Bb6 = new ModelRenderer(this, 0, 0);
        this.Bb6.mirror = true;
        this.Bb6.setRotationPoint(5.0F, 4.0F, 0.0F);
        this.Bb6.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
        this.Bd2 = new ModelRenderer(this, 0, 0);
        this.Bd2.mirror = true;
        this.Bd2.setRotationPoint(1.0F, 0.0F, 0.0F);
        this.Bd2.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.Ba5 = new ModelRenderer(this, 0, 0);
        this.Ba5.mirror = true;
        this.Ba5.setRotationPoint(4.0F, 1.0F, 0.0F);
        this.Ba5.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
        this.Bc2 = new ModelRenderer(this, 0, 0);
        this.Bc2.mirror = true;
        this.Bc2.setRotationPoint(1.0F, 0.0F, 0.0F);
        this.Bc2.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.Bc3 = new ModelRenderer(this, 0, 0);
        this.Bc3.mirror = true;
        this.Bc3.setRotationPoint(1.0F, 3.0F, 0.0F);
        this.Bc3.addBox(0.0F, 0.0F, 0.0F, 5, 1, 1, 0.0F);
        this.Ba3 = new ModelRenderer(this, 0, 0);
        this.Ba3.mirror = true;
        this.Ba3.setRotationPoint(1.0F, 3.0F, 0.0F);
        this.Ba3.addBox(0.0F, 0.0F, 0.0F, 5, 1, 1, 0.0F);
        this.Bd5 = new ModelRenderer(this, 0, 0);
        this.Bd5.mirror = true;
        this.Bd5.setRotationPoint(4.0F, 1.0F, 0.0F);
        this.Bd5.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
        this.Bc1 = new ModelRenderer(this, 0, 0);
        this.Bc1.mirror = true;
        this.Bc1.setRotationPoint(1.0F, 16.0F, 7.0F);
        this.Bc1.addBox(0.0F, 0.0F, 0.0F, 1, 7, 1, 0.0F);
        this.setRotateAngle(Bc1, 0.0F, 3.141592653589793F, 0.0F);
        this.Bc6 = new ModelRenderer(this, 0, 0);
        this.Bc6.mirror = true;
        this.Bc6.setRotationPoint(5.0F, 4.0F, 0.0F);
        this.Bc6.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
        this.Ba1 = new ModelRenderer(this, 0, 0);
        this.Ba1.mirror = true;
        this.Ba1.setRotationPoint(5.0F, 4.0F, -1.0F);
        this.Ba1.addBox(0.0F, 0.0F, 0.0F, 1, 7, 1, 0.0F);
        this.Bb1 = new ModelRenderer(this, 0, 0);
        this.Bb1.mirror = true;
        this.Bb1.setRotationPoint(13.0F, 4.0F, 5.0F);
        this.Bb1.addBox(0.0F, 0.0F, 0.0F, 1, 7, 1, 0.0F);
        this.setRotateAngle(Bb1, 0.0F, -1.5707963267948966F, 0.0F);
        this.Bb1.addChild(this.Bb4);
        this.Ba1.addChild(this.Ba6);
        this.Ba1.addChild(this.Ba2);
        this.Bb1.addChild(this.Bb3);
        this.Bb1.addChild(this.Bb5);
        this.Bc1.addChild(this.Bc5);
        this.Bd1.addChild(this.Bd6);
        this.Bc1.addChild(this.Bc4);
        this.Bd1.addChild(this.Bd3);
        this.Ba1.addChild(this.Ba4);
        this.Bd1.addChild(this.Bd4);
        this.Bb1.addChild(this.Bb2);
        this.Bb1.addChild(this.Bb6);
        this.Bd1.addChild(this.Bd2);
        this.Ba1.addChild(this.Ba5);
        this.Bc1.addChild(this.Bc2);
        this.Bc1.addChild(this.Bc3);
        this.Ba1.addChild(this.Ba3);
        this.Bd1.addChild(this.Bd5);
        this.Bc1.addChild(this.Bc6);
        this.baseblock.addChild(this.Ba1);
        this.baseblock.addChild(this.Bb1);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.baseblock.render(f5);
        this.Bd1.render(f5);
        this.Bc1.render(f5);
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
