// Decompiled by DJ v3.12.12.101 Copyright 2016 Atanas Neshkov  Date: 4/7/2018 9:31:23 AM
// Home Page:  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   App.java

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JLabel;

import net.sourceforge.tess4j.Tesseract;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseListener;
import org.jnativehook.mouse.NativeMouseMotionListener;
import org.jnativehook.mouse.NativeMouseWheelListener;

public class App {
    private String mode = "eng";
    private Result result;
    private boolean suspended = true;
    private List<String> searchResults;
    private int resultIdx;
    private Point startPoint;
    private Point endPoint;
    private Point tempPoint;
    private static final String MAC_PASSWORD = "8e32c2e3608d490c2e738123d68f51dc";
    private String pass = "";
    private FileProcess fileProc = null;
    private String[] listQuest;

    public App() {
        if (!this.isAccess()) {
            System.err.println("You don't have permission to access");
            System.exit(0);
        }
        EventQueue.invokeLater(() -> {
                    this.result = new Result();
                }
        );
        this.suspended = true;
        try {
            this.fileProc = new FileProcess();
        } catch (IOException var1_1) {
        }
    }

    public Result getResult() {
        return this.result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public boolean isSuspended() {
        return this.suspended;
    }

    public void setSuspended(boolean suspended) {
        if (suspended && this.result != null) {
            this.result.getLblQuery().setText("");
            this.result.getLblQ().setText("");
            this.result.getLblA().setText("");
        }
        this.suspended = suspended;
    }

    public List<String> getSearchResults() {
        return this.searchResults;
    }

    public void setSearchResults(List<String> searchResults) {
        this.searchResults = searchResults;
    }

    public int getResultIdx() {
        return this.resultIdx;
    }

    public void setResultIdx(int resultIdx) {
        this.resultIdx = resultIdx;
    }

    public Point getStartPoint() {
        return this.startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public Point getEndPoint() {
        return this.endPoint;
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }

    public String getPass() {
        return this.pass;
    }

    public void setPass(String pass) {
        this.pass = pass.toLowerCase();
    }

    public Point getTempPoint() {
        return this.tempPoint;
    }

    public void setTempPoint(Point tempPoint) {
        this.tempPoint = tempPoint;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    private String md5(String md5) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString(array[i] & 255 | 256).substring(1, 3));
            }
            return sb.toString().toLowerCase();
        } catch (NoSuchAlgorithmException md) {
            return null;
        }
    }

    private boolean isAccess() {
        return true;
    }

    public boolean isCorrectPass() {
        return "".equals(this.pass);
    }

    private String crackImage(String filePath) {
        File imageFile = new File(filePath);
        Tesseract instance = new Tesseract();
        instance.setLanguage(this.mode.toLowerCase());
        try {
            String resultString = trimAdvanced(instance.doOCR(imageFile));
            return trimAdvanced(resultString);
        } catch (Exception e) {
            return "N/A";
        }
    }

    public void search() {
        int y;
        int distanceY;
        int x;
        int distanceX;
        try {
            distanceX = Math.abs((int) (this.getEndPoint().getX() - this.getStartPoint().getX()));
            distanceY = Math.abs((int) (this.getEndPoint().getY() - this.getStartPoint().getY()));
            x = (int) this.getStartPoint().getX();
            y = (int) this.getStartPoint().getY();
            if ((double) x > this.getEndPoint().getX()) {
                x = (int) this.getEndPoint().getX();
            }
            if ((double) y > this.getEndPoint().getY()) {
                y = (int) this.getEndPoint().getY();
            }
        } catch (Exception e) {
            distanceX = 0;
            distanceY = 0;
            x = 0;
            y = 0;
        }
        if (distanceX != 0 && distanceY != 0) {
            try {
                BufferedImage screenFullImage = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                screenFullImage = screenFullImage.getSubimage(x, y, distanceX, distanceY);
                new File("temp").mkdirs();
                BufferedImage after = new BufferedImage(distanceX * 3, distanceY * 3, 1);
                AffineTransform at = new AffineTransform();
                at.scale(3.0, 3.0);
                AffineTransformOp scaleOp = new AffineTransformOp(at, 2);
                after = scaleOp.filter(screenFullImage, after);
                String filename = "temp/" + System.currentTimeMillis() + ".png";
                ImageIO.write((RenderedImage) after, "png", new File(filename));
                String quest = this.crackImage(filename);
                quest = VNCharacterUtils.removeAccent((String) quest);
                this.searchByText(quest);
                new File(filename).delete();
            } catch (AWTException | IOException screenFullImage) {
            } finally {
                this.startPoint = null;
                this.endPoint = null;
            }
        }
    }

    public void searchClone() {
        int y = 228;
        int distanceY = 880 - y;
        int x = 103;
        int distanceX = 1400 - x;

        if (distanceX != 0 && distanceY != 0) {
            try {
                BufferedImage screenFullImage = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                screenFullImage = screenFullImage.getSubimage(x, y, distanceX, distanceY);
                new File("temp").mkdirs();
                BufferedImage after = new BufferedImage(distanceX * 3, distanceY * 3, 1);
                AffineTransform at = new AffineTransform();
                at.scale(3.0, 3.0);
                AffineTransformOp scaleOp = new AffineTransformOp(at, 2);
                after = scaleOp.filter(screenFullImage, after);
                String filename = "temp/" + System.currentTimeMillis() + ".png";
                ImageIO.write((RenderedImage) after, "png", new File(filename));
                String allQuest = this.crackImage(filename);
                allQuest = VNCharacterUtils.removeAccent((String) allQuest);
                allQuest = allQuest.replaceAll("\n\n", "\n").replaceAll("\\?", "").replaceAll("  ", " ");
                String[] listQuest = allQuest.split("\n");
                String quest;
                try {
                    quest = listQuest[1];
                    String ans = this.searchByTextClone(quest);
                    System.out.println(ans);
                    if (!ans.equalsIgnoreCase("N/A")) {
                        int saveLongest = 0;
                        int index = -1;
                        if (ans.length() == 1) {
                            if (ans.toLowerCase().charAt(0) == 't') ans = "true";
                            else if (ans.toLowerCase().charAt(0) == 'f') ans = "false";
                        }
                        String[] ansWords = ans.split(" ");
                        for (int i = 2; i < listQuest.length; i++) {
                            String[] questWords = listQuest[i].substring(3).split(" ");
                            int longest = longestSameSubstring(ansWords, questWords);
                            if (longest > saveLongest) {
                                saveLongest = longest;
                                index = i;
                            }
                        }
                        if (saveLongest != 0) {
                            showAnswer(listQuest[index].substring(0, 2));
                        }
                    }
                } catch (Exception e) {
                }
            } catch (AWTException | IOException screenFullImage) {
            } finally {
                this.startPoint = null;
                this.endPoint = null;
            }
        }
    }

    public void showAnswer(String ans) {
        System.out.println(ans);
        try {
            Robot robot = new Robot();
            switch (ans.toLowerCase()) {
                case "a.":
                    robot.mouseMove(30, 270);
                    break;
                case "b.":
                    robot.mouseMove(30, 305);
                    break;
                case "c.":
                    robot.mouseMove(30, 335);
                    break;
                case "d.":
                    robot.mouseMove(30, 365);
                    break;
                case "e.":
                    robot.mouseMove(30, 400);
                    break;
            }
        } catch (AWTException e) {
        }
    }

    public void searchByText(String quest) {
        System.out.println("Question: " + quest);
        if (quest == null || quest.isEmpty()) {
            this.getResult().getLblQuery().setText("");
            this.getResult().getLblQ().setText("");
            this.getResult().getLblA().setText("");
            return;
        }
        while (quest.contains("  ")) {
            quest = trimAdvanced(quest.replaceAll("  ", " "));
        }
        quest = quest.toLowerCase();
        this.getResult().getLblQuery().setText(quest);
        this.setSearchResults(this.fileProc.search(this.getResult().getLblQuery().getText()));
        this.setResultIdx(0);
        if (!this.getSearchResults().isEmpty()) {
            String searchResult = this.getSearchResults().get(this.getResultIdx());
            String[] substring = searchResult.split("\\|", 2);
            this.getResult().getLblQ().setText(substring[0]);
            this.getResult().getLblA().setText(substring[1]);
        } else {
            this.setResultIdx(-1);
            this.getResult().getLblQ().setText("N/A");
            this.getResult().getLblA().setText("N/A");
        }
    }

    public String searchByTextClone(String quest) {
        System.out.println("Question: " + quest);
        if (quest == null || quest.isEmpty()) {
            this.getResult().getLblQuery().setText("");
            this.getResult().getLblQ().setText("");
            this.getResult().getLblA().setText("");
            return "N/A";
        }
        quest = quest.toLowerCase();
        int indexUnder = -1;
        String[] words = quest.split(" ");
        quest = "";
        for (int i = 0; i < words.length; i++) {
            if (words[i].contains("_")) {
                indexUnder = i;
                break;
            }
        }
        if (indexUnder != -1) {
            if (indexUnder < 2) {
                int index = indexUnder + 1;
                int des = Math.min(index + 3, words.length);
                for (int i = index; i < des; i++) {
                    quest += words[i];
                    if (i != des - 1) quest += " ";
                }
            } else {
                int index = indexUnder - 1;
                int des = Math.max(index - 3, 0);
                for (int i = des; i < index; i++) {
                    quest += words[i];
                    if (i != des - 1) quest += " ";
                }
            }
        } else {
            int mid = words.length / 2;
            int des = Math.min(mid + 3, words.length);
            for (int i = mid; i < des; i++) {
                quest += words[i];
                if (i != des - 1) quest += " ";
            }
        }
        quest = quest.replaceAll("\\_", "");
        List<String> searchResult = this.fileProc.search(quest);
        int countSave = 0;
        String stringSave = "";
        String quesSave="";
        for (String s : searchResult) {
            String[] substring = s.split("\\|", 2);
            if (substring.length == 2) {
                substring[0] = trimAdvanced(substring[0].replaceAll("  ", " "));
                String[] questWords = substring[0].split(" ");
                int longest = longestSameSubstring(questWords, words);
                if (longest > countSave) {
                    countSave = longest;
                    stringSave = substring[1];
                    quesSave = substring[0];
                }
            }
        }
        this.getResult().getLblQuery().setText(quest);
        if (!stringSave.isEmpty()) {
            this.getResult().getLblQ().setText(quesSave);
            this.getResult().getLblA().setText(stringSave);
            return trimAdvanced(stringSave.replaceAll("  ", " "));
        }else{
            this.getResult().getLblQ().setText("N/A");
            this.getResult().getLblA().setText("N/A");
            return "N/A";
        }
    }

    public int longestSameSubstring(String[] a, String[] b) {
        int longest = 0;
        int[][] table = new int[a.length][b.length];
        for (int i = 0; i < a.length; i++) {
            if (!a[i].contains("_")) {
                for (int j = 0; j < b.length; j++) {
                    if (!b[j].contains("_")) {
                        if (!a[i].equalsIgnoreCase(b[j])) {
                            continue;
                        }
                        table[i][j] = (i == 0 || j == 0) ? 1 : 1 + table[i - 1][j - 1];
                        if (table[i][j] > longest) {
                            longest = table[i][j];
                        }
                    }
                }
            }
        }
        return longest;
    }

    public static String trimAdvanced(String value) {

        if (value.isEmpty()) return "";

        int strLength = value.length();
        int len = value.length();
        int st = 0;
        char[] val = value.toCharArray();

        if (strLength == 0) {
            return "";
        }

        while ((st < len) && (val[st] <= ' ') || (val[st] == '\u00A0')) {
            st++;
            if (st == strLength) {
                break;
            }
        }
        while ((st < len) && (val[len - 1] <= ' ') || (val[len - 1] == '\u00A0')) {
            len--;
            if (len == 0) {
                break;
            }
        }
        return (st > len) ? "" : ((st > 0) || (len < strLength)) ? value.substring(st, len) : value;
    }

    public static void main(String[] args) throws IOException {
        App app = new App();
        if (args.length > 0) {
            app.setMode(args[0]);
        }
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.exit(1);
        }
        MouseListener mouseListener = new MouseListener(app);
        KeyListener keyListener = new KeyListener(app);
        GlobalScreen.addNativeMouseListener((NativeMouseListener) mouseListener);
        GlobalScreen.addNativeMouseMotionListener((NativeMouseMotionListener) mouseListener);
        GlobalScreen.addNativeMouseWheelListener((NativeMouseWheelListener) mouseListener);
        GlobalScreen.addNativeKeyListener((NativeKeyListener) keyListener);
    }
}
