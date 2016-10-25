package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * Emoji Windows
 */
public class EmojiWindows extends JWindow {

    private static final long serialVersionUID = 1L;

    private GridLayout gridLayout = new GridLayout(7, 15);

    private JLabel[] ico = new JLabel[105];

    private String emojiPath = "/resources/emoji/";

    private JTextPane messEditPane;

    private JButton emojiButton;

    private int i;

    /**
     * Init base windows
     *
     * @param messEditPane
     * @param emojiButton
     */
    public EmojiWindows(JTextPane messEditPane, JButton emojiButton) {
        super();
        this.messEditPane = messEditPane;
        this.emojiButton = emojiButton;
        this.setAlwaysOnTop(true);
        init();
    }

    /**
     * Init this windows
     */
    private void init() {
        this.setPreferredSize(new Dimension(28 * 15, 28 * 7));
        JPanel p = new JPanel();
        p.setOpaque(true);
        this.setContentPane(p);
        p.setLayout(gridLayout);
        p.setBackground(SystemColor.text);
        String fileName = "";
        for (i = 1; i < ico.length; i++) {      //对于每一个图标操作
            fileName = emojiPath + i + ".gif";
            ico[i] = new JLabel(new ChatPic(EmojiWindows.class.getResource(fileName), i), SwingConstants.CENTER);
            ico[i].setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225), 1));
            ico[i].setToolTipText(i + "");
            ico[i].addMouseListener(new MouseAdapter() {    //添加图片监听事件
                /**
                 * {@inheritDoc}
                 *
                 * @param e
                 */
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == 1) {
                        JLabel cub1 = (JLabel) (e.getSource());
                        ChatPic cupic = (ChatPic) (cub1.getIcon());
                        messEditPane.insertIcon(cupic);
                        cub1.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225), 1));
                        getObj().dispose();
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    ((JLabel) e.getSource()).setBorder(BorderFactory.createLineBorder(Color.BLUE));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    ((JLabel) e.getSource()).setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225), 1));
                }
            });

            p.add(ico[i]);
            p.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    getObj().dispose();
                }
            });

        }
    }

    /**
     * Return this obeject
     *
     * @return
     */
    private JWindow getObj() {
        return this;
    }

    /**
     * Set visible proper
     *
     * @param show
     */
    @Override
    public void setVisible(boolean show) {
        if (show) {
            determineAndSetLocation();
        }
        super.setVisible(show);
    }

    /**
     *
     */
    private void determineAndSetLocation() {
        Point loc = emojiButton.getLocationOnScreen();          //获取控件相对于屏幕的位置
        setBounds(loc.x - getPreferredSize().width / 3, loc.y - getPreferredSize().height,
                getPreferredSize().width, getPreferredSize().height);
    }

}

/**
 * Using to save image icon
 */
class ChatPic extends ImageIcon {

    private static final long serialVersionUID = 1L;

    int im;

    /**
     * Init each paine for pic
     *
     * @param url
     * @param im
     */
    public ChatPic(URL url, int im) {
        super(url);
        this.im = im;
    }
}