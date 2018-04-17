package mastef_chief.gitwebbuilder.app;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.component.*;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.TextArea;
import com.mrcrayfish.device.api.app.interfaces.IHighlight;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.programs.system.layout.StandardLayout;
import mastef_chief.gitwebbuilder.app.components.PasteBinCompleteDialog;
import mastef_chief.gitwebbuilder.app.models.GWBLogoModel;
import mastef_chief.gitwebbuilder.app.tasks.TaskNotificationCopiedCode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Predicate;

public class GWBApp extends Application {

    private static final Predicate<File> PREDICATE_FILE_SITE = file -> !file.isFolder()
            && file.getData().hasKey("content", Constants.NBT.TAG_STRING);

    public static final IHighlight CODE_HIGHLIGHT = text ->
    {
        if(text.startsWith("#"))
            return asArray(TextFormatting.GREEN);

        if(text.startsWith("\"") && text.endsWith("\""))
            return asArray(TextFormatting.AQUA);

        switch(text)
        {
            case "text":
            case "image":
                return asArray(TextFormatting.BLUE);
            default:
                return asArray(TextFormatting.WHITE);
        }
    };


    Minecraft mc = Minecraft.getMinecraft();

    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Clipboard clipboard = toolkit.getSystemClipboard();

    private float rotationCounter = 0;
    private int characterCounter = 0;

    private File currentFile;

    private String rawTextSaved = "";

    private StandardLayout layoutMain;
    private StandardLayout layoutCodeView;
    private StandardLayout layoutDesignView;
    private StandardLayout layoutLiveView;
    //Todo 2 new layouts called layoutDesingView and layoutLiveView

    private Button newSiteButton;
    private Button loadSiteButton;
    private Button backToMenuButton;
    private Button saveAsSiteButton;
    private Button exportToPastebinButton;
    private Button saveSiteButton;
    private Button copyToClipboardButton;

    private ButtonToggle liveViewButton;

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

    private ItemList<String> textFormattingSelectionList;

    private Label descLabel;

    private CheckBox codeViewCheckBox;
    private CheckBox designViewCheckBox;
    private CheckBox liveViewCheckBox;

    private TextArea siteBuilderTextArea;

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
        /*--------------------------------------------------------------------------*/
        layoutMain = new StandardLayout("Menu", 363, 165, this, null);
        layoutMain.setIcon(Icons.HOME);

        layoutMain.setInitListener(() ->
        {
            FileSystem.getApplicationFolder(this, (folder, success) ->
            {
                if (success) {
                    folder.search(file -> file.isForApplication(this)).forEach(file ->
                    {

                    });
                } else {
                    this.openDialog(new Dialog.Message("Error creating app directory"));
                }
            });
        });

