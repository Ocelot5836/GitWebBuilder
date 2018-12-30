package mastef_chief.gitwebbuilder.app.components;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.Alphabet;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.IIcon;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.ScrollableLayout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.Slider;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.api.app.component.TextArea;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.SlideListener;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.object.ColorGrid;
import com.mrcrayfish.device.programs.ApplicationIcons;
import com.mrcrayfish.device.programs.gitweb.component.GitWebFrame;

import mastef_chief.gitwebbuilder.app.EnumModuleType;
import mastef_chief.gitwebbuilder.app.components.ComponentInventory.InventorySlot;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class ModuleCreatorDialog extends Dialog {

    private static final int DIVIDE_WIDTH = 15;

    private Application application;

    private EnumModuleType selectedModule = null;
    private String positiveText = "Create";
    private String negativeText = "Cancel";

    private Color setColor = Color.darkGray;

    private ResponseHandler<String> responseListener;

    private Button buttonPositive;
    private Button buttonNegative;

    private Component colorDisplay;

    private ColorGrid colorGrid;

    private Slider redSlider;
    private Slider greenSlider;
    private Slider blueSlider;

    private TextArea selectedTextArea;

    public static final int LAYOUT_WIDTH = 175;
    public static final int LAYOUT_HEIGHT = 150;

    public ModuleCreatorDialog(EnumModuleType module, TextArea textArea, Application application) {

        this.selectedModule = module;
        this.selectedTextArea = textArea;
        this.application = application;
    }

    @Override
    public void init(@Nullable NBTTagCompound nbtTagCompound) {
        super.init(nbtTagCompound);

        Layout layout = new Layout(LAYOUT_WIDTH, LAYOUT_HEIGHT);
        layout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
            gui.drawRect(x, y, x + LAYOUT_WIDTH, y + LAYOUT_HEIGHT, Color.DARK_GRAY.getRGB());
        });

        this.setTitle("Module Builder (" + selectedModule + ")");

        switch (this.selectedModule) {
        case PARAGRAPH: {
            ScrollableLayout scrollableLayout = new ScrollableLayout(0, 0, LAYOUT_WIDTH, 125, LAYOUT_HEIGHT - 25);
            scrollableLayout.setScrollSpeed(8);
            scrollableLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
                Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
                gui.drawRect(x, y, x + LAYOUT_WIDTH, y + LAYOUT_HEIGHT - 25, Color.gray.getRGB());
            });

            Label textLabel = new Label(TextFormatting.RED + "*" + TextFormatting.RESET + "Text:", 5, 8);
            scrollableLayout.addComponent(textLabel);
            TextArea textTextField = new TextArea(5, 20, 162, 35);
            scrollableLayout.addComponent(textTextField);

            Label paddingLabel = new Label("Padding (Max 178):", 5, 60);
            scrollableLayout.addComponent(paddingLabel);
            TextField paddingTextField = new TextField(5, 72, 162) {
                @Override
                public void handleKeyTyped(char character, int code) {
                    if (Character.isDigit(character) || code == Keyboard.KEY_BACK) {
                        super.handleKeyTyped(character, code);
                    }
                }
            };
            scrollableLayout.addComponent(paddingTextField);

            Label imageLabel = new Label("Image Link (Requires Valid URL):", 5, 90);
            scrollableLayout.addComponent(imageLabel);
            TextField imageTextField = new TextField(5, 102, 162);
            scrollableLayout.addComponent(imageTextField);

            layout.addComponent(scrollableLayout);

            int positiveWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(positiveText);
            buttonPositive = new Button(125, 130, positiveText) {
                @Override
                protected void handleKeyTyped(char character, int code) {
                    super.handleKeyTyped(character, code);

                    if (!textTextField.getText().isEmpty()) {
                        if (!imageTextField.getText().isEmpty()) {
                            if (IsMatch(imageTextField.getText())) {
                                buttonPositive.setEnabled(true);
                            } else {
                                buttonPositive.setEnabled(false);
                            }
                        } else {
                            buttonPositive.setEnabled(true);
                        }

                    } else {
                        buttonPositive.setEnabled(false);
                    }

                }
            };
            buttonPositive.setEnabled(false);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {

                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("#paragraph");
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("text=" + textTextField.getText().replace("\n\n", "\\n"));
                    if (!paddingTextField.getText().isEmpty()) {
                        selectedTextArea.performReturn();
                        selectedTextArea.writeText("padding=" + paddingTextField.getText());
                    }
                    if (!imageTextField.getText().isEmpty()) {
                        selectedTextArea.performReturn();
                        selectedTextArea.writeText("image=" + imageTextField.getText());

                    }
                    selectedTextArea.performReturn();
                    close();

                }
            });
            layout.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(negativeText);
            buttonNegative = new Button(75, 130, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            layout.addComponent(buttonNegative);

            Label requiredLabel = new Label(TextFormatting.RED + "*" + TextFormatting.RESET + " Required", 7, 134);
            layout.addComponent(requiredLabel);
            break;
        }
        case NAVIGATION: {
            ScrollableLayout scrollableLayout = new ScrollableLayout(0, 0, LAYOUT_WIDTH, 1080, LAYOUT_HEIGHT - 25);
            scrollableLayout.setScrollSpeed(8);
            scrollableLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
                Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
                gui.drawRect(x, y, x + LAYOUT_WIDTH, y + 1080, Color.gray.getRGB());
            });

            Text colorLabel = new Text(TextFormatting.RESET + "Menu \nColor:", 10, 15, 60);
            scrollableLayout.addComponent(colorLabel);

            Label redLabel = new Label("R ", 100, 12);
            scrollableLayout.addComponent(redLabel);
            redSlider = new Slider(110, 10, 50);
            redSlider.setSlideListener(new SlideListener() {
                @Override
                public void onSlide(float v) {
                    setColor = new Color(v, greenSlider.getPercentage(), blueSlider.getPercentage());
                }
            });
            scrollableLayout.addComponent(redSlider);

            Label greenLabel = new Label("G ", 100, 28);
            scrollableLayout.addComponent(greenLabel);
            greenSlider = new Slider(110, 26, 50);
            greenSlider.setSlideListener(new SlideListener() {
                @Override
                public void onSlide(float v) {
                    setColor = new Color(redSlider.getPercentage(), v, blueSlider.getPercentage());
                }
            });
            scrollableLayout.addComponent(greenSlider);

            Label blueLabel = new Label("B ", 100, 44);
            scrollableLayout.addComponent(blueLabel);
            blueSlider = new Slider(110, 42, 50);
            blueSlider.setSlideListener(new SlideListener() {
                @Override
                public void onSlide(float v) {
                    setColor = new Color(redSlider.getPercentage(), greenSlider.getPercentage(), v);
                }
            });
            scrollableLayout.addComponent(blueSlider);

            colorDisplay = new Component(45, 5) {
                @Override
                public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
                    drawRect(xPosition, yPosition, xPosition + 50, yPosition + 51, Color.DARK_GRAY.getRGB());
                    drawRect(xPosition + 1, yPosition + 1, xPosition + 49, yPosition + 50, setColor.getRGB());
                }
            };
            scrollableLayout.addComponent(colorDisplay);

            Label menuItemLink1Label = new Label("Menu Item Link (1):", 5, 70);
            scrollableLayout.addComponent(menuItemLink1Label);
            TextField menuItemLink1TextField = new TextField(5, 80, 162);
            scrollableLayout.addComponent(menuItemLink1TextField);

            Label menuItemLabel1Label = new Label("Menu Item Label (1):", 5, 100);
            scrollableLayout.addComponent(menuItemLabel1Label);
            TextField menuItemLabel1TextField = new TextField(5, 110, 162);
            scrollableLayout.addComponent(menuItemLabel1TextField);

            Label menuItemIcon1Label = new Label("Menu Item Icon (1):", 5, 130);
            scrollableLayout.addComponent(menuItemIcon1Label);
            Button menuItemIcon1Button = new Button(152, 147, 16, 16, "");
            scrollableLayout.addComponent(menuItemIcon1Button);
            ItemList<IIcon> menuItemIcon1List = new ItemList(5, 140, 140, 2);
            getIcons(menuItemIcon1List);
            menuItemIcon1List.setItemClickListener((e, index, mouseButton) -> {
                if (mouseButton == 0) {
                    menuItemIcon1Button.setIcon(menuItemIcon1List.getSelectedItem());
                    if (!menuItemLink1TextField.getText().isEmpty()) {
                        buttonPositive.setEnabled(true);
                    } else {
                        buttonPositive.setEnabled(false);
                    }
                }
            });
            scrollableLayout.addComponent(menuItemIcon1List);

            Label menuItemLink2Label = new Label("Menu Item Link (2):", 5, 170);
            scrollableLayout.addComponent(menuItemLink2Label);
            TextField menuItemLink2TextField = new TextField(5, 180, 162);
            scrollableLayout.addComponent(menuItemLink2TextField);

            Label menuItemLabel2Label = new Label("Menu Item Label (2):", 5, 200);
            scrollableLayout.addComponent(menuItemLabel2Label);
            TextField menuItemLabel2TextField = new TextField(5, 210, 162);
            scrollableLayout.addComponent(menuItemLabel2TextField);

            Label menuItemIcon2Label = new Label("Menu Item Icon (2):", 5, 230);
            scrollableLayout.addComponent(menuItemIcon2Label);
            Button menuItemIcon2Button = new Button(152, 247, 16, 16, "");
            scrollableLayout.addComponent(menuItemIcon2Button);
            ItemList<IIcon> menuItemIcon2List = new ItemList(5, 240, 140, 2);
            getIcons(menuItemIcon2List);
            menuItemIcon2List.setItemClickListener((e, index, mouseButton) -> {
                if (mouseButton == 0) {
                    menuItemIcon2Button.setIcon(menuItemIcon2List.getSelectedItem());
                    if (!menuItemLink2TextField.getText().isEmpty()) {
                        buttonPositive.setEnabled(true);
                    } else {
                        buttonPositive.setEnabled(false);
                    }
                }
            });
            scrollableLayout.addComponent(menuItemIcon2List);

            Label menuItemLink3Label = new Label("Menu Item Link (3):", 5, 270);
            scrollableLayout.addComponent(menuItemLink3Label);
            TextField menuItemLink3TextField = new TextField(5, 280, 162);
            scrollableLayout.addComponent(menuItemLink3TextField);

            Label menuItemLabel3Label = new Label("Menu Item Label (3):", 5, 300);
            scrollableLayout.addComponent(menuItemLabel3Label);
            TextField menuItemLabel3TextField = new TextField(5, 310, 162);
            scrollableLayout.addComponent(menuItemLabel3TextField);

            Label menuItemIcon3Label = new Label("Menu Item Icon (3):", 5, 330);
            scrollableLayout.addComponent(menuItemIcon3Label);
            Button menuItemIcon3Button = new Button(152, 347, 16, 16, "");
            scrollableLayout.addComponent(menuItemIcon3Button);
            ItemList<IIcon> menuItemIcon3List = new ItemList(5, 340, 140, 2);
            getIcons(menuItemIcon3List);
            menuItemIcon3List.setItemClickListener((e, index, mouseButton) -> {
                if (mouseButton == 0) {
                    menuItemIcon3Button.setIcon(menuItemIcon3List.getSelectedItem());
                    if (!menuItemLink3TextField.getText().isEmpty()) {
                        buttonPositive.setEnabled(true);
                    } else {
                        buttonPositive.setEnabled(false);
                    }
                }
            });
            scrollableLayout.addComponent(menuItemIcon3List);

            Label menuItemLink4Label = new Label("Menu Item Link (4):", 5, 370);
            scrollableLayout.addComponent(menuItemLink4Label);
            TextField menuItemLink4TextField = new TextField(5, 380, 162);
            scrollableLayout.addComponent(menuItemLink4TextField);

            Label menuItemLabel4Label = new Label("Menu Item Label (4):", 5, 400);
            scrollableLayout.addComponent(menuItemLabel4Label);
            TextField menuItemLabel4TextField = new TextField(5, 410, 162);
            scrollableLayout.addComponent(menuItemLabel4TextField);

            Label menuItemIcon4Label = new Label("Menu Item Icon (4):", 5, 430);
            scrollableLayout.addComponent(menuItemIcon4Label);
            Button menuItemIcon4Button = new Button(152, 447, 16, 16, "");
            scrollableLayout.addComponent(menuItemIcon4Button);
            ItemList<IIcon> menuItemIcon4List = new ItemList(5, 440, 140, 2);
            getIcons(menuItemIcon4List);
            menuItemIcon4List.setItemClickListener((e, index, mouseButton) -> {
                if (mouseButton == 0) {
                    menuItemIcon4Button.setIcon(menuItemIcon4List.getSelectedItem());
                    if (!menuItemLink4TextField.getText().isEmpty()) {
                        buttonPositive.setEnabled(true);
                    } else {
                        buttonPositive.setEnabled(false);
                    }
                }
            });
            scrollableLayout.addComponent(menuItemIcon4List);

            Label menuItemLink5Label = new Label("Menu Item Link (5):", 5, 470);
            scrollableLayout.addComponent(menuItemLink5Label);
            TextField menuItemLink5TextField = new TextField(5, 480, 162);
            scrollableLayout.addComponent(menuItemLink5TextField);

            Label menuItemLabel5Label = new Label("Menu Item Label (5):", 5, 500);
            scrollableLayout.addComponent(menuItemLabel5Label);
            TextField menuItemLabel5TextField = new TextField(5, 510, 162);
            scrollableLayout.addComponent(menuItemLabel5TextField);

            Label menuItemIcon5Label = new Label("Menu Item Icon (5):", 5, 530);
            scrollableLayout.addComponent(menuItemIcon5Label);
            Button menuItemIcon5Button = new Button(152, 547, 16, 16, "");
            scrollableLayout.addComponent(menuItemIcon5Button);
            ItemList<IIcon> menuItemIcon5List = new ItemList(5, 540, 140, 2);
            getIcons(menuItemIcon5List);
            menuItemIcon5List.setItemClickListener((e, index, mouseButton) -> {
                if (mouseButton == 0) {
                    menuItemIcon5Button.setIcon(menuItemIcon5List.getSelectedItem());
                    if (!menuItemLink5TextField.getText().isEmpty()) {
                        buttonPositive.setEnabled(true);
                    } else {
                        buttonPositive.setEnabled(false);
                    }
                }
            });
            scrollableLayout.addComponent(menuItemIcon5List);

            Label menuItemLink6Label = new Label("Menu Item Link (6):", 5, 570);
            scrollableLayout.addComponent(menuItemLink6Label);
            TextField menuItemLink6TextField = new TextField(5, 580, 162);
            scrollableLayout.addComponent(menuItemLink6TextField);

            Label menuItemLabel6Label = new Label("Menu Item Label (6):", 5, 600);
            scrollableLayout.addComponent(menuItemLabel6Label);
            TextField menuItemLabel6TextField = new TextField(5, 610, 162);
            scrollableLayout.addComponent(menuItemLabel6TextField);

            Label menuItemIcon6Label = new Label("Menu Item Icon (6):", 5, 630);
            scrollableLayout.addComponent(menuItemIcon6Label);
            Button menuItemIcon6Button = new Button(152, 647, 16, 16, "");
            scrollableLayout.addComponent(menuItemIcon6Button);
            ItemList<IIcon> menuItemIcon6List = new ItemList(5, 640, 140, 2);
            getIcons(menuItemIcon6List);
            menuItemIcon6List.setItemClickListener((e, index, mouseButton) -> {
                if (mouseButton == 0) {
                    menuItemIcon6Button.setIcon(menuItemIcon6List.getSelectedItem());
                    if (!menuItemLink6TextField.getText().isEmpty()) {
                        buttonPositive.setEnabled(true);
                    } else {
                        buttonPositive.setEnabled(false);
                    }
                }
            });
            scrollableLayout.addComponent(menuItemIcon6List);

            Label menuItemLink7Label = new Label("Menu Item Link (7):", 5, 670);
            scrollableLayout.addComponent(menuItemLink7Label);
            TextField menuItemLink7TextField = new TextField(5, 680, 162);
            scrollableLayout.addComponent(menuItemLink7TextField);

            Label menuItemLabel7Label = new Label("Menu Item Label (7):", 5, 700);
            scrollableLayout.addComponent(menuItemLabel7Label);
            TextField menuItemLabel7TextField = new TextField(5, 710, 162);
            scrollableLayout.addComponent(menuItemLabel7TextField);

            Label menuItemIcon7Label = new Label("Menu Item Icon (7):", 5, 730);
            scrollableLayout.addComponent(menuItemIcon7Label);
            Button menuItemIcon7Button = new Button(152, 747, 16, 16, "");
            scrollableLayout.addComponent(menuItemIcon7Button);
            ItemList<IIcon> menuItemIcon7List = new ItemList(5, 740, 140, 2);
            getIcons(menuItemIcon7List);
            menuItemIcon7List.setItemClickListener((e, index, mouseButton) -> {
                if (mouseButton == 0) {
                    menuItemIcon7Button.setIcon(menuItemIcon7List.getSelectedItem());
                    if (!menuItemLink7TextField.getText().isEmpty()) {
                        buttonPositive.setEnabled(true);
                    } else {
                        buttonPositive.setEnabled(false);
                    }
                }
            });
            scrollableLayout.addComponent(menuItemIcon7List);

            Label menuItemLink8Label = new Label("Menu Item Link (8):", 5, 770);
            scrollableLayout.addComponent(menuItemLink8Label);
            TextField menuItemLink8TextField = new TextField(5, 780, 162);
            scrollableLayout.addComponent(menuItemLink8TextField);

            Label menuItemLabel8Label = new Label("Menu Item Label (8):", 5, 800);
            scrollableLayout.addComponent(menuItemLabel8Label);
            TextField menuItemLabel8TextField = new TextField(5, 810, 162);
            scrollableLayout.addComponent(menuItemLabel8TextField);

            Label menuItemIcon8Label = new Label("Menu Item Icon (8):", 5, 830);
            scrollableLayout.addComponent(menuItemIcon8Label);
            Button menuItemIcon8Button = new Button(152, 847, 16, 16, "");
            scrollableLayout.addComponent(menuItemIcon8Button);
            ItemList<IIcon> menuItemIcon8List = new ItemList(5, 840, 140, 2);
            getIcons(menuItemIcon8List);
            menuItemIcon8List.setItemClickListener((e, index, mouseButton) -> {
                if (mouseButton == 0) {
                    menuItemIcon8Button.setIcon(menuItemIcon8List.getSelectedItem());
                    if (!menuItemLink8TextField.getText().isEmpty()) {
                        buttonPositive.setEnabled(true);
                    } else {
                        buttonPositive.setEnabled(false);
                    }
                }
            });
            scrollableLayout.addComponent(menuItemIcon8List);

            Label menuItemLink9Label = new Label("Menu Item Link (9):", 5, 870);
            scrollableLayout.addComponent(menuItemLink9Label);
            TextField menuItemLink9TextField = new TextField(5, 880, 162);
            scrollableLayout.addComponent(menuItemLink9TextField);

            Label menuItemLabel9Label = new Label("Menu Item Label (9):", 5, 900);
            scrollableLayout.addComponent(menuItemLabel9Label);
            TextField menuItemLabel9TextField = new TextField(5, 910, 162);
            scrollableLayout.addComponent(menuItemLabel9TextField);

            Label menuItemIcon9Label = new Label("Menu Item Icon (9):", 5, 930);
            scrollableLayout.addComponent(menuItemIcon9Label);
            Button menuItemIcon9Button = new Button(152, 947, 16, 16, "");
            scrollableLayout.addComponent(menuItemIcon9Button);
            ItemList<IIcon> menuItemIcon9List = new ItemList(5, 940, 140, 2);
            getIcons(menuItemIcon9List);
            menuItemIcon9List.setItemClickListener((e, index, mouseButton) -> {
                if (mouseButton == 0) {
                    menuItemIcon9Button.setIcon(menuItemIcon9List.getSelectedItem());
                    if (!menuItemLink9TextField.getText().isEmpty()) {
                        buttonPositive.setEnabled(true);
                    } else {
                        buttonPositive.setEnabled(false);
                    }
                }
            });
            scrollableLayout.addComponent(menuItemIcon9List);

            Label menuItemLink10Label = new Label("Menu Item Link (10):", 5, 970);
            scrollableLayout.addComponent(menuItemLink10Label);
            TextField menuItemLink10TextField = new TextField(5, 980, 162);
            scrollableLayout.addComponent(menuItemLink10TextField);

            Label menuItemLabel10Label = new Label("Menu Item Label (10):", 5, 1000);
            scrollableLayout.addComponent(menuItemLabel10Label);
            TextField menuItemLabel10TextField = new TextField(5, 1010, 162);
            scrollableLayout.addComponent(menuItemLabel10TextField);

            Label menuItemIcon10Label = new Label("Menu Item Icon (10):", 5, 1030);
            scrollableLayout.addComponent(menuItemIcon10Label);
            Button menuItemIcon10Button = new Button(152, 1047, 16, 16, "");
            scrollableLayout.addComponent(menuItemIcon10Button);
            ItemList<IIcon> menuItemIcon10List = new ItemList(5, 1040, 140, 2);
            getIcons(menuItemIcon10List);
            menuItemIcon10List.setItemClickListener((e, index, mouseButton) -> {
                if (mouseButton == 0) {
                    menuItemIcon10Button.setIcon(menuItemIcon10List.getSelectedItem());
                    if (!menuItemLink10TextField.getText().isEmpty()) {
                        buttonPositive.setEnabled(true);
                    } else {
                        buttonPositive.setEnabled(false);
                    }
                }
            });
            scrollableLayout.addComponent(menuItemIcon10List);

            layout.addComponent(scrollableLayout);

            int positiveWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(positiveText);
            buttonPositive = new Button(125, 130, positiveText) {
                @Override
                protected void handleKeyTyped(char character, int code) {
                    super.handleKeyTyped(character, code);

                    if (!menuItemLink1TextField.getText().isEmpty() && !menuItemLabel1TextField.getText().isEmpty() || menuItemIcon1List.getSelectedItem() != null) {
                        buttonPositive.setEnabled(true);
                    } else {
                        buttonPositive.setEnabled(false);
                    }

                }
            };
            buttonPositive.setEnabled(false);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("#navigation");
                    selectedTextArea.performReturn();
                    if (setColor != Color.darkGray) {
                        selectedTextArea.writeText("color=" + setColor.getRGB());
                        selectedTextArea.performReturn();
                    }
                    if (!menuItemLink1TextField.getText().isEmpty()) {
                        selectedTextArea.writeText("item-link-1=" + menuItemLink1TextField.getText());
                        selectedTextArea.performReturn();
                        if (!menuItemLabel1TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-label-1=" + menuItemLabel1TextField.getText());
                            selectedTextArea.performReturn();
                        }
                        if (menuItemIcon1List.getSelectedItem() != null) {
                            selectedTextArea.writeText("item-icon-1=" + menuItemIcon1List.getSelectedItem());
                            selectedTextArea.performReturn();
                        }
                    }

                    if (!menuItemLink2TextField.getText().isEmpty()) {
                        selectedTextArea.writeText("item-link-2=" + menuItemLink2TextField.getText());
                        selectedTextArea.performReturn();
                        if (!menuItemLabel2TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-label-2=" + menuItemLabel2TextField.getText());
                            selectedTextArea.performReturn();
                        }
                        if (menuItemIcon2List.getSelectedItem() != null) {
                            selectedTextArea.writeText("item-icon-2=" + menuItemIcon2List.getSelectedItem());
                            selectedTextArea.performReturn();
                        }
                    }

                    if (!menuItemLink3TextField.getText().isEmpty()) {
                        selectedTextArea.writeText("item-link-3=" + menuItemLink3TextField.getText());
                        selectedTextArea.performReturn();
                        if (!menuItemLabel3TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-label-3=" + menuItemLabel3TextField.getText());
                            selectedTextArea.performReturn();
                        }
                        if (menuItemIcon3List.getSelectedItem() != null) {
                            selectedTextArea.writeText("item-icon-3=" + menuItemIcon3List.getSelectedItem());
                            selectedTextArea.performReturn();
                        }
                    }

                    if (!menuItemLink4TextField.getText().isEmpty()) {
                        selectedTextArea.writeText("item-link-4=" + menuItemLink4TextField.getText());
                        selectedTextArea.performReturn();
                        if (!menuItemLabel4TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-label-4=" + menuItemLabel4TextField.getText());
                            selectedTextArea.performReturn();
                        }
                        if (menuItemIcon4List.getSelectedItem() != null) {
                            selectedTextArea.writeText("item-icon-4=" + menuItemIcon4List.getSelectedItem());
                            selectedTextArea.performReturn();
                        }
                    }

                    if (!menuItemLink5TextField.getText().isEmpty()) {
                        selectedTextArea.writeText("item-link-5=" + menuItemLink5TextField.getText());
                        selectedTextArea.performReturn();
                        if (!menuItemLabel5TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-label-5=" + menuItemLabel5TextField.getText());
                            selectedTextArea.performReturn();
                        }
                        if (menuItemIcon5List.getSelectedItem() != null) {
                            selectedTextArea.writeText("item-icon-5=" + menuItemIcon5List.getSelectedItem());
                            selectedTextArea.performReturn();
                        }

                    }

                    if (!menuItemLink6TextField.getText().isEmpty()) {
                        selectedTextArea.writeText("item-link-6=" + menuItemLink6TextField.getText());
                        selectedTextArea.performReturn();
                        if (!menuItemLabel6TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-label-6=" + menuItemLabel6TextField.getText());
                            selectedTextArea.performReturn();
                        }
                        if (menuItemIcon6List.getSelectedItem() != null) {
                            selectedTextArea.writeText("item-icon-6=" + menuItemIcon6List.getSelectedItem());
                            selectedTextArea.performReturn();
                        }

                    }

                    if (!menuItemLink7TextField.getText().isEmpty()) {
                        selectedTextArea.writeText("item-link-7=" + menuItemLink7TextField.getText());
                        selectedTextArea.performReturn();
                        if (!menuItemLabel7TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-label-7=" + menuItemLabel7TextField.getText());
                            selectedTextArea.performReturn();
                        }
                        if (menuItemIcon7List.getSelectedItem() != null) {
                            selectedTextArea.writeText("item-icon-7=" + menuItemIcon7List.getSelectedItem());
                            selectedTextArea.performReturn();
                        }
                    }

                    if (!menuItemLink8TextField.getText().isEmpty()) {
                        selectedTextArea.writeText("item-link-8=" + menuItemLink8TextField.getText());
                        selectedTextArea.performReturn();
                        if (!menuItemLabel8TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-label-8=" + menuItemLabel8TextField.getText());
                            selectedTextArea.performReturn();
                        }
                        if (menuItemIcon8List.getSelectedItem() != null) {
                            selectedTextArea.writeText("item-icon-8=" + menuItemIcon8List.getSelectedItem());
                            selectedTextArea.performReturn();
                        }
                    }

                    if (!menuItemLink9TextField.getText().isEmpty()) {
                        selectedTextArea.writeText("item-link-9=" + menuItemLink9TextField.getText());
                        selectedTextArea.performReturn();
                        if (!menuItemLabel9TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-label-9=" + menuItemLabel9TextField.getText());
                            selectedTextArea.performReturn();
                        }
                        if (menuItemIcon9List.getSelectedItem() != null) {
                            selectedTextArea.writeText("item-icon-9=" + menuItemIcon9List.getSelectedItem());
                            selectedTextArea.performReturn();
                        }
                    }

                    if (!menuItemLink10TextField.getText().isEmpty()) {
                        selectedTextArea.writeText("item-link-10=" + menuItemLink10TextField.getText());
                        selectedTextArea.performReturn();
                        if (!menuItemLabel10TextField.getText().isEmpty()) {
                            selectedTextArea.writeText("item-label-10=" + menuItemLabel10TextField.getText());
                            selectedTextArea.performReturn();
                        }
                        if (menuItemIcon10List.getSelectedItem() != null) {
                            selectedTextArea.writeText("item-icon-10=" + menuItemIcon10List.getSelectedItem());
                            selectedTextArea.performReturn();
                        }
                    }

                    close();
                }
            });
            layout.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(negativeText);
            buttonNegative = new Button(75, 130, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            layout.addComponent(buttonNegative);
            break;
        }
        case BREWING: {
            ScrollableLayout scrollableLayout = new ScrollableLayout(0, 0, LAYOUT_WIDTH, 169, LAYOUT_HEIGHT - 25);
            scrollableLayout.setScrollSpeed(8);
            scrollableLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
                Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
                gui.drawRect(x, y, x + LAYOUT_WIDTH, y + 220, Color.gray.getRGB());
            });

            int inventoryX = LAYOUT_WIDTH / 2 - 128 / 2;
            int inventoryY = 80;
            ComponentInventory inventory = new ComponentInventory(0, 0, scrollableLayout.width, scrollableLayout.height, new Image(inventoryX, inventoryY, 0, 136, 128, 73, new ResourceLocation(Reference.MOD_ID, "textures/gui/container_boxes.png")), true, new InventorySlot(inventoryX + 13, inventoryY + 7, true), new InventorySlot(inventoryX + 74, inventoryY + 7), new InventorySlot(inventoryX + 51, inventoryY + 41), new InventorySlot(inventoryX + 74, inventoryY + 48), new InventorySlot(inventoryX + 97, inventoryY + 41));
            inventory.setStackInSlot(0, new ItemStack(Items.BLAZE_POWDER));
            inventory.setItemClickListener((slot, index, mouseButton) -> {
                if (index > 0) {
                    if (mouseButton == 0) {
                        ItemSelectionDialog dialog = new ItemSelectionDialog();
                        dialog.setItemClickListener((slot1, index1, mouseButton1) -> {
                            inventory.setStackInSlot(index, slot1.getStack());
                        });
                        this.openDialog(dialog);
                    }
                    if (mouseButton == 1 && !slot.getStack().isEmpty()) {
                        Dialog.Input dialog = new Dialog.Input(slot.getStack().getItem().getItemStackDisplayName(slot.getStack()));
                        dialog.setTitle("Set Count");
                        dialog.setInputText(Integer.toString(slot.getStack().getCount()));
                        dialog.setResponseHandler((success, input) -> {
                            if (success) {
                                ItemStack stack = slot.getStack();
                                if (StringUtils.isNumeric(input)) {
                                    try {
                                        int count = Integer.parseInt(input);
                                        if (count <= 0 || count > stack.getMaxStackSize()) {
                                            Dialog.Message error = new Dialog.Message("Invalid stack size \'" + input + "\' for item \'" + stack.getDisplayName() + "\'. " + (stack.getMaxStackSize() == 1 ? "Must be 1." : "Must be from 1 to " + stack.getMaxStackSize()));
                                            error.setTitle("Error");
                                            this.openDialog(error);
                                        } else {
                                            stack.setCount(count);
                                            return true;
                                        }
                                    } catch (NumberFormatException e) {
                                        Dialog.Message error = new Dialog.Message("Invalid integer \'" + input + "\'");
                                        error.setTitle("Error");
                                        this.openDialog(error);
                                    }
                                } else {
                                    Dialog.Message error = new Dialog.Message("Invalid number \'" + input + "\'");
                                    error.setTitle("Error");
                                    this.openDialog(error);
                                }
                            }
                            return false;
                        });
                        this.openDialog(dialog);
                    }
                }
            });
            scrollableLayout.addComponent(inventory);

            Label titleLabel = new Label("Title:", 5, 8);
            scrollableLayout.addComponent(titleLabel);
            TextField titleTextField = new TextField(5, 18, 162);
            scrollableLayout.addComponent(titleTextField);

            Label descLabel = new Label("Description:", 5, 38);
            scrollableLayout.addComponent(descLabel);
            TextField descTextField = new TextField(5, 48, 162);
            scrollableLayout.addComponent(descTextField);

            layout.addComponent(scrollableLayout);

            int positiveWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(positiveText);
            buttonPositive = new Button(125, 130, positiveText);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("#brewing");
                    selectedTextArea.performReturn();
                    if (!titleTextField.getText().isEmpty()) {
                        selectedTextArea.writeText("title=" + titleTextField.getText());
                        selectedTextArea.performReturn();
                    }
                    if (!descTextField.getText().isEmpty()) {
                        selectedTextArea.writeText("desc=" + descTextField.getText());
                        selectedTextArea.performReturn();
                    }
                    if (!inventory.getStackInSlot(0).isEmpty()) {
                        selectedTextArea.writeText("slot-fuel=" + inventory.getStackInSlot(0).serializeNBT());
                        selectedTextArea.performReturn();
                    }
                    if (!inventory.getStackInSlot(1).isEmpty()) {
                        selectedTextArea.writeText("slot-input=" + inventory.getStackInSlot(1).serializeNBT());
                        selectedTextArea.performReturn();
                    }
                    for (int i = 0; i < 3; i++) {
                        if (!inventory.getStackInSlot(2 + i).isEmpty()) {
                            selectedTextArea.writeText("slot-output-" + (i + 1) + "=" + inventory.getStackInSlot(2 + i).serializeNBT());
                            selectedTextArea.performReturn();
                        }
                    }

                    close();
                }
            });
            layout.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(negativeText);
            buttonNegative = new Button(75, 130, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            layout.addComponent(buttonNegative);
            break;
        }
        case DOWNLOAD: {
            ScrollableLayout scrollableLayout = new ScrollableLayout(0, 0, LAYOUT_WIDTH, 130, LAYOUT_HEIGHT - 25);
            scrollableLayout.setScrollSpeed(8);
            scrollableLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
                Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
                gui.drawRect(x, y, x + LAYOUT_WIDTH, y + 130, Color.gray.getRGB());
            });

            Label fileAppLabel = new Label(TextFormatting.RED + "*" + TextFormatting.RESET + "File App:", 5, 8);
            scrollableLayout.addComponent(fileAppLabel);
            TextField fileAppTextField = new TextField(5, 18, 162);
            scrollableLayout.addComponent(fileAppTextField);

            Label fileDataLabel = new Label(TextFormatting.RED + "*" + TextFormatting.RESET + "File Data:", 5, 38);
            scrollableLayout.addComponent(fileDataLabel);
            TextField fileDataTextField = new TextField(5, 48, 162);
            scrollableLayout.addComponent(fileDataTextField);

            Label fileNameLabel = new Label("File Name:", 5, 68);
            scrollableLayout.addComponent(fileNameLabel);
            TextField fileNameTextField = new TextField(5, 78, 162);
            scrollableLayout.addComponent(fileNameTextField);

            Label textLabel = new Label("Text:", 5, 98);
            scrollableLayout.addComponent(textLabel);
            TextField textTextField = new TextField(5, 108, 162);
            scrollableLayout.addComponent(textTextField);

            layout.addComponent(scrollableLayout);

            Dialog.OpenFile openFile = new Dialog.OpenFile(application);
            openFile.setResponseHandler((success, file) -> {

                if (success) {
                    fileAppTextField.writeText(file.getOpeningApp());
                    String data = file.getData().toString().replace("\n\n", "\\\\n");
                    fileDataTextField.writeText(data);
                    fileNameTextField.writeText(file.getName());
                    buttonPositive.setEnabled(true);

                    return true;
                }
                return false;

            });

            application.openDialog(openFile);

            int positiveWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(positiveText);
            buttonPositive = new Button(125, 130, positiveText) {
                @Override
                protected void handleKeyTyped(char character, int code) {
                    super.handleKeyTyped(character, code);

                    if (!fileAppTextField.getText().isEmpty() && !fileDataTextField.getText().isEmpty()) {
                        buttonPositive.setEnabled(true);
                    } else {
                        buttonPositive.setEnabled(false);
                    }

                }
            };
            buttonPositive.setEnabled(false);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) -> {

                if (mouseButton == 0) {

                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("#download");
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("file-app=" + fileAppTextField.getText());
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("file-data=" + fileDataTextField.getText());
                    selectedTextArea.performReturn();
                    if (!fileNameTextField.getText().isEmpty()) {
                        selectedTextArea.writeText("file-name=" + fileNameTextField.getText());
                        selectedTextArea.performReturn();
                    }
                    if (!textTextField.getText().isEmpty()) {
                        selectedTextArea.writeText("text=" + textTextField.getText());
                        selectedTextArea.performReturn();
                    }
                    close();

                }
            });
            layout.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(negativeText);
            buttonNegative = new Button(75, 130, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            layout.addComponent(buttonNegative);

            Label requiredLabel = new Label(TextFormatting.RED + "*" + TextFormatting.RESET + " Required", 7, 134);
            layout.addComponent(requiredLabel);
            break;
        }
        case FURNACE: {
            ScrollableLayout scrollableLayout = new ScrollableLayout(0, 0, LAYOUT_WIDTH, 164, LAYOUT_HEIGHT - 25);
            scrollableLayout.setScrollSpeed(8);
            scrollableLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
                Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
                gui.drawRect(x, y, x + LAYOUT_WIDTH, y + 220, Color.gray.getRGB());
            });

            int inventoryX = LAYOUT_WIDTH / 2 - 128 / 2;
            int inventoryY = 80;
            ComponentInventory inventory = new ComponentInventory(0, 0, scrollableLayout.width, scrollableLayout.height, new Image(inventoryX, inventoryY, 0, 68, 128, 68, new ResourceLocation(Reference.MOD_ID, "textures/gui/container_boxes.png")), true, new InventorySlot(inventoryX + 25, inventoryY + 7), new InventorySlot(inventoryX + 25, inventoryY + 43), new InventorySlot(inventoryX + 85, inventoryY + 25));
            inventory.setItemClickListener((slot, index, mouseButton) -> {
                if (mouseButton == 0) {
                    ItemSelectionDialog dialog = new ItemSelectionDialog();
                    dialog.setItemClickListener((slot1, index1, mouseButton1) -> {
                        inventory.setStackInSlot(index, slot1.getStack());
                    });
                    this.openDialog(dialog);
                }
                if (mouseButton == 1 && !slot.getStack().isEmpty()) {
                    Dialog.Input dialog = new Dialog.Input(slot.getStack().getItem().getItemStackDisplayName(slot.getStack()));
                    dialog.setTitle("Set Count");
                    dialog.setInputText(Integer.toString(slot.getStack().getCount()));
                    dialog.setResponseHandler((success, input) -> {
                        if (success) {
                            ItemStack stack = slot.getStack();
                            if (StringUtils.isNumeric(input)) {
                                try {
                                    int count = Integer.parseInt(input);
                                    if (count <= 0 || count > stack.getMaxStackSize()) {
                                        Dialog.Message error = new Dialog.Message("Invalid stack size \'" + input + "\' for item \'" + stack.getDisplayName() + "\'. " + (stack.getMaxStackSize() == 1 ? "Must be 1." : "Must be from 1 to " + stack.getMaxStackSize()));
                                        error.setTitle("Error");
                                        this.openDialog(error);
                                    } else {
                                        stack.setCount(count);
                                        return true;
                                    }
                                } catch (NumberFormatException e) {
                                    Dialog.Message error = new Dialog.Message("Invalid integer \'" + input + "\'");
                                    error.setTitle("Error");
                                    this.openDialog(error);
                                }
                            } else {
                                Dialog.Message error = new Dialog.Message("Invalid number \'" + input + "\'");
                                error.setTitle("Error");
                                this.openDialog(error);
                            }
                        }
                        return false;
                    });
                    this.openDialog(dialog);
                }
            });
            scrollableLayout.addComponent(inventory);

            Label titleLabel = new Label("Title:", 5, 8);
            scrollableLayout.addComponent(titleLabel);
            TextField titleTextField = new TextField(5, 18, 162);
            scrollableLayout.addComponent(titleTextField);

            Label descLabel = new Label("Description:", 5, 38);
            scrollableLayout.addComponent(descLabel);
            TextField descTextField = new TextField(5, 48, 162);
            scrollableLayout.addComponent(descTextField);

            layout.addComponent(scrollableLayout);

            int positiveWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(positiveText);
            buttonPositive = new Button(125, 130, positiveText);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("#furnace");
                    selectedTextArea.performReturn();
                    if (!titleTextField.getText().isEmpty()) {
                        selectedTextArea.writeText("title=" + titleTextField.getText());
                        selectedTextArea.performReturn();
                    }
                    if (!descTextField.getText().isEmpty()) {
                        selectedTextArea.writeText("desc=" + descTextField.getText());
                        selectedTextArea.performReturn();
                    }
                    if (!inventory.getStackInSlot(0).isEmpty()) {
                        selectedTextArea.writeText("slot-input=" + inventory.getStackInSlot(0).serializeNBT());
                        selectedTextArea.performReturn();
                    }
                    if (!inventory.getStackInSlot(1).isEmpty()) {
                        selectedTextArea.writeText("slot-fuel=" + inventory.getStackInSlot(1).serializeNBT());
                        selectedTextArea.performReturn();
                    }
                    if (!inventory.getStackInSlot(2).isEmpty()) {
                        selectedTextArea.writeText("slot-result=" + inventory.getStackInSlot(2).serializeNBT());
                        selectedTextArea.performReturn();
                    }

                    close();
                }
            });
            layout.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(negativeText);
            buttonNegative = new Button(75, 130, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            layout.addComponent(buttonNegative);
            break;
        }
        case FOOTER: {
            ScrollableLayout scrollableLayout = new ScrollableLayout(0, 0, LAYOUT_WIDTH, 160, LAYOUT_HEIGHT - 25);
            scrollableLayout.setScrollSpeed(8);
            scrollableLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
                Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
                gui.drawRect(x, y, x + LAYOUT_WIDTH, y + 160, Color.gray.getRGB());
            });

            Label titleLabel = new Label(TextFormatting.RED + "*" + TextFormatting.RESET + "Title:", 5, 8);
            scrollableLayout.addComponent(titleLabel);
            TextField titleTextField = new TextField(5, 18, 162);
            scrollableLayout.addComponent(titleTextField);

            Label subtitleLabel = new Label(TextFormatting.RED + "*" + TextFormatting.RESET + "Subtitle:", 5, 38);
            scrollableLayout.addComponent(subtitleLabel);
            TextField subtitleTextField = new TextField(5, 48, 162);
            scrollableLayout.addComponent(subtitleTextField);

            Label homePageLinkLabel = new Label(TextFormatting.RED + "*" + TextFormatting.RESET + "Home Page Link:", 5, 68);
            scrollableLayout.addComponent(homePageLinkLabel);
            TextField homePageLinkTextField = new TextField(5, 78, 162);
            scrollableLayout.addComponent(homePageLinkTextField);

            Label colorLabel = new Label("Color:", 5, 98);
            scrollableLayout.addComponent(colorLabel);

            Label redLabel = new Label("R ", 100, 107);
            scrollableLayout.addComponent(redLabel);
            redSlider = new Slider(110, 105, 50);
            redSlider.setSlideListener(new SlideListener() {
                @Override
                public void onSlide(float v) {
                    setColor = new Color(v, greenSlider.getPercentage(), blueSlider.getPercentage());
                }
            });
            scrollableLayout.addComponent(redSlider);

            Label greenLabel = new Label("G ", 100, 123);
            scrollableLayout.addComponent(greenLabel);
            greenSlider = new Slider(110, 121, 50);
            greenSlider.setSlideListener(new SlideListener() {
                @Override
                public void onSlide(float v) {
                    setColor = new Color(redSlider.getPercentage(), v, blueSlider.getPercentage());
                }
            });
            scrollableLayout.addComponent(greenSlider);

            Label blueLabel = new Label("B ", 100, 139);
            scrollableLayout.addComponent(blueLabel);
            blueSlider = new Slider(110, 137, 50);
            blueSlider.setSlideListener(new SlideListener() {
                @Override
                public void onSlide(float v) {
                    setColor = new Color(redSlider.getPercentage(), greenSlider.getPercentage(), v);
                }
            });
            scrollableLayout.addComponent(blueSlider);

            colorDisplay = new Component(45, 100) {
                @Override
                public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
                    drawRect(xPosition, yPosition, xPosition + 50, yPosition + 51, Color.DARK_GRAY.getRGB());
                    drawRect(xPosition + 1, yPosition + 1, xPosition + 49, yPosition + 50, setColor.getRGB());
                }
            };
            scrollableLayout.addComponent(colorDisplay);

            layout.addComponent(scrollableLayout);

            int positiveWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(positiveText);
            buttonPositive = new Button(125, 130, positiveText) {
                @Override
                protected void handleKeyTyped(char character, int code) {
                    super.handleKeyTyped(character, code);

                    if (!titleTextField.getText().isEmpty() && !subtitleTextField.getText().isEmpty() && !homePageLinkTextField.getText().isEmpty()) {
                        buttonPositive.setEnabled(true);
                    } else {
                        buttonPositive.setEnabled(false);
                    }

                }
            };
            buttonPositive.setEnabled(false);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) -> {

                if (mouseButton == 0) {

                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("#footer");
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("title=" + titleTextField.getText());
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("sub-title=" + subtitleTextField.getText());
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("home-page=" + homePageLinkTextField.getText());
                    selectedTextArea.performReturn();
                    if (setColor != Color.darkGray) {
                        selectedTextArea.writeText("color=" + setColor.getRGB());
                        selectedTextArea.performReturn();
                    }
                    close();
                }
            });
            layout.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(negativeText);
            buttonNegative = new Button(75, 130, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            layout.addComponent(buttonNegative);

            Label requiredLabel = new Label(TextFormatting.RED + "*" + TextFormatting.RESET + " Required", 7, 134);
            layout.addComponent(requiredLabel);
            break;
        }
        case DIVIDER: {
            ScrollableLayout scrollableLayout = new ScrollableLayout(0, 0, LAYOUT_WIDTH, 120, LAYOUT_HEIGHT - 25);
            scrollableLayout.setScrollSpeed(8);
            scrollableLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
                Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
                gui.drawRect(x, y, x + LAYOUT_WIDTH, y + 120, Color.gray.getRGB());
            });

            Label sizeLabel = new Label(TextFormatting.RED + "*" + TextFormatting.RESET + "Size:", 5, 8);
            scrollableLayout.addComponent(sizeLabel);
            TextField sizeTextField = new TextField(5, 18, 162) {
                @Override
                public void handleKeyTyped(char character, int code) {
                    if (Character.isDigit(character) || code == Keyboard.KEY_BACK) {
                        super.handleKeyTyped(character, code);
                    }
                }
            };
            scrollableLayout.addComponent(sizeTextField);

            Label colorLabel = new Label("Color:", 5, 38);
            scrollableLayout.addComponent(colorLabel);

            Label redLabel = new Label("R ", 100, 47);
            scrollableLayout.addComponent(redLabel);
            redSlider = new Slider(110, 45, 50);
            redSlider.setSlideListener(new SlideListener() {
                @Override
                public void onSlide(float v) {
                    setColor = new Color(v, greenSlider.getPercentage(), blueSlider.getPercentage());
                }
            });
            scrollableLayout.addComponent(redSlider);

            Label greenLabel = new Label("G ", 100, 63);
            scrollableLayout.addComponent(greenLabel);
            greenSlider = new Slider(110, 61, 50);
            greenSlider.setSlideListener(new SlideListener() {
                @Override
                public void onSlide(float v) {
                    setColor = new Color(redSlider.getPercentage(), v, blueSlider.getPercentage());
                }
            });
            scrollableLayout.addComponent(greenSlider);

            Label blueLabel = new Label("B ", 100, 79);
            scrollableLayout.addComponent(blueLabel);
            blueSlider = new Slider(110, 77, 50);
            blueSlider.setSlideListener(new SlideListener() {
                @Override
                public void onSlide(float v) {
                    setColor = new Color(redSlider.getPercentage(), greenSlider.getPercentage(), v);
                }
            });
            scrollableLayout.addComponent(blueSlider);

            colorDisplay = new Component(45, 40) {
                @Override
                public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
                    drawRect(xPosition, yPosition, xPosition + 50, yPosition + 51, Color.DARK_GRAY.getRGB());
                    drawRect(xPosition + 1, yPosition + 1, xPosition + 49, yPosition + 50, setColor.getRGB());
                }
            };
            scrollableLayout.addComponent(colorDisplay);

            layout.addComponent(scrollableLayout);

            int positiveWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(positiveText);
            buttonPositive = new Button(125, 130, positiveText) {
                @Override
                protected void handleKeyTyped(char character, int code) {
                    super.handleKeyTyped(character, code);

                    if (!sizeTextField.getText().isEmpty()) {
                        buttonPositive.setEnabled(true);
                    } else {
                        buttonPositive.setEnabled(false);
                    }

                }
            };
            buttonPositive.setEnabled(false);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) -> {

                if (mouseButton == 0) {

                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("#divider");
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("size=" + sizeTextField.getText());
                    selectedTextArea.performReturn();
                    if (setColor != Color.darkGray) {
                        selectedTextArea.writeText("color=" + setColor.getRGB());
                        selectedTextArea.performReturn();
                    }
                    close();

                }
            });
            layout.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(negativeText);
            buttonNegative = new Button(75, 130, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            layout.addComponent(buttonNegative);

            Label requiredLabel = new Label(TextFormatting.RED + "*" + TextFormatting.RESET + " Required", 7, 134);
            layout.addComponent(requiredLabel);
            break;
        }
        case CRAFTING: {
            ScrollableLayout scrollableLayout = new ScrollableLayout(0, 0, LAYOUT_WIDTH, 164, LAYOUT_HEIGHT - 25);
            scrollableLayout.setScrollSpeed(8);
            scrollableLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
                Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
                gui.drawRect(x, y, x + LAYOUT_WIDTH, y + 220, Color.gray.getRGB());
            });

            int inventoryX = LAYOUT_WIDTH / 2 - 128 / 2;
            int inventoryY = 80;
            ComponentInventory inventory = new ComponentInventory(0, 0, scrollableLayout.width, scrollableLayout.height, new Image(inventoryX, inventoryY, 0, 0, 128, 68, new ResourceLocation(Reference.MOD_ID, "textures/gui/container_boxes.png")), true, new InventorySlot(inventoryX + 7, inventoryY + 7), new InventorySlot(inventoryX + 25, inventoryY + 7), new InventorySlot(inventoryX + 43, inventoryY + 7), new InventorySlot(inventoryX + 7, inventoryY + 25), new InventorySlot(inventoryX + 25, inventoryY + 25), new InventorySlot(inventoryX + 43, inventoryY + 25), new InventorySlot(inventoryX + 7, inventoryY + 43), new InventorySlot(inventoryX + 25, inventoryY + 43), new InventorySlot(inventoryX + 43, inventoryY + 43), new InventorySlot(inventoryX + 98, inventoryY + 25));
            inventory.setItemClickListener((slot, index, mouseButton) -> {
                if (mouseButton == 0) {
                    ItemSelectionDialog dialog = new ItemSelectionDialog();
                    dialog.setItemClickListener((slot1, index1, mouseButton1) -> {
                        inventory.setStackInSlot(index, slot1.getStack());
                    });
                    this.openDialog(dialog);
                }
                if (mouseButton == 1 && !slot.getStack().isEmpty()) {
                    Dialog.Input dialog = new Dialog.Input(slot.getStack().getItem().getItemStackDisplayName(slot.getStack()));
                    dialog.setTitle("Set Count");
                    dialog.setInputText(Integer.toString(slot.getStack().getCount()));
                    dialog.setResponseHandler((success, input) -> {
                        if (success) {
                            ItemStack stack = slot.getStack();
                            if (StringUtils.isNumeric(input)) {
                                try {
                                    int count = Integer.parseInt(input);
                                    if (count <= 0 || count > stack.getMaxStackSize()) {
                                        Dialog.Message error = new Dialog.Message("Invalid stack size \'" + input + "\' for item \'" + stack.getDisplayName() + "\'. " + (stack.getMaxStackSize() == 1 ? "Must be 1." : "Must be from 1 to " + stack.getMaxStackSize()));
                                        error.setTitle("Error");
                                        this.openDialog(error);
                                    } else {
                                        stack.setCount(count);
                                        return true;
                                    }
                                } catch (NumberFormatException e) {
                                    Dialog.Message error = new Dialog.Message("Invalid integer \'" + input + "\'");
                                    error.setTitle("Error");
                                    this.openDialog(error);
                                }
                            } else {
                                Dialog.Message error = new Dialog.Message("Invalid number \'" + input + "\'");
                                error.setTitle("Error");
                                this.openDialog(error);
                            }
                        }
                        return false;
                    });
                    this.openDialog(dialog);
                }
            });
            scrollableLayout.addComponent(inventory);

            Label titleLabel = new Label("Title:", 5, 8);
            scrollableLayout.addComponent(titleLabel);
            TextField titleTextField = new TextField(5, 18, 162);
            scrollableLayout.addComponent(titleTextField);

            Label descLabel = new Label("Description:", 5, 38);
            scrollableLayout.addComponent(descLabel);
            TextField descTextField = new TextField(5, 48, 162);
            scrollableLayout.addComponent(descTextField);

            layout.addComponent(scrollableLayout);

            int positiveWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(positiveText);
            buttonPositive = new Button(125, 130, positiveText);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("#crafting");
                    selectedTextArea.performReturn();
                    if (!titleTextField.getText().isEmpty()) {
                        selectedTextArea.writeText("title=" + titleTextField.getText());
                        selectedTextArea.performReturn();
                    }
                    if (!descTextField.getText().isEmpty()) {
                        selectedTextArea.writeText("desc=" + descTextField.getText());
                        selectedTextArea.performReturn();
                    }
                    for (int i = 0; i < 9; i++) {
                        if (!inventory.getStackInSlot(i).isEmpty()) {
                            selectedTextArea.writeText("slot-" + (i + 1) + "=" + inventory.getStackInSlot(i).serializeNBT());
                            selectedTextArea.performReturn();
                        }
                    }
                    if (!inventory.getStackInSlot(9).isEmpty()) {
                        selectedTextArea.writeText("slot-result=" + inventory.getStackInSlot(9).serializeNBT());
                        selectedTextArea.performReturn();
                    }

                    close();
                }
            });
            layout.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(negativeText);
            buttonNegative = new Button(75, 130, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            layout.addComponent(buttonNegative);
            break;
        }
        case ANVIL: {
            ScrollableLayout scrollableLayout = new ScrollableLayout(0, 0, LAYOUT_WIDTH, 128, LAYOUT_HEIGHT - 25);
            scrollableLayout.setScrollSpeed(8);
            scrollableLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
                Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
                gui.drawRect(x, y, x + LAYOUT_WIDTH, y + 220, Color.gray.getRGB());
            });

            int inventoryX = LAYOUT_WIDTH / 2 - 128 / 2;
            int inventoryY = 80;
            ComponentInventory inventory = new ComponentInventory(0, 0, scrollableLayout.width, scrollableLayout.height, new Image(inventoryX, inventoryY, 0, 209, 128, 32, new ResourceLocation(Reference.MOD_ID, "textures/gui/container_boxes.png")), true, new InventorySlot(inventoryX + 11, inventoryY + 7), new InventorySlot(inventoryX + 50, inventoryY + 7), new InventorySlot(inventoryX + 98, inventoryY + 7));
            inventory.setItemClickListener((slot, index, mouseButton) -> {
                if (mouseButton == 0) {
                    ItemSelectionDialog dialog = new ItemSelectionDialog();
                    dialog.setItemClickListener((slot1, index1, mouseButton1) -> {
                        inventory.setStackInSlot(index, slot1.getStack());
                    });
                    this.openDialog(dialog);
                }
                if (mouseButton == 1 && !slot.getStack().isEmpty()) {
                    Dialog.Input dialog = new Dialog.Input(slot.getStack().getItem().getItemStackDisplayName(slot.getStack()));
                    dialog.setTitle("Set Count");
                    dialog.setInputText(Integer.toString(slot.getStack().getCount()));
                    dialog.setResponseHandler((success, input) -> {
                        if (success) {
                            ItemStack stack = slot.getStack();
                            if (StringUtils.isNumeric(input)) {
                                try {
                                    int count = Integer.parseInt(input);
                                    if (count <= 0 || count > stack.getMaxStackSize()) {
                                        Dialog.Message error = new Dialog.Message("Invalid stack size \'" + input + "\' for item \'" + stack.getDisplayName() + "\'. " + (stack.getMaxStackSize() == 1 ? "Must be 1." : "Must be from 1 to " + stack.getMaxStackSize()));
                                        error.setTitle("Error");
                                        this.openDialog(error);
                                    } else {
                                        stack.setCount(count);
                                        return true;
                                    }
                                } catch (NumberFormatException e) {
                                    Dialog.Message error = new Dialog.Message("Invalid integer \'" + input + "\'");
                                    error.setTitle("Error");
                                    this.openDialog(error);
                                }
                            } else {
                                Dialog.Message error = new Dialog.Message("Invalid number \'" + input + "\'");
                                error.setTitle("Error");
                                this.openDialog(error);
                            }
                        }
                        return false;
                    });
                    this.openDialog(dialog);
                }
            });
            scrollableLayout.addComponent(inventory);

            Label titleLabel = new Label("Title:", 5, 8);
            scrollableLayout.addComponent(titleLabel);
            TextField titleTextField = new TextField(5, 18, 162);
            scrollableLayout.addComponent(titleTextField);

            Label descLabel = new Label("Description:", 5, 38);
            scrollableLayout.addComponent(descLabel);
            TextField descTextField = new TextField(5, 48, 162);
            scrollableLayout.addComponent(descTextField);

            layout.addComponent(scrollableLayout);

            int positiveWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(positiveText);
            buttonPositive = new Button(125, 130, positiveText);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("#anvil");
                    selectedTextArea.performReturn();
                    if (!titleTextField.getText().isEmpty()) {
                        selectedTextArea.writeText("title=" + titleTextField.getText());
                        selectedTextArea.performReturn();
                    }
                    if (!descTextField.getText().isEmpty()) {
                        selectedTextArea.writeText("desc=" + descTextField.getText());
                        selectedTextArea.performReturn();
                    }
                    for (int i = 0; i < 2; i++) {
                        if (!inventory.getStackInSlot(i).isEmpty()) {
                            selectedTextArea.writeText("slot-" + (i + 1) + "=" + inventory.getStackInSlot(i).serializeNBT());
                            selectedTextArea.performReturn();
                        }
                    }
                    if (!inventory.getStackInSlot(2).isEmpty()) {
                        selectedTextArea.writeText("slot-result=" + inventory.getStackInSlot(2).serializeNBT());
                        selectedTextArea.performReturn();
                    }

                    close();
                }
            });
            layout.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(negativeText);
            buttonNegative = new Button(75, 130, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            layout.addComponent(buttonNegative);
            break;
        }
        case HEADER: {
            ScrollableLayout scrollableLayout = new ScrollableLayout(0, 0, LAYOUT_WIDTH, 150, LAYOUT_HEIGHT - 25);
            scrollableLayout.setScrollSpeed(8);
            scrollableLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
                Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
                gui.drawRect(x, y, x + LAYOUT_WIDTH, y + 150, Color.gray.getRGB());
            });

            Label textLabel = new Label(TextFormatting.RED + "*" + TextFormatting.RESET + "Text:", 5, 8);
            scrollableLayout.addComponent(textLabel);
            TextField textTextField = new TextField(5, 18, 162);
            scrollableLayout.addComponent(textTextField);

            Label scaleLabel = new Label("Scale:", 5, 38);
            scrollableLayout.addComponent(scaleLabel);
            TextField scaleTextField = new TextField(5, 48, 162) {
                @Override
                public void handleKeyTyped(char character, int code) {
                    if (Character.isDigit(character) || code == Keyboard.KEY_BACK) {
                        super.handleKeyTyped(character, code);
                    }
                }
            };
            scrollableLayout.addComponent(scaleTextField);

            Label paddingLabel = new Label("Padding:", 5, 68);
            scrollableLayout.addComponent(paddingLabel);
            TextField paddingTextField = new TextField(5, 78, 162) {
                @Override
                public void handleKeyTyped(char character, int code) {
                    if (Character.isDigit(character) || code == Keyboard.KEY_BACK) {
                        super.handleKeyTyped(character, code);
                    }
                }
            };
            scrollableLayout.addComponent(paddingTextField);

            Label alignLabel = new Label("Align:", 5, 98);
            scrollableLayout.addComponent(alignLabel);
            ItemList<String> alignList = new ItemList(5, 108, 50, 2);
            alignList.addItem("Left");
            alignList.addItem("Right");
            scrollableLayout.addComponent(alignList);

            layout.addComponent(scrollableLayout);

            int positiveWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(positiveText);
            buttonPositive = new Button(125, 130, positiveText) {
                @Override
                protected void handleKeyTyped(char character, int code) {
                    super.handleKeyTyped(character, code);

                    if (!textTextField.getText().isEmpty()) {
                        buttonPositive.setEnabled(true);
                    } else {
                        buttonPositive.setEnabled(false);
                    }

                }
            };
            buttonPositive.setEnabled(false);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("#header");
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("text=" + textTextField.getText());
                    selectedTextArea.performReturn();
                    if (!scaleTextField.getText().isEmpty()) {
                        selectedTextArea.writeText("scale=" + scaleTextField.getText());
                        selectedTextArea.performReturn();
                    }
                    if (!paddingTextField.getText().isEmpty()) {
                        selectedTextArea.writeText("padding=" + paddingTextField.getText());
                        selectedTextArea.performReturn();
                    }
                    if (alignList.getSelectedItem() != null) {
                        selectedTextArea.writeText("align=" + alignList.getSelectedItem().toLowerCase());
                        selectedTextArea.performReturn();
                    }

                    close();

                }
            });
            layout.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(negativeText);
            buttonNegative = new Button(75, 130, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            layout.addComponent(buttonNegative);

            Label requiredLabel = new Label(TextFormatting.RED + "*" + TextFormatting.RESET + " Required", 7, 134);
            layout.addComponent(requiredLabel);
            break;
        }
        case BANNER: {
            ScrollableLayout scrollableLayout = new ScrollableLayout(0, 0, LAYOUT_WIDTH, 125, LAYOUT_HEIGHT - 25);
            scrollableLayout.setScrollSpeed(8);
            scrollableLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
                Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
                gui.drawRect(x, y, x + LAYOUT_WIDTH, y + LAYOUT_HEIGHT - 25, Color.gray.getRGB());
            });

            Label imageLabel = new Label(TextFormatting.RED + "*" + TextFormatting.RESET + "Image Link (Requires Valid URL):", 5, 8);
            scrollableLayout.addComponent(imageLabel);
            TextField imageTextField = new TextField(5, 18, 162);
            scrollableLayout.addComponent(imageTextField);

            Label textLabel = new Label("Text:", 5, 38);
            scrollableLayout.addComponent(textLabel);
            TextField textTextField = new TextField(5, 48, 162);
            scrollableLayout.addComponent(textTextField);

            layout.addComponent(scrollableLayout);

            int positiveWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(positiveText);
            buttonPositive = new Button(125, 130, positiveText) {
                @Override
                protected void handleKeyTyped(char character, int code) {
                    super.handleKeyTyped(character, code);

                    if (!imageTextField.getText().isEmpty()) {
                        if (IsMatch(imageTextField.getText())) {
                            buttonPositive.setEnabled(true);
                        } else {
                            buttonPositive.setEnabled(false);
                        }
                    } else {
                        buttonPositive.setEnabled(false);
                    }

                }
            };
            buttonPositive.setEnabled(false);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("#banner");
                    selectedTextArea.performReturn();
                    selectedTextArea.writeText("image=" + imageTextField.getText());
                    selectedTextArea.performReturn();
                    if (!textTextField.getText().isEmpty()) {
                        selectedTextArea.writeText("text=" + textTextField.getText());
                    }

                    close();

                }
            });
            layout.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(negativeText);
            buttonNegative = new Button(75, 130, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            layout.addComponent(buttonNegative);

            Label requiredLabel = new Label(TextFormatting.RED + "*" + TextFormatting.RESET + " Required", 7, 134);
            layout.addComponent(requiredLabel);
            break;
        }
        default: {
            this.close();
            break;
        }
        }

        this.setLayout(layout);
    }

    private void getIcons(ItemList itemList) {
        ApplicationIcons.IconSet iconSet = new ApplicationIcons.IconSet("Standard Icons", Icons.values());
        for (int i = 0; i < 126 && i < iconSet.getIcons().length; ++i) {
            Enum<? extends IIcon> anEnum = iconSet.getIcons()[i];
            IIcon icon = (IIcon) anEnum;
            itemList.addItem(icon);
        }
        ApplicationIcons.IconSet alphabetSet = new ApplicationIcons.IconSet("Alphabet", Alphabet.values());
        for (int i = 0; i < 126 && i < alphabetSet.getIcons().length; ++i) {
            Enum<? extends IIcon> anEnum = alphabetSet.getIcons()[i];
            IIcon icon = (IIcon) anEnum;
            itemList.addItem(icon);
        }
    }

    public static class IconSet {
        private String name;
        private Enum<? extends IIcon>[] icons;

        public IconSet(String name, Enum<? extends IIcon>[] icons) {
            this.name = name;
            this.icons = icons;
        }

        public String getName() {
            return this.name;
        }

        public Enum<? extends IIcon>[] getIcons() {
            return this.icons;
        }

        public String toString() {
            return this.name;
        }
    }

    public boolean isGitwebSite(String website) {

        Matcher matcher = GitWebFrame.PATTERN_LINK.matcher(website);

        if (!matcher.matches()) {
            return false;
        } else {
            return true;
        }

    }

    private boolean IsMatch(String s) {
        try {
            Pattern patt = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }

    }
}
