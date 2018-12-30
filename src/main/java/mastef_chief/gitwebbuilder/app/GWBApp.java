package mastef_chief.gitwebbuilder.app;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Predicate;
import java.util.regex.Matcher;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Notification;
import com.mrcrayfish.device.api.app.ScrollableLayout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.CheckBox;
import com.mrcrayfish.device.api.app.component.ComboBox;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.RadioGroup;
import com.mrcrayfish.device.api.app.component.TextArea;
import com.mrcrayfish.device.api.app.interfaces.IHighlight;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.utils.OnlineRequest;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.programs.gitweb.component.GitWebFrame;
import com.mrcrayfish.device.programs.system.layout.StandardLayout;

import mastef_chief.gitwebbuilder.Reference;
import mastef_chief.gitwebbuilder.app.components.MenuButton;
import mastef_chief.gitwebbuilder.app.components.ModuleCreatorDialog;
import mastef_chief.gitwebbuilder.app.components.PasteBinCompleteDialog;
import mastef_chief.gitwebbuilder.app.models.GWBLogoModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;

public class GWBApp extends Application {

    // TODO module dialog, scrollable layout for design view, custom layouts which take in module instance, which type of module

    private static final Predicate<File> PREDICATE_FILE_SITE = file -> !file.isFolder() && file.getData().hasKey("content", Constants.NBT.TAG_STRING);

    private static TextFormatting[] carriedFormatting = new TextFormatting[0];
    public static final IHighlight CODE_HIGHLIGHT = text -> {
        if (text.equals("\n")) {
            carriedFormatting = new TextFormatting[0];
        }

        if (text.startsWith("#")) {
            carriedFormatting = asArray(TextFormatting.GREEN);
        }

        if (text.equals("{")) {
            carriedFormatting = asArray(TextFormatting.AQUA);
            return asArray(TextFormatting.GOLD);
        }

        if (text.equals(",")) {
            return asArray(TextFormatting.GOLD);
        }

        if (text.equals("}")) {
            carriedFormatting = new TextFormatting[0];
            return asArray(TextFormatting.GOLD);
        }

        if (text.startsWith("http")) {
            carriedFormatting = asArray(TextFormatting.ITALIC, TextFormatting.AQUA, TextFormatting.UNDERLINE);
        }

        if (text.equals("=")) {
            return asArray(TextFormatting.GOLD);
        }

        return carriedFormatting;
    };

    Minecraft mc = Minecraft.getMinecraft();

    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Clipboard clipboard = toolkit.getSystemClipboard();

    private GitWebFrame liveGitWebFrame;

    private float rotationCounter = 0;
    private int tickCounter = 0;

    // TODO work on save method to enable and disable save button
    private String savedData;
    private boolean autoSave = true;

    private File currentFile;

    private StandardLayout layoutMain;
    private StandardLayout layoutSettings;
    private StandardLayout layoutCodeView;
    private StandardLayout layoutDesignView;
    private StandardLayout layoutLiveView;

    private ScrollableLayout moduleSelctionLayout;

    private MenuButton newSiteButton;
    private MenuButton loadSiteButton;
    private MenuButton settingsButton;
    private MenuButton loadSiteFromListButton;
    private Button backToMenuButton1;
    private Button saveAsSiteButton;
    private Button saveSiteButton;
    private Button exportToPastebinButton;
    private Button importButton;
    private Button copyToClipboardButton;

    // Module Buttons
    private Button paragraphModuleButton;
    private Button navigationModuleButton;
    private Button brewingModuleButton;
    private Button downloadModuleButton;
    private Button furnaceModuleButton;
    private Button footerModuleButton;
    private Button dividerModuleButton;
    private Button craftingModuleButton;
    private Button anvilModuleButton;
    private Button headerModuleButton;
    private Button bannerModuleButton;

    // 16 Color Buttons
    private Button[] formattingButtons = new Button[TextFormatting.values().length - 1];

    // Formatting Buttons
    private Button resetButton;

    private ComboBox.List<String> textFormattingSelectionList;

    private ItemList<GWBApp.Sites> sitesList;

