package mastef_chief.gitwebbuilder.app;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.*;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.TextArea;
import com.mrcrayfish.device.api.app.interfaces.IHighlight;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.api.utils.OnlineRequest;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.programs.gitweb.component.GitWebFrame;
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

    //Todo module dialog, scrollable layout for design view, custom layouts which take in module instance, which type of module

    private static final Predicate<File> PREDICATE_FILE_SITE = file -> !file.isFolder()
            && file.getData().hasKey("content", Constants.NBT.TAG_STRING);

    public static final IHighlight CODE_HIGHLIGHT = text ->
    {
        if (text.startsWith("#"))
            return asArray(TextFormatting.GREEN);

        if (text.startsWith("\"") && text.endsWith("\""))
            return asArray(TextFormatting.AQUA);

        switch (text) {
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

    private GitWebFrame liveGitWebFrame;

    private float rotationCounter = 0;
    private int tickCounter = 0;

    private boolean autoSave = true;

    private File currentFile;

    private StandardLayout layoutMain;
    private StandardLayout layoutSettings;
    private StandardLayout layoutCodeView;
    private StandardLayout layoutDesignView;
    private StandardLayout layoutLiveView;

    private Button newSiteButton;
    private Button loadSiteButton;
    private Button settingsButton;
    private Button backToMenuButton1;
    private Button saveAsSiteButton;
    private Button saveSiteButton;
    private Button exportToPastebinButton;
    private Button importButton;
    private Button copyToClipboardButton;

    //16 Color Buttons
    private Button[] formattingButtons = new Button[TextFormatting.values().length - 1];

    //Formatting Buttons
    private Button resetButton;

    private ComboBox.List<String> textFormattingSelectionList;

    private Label descLabel;
    private Label autoSaveLabel;

    private CheckBox autoSaveOnCheckBox;
    private CheckBox autoSaveOffCheckBox;
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

        settingsButton = new Button(250, 105, 75, 16, "Settings", Icons.WRENCH);
        settingsButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                this.setCurrentLayout(layoutSettings);
            }
        });
        layoutMain.addComponent(settingsButton);

        descLabel = new Label("A Site Builder For GitWeb", 43, 150);
        layoutMain.addComponent(descLabel);

        this.setCurrentLayout(layoutMain);
        /*----------------------------------------------------------------------------------------------------------------------------------------*/

        layoutSettings = new StandardLayout("Settings", 363, 165, this, layoutMain){
            @Override
            protected void handleUnload() {
                super.handleUnload();
            }
        };
        layoutSettings.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
        {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
            gui.drawRect(x, y + 21, x + width, y + 164, Color.GRAY.getRGB());
        });

        int autoSaveX = 10;
        int autoSaveY = 30;
        autoSaveLabel = new Label("\u00A7n\u00A7lAuto Save", autoSaveX, autoSaveY);
        layoutSettings.addComponent(autoSaveLabel);
        RadioGroup autoSaveToggle = new RadioGroup();

        autoSaveOnCheckBox = new CheckBox("On", autoSaveX, autoSaveY + 15);
        autoSaveOnCheckBox.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                autoSave = true;
                markDirty();
            }
        });
        autoSaveOnCheckBox.setRadioGroup(autoSaveToggle);
        layoutSettings.addComponent(autoSaveOnCheckBox);

        autoSaveOffCheckBox = new CheckBox("Off", autoSaveX + 30, autoSaveY + 15);
        autoSaveOffCheckBox.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                autoSave = false;
                markDirty();
            }
        });
        autoSaveOffCheckBox.setRadioGroup(autoSaveToggle);
        layoutSettings.addComponent(autoSaveOffCheckBox);


        /*----------------------------------------------------------------------------------------------------------------------------------------*/

        layoutCodeView = new StandardLayout("Code View", 363, 165, this, null);
        layoutCodeView.setIcon(Icons.EARTH);

        backToMenuButton1 = new Button(100, 2, Icons.ARROW_LEFT);
        backToMenuButton1.setToolTip("Back To Menu", "Will take you back to the main menu");
        backToMenuButton1.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {

                //Todo add function to check if file is saved when back button is pressed and if not saved see if user would like to save

                if (currentFile == null) {

                    if (siteBuilderTextArea.getText().isEmpty()) {
                        this.setCurrentLayout(layoutMain);
                    } else {
                        Dialog.Confirmation saveCheckDialog = new Dialog.Confirmation("You have not saved your site, would you like to save?");
                        saveCheckDialog.setPositiveText("Yes");
                        saveCheckDialog.setNegativeText("No");
                        this.openDialog(saveCheckDialog);
                        saveCheckDialog.setPositiveListener((mouseX1, mouseY1, mouseButton1) -> {
                            if (mouseButton1 == 0) {
                                NBTTagCompound data = new NBTTagCompound();
                                data.setString("content", siteBuilderTextArea.getText());
                                Dialog.SaveFile saveDialog = new Dialog.SaveFile(this, data);
                                this.openDialog(saveDialog);
                                saveDialog.setResponseHandler((success, file) -> {
                                    siteBuilderTextArea.clear();
                                    this.setCurrentLayout(layoutMain);
                                    return true;
                                });
                            }
                        });
                        saveCheckDialog.setNegativeListener((mouseX1, mouseY1, mouseButton1) -> {
                            System.out.println("Tada");
                            if (mouseButton1 == 0) {

                                this.setCurrentLayout(layoutMain);
                            }
                        });
                    }
                } else {
                    if (currentFile.getData().getString("content").equals(siteBuilderTextArea.getText())) {
                        siteBuilderTextArea.clear();
                        this.setCurrentLayout(layoutMain);
                        currentFile = null;
                    } else {
                        Dialog.Confirmation saveCheckDialog = new Dialog.Confirmation("You have unsaved changes, would you like to save?");
                        this.openDialog(saveCheckDialog);
                        saveCheckDialog.setPositiveListener((mouseX1, mouseY1, mouseButton1) -> {
                            if (mouseButton1 == 0) {
                                NBTTagCompound data = new NBTTagCompound();
                                data.setString("content", siteBuilderTextArea.getText());
                                currentFile.setData(data, (v, success) -> {
                                });
                                siteBuilderTextArea.clear();
                                this.setCurrentLayout(layoutMain);
                                currentFile = null;
                            }
                        });
                        saveCheckDialog.setNegativeListener((mouseX2, mouseY2, mouseButton2) -> {
                            if (mouseButton2 == 0) {
                                saveCheckDialog.close();
                                siteBuilderTextArea.clear();
                                this.setCurrentLayout(layoutMain);
                                currentFile = null;
                            }
                        });
                    }
                }
            }
        });
        layoutCodeView.addComponent(backToMenuButton1);

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

        importButton = new Button(172, 2, Icons.IMPORT);
        importButton.setToolTip("Import", "Import an existing site into GitWeb Builder");
        importButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {

                Dialog.Input importDialog = new Dialog.Input("Insert URL to the raw text of the site");
                importDialog.setTitle("Import Site");
                importDialog.setPositiveText("Import");
                this.openDialog(importDialog);
                importDialog.setResponseHandler((success, s) -> {
                    if (success) {

                        OnlineRequest.getInstance().make(importDialog.getTextFieldInput().getText().toString(), (success1, response) -> {
                            if (success1) {
                                siteBuilderTextArea.setText(unrenderFormatting(response));
                            }
                        });
                        importDialog.close();
                    }
                    return false;
                });


            }
        });
        layoutCodeView.addComponent(importButton);

        copyToClipboardButton = new Button(190, 2, Icons.COPY);
        copyToClipboardButton.setToolTip("Copy to Clipboard", "Copy's code to clipboard with correct formatting for GitWeb");
        copyToClipboardButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                StringSelection code = new StringSelection(siteBuilderTextArea.getText().replace("\n\n", "\n"));
                clipboard.setContents(code, null);
                TaskManager.sendTask(new TaskNotificationCopiedCode());
            }
        });
        layoutCodeView.addComponent(copyToClipboardButton);

        RadioGroup viewGroup = new RadioGroup();

        codeViewCheckBox = new CheckBox("Code", 240, 5);
        codeViewCheckBox.setSelected(true);
        codeViewCheckBox.setRadioGroup(viewGroup);
        codeViewCheckBox.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {

                this.setCurrentLayout(layoutCodeView);

            }
        });
        layoutCodeView.addComponent(codeViewCheckBox);

        designViewCheckBox = new CheckBox("Design", 280, 5);
        designViewCheckBox.setRadioGroup(viewGroup);
        designViewCheckBox.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {

                this.setCurrentLayout(layoutDesignView);

            }
        });
        layoutCodeView.addComponent(designViewCheckBox);

        liveViewCheckBox = new CheckBox("Live", 327, 5);
        liveViewCheckBox.setRadioGroup(viewGroup);
        liveViewCheckBox.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                liveGitWebFrame.loadRaw(renderFormatting(siteBuilderTextArea.getText()));
                this.setCurrentLayout(layoutLiveView);
            }
        });
        layoutCodeView.addComponent(liveViewCheckBox);

        siteBuilderTextArea = new TextArea(0, 21, layoutCodeView.width - 75, layoutCodeView.height - 22);
        //siteBuilderTextArea.setHighlight(CODE_HIGHLIGHT);
        layoutCodeView.addComponent(siteBuilderTextArea);

        for (int i = 0; i < formattingButtons.length; i++) {
            int x = 290 + (i % 4) * 18;
            int y = 39 + (i / 4) * 18;
            final int index = i;
            Button button = new Button(x, y, 16, 16, TextFormatting.values()[i] + "A");
            button.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {
                    siteBuilderTextArea.writeText("&" + TextFormatting.values()[index].toString().substring(1));
                    siteBuilderTextArea.setFocused(true);
                }
            });
            layoutCodeView.addComponent(button);
            formattingButtons[i] = button;
        }

        String[] formattingType = new String[]{"Formatting", "Modules"};
        textFormattingSelectionList = new ComboBox.List<>(290, 23, 70, formattingType);
        textFormattingSelectionList.setChangeListener((oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                if (newValue.equals("Formatting")) {
                    for (Button formattingButton : formattingButtons) {
                        formattingButton.setVisible(true);
                    }
                    resetButton.setVisible(true);
                } else if (newValue.equals("Modules")) {
                    for (Button formattingButton : formattingButtons) {
                        formattingButton.setVisible(false);
                    }
                    resetButton.setVisible(false);
                }
            }
        });
        layoutCodeView.addComponent(textFormattingSelectionList);

        resetButton = new Button(308, 129, 52, 16, "Reset");
        resetButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&r");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(resetButton);

        //Todo Add search for code

        /*---------------------------------------------------------------------------------------------------------------*/

        layoutDesignView = new StandardLayout("Design View", 363, 165, this, null);
        layoutDesignView.setIcon(Icons.EDIT);
        layoutDesignView.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
        {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
            gui.drawRect(x, y + 21, x + width, y + 164, Color.GRAY.getRGB());
        });

        layoutDesignView.addComponent(codeViewCheckBox);
        layoutDesignView.addComponent(designViewCheckBox);
        layoutDesignView.addComponent(liveViewCheckBox);


        /*---------------------------------------------------------------------------------------------------------------*/

        layoutLiveView = new StandardLayout("Live View", 363, 165, this, null);
        layoutLiveView.setIcon(Icons.PLAY);
        layoutLiveView.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
        {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
            gui.drawRect(x, y + 21, x + width, y + 164, Color.GRAY.getRGB());
        });

        layoutLiveView.addComponent(codeViewCheckBox);
        layoutLiveView.addComponent(designViewCheckBox);
        layoutLiveView.addComponent(liveViewCheckBox);

        liveGitWebFrame = new GitWebFrame(this, 0, 21, layoutCodeView.width, layoutCodeView.height - 22);
        layoutLiveView.addComponent(liveGitWebFrame);

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


        if (autoSave) {
            if (this.getCurrentLayout().equals(layoutCodeView) || this.getCurrentLayout().equals(layoutDesignView)) {
                if (currentFile != null) {
                    if (tickCounter == 100) {
                        NBTTagCompound data = new NBTTagCompound();
                        data.setString("content", siteBuilderTextArea.getText());
                        currentFile.setData(data, (v, success) -> {});
                        tickCounter = 0;
                    }
                    tickCounter++;
                }
            }
        }
    }

    public void createPastebin(String title, String code) {
        String apikey = "12bfae53f22fc3d7bd73eb515f5a147f";
        String option = "paste";
        try {
            URL url = new URL("https://pastebin.com/api/api_post.php");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            writer.write("api_dev_key=" + apikey + "&api_option=" + option + "&api_paste_code=" + code + "&api_paste_name=" + title + "&api_paste_private=" + "0");
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


    public String renderFormatting(String content) {

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

        if (this.getCurrentLayout().equals(layoutCodeView)) {
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

        if (code == Keyboard.KEY_DELETE) {
            siteBuilderTextArea.moveCursorRight(1);
            siteBuilderTextArea.performBackspace();
        }*/

        if (this.getCurrentLayout().equals(layoutCodeView) || this.getCurrentLayout().equals(layoutDesignView) || this.getCurrentLayout().equals(layoutLiveView)) {
            if (GuiScreen.isCtrlKeyDown()) {
                if (code == Keyboard.KEY_LEFT) {
                    if (this.getCurrentLayout().equals(layoutCodeView)) {
                        this.setCurrentLayout(layoutLiveView);
                        codeViewCheckBox.setSelected(false);
                        designViewCheckBox.setSelected(false);
                        liveViewCheckBox.setSelected(true);
                    } else if (this.getCurrentLayout().equals(layoutDesignView)) {
                        this.setCurrentLayout(layoutCodeView);
                        codeViewCheckBox.setSelected(true);
                        designViewCheckBox.setSelected(false);
                        liveViewCheckBox.setSelected(false);
                    } else if (this.getCurrentLayout().equals(layoutLiveView)) {
                        this.setCurrentLayout(layoutDesignView);
                        codeViewCheckBox.setSelected(false);
                        designViewCheckBox.setSelected(true);
                        liveViewCheckBox.setSelected(false);
                    }
                }

                if (code == Keyboard.KEY_RIGHT) {
                    if (this.getCurrentLayout().equals(layoutCodeView)) {
                        this.setCurrentLayout(layoutDesignView);
                        codeViewCheckBox.setSelected(false);
                        designViewCheckBox.setSelected(true);
                        liveViewCheckBox.setSelected(false);
                    } else if (this.getCurrentLayout().equals(layoutDesignView)) {
                        this.setCurrentLayout(layoutLiveView);
                        codeViewCheckBox.setSelected(false);
                        designViewCheckBox.setSelected(false);
                        liveViewCheckBox.setSelected(true);
                    } else if (this.getCurrentLayout().equals(layoutLiveView)) {
                        this.setCurrentLayout(layoutCodeView);
                        codeViewCheckBox.setSelected(true);
                        designViewCheckBox.setSelected(false);
                        liveViewCheckBox.setSelected(false);
                    }
                }

            }
        }


    }

    @Override
    public boolean handleFile(File file) {

        if (!PREDICATE_FILE_SITE.test(file))
            return false;

        currentFile = file;

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

    private static <T extends Object> T[] asArray(T... t) {
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

        if (tagCompound.hasKey("autoSave", Constants.NBT.TAG_BYTE)) {
            autoSave = tagCompound.getBoolean("autoSave");
        }

        if (autoSave) {
            autoSaveOnCheckBox.setSelected(true);
        } else {
            autoSaveOffCheckBox.setSelected(true);
        }

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

        tagCompound.setBoolean("autoSave", autoSave);

    }
}
