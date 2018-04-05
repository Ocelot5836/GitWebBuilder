package mastef_chief.gitwebbuilder.app;

import com.mrcrayfish.device.api.app.*;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.ComboBox;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.TextArea;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.programs.ApplicationNoteStash;
import com.mrcrayfish.device.programs.system.layout.StandardLayout;
import mastef_chief.gitwebbuilder.app.models.GWBLogoModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;

import java.lang.System;
import java.security.Key;

public class GWBApp extends Application {

    Minecraft mc = Minecraft.getMinecraft();

    private float rotationCounter = 0;

    private StandardLayout layoutMain;
    private StandardLayout layoutSiteBuilder; //Editor View
    private StandardLayout layoutSiteBuilderTF; //Text Formatting View
    private StandardLayout layoutSiteBuilderLV; //Live View

    private Button newSiteButton;
    private Button loadSiteButton;
    private Button saveSiteButton;

    //16 Color Buttons
    private Button colorBlackButton;
    private Button colorDarkBlueButton;
    private Button colorDarkGreenButton;
    private Button colorDarkAquaButton;
    private Button colorDarkRedButton;
    private Button colorDarkPurpleButton;
    private Button colorGoldButton;
    private Button colorGrayButton;
    private Button colorDarkGrayButton;
    private Button colorBlueButton;
    private Button colorGreenButton;
    private Button colorAquaButton;
    private Button colorRedButton;
    private Button colorLightPurpleButton;
    private Button colorYellowButton;
    private Button colorWhiteButton;

    //Formatting Buttons
    private Button obfuscateButton;
    private Button boldButton;
    private Button strikethroughButton;
    private Button underlineButton;
    private Button italicButton;
    private Button resetButton;


    private ComboBox.List<String> editorSelectionList;
    private ComboBox.List<String> textFormattingSelectionList;

    private Label descLabel;

    private TextArea siteBuilderTextArea;
    private TextArea siteBuilderTFTextArea;
    private TextArea siteBuilderLVTextArea;

    private GWBLogoModel gwbLogoModel = new GWBLogoModel();

    private static final ResourceLocation logo = new ResourceLocation("gitwebbuilder:textures/app/gui/logo.png");