    private Label recentSitesLabel;
    private Label autoSaveLabel;

    private CheckBox autoSaveOnCheckBox;
    private CheckBox autoSaveOffCheckBox;
    private CheckBox codeViewCheckBox;
    private CheckBox designViewCheckBox;
    private CheckBox liveViewCheckBox;

    private TextArea siteBuilderTextArea;

    private GWBLogoModel gwbLogoModel = new GWBLogoModel();

    private static final ResourceLocation LOGO = new ResourceLocation(Reference.MOD_ID, "textures/app/gui/logo.png");
    private static final ResourceLocation UD = new ResourceLocation(Reference.MOD_ID, "textures/app/gui/ud.png");

    /**
     * The default initialization method. Clears any components in the default layout and sets it as the current layout. If you override this method and are using the default layout, make sure you call it using <code>super.init(x, y)</code>
     * <p>
     * The parameters passed are the x and y location of the top left corner or your application window.
     */
    @Override
    public void init(@Nullable NBTTagCompound nbtTagCompound) {

        /*--------------------------------------------------------------------------*/
        layoutMain = new StandardLayout("Menu", 363, 165, this, null);
        layoutMain.setIcon(Icons.HOME);
        layoutMain.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
            gui.drawRect(x, y + 21, x + width, y + 164, Color.GRAY.getRGB());
        });

        layoutMain.setInitListener(() -> {
            sitesList.getItems().clear();
            FileSystem.getApplicationFolder(this, (folder, success) -> {
                if (success) {
                    folder.search(file -> file.isForApplication(this)).forEach(file -> {
                        sitesList.addItem(Sites.fromFile(file));
                    });
                } else {
                    this.openDialog(new Dialog.Message("Error creating app directory"));
                }
            });
        });

        newSiteButton = new MenuButton(160, 45, 75, 16, "New Site", Icons.NEW_FILE);
        newSiteButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                this.setCurrentLayout(layoutCodeView);
                siteBuilderTextArea.setFocused(true);
            }
        });

        layoutMain.addComponent(newSiteButton);
        loadSiteButton = new MenuButton(185, 85, 75, 16, "Load Site", Icons.LOAD);
        loadSiteButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                Dialog.OpenFile openDialog = new Dialog.OpenFile(this);
                openDialog.setFilter(this);
                openDialog.setResponseHandler((success, file) -> {
                    if (file.isForApplication(this)) {
                        NBTTagCompound data = file.getData();
                        siteBuilderTextArea.setText(data.getString("content"));
                        currentFile = file;
                        // TODO Testing Code
                        /*
                         * saveSiteButton.setEnabled(false); savedData = siteBuilderTextArea.getText();
                         */
                        this.setCurrentLayout(layoutCodeView);
                        return true;
                    }
                    return false;
                });
                this.openDialog(openDialog);
            }
        });

        layoutMain.addComponent(loadSiteButton);

        settingsButton = new MenuButton(210, 125, 75, 16, "Settings", Icons.WRENCH);
        settingsButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                // this.setCurrentLayout(layoutSettings);
            }
        });
        layoutMain.addComponent(settingsButton);

        recentSitesLabel = new Label("\u00A7n\u00A7lRecent Sites", 273, 29);
        layoutMain.addComponent(recentSitesLabel);

        sitesList = new ItemList(265, 40, 90, 5);
        sitesList.setItemClickListener((e, index, mouseButton) -> {
            if (mouseButton == 0) {
                loadSiteFromListButton.setEnabled(true);
            }
        });
        layoutMain.addComponent(sitesList);

        loadSiteFromListButton = new MenuButton(265, 110, 90, 16, "Load", Icons.LOAD);
        loadSiteFromListButton.setEnabled(false);
        loadSiteFromListButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                if (sitesList.getSelectedIndex() != -1) {
                    Sites sites = sitesList.getSelectedItem();
                    siteBuilderTextArea.setText(sites.getContent().replace("\n\n", "\n"));
                    currentFile = sites.getSource();
                    setCurrentLayout(layoutCodeView);
                }
            }
        });
        layoutMain.addComponent(loadSiteFromListButton);

        this.setCurrentLayout(layoutMain);
        /*----------------------------------------------------------------------------------------------------------------------------------------*/

        layoutSettings = new StandardLayout("Settings", 363, 165, this, layoutMain) {
            @Override
            protected void handleUnload() {
                super.handleUnload();
            }
        };
        layoutSettings.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
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
                            if (mouseButton1 == 0) {
                                siteBuilderTextArea.clear();
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
                saveDialog.setFolder(getApplicationFolderPath());
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
                    saveDialog.setFolder(getApplicationFolderPath());
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
                exportDialog.setResponseHandler((success, v) -> {
                    if (success) {
                        createPastebin(exportDialog.getTextFieldInput().getText(), siteBuilderTextArea.getText().replace("\n\n", "\n").replace("&", "%26"));
                    }
                    return true;
                });
            }
        });
        layoutCodeView.addComponent(exportToPastebinButton);

        importButton = new Button(172, 2, Icons.IMPORT);
        importButton.setToolTip("Import", "Import an existing Gitweb site into GitWeb Builder");
        importButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {

                Dialog.Input importDialog = new Dialog.Input("Insert URL to the raw text of the site");
                importDialog.setTitle("Import Site");
                importDialog.setPositiveText("Import");
                this.openDialog(importDialog);
                importDialog.setResponseHandler((success, s) -> {
                    if (success) {
                        return getWebsite(importDialog.getTextFieldInput().getText());
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
                MrCrayfishDeviceMod.proxy.showNotification(new Notification(Icons.COPY, TextFormatting.BOLD + "Copied", "Code To Clipboard").toTag());
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
                try {
                    liveGitWebFrame.loadRaw(renderFormatting(siteBuilderTextArea.getText()));
                } catch (Throwable e) {
                    this.openDialog(new Dialog.Message(TextFormatting.RED + "Error \n" + TextFormatting.RESET + e.getLocalizedMessage() + "\nCheck logs for more info."));
                    e.printStackTrace();
                }

                this.setCurrentLayout(layoutLiveView);

            }
        });
        layoutCodeView.addComponent(liveViewCheckBox);

        siteBuilderTextArea = new TextArea(0, 21, layoutCodeView.width - 75, layoutCodeView.height - 22);
        siteBuilderTextArea.setHighlight(CODE_HIGHLIGHT);
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

        String[] formattingType = new String[] { "Formatting", "Modules" };
        textFormattingSelectionList = new ComboBox.List<>(290, 23, 70, formattingType);
        textFormattingSelectionList.setChangeListener((oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                if (newValue.equals("Formatting")) {
                    for (Button formattingButton : formattingButtons) {
                        formattingButton.setVisible(true);
                    }
                    resetButton.setVisible(true);
                    moduleSelctionLayout.setVisible(false);
                } else if (newValue.equals("Modules")) {
                    for (Button formattingButton : formattingButtons) {
                        formattingButton.setVisible(false);
                    }
                    resetButton.setVisible(false);
                    moduleSelctionLayout.setVisible(true);
                    moduleSelctionLayout.resetScroll();
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

        moduleSelctionLayout = new ScrollableLayout(290, 39, 70, 200, 123);
        moduleSelctionLayout.setVisible(false);
        moduleSelctionLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
            gui.drawRect(x, y, x + 100, y + 300, Color.gray.getRGB());
        });
        layoutCodeView.addComponent(moduleSelctionLayout);

        paragraphModuleButton = new Button(1, 1, 62, 16, "Paragraph");
        paragraphModuleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                ModuleCreatorDialog moduleCreatorDialog = new ModuleCreatorDialog(EnumModuleType.PARAGRAPH, siteBuilderTextArea, this);
                this.openDialog(moduleCreatorDialog);
            }
        });
        moduleSelctionLayout.addComponent(paragraphModuleButton);

        navigationModuleButton = new Button(1, 19, 62, 16, "Navigation");
        navigationModuleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                ModuleCreatorDialog moduleCreatorDialog = new ModuleCreatorDialog(EnumModuleType.NAVIGATION, siteBuilderTextArea, this);
                this.openDialog(moduleCreatorDialog);
            }
        });
        moduleSelctionLayout.addComponent(navigationModuleButton);

        brewingModuleButton = new Button(1, 37, 62, 16, "Brewing");
        brewingModuleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                ModuleCreatorDialog moduleCreatorDialog = new ModuleCreatorDialog(EnumModuleType.BREWING, siteBuilderTextArea, this);
                this.openDialog(moduleCreatorDialog);
            }
        });
        moduleSelctionLayout.addComponent(brewingModuleButton);

        downloadModuleButton = new Button(1, 55, 62, 16, "Download");
        downloadModuleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                ModuleCreatorDialog moduleCreatorDialog = new ModuleCreatorDialog(EnumModuleType.DOWNLOAD, siteBuilderTextArea, this);
                this.openDialog(moduleCreatorDialog);
            }
        });
        moduleSelctionLayout.addComponent(downloadModuleButton);

        furnaceModuleButton = new Button(1, 73, 62, 16, "Furnace");
        furnaceModuleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                ModuleCreatorDialog moduleCreatorDialog = new ModuleCreatorDialog(EnumModuleType.FURNACE, siteBuilderTextArea, this);
                this.openDialog(moduleCreatorDialog);
            }
        });
        moduleSelctionLayout.addComponent(furnaceModuleButton);

        footerModuleButton = new Button(1, 91, 62, 16, "Footer");
        footerModuleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                ModuleCreatorDialog moduleCreatorDialog = new ModuleCreatorDialog(EnumModuleType.FOOTER, siteBuilderTextArea, this);
                this.openDialog(moduleCreatorDialog);
            }
        });
        moduleSelctionLayout.addComponent(footerModuleButton);

        dividerModuleButton = new Button(1, 109, 62, 16, "Divider");
        dividerModuleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                ModuleCreatorDialog moduleCreatorDialog = new ModuleCreatorDialog(EnumModuleType.DIVIDER, siteBuilderTextArea, this);
                this.openDialog(moduleCreatorDialog);
            }
        });
        moduleSelctionLayout.addComponent(dividerModuleButton);

        craftingModuleButton = new Button(1, 127, 62, 16, "Crafting");
        craftingModuleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                ModuleCreatorDialog moduleCreatorDialog = new ModuleCreatorDialog(EnumModuleType.CRAFTING, siteBuilderTextArea, this);
                this.openDialog(moduleCreatorDialog);
            }
        });
        moduleSelctionLayout.addComponent(craftingModuleButton);

        anvilModuleButton = new Button(1, 145, 62, 16, "Anvil");
        anvilModuleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                ModuleCreatorDialog moduleCreatorDialog = new ModuleCreatorDialog(EnumModuleType.ANVIL, siteBuilderTextArea, this);
                this.openDialog(moduleCreatorDialog);
            }
        });
        moduleSelctionLayout.addComponent(anvilModuleButton);

        headerModuleButton = new Button(1, 163, 62, 16, "Header");
        headerModuleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                ModuleCreatorDialog moduleCreatorDialog = new ModuleCreatorDialog(EnumModuleType.HEADER, siteBuilderTextArea, this);
                this.openDialog(moduleCreatorDialog);
            }
        });
        moduleSelctionLayout.addComponent(headerModuleButton);

        bannerModuleButton = new Button(1, 181, 62, 16, "Banner");
        bannerModuleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                ModuleCreatorDialog moduleCreatorDialog = new ModuleCreatorDialog(EnumModuleType.BANNER, siteBuilderTextArea, this);
                this.openDialog(moduleCreatorDialog);
            }
        });
        moduleSelctionLayout.addComponent(bannerModuleButton);

        // TODO Add search for code

        /*---------------------------------------------------------------------------------------------------------------*/

        layoutDesignView = new StandardLayout("Design View", 363, 165, this, null);
        layoutDesignView.setIcon(Icons.EDIT);
        layoutDesignView.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
            gui.drawRect(x, y + 21, x + width, y + 164, Color.darkGray.getRGB());
        });

        layoutDesignView.addComponent(codeViewCheckBox);
        layoutDesignView.addComponent(designViewCheckBox);
        layoutDesignView.addComponent(liveViewCheckBox);

        // TODO work on design view

        /*---------------------------------------------------------------------------------------------------------------*/

        layoutLiveView = new StandardLayout("Live View", 363, 165, this, null);
        layoutLiveView.setIcon(Icons.PLAY);
        layoutLiveView.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
            gui.drawRect(x, y + 21, x + width, y + 164, Color.GRAY.getRGB());
        });

        layoutLiveView.addComponent(codeViewCheckBox);
        layoutLiveView.addComponent(designViewCheckBox);
        layoutLiveView.addComponent(liveViewCheckBox);

        liveGitWebFrame = new GitWebFrame(this, 0, 21, layoutLiveView.width, layoutLiveView.height - 22);

        layoutLiveView.addComponent(liveGitWebFrame);
    }

    @Override
    public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks) {

        super.render(laptop, mc, x, y, mouseX, mouseY, active, partialTicks);

        if (this.getCurrentLayout() == layoutMain) {
            GlStateManager.pushMatrix();
            {
                GlStateManager.enableDepth();
                RenderHelper.enableStandardItemLighting();
                GlStateManager.translate(x + 135, y - 43, 250);
                GlStateManager.scale((float) -8.0, (float) -8.0, (float) -8.0);
                GlStateManager.rotate(5F, 1, 0, 0);
                GlStateManager.rotate(200F, 0, 0, 1);
                GlStateManager.rotate(-rotationCounter - partialTicks, 0, 1, 0);
                mc.getTextureManager().bindTexture(LOGO);
                gwbLogoModel.render((Entity) null, 0F, 0F, 0F, 0F, 0F, 1.0F);
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableDepth();

            }
            GlStateManager.popMatrix();
        }

        if (this.getCurrentLayout() == layoutDesignView) {

            mc.getTextureManager().bindTexture(UD);
            RenderUtil.drawRectWithTexture((double) (x + 46), (double) (y + 19), 0.0F, 0.0F, 250, 145, 250, 250);

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
                        currentFile.setData(data, (v, success) -> {
                        });
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
                // new line fix
                .replace("\n\n", "\n")
                // color conversion to unicode format
                .replace("&0", "\u00A70").replace("&1", "\u00A71").replace("&2", "\u00A72").replace("&3", "\u00A73").replace("&4", "\u00A74").replace("&5", "\u00A75").replace("&6", "\u00A76").replace("&7", "\u00A77").replace("&8", "\u00A78").replace("&9", "\u00A79").replace("&a", "\u00A7a").replace("&b", "\u00A7b").replace("&c", "\u00A7c").replace("&d", "\u00A7d").replace("&e", "\u00A7e").replace("&f", "\u00A7f")
                // Formatting conversion to unicode format
                .replace("&k", "\u00A7k").replace("&l", "\u00A7l").replace("&m", "\u00A7m").replace("&n", "\u00A7n").replace("&o", "\u00A7o").replace("&r", "\u00A7r");

    }

    @Override
    public void handleKeyTyped(char character, int code) {
        super.handleKeyTyped(character, code);

        if (this.getCurrentLayout().equals(layoutCodeView)) {
            if (GuiScreen.isCtrlKeyDown() && code == Keyboard.KEY_S) {

                if (currentFile != null) {
                    NBTTagCompound data = new NBTTagCompound();
                    data.setString("content", siteBuilderTextArea.getText());
                    currentFile.setData(data, (file, success) -> {
                        if (success) {
                        }
                    });
                } else {
                    NBTTagCompound data = new NBTTagCompound();
                    data.setString("content", siteBuilderTextArea.getText());

                    Dialog.SaveFile saveDialog = new Dialog.SaveFile(this, data);
                    saveDialog.setFolder(getApplicationFolderPath());
                    saveDialog.setResponseHandler((success, file) -> {
                        if (success) {
                            currentFile = file;
                        }
                        return true;
                    });
                    this.openDialog(saveDialog);
                }
            }
            if (GuiScreen.isCtrlKeyDown() && GuiScreen.isShiftKeyDown() && code == Keyboard.KEY_S) {

                NBTTagCompound data = new NBTTagCompound();
                data.setString("content", siteBuilderTextArea.getText());

                Dialog.SaveFile saveDialog = new Dialog.SaveFile(this, data);
                saveDialog.setFolder(getApplicationFolderPath());
                saveDialog.setResponseHandler((success, file) -> {
                    if (success) {
                        currentFile = file;
                    }
                    return true;
                });
                this.openDialog(saveDialog);
            }
        }

        /*
         * if (code == Keyboard.KEY_DELETE) { siteBuilderTextArea.moveCursorRight(1); siteBuilderTextArea.performBackspace(); }
         */

        if (this.getCurrentLayout().equals(layoutCodeView) || this.getCurrentLayout().equals(layoutDesignView) || this.getCurrentLayout().equals(layoutLiveView)) {
            if (GuiScreen.isCtrlKeyDown()) {
                if (code == Keyboard.KEY_LEFT) {
                    if (this.getCurrentLayout().equals(layoutCodeView)) {
                        liveGitWebFrame.loadRaw(renderFormatting(siteBuilderTextArea.getText()));
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
                        liveGitWebFrame.loadRaw(renderFormatting(siteBuilderTextArea.getText()));
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
        siteBuilderTextArea.setText(data.getString("content").replace("\n\n", "\n").replace("&nl", "\n"));
        this.setCurrentLayout(layoutCodeView);

        return true;
    }

    @Override
    public void onClose() {
        super.onClose();

        // TODO add save on close if not saved already

        currentFile = null;
    }

    private boolean getWebsite(String website) {

        Matcher matcher = GitWebFrame.PATTERN_LINK.matcher(website);

        if (matcher.matches()) {
            String domain = matcher.group("domain");
            String extension = matcher.group("extension");
            String directory = matcher.group("directory");
            String url;

            if (directory == null) {
                url = "https://raw.githubusercontent.com/MrCrayfish/GitWeb-Sites/master/" + extension + "/" + domain + "/index";
            } else {
                if (directory.endsWith("/")) {
                    directory = directory.substring(0, directory.length() - 1);
                }
                url = "https://raw.githubusercontent.com/MrCrayfish/GitWeb-Sites/master/" + extension + "/" + domain + directory + "/index";
            }

            this.getCode(url);
            return true;
        }
        return false;
    }

    private void getCode(String url) {
        OnlineRequest.getInstance().make(url, (success, response) -> {
            if (success && !(response.startsWith("404"))) {
                this.siteBuilderTextArea.setText(response.replace("\n\n", "\n").replace("&nl", "\n"));
            } else {
                Dialog dialog = new Dialog.Message(response);
                dialog.setTitle(url);
                this.openDialog(dialog);
            }
        });
    }

    private static <T extends Object> T[] asArray(T... t) {
        return t;
    }

    private static TextFormatting[] getFormatting(TextFormatting... formatting) {
        TextFormatting[] array = new TextFormatting[carriedFormatting.length + formatting.length];
        for (int i = 0; i < array.length; i++) {
            if (i < carriedFormatting.length)
                array[i] = carriedFormatting[i];
            else
                array[i] = formatting[i - carriedFormatting.length];
        }
        return array;
    }

    @Override
    public void load(NBTTagCompound tagCompound) {
        autoSave = tagCompound.getBoolean("autoSave");
    }

    @Override
    public void save(NBTTagCompound tagCompound) {
        tagCompound.setBoolean("autoSave", autoSave);
    }

    private static class Sites {
        private File source;
        private String fileName;
        private String content;

        public Sites(String fileName, String content) {
            this.fileName = fileName;
            this.content = content;
        }

        public File getSource() {
            return this.source;
        }

        public String getFileName() {
            return this.fileName;
        }

        public String getContent() {
            return this.content;
        }

        public String toString() {
            return this.fileName;
        }

        public static Sites fromFile(File file) {
            Sites note = new Sites(file.getName(), file.getData().getString("content"));
            note.source = file;
            return note;
        }
    }

}