        newSiteButton = new Button(250, 65, 75, 16, "New Site", Icons.NEW_FILE);
        newSiteButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                this.setCurrentLayout(layoutCodeView);
                siteBuilderTextArea.setFocused(true);
            }
        });

        layoutMain.addComponent(newSiteButton);
        loadSiteButton = new Button(250, 85, 75, 16, "Load Site", Icons.LOAD);
        loadSiteButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                Dialog.OpenFile openDialog = new Dialog.OpenFile(this);
                openDialog.setResponseHandler((success, file) ->
                {
                    if (file.isForApplication(this)) {
                        NBTTagCompound data = file.getData();
                        siteBuilderTextArea.setText(data.getString("content").replace("\n\n", "\n"));
                        currentFile = file;
                        this.setCurrentLayout(layoutCodeView);
                        return true;
                    } else {
                        Dialog.Message errorDialog = new Dialog.Message("Invalid file for GitWeb Builder");
                        openDialog(errorDialog);
                    }
                    return false;
                });
                this.openDialog(openDialog);
            }
        });

        layoutMain.addComponent(loadSiteButton);

        descLabel = new Label("A Site Builder For GitWeb", 43, 150);
        layoutMain.addComponent(descLabel);

        this.setCurrentLayout(layoutMain);

        /*----------------------------------------------------------------------------------------------------------------------------------------*/

        layoutCodeView = new StandardLayout("Code View", 363, 165, this, null);
        layoutCodeView.setIcon(Icons.EARTH);

        backToMenuButton = new Button(100, 2, Icons.ARROW_LEFT);
        backToMenuButton.setToolTip("Back To Menu", "Will take you back to the main menu");
        backToMenuButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {

                //Todo add function to check if file is saved when back button is pressed and if not saved see if user would like to save
                /*if(currentFile == null) {

                    if(!siteBuilderTextArea.getText().isEmpty()){
                    Dialog.Confirmation dialog = new Dialog.Confirmation("sadsa");
                    dialog.setPositiveText("Yes");
                    dialog.setNegativeText("No");
                    this.openDialog(dialog);
                    dialog.setPositiveListener((mouseX1, mouseY1, mouseButton1) -> {
                        if (mouseButton1 == 0) {
                            NBTTagCompound data = new NBTTagCompound();
                            data.setString("content", siteBuilderTextArea.getText());

                            Dialog.SaveFile saveDialog = new Dialog.SaveFile(this, data);

                            saveDialog.setResponseHandler((success, file) -> {
                                currentFile = file;
                                this.setCurrentLayout(layoutMain);
                                siteBuilderTextArea.clear();
                                liveViewButton.setSelected(false);
                                liveViewButton.setIcon(Icons.PLAY);
                                saveAsSiteButton.setEnabled(true);
                                saveSiteButton.setEnabled(true);
                                exportToPastebinButton.setEnabled(true);
                                copyToClipboardButton.setEnabled(true);
                                return true;
                            });
                            this.openDialog(saveDialog);
                        }
                    });
                    dialog.setNegativeListener((mouseX1, mouseY1, mouseButton1) -> {
                        if (mouseButton1 == 0) {
                            dialog.close();
                            this.setCurrentLayout(layoutMain);
                            siteBuilderTextArea.clear();
                            liveViewButton.setSelected(false);
                            liveViewButton.setIcon(Icons.PLAY);
                            saveAsSiteButton.setEnabled(true);
                            saveSiteButton.setEnabled(true);
                            exportToPastebinButton.setEnabled(true);
                            copyToClipboardButton.setEnabled(true);
                        }
                    });

                }else{
                        this.setCurrentLayout(layoutMain);
                        siteBuilderTextArea.clear();
                        liveViewButton.setSelected(false);
                        liveViewButton.setIcon(Icons.PLAY);
                        saveAsSiteButton.setEnabled(true);
                        saveSiteButton.setEnabled(true);
                        exportToPastebinButton.setEnabled(true);
                        copyToClipboardButton.setEnabled(true);
                    }
                }else{
                    NBTTagCompound data = new NBTTagCompound();
                    data.setString("content", siteBuilderTextArea.getText());
                    currentFile.setData(data, (v, success) -> {
                        if (success) {

                        }
                    });
                    this.setCurrentLayout(layoutMain);
                    siteBuilderTextArea.clear();
                    liveViewButton.setSelected(false);
                    liveViewButton.setIcon(Icons.PLAY);
                    saveAsSiteButton.setEnabled(true);
                    saveSiteButton.setEnabled(true);
                    exportToPastebinButton.setEnabled(true);
                    copyToClipboardButton.setEnabled(true);
                }*/
                this.setCurrentLayout(layoutMain);
                siteBuilderTextArea.clear();
                liveViewButton.setSelected(false);
                liveViewButton.setIcon(Icons.PLAY);
                saveAsSiteButton.setEnabled(true);
                saveSiteButton.setEnabled(true);
                exportToPastebinButton.setEnabled(true);
                copyToClipboardButton.setEnabled(true);
                currentFile = null;
            }
        });
        layoutCodeView.addComponent(backToMenuButton);

        saveAsSiteButton = new Button(118, 2, Icons.SAVE);
        saveAsSiteButton.setToolTip("Save As", "Saves your site to a new file");
        saveAsSiteButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                NBTTagCompound data = new NBTTagCompound();
                data.setString("content", siteBuilderTextArea.getText());

                Dialog.SaveFile saveDialog = new Dialog.SaveFile(this, data);

                saveDialog.setResponseHandler((success, file) -> {
                    currentFile = file;
                    return true;
                });
                this.openDialog(saveDialog);
            }
        });
        layoutCodeView.addComponent(saveAsSiteButton);

        saveSiteButton = new Button(136, 2, Icons.SAVE);
        saveSiteButton.setToolTip("Save", "Saves your site to the current file");
        saveSiteButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                if (currentFile != null) {
                    NBTTagCompound data = new NBTTagCompound();
                    data.setString("content", siteBuilderTextArea.getText());
                    currentFile.setData(data, (v, success) -> {
                        if (success) {

                        }
                    });
                } else {
                    NBTTagCompound data = new NBTTagCompound();
                    data.setString("content", siteBuilderTextArea.getText());

                    Dialog.SaveFile saveDialog = new Dialog.SaveFile(this, data);

                    saveDialog.setResponseHandler((success, file) -> {
                        currentFile = file;
                        return true;
                    });
                    this.openDialog(saveDialog);
                }

            }
        });
        layoutCodeView.addComponent(saveSiteButton);
        exportToPastebinButton = new Button(154, 2, Icons.EXPORT);
        exportToPastebinButton.setToolTip("Export To  PasteBin", "Exports code to GitWeb Buidler's Pastebin");
        exportToPastebinButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                Dialog.Input exportDialog = new Dialog.Input("Site Title:");
                exportDialog.setTitle("Export Site");
                exportDialog.setPositiveText("Export");
                this.openDialog(exportDialog);
                exportDialog.setResponseHandler((success, v) ->
                {
                    if (success) {
                        createPastebin(exportDialog.getTextFieldInput().getText(), siteBuilderTextArea.getText().replace("\n\n", "\n").replace("&", "%26"));
                    }
                    return true;
                });
            }
        });
        layoutCodeView.addComponent(exportToPastebinButton);

        copyToClipboardButton = new Button(172, 2, Icons.COPY);
        copyToClipboardButton.setToolTip("Copy to Clipboard", "Copy's code to clipboard with correct formatting for GitWeb");
        copyToClipboardButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                StringSelection code = new StringSelection(siteBuilderTextArea.getText().replace("\n\n", "\n"));
                clipboard.setContents(code, null);
                TaskManager.sendTask(new TaskNotificationCopiedCode());
            }
        });
        layoutCodeView.addComponent(copyToClipboardButton);

        liveViewButton = new ButtonToggle(190, 2, Icons.PLAY);
        liveViewButton.setClickListener((mouseX, mouseY, mouseButton) -> {

            if (mouseButton == 0) {

                boolean active = !liveViewButton.isSelected();
                liveViewButton.setIcon(active ? Icons.STOP : Icons.PLAY);
                if (active) {
                    rawTextSaved = siteBuilderTextArea.getText();
                    siteBuilderTextArea.setEditable(false);
                    siteBuilderTextArea.setWrapText(true);
                    siteBuilderTextArea.setText(renderLiveView(siteBuilderTextArea.getText()));
                    saveAsSiteButton.setEnabled(false);
                    saveSiteButton.setEnabled(false);
                    exportToPastebinButton.setEnabled(false);
                    copyToClipboardButton.setEnabled(false);
                } else {
                    siteBuilderTextArea.setEditable(true);
                    siteBuilderTextArea.setWrapText(false);
                    siteBuilderTextArea.setText(rawTextSaved.replace("\n\n", "\n"));
                    saveAsSiteButton.setEnabled(true);
                    saveSiteButton.setEnabled(true);
                    exportToPastebinButton.setEnabled(true);
                    copyToClipboardButton.setEnabled(true);
                }

            }

        });
        layoutCodeView.addComponent(liveViewButton);

        RadioGroup viewGroup = new RadioGroup();

        codeViewCheckBox = new CheckBox("Code", 240, 5);
        codeViewCheckBox.setSelected(true);
        codeViewCheckBox.setRadioGroup(viewGroup);
        codeViewCheckBox.setClickListener((mouseX, mouseY, mouseButton) -> {
            if(mouseButton == 0){

                this.setCurrentLayout(layoutCodeView);

            }
        });
        layoutCodeView.addComponent(codeViewCheckBox);

        designViewCheckBox = new CheckBox("Design", 280, 5);
        designViewCheckBox.setRadioGroup(viewGroup);
        designViewCheckBox.setClickListener((mouseX, mouseY, mouseButton) -> {
            if(mouseButton == 0){

                this.setCurrentLayout(layoutDesignView);

            }
        });
        layoutCodeView.addComponent(designViewCheckBox);

        liveViewCheckBox = new CheckBox("Live", 327, 5);
        liveViewCheckBox.setRadioGroup(viewGroup);
        liveViewCheckBox.setClickListener((mouseX, mouseY, mouseButton) -> {
            if(mouseButton == 0){

                this.setCurrentLayout(layoutLiveView);

            }
        });
        layoutCodeView.addComponent(liveViewCheckBox);



        siteBuilderTextArea = new TextArea(0, 21, layoutCodeView.width, layoutCodeView.height - 55);
        siteBuilderTextArea.setHighlight(CODE_HIGHLIGHT);
        layoutCodeView.addComponent(siteBuilderTextArea);

        //Color Buttons
        colorBlackButton = new Button(75, 140, 16, 16, TextFormatting.BLACK + "A");
        colorBlackButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&0");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(colorBlackButton);

        colorDarkBlueButton = new Button(93, 140, 16, 16, TextFormatting.DARK_BLUE + "A");
        colorDarkBlueButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&1");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(colorDarkBlueButton);

        colorDarkGreenButton = new Button(111, 140, 16, 16, TextFormatting.DARK_GREEN + "A");
        colorDarkGreenButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&2");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(colorDarkGreenButton);

        colorDarkAquaButton = new Button(129, 140, 16, 16, TextFormatting.DARK_AQUA + "A");
        colorDarkAquaButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&3");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(colorDarkAquaButton);

        colorDarkRedButton = new Button(147, 140, 16, 16, TextFormatting.DARK_RED + "A");
        colorDarkRedButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&4");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(colorDarkRedButton);

        colorDarkPurpleButton = new Button(165, 140, 16, 16, TextFormatting.DARK_PURPLE + "A");
        colorDarkPurpleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&5");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(colorDarkPurpleButton);

        colorGoldButton = new Button(183, 140, 16, 16, TextFormatting.GOLD + "A");
        colorGoldButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&6");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(colorGoldButton);

        colorGrayButton = new Button(201, 140, 16, 16, TextFormatting.GRAY + "A");
        colorGrayButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&7");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(colorGrayButton);

        colorDarkGrayButton = new Button(219, 140, 16, 16, TextFormatting.DARK_GRAY + "A");
        colorDarkGrayButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&8");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(colorDarkGrayButton);

        colorBlueButton = new Button(237, 140, 16, 16, TextFormatting.BLUE + "A");
        colorBlueButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&9");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(colorBlueButton);

        colorGreenButton = new Button(255, 140, 16, 16, TextFormatting.GREEN + "A");
        colorGreenButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&a");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(colorGreenButton);

        colorAquaButton = new Button(273, 140, 16, 16, TextFormatting.AQUA + "A");
        colorAquaButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&b");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(colorAquaButton);

        colorRedButton = new Button(291, 140, 16, 16, TextFormatting.RED + "A");
        colorRedButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&c");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(colorRedButton);

        colorLightPurpleButton = new Button(309, 140, 16, 16, TextFormatting.LIGHT_PURPLE + "A");
        colorLightPurpleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&d");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(colorLightPurpleButton);

        colorYellowButton = new Button(327, 140, 16, 16, TextFormatting.YELLOW + "A");
        colorYellowButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&e");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(colorYellowButton);

        colorWhiteButton = new Button(345, 140, 16, 16, TextFormatting.WHITE + "A");
        colorWhiteButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&f");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(colorWhiteButton);


        //Formatting Button
        obfuscateButton = new Button(75, 140, 16, 16, TextFormatting.OBFUSCATED + "A");
        obfuscateButton.setVisible(false);
        obfuscateButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&k");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(obfuscateButton);

        boldButton = new Button(93, 140, 16, 16, TextFormatting.BOLD + "A");
        boldButton.setVisible(false);
        boldButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&l");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(boldButton);

        strikethroughButton = new Button(111, 140, 16, 16, TextFormatting.STRIKETHROUGH + "A");
        strikethroughButton.setVisible(false);
        strikethroughButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&m");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(strikethroughButton);

        underlineButton = new Button(129, 140, 16, 16, TextFormatting.UNDERLINE + "A");
        underlineButton.setVisible(false);
        underlineButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&n");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(underlineButton);

        italicButton = new Button(147, 140, 16, 16, TextFormatting.ITALIC + "A");
        italicButton.setVisible(false);
        italicButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&o");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(italicButton);

        resetButton = new Button(165, 140, 35, 16, "Reset");
        resetButton.setVisible(false);
        resetButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&r");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(resetButton);

        //Todo Add search for code

        textFormattingSelectionList = new ItemList<String>(2, 133, 72, 2);
        textFormattingSelectionList.addItem("Coloring");
        textFormattingSelectionList.addItem("Formatting");
        textFormattingSelectionList.addItem("Modules");
        textFormattingSelectionList.setSelectedIndex(0);
        textFormattingSelectionList.setItemClickListener((e, index, mouseButton) -> {

            if (mouseButton == 0) {

                if (e == "Coloring") {
                    //Set Formatting Buttons Visible to false
                    obfuscateButton.setVisible(false);
                    boldButton.setVisible(false);
                    strikethroughButton.setVisible(false);
                    underlineButton.setVisible(false);
                    italicButton.setVisible(false);
                    resetButton.setVisible(false);


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

                if (e == "Formatting") {
                    //Set Formatting Buttons Visible to true
                    obfuscateButton.setVisible(true);
                    boldButton.setVisible(true);
                    strikethroughButton.setVisible(true);
                    underlineButton.setVisible(true);
                    italicButton.setVisible(true);
                    resetButton.setVisible(true);

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
        layoutCodeView.addComponent(textFormattingSelectionList);

        /*---------------------------------------------------------------------------------------------------------------*/

        layoutDesignView = new StandardLayout("Design View", 363, 165, this, null);
        layoutDesignView.setIcon(Icons.EDIT);

        layoutDesignView.addComponent(codeViewCheckBox);
        layoutDesignView.addComponent(designViewCheckBox);
        layoutDesignView.addComponent(liveViewCheckBox);


        /*---------------------------------------------------------------------------------------------------------------*/

        layoutLiveView = new StandardLayout("Live View", 363, 165, this, null);
        layoutLiveView.setIcon(Icons.PLAY);

        layoutLiveView.addComponent(codeViewCheckBox);
        layoutLiveView.addComponent(designViewCheckBox);
        layoutLiveView.addComponent(liveViewCheckBox);

    }

    @Override
    public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks) {

        partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();

        super.render(laptop, mc, x, y, mouseX, mouseY, active, partialTicks);


        if (this.getCurrentLayout() == layoutMain) {
            GlStateManager.pushMatrix();
            {
                GlStateManager.enableDepth();
                RenderHelper.enableStandardItemLighting();
                GlStateManager.translate(x + 150, y - 33, 250);
                GlStateManager.scale((float) -7.0, (float) -7.0, (float) -7.0);
                GlStateManager.rotate(5F, 1, 0, 0);
                GlStateManager.rotate(200F, 0, 0, 1);
                GlStateManager.rotate(-rotationCounter - partialTicks, 0, 1, 0);
                mc.getTextureManager().bindTexture(logo);
                gwbLogoModel.render((Entity) null, 0F, 0F, 0F, 0F, 0F, 1.0F);
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableDepth();

            }
            GlStateManager.popMatrix();
        }

    }

    @Override
    public void onTick() {
        super.onTick();

        rotationCounter++;
        rotationCounter %= 360;


    }

    public void createPastebin(String title, String code) {
        String apikey = "";
        try {
            URL url = new URL("http://mastefchief.com/gwbpb/api/create");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            writer.write("text=" + code + "&title=" + title + "&name=" + Minecraft.getMinecraft().player.getName());
            writer.flush();
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                this.openDialog(new PasteBinCompleteDialog(line));
            }
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public String renderLiveView(String content) {

        return content
                //new line fix
                .replace("\n\n", "\n")
                //color conversion to unicode format
                .replace("&0", "\u00A70").replace("&1", "\u00A71").replace("&2", "\u00A72").replace("&3", "\u00A73")
                .replace("&4", "\u00A74").replace("&5", "\u00A75").replace("&6", "\u00A76").replace("&7", "\u00A77")
                .replace("&8", "\u00A78").replace("&9", "\u00A79").replace("&a", "\u00A7a").replace("&b", "\u00A7b")
                .replace("&c", "\u00A7c").replace("&d", "\u00A7d").replace("&e", "\u00A7e").replace("&f", "\u00A7f")
                //Formatting conversion to unicode format
                .replace("&k", "\u00A7k").replace("&l", "\u00A7l").replace("&m", "\u00A7m").replace("&n", "\u00A7n")
                .replace("&o", "\u00A7o").replace("&r", "\u00A7r");

    }

    @Override
    public void handleKeyTyped(char character, int code) {
        super.handleKeyTyped(character, code);

        if (this.getCurrentLayout() == layoutCodeView) {
            if (GuiScreen.isCtrlKeyDown() && code == Keyboard.KEY_S) {

                if (currentFile != null) {
                    NBTTagCompound data = new NBTTagCompound();
                    data.setString("content", siteBuilderTextArea.getText());
                    currentFile.setData(data, (v, success) -> {
                        if (success) {

                        }
                    });
                } else {
                    NBTTagCompound data = new NBTTagCompound();
                    data.setString("content", siteBuilderTextArea.getText());

                    Dialog.SaveFile saveDialog = new Dialog.SaveFile(this, data);
                    saveDialog.setResponseHandler((success, file) -> {
                        currentFile = file;
                        return true;
                    });
                    this.openDialog(saveDialog);
                }
            }
            if (GuiScreen.isCtrlKeyDown() && GuiScreen.isShiftKeyDown() && code == Keyboard.KEY_S) {

                NBTTagCompound data = new NBTTagCompound();
                data.setString("content", siteBuilderTextArea.getText());

                Dialog.SaveFile saveDialog = new Dialog.SaveFile(this, data);
                saveDialog.setResponseHandler((success, file) -> {
                    currentFile = file;
                    return true;
                });
                this.openDialog(saveDialog);
            }
        }


        if (characterCounter == 5) {
            if (currentFile != null) {

                NBTTagCompound data = new NBTTagCompound();
                data.setString("content", siteBuilderTextArea.getText());
                currentFile.setData(data, (v, success) -> {
                    if (success) {

                    }
                });
            }
            characterCounter = 0;
        }
        characterCounter++;

        if (code == Keyboard.KEY_DELETE) {
            siteBuilderTextArea.moveCursorRight(1);
            siteBuilderTextArea.performBackspace();
        }
    }

    @Override
    public boolean handleFile(File file) {

        if (!PREDICATE_FILE_SITE.test(file))
            return false;


        NBTTagCompound data = file.getData();
        siteBuilderTextArea.setText(data.getString("content").replace("\n\n", "\n"));
        this.setCurrentLayout(layoutCodeView);

        return true;

    }

    @Override
    public void onClose() {
        super.onClose();
        currentFile = null;
    }

    private static <T extends Object> T[] asArray(T ... t)
    {
        return t;
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
