//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.awt.Color;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class KeyListener implements NativeKeyListener {
    private App app;

    KeyListener(App app) {
        this.app = app;
    }

    public void nativeKeyPressed(NativeKeyEvent nke) {
        int keyCode = nke.getKeyCode();
        System.out.println(keyCode);
        switch(keyCode) {
            case 29:
                this.app.setStartPoint(this.app.getTempPoint());
                this.app.searchByText((String)null);
                break;
            case 42:
                if (this.app.getTempPoint().getX() > 0.0D || this.app.getTempPoint().getY() > 0.0D) {
                    System.out.println("x,y");
                    System.out.println(this.app.getTempPoint());
                    return;
                }

                if (this.app.getResult().getBackground().equals(new Color(240, 240, 240))) {
                    this.app.getResult().setBackground(new Color(255, 0, 0));
                } else {
                    this.app.getResult().setBackground(new Color(240, 240, 240));
                }

                this.app.getResult().pack();
                break;
            case NativeKeyEvent.VC_ESCAPE:
                System.exit(0);
            case 56:
                this.app.setEndPoint(this.app.getTempPoint());
                this.app.search();
        }

    }

    public void nativeKeyReleased(NativeKeyEvent nke) {
    }

    public void nativeKeyTyped(NativeKeyEvent nke) {
        this.app.setPass(this.app.getPass() + nke.getKeyChar());
        String text = this.app.getResult().getLblQuery().getText();
        if (nke.getRawCode() == 8) {
            if (!text.isEmpty()) {
                this.app.searchByText(text.substring(0, text.length() - 1));
            }
        } else {
            this.app.searchByText(text + nke.getKeyChar());
        }

    }
}