    /**
     * The default initialization method. Clears any components in the default
     * layout and sets it as the current layout. If you override this method and
     * are using the default layout, make sure you call it using
     * <code>super.init(x, y)</code>
     * <p>
     * The parameters passed are the x and y location of the top left corner or
     * your application window.
     */
    @Override
    public void init() {

        //TODO work on opening and saving files

        layoutMain = new StandardLayout("Menu",363, 165, this, null);
        layoutMain.setIcon(Icons.HOME);
        layoutMain.setTitle("GitWeb Builder (Menu)");
        layoutMain.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            GlStateManager.pushMatrix();
            {
                GlStateManager.enableDepth();
                GlStateManager.disableLighting();
                GlStateManager.translate(x + 150, y - 20, 250);
                GlStateManager.scale((float) -7.0, (float) -7.0, (float) -7.0);
                GlStateManager.rotate(5F, 1, 0, 0);
                GlStateManager.rotate(200F, 0, 0, 1);
                GlStateManager.rotate(-rotationCounter - 45, 0, 1, 0);
                mc.getTextureManager().bindTexture(logo);
                gwbLogoModel.render((Entity)null, 0F, 0F, 0F , 0F , 0F , 1.0F);
                GlStateManager.disableDepth();
            }
            GlStateManager.popMatrix();
        });

        layoutMain.setInitListener(() ->
        {
            //notes.getItems().clear();
            FileSystem.getApplicationFolder(this, (folder, success) ->
            {
                if(success)
                {
                    folder.search(file -> file.isForApplication(this)).forEach(file ->
                    {
                        //notes.addItem(ApplicationNoteStash.Note.fromFile(file));
                    });
                }
                else
                {
                    this.openDialog(new Dialog.Message("Error creating app directory"));
                }
            });
        });


        newSiteButton = new Button(250, 65, 75, 16, "New Site", Icons.NEW_FILE);
        newSiteButton.setClickListener((mouseX, mouseY, mouseButton) ->{
            if(mouseButton == 0){
                this.setCurrentLayout(layoutSiteBuilder);
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutMain.addComponent(newSiteButton);
        loadSiteButton = new Button(250, 85, 75, 16, "Load Site", Icons.LOAD);
        Dialog.OpenFile openDialog = new Dialog.OpenFile(this);
        loadSiteButton.setClickListener((mouseX, mouseY, mouseButton) ->{
            if(mouseButton == 0)
            {
                openDialog.setResponseHandler((success, file) ->
                {
                    this.setCurrentLayout(layoutSiteBuilder);
                    return true;
                });
                this.openDialog(openDialog);
            }
        });
        layoutMain.addComponent(loadSiteButton);

        descLabel = new Label("A Site Builder For GitWeb", 43, 150);
        layoutMain.addComponent(descLabel);

        this.setCurrentLayout(layoutMain);

        /*----------------------------------------------------------------------------------------------------------------------------------------*/

        layoutSiteBuilder = new StandardLayout("Site Builder", 363, 165, this, null);
        layoutSiteBuilder.setIcon(Icons.EARTH);
        layoutSiteBuilder.setTitle("GitWeb Builder (Site Builder)");

        Object[] editorType = new String[]{"Site Editor View", "Text Formatting View", "Site Live View"};
        editorSelectionList = new ComboBox.List(235, 3, 125, editorType);
        editorSelectionList.setChangeListener((oldValue, newValue) -> {
            if(newValue != oldValue){
                if (newValue.equals("Site Editor View")) {
                    if(this.getCurrentLayout().getTitle() != "GitWeb Builder (Site Builder Live)"){
                        siteBuilderTextArea.clear();
                        siteBuilderTextArea.setText(siteBuilderTFTextArea.getText().replace("\n\n", "\n"));
                        siteBuilderTextArea.setFocused(true);
                    }
                    this.setCurrentLayout(layoutSiteBuilder);
                }
                if (newValue.equals("Text Formatting View")) {
                    if(this.getCurrentLayout().getTitle() != "GitWeb Builder (Site Builder Live)") {
                        siteBuilderTFTextArea.clear();
                        siteBuilderTFTextArea.setText(siteBuilderTextArea.getText().replace("\n\n", "\n"));
                        siteBuilderTFTextArea.setFocused(true);
                    }
                    this.setCurrentLayout(layoutSiteBuilderTF);
                }
                if (newValue.equals("Site Live View")) {
                    if(this.getCurrentLayout().getTitle() == "GitWeb Builder (Site Builder)"){
                        siteBuilderLVTextArea.clear();
                        siteBuilderLVTextArea.setText(siteBuilderTextArea.getText().replace("~&", "§").replace("\n\n", "\n"));
                    }
                    if(this.getCurrentLayout().getTitle() == "GitWeb Builder (Site Builder Formatting)"){
                        siteBuilderLVTextArea.clear();
                        siteBuilderLVTextArea.setText(siteBuilderTFTextArea.getText().replace("~&", "§").replace("\n\n", "\n"));
                    }

                    this.setCurrentLayout(layoutSiteBuilderLV);

                }
            }
        });
        layoutSiteBuilder.addComponent(editorSelectionList);

        saveSiteButton = new Button(100, 2, Icons.SAVE);
        saveSiteButton.setClickListener((mouseX, mouseY, mouseButton) ->{
            if(mouseButton == 0){
                NBTTagCompound data = new NBTTagCompound();

                Dialog.SaveFile saveDialog = new Dialog.SaveFile(this, data);
                saveDialog.setFolder(getApplicationFolderPath());
                saveDialog.setResponseHandler((success, file) ->{
                    return true;
                });
                this.openDialog(saveDialog);
            }
        });
        layoutSiteBuilder.addComponent(saveSiteButton);

        siteBuilderTextArea = new TextArea(0, 21, layoutSiteBuilder.width, layoutSiteBuilder.height - 22);
        siteBuilderTextArea.setWrapText(true);
        layoutSiteBuilder.addComponent(siteBuilderTextArea);

        /*----------------------------------------------------------------------------------------------------------------------------------------*/

        layoutSiteBuilderTF = new StandardLayout("Text Formatting",363, 165, this, null);
        layoutSiteBuilderTF.setIcon(Alphabet.UPPERCASE_A);
        layoutSiteBuilderTF.setTitle("GitWeb Builder (Site Builder Formatting)");

        layoutSiteBuilderTF.addComponent(editorSelectionList);
        layoutSiteBuilderTF.addComponent(saveSiteButton);

        siteBuilderTFTextArea = new TextArea(0, 21, layoutSiteBuilder.width, layoutSiteBuilder.height - 40);
        siteBuilderTFTextArea.setWrapText(true);
        layoutSiteBuilderTF.addComponent(siteBuilderTFTextArea);

        Object[] formattingType = new String[]{"Coloring", "Formatting"};
        textFormattingSelectionList = new ComboBox.List(2, 148, 72, formattingType);
        textFormattingSelectionList.setChangeListener((oldValue, newValue) -> {
            if(newValue != oldValue){
                if (newValue.equals("Coloring")) {

                    //Set Formatting Buttons Visible to false
                    obfuscateButton.setVisible(false);

                    //Set Color Buttons Visible to true
                    colorBlackButton.setVisible(true);
                    colorDarkBlueButton.setVisible(true);
                    colorDarkGreenButton.setVisible(true);
                    colorDarkAquaButton.setVisible(true);
                    colorDarkRedButton.setVisible(true);
                    colorDarkPurpleButton.setVisible(true);
                    colorGoldButton.setVisible(true);
                    colorGrayButton.setVisible(true);
                    colorDarkGrayButton.setVisible(true);
                    colorBlueButton.setVisible(true);
                    colorGreenButton.setVisible(true);
                    colorAquaButton.setVisible(true);
                    colorRedButton.setVisible(true);
                    colorLightPurpleButton.setVisible(true);
                    colorYellowButton.setVisible(true);
                    colorWhiteButton.setVisible(true);

                }
                if (newValue.equals("Formatting")) {

                    //Set Formatting Buttons Visible to true
                    obfuscateButton.setVisible(true);

                    //Set Color Buttons Visible to false
                    colorBlackButton.setVisible(false);
                    colorDarkBlueButton.setVisible(false);
                    colorDarkGreenButton.setVisible(false);
                    colorDarkAquaButton.setVisible(false);
                    colorDarkRedButton.setVisible(false);
                    colorDarkPurpleButton.setVisible(false);
                    colorGoldButton.setVisible(false);
                    colorGrayButton.setVisible(false);
                    colorDarkGrayButton.setVisible(false);
                    colorBlueButton.setVisible(false);
                    colorGreenButton.setVisible(false);
                    colorAquaButton.setVisible(false);
                    colorRedButton.setVisible(false);
                    colorLightPurpleButton.setVisible(false);
                    colorYellowButton.setVisible(false);
                    colorWhiteButton.setVisible(false);

                }
            }
        });
        layoutSiteBuilderTF.addComponent(textFormattingSelectionList);


        //Color Buttons
        colorBlackButton = new Button(75, 147, 16, 16,TextFormatting.BLACK + "⬤");
        colorBlackButton.setClickListener((mouseX, mouseY, mouseButton) ->{
            if(mouseButton == 0){
                siteBuilderTFTextArea.writeText("~&0");
                siteBuilderTFTextArea.setFocused(true);
            }
        });
        layoutSiteBuilderTF.addComponent(colorBlackButton);

        colorDarkBlueButton = new Button(93, 147, 16, 16,TextFormatting.DARK_BLUE + "⬤");
        colorDarkBlueButton.setClickListener((mouseX, mouseY, mouseButton) ->{
            if(mouseButton == 0){
                siteBuilderTFTextArea.writeText("~&1");
                siteBuilderTFTextArea.setFocused(true);
            }
        });
        layoutSiteBuilderTF.addComponent(colorDarkBlueButton);

        colorDarkGreenButton = new Button(111, 147, 16, 16,TextFormatting.DARK_GREEN + "⬤");
        colorDarkGreenButton.setClickListener((mouseX, mouseY, mouseButton) ->{
            if(mouseButton == 0){
                siteBuilderTFTextArea.writeText("~&2");
                siteBuilderTFTextArea.setFocused(true);
            }
        });
        layoutSiteBuilderTF.addComponent(colorDarkGreenButton);

        colorDarkAquaButton = new Button(129, 147, 16, 16,TextFormatting.DARK_AQUA + "⬤");
        colorDarkAquaButton.setClickListener((mouseX, mouseY, mouseButton) ->{
            if(mouseButton == 0){
                siteBuilderTFTextArea.writeText("~&3");
                siteBuilderTFTextArea.setFocused(true);
            }
        });
        layoutSiteBuilderTF.addComponent(colorDarkAquaButton);

        colorDarkRedButton = new Button(147, 147, 16, 16,TextFormatting.DARK_RED + "⬤");
        colorDarkRedButton.setClickListener((mouseX, mouseY, mouseButton) ->{
            if(mouseButton == 0){
                siteBuilderTFTextArea.writeText("~&4");
                siteBuilderTFTextArea.setFocused(true);
            }
        });
        layoutSiteBuilderTF.addComponent(colorDarkRedButton);

        colorDarkPurpleButton = new Button(165, 147, 16, 16,TextFormatting.DARK_PURPLE + "⬤");
        colorDarkPurpleButton.setClickListener((mouseX, mouseY, mouseButton) ->{
            if(mouseButton == 0){
                siteBuilderTFTextArea.writeText("~&5");
                siteBuilderTFTextArea.setFocused(true);
            }
        });
        layoutSiteBuilderTF.addComponent(colorDarkPurpleButton);

        colorGoldButton = new Button(183, 147, 16, 16,TextFormatting.GOLD + "⬤");
        colorGoldButton.setClickListener((mouseX, mouseY, mouseButton) ->{
            if(mouseButton == 0){
                siteBuilderTFTextArea.writeText("~&6");
                siteBuilderTFTextArea.setFocused(true);
            }
        });
        layoutSiteBuilderTF.addComponent(colorGoldButton);

        colorGrayButton = new Button(201, 147, 16, 16,TextFormatting.GRAY + "⬤");
        colorGrayButton.setClickListener((mouseX, mouseY, mouseButton) ->{
            if(mouseButton == 0){
                siteBuilderTFTextArea.writeText("~&7");
                siteBuilderTFTextArea.setFocused(true);
            }
        });
        layoutSiteBuilderTF.addComponent(colorGrayButton);

        colorDarkGrayButton = new Button(219, 147, 16, 16,TextFormatting.DARK_GRAY + "⬤");
        colorDarkGrayButton.setClickListener((mouseX, mouseY, mouseButton) ->{
            if(mouseButton == 0){
                siteBuilderTFTextArea.writeText("~&8");
                siteBuilderTFTextArea.setFocused(true);
            }
        });
        layoutSiteBuilderTF.addComponent(colorDarkGrayButton);

        colorBlueButton = new Button(237, 147, 16, 16,TextFormatting.BLUE + "⬤");
        colorBlueButton.setClickListener((mouseX, mouseY, mouseButton) ->{
            if(mouseButton == 0){
                siteBuilderTFTextArea.writeText("~&9");
                siteBuilderTFTextArea.setFocused(true);
            }
        });
        layoutSiteBuilderTF.addComponent(colorBlueButton);

        colorGreenButton = new Button(255, 147, 16, 16,TextFormatting.GREEN + "⬤");
        colorGreenButton.setClickListener((mouseX, mouseY, mouseButton) ->{
            if(mouseButton == 0){
                siteBuilderTFTextArea.writeText("~&a");
                siteBuilderTFTextArea.setFocused(true);
            }
        });
        layoutSiteBuilderTF.addComponent(colorGreenButton);

        colorAquaButton = new Button(273, 147, 16, 16,TextFormatting.AQUA + "⬤");
        colorAquaButton.setClickListener((mouseX, mouseY, mouseButton) ->{
            if(mouseButton == 0){
                siteBuilderTFTextArea.writeText("~&b");
                siteBuilderTFTextArea.setFocused(true);
            }
        });
        layoutSiteBuilderTF.addComponent(colorAquaButton);

        colorRedButton = new Button(291, 147, 16, 16,TextFormatting.RED + "⬤");
        colorRedButton.setClickListener((mouseX, mouseY, mouseButton) ->{
            if(mouseButton == 0){
                siteBuilderTFTextArea.writeText("~&c");
                siteBuilderTFTextArea.setFocused(true);
            }
        });
        layoutSiteBuilderTF.addComponent(colorRedButton);

        colorLightPurpleButton = new Button(309, 147, 16, 16,TextFormatting.LIGHT_PURPLE + "⬤");
        colorLightPurpleButton.setClickListener((mouseX, mouseY, mouseButton) ->{
            if(mouseButton == 0){
                siteBuilderTFTextArea.writeText("~&d");
                siteBuilderTFTextArea.setFocused(true);
            }
        });
        layoutSiteBuilderTF.addComponent(colorLightPurpleButton);

        colorYellowButton = new Button(327, 147, 16, 16,TextFormatting.YELLOW + "⬤");
        colorYellowButton.setClickListener((mouseX, mouseY, mouseButton) ->{
            if(mouseButton == 0){
                siteBuilderTFTextArea.writeText("~&e");
                siteBuilderTFTextArea.setFocused(true);
            }
        });
        layoutSiteBuilderTF.addComponent(colorYellowButton);

        colorWhiteButton = new Button(345, 147, 16, 16,TextFormatting.WHITE + "⬤");
        colorWhiteButton.setClickListener((mouseX, mouseY, mouseButton) ->{
            if(mouseButton == 0){
                siteBuilderTFTextArea.writeText("~&f");
                siteBuilderTFTextArea.setFocused(true);
            }
        });
        layoutSiteBuilderTF.addComponent(colorWhiteButton);


        //Formatting Button
        obfuscateButton = new Button(75,147, 16, 16, TextFormatting.OBFUSCATED + "A");
        obfuscateButton.setVisible(false);
        obfuscateButton.setClickListener((mouseX, mouseY, mouseButton) ->{
            if(mouseButton == 0){
                siteBuilderTFTextArea.writeText("~&k");
                siteBuilderTFTextArea.setFocused(true);
            }
        });
        layoutSiteBuilderTF.addComponent(obfuscateButton);

        /*----------------------------------------------------------------------------------------------------------------------------------------*/

        layoutSiteBuilderLV = new StandardLayout("Live View",363, 165, this, null);
        layoutSiteBuilderLV.setIcon(Icons.PLAY);
        layoutSiteBuilderLV.setTitle("GitWeb Builder (Site Builder Live)");

        layoutSiteBuilderLV.addComponent(editorSelectionList);
        layoutSiteBuilderLV.addComponent(saveSiteButton);

        siteBuilderLVTextArea = new TextArea(0, 21, layoutSiteBuilder.width, layoutSiteBuilder.height - 22);
        //siteBuilderLVTextArea.setEditable(false);
        siteBuilderLVTextArea.setWrapText(true);
        layoutSiteBuilderLV.addComponent(siteBuilderLVTextArea);

        //Todo Add ComboBox with selection of Color Codes Buttons & Formatting Codes Buttons

    }

    @Override
    public void onTick() {
        super.onTick();

        if (rotationCounter == 360) {
            rotationCounter = 0;
        }

        rotationCounter =  rotationCounter + 1 ;

    }

    //§§§§§§§§§§§§§§§§§

    @Override
    public void handleKeyTyped(char character, int code) {
        super.handleKeyTyped(character, code);
    }

    @Override
    public boolean handleFile(File file) {
        return super.handleFile(file);
    }

    /**
     * Called when you first load up your application. Allows you to read any
     * stored data you have saved. Only called if you have saved data. This
     * method is called after {{@link #init()} so you can update any
     * Components with this data.
     *
     * @param tagCompound the tag compound where you saved data is
     */
    @Override
    public void load(NBTTagCompound tagCompound) {

    }

    /**
     * Allows you to save data from your application. This is only called if
     * {@link #isDirty()} returns true. You can mark your application as dirty
     * by calling {@link #markDirty()}.
     *
     * @param tagCompound the tag compound to save your data to
     */
    @Override
    public void save(NBTTagCompound tagCompound) {

    }
}
