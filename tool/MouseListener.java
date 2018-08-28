//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window.Type;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;
import org.jnativehook.mouse.NativeMouseMotionListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

public class MouseListener implements NativeMouseListener, NativeMouseMotionListener, NativeMouseWheelListener {
    App app;

    MouseListener(App app) {
        this.app = app;
    }

    public void nativeMouseClicked(NativeMouseEvent nme) {
        if (!this.app.isSuspended()) {
            if (nme.getButton() == 2) {
                this.app.getResult().setVisible(false);
                this.app.setStartPoint((Point)null);
                this.app.setEndPoint((Point)null);
            }

        }
    }

    public void nativeMousePressed(NativeMouseEvent nme) {
        switch(nme.getButton()) {
            case 1:
                if (nme.getClickCount() == 1) {
                    if (this.app.getStartPoint() == null) {
                        this.app.setStartPoint(this.app.getTempPoint());
                    } else {
                        this.app.setEndPoint(this.app.getTempPoint());
                        this.app.search();
                    }
                } else if (nme.getClickCount() == 2) {
                    try {
                        this.app.searchClone();
                        BufferedImage screenFullImage = (new Robot()).createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                        (new File("Screenshots")).mkdirs();
                        ImageIO.write(screenFullImage, "png", new File("Screenshots/" + System.currentTimeMillis() + ".png"));
                    } catch (IOException | AWTException var4) {
                        ;
                    }
                }
                break;
            case 2:
                this.app.setStartPoint((Point)null);
                this.app.setEndPoint((Point)null);
                if (this.app.isCorrectPass() && nme.getClickCount() == 10) {
                    this.app.setSuspended(false);
                    this.app.getResult().setVisible(false);
                } else if (!this.app.isSuspended()) {
                    if (nme.getClickCount() == 3) {
                        try {
                            this.app.getResult().setExtendedState(6);
                            this.app.getResult().setLocation(new Point((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - 250, (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 60));
                            this.app.getResult().setMaximizedBounds(new Rectangle((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - 250, (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 60, 500, 60));
                            this.app.getResult().setMaximumSize(new Dimension(500, 60));
                            this.app.getResult().setMinimumSize(new Dimension(500, 60));
                            this.app.getResult().setUndecorated(true);
                            this.app.getResult().setOpacity(0.4F);
                            this.app.getResult().setPreferredSize(new Dimension(500, 60));
                            this.app.getResult().setSize(new Dimension(500, 60));
                            this.app.getResult().setState(0);
                            this.app.getResult().setType(Type.UTILITY);
                            this.app.getResult().pack();
                            this.app.getResult().setVisible(true);
                        } catch (Exception var3) {
                            ;
                        }
                    }

                    this.app.getResult().setVisible(true);
                    this.app.getResult().toFront();
                }
                break;
            default:
                this.app.setPass("");
                this.app.getResult().setVisible(false);
                this.app.setSuspended(true);
                if (nme.getClickCount() == 5) {
                    System.exit(0);
                }
        }

    }

    public void nativeMouseReleased(NativeMouseEvent nme) {
        this.app.getResult().setVisible(false);
        if (!this.app.isSuspended()) {
            if (nme.getButton() == 1) {
                this.app.search();
            }

        }
    }

    public void nativeMouseMoved(NativeMouseEvent nme) {
        if (!this.app.isSuspended()) {
            this.app.getResult().setVisible(false);
            this.app.setTempPoint(nme.getPoint());
        }
    }

    public void nativeMouseDragged(NativeMouseEvent nme) {
        if (!this.app.isSuspended()) {
            if (nme.getModifiers() <= 256) {
                this.app.setEndPoint(new Point(nme.getX(), nme.getY()));
            } else if (nme.getModifiers() <= 512) {
                this.app.getResult().setVisible(true);
                this.app.getResult().toFront();
            }

        }
    }

    public void nativeMouseWheelMoved(NativeMouseWheelEvent nmwe) {
        if (!this.app.isSuspended() && this.app.getResultIdx() != -1 && this.app.getSearchResults() != null && !this.app.getSearchResults().isEmpty()) {
            String searchResult = "";
            String[] substring;
            if (nmwe.getWheelRotation() > 0) {
                if (this.app.getResultIdx() < this.app.getSearchResults().size() - 1) {
                    searchResult = (String)this.app.getSearchResults().get(this.app.getResultIdx());
                    this.app.setResultIdx(this.app.getResultIdx() + 1);
                    substring = searchResult.split("\\|", 2);
                    this.app.getResult().getLblQ().setText(substring[0]);
                    this.app.getResult().getLblA().setText(substring[1]);
                }
            } else if (nmwe.getWheelRotation() < 0 && this.app.getResultIdx() > 0) {
                searchResult = (String)this.app.getSearchResults().get(this.app.getResultIdx());
                this.app.setResultIdx(this.app.getResultIdx() - 1);
                substring = searchResult.split("\\|", 2);
                this.app.getResult().getLblQ().setText(substring[0]);
                this.app.getResult().getLblA().setText(substring[1]);
            }

        }
    }
}
